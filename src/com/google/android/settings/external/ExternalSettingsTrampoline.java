package com.google.android.settings.external;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import com.android.settings.SubSettings;

public class ExternalSettingsTrampoline extends Activity {
    private final String TAG = "TrampolineActivity";

    @VisibleForTesting
    Intent buildSubSettingIntent() {
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra(":settingsgoogle:fragment_args_key");
        Bundle bundle = new Bundle();
        bundle.putString(":settings:fragment_args_key", stringExtra);
        intent.putExtra(":settings:show_fragment_args", bundle);
        intent.setClass(this, SubSettings.class).addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        return intent;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ComponentName callingActivity = getCallingActivity();
        if (callingActivity == null) {
            throw new IllegalStateException("ExternalSettingsTrampoline intents must be called with startActivityForResult");
        } else if (SignatureVerifier.isPackageWhitelisted(this, callingActivity.getPackageName())) {
            startActivity(buildSubSettingIntent());
            finish();
        } else {
            throw new SecurityException("ExternalSettingsTrampoline intents must be called with from a whitelisted package.");
        }
    }
}
