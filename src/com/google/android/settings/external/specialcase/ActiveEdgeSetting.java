package com.google.android.settings.external.specialcase;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.Settings;
import android.provider.Settings.Secure;
import com.android.settings.gestures.AssistGestureSettings;
import com.android.settings.overlay.FeatureFactory;
import com.google.android.settings.external.ExternalSettingsContract;
import com.google.android.settings.external.Queryable;

public class ActiveEdgeSetting implements Queryable {
    private final String SECURE_KEY = "assist_gesture_enabled";

    private int getAvailability(Context context) {
        return FeatureFactory.getFactory(context).getAssistGestureFeatureProvider().isSupported(context) ? 0 : 2;
    }

    private int getCurrentValue(Context context) {
        return Secure.getInt(context.getContentResolver(), "assist_gesture_enabled", 1) != 0 ? 1 : 0;
    }

    private int getIconResource() {
        return 0;
    }

    private String getScreenTitle(Context context) {
        return context.getString(2131886384);
    }

    private void validateInput(int i) {
        if (i != 1 && i != 0) {
            throw new IllegalArgumentException("Unexpected value for Assist gesture. Expected 0 or 1, but found: " + i);
        }
    }

    public Cursor getAccessCursor(final Context context) {
        final int currentValue = this.getCurrentValue(context);
        final int availability = this.getAvailability(context);
        final int iconResource = this.getIconResource();
        final String intentString = this.getIntentString(context, "gesture_assist", AssistGestureSettings.class, this.getScreenTitle(context));
        final MatrixCursor matrixCursor = new MatrixCursor(ExternalSettingsContract.EXTERNAL_SETTINGS_QUERY_COLUMNS);
        matrixCursor.newRow().add("existing_value", (Object)currentValue).add("availability", (Object)availability).add("intent", (Object)intentString).add("icon", (Object)iconResource);
        return (Cursor)matrixCursor;
    }

    @Override
    public Cursor getUpdateCursor(final Context context, final int n) {
        this.validateInput(n);
        final int currentValue = this.getCurrentValue(context);
        final int availability = this.getAvailability(context);
        final String intentString = this.getIntentString(context, "gesture_assist", AssistGestureSettings.class, this.getScreenTitle(context));
        final int iconResource = this.getIconResource();
        int n2 = currentValue;
        if (this.shouldChangeValue(availability, currentValue, n)) {
            n2 = n2;
            if (Settings.Secure.putInt(context.getContentResolver(), "assist_gesture_enabled", n)) {
                n2 = n;
            }
        }
        final MatrixCursor matrixCursor = new MatrixCursor(ExternalSettingsContract.EXTERNAL_SETTINGS_UPDATE_COLUMNS);
        matrixCursor.newRow().add("newValue", (Object)n2).add("existing_value", (Object)currentValue).add("availability", (Object)availability).add("intent", (Object)intentString).add("icon", (Object)iconResource);
        return (Cursor)matrixCursor;
    }
}

/*
    public Cursor getAccessCursor(Context context) {
        int currentValue = getCurrentValue(context);
        int availability = getAvailability(context);
        int iconResource = getIconResource();
        String intentString = getIntentString(context, "gesture_assist", AssistGestureSettings.class, getScreenTitle(context));
        Cursor matrixCursor = new MatrixCursor(ExternalSettingsContract.EXTERNAL_SETTINGS_QUERY_COLUMNS);
        matrixCursor.newRow().add("existing_value", Integer.valueOf(currentValue)).add("availability", Integer.valueOf(availability)).add("intent", intentString).add("icon", Integer.valueOf(iconResource));
        return matrixCursor;
    }

    public Cursor getUpdateCursor(Context context, int i) {
        validateInput(i);
        int currentValue = getCurrentValue(context);
        int availability = getAvailability(context);
        String intentString = getIntentString(context, "gesture_assist", AssistGestureSettings.class, getScreenTitle(context));
        int iconResource = getIconResource();
        if (!(shouldChangeValue(availability, currentValue, i) && Secure.putInt(context.getContentResolver(), "assist_gesture_enabled", i))) {
            i = currentValue;
        }
        Cursor matrixCursor = new MatrixCursor(ExternalSettingsContract.EXTERNAL_SETTINGS_UPDATE_COLUMNS);
        matrixCursor.newRow().add("newValue", Integer.valueOf(i)).add("existing_value", Integer.valueOf(currentValue)).add("availability", Integer.valueOf(availability)).add("intent", intentString).add("icon", Integer.valueOf(iconResource));
        return matrixCursor;
    }
}
*/
