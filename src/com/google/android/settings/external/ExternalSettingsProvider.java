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
import com.google.android.settings.support.PsdLoaderHelper;
import com.google.android.settings.support.PsdValuesLoader;

public class ExternalSettingsProvider extends ContentProvider
{
    private final int CODE_SETTINGS_MANAGER;
    private final int CODE_SETTINGS_SIGNALS;
    private final String TAG;
    private UriMatcher mMatcher;
    
    public ExternalSettingsProvider() {
        this.TAG = "ExternalSettingProvider";
        this.CODE_SETTINGS_MANAGER = 1;
        this.CODE_SETTINGS_SIGNALS = 2;
    }
    
    private Cursor buildAccessCursor(final Context context, final String s, final String s2) {
        if (ExternalSettingsManager.SPECIAL_SETTINGS.contains(s2)) {
            return ExternalSettingsManager.getAccessCursorForSpecialSetting(context, s, s2);
        }
        return ExternalSettingsManager.getAccessCursorFromPayload(context, s, s2);
    }
    
    private Cursor collectDeviceSignals(String buildPrefKey, final Uri uri) {
        if (!this.isSignalsApiEnabled()) {
            Log.i("ExternalSettingProvider", "Signals API disabled by gservices flag");
            return null;
        }
        final MatrixCursor matrixCursor = new MatrixCursor(ExternalSettingsContract.DEVICE_SIGNALS_COLUMNS);
        final PsdValuesLoader.PsdBundle psdBundle = this.getPsdBundle(uri);
        final String[] keys = psdBundle.getKeys();
        final String[] values = psdBundle.getValues();
        for (int i = 0; i < keys.length; ++i) {
            matrixCursor.newRow().add("signal_key", (Object)keys[i]).add("signal_value", (Object)values[i]);
        }
        final Context context = this.getContext();
        buildPrefKey = SharedPreferencesLogger.buildPrefKey(buildPrefKey, "/signal");
        FeatureFactory.getFactory(context).getMetricsFeatureProvider().count(context, buildPrefKey, 1);
        return (Cursor)matrixCursor;
    }
    
    private Cursor querySettings(final Context context, final String s, final Uri uri) {
        if (!this.isSettingsAccessApiEnabled()) {
            Log.i("ExternalSettingProvider", "Settings API disabled by gservices flag");
            return null;
        }
        final String lastPathSegment = uri.getLastPathSegment();
        final String newSettingValueQueryParameter = ExternalSettingsManager.getNewSettingValueQueryParameter(uri);
        if (TextUtils.isEmpty((CharSequence)newSettingValueQueryParameter)) {
            return this.buildAccessCursor(context, s, lastPathSegment);
        }
        return this.updateSetting(context, s, lastPathSegment, newSettingValueQueryParameter);
    }
    
    private Cursor updateSetting(final Context context, final String s, final String s2, final String s3) {
        if (ExternalSettingsManager.SPECIAL_SETTINGS.contains(s2)) {
            return ExternalSettingsManager.getUpdateCursorForSpecialSetting(context, s, s2, s3);
        }
        return ExternalSettingsManager.getUpdateCursorFromPayload(context, s, s2, s3);
    }
    
    public final void attachInfo(final Context context, final ProviderInfo providerInfo) {
        (this.mMatcher = new UriMatcher(-1)).addURI("com.google.android.settings.external", "settings_manager/*", 1);
        this.mMatcher.addURI("com.google.android.settings.external", "signals", 2);
        if (!providerInfo.exported) {
            throw new SecurityException("Provider must be exported");
        }
        if (!providerInfo.grantUriPermissions) {
            throw new SecurityException("Provider must grantUriPermissions");
        }
        super.attachInfo(context, providerInfo);
    }
    
    public final int delete(final Uri uri, final String s, final String[] array) {
        throw new UnsupportedOperationException("Delete not supported");
    }
    
    PsdValuesLoader.PsdBundle getPsdBundle(final Uri uri) {
        final Context context = this.getContext();
        return PsdValuesLoader.makePsdBundle(context, 2, new PsdLoaderHelper(context));
    }
    
    public final String getType(final Uri uri) {
        throw new UnsupportedOperationException("MIME types not supported");
    }
    
    public final Uri insert(final Uri uri, final ContentValues contentValues) {
        throw new UnsupportedOperationException("Insert not supported");
    }
    
    boolean isSettingsAccessApiEnabled() {
        try {
            return true; //Gservices.getBoolean(this.getContext().getContentResolver(), "settingsgoogle:use_settings_api", false);
        }
        catch (Exception ex) {
            Log.w("ExternalSettingProvider", "Error reading settings access api enabled flag", (Throwable)ex);
            return false;
        }
    }
    
    boolean isSignalsApiEnabled() {
        try {
            return true; // Gservices.getBoolean(this.getContext().getContentResolver(), "settingsgoogle:use_psd_api", false);
        }
        catch (Exception ex) {
            Log.w("ExternalSettingProvider", "Error reading psd api enabled flag", (Throwable)ex);
            return false;
        }
    }
    
    public boolean onCreate() {
        return true;
    }
    
    public final Cursor query(final Uri uri, final String[] array, final String s, final String[] array2, final String s2) {
        final String verifyPermission = this.verifyPermission();
        switch (this.mMatcher.match(uri)) {
            default: {
                throw new IllegalArgumentException("Unknown Uri: " + uri);
            }
            case 1: {
                return this.querySettings(this.getContext(), verifyPermission, uri);
            }
            case 2: {
                return this.collectDeviceSignals(verifyPermission, uri);
            }
        }
    }
    
    public final int update(final Uri uri, final ContentValues contentValues, final String s, final String[] array) {
        throw new UnsupportedOperationException("Update not supported");
    }
    
    String verifyPermission() throws SecurityException {
        return SignatureVerifier.verifyCallerIsWhitelisted(this.getContext(), Binder.getCallingUid());
    }
}
