package com.google.android.settings.gestures.assist.bubble;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class AssistGesturePlayButtonDrawable extends Drawable {
    private Rect mBounds;
    private PointF mCircleCenter;
    private float mCircleRadius;
    private Paint mPaint = new Paint();

    public AssistGesturePlayButtonDrawable() {
        this.mPaint.setAntiAlias(true);
        this.mCircleCenter = new PointF();
    }

    private double distance(PointF pointF, PointF pointF2) {
        return Math.sqrt(Math.pow((double) (pointF2.x - pointF.x), 2.0d) + Math.pow((double) (pointF2.y - pointF.y), 2.0d));
    }

    private void drawTriangle(Canvas canvas, float f, float f2, float f3, Paint paint) {
        float cos = ((float) Math.cos(0.5235987901687622d)) * f3;
        float sin = ((float) Math.sin(0.5235987901687622d)) * f3;
        PointF pointF = new PointF(f, f2 - f3);
        PointF pointF2 = new PointF(f + cos, f2 + sin);
        PointF pointF3 = new PointF(f - cos, sin + f2);
        canvas.save();
        canvas.rotate(90.0f, f, f2);
        Path path = new Path();
        path.setFillType(FillType.EVEN_ODD);
        path.moveTo(pointF.x, pointF.y);
        path.lineTo(pointF2.x, pointF2.y);
        path.lineTo(pointF3.x, pointF3.y);
        path.lineTo(pointF.x, pointF.y);
        path.close();
        canvas.drawPath(path, paint);
        canvas.restore();
    }

    public void draw(Canvas canvas) {
        canvas.save();
        if (this.mBounds != null) {
            this.mCircleCenter.x = (float) (this.mBounds.width() / 2);
            this.mCircleCenter.y = (float) (this.mBounds.height() / 2);
            this.mCircleRadius = (float) (this.mBounds.width() / 6);
            this.mPaint.setColor(Color.rgb(90, 120, 160));
            canvas.drawCircle(this.mCircleCenter.x, this.mCircleCenter.y, this.mCircleRadius, this.mPaint);
            this.mPaint.setColor(-1);
            drawTriangle(canvas, (float) (this.mBounds.width() / 2), (float) (this.mBounds.height() / 2), (float) (this.mBounds.width() / 12), this.mPaint);
            canvas.restore();
        }
    }

    public int getOpacity() {
        return -3;
    }

    public boolean hitTest(float f, float f2) {
        return distance(new PointF(f, f2), this.mCircleCenter) <= ((double) this.mCircleRadius);
    }

    public void onBoundsChange(Rect rect) {
        this.mBounds = rect;
        invalidateSelf();
    }

    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }
}
