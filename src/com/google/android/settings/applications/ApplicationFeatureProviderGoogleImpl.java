package com.google.android.settings.applications;

import android.content.Context;
import com.android.settings.applications.ApplicationFeatureProviderImpl;
import com.android.settings.applications.IPackageManagerWrapper;
import com.android.settings.applications.PackageManagerWrapper;
import com.android.settings.enterprise.DevicePolicyManagerWrapper;
import java.util.Set;

public class ApplicationFeatureProviderGoogleImpl extends ApplicationFeatureProviderImpl {
    public ApplicationFeatureProviderGoogleImpl(Context context, PackageManagerWrapper packageManagerWrapper, IPackageManagerWrapper iPackageManagerWrapper, DevicePolicyManagerWrapper devicePolicyManagerWrapper) {
        super(context, packageManagerWrapper, iPackageManagerWrapper, devicePolicyManagerWrapper);
    }

    public Set<String> getKeepEnabledPackages() {
        Set<String> keepEnabledPackages = super.getKeepEnabledPackages();
        keepEnabledPackages.add("com.google.android.inputmethod.latin");
        return keepEnabledPackages;
    }
}

