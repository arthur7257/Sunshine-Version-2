package com.example.android.sunshine.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.StringDef;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by arturo.ayala on 6/16/16.
 */
public class WindView extends View {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({N, S, E, W, NE, NW, SE, SW})
    public @interface Direction {}
    public static final String N = "N";
    public static final String S = "S";
    public static final String E = "E";
    public static final String W = "W";
    public static final String NE = "NE";
    public static final String NW = "NW";
    public static final String SE = "SE";
    public static final String SW = "SW";


    private static final Map<String, Integer> DIRECTION_MAP = new HashMap<>();
    static {
        DIRECTION_MAP.put(N, 90);
        DIRECTION_MAP.put(S, 270);
        DIRECTION_MAP.put(E, 180);
        DIRECTION_MAP.put(W, 0);
        DIRECTION_MAP.put(NE, 135);
        DIRECTION_MAP.put(NW, 45);
        DIRECTION_MAP.put(SE, 225);
        DIRECTION_MAP.put(SW, 315);
    }
    private float mSpeed;
    private int mDegrees;

    @ColorInt
    private int mColor;

    public WindView(Context context) {
        super(context);
        mColor = getResources().getColor(android.R.color.black);
        init();
    }

    public WindView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typed = context.getTheme()
                                  .obtainStyledAttributes(attrs, R.styleable.WindView, 0, 0);
        try {
            mSpeed = typed.getFloat(R.styleable.WindView_speed, 0f);
            mDegrees = typed.getInt(R.styleable.WindView_direction,
                                    typed.getInt(R.styleable.WindView_degrees, 0));
            mColor = typed.getColor(R.styleable.WindView_speedColor,
                                    getResources().getColor(android.R.color.black));
        } finally {
            typed.recycle();
        }
        init();
    }

    private Paint mPaint;
    private TextPaint mTextPaint;
    private float mTextHeight;
    private boolean mIsImperial;

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mColor);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mColor);
        if (mTextHeight == 0) {
            mTextHeight = mTextPaint.getTextSize();
        } else {
            mTextPaint.setTextSize(mTextHeight);
        }
        mPaint.setTextSize(mTextHeight);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2f);
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
        invalidate();
        requestLayout();
    }

    public void setDegrees(int degrees) {
        mDegrees = degrees;
        invalidate();
        requestLayout();
    }

    public void setImperial(boolean imperial) {
        mIsImperial = imperial;
        invalidate();
        requestLayout();
    }

    public void setDirection(@Direction String direction) {
        mDegrees = direction != null && DIRECTION_MAP.containsKey(direction) ?
                   DIRECTION_MAP.get(direction) :
                   mDegrees;
        invalidate();
        requestLayout();
    }

    private float mRadius;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddedWidth = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int w = paddedWidth;
        if (mode == MeasureSpec.EXACTLY) {
            w += MeasureSpec.getSize(widthMeasureSpec);
        }
        mRadius = w / 2f;
        setMeasuredDimension(w, w);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float speed = mIsImperial ? Utility.getImperialWindSpeed(mSpeed) : mSpeed;
        float center = mRadius;

        canvas.drawText(String.format(Locale.US, "%.2f", speed), center, center, mPaint);
        canvas.drawCircle(center, center, center, mPaint);
    }
}
