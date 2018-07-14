package com.google.android.settings.gestures.assist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.android.settings.core.InstrumentedActivity;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.R;

public abstract class AssistGestureTrainingBase extends InstrumentedActivity implements AssistGestureHelper.GestureListener {
    protected AssistGestureHelper mAssistGestureHelper;
    private String mLaunchedFrom;

    protected boolean flowTypeAccidentalTrigger() {
        return "accidental_trigger".contentEquals(this.mLaunchedFrom);
    }

    protected boolean flowTypeDeferredSetup() {
        return "deferred_setup".contentEquals(this.mLaunchedFrom);
    }

    protected boolean flowTypeSettingsSuggestion() {
        return "settings_suggestion".contentEquals(this.mLaunchedFrom);
    }

    @Override
    public abstract int getMetricsCategory();

    protected void handleDoneAndLaunch() {
        this.setResult(-1);
        this.mAssistGestureHelper.setListener(null);
        this.mAssistGestureHelper.launchAssistant();
        this.finishAndRemoveTask();
    }

    protected void launchAssistGestureSettings() {
        this.startActivity(new Intent("android.settings.ASSIST_GESTURE_SETTINGS"));
    }

    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        (this.mAssistGestureHelper = new AssistGestureHelper(this.getApplicationContext()))
                .setListener(this);
        this.mLaunchedFrom = this.getIntent().getStringExtra("launched_from");
    }

    @Override
    public abstract void onGestureDetected();

    @Override
    public abstract void onGestureProgress(final float p0, final int p1);

    public void onPause() {
        super.onPause();
        this.mAssistGestureHelper.setListener(null);
        this.mAssistGestureHelper.unbindFromElmyraServiceProxy();
    }

    public void onResume() {
        super.onResume();
        Settings.Secure.putInt(this.getContentResolver(), "assist_gesture_enabled", 1);
        boolean b;
        b = Settings.Secure.getInt(this.getContentResolver(), "assist_gesture_enabled", 1) != 0;
        if (!FeatureFactory.getFactory(this).getAssistGestureFeatureProvider().isSupported(this)) {
            this.setResult(1);
            this.finishAndRemoveTask();
            return;
        } else if ((!b)) {
            Log.e("AssistGestureTrainingBase", "Unable to start activity ");
            this.setResult(1);
            this.finishAndRemoveTask();
            return;
        }
        this.mAssistGestureHelper.bindToElmyraServiceProxy();
        this.mAssistGestureHelper.setListener(this);
    }

    protected class HandleProgress {
        private final Handler mHandler;
        private boolean mErrorSqueezeBottomShown;
        private int mLastStage;
        private boolean mShouldCheckForNoProgress;

        public HandleProgress(final Handler mHandler) {
            this.mHandler = mHandler;
            this.mShouldCheckForNoProgress = true;
        }

        private boolean checkSqueezeNoProgress(final int n) {
            return this.mLastStage == 1 && n == 0;
        }

        private boolean checkSqueezeTooLong(final int n) {
            return this.mLastStage == 2 && n == 0;
        }

        public void onGestureDetected() {
            this.mLastStage = 0;
        }

        public void onGestureProgress(final float n, int mLastStage) {
            int n2 = 0;
            if (this.mLastStage != mLastStage) {
                if (this.mShouldCheckForNoProgress && this.checkSqueezeNoProgress(mLastStage)) {
                    n2 = 1;
                } else if (this.checkSqueezeTooLong(mLastStage)) {
                    n2 = 2;
                }
                this.mLastStage = mLastStage;
                if (n2 != 0) {
                    if ((mLastStage = n2) == 1) {
                        if (this.mErrorSqueezeBottomShown) {
                            n2 = 4;
                        }
                        this.mErrorSqueezeBottomShown = true;
                        mLastStage = n2;
                    }
                    this.mHandler.obtainMessage(2, mLastStage, 0).sendToTarget();
                }
            }
        }

        public void setShouldCheckForNoProgress(final boolean mShouldCheckForNoProgress) {
            this.mShouldCheckForNoProgress = mShouldCheckForNoProgress;
        }
    }
}

