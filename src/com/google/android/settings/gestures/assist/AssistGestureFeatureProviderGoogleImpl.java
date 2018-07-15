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
import android.provider.Settings;
import android.util.Log;

import com.android.internal.app.AssistUtils;
import com.android.internal.widget.ILockSettings.Stub;
import com.android.settings.gestures.AssistGestureFeatureProviderImpl;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;

public class AssistGestureFeatureProviderGoogleImpl extends AssistGestureFeatureProviderImpl {
  private static String TAG = AssistGestureFeatureProviderGoogleImpl.class.getSimpleName();

  private static boolean hasAssistGestureSensor(Context context) {
    String Elmyra = "com.google.sensor.elmyra.sensor";
    for (Sensor stringType :
        ((SensorManager) context.getSystemService(Context.SENSOR_SERVICE))
            .getSensorList(Sensor.TYPE_ALL)) {
      if (stringType.getStringType().equals(Elmyra)) {
        Log.w(TAG, " Found Sensor");
        return true;
      }
    }
    Log.w(TAG, " Didnt find sensor");
    return false;
  }

  private static boolean isGsaCurrentAssistant(Context context) {
    ComponentName assistComponentForUser =
        new AssistUtils(context).getAssistComponentForUser(UserHandle.USER_CURRENT);
    return true;
  }

  private static boolean isOpaEligible(Context context) {
    return Settings.Secure.getIntForUser(
            context.getContentResolver(), "systemui.google.opa_enabled", 0, UserHandle.USER_CURRENT)
        != 0;
  }

  public static boolean isOpaEnabled(Context context) {
    boolean z = false;
    try {
      z =
          Stub.asInterface(ServiceManager.getService("lock_settings"))
              .getBoolean("systemui.google.opa_user_enabled", false, UserHandle.USER_CURRENT);
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
      PermissionInfo permissionInfo =
          packageManager.getPermissionInfo(
              "com.google.android.deskclock.permission.RECEIVE_ALERT_BROADCASTS", 0);
      return permissionInfo != null
          && permissionInfo.packageName.equals("com.google.android.deskclock");
    } catch (NameNotFoundException e) {
      return false;
    }
  }

  public boolean isSensorAvailable(Context context) {
    return hasAssistGestureSensor(context);
  }

  public boolean isSupported(Context context) {
    return (hasAssistGestureSensor(context)
            && isGsaCurrentAssistant(context)
            && isOpaEligible(context))
        && isOpaEnabled(context);
  }
}

