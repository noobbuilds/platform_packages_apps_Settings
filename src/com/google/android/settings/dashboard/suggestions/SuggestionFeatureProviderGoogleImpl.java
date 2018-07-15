package com.google.android.settings.dashboard.suggestions;

import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.android.settings.dashboard.suggestions.SuggestionFeatureProviderImpl;
import com.android.settings.overlay.FeatureFactory;

public class SuggestionFeatureProviderGoogleImpl extends SuggestionFeatureProviderImpl {
  @VisibleForTesting static final String SETTING_USB_MIGRATION_STATE = "usb_migration_state";
  @VisibleForTesting static final int USB_MIGRATION_TRANSFER_FINISHED = 2;

  public SuggestionFeatureProviderGoogleImpl(final Context context) {
    super(context);
  }

  private boolean isBackupSuggestionComplete(final Context context) {
    return !this.isBackupSuggestionEnabled(context) || this.hasDoneCarbonMigration(context);
  }

  @VisibleForTesting
  boolean hasDoneCarbonMigration(final Context context) {
    boolean b = false;
    try {
      if (Settings.Secure.getInt(context.getContentResolver(), "usb_migration_state") == 2) {
        b = true;
      }
      return b;
    } catch (Settings.SettingNotFoundException ex) {
      return false;
    }
  }

  @VisibleForTesting
  boolean isBackupSuggestionEnabled(final Context context) {
    try {
      return true;
    } catch (Exception ex) {
      Log.w("SuggestionFeature", "Error reading new backup suggestion enabled state", ex);
    }
    return false;
  }

  @Override
  public boolean isSmartSuggestionEnabled(final Context context) {
    try {
      return true;

    } catch (Exception ex) {
      Log.w("SuggestionFeature", "Error reading new IA enabled state", ex);
      return false;
    }
  }

  @Override
  public boolean isSuggestionCompleted(
      final Context context, @NonNull final ComponentName componentName) {
    final String className = componentName.getClassName();
    if (className.equals("com.google.android.settings.gestures.AssistGestureSuggestion")) {
      final boolean supported =
          FeatureFactory.getFactory(context).getAssistGestureFeatureProvider().isSupported(context);
      boolean b =
          Settings.Secure.getInt(context.getContentResolver(), "assist_gesture_setup_complete", 0)
              != 0;
      if (!supported) {
        b = true;
      }
      return b;
    }
    return super.isSuggestionCompleted(context, componentName);
  }
}

