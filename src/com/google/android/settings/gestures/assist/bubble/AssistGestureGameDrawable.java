package com.google.android.settings.gestures.assist.bubble;

import android.animation.TimeAnimator;
import android.animation.TimeAnimator.TimeListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings.Secure;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import com.google.android.settings.gestures.assist.AssistGestureHelper;
import com.google.android.settings.gestures.assist.AssistGestureHelper.GestureListener;
import java.util.ArrayList;
import java.util.List;

public class AssistGestureGameDrawable extends Drawable {
    private AssistGestureHelper mAssistGestureHelper;
    private Rect mBounds;
    private boolean mBubbleShouldShrink = true;
    private boolean mBubbleTouchedBottom;
    private List<Bubble> mBubbles;
    private Context mContext;
    private List<Bubble> mDeadBubbles;
    private TimeAnimator mDriftAnimation;
    private VibrationEffect mErrorVibrationEffect;
    private int mGameState;
    private GameStateListener mGameStateListener;
    private GestureListener mGestureListener = new C10371();
    private int mKilledBubbles;
    private long mLastGestureTime;
    private float mLastProgress;
    private Bubble mLastShrunkBubble;
    private int mLastStage;
    private float mLastTime;
    private float mNextBubbleTime;
    private Paint mPaint;
    private boolean mServiceConnected;
    private List<SpiralingAndroid> mSpiralingAndroids;
    private int mTopKilledBubbles;
    private long mTopKilledBubblesDate;
    private Vibrator mVibrator;

    public interface GameStateListener {
        void gameStateChanged(int i);

        void updateScoreText(String str);
    }

    class C10371 implements GestureListener {
        C10371() {
        }

        public void onGestureDetected() {
            AssistGestureGameDrawable.this.onGestureDetected();
        }

        public void onGestureProgress(float f, int i) {
            AssistGestureGameDrawable.this.onGestureProgress(f, i);
        }
    }

    class C10382 implements TimeListener {
        C10382() {
        }

        public void onTimeUpdate(TimeAnimator timeAnimator, long j, long j2) {
            int size;
            Object obj = null;
            AssistGestureGameDrawable.this.mLastTime = ((float) j) * 0.001f;
            if (AssistGestureGameDrawable.this.mGameState == 3) {
                synchronized (this) {
                    if (AssistGestureGameDrawable.this.mLastTime > AssistGestureGameDrawable.this.mNextBubbleTime) {
                        AssistGestureGameDrawable.this.mBubbles.add(new Bubble(AssistGestureGameDrawable.this.mBounds));
                        AssistGestureGameDrawable.this.mNextBubbleTime = AssistGestureGameDrawable.this.mLastTime + 1.0f;
                    }
                    for (size = AssistGestureGameDrawable.this.mBubbles.size() - 1; size >= 0; size--) {
                        Bubble bubble = (Bubble) AssistGestureGameDrawable.this.mBubbles.get(size);
                        bubble.update(j, j2);
                        if (bubble.isBubbleDead()) {
                            AssistGestureGameDrawable.this.mBubbles.remove(size);
                        } else if (bubble.isBubbleTouchingTop() && bubble.getState() == 0) {
                            AssistGestureGameDrawable.this.mDeadBubbles.add(bubble);
                            AssistGestureGameDrawable.this.mBubbles.remove(size);
                        } else if (AssistGestureGameDrawable.this.hasCollisionWithDeadBubbles(bubble)) {
                            if (bubble.getPoint().y + ((float) bubble.getSize()) > ((float) AssistGestureGameDrawable.this.mBounds.bottom)) {
                                AssistGestureGameDrawable.this.mGameState = 4;
                                AssistGestureGameDrawable.this.mBubbleTouchedBottom = true;
                            }
                            if (bubble.getState() == 0) {
                                AssistGestureGameDrawable.this.mDeadBubbles.add(bubble);
                                AssistGestureGameDrawable.this.mBubbles.remove(size);
                            }
                        }
                    }
                }
            }
            if (AssistGestureGameDrawable.this.mGameState == 4) {
                synchronized (this) {
                    size = 0;
                    while (size < AssistGestureGameDrawable.this.mSpiralingAndroids.size()) {
                        Object obj2;
                        SpiralingAndroid spiralingAndroid = (SpiralingAndroid) AssistGestureGameDrawable.this.mSpiralingAndroids.get(size);
                        if (spiralingAndroid.getAndroid().getBounds().bottom < AssistGestureGameDrawable.this.mBounds.bottom) {
                            spiralingAndroid.update(j, j2);
                            obj2 = 1;
                        } else {
                            obj2 = obj;
                        }
                        size++;
                        obj = obj2;
                    }
                    if (AssistGestureGameDrawable.this.mServiceConnected) {
                        AssistGestureGameDrawable.this.disconnectService();
                    }
                    if (obj == null) {
                        AssistGestureGameDrawable.this.notifyGameStateChanged();
                        AssistGestureGameDrawable.this.mDriftAnimation.pause();
                    }
                }
            }
            AssistGestureGameDrawable.this.invalidateSelf();
        }
    }

    public AssistGestureGameDrawable(Context context, GameStateListener gameStateListener) {
        this.mContext = context;
        this.mAssistGestureHelper = new AssistGestureHelper(this.mContext);
        this.mGameStateListener = gameStateListener;
        this.mVibrator = (Vibrator) context.getSystemService(Vibrator.class);
        this.mErrorVibrationEffect = VibrationEffect.get(1);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mBubbles = new ArrayList();
        this.mDeadBubbles = new ArrayList();
        this.mSpiralingAndroids = new ArrayList();
        this.mTopKilledBubbles = Secure.getIntForUser(context.getContentResolver(), "assist_gesture_egg_top_score", 0, -2);
        this.mTopKilledBubblesDate = Secure.getLongForUser(context.getContentResolver(), "assist_gesture_egg_top_score_time", 0, -2);
        updateScoreText();
    }

    private void connectService() {
        this.mAssistGestureHelper.bindToElmyraServiceProxy();
        this.mAssistGestureHelper.setListener(this.mGestureListener);
        this.mServiceConnected = true;
    }

    private double distance(Bubble bubble, Bubble bubble2) {
        PointF point = bubble.getPoint();
        PointF point2 = bubble2.getPoint();
        return Math.sqrt(Math.pow((double) (point2.y - point.y), 2.0d) + Math.pow((double) (point2.x - point.x), 2.0d));
    }

    private boolean hasCollisionWithDeadBubbles(Bubble bubble) {
        for (int i = 0; i < this.mDeadBubbles.size(); i++) {
            Bubble bubble2 = (Bubble) this.mDeadBubbles.get(i);
            if (distance(bubble, bubble2) < ((double) (bubble2.getSize() + bubble.getSize()))) {
                return true;
            }
        }
        return false;
    }

    private void notifyGameStateChanged() {
        if (this.mGameStateListener != null) {
            this.mGameStateListener.gameStateChanged(this.mGameState);
        }
    }

    private void onGestureDetected() {
        if (this.mGameState == 3) {
            this.mLastProgress = 0.0f;
            this.mLastStage = 0;
            this.mBubbleShouldShrink = true;
            long currentTimeMillis = System.currentTimeMillis();
            this.mLastGestureTime = currentTimeMillis;
            if (this.mLastShrunkBubble != null) {
                synchronized (this) {
                    this.mLastShrunkBubble.setState(1);
                }
                this.mKilledBubbles++;
                if (this.mKilledBubbles > this.mTopKilledBubbles) {
                    this.mTopKilledBubbles = this.mKilledBubbles;
                    this.mTopKilledBubblesDate = currentTimeMillis;
                    Secure.putIntForUser(this.mContext.getContentResolver(), "assist_gesture_egg_top_score", this.mTopKilledBubbles, -2);
                    Secure.putLongForUser(this.mContext.getContentResolver(), "assist_gesture_egg_top_score_time", this.mTopKilledBubblesDate, -2);
                }
                this.mNextBubbleTime = 0.0f;
                updateScoreText();
            }
        }
    }

    private void onGestureProgress(float f, int i) {
        if (this.mGameState == 3) {
            if (i == 0 && this.mLastStage == 2) {
                this.mVibrator.vibrate(this.mErrorVibrationEffect);
            }
            if (i == 0) {
                this.mBubbleShouldShrink = true;
            }
            synchronized (this) {
                int i2 = 0;
                while (i2 < this.mBubbles.size()) {
                    if (((Bubble) this.mBubbles.get(i2)).getState() != 0) {
                        i2++;
                    } else if (i == 0 || (((Bubble) this.mBubbles.get(0)).equals(this.mLastShrunkBubble)))  {
                        this.mBubbleShouldShrink = true;
                        this.mLastShrunkBubble = (Bubble) this.mBubbles.get(0);
                        if (this.mLastShrunkBubble != null && this.mBubbleShouldShrink && this.mLastShrunkBubble.getState() == 0) {
                            this.mLastShrunkBubble.setSize(Math.max((int) (((float) this.mLastShrunkBubble.getOriginalSize()) - (((float) this.mLastShrunkBubble.getOriginalSize()) * f)), 16));
                        }
                    } else {
                        this.mBubbleShouldShrink = false;
                        this.mLastShrunkBubble.setSize(Math.max((int) (((float) this.mLastShrunkBubble.getOriginalSize()) - (((float) this.mLastShrunkBubble.getOriginalSize()) * f)), 16));
                    }
                }
                this.mLastShrunkBubble.setSize(Math.max((int) (((float) this.mLastShrunkBubble.getOriginalSize()) - (((float) this.mLastShrunkBubble.getOriginalSize()) * f)), 16));
            }
            this.mLastProgress = f;
            this.mLastStage = i;
        }
    }

    private void resetGameState() {
        resetSpiralingAndroids(this.mBounds);
        this.mDeadBubbles.clear();
        this.mKilledBubbles = 0;
        updateScoreText();
        this.mBubbleTouchedBottom = false;
    }

    private void resetSpiralingAndroids(Rect rect) {
        synchronized (this) {
            this.mSpiralingAndroids.clear();
            for (int i = 0; i < 40; i++) {
                this.mSpiralingAndroids.add(new SpiralingAndroid(this.mContext, rect));
            }
        }
    }

    private void updateScoreText() {
        this.mGameStateListener.updateScoreText("" + this.mKilledBubbles + "/" + this.mTopKilledBubbles + " " + DateFormat.format("MM/dd/yyyy HH:mm:ss", this.mTopKilledBubblesDate).toString());
    }

    public void disconnectService() {
        this.mAssistGestureHelper.setListener(null);
        this.mAssistGestureHelper.unbindFromElmyraServiceProxy();
        this.mServiceConnected = false;
    }

    public void draw(@NonNull Canvas canvas) {
            float centerX;
            float centerX2;
            long currentTimeMillis = System.currentTimeMillis();
            canvas.save();
            synchronized (this) {
                int i;
                for (i = 0; i < this.mBubbles.size(); i++) {
                    Bubble bubble = (Bubble) this.mBubbles.get(i);
                    this.mPaint.setColor(bubble.getColor());
                    canvas.drawCircle(bubble.getPoint().x, bubble.getPoint().y, (float) bubble.getSize(), this.mPaint);
                }
                for (i = 0; i < this.mDeadBubbles.size(); i++) {
                    Bubble bubble = (Bubble) this.mDeadBubbles.get(i);
                    this.mPaint.setColor(bubble.getColor());
                    canvas.drawCircle(bubble.getPoint().x, bubble.getPoint().y, (float) bubble.getSize(), this.mPaint);
                }
            }
            this.mPaint.setColor(-1);
            this.mPaint.setAlpha(180);
            float height = (float) (this.mBounds.height() - 80);
            float height2 = (float) this.mBounds.height();
            if (currentTimeMillis - this.mLastGestureTime < 450) {
                float centerX3 = (float) ((((long) this.mBounds.centerX()) * (currentTimeMillis - this.mLastGestureTime)) / 450);
                centerX = ((float) this.mBounds.centerX()) - centerX3;
                centerX2 = ((float) this.mBounds.centerX()) + centerX3;
            } else {
                centerX = this.mLastProgress * ((float) this.mBounds.centerX());
                centerX2 = ((float) this.mBounds.width()) - centerX;
            }
            canvas.drawRect(centerX, height, centerX2, height2, this.mPaint);
            if (this.mGameState != 3) {
                synchronized (this) {
                    int i;
                    for (i = 0; i < this.mSpiralingAndroids.size(); i++) {
                        canvas.save();
                        SpiralingAndroid spiralingAndroid = (SpiralingAndroid) this.mSpiralingAndroids.get(i);
                        Drawable android = spiralingAndroid.getAndroid();
                        canvas.rotate(spiralingAndroid.getCurrentRotation(), (float) android.getBounds().centerX(), (float) android.getBounds().centerY());
                        spiralingAndroid.getAndroid().draw(canvas);
                        canvas.restore();
                    }
                }
            }
            canvas.restore();
        }

    public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
    }

    public void onBoundsChange(Rect rect) {
        this.mBounds = rect;
        if (this.mGameState == 2) {
            startGame(true);
        }
    }

    public void pauseGame() {
        if (this.mGameState != 1) {
            this.mGameState = 1;
            notifyGameStateChanged();
            disconnectService();
            this.mNextBubbleTime -= this.mLastTime;
            if (this.mDriftAnimation != null) {
                this.mDriftAnimation.pause();
            }
        }
    }

    public void setAlpha(@IntRange(from = 0, to = 255) int i) {
        this.mPaint.setAlpha(i);
    }

    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    public void startGame(boolean z) {
        if (this.mBounds == null) {
            this.mGameState = 2;
            notifyGameStateChanged();
        } else if (this.mGameState != 3) {
            if (z) {
                resetGameState();
            }
            connectService();
            if (this.mBubbleTouchedBottom) {
                this.mGameState = 4;
            } else {
                this.mGameState = 3;
                notifyGameStateChanged();
            }
            if (this.mDriftAnimation == null) {
                this.mDriftAnimation = new TimeAnimator();
                this.mDriftAnimation.setTimeListener(new C10382());
            }
            this.mDriftAnimation.start();
        }
    }
}
