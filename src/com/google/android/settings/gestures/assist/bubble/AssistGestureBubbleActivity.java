package com.google.android.settings.gestures.assist.bubble;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.settings.gestures.assist.AssistGestureHelper;
import com.google.android.settings.gestures.assist.AssistGestureHelper.GestureListener;
import com.google.android.settings.gestures.assist.bubble.AssistGestureGameDrawable.GameStateListener;

public class AssistGestureBubbleActivity extends Activity {
    private AssistGestureHelper mAssistGestureHelper;
    private TextView mCurrentScoreTextView;
    private AssistGestureGameDrawable mEasterEggDrawable;
    private GameStateListener mEasterEggListener = new C10331();
    private AssistGesturePlayButtonDrawable mEasterEggPlayDrawable;
    private int mGameState;
    private ImageView mGameView;
    private GestureListener mGestureListener = new C10342();
    private Handler mHandler;
    private boolean mIsNavigationHidden;
    private ImageView mPlayView;
    private boolean mShouldStartNewGame = true;

    class C10331 implements GameStateListener {
        C10331() {
        }

        public void gameStateChanged(int i) {
            AssistGestureBubbleActivity.this.mGameState = i;
            if (i == 4) {
                AssistGestureBubbleActivity.this.pauseGame();
                AssistGestureBubbleActivity.this.mShouldStartNewGame = true;
            }
        }

        public void updateScoreText(String str) {
            AssistGestureBubbleActivity.this.mCurrentScoreTextView.setText(str);
        }
    }

    class C10342 implements GestureListener {
        C10342() {
        }

        /* synthetic */ void m103x3294858e() {
            AssistGestureBubbleActivity.this.startGame(AssistGestureBubbleActivity.this.mShouldStartNewGame);
        }

        public void onGestureDetected() {
            AssistGestureBubbleActivity.this.mAssistGestureHelper.setListener(null);
            AssistGestureBubbleActivity.this.mAssistGestureHelper.unbindFromElmyraServiceProxy();
        }

        public void onGestureProgress(float f, int i) {
        }
    }

    class C10353 implements OnSystemUiVisibilityChangeListener {
        C10353() {
        }

        public void onSystemUiVisibilityChange(int i) {
            if ((i & 4) == 0) {
                AssistGestureBubbleActivity.this.mIsNavigationHidden = false;
            } else {
                AssistGestureBubbleActivity.this.mIsNavigationHidden = true;
            }
            AssistGestureBubbleActivity.this.updateGameState();
        }
    }

    class C10364 implements OnTouchListener {
        boolean mTouching;

        C10364() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getActionMasked()) {
                case 0:
                    if (!AssistGestureBubbleActivity.this.mEasterEggPlayDrawable.hitTest(motionEvent.getX(), motionEvent.getY())) {
                        this.mTouching = false;
                        break;
                    }
                    this.mTouching = true;
                    break;
                case 1:
                    if (this.mTouching) {
                        AssistGestureBubbleActivity.this.mPlayView.setVisibility(4);
                        AssistGestureBubbleActivity.this.enterFullScreen();
                        AssistGestureBubbleActivity.this.startGame(AssistGestureBubbleActivity.this.mShouldStartNewGame);
                        this.mTouching = false;
                        break;
                    }
                    break;
                case 3:
                    this.mTouching = false;
                    break;
            }
            return true;
        }
    }

    private void enterFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(3846);
    }

    private void pauseGame() {
        if (this.mPlayView.getVisibility() == 4) {
            this.mPlayView.setVisibility(0);
        }
        this.mEasterEggDrawable.pauseGame();
        this.mAssistGestureHelper.bindToElmyraServiceProxy();
        this.mAssistGestureHelper.setListener(this.mGestureListener);
    }

    private void registerDecorViewListener() {
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new C10353());
    }

    private void unregisterDecorViewListener() {
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(null);
    }

    private void updateGameState() {
        if (this.mPlayView.getVisibility() == 4 && this.mIsNavigationHidden) {
            startGame(this.mShouldStartNewGame);
        } else {
            pauseGame();
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(2131558447);
        getWindow().setBackgroundDrawableResource(2131230832);
        this.mHandler = new Handler(getMainLooper());
        this.mAssistGestureHelper = new AssistGestureHelper(getApplicationContext());
        this.mCurrentScoreTextView = (TextView) findViewById(2131361998);
        this.mGameView = (ImageView) findViewById(2131362121);
        this.mEasterEggDrawable = new AssistGestureGameDrawable(getApplicationContext(), this.mEasterEggListener);
        this.mGameView.setImageDrawable(this.mEasterEggDrawable);
        this.mPlayView = (ImageView) findViewById(2131362358);
        this.mEasterEggPlayDrawable = new AssistGesturePlayButtonDrawable();
        this.mEasterEggPlayDrawable.setAlpha(200);
        this.mPlayView.setImageDrawable(this.mEasterEggPlayDrawable);
        this.mPlayView.setOnTouchListener(new C10364());
    }

    public void onPause() {
        super.onPause();
        this.mEasterEggDrawable.pauseGame();
        unregisterDecorViewListener();
        this.mAssistGestureHelper.setListener(null);
        this.mAssistGestureHelper.unbindFromElmyraServiceProxy();
    }

    public void onResume() {
        super.onResume();
        registerDecorViewListener();
        enterFullScreen();
    }

    public void startGame(boolean z) {
        enterFullScreen();
        if (this.mPlayView.getVisibility() == 0) {
            this.mPlayView.setVisibility(4);
        }
        this.mEasterEggDrawable.startGame(z);
        this.mShouldStartNewGame = false;
    }
}
