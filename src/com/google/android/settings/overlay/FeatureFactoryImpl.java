package com.google.android.settings.overlay;

import android.app.AppGlobals;
import android.app.admin.DevicePolicyManager;
import android.content.Context;

import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.applications.IPackageManagerWrapperImpl;
import com.android.settings.applications.PackageManagerWrapperImpl;
import com.android.settings.bluetooth.BluetoothFeatureProvider;
import com.android.settings.connecteddevice.SmsMirroringFeatureProvider;
import com.android.settings.dashboard.DashboardFeatureProvider;
import com.android.settings.dashboard.suggestions.SuggestionFeatureProvider;
import com.android.settings.enterprise.DevicePolicyManagerWrapperImpl;
import com.android.settings.fuelgauge.PowerUsageFeatureProvider;
import com.android.settings.gestures.AssistGestureFeatureProvider;
import com.android.settings.overlay.SupportFeatureProvider;
import com.android.settings.overlay.SurveyFeatureProvider;
import com.android.settings.search.SearchFeatureProvider;
import com.google.android.settings.applications.ApplicationFeatureProviderGoogleImpl;
import com.google.android.settings.dashboard.DashboardFeatureProviderGoogleImpl;
import com.google.android.settings.dashboard.suggestions.SuggestionFeatureProviderGoogleImpl;
import com.google.android.settings.gestures.assist.AssistGestureFeatureProviderGoogleImpl;
import com.google.android.settings.search.SearchFeatureProviderGoogleImpl;

public final class FeatureFactoryImpl extends com.android.settings.overlay.FeatureFactoryImpl {
  private ApplicationFeatureProvider mApplicationFeatureProvider;
  private AssistGestureFeatureProvider mAssistGestureFeatureProvider;
  private BluetoothFeatureProvider mBluetoothFeatureProvider;
  private DashboardFeatureProvider mDashboardFeatureProvider;
  private PowerUsageFeatureProvider mPowerUsageProvider;
  private SearchFeatureProvider mSearchFeatureProvider;
  private SmsMirroringFeatureProvider mSmsMirroringFeatureProvider;
  private SuggestionFeatureProvider mSuggestionFeatureProvider;
  private SupportFeatureProvider mSupportProvider;
  private SurveyFeatureProvider mSurveyFeatureProvider;

  @Override
  public ApplicationFeatureProvider getApplicationFeatureProvider(final Context context) {
    if (this.mApplicationFeatureProvider == null) {
      this.mApplicationFeatureProvider =
          new ApplicationFeatureProviderGoogleImpl(
              context,
              new PackageManagerWrapperImpl(context.getPackageManager()),
              new IPackageManagerWrapperImpl(AppGlobals.getPackageManager()),
              new DevicePolicyManagerWrapperImpl(
                  (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE)));
    }
    return this.mApplicationFeatureProvider;
  }

  @Override
  public AssistGestureFeatureProvider getAssistGestureFeatureProvider() {
    if (this.mAssistGestureFeatureProvider == null) {
      this.mAssistGestureFeatureProvider = new AssistGestureFeatureProviderGoogleImpl();
    }
    return this.mAssistGestureFeatureProvider;
  }

  @Override
  public DashboardFeatureProvider getDashboardFeatureProvider(final Context context) {
    if (this.mDashboardFeatureProvider == null) {
      this.mDashboardFeatureProvider = new DashboardFeatureProviderGoogleImpl(context);
    }
    return this.mDashboardFeatureProvider;
  }

  @Override
  public SearchFeatureProvider getSearchFeatureProvider() {
    if (this.mSearchFeatureProvider == null) {
      this.mSearchFeatureProvider = new SearchFeatureProviderGoogleImpl();
    }
    return this.mSearchFeatureProvider;
  }

  @Override
  public SuggestionFeatureProvider getSuggestionFeatureProvider(final Context context) {
    if (this.mSuggestionFeatureProvider == null) {
      this.mSuggestionFeatureProvider = new SuggestionFeatureProviderGoogleImpl(context);
    }
    return this.mSuggestionFeatureProvider;
  }
}

