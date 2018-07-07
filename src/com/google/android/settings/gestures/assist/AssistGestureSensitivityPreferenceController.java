package com.google.android.settings.gestures.assist;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.R;

import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.gestures.AssistGestureFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.SeekBarPreference;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.google.android.settings.gestures.assist.AssistGestureHelper.GestureListener;
import com.google.android.settings.gestures.assist.bubble.AssistGestureBubbleActivity;

public class AssistGestureSensitivityPreferenceController extends AbstractPreferenceController
    implements PreferenceControllerMixin,
        OnPreferenceChangeListener,
        LifecycleObserver,
        OnPause,
        OnResume {
  private final AssistGestureFeatureProvider mFeatureProvider;
  private final GestureListener mGestureListener = new C10281();
  private final SettingObserver mSettingObserver;
  private final UserManager mUserManager;
  private AssistGestureHelper mAssistGestureHelper;
  private EnforcedAdmin mFunDisallowedAdmin;
  private boolean mFunDisallowedBySystem;
  private long[] mHits = new long[3];
  private SeekBarPreference mPreference;
  private final Handler mHandler =
      new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
          switch (message.what) {
            case 1:
              AssistGestureSensitivityPreferenceController.this.mPreference.setShouldBlink(true);
              return;
            default:
              return;
          }
        }
      };
  private boolean mWasListening;

  public AssistGestureSensitivityPreferenceController(Context context, Lifecycle lifecycle) {
    super(context);
    this.mFeatureProvider = FeatureFactory.getFactory(context).getAssistGestureFeatureProvider();
    this.mSettingObserver = new SettingObserver();
    this.mAssistGestureHelper = new AssistGestureHelper(context);
    this.mUserManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
    if (lifecycle != null) {
      lifecycle.addObserver(this);
    }
  }

  public static int convertSensitivityFloatToInt(Context context, float f) {
    return Math.round(((float) getMaxSensitivityResourceInteger(context)) * f);
  }

  public static float convertSensitivityIntToFloat(Context context, int i) {
    return 1.0f - (((float) i) / ((float) getMaxSensitivityResourceInteger(context)));
  }

  public static int getMaxSensitivityResourceInteger(Context context) {
    int maximum = 8;
//    maximum = context.getResources().getInteger(R.integer.gesture_assist_sensitivity_max);
    return maximum;
  }

  public static float getSensitivity(Context context) {
    float f = Secure.getFloat(context.getContentResolver(), "assist_gesture_sensitivity", 0.5f);
    if (f < 0.0f || f > 1.0f) {
      f = 0.5f;
    }
    return 1.0f - f;
  }

  public static int getSensitivityInt(Context context) {
    return convertSensitivityFloatToInt(context, getSensitivity(context));
  }

  public static boolean isAvailable(
      Context context, AssistGestureFeatureProvider assistGestureFeatureProvider) {
    return assistGestureFeatureProvider.isSensorAvailable(context);
  }

  private void updateGestureListenerState(boolean mWasListening) {
    if (mWasListening == this.mWasListening) {
      return;
    }
    if (mWasListening) {
      this.mAssistGestureHelper.setListener(this.mGestureListener);
    } else {
      this.mAssistGestureHelper.setListener(null);
    }
    this.mWasListening = mWasListening;
  }

  private void updatePreference() {
    if (this.mPreference != null) {
      this.mPreference.setProgress(getSensitivityInt(this.mContext));
      boolean z =
          Secure.getInt(this.mContext.getContentResolver(), "assist_gesture_enabled", 1) != 0;
      boolean z2 =
          Secure.getInt(
                  this.mContext.getContentResolver(), "assist_gesture_silence_alerts_enabled", 1)
              != 0;
      if (this.mFeatureProvider.isSupported(this.mContext) && (z || z2)) {
        this.mPreference.setEnabled(true);
      } else if (this.mFeatureProvider.isSensorAvailable(this.mContext) && z2) {
        this.mPreference.setEnabled(true);
      } else {
        this.mPreference.setEnabled(false);
      }
      if (z && this.mFeatureProvider.isSupported(this.mContext)) {
        z2 = true;
      }
      updateGestureListenerState(z2);
    }
  }

  @Override
  public void displayPreference(PreferenceScreen preferenceScreen) {
    this.mPreference = (SeekBarPreference) preferenceScreen.findPreference(getPreferenceKey());
    if (this.mFeatureProvider.isSupported(this.mContext)) {
      removePreference(preferenceScreen, "gesture_assist_video_silence");
    } else {
      removePreference(preferenceScreen, "gesture_assist_video");
    }
    super.displayPreference(preferenceScreen);
  }

  public String getPreferenceKey() {
    return "gesture_assist_sensitivity";
  }

  public boolean handlePreferenceTreeClick(Preference preference) {
    if (!TextUtils.equals(preference.getKey(), "gesture_assist_sensitivity")) {
      return false;
    }
    System.arraycopy(this.mHits, 1, this.mHits, 0, this.mHits.length - 1);
    this.mHits[this.mHits.length - 1] = SystemClock.uptimeMillis();
    if (this.mHits[0] < SystemClock.uptimeMillis() - 500) {
      return false;
    }
    if (!this.mUserManager.hasUserRestriction("no_fun")) {
      Intent intent = new Intent(this.mContext, AssistGestureBubbleActivity.class);
      try {
        this.mContext.startActivity(intent);
        return true;
      } catch (Exception e) {
        Log.e(
            "AssistGestureSensitivityPreferenceController",
            "Unable to start activity " + intent.toString());
        return false;
      }
    } else if (this.mFunDisallowedAdmin == null) {
      return false;
    } else {
      RestrictedLockUtils.sendShowAdminSupportDetailsIntent(
          this.mContext, this.mFunDisallowedAdmin);
      return false;
    }
  }

  public boolean isAvailable() {
    return isAvailable(this.mContext, this.mFeatureProvider);
  }

  public void onPause() {
    updateGestureListenerState(false);
    this.mAssistGestureHelper.unbindFromElmyraServiceProxy();
    this.mSettingObserver.unregister();
  }

  public boolean onPreferenceChange(Preference preference, Object obj) {
    Secure.putFloat(
        this.mContext.getContentResolver(),
        "assist_gesture_sensitivity",
        convertSensitivityIntToFloat(this.mContext, ((Integer) obj).intValue()));
    return true;
  }

  public void onResume() {
    this.mAssistGestureHelper.bindToElmyraServiceProxy();
    this.mSettingObserver.register();
    updatePreference();
    this.mFunDisallowedAdmin =
        RestrictedLockUtils.checkIfRestrictionEnforced(
            this.mContext, "no_fun", UserHandle.myUserId());
    this.mFunDisallowedBySystem =
        RestrictedLockUtils.hasBaseUserRestriction(this.mContext, "no_fun", UserHandle.myUserId());
  }

  public void updateState(Preference preference) {
    super.updateState(preference);
    updatePreference();
  }

  class C10281 implements GestureListener {
    C10281() {}

    public void onGestureDetected() {
      AssistGestureSensitivityPreferenceController.this.mHandler.obtainMessage(1).sendToTarget();
    }

    public void onGestureProgress(float f, int i) {}
  }

  class SettingObserver extends ContentObserver {
    private final Uri ASSIST_GESTURE_ENABLED_URI = Secure.getUriFor("assist_gesture_enabled");
    private final Uri ASSIST_GESTURE_SENSITIVITY_URI =
        Secure.getUriFor("assist_gesture_sensitivity");
    private final Uri ASSIST_GESTURE_SILENCE_PHONE_ENABLED_URI =
        Secure.getUriFor("assist_gesture_silence_alerts_enabled");

    SettingObserver() {
      super(AssistGestureSensitivityPreferenceController.this.mHandler);
    }

    public void onChange(boolean z) {
      AssistGestureSensitivityPreferenceController.this.updatePreference();
    }

    public void register() {
      ContentResolver contentResolver =
          AssistGestureSensitivityPreferenceController.this.mContext.getContentResolver();
      contentResolver.registerContentObserver(this.ASSIST_GESTURE_ENABLED_URI, false, this);
      contentResolver.registerContentObserver(
          this.ASSIST_GESTURE_SILENCE_PHONE_ENABLED_URI, false, this);
      contentResolver.registerContentObserver(this.ASSIST_GESTURE_SENSITIVITY_URI, false, this);
    }

    public void unregister() {
      AssistGestureSensitivityPreferenceController.this
          .mContext
          .getContentResolver()
          .unregisterContentObserver(this);
    }
  }
}
