package com.google.android.settings.gestures.assist;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import com.android.settings.R;

import com.android.settings.SetupWizardUtils;

public class AssistGestureTrainingFinishedActivity extends AssistGestureTrainingSliderBase {
    private View mAssistGestureCheck;
    private View mAssistGestureIllustration;
    private Handler mHandler;
    private void fadeOutCheckAfterDelay() {
        mHandler.sendMessageDelayed(mHandler.obtainMessage(4, mAssistGestureIllustration), 1000);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(5, mAssistGestureCheck), 1000);
    }

    public int getMetricsCategory() {
        return 993;
    }

    protected void handleGestureDetected() {
        mErrorView.setVisibility(View.INVISIBLE);
        mAssistGestureCheck.animate().cancel();
        mAssistGestureCheck.setAlpha(1.0f);
        mAssistGestureCheck.setVisibility(0);
        mAssistGestureIllustration.animate().cancel();
        mAssistGestureIllustration.setAlpha(0.0f);
        mAssistGestureIllustration.setVisibility(View.INVISIBLE);
        mHandler.removeMessages(4);
        mHandler.removeMessages(5);
        fadeOutCheckAfterDelay();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case 16908314:
                launchAssistGestureSettings();
                return;
            case 16908313:
                if (flowTypeDeferredSetup() || flowTypeSettingsSuggestion()) {
                    setResult(-1);
                    mAssistGestureHelper.setListener(null);
                    finishAndRemoveTask();
                    return;
                } else if (flowTypeAccidentalTrigger()) {
                    handleDoneAndLaunch();
                    return;
                } else {
                    return;
                }
            default:
                return;
        }
    }

    protected int getContentView() {
        return R.layout.assist_gesture_training_finished_activity;
    }

    protected void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(getIntent()));
        setContentView(getContentView());
        super.onCreate(bundle);
        setShouldCheckForNoProgress(false);
        Button button = this.findViewById(16908313);
        button.setOnClickListener((View.OnClickListener)this);
        if (flowTypeDeferredSetup()) {
            button.setText(R.string.next_label);
        } else if (flowTypeSettingsSuggestion()) {
            button.setText(R.string.done);
        } else if (flowTypeAccidentalTrigger()) {
            button.setText(R.string.assist_gesture_enrollment_continue_to_assistant);
        }
        button = (Button) findViewById(16908314);
        button.setOnClickListener(this);
        if (flowTypeDeferredSetup()) {
            button.setVisibility(View.INVISIBLE);
        }
        mAssistGestureCheck = findViewById(R.id.assist_gesture_training_check);
        mAssistGestureIllustration = findViewById(R.id.assist_gesture_training_illustration);
        fadeOutCheckAfterDelay();
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        super.onProgressChanged(seekBar, i, z);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        mHandler.removeMessages(4);
        mHandler.removeMessages(5);
        mHandler.obtainMessage(6, mAssistGestureCheck).sendToTarget();
        mHandler.obtainMessage(7, mAssistGestureIllustration).sendToTarget();
    }

    protected void showMessage(int i, String str) {
        if (mAssistGestureCheck.getVisibility() == View.INVISIBLE) {
            super.showMessage(i, str);
        }
    }
}

