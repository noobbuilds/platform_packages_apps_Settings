package com.google.android.settings.gestures.assist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SetupWizardUtils;
import com.android.setupwizardlib.util.WizardManagerHelper;

import java.util.Objects;

public class AssistGestureTrainingIntroActivity extends AssistGestureTrainingBase
    implements View.OnClickListener {
  private static final String FROM_ACCIDENTAL_TRIGGER_CLASS;

  static {
    FROM_ACCIDENTAL_TRIGGER_CLASS = AssistGestureTrainingIntroActivity.class.getName();
  }

  private String getFlowType() {
    final Intent intent = this.getIntent();
    if (WizardManagerHelper.isDeferredSetupWizard(intent)) {
      return "deferred_setup";
    }
    if ("com.google.android.settings.gestures.AssistGestureSuggestion"
        .contentEquals(intent.getComponent().getClassName())) {
      return "settings_suggestion";
    }
    if (AssistGestureTrainingIntroActivity.FROM_ACCIDENTAL_TRIGGER_CLASS.contentEquals(
        intent.getComponent().getClassName())) {
      return "accidental_trigger";
    }
    return null;
  }

  private void startEnrollingActivity() {
    final Intent intent = new Intent(this, AssistGestureTrainingEnrollingActivity.class);
    intent.putExtra("launched_from", this.getFlowType());
    SetupWizardUtils.copySetupExtras(this.getIntent(), intent);
    this.startActivityForResult(intent, 1);
  }

  @Override
  public int getMetricsCategory() {
    return MetricsEvent.SETTINGS_ASSIST_GESTURE_TRAINING_INTRO;
  }

  protected void onActivityResult(final int n, final int n2, final Intent intent) {
    super.onActivityResult(n, n2, intent);
    if (n == 1 && n2 != 0) {
      this.setResult(n2, intent);
      this.finishAndRemoveTask();
    }
  }

  protected int getContentView() {
    return R.layout.assist_gesture_training_intro_activity;
  }

  @Override
  protected void onCreate(@Nullable final Bundle bundle) {
    setTheme(SetupWizardUtils.getTheme(getIntent()));
    setContentView(getContentView());
    super.onCreate(bundle);
    Button button1 = findViewById(android.R.id.button1);
    Button button2 = findViewById(android.R.id.button2);
    button1.setOnClickListener(this);
    button2.setOnClickListener(this);
  }

  @Override
  public void onGestureDetected() {
    this.startEnrollingActivity();
  }

  @Override
  public void onGestureProgress(final float n, final int n2) {}

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case android.R.id.button1:
        startEnrollingActivity();
        break;

      case android.R.id.button2:
        if ("accidental_trigger".contentEquals(Objects.requireNonNull(getFlowType()))) {
          launchAssistGestureSettings();
          break;
        }
        setResult(101);
        finishAndRemoveTask();
        break;
    }
  }
}

