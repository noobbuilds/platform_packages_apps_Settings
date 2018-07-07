package com.google.android.settings.external;

import android.net.Uri;

public class ExternalSettingsContract {
    private static Uri CONTENT_BASE_URI = Uri.parse("content://com.google.android.settings.external");
    public static final String[] DEVICE_SIGNALS_COLUMNS = new String[]{"signal_key", "signal_value"};
    public static final Uri DEVICE_SIGNALS_URI = Uri.withAppendedPath(CONTENT_BASE_URI, "signals");
    public static final String[] EXTERNAL_SETTINGS_QUERY_COLUMNS = new String[]{"existing_value", "availability", "intent", "icon", "dependent_setting"};
    public static final String[] EXTERNAL_SETTINGS_QUERY_COLUMNS_WITH_SUPPORTED_VALUES = new String[]{"existing_value", "availability", "intent", "icon", "supported_values", "dependent_setting"};
    public static final String[] EXTERNAL_SETTINGS_QUERY_RANGE_COLUMNS = new String[]{"existing_value", "availability", "intent", "min_value", "max_value", "icon", "dependent_setting"};
    public static final String[] EXTERNAL_SETTINGS_UPDATE_COLUMNS = new String[]{"newValue", "existing_value", "availability", "intent", "icon", "dependent_setting"};
    public static final Uri SETTINGS_MANAGER_URI = Uri.withAppendedPath(CONTENT_BASE_URI, "settings_manager");

    static {
        Uri uri = CONTENT_BASE_URI;
        uri = CONTENT_BASE_URI;
    }
}
