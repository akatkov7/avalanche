package com.example.avalanchegame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class Player
{
    private RectF     playerRect;
    private Paint     playerPaint;
    private int       canvasWidth;
    private int       canvasHeight;
    private float     width;
    private float     height;
    private float     ay;
    private float     ax;
    private float     vy       = 0;
    private float     vx       = 0;
    private float     py;
    private float     px;
    protected boolean grounded = false;


    public Player(RectF r, int cW, int cH)
    {
        playerRect = r;
        width = r.right - r.left;
        height = r.top - r.bottom;
        canvasWidth = cW;
        canvasHeight = cH;
        py = playerRect.centerY();
        px = playerRect.centerX();
        ax = 10;
        ay = -canvasHeight * 1.5f;
        playerPaint = new Paint();
        playerPaint.setColor(Color.BLACK);
        playerPaint.setStyle(Style.FILL);
    }


    public void draw(Canvas c)
    {
        float ground = c.getHeight() * 0.8f;
        float cutoff = c.getHeight() * 0.5f;

        RectF localRect;
        if (playerRect.bottom < cutoff)
        {
            Log.d("CUTOFF", "LESS THAN");
            localRect =
                new RectF(
                    playerRect.left,
                    ground - playerRect.top,
                    playerRect.right,
                    ground - playerRect.bottom);
        }
        else
        {
            Log.d("CUTOFF", "GREATER THAN");
            localRect =
                new RectF(
                    playerRect.left,
                    ground - (playerRect.top - cutoff),
                    playerRect.right,
                    ground - (playerRect.bottom - cutoff));
            System.out.println(localRect + "");
        }

        c.drawRect(localRect, playerPaint);
    }


    public RectF getRect()
    {
        return playerRect;
    }


    public int intersects(RectF collided)
    {
        int minimumIntersectIndex = -1;
        float minimumIntersect = Math.max(canvasHeight, canvasWidth);
        if (playerRect.bottom < collided.top
            && playerRect.bottom > collided.bottom
            && ((playerRect.right < collided.right && playerRect.right > collided.left) || (playerRect.left > collided.left && playerRect.left < collided.right)))
        {
            float intersectBot = collided.top - playerRect.bottom;
            float intersectRight = playerRect.right - collided.left;
            float intersectLeft = collided.right - playerRect.left;
            if (intersectBot > 0 && intersectBot < minimumIntersect)
            {
                minimumIntersectIndex = 2;
                minimumIntersect = intersectBot;
                // Log.d("INTERSECTS", "2 "+this.playerRect+" "+collided);
            }
            if (intersectRight > 0 && intersectRight < minimumIntersect)
            {
                minimumIntersectIndex = 1;
                minimumIntersect = intersectRight;
                Log.d("INTERSECTS", "1 " + this.playerRect + " " + collided);
            }
            if (intersectLeft > 0 && intersectLeft < minimumIntersect)
            {
                minimumIntersectIndex = 3;
                minimumIntersect = intersectLeft;
                Log.d("INTERSECTS", "3 " + this.playerRect + " " + collided);
            }
        }
        else if (playerRect.top < collided.top
            && playerRect.top > collided.bottom
            && ((playerRect.right < collided.right && playerRect.right > collided.left) || (playerRect.left > collided.left && playerRect.left < collided.right)))
        {
            float intersectBot = playerRect.top - collided.bottom;
            float intersectRight = playerRect.right - collided.left;
            float intersectLeft = collided.right - playerRect.left;
            if (intersectBot > 0 && intersectBot < minimumIntersect)
            {
                minimumIntersectIndex = 0;
                minimumIntersect = intersectBot;
                Log.d("INTERSECTS", "0 " + this.playerRect + " " + collided);
            }
            if (intersectRight > 0 && intersectRight < minimumIntersect)
            {
                minimumIntersectIndex = 1;
                minimumIntersect = intersectRight;
                Log.d("INTERSECTS", "1 " + this.playerRect + " " + collided);
            }
            if (intersectLeft > 0 && intersectLeft < minimumIntersect)
            {
                minimumIntersectIndex = 3;
                minimumIntersect = intersectLeft;
                Log.d("INTERSECTS", "3 " + this.playerRect + " " + collided);
            }
        }
        return minimumIntersectIndex;
    }


    public void fixIntersection(RectF other, int whichSide)
    {
        if (whichSide == 0) // top
        {
            playerRect.top = other.bottom; // + 10;
            playerRect.bottom = playerRect.top - height;
            vy = 0;
            py = playerRect.centerY();
            // Log.d("CENTER", playerRect + "");
        }
        else if (whichSide == 1) // right
        {
            playerRect.right = other.left;
            playerRect.left = playerRect.right - width;
            vx = 0;
            px = playerRect.centerX();
            // Log.d("CENTER", playerRect + "");
        }
        else if (whichSide == 2) // bottom
        {
            playerRect.bottom = other.top;
            playerRect.top = playerRect.bottom + height;
            vy = 0;
            py = playerRect.centerY();
            // Log.d("CENTER", playerRect+"");
        }
        else if (whichSide == 3) // left
        {
            playerRect.left = other.right;
            playerRect.right = playerRect.left + width;
            vx = 0;
            px = playerRect.centerX();
            // Log.d("CENTER", playerRect + "");
        }
    }


    public void adjustPosition(int deltaT)
    {
        vy += ay * (deltaT / 1000.0f);
        float pytemp =
            py + vy * (deltaT / 1000.0f)
                + (-ay * (deltaT / 1000.0f) * (deltaT / 1000.0f));

        // TODO: add a friction for ground stuff
        float pxtemp = px + vx * (deltaT / 1000.0f);
        if (pytemp > py)
            grounded = false;
        playerRect.offset(pxtemp - px, pytemp - py);
        if (playerRect.left < 0)
        {
            Log.d("OFF SCREEN", "YAYA");
            playerRect.right = canvasWidth;
            playerRect.left = playerRect.right - width;
        }
        else if (playerRect.right > canvasWidth)
        {
            Log.d("OFF SCREEN", "YAYA");
            playerRect.left = 0;
            playerRect.right = playerRect.left + width;
        }
        py = playerRect.centerY();
        px = playerRect.centerX();
    }


    public void setXVelocity(float fok)
    {
        vx = -100 * fok;
    }


    public void jump()
    {
        vy += 1500;
        // playerRect.offset(0, -canvasHeight / 5);
    }
}
