package com.google.android.settings.gestures.assist.bubble;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import java.util.Random;

public class SpiralingAndroid {
    private Drawable mAndroid;
    private float mCurrentRotation;
    private Random mRandom = new Random();
    private final float mRotationSpeed = (((this.mRandom.nextFloat() * 0.8f) + 0.8f) * 360.0f);
    private final float mVelocityY = (((this.mRandom.nextFloat() * 0.8f) + 0.8f) * 400.0f);

    public SpiralingAndroid(Context context, Rect rect) {
        int width = (rect.width() / 10) + this.mRandom.nextInt(rect.width() / 5);
       // (this.mAndroid = context.getDrawable(com.android.settings.R) instanceof  ? (()(this.mAndroid = context.getDrawable(com.android.settings.R)) : null;;

        this.mAndroid.mutate();
        this.mAndroid.setColorFilter(this.mRandom.nextInt(), Mode.SRC_ATOP);
        int nextInt = this.mRandom.nextInt(rect.width() - width);
        int i = (-this.mRandom.nextInt(rect.height() / 2)) - width;
        this.mAndroid.setBounds(new Rect(nextInt, i, nextInt + width, width + i));
    }

    public Drawable getAndroid() {
        return this.mAndroid;
    }

    public float getCurrentRotation() {
        return this.mCurrentRotation;
    }

    public void update(long j, long j2) {
        float f = (float) j;
        f = 0.001f * ((float) j2);
        float f2 = this.mVelocityY;
        Rect copyBounds = this.mAndroid.copyBounds();
        copyBounds.offset(0, (int) (f2 * f));
        this.mAndroid.setBounds(copyBounds);
        this.mCurrentRotation = (f * this.mRotationSpeed) + this.mCurrentRotation;
    }
}
