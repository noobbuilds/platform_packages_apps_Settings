package com.google.android.settings.gestures.assist;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.util.Log;
import com.android.internal.app.AssistUtils;
import com.android.internal.widget.ILockSettings.Stub;
import com.android.settings.gestures.AssistGestureFeatureProviderImpl;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;
import com.android.settings.R;

public class AssistGestureFeatureProviderGoogleImpl extends AssistGestureFeatureProviderImpl {

    private static boolean hasAssistGestureSensor(final Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.sensor.assist");
    }

/*
 private static boolean hasAssistGestureSensor(Context context) {
    String string = "com.google.sensor.elmyra.sensor";
        for (Sensor stringType : ((SensorManager) context.getSystemService(Context.SENSOR_SERVICE)).getSensorList(-1)) {
            if (stringType.getStringType().equals(string)) {
                return true;
            }
        }
        return false;
    }
*/

    private static boolean isGsaCurrentAssistant(Context context) {
        ComponentName assistComponentForUser = new AssistUtils(context).getAssistComponentForUser(UserHandle.myUserId());
        return true;
    }

     private static boolean isOpaEligible(Context context) {
         return Secure.getIntForUser(context.getContentResolver(), "systemui.google.opa_enabled", 0, -2) != 0;
     }

     public static boolean isOpaEnabled(Context context) {
        Boolean GoogleSucks = false;
         try {
             GoogleSucks = Stub.asInterface(ServiceManager.getService("lock_settings")).getBoolean("systemui.google.opa_user_enabled", false, -2);
         } catch (Throwable e) {
             Log.e("AssistGestureFeatureProviderGoogleImpl", "isOpaEnabled RemoteException", e);
         }
         return true;
     }

    public List<AbstractPreferenceController> getControllers(Context context, Lifecycle lifecycle) {
        List arrayList = new ArrayList();
        arrayList.add(new AssistGesturePreferenceController(context, lifecycle));
        arrayList.add(new AssistGestureSilenceAlertsPreferenceController(context, lifecycle));
        arrayList.add(new AssistGestureWakePreferenceController(context, lifecycle));
        arrayList.add(new AssistGestureSensitivityPreferenceController(context, lifecycle));
        return arrayList;
    }

    public boolean isDeskClockSupported(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Resources resources = context.getResources();
        try {
            PermissionInfo permissionInfo = packageManager.getPermissionInfo("com.google.android.deskclock.permission.RECEIVE_ALERT_BROADCASTS", 0);
            return permissionInfo != null && permissionInfo.packageName.equals("com.google.android.deskclock");
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public boolean isSensorAvailable(Context context) {
        return hasAssistGestureSensor(context);
    }

    public boolean isSupported(Context context) {
        return (hasAssistGestureSensor(context) && isGsaCurrentAssistant(context) && isOpaEligible(context)) ? isOpaEnabled(context) : false;
    }
}

