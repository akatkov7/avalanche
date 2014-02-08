package com.example.avalanchegame;

import android.util.Log;
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


    /**
     * Constructor for the box class
     *
     * @param x
     *            the center x of the box
     * @param y
     *            the center y of the box
     * @param size
     *            the length and the width of the box
     * @param initVY
     *            a negative float that dictates how fast the box falls (-200f
     *            is a decent speed)
     */
    public Box(float x, float y, float size, float initVY)
    {
        super(x - size / 2, y + size / 2, x + size / 2, y - size / 2);

        this.x = x;
        this.y = y;
        this.size = size;

        setVy(initVY);

        fillPaint = new Paint();
        fillPaint.setColor(Color.BLUE);
        fillPaint.setStyle(Style.FILL);
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.BLACK);
        outlinePaint.setStyle(Style.STROKE);

    }


    public void updateBounds()
    {
        set(x - size / 2, y + size / 2, x + size / 2, y - size / 2);
    }


    public void draw(Canvas c, float playerY)
    {
        float ground = c.getHeight() * 0.8f;
        float cutoff = c.getHeight() * 0.5f;

        RectF localBox;

        /*
         * The function to convert from global to local coordinates not
         * accounting for camera shift is l(y) = groundY - y. The playerY passed
         * in is in local coordinates, so it should first be converted to global
         * coordinates.
         */
        if (playerY < cutoff)
        {
            localBox = new RectF(left, ground - top, right, ground - bottom);
        }
        else
        {
            localBox =
                new RectF(
                    left,
                    ground - (top - (playerY - cutoff)),
                    right,
                    ground - (bottom - (playerY - cutoff)));
        }
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
        return this.centerX();
    }


    // ----------------------------------------------------------
    /**
     * @param x
     *            the x to set
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
        return this.centerY();
    }


    // ----------------------------------------------------------
    /**
     * @param y
     *            the y to set
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
     * @param size
     *            the size to set
     */
    public void setSize(float size)
    {
        this.size = size;
        updateBounds();
    }


    public void adjustPosition(int deltaT)
    {
        this.offset(0, vy * deltaT / 1000);
        y += vy * deltaT / 1000;
    }


    public boolean isMoving()
    {
        return vy != 0;
    }


    public int intersects(RectF collided)
    {
        int minimumIntersectIndex = -1;
        if (this.bottom < collided.top
            && this.bottom > collided.bottom
            && ((this.right < collided.right && this.right > collided.left)
                || (this.left > collided.left && this.left < collided.right) || (this.right > collided.right && this.left < collided.left)))
        {
            minimumIntersectIndex = 2;
        }
// else if (this.top < collided.top
// && this.top > collided.bottom
// && ((this.right < collided.right && this.right > collided.left) || (this.left
// > collided.left && this.left < collided.right)))
// {
// minimumIntersectIndex = 0;
// }
        return minimumIntersectIndex;
    }


    public void fixIntersection(RectF other, int whichSide)
    {
        if (whichSide == 2) // bottom
        {
            Log.d("Fock", "Top Top Fockothy");
            float amount = other.top - this.bottom + 0.5f;
            offset(0, amount);

            vy = 0;
            y += amount;
            // Log.d("CENTER", playerRect + "");
        }
    }

}
