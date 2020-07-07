package com.example.teleprompter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

public class ColorPicker extends View {
    private Paint paint = new Paint();
    private String color = "#000000";
    private float luminance = 0;
    private final float scale = getResources().getDisplayMetrics().density;
    private boolean showText;

    public ColorPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        showText = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorPicker, 0, 0).getBoolean(R.styleable.ColorPicker_show_text, true);
    }

    public ColorPicker(Context context) {
        super(context);
    }

    public ColorPicker(Context context, @Nullable AttributeSet attrs, int i) {
        super(context, attrs, i);
    }

    public void setColor(String color) {
        setBackgroundColor(Color.parseColor(color));
    }

    public void setColor(@ColorInt int color) {
        setBackgroundColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!showText) return;

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((int) (16.0f * scale + 0.7f));
        paint.setColor((luminance >= 0.5) ? Color.BLACK : Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        int x = getWidth() / 2;
        Paint.FontMetrics metrics = paint.getFontMetrics();
        int height = (int) (metrics.descent + metrics.ascent);
        int y = getHeight() / 2 - height / 2;
        canvas.drawText(color, x, y, paint);
    }

    @Override
    public void setBackgroundColor(@ColorInt int i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) luminance = Color.luminance(i);
        else luminance = (float) ColorUtils.calculateLuminance(i);
        color = String.format("#%06X", (0xFFFFFF & i));
        super.setBackgroundColor(i);
    }
}