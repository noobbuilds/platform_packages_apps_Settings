package com.google.android.settings.external.specialcase;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.support.annotation.VisibleForTesting;
import android.provider.Settings.Secure;
import android.provider.Settings;
import com.android.settings.gestures.AssistGestureSettings;
import com.android.settings.overlay.FeatureFactory;
import com.google.android.settings.external.ExternalSettingsContract;
import com.google.android.settings.external.Queryable;
import com.google.android.settings.gestures.assist.AssistGestureSensitivityPreferenceController;
import com.android.settings.R;

public class ActiveEdgeSensitivitySetting implements Queryable {
    private static int getAvailability(Context context) {
        return AssistGestureSensitivityPreferenceController.isAvailable(context, FeatureFactory.getFactory(context).getAssistGestureFeatureProvider()) ? 0 : 2;
    }

    private int getIconResource() {
        return 0;
    }

    private String getScreenTitle(Context context) {
        return context.getString(R.string.assist_gesture_squeeze_sensitivity_label);
    }
    private String getSupportedValues(Context context) {
        int maxSensitivityResourceInteger = AssistGestureSensitivityPreferenceController.getMaxSensitivityResourceInteger(context);
        if (maxSensitivityResourceInteger < 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i <= maxSensitivityResourceInteger; i++) {
            stringBuilder.append(i).append(",");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    private void validateInput(Context context, int i) {
        int maxSensitivityResourceInteger = AssistGestureSensitivityPreferenceController.getMaxSensitivityResourceInteger(context);
        if (i < 0 || i > maxSensitivityResourceInteger) {
            throw new IllegalArgumentException("Requested sensitivity rating out of bounds. Expected between 0 and " + maxSensitivityResourceInteger + ", but found: " + i);
        }
    }

    public Cursor getAccessCursor(Context context) {
        int sensitivityInt = AssistGestureSensitivityPreferenceController.getSensitivityInt(context);
        String intentString = getIntentString(context, "assist_sensitivity", AssistGestureSettings.class, getScreenTitle(context));
        int iconResource = getIconResource();
        MatrixCursor matrixCursor = new MatrixCursor(ExternalSettingsContract.EXTERNAL_SETTINGS_QUERY_COLUMNS_WITH_SUPPORTED_VALUES);
        matrixCursor.newRow().add("existing_value", Integer.valueOf(sensitivityInt)).add("availability", Integer.valueOf(getAvailability(context))).add("intent", intentString).add("icon", Integer.valueOf(iconResource)).add("supported_values", getSupportedValues(context));
        return matrixCursor;
    }

    public Cursor getUpdateCursor(Context context, int i) {
        validateInput(context, i);
        int sensitivityInt = AssistGestureSensitivityPreferenceController.getSensitivityInt(context);
        int availability = getAvailability(context);
        String intentString = getIntentString(context, "assist_sensitivity", AssistGestureSettings.class, getScreenTitle(context));
        int iconResource = getIconResource();
        float convertSensitivityIntToFloat = AssistGestureSensitivityPreferenceController.convertSensitivityIntToFloat(context, i);
        if (!(shouldChangeValue(availability, sensitivityInt, i) && Secure.putFloat(context.getContentResolver(), "assist_gesture_sensitivity", convertSensitivityIntToFloat))) {
            i = sensitivityInt;
        }
        MatrixCursor matrixCursor = new MatrixCursor(ExternalSettingsContract.EXTERNAL_SETTINGS_UPDATE_COLUMNS);
        matrixCursor.newRow().add("newValue", Integer.valueOf(i)).add("existing_value", Integer.valueOf(sensitivityInt)).add("availability", Integer.valueOf(availability)).add("intent", intentString).add("icon", Integer.valueOf(iconResource));
        return matrixCursor;
    }
}
