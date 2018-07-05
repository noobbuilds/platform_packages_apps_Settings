package com.google.android.settings.external;

import android.database.Cursor;
import android.app.Fragment;
import android.content.Context;

public interface Queryable
{
    default String getIntentString(final Context context, final String s, final Class<? extends Fragment> clazz, final String s2) {
        return ExternalSettingsManager.buildTrampolineIntentString(context, s, clazz.getName(), s2);
    }

    default Cursor getUpdateCursor(final Context context, final float n) {
        throw new UnsupportedOperationException("Method not supported");
    }

    default Cursor getUpdateCursor(final Context context, final int n) {
        throw new UnsupportedOperationException("Method not supported");
    }

    default Cursor getUpdateCursor(final Context context, final String s) {
        try {
            return this.getUpdateCursor(context, Integer.valueOf(s));
        }
        catch (NumberFormatException ex) {
            return this.getUpdateCursor(context, Float.valueOf(s));
        }
    }

    default boolean shouldChangeValue(final int n, final int n2, final int n3) {
        boolean b = false;
        if (n == 0) {
            b = b;
            if (n2 != n3) {
                b = true;
            }
        }
        return b;
    }

    default boolean shouldChangeValue(final int n, final long n2, final long n3) {
        boolean b = false;
        if (n == 0) {
            b = b;
            if (n2 != n3) {
                b = true;
            }
        }
        return b;
    }
}
