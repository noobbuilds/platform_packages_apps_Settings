package com.google.android.settings.external;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.support.annotation.VisibleForTesting;

import com.android.settings.Utils;
import com.android.settings.core.instrumentation.SharedPreferencesLogger;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.CursorToSearchResultConverter;
import com.android.settings.search.IndexDatabaseHelper;
import com.android.settings.search.InlinePayload;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ExternalSettingsManager {
  public static final Set<?> SPECIAL_SETTINGS;
  private static final String[] SELECT_COLUMNS;

  static {
    SELECT_COLUMNS = new String[] {"class_name", "payload_type", "payload", "icon", "screen_title"};
    final HashSet<String> set = new HashSet<String>();
    set.add("gesture_assist");
    set.add("assist_sensitivity");
    set.add("toggle_airplane");
    set.add("ambient_display_always_on");
    set.add("toggle_lock_screen_rotation_preference");
    set.add("bluetooth_toggle_key");
    set.add("color_inversion");
    set.add("data_saver");
    set.add("screen_timeout");
    set.add("zen_mode");
    set.add("gesture_double_twist");
    set.add("font_size");
    set.add("enable_wifi_ap");
    set.add("location");
    set.add("location_mode");
    set.add("magnify_gesture");
    set.add("magnify_navbar");
    set.add("mobile_data");
    set.add("nfc");
    set.add("night_display");
    set.add("night_display_temperature");
    set.add("gesture_pick_up");
    set.add("preferred_network_type");
    set.add("screen_zoom");
    set.add("swipe_to_notification");
    set.add("switch_access");
    set.add("system_update");
    set.add("talkback");
    set.add("master_wifi_toggle");
    SPECIAL_SETTINGS = Collections.unmodifiableSet((Set<?>) set);
  }

  public static String buildTrampolineIntentString(
      final Context context, final String s, final String s2, final String s3) {
    final Intent onBuildStartFragmentIntent =
        Utils.onBuildStartFragmentIntent(context, s2, null, null, 0, s3, false, 1033);
    onBuildStartFragmentIntent.setClass(context, (Class) ExternalSettingsTrampoline.class);
    onBuildStartFragmentIntent.putExtra(":settingsgoogle:fragment_args_key", s);
    return onBuildStartFragmentIntent.toUri(0);
  }

  public static Cursor getAccessCursorForSpecialSetting(
      final Context context, final String s, final String s2) {
    Cursor cursor = null;
    if (s2.equals("gesture_assist")) {
      cursor = InlineSettings.ACTIVE_EDGE_SETTING.getAccessCursor(context);
    } else if (s2.equals("assist_sensitivity")) {
      cursor = InlineSettings.ACTIVE_EDGE_SENSITIVITY_SETTING.getAccessCursor(context);
    } else if (s2.equals("toggle_airplane")) {
      cursor = InlineSettings.AIRPLANE_MODE_SETTING.getAccessCursor(context);
    } else if (s2.equals("ambient_display_always_on")) {
      cursor = InlineSettings.AMBIENT_DISPLAY_ALWAYS_ON_SETTING.getAccessCursor(context);
    } else if (s2.equals("toggle_lock_screen_rotation_preference")) {
      cursor = InlineSettings.AUTO_ROTATE_SETTING.getAccessCursor(context);
    } else if (s2.equals("bluetooth_toggle_key")) {
      cursor = InlineSettings.BLUETOOTH_SETTING.getAccessCursor(context);
    } else if (s2.equals("color_inversion")) {
      cursor = InlineSettings.COLOR_INVERSION_SETTING.getAccessCursor(context);
    } else if (s2.equals("data_saver")) {
      cursor = InlineSettings.DATA_SAVER_SETTING.getAccessCursor(context);
    } else if (s2.equals("screen_timeout")) {
      cursor = InlineSettings.DISPLAY_TIMEOUT_SETTING.getAccessCursor(context);
    } else if (s2.equals("zen_mode")) {
      cursor = InlineSettings.DO_NOT_DISTURB_SETTING.getAccessCursor(context);
    } else if (s2.equals("gesture_double_twist")) {
      cursor = InlineSettings.DOUBLE_TWIST_GESTURE_SETTING.getAccessCursor(context);
    } else if (s2.equals("font_size")) {
      cursor = InlineSettings.FONT_SIZE_SETTING.getAccessCursor(context);
    } else if (s2.equals("enable_wifi_ap")) {
      cursor = InlineSettings.HOTSPOT_SETTING.getAccessCursor(context);
    } else if (s2.equals("location")) {
      cursor = InlineSettings.LOCATION_SETTING.getAccessCursor(context);
    } else if (s2.equals("location_mode")) {
      cursor = InlineSettings.LOCATION_MODE_SETTING.getAccessCursor(context);
    } else if (s2.equals("magnify_gesture")) {
      cursor = InlineSettings.MAGNIFY_GESTURE_SETTING.getAccessCursor(context);
    } else if (s2.equals("magnify_navbar")) {
      cursor = InlineSettings.MAGNIFY_NAVBAR_SETTING.getAccessCursor(context);
    } else if (s2.equals("mobile_data")) {
      cursor = InlineSettings.MOBILE_DATA_SETTING.getAccessCursor(context);
    } else if (s2.equals("nfc")) {
      cursor = InlineSettings.NFC_SETTING.getAccessCursor(context);
    } else if (s2.equals("night_display")) {
      cursor = InlineSettings.NIGHTDISPLAY_SETTING.getAccessCursor(context);
    } else if (s2.equals("night_display_temperature")) {
      cursor = InlineSettings.NIGHTDISPLAY_INTENSITY_SETTING.getAccessCursor(context);
    } else if (s2.equals("gesture_pick_up")) {
      cursor = InlineSettings.PICKUP_GESTURE_SETTING.getAccessCursor(context);
    } else if (s2.equals("screen_zoom")) {
      cursor = InlineSettings.SCREEN_ZOOM_SETTING.getAccessCursor(context);
    } else if (s2.equals("swipe_to_notification")) {
      cursor = InlineSettings.SWIPE_TO_NOTIFICATION_SETTING.getAccessCursor(context);
    } else if (s2.equals("switch_access")) {
      cursor = InlineSettings.SWITCH_ACCESS_SETTING.getAccessCursor(context);
    } else if (s2.equals("system_update")) {
      cursor = InlineSettings.SYSTEM_UPDATE_SETTING.getAccessCursor(context);
    } else if (s2.equals("talkback")) {
      cursor = InlineSettings.TALKBACK_SETTING.getAccessCursor(context);
    } else if (s2.equals("master_wifi_toggle")) {
      cursor = InlineSettings.WIFI_SETTING.getAccessCursor(context);
    } else if (s2.equals("preferred_network_type")) {
      cursor = InlineSettings.MOBILE_NETWORK_SETTINGS.getAccessCursor(context);
    }
    if (cursor != null && cursor.moveToFirst()) {
      final int columnIndex = cursor.getColumnIndex("existing_value");
      int int1;
      if (columnIndex >= 0) {
        int1 = cursor.getInt(columnIndex);
      } else {
        int1 = -1;
      }
      logAccessSetting(context, s, s2, int1);
      return cursor;
    }
    throw new IllegalArgumentException("Invalid access special case key: " + s2);
  }

  public static Cursor getAccessCursorFromPayload(Context context, String str, String str2) {
    Cursor cursorFromUriKey;
    Throwable th;
    Throwable th2;
    Throwable th3 = null;
    try {
      cursorFromUriKey = getCursorFromUriKey(context, str2);
      try {
        String string = cursorFromUriKey.getString(cursorFromUriKey.getColumnIndex("class_name"));
        int i = cursorFromUriKey.getInt(cursorFromUriKey.getColumnIndex("payload_type"));
        byte[] blob = cursorFromUriKey.getBlob(cursorFromUriKey.getColumnIndex("payload"));
        int i2 = cursorFromUriKey.getInt(cursorFromUriKey.getColumnIndex("icon"));
        String string2 =
            cursorFromUriKey.getString(cursorFromUriKey.getColumnIndex("screen_title"));
        if (cursorFromUriKey != null) {
          try {
            cursorFromUriKey.close();
          } catch (Throwable th4) {
            th3 = th4;
          }
        }
        if (th3 != null) {
          throw th3;
        }
        int i3 = -1;
        int i4 = 5;
        String buildTrampolineIntentString =
            buildTrampolineIntentString(context, str2, string, string2);
        if (i != 0) {
          InlinePayload inlinePayload =
              (InlinePayload) CursorToSearchResultConverter.getUnmarshalledPayload(blob, i);
          i3 = inlinePayload.getValue(context);
          i4 = inlinePayload.getAvailability();
        }
        Cursor matrixCursor =
            new MatrixCursor(ExternalSettingsContract.EXTERNAL_SETTINGS_QUERY_COLUMNS);
        matrixCursor
            .newRow()
            .add("existing_value", Integer.valueOf(i3))
            .add("availability", Integer.valueOf(i4))
            .add("intent", buildTrampolineIntentString)
            .add("icon", Integer.valueOf(i2));
        logAccessSetting(context, str, str2, i3);
        return matrixCursor;
      } catch (Throwable th5) {
        th = th5;
        th2 = th;
        th = null;
        th3 = th2;
        if (cursorFromUriKey != null) {
          try {
            cursorFromUriKey.close();
          } catch (Throwable th6) {
            if (th == null) {
              th = th6;
            } else if (th != th6) {
              th.addSuppressed(th6);
            }
          }
        }
        if (th == null) {
          throw th;
        }
        throw th3;
      }
    } catch (Throwable th7) {
      th = th7;
      cursorFromUriKey = null;
      th2 = th;
      th = null;
      th3 = th2;
      if (cursorFromUriKey != null) {
        cursorFromUriKey.close();
      }
      if (th == null) {
        try {
          throw th3;
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }
      try {
        throw th;
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }

  public static Cursor getCursorFromUriKey(Context context, String str) {
    verifyIndexing(context);
    Cursor query =
        IndexDatabaseHelper.getInstance(context)
            .getReadableDatabase()
            .query(
                "prefs_index",
                SELECT_COLUMNS,
                "data_key_reference like ? ",
                new String[] {str},
                null,
                null,
                null);
    if (query.getCount() != 1 || (query.moveToFirst() ^ true)) {
      throw new IllegalArgumentException("Key not found: " + str);
    }
    return query;
  }

  public static String getNewSettingValueQueryParameter(Uri uri) {
    return uri.getQueryParameter("new_setting_value");
  }

  public static Cursor getUpdateCursorForSpecialSetting(
      final Context context, final String s, final String s2, final String s3) {
    Cursor cursor = null;
    if (s2.equals("gesture_assist")) {
      cursor = InlineSettings.ACTIVE_EDGE_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("assist_sensitivity")) {
      cursor = InlineSettings.ACTIVE_EDGE_SENSITIVITY_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("toggle_airplane")) {
      cursor = InlineSettings.AIRPLANE_MODE_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("ambient_display_always_on")) {
      cursor = InlineSettings.AMBIENT_DISPLAY_ALWAYS_ON_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("toggle_lock_screen_rotation_preference")) {
      cursor = InlineSettings.AUTO_ROTATE_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("bluetooth_toggle_key")) {
      cursor = InlineSettings.BLUETOOTH_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("color_inversion")) {
      cursor = InlineSettings.COLOR_INVERSION_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("data_saver")) {
      cursor = InlineSettings.DATA_SAVER_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("screen_timeout")) {
      cursor = InlineSettings.DISPLAY_TIMEOUT_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("zen_mode")) {
      cursor = InlineSettings.DO_NOT_DISTURB_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("gesture_double_twist")) {
      cursor = InlineSettings.DOUBLE_TWIST_GESTURE_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("font_size")) {
      cursor = InlineSettings.FONT_SIZE_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("enable_wifi_ap")) {
      cursor = InlineSettings.HOTSPOT_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("location")) {
      cursor = InlineSettings.LOCATION_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("location_mode")) {
      cursor = InlineSettings.LOCATION_MODE_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("magnify_gesture")) {
      cursor = InlineSettings.MAGNIFY_GESTURE_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("magnify_navbar")) {
      cursor = InlineSettings.MAGNIFY_NAVBAR_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("mobile_data")) {
      cursor = InlineSettings.MOBILE_DATA_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("nfc")) {
      cursor = InlineSettings.NFC_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("night_display")) {
      cursor = InlineSettings.NIGHTDISPLAY_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("night_display_temperature")) {
      cursor = InlineSettings.NIGHTDISPLAY_INTENSITY_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("gesture_pick_up")) {
      cursor = InlineSettings.PICKUP_GESTURE_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("screen_zoom")) {
      cursor = InlineSettings.SCREEN_ZOOM_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("swipe_to_notification")) {
      cursor = InlineSettings.SWIPE_TO_NOTIFICATION_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("switch_access")) {
      cursor = InlineSettings.SWITCH_ACCESS_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("system_update")) {
      cursor = InlineSettings.SYSTEM_UPDATE_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("talkback")) {
      cursor = InlineSettings.TALKBACK_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("master_wifi_toggle")) {
      cursor = InlineSettings.WIFI_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("preferred_network_type")) {
      cursor = InlineSettings.MOBILE_NETWORK_SETTINGS.getUpdateCursor(context, s3);
    }
    if (cursor != null && cursor.moveToFirst()) {
      final int columnIndex = cursor.getColumnIndex("newValue");
      int int1;
      if (columnIndex >= 0) {
        int1 = cursor.getInt(columnIndex);
      } else {
        int1 = -1;
      }
      logUpdateSetting(context, s, s2, int1);
      return cursor;
    }
    throw new IllegalArgumentException("Invalid update special case key: " + s2);
  }

  public static Cursor getUpdateCursorFromPayload(
      Context context, String str, String str2, String str3) {
    Throwable th;
    Throwable th2;
    Throwable th3 = null;
    Cursor cursorFromUriKey;
    try {
      cursorFromUriKey = getCursorFromUriKey(context, str2);
      try {
        String string = cursorFromUriKey.getString(cursorFromUriKey.getColumnIndex("class_name"));
        int i = cursorFromUriKey.getInt(cursorFromUriKey.getColumnIndex("payload_type"));
        byte[] blob = cursorFromUriKey.getBlob(cursorFromUriKey.getColumnIndex("payload"));
        int i2 = cursorFromUriKey.getInt(cursorFromUriKey.getColumnIndex("icon"));
        String string2 =
            cursorFromUriKey.getString(cursorFromUriKey.getColumnIndex("screen_title"));
        if (cursorFromUriKey != null) {
          try {
            cursorFromUriKey.close();
          } catch (Throwable th4) {
            th3 = th4;
          }
        }
        if (th3 != null) {
          throw th3;
        } else if (i == 0) {
          throw new IllegalArgumentException("No update support for settings key: " + str2);
        } else {
          InlinePayload inlinePayload =
              (InlinePayload) CursorToSearchResultConverter.getUnmarshalledPayload(blob, i);
          string = buildTrampolineIntentString(context, str2, string, string2);
          int value = inlinePayload.getValue(context);
          int availability = inlinePayload.getAvailability();
          int intValue = Integer.valueOf(str3).intValue();
          i = (availability == 0 && inlinePayload.setValue(context, intValue)) ? intValue : value;
          cursorFromUriKey =
              new MatrixCursor(ExternalSettingsContract.EXTERNAL_SETTINGS_UPDATE_COLUMNS);
          cursorFromUriKey
              .newRow()
              .add("newValue", Integer.valueOf(i))
              .add("existing_value", Integer.valueOf(value))
              .add("availability", Integer.valueOf(availability))
              .add("intent", string)
              .add("icon", Integer.valueOf(i2));
          logUpdateSetting(context, str, str2, i);
          return cursorFromUriKey;
        }
      } catch (Throwable th5) {
        th = th5;
        th2 = th;
        th = null;
        th3 = th2;
        if (cursorFromUriKey != null) {
          try {
            cursorFromUriKey.close();
          } catch (Throwable th6) {
            if (th == null) {
              th = th6;
            } else if (th != th6) {
              th.addSuppressed(th6);
            }
          }
        }
        if (th == null) {
          throw th;
        }
        throw th3;
      }
    } catch (Throwable th7) {
      th = th7;
      cursorFromUriKey = null;
      th2 = th;
      th = null;
      th3 = th2;
      if (cursorFromUriKey != null) {
        cursorFromUriKey.close();
      }
      if (th == null) {
        try {
          throw th3;
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }
      try {
        throw th;
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }

  private static void logAccessSetting(
      final Context context, String buildCountName, final String s, final int n) {
    buildCountName =
        SharedPreferencesLogger.buildCountName(
            SharedPreferencesLogger.buildPrefKey(buildCountName + "/access", s), n);
    FeatureFactory.getFactory(context)
        .getMetricsFeatureProvider()
        .count(context, buildCountName, 1);
  }

  private static void logUpdateSetting(
      final Context context, String buildCountName, final String s, final int n) {
    buildCountName =
        SharedPreferencesLogger.buildCountName(
            SharedPreferencesLogger.buildPrefKey(buildCountName, s), n);
    FeatureFactory.getFactory(context)
        .getMetricsFeatureProvider()
        .count(context, buildCountName, 1);
  }

  @VisibleForTesting
  static void verifyIndexing(final Context context) {
    final long clearCallingIdentity = Binder.clearCallingIdentity();
    try {
      FeatureFactory.getFactory(context).getSearchFeatureProvider().updateIndex(context);
    } finally {
      Binder.restoreCallingIdentity(clearCallingIdentity);
    }
  }
}
