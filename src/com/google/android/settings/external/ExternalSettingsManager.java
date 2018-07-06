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
import com.android.settings.search.IndexDatabaseHelper;
import com.android.settings.search.InlinePayload;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.android.settings.search.CursorToSearchResultConverter.getUnmarshalledPayload;

public class ExternalSettingsManager {
  public static final Set<?> SPECIAL_SETTINGS;
  private static final String[] SELECT_COLUMNS;

  static {
    SELECT_COLUMNS = new String[] {"class_name", "payload_type", "payload", "icon", "screen_title"};
    final HashSet<String> set = new HashSet<String>();
    set.add("gesture_assist");
    set.add("assist_sensitivity");
    /*
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
    */
    SPECIAL_SETTINGS = Collections.unmodifiableSet((Set<?>) set);
  }

  private static void closeResource(Throwable t, AutoCloseable autoCloseable) {
    if (t != null) {
      try {
        autoCloseable.close();
        return;
      } catch (Throwable t2) {
        t.addSuppressed(t2);
        return;
      }
    }
    try {
      autoCloseable.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
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
      /*
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
      */
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
    final StringBuilder sb = new StringBuilder();
    sb.append("Invalid access special case key: ");
    sb.append(s2);
    throw new IllegalArgumentException(sb.toString());
  }

  public static Cursor getAccessCursorFromPayload(Context context, String s, String s2) {
    final Cursor cursorFromUriKey = getCursorFromUriKey(context, s2);
    Serializable buildTrampolineIntentString;
    final Serializable s3 = buildTrampolineIntentString = null;
    try {
      try {
        final String string =
            cursorFromUriKey.getString(cursorFromUriKey.getColumnIndex("class_name"));
        buildTrampolineIntentString = s3;
        final int int1 = cursorFromUriKey.getInt(cursorFromUriKey.getColumnIndex("payload_type"));
        buildTrampolineIntentString = s3;
        final byte[] blob = cursorFromUriKey.getBlob(cursorFromUriKey.getColumnIndex("payload"));
        buildTrampolineIntentString = s3;
        final int int2 = cursorFromUriKey.getInt(cursorFromUriKey.getColumnIndex("icon"));
        buildTrampolineIntentString = s3;
        final String string2 =
            cursorFromUriKey.getString(cursorFromUriKey.getColumnIndex("screen_title"));
        if (cursorFromUriKey != null) {
          closeResource(null, (AutoCloseable) cursorFromUriKey);
        }
        int value = -1;
        int availability = 5;
        buildTrampolineIntentString = buildTrampolineIntentString(context, string, s2, string2);
        if (int1 != 0) {
          final InlinePayload inlinePayload = (InlinePayload) getUnmarshalledPayload(blob, int1);
          value = inlinePayload.getValue(context);
          availability = inlinePayload.getAvailability();
        }
        final MatrixCursor matrixCursor =
            new MatrixCursor(ExternalSettingsContract.EXTERNAL_SETTINGS_QUERY_COLUMNS);
        matrixCursor
            .newRow()
            .add("existing_value", (Object) value)
            .add("availability", (Object) availability)
            .add("intent", (Object) buildTrampolineIntentString)
            .add("icon", (Object) int2);
        logAccessSetting(context, s, s2, value);
        return (Cursor) matrixCursor;
      } finally {
        if (cursorFromUriKey != null) {
          closeResource((Throwable) buildTrampolineIntentString, (AutoCloseable) cursorFromUriKey);
        }
      }
    } finally {

    }
  }

  public static Cursor getCursorFromUriKey(Context context, String s) {
    verifyIndexing(context);
    Cursor query =
        IndexDatabaseHelper.getInstance(context)
            .getReadableDatabase()
            .query(
                "prefs_index",
                ExternalSettingsManager.SELECT_COLUMNS,
                "data_key_reference like ? ",
                new String[] {s},
                (String) null,
                (String) null,
                (String) null);
    if (query.getCount() == 1 && query.moveToFirst()) {
      return query;
    }
    StringBuilder sb = new StringBuilder();
    sb.append("Key not found: ");
    sb.append(s);
    throw new IllegalArgumentException(sb.toString());
  }

  public static String getNewSettingValueQueryParameter(final Uri uri) {
    return uri.getQueryParameter("new_setting_value");
  }

  public static Cursor getUpdateCursorForSpecialSetting(
      final Context context, final String s, final String s2, final String s3) {
    Cursor cursor = null;
    if (s2.equals("gesture_assist")) {
      cursor = InlineSettings.ACTIVE_EDGE_SETTING.getUpdateCursor(context, s3);
    } else if (s2.equals("assist_sensitivity")) {
      cursor = InlineSettings.ACTIVE_EDGE_SENSITIVITY_SETTING.getUpdateCursor(context, s3);
      /*
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
      */
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
    final StringBuilder sb = new StringBuilder();
    sb.append("Invalid update special case key: ");
    sb.append(s2);
    throw new IllegalArgumentException(sb.toString());
  }

  public static Cursor getUpdateCursorFromPayload(
      final Context context, String t, final String s, final String s2) {
    Object o = getCursorFromUriKey(context, s);
    InlinePayload inlinePayload = null;
    try {
      final String string = ((Cursor) o).getString(((Cursor) o).getColumnIndex("class_name"));
      final int int1 = ((Cursor) o).getInt(((Cursor) o).getColumnIndex("payload_type"));
      final byte[] blob = ((Cursor) o).getBlob(((Cursor) o).getColumnIndex("payload"));
      final int int2 = ((Cursor) o).getInt(((Cursor) o).getColumnIndex("icon"));
      final String string2 = ((Cursor) o).getString(((Cursor) o).getColumnIndex("screen_title"));
      if (o != null) {
        closeResource(null, (AutoCloseable) o);
      }
      if (int1 != 0) {
        inlinePayload = (InlinePayload) getUnmarshalledPayload(blob, int1);
        o = buildTrampolineIntentString(context, s, string, string2);
        final int value = inlinePayload.getValue(context);
        final int availability = inlinePayload.getAvailability();
        final int n = value;
        final int intValue = Integer.valueOf(s2);
        int n2 = n;
        if (availability == 0) {
          n2 = n;
          if (inlinePayload.setValue(context, intValue)) {
            n2 = intValue;
          }
        }
        final MatrixCursor matrixCursor =
            new MatrixCursor(ExternalSettingsContract.EXTERNAL_SETTINGS_UPDATE_COLUMNS);
        matrixCursor
            .newRow()
            .add("newValue", (Object) n2)
            .add("existing_value", (Object) value)
            .add("availability", (Object) availability)
            .add("intent", o)
            .add("icon", (Object) int2);
        logUpdateSetting(context, (String) t, s, n2);
        return (Cursor) matrixCursor;
      }
      final StringBuilder sb = new StringBuilder();
      sb.append("No update support for settings key: ");
      sb.append(s);
      throw new IllegalArgumentException(sb.toString());
    } finally {

    }
  }

  private static void logAccessSetting(
      final Context context, String buildCountName, final String s, final int n) {
    final StringBuilder sb = new StringBuilder();
    sb.append(buildCountName);
    sb.append("/access");
    buildCountName =
        SharedPreferencesLogger.buildCountName(
            SharedPreferencesLogger.buildPrefKey(sb.toString(), s), n);
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

