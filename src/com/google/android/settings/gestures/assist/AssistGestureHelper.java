package com.google.android.settings.gestures.assist;

import android.os.UserHandle;
import android.content.Intent;
import android.os.SystemClock;
import android.os.RemoteException;
import android.util.Log;
import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.content.ServiceConnection;
import com.google.android.systemui.elmyra.IElmyraService;
import android.os.PowerManager;
import com.google.android.systemui.elmyra.IElmyraServiceSettingsListener;
import android.content.Context;

public class AssistGestureHelper
{
    private boolean mBoundToService;
    private Context mContext;
    private final IElmyraServiceSettingsListener mElmyraServiceSettingsListener;
    private GestureListener mGestureListener;
    private PowerManager mPowerManager;
    private IElmyraService mService;
    private final ServiceConnection mServiceConnection;
    private IBinder mToken;
    
    public AssistGestureHelper(final Context mContext) {
        this.mToken = (IBinder)new Binder();
        this.mServiceConnection = (ServiceConnection)new ServiceConnection() {
            public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
                AssistGestureHelper.this.mService = IElmyraService.Stub.asInterface(binder);
                if (AssistGestureHelper.this.mGestureListener == null) {
                    return;
                }
                try {
                    AssistGestureHelper.this.mService.registerSettingsListener(AssistGestureHelper.this.mToken, (IBinder)AssistGestureHelper.this.mElmyraServiceSettingsListener);
                }
                catch (RemoteException ex) {
                    Log.e("AssistGestureHelper", "registerSettingsListener()", (Throwable)ex);
                }
            }
            
            public void onServiceDisconnected(final ComponentName componentName) {
                AssistGestureHelper.this.mService = null;
            }
        };
        this.mElmyraServiceSettingsListener = new IElmyraServiceSettingsListener.Stub() {
            private int mLastStage = 0;
            
            public void onGestureDetected() throws RemoteException {
                AssistGestureHelper.this.mGestureListener.onGestureDetected();
            }
            
            public void onGestureProgress(final float n, final int mLastStage) throws RemoteException {
                AssistGestureHelper.this.mGestureListener.onGestureProgress(n, mLastStage);
                if (this.mLastStage != 2 && mLastStage == 2) {
                    AssistGestureHelper.this.mPowerManager.userActivity(SystemClock.uptimeMillis(), 0, 0);
                }
                this.mLastStage = mLastStage;
            }
        };
        this.mContext = mContext;
        this.mPowerManager = (PowerManager)mContext.getSystemService("power");
    }
    
    public void bindToElmyraServiceProxy() {
        if (this.mService != null) {
            return;
        }
        try {
            final Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.android.systemui", "com.google.android.systemui.elmyra.ElmyraServiceProxy"));
            this.mContext.bindServiceAsUser(intent, this.mServiceConnection, 1, UserHandle.getUserHandleForUid(0));
            this.mBoundToService = true;
        }
        catch (SecurityException ex) {
            Log.e("AssistGestureHelper", "Unable to bind to ElmyraService", (Throwable)ex);
        }
    }
    
    public void launchAssistant() {
        try {
            this.mService.launchAssistant();
        }
        catch (RemoteException ex) {
            Log.e("AssistGestureHelper", "launchAssistant()", (Throwable)ex);
        }
    }
    
    public void setListener(GestureListener gestureListener) {
        this.mGestureListener = gestureListener;
        if (this.mService == null) {
            Log.w("AssistGestureHelper", "Service is null, should try to reconnect");
        } else if (gestureListener != null) {
            try {
                this.mService.registerSettingsListener(this.mToken, (IBinder) this.mElmyraServiceSettingsListener);
            } catch (Throwable e) {
                Log.e("AssistGestureHelper", "Failed to " + (gestureListener == null ? "unregister" : "register") + " listener", e);
            }
        } else {
            try {

            this.mService.registerSettingsListener(this.mToken, null);
        }
            catch (RemoteException ex) {
        Log.e("AssistGestureHelper", "Failed to " + (gestureListener == null ? "unregister" : "register") + " listener", ex);
            }
   }
}

    
    public void unbindFromElmyraServiceProxy() {
        if (this.mBoundToService) {
            this.mContext.unbindService(this.mServiceConnection);
            this.mBoundToService = false;
        }
    }
    
    public interface GestureListener
    {
        void onGestureDetected();
        
        void onGestureProgress(final float p0, final int p1);
    }
}

