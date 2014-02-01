package com.example.avalanchegame;

import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public class Box
    extends RectF
{
    private float x;
    private float y;
    private float size;

    private float vy;

    private Paint fillPaint;
    private Paint outlinePaint;

    public Box(float x, float y, float size)
    {
        super(x - size/2, y + size/2, x + size/2, y - size/2);

        this.x = x;
        this.y = y;
        this.size = size;

        setVy(-5f);

        fillPaint = new Paint();
        fillPaint.setColor(Color.BLUE);
        fillPaint.setStyle(Style.FILL);
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.BLACK);
        outlinePaint.setStyle(Style.STROKE);
    }

    public void updateBounds() {
        set(x - size/2, y + size/2, x + size/2, y - size/2);
    }

    public void draw(Canvas c) {
        float ground = c.getHeight() * 0.8f;
        RectF localBox = new RectF(left, ground - top, right, ground - bottom);
        c.drawRect(localBox, fillPaint);
        c.drawRect(localBox, outlinePaint);
    }

    public float getVy()
    {
        return vy;
    }

    public void setVy(float vy)
    {
        this.vy = vy;
    }

    // ----------------------------------------------------------
    /**
     * @return the x
     */
    public float getX()
    {
        return x;
    }

    // ----------------------------------------------------------
    /**
     * @param x the x to set
     */
    public void setX(float x)
    {
        this.x = x;
        updateBounds();
    }

    // ----------------------------------------------------------
    /**
     * @return the y
     */
    public float getY()
    {
        return y;
    }

    // ----------------------------------------------------------
    /**
     * @param y the y to set
     */
    public void setY(float y)
    {
        this.y = y;
        updateBounds();
    }

    // ----------------------------------------------------------
    /**
     * @return the size
     */
    public float getSize()
    {
        return size;
    }

    // ----------------------------------------------------------
    /**
     * @param size the size to set
     */
    public void setSize(float size)
    {
        this.size = size;
        updateBounds();
    }


}
