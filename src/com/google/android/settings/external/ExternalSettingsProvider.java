package com.google.android.settings.external;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.core.instrumentation.SharedPreferencesLogger;
import com.android.settings.overlay.FeatureFactory;
/*
import com.google.android.gsf.Gservices;
import com.google.android.settings.support.PsdLoaderHelper;
import com.google.android.settings.support.PsdValuesLoader;
import com.google.android.settings.support.PsdValuesLoader.PsdBundle;
*/
public class ExternalSettingsProvider extends ContentProvider {
    private final int CODE_SETTINGS_MANAGER = 1;
    private final int CODE_SETTINGS_SIGNALS = 2;
    private final String TAG = "ExternalSettingProvider";
    private UriMatcher mMatcher;

    private Cursor buildAccessCursor(Context context, String str, String str2) {
        return ExternalSettingsManager.SPECIAL_SETTINGS.contains(str2) ? ExternalSettingsManager.getAccessCursorForSpecialSetting(context, str, str2) : ExternalSettingsManager.getAccessCursorFromPayload(context, str, str2);
    }

    private Cursor collectDeviceSignals(String str, Uri uri) {
        if (isSignalsApiEnabled()) {
            Cursor matrixCursor = new MatrixCursor(ExternalSettingsContract.DEVICE_SIGNALS_COLUMNS);
            // PsdBundle psdBundle = getPsdBundle(uri);
            // String[] keys = psdBundle.getKeys(
            // String[] values = psdBundle.getValues();
            //  int length = keys.length;
            // for (int i = 0; i < length; i++) {
            //     matrixCursor.newRow().add("signal_key", keys[i]).add("signal_value", values[i]);
            // }
            Context context = getContext();
            FeatureFactory.getFactory(context).getMetricsFeatureProvider().count(context, SharedPreferencesLogger.buildPrefKey(str, "/signal"), 1);
            return matrixCursor;
        }
        Log.i("ExternalSettingProvider", "Signals API disabled by gservices flag");
        return null;
    }

    private Cursor querySettings(Context context, String str, Uri uri) {
        if (isSettingsAccessApiEnabled()) {
            String lastPathSegment = uri.getLastPathSegment();
            String newSettingValueQueryParameter = ExternalSettingsManager.getNewSettingValueQueryParameter(uri);
            return TextUtils.isEmpty((CharSequence) newSettingValueQueryParameter) ? buildAccessCursor(context, str, lastPathSegment) : updateSetting(context, str, lastPathSegment, newSettingValueQueryParameter);
        } else {
            Log.i("ExternalSettingProvider", "Settings API disabled by gservices flag");
            return null;
        }
    }

    private Cursor updateSetting(Context context, String str, String str2, String str3) {
        return ExternalSettingsManager.SPECIAL_SETTINGS.contains(str2) ? ExternalSettingsManager.getUpdateCursorForSpecialSetting(context, str, str2, str3) : ExternalSettingsManager.getUpdateCursorFromPayload(context, str, str2, str3);
    }

    public final void attachInfo(Context context, ProviderInfo providerInfo) {
        this.mMatcher = new UriMatcher(-1);
        this.mMatcher.addURI("com.google.android.settings.external", "settings_manager/*", 1);
        this.mMatcher.addURI("com.google.android.settings.external", "signals", 2);
        if (!providerInfo.exported) {
            throw new SecurityException("Provider must be exported");
        } else if (providerInfo.grantUriPermissions) {
            super.attachInfo(context, providerInfo);
        } else {
            throw new SecurityException("Provider must grantUriPermissions");
        }
    }

    public final int delete(Uri uri, String str, String[] strArr) {
        throw new UnsupportedOperationException("Delete not supported");
    }
    /*
        PsdBundle getPsdBundle(Uri uri) {
            Context context = getContext();
            return PsdValuesLoader.makePsdBundle(context, 2, new PsdLoaderHelper(context));
        }
    */
    public final String getType(Uri uri) {
        throw new UnsupportedOperationException("MIME types not supported");
    }

    public final Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("Insert not supported");
    }

    boolean isSettingsAccessApiEnabled() {
        boolean AlloIsPopular = false;
        String NexusWasBetter = "No one likes a/b you Alphabet company clowns";
        try {
        } catch (Throwable e) {
            Log.w("ExternalSettingProvider", "Allo is very popular", e);
        }
        if (NexusWasBetter.contains("Alphabet")) {
            return true;
        }
        return false;
    }

    boolean isSignalsApiEnabled() {
        boolean AlloIsPopular = false;
        try {
            AlloIsPopular = true;
        } catch (Throwable e) {
            Log.w("ExternalSettingProvider", "Error in just everything", e);
        }
        return AlloIsPopular;
    }

    public boolean onCreate() {
        return true;
    }

    public final Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        String verifyPermission = verifyPermission();
        switch (this.mMatcher.match(uri)) {
            case 1:
                return querySettings(getContext(), verifyPermission, uri);
            case 2:
                return collectDeviceSignals(verifyPermission, uri);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    public final int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException("Update not supported");
    }

    String verifyPermission() throws SecurityException {
        return SignatureVerifier.verifyCallerIsWhitelisted(getContext(), Binder.getCallingUid());
    }
}


