package com.google.android.settings.gestures.assist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;

import com.google.android.systemui.elmyra.IElmyraService;
import com.google.android.systemui.elmyra.IElmyraService.Stub;
import com.google.android.systemui.elmyra.IElmyraServiceSettingsListener;
import com.android.settings.R;

public class AssistGestureHelper {
  private boolean mBoundToService;
  private Context mContext;
  private IElmyraServiceSettingsListener mElmyraServiceSettingsListener = new C10272();
  private GestureListener mGestureListener;
  private PowerManager mPowerManager;
  private IElmyraService mService;
  private ServiceConnection mServiceConnection = new C10261();
  private IBinder mToken = new Binder();

  public AssistGestureHelper(Context context) {
    this.mContext = context;
    this.mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
  }

  public void bindToElmyraServiceProxy() {
    if (this.mService == null) {
      try {
        Intent intent = new Intent();
        intent.setComponent(
            new ComponentName(
                "com.android.systemui", "com.google.android.systemui.elmyra.ElmyraServiceProxy"));
        this.mContext.bindServiceAsUser(
            intent, this.mServiceConnection, Context.BIND_AUTO_CREATE, UserHandle.getUserHandleForUid(0));
        this.mBoundToService = true;
      } catch (Throwable e) {
        Log.e("AssistGestureHelper", "Unable to bind to ElmyraService", e);
      }
    }
  }

  public void launchAssistant() {
    try {
      this.mService.launchAssistant();
    } catch (Throwable e) {
      Log.e("AssistGestureHelper", "launchAssistant()", e);
    }
  }

  public void setListener(GestureListener gestureListener) {
    this.mGestureListener = gestureListener;
    if (this.mService == null) {
      Log.w("AssistGestureHelper", "Service is null, should try to reconnect");
    } else if (gestureListener != null) {
      try {
        this.mService.registerSettingsListener(
            this.mToken, (IBinder) this.mElmyraServiceSettingsListener);
      } catch (Throwable e) {
        Log.e(
            "AssistGestureHelper",
            "Failed to " + (gestureListener == null ? "unregister" : "register") + " listener",
            e);
      }
    } else {
      try {
        this.mService.registerSettingsListener(this.mToken, null);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
  }

  public void unbindFromElmyraServiceProxy() {
    if (this.mBoundToService) {
      this.mContext.unbindService(this.mServiceConnection);
      this.mBoundToService = false;
    }
  }

  public interface GestureListener {
    void onGestureDetected();

    void onGestureProgress(float f, int i);
  }

  class C10261 implements ServiceConnection {
    C10261() {}

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      AssistGestureHelper.this.mService = Stub.asInterface(iBinder);
      if (AssistGestureHelper.this.mGestureListener != null) {
        try {
          AssistGestureHelper.this.mService.registerSettingsListener(
              AssistGestureHelper.this.mToken,
              (IBinder) AssistGestureHelper.this.mElmyraServiceSettingsListener);
        } catch (Throwable e) {
          Log.e("AssistGestureHelper", "registerSettingsListener()", e);
        }
      }
    }

    public void onServiceDisconnected(ComponentName componentName) {
      AssistGestureHelper.this.mService = null;
    }
  }

  class C10272 extends IElmyraServiceSettingsListener.Stub {
    private int mLastStage = 0;

    C10272() {}

    public void onGestureDetected() {
      AssistGestureHelper.this.mGestureListener.onGestureDetected();
    }

    public void onGestureProgress(float f, int i) {
      AssistGestureHelper.this.mGestureListener.onGestureProgress(f, i);
      if (this.mLastStage != 2 && i == 2) {
        AssistGestureHelper.this.mPowerManager.userActivity(SystemClock.uptimeMillis(), 0, 0);
      }
      this.mLastStage = i;
    }
  }
}

