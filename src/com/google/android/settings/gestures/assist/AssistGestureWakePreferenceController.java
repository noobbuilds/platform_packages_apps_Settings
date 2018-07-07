package com.google.android.settings.gestures.assist;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;

import com.android.settings.gestures.AssistGestureFeatureProvider;
import com.android.settings.gestures.GesturePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settings.R;

public class AssistGestureWakePreferenceController extends GesturePreferenceController
    implements OnPause, OnResume, LifecycleObserver {
  private final String ASSIST_GESTURE_WAKE_PREF_KEY = "gesture_assist_wake";
  private final AssistGestureFeatureProvider mFeatureProvider;
  private final SettingObserver mSettingObserver;
  private Handler mHandler;
  private SwitchPreference mPreference;
  private PreferenceScreen mScreen;

  public AssistGestureWakePreferenceController(Context context, Lifecycle lifecycle) {
    super(context, lifecycle);
    this.mFeatureProvider = FeatureFactory.getFactory(context).getAssistGestureFeatureProvider();
    this.mHandler = new Handler(Looper.getMainLooper());
    this.mSettingObserver = new SettingObserver();
    if (lifecycle != null) {
      lifecycle.addObserver(this);
    }
  }

  private void updatePreference() {
    if (this.mPreference != null) {
      if (this.mFeatureProvider.isSupported(this.mContext)) {
        if (this.mScreen.findPreference(getPreferenceKey()) == null) {
          this.mScreen.addPreference(this.mPreference);
        }
        this.mPreference.setEnabled(canHandleClicks());
        return;
      }
      this.mScreen.removePreference(this.mPreference);
    }
  }

  protected boolean canHandleClicks() {
    return Secure.getInt(this.mContext.getContentResolver(), "assist_gesture_enabled", 1) != 0;
  }

  public void displayPreference(PreferenceScreen preferenceScreen) {
    this.mScreen = preferenceScreen;
    this.mPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    if (this.mFeatureProvider.isSupported(this.mContext)) {
      super.displayPreference(preferenceScreen);
    } else {
      this.mScreen.removePreference(this.mPreference);
    }
  }

  public String getPreferenceKey() {
    return "gesture_assist_wake";
  }

  protected String getVideoPrefKey() {
    return "gesture_assist_video";
  }

  public boolean isAvailable() {
    return this.mFeatureProvider.isSensorAvailable(this.mContext);
  }

  protected boolean isSwitchPrefEnabled() {
    return Secure.getInt(this.mContext.getContentResolver(), "assist_gesture_wake_enabled", 1) != 0;
  }

  public void onPause() {
    this.mSettingObserver.unregister();
  }

  public boolean onPreferenceChange(Preference preference, Object obj) {
    Secure.putInt(
        this.mContext.getContentResolver(),
        "assist_gesture_wake_enabled",
        ((Boolean) obj).booleanValue() ? 1 : 0);
    return true;
  }

  public void onResume() {
    this.mSettingObserver.register();
    updatePreference();
  }

  class SettingObserver extends ContentObserver {
    private final Uri ASSIST_GESTURE_ENABLED_URI = Secure.getUriFor("assist_gesture_enabled");

    public SettingObserver() {
      super(AssistGestureWakePreferenceController.this.mHandler);
    }

    public void onChange(boolean z) {
      AssistGestureWakePreferenceController.this.updatePreference();
    }

    public void register() {
      AssistGestureWakePreferenceController.this
          .mContext
          .getContentResolver()
          .registerContentObserver(this.ASSIST_GESTURE_ENABLED_URI, false, this);
    }

    public void unregister() {
      AssistGestureWakePreferenceController.this
          .mContext
          .getContentResolver()
          .unregisterContentObserver(this);
    }
  }
}

