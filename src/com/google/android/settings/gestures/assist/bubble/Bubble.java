package com.google.android.settings.gestures.assist.bubble;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import com.android.settings.R;

public class Bubble {
    private int mAlpha;
    private float mAmplitude;
    private int mBubbleState = 0;
    private int mColor;
    private float mFrequency;
    private PointF mOriginalPoint = new PointF();
    private int mOriginalSize = ThreadLocalRandom.current().nextInt(40, 140);
    private PointF mPoint = new PointF();
    private Random mRandom = new Random();
    private int mSize = this.mOriginalSize;
    private float mVelocityY;

    public Bubble(Rect rect) {
        this.mOriginalPoint.x = ((float) rect.width()) * ((this.mRandom.nextFloat() * 0.6f) + 0.2f);
        this.mOriginalPoint.y = (float) (rect.height() + this.mOriginalSize);
        this.mPoint = this.mOriginalPoint;
        this.mFrequency = this.mRandom.nextFloat() * 2.0f;
        this.mAmplitude = this.mRandom.nextFloat() * 10.0f;
        this.mVelocityY = ((this.mRandom.nextFloat() * 0.4f) + 0.8f) * 600.0f;
        this.mAlpha = (int) (((this.mRandom.nextFloat() * 0.2f) + 0.6f) * 255.0f);
        this.mColor = Color.argb(this.mAlpha, this.mRandom.nextInt(255), this.mRandom.nextInt(255), this.mRandom.nextInt(255));
    }

    private void updateDying(long j, long j2) {
        this.mSize -= (int) (((float) j2) * 0.1f);
        if (this.mSize < 0) {
            this.mSize = 0;
            this.mBubbleState = 2;
        }
    }

    public int getColor() {
        return this.mColor;
    }

    public int getOriginalSize() {
        return this.mOriginalSize;
    }

    public PointF getPoint() {
        return this.mPoint;
    }

    public int getSize() {
        return this.mSize;
    }

    public int getState() {
        return this.mBubbleState;
    }

    public boolean isBubbleDead() {
        return this.mBubbleState == 2;
    }

    public boolean isBubbleTouchingTop() {
        return this.mPoint.y - ((float) this.mSize) <= 0.0f;
    }

    public void setSize(int i) {
        this.mSize = i;
    }

    public void setState(int i) {
        this.mBubbleState = i;
    }

    public void update(long j, long j2) {
        float f = (float) j;
        float f2 = (float) j2;
        PointF pointF = this.mPoint;
        pointF.y -= (f2 * 0.001f) * this.mVelocityY;
        this.mPoint.x = (((float) Math.sin((((double) this.mFrequency) * 6.283185307179586d) * ((double) (f * 0.001f)))) * this.mAmplitude) + this.mOriginalPoint.x;
        if (this.mBubbleState == 1) {
            updateDying(j, j2);
        }
    }
}
