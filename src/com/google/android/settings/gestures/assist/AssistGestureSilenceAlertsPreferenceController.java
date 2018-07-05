package com.google.android.settings.gestures.assist;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;

import com.android.settings.gestures.AssistGestureFeatureProvider;
import com.android.settings.gestures.GesturePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class AssistGestureSilenceAlertsPreferenceController extends GesturePreferenceController
{
    private final AssistGestureFeatureProvider mFeatureProvider;
    public AssistGestureSilenceAlertsPreferenceController(final Context context, final Lifecycle lifecycle) {
        super(context, lifecycle);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getAssistGestureFeatureProvider();
    }

    @Override
    public void displayPreference(final PreferenceScreen preferenceScreen) {
        if (((com.google.android.settings.gestures.assist.AssistGestureFeatureProviderGoogleImpl)this.mFeatureProvider).isDeskClockSupported(this.mContext)) {
            final Preference preference = preferenceScreen.findPreference("gesture_assist_silence");
            if (preference != null) {
        preference.setSummary("Squeeze for silence");
            }
        }
        super.displayPreference(preferenceScreen);
    }

    @Override
    public String getPreferenceKey() {
        return "gesture_assist_silence";
    }

    @Override
    protected String getVideoPrefKey() {
        return "gesture_assist_video";
    }

    @Override
    public boolean isAvailable() {
        return this.mFeatureProvider.isSensorAvailable(this.mContext);
    }

    @Override
    protected boolean isSwitchPrefEnabled() {
        boolean b = true;
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "assist_gesture_silence_alerts_enabled", 1) == 0) {
            b = false;
        }
        return b;
    }

    @Override
    public boolean onPreferenceChange(final Preference preference, final Object o) {
        final boolean booleanValue = (boolean)o;
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        int n;
        if (booleanValue) {
            n = 1;
        }
        else {
            n = 0;
        }
        Settings.Secure.putInt(contentResolver, "assist_gesture_silence_alerts_enabled", n);
        return true;
    }
}

