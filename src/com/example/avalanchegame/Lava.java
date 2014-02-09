package com.example.avalanchegame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class Lava
    extends RectF
{
    private Paint lavaPaint;
    public Lava(float left, float top, float right, float bottom)
    {
        super(left, top, right, bottom);
        lavaPaint = new Paint();
        lavaPaint.setStyle(Style.FILL);
        lavaPaint.setColor(Color.RED);
    }

    public void draw(Canvas c, float playerY)
    {
        float ground = c.getHeight() * 0.8f;
        float cutoff = c.getHeight() * 0.5f;
        RectF localBox = new RectF(left, ground - top, right, ground - bottom);
        localBox.offset(0, playerY - (ground - cutoff));
        c.drawRect(localBox, lavaPaint);
    }
}
