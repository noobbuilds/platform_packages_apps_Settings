package com.google.android.settings.gestures.assist;

import android.content.Context;
import android.provider.Settings.Secure;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.settings.gestures.AssistGestureFeatureProvider;
import com.android.settings.gestures.GesturePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class AssistGesturePreferenceController extends GesturePreferenceController implements LifecycleObserver, OnResume {
    static final int OFF = 0;
    static final int ON = 1;
    private final AssistGestureFeatureProvider mFeatureProvider;
    private Preference mPreference;
    private PreferenceScreen mScreen;

    public AssistGesturePreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getAssistGestureFeatureProvider();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    private void updatePreference() {
        if (this.mPreference != null && this.mScreen != null) {
            if (!isAvailable()) {
                removePreference(this.mScreen, getPreferenceKey());
            } else if (this.mScreen.findPreference(getPreferenceKey()) == null) {
                this.mScreen.addPreference(this.mPreference);
            }
        }
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mScreen = preferenceScreen;
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
        super.displayPreference(preferenceScreen);
    }

    public String getPreferenceKey() {
        return "gesture_assist";
    }

    protected String getVideoPrefKey() {
        return "gesture_assist_video";
    }

    public boolean isAvailable() {
        return this.mFeatureProvider.isSupported(this.mContext);
    }

    protected boolean isSwitchPrefEnabled() {
        return Secure.getInt(this.mContext.getContentResolver(), "assist_gesture_enabled", 1) != 0;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        Secure.putInt(this.mContext.getContentResolver(), "assist_gesture_enabled", ((Boolean) obj).booleanValue() ? 1 : 0);
        return true;
    }

    public void onResume() {
        updatePreference();
    }
}

