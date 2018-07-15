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
import com.google.android.systemui.elmyra.IElmyraServiceGestureListener;
import com.google.android.systemui.elmyra.IElmyraServiceSettingsListener;

import static android.os.Binder.getCallingPid;
import static android.os.Binder.getCallingUid;

public class AssistGestureHelper {
  private boolean mBoundToService;
  private Context mContext;
  private IElmyraServiceSettingsListener mElmyraServiceSettingsListener;
  private IElmyraServiceGestureListener mElmyraServiceGestureListener;
  private GestureListener mGestureListener;
  private PowerManager mPowerManager;
  private IElmyraService mService;
  private ServiceConnection mServiceConnection;
  private IBinder mToken;
  private static String LOG_TAG = "AssistGestureElmyra";

  public AssistGestureHelper(Context mContext) {
    mToken = new Binder();
    mServiceConnection =
        new ServiceConnection() {
          public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mService = IElmyraService.Stub.asInterface(binder);
            if (mGestureListener == null) {
              return;
            }
            try {
              mService.registerSettingsListener(mToken, (IBinder) mElmyraServiceSettingsListener);
            } catch (RemoteException ex) {
              Log.e("AssistGestureHelper", "registerSettingsListener()", (Throwable) ex);
            }
          }

          public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
          }
        };
    mElmyraServiceSettingsListener =
        new IElmyraServiceSettingsListener.Stub() {
          private int mLastStage = 0;

          public void onGestureDetected() throws RemoteException {
            if (AssistGestureHelper.this.mGestureListener != null) {
              AssistGestureHelper.this.mGestureListener.onGestureDetected();
            }
          }

          public void onGestureProgress(final float n, final int mLastStage)
              throws RemoteException {
            if (AssistGestureHelper.this.mGestureListener != null) {
              AssistGestureHelper.this.mGestureListener.onGestureProgress(n, mLastStage);
            }
            if (this.mLastStage != 2 && mLastStage == 2) {
              AssistGestureHelper.this.mPowerManager.userActivity(SystemClock.uptimeMillis(), 0, 0);
            }
            this.mLastStage = mLastStage;
          }
        };
    this.mContext = mContext;
    this.mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
  }

    public void bindToElmyraServiceProxy() {
        if (this.mService != null) {
            return;
        }
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.android.systemui", "com.google.android.systemui.elmyra.ElmyraServiceProxy"));
            this.mContext.bindServiceAsUser(intent, this.mServiceConnection, Context.BIND_AUTO_CREATE, UserHandle.getUserHandleForUid(0));
            Log.d(LOG_TAG, "Starting service: " + intent);
            Log.i(LOG_TAG, " caller's uid " + getCallingUid()
                    + ", pid " + getCallingPid());
            mContext.startService(intent);
            this.mBoundToService = true;
        }
        catch (SecurityException ex) {
            Log.e("AssistGestureHelper", "Unable to bind to ElmyraService", ex);
        }
    }

  public void launchAssistant() {
    try {
      mService.launchAssistant();
    } catch (RemoteException ex) {
      Log.e("AssistGestureHelper", "launchAssistant()", (Throwable) ex);
    }
  }

  public void setListener(GestureListener mGestureListener) {
    this.mGestureListener = mGestureListener;
    if (this.mService == null) {
      Log.w("AssistGestureHelper", "Service is null, should try to reconnect");
      return;
    }
    while (true) {
      if (this.mGestureListener != null) {
        try {
          mService.registerSettingsListener(mToken, (IBinder) mElmyraServiceSettingsListener);
        } catch (RemoteException ex) {
          final StringBuilder sb = new StringBuilder();
          sb.append("Failed to ");
          String s;
          if (this.mGestureListener == null) {
            s = "unregister";
          } else {
            s = "register";
          }
          sb.append(s);
          sb.append(" listener");
          Log.e("AssistGestureHelper", sb.toString(), ex);
          try {
            mService.registerSettingsListener(this.mToken, null);
          } catch (RemoteException e) {
            e.printStackTrace();
          }
        }
        return;
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

    void onGestureProgress(final float p0, final int p1);
  }
}

