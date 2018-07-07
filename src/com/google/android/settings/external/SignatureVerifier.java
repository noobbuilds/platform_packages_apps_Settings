package com.google.android.settings.external;

import android.text.TextUtils;
import com.android.internal.util.ArrayUtils;
import android.os.Build;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Context;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import android.util.Log;
import java.security.MessageDigest;
import java.util.Arrays;

public class SignatureVerifier {
    private static final byte[] debugDigestBytes = new byte[]{(byte) 25, (byte) 117, (byte) -78, (byte) -15, (byte) 113, (byte) 119, (byte) -68, (byte) -119, (byte) -91, (byte) -33, (byte) -13, (byte) 31, (byte) -98, (byte) 100, (byte) -90, (byte) -54, (byte) -30, (byte) -127, (byte) -91, (byte) 61, (byte) -63, (byte) -47, (byte) -43, (byte) -101, (byte) 29, (byte) 20, Byte.MAX_VALUE, (byte) -31, (byte) -56, (byte) 42, (byte) -6, (byte) 0};
    private static final byte[] releaseDigestBytes = new byte[]{(byte) -16, (byte) -3, (byte) 108, (byte) 91, (byte) 65, (byte) 15, (byte) 37, (byte) -53, (byte) 37, (byte) -61, (byte) -75, (byte) 51, (byte) 70, (byte) -56, (byte) -105, (byte) 47, (byte) -82, (byte) 48, (byte) -8, (byte) -18, (byte) 116, (byte) 17, (byte) -33, (byte) -111, (byte) 4, Byte.MIN_VALUE, (byte) -83, (byte) 107, (byte) 45, (byte) 96, (byte) -37, (byte) -125};

    private static boolean isCertWhitelisted(byte[] bArr, boolean z) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(bArr);
            if (Log.isLoggable("SignatureVerifier", 3)) {
                Log.d("SignatureVerifier", "Checking cert for " + (z ? "debug" : "release"));
            }
            return z ? Arrays.equals(digest, debugDigestBytes) : Arrays.equals(digest, releaseDigestBytes);
        } catch (Throwable e) {
            throw new SecurityException("Failed to obtain SHA-256 digest impl.", e);
        }
    }

    public static boolean isPackageWhitelisted(Context context, String str) {
        boolean z = false;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(str, 64);
            String str2 = packageInfo.packageName;
            if (verifyWhitelistedPackage(str2)) {
                z = isSignatureWhitelisted(packageInfo);
            } else {
                Log.e("SignatureVerifier", "Package name: " + str2 + " is not whitelisted.");
            }
        } catch (Throwable e) {
            Log.e("SignatureVerifier", "Could not find package name.", e);
        }
        return z;
    }

    private static boolean isSignatureWhitelisted(PackageInfo packageInfo) {
        if (packageInfo.signatures.length == 1) {
            return isCertWhitelisted(packageInfo.signatures[0].toByteArray(), Build.IS_DEBUGGABLE);
        }
        Log.w("SignatureVerifier", "Package has more than one signature.");
        return false;
    }

    private static String isUidWhitelisted(Context context, int i) {
        String[] packagesForUid = context.getPackageManager().getPackagesForUid(i);
        if (ArrayUtils.isEmpty(packagesForUid)) {
            return null;
        }
        for (String str : packagesForUid) {
            if (isPackageWhitelisted(context, str)) {
                return str;
            }
        }
        return null;
    }

    public static String verifyCallerIsWhitelisted(Context context, int i) throws SecurityException {
        Object isUidWhitelisted = isUidWhitelisted(context, i);
        if (!TextUtils.isEmpty((CharSequence) isUidWhitelisted)) {
            return (String) isUidWhitelisted;
        }
        throw new SecurityException("UID is not Google Signed");
    }

    private static boolean verifyWhitelistedPackage(String str) {
        return ("com.google.android.googlequicksearchbox".equals(str) || "com.google.android.gms".equals(str)) ? true : Build.IS_DEBUGGABLE ? "com.google.android.settings.api.tester".equals(str) : false;
    }
}

