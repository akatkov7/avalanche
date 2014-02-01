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
    private RectF playerRect;
    private Paint playerPaint;
    private int   canvasWidth;
    private int   canvasHeight;
    private int   width;
    private int   height;
    private float ay;
    private float ax;
    private float vy = 0;
    private float vx = 0;
    private float py;
    private float px;


    public Player(RectF r, int cW, int cH)
    {
        playerRect = r;
        width = (int)(r.right - r.left);
        height = (int)(r.bottom - r.top);
        canvasWidth = cW;
        canvasHeight = cH;
        py = playerRect.centerY();
        px = playerRect.centerX();
        ay = canvasHeight * 1.5f;
        playerPaint = new Paint();
        playerPaint.setColor(Color.BLACK);
        playerPaint.setStyle(Style.FILL);
    }


    public void draw(Canvas c)
    {
        c.drawRect(playerRect, playerPaint);
    }


    public RectF getRect()
    {
        return playerRect;
    }


    public int intersects(RectF collided)
    {
// if ((playerRect.top < collided.bottom && playerRect.top > collided.top)
// || (playerRect.bottom > collided.top && playerRect.bottom < collided.bottom))
// {
// if (playerRect.top < collided.bottom
// && playerRect.top > collided.top)
// return 0;
// if (playerRect.bottom > collided.top
// && playerRect.bottom < collided.bottom)
// return 2;
// if (playerRect.right > collided.left
// && playerRect.right < collided.right)
// return 1;
// if (playerRect.left < collided.right
// && playerRect.left > collided.left)
// return 3;
// }
        int minimumIntersectIndex = -1;
        if (playerRect.bottom > collided.top
            && playerRect.bottom < collided.bottom
            && ((playerRect.right < collided.right && playerRect.right > collided.left) || (playerRect.left > collided.left && playerRect.left < collided.right)))
        {
            float intersectBot = playerRect.bottom - collided.top;
            float intersectRight = playerRect.right - collided.left;
            float intersectLeft = collided.right - playerRect.left;
            minimumIntersectIndex = 2;
            float minimumIntersect = intersectBot;
            if(intersectRight > 0 && intersectRight < minimumIntersect)
            {
                minimumIntersectIndex = 1;
                minimumIntersect = intersectRight;
            }
            if(intersectLeft > 0 && intersectLeft < minimumIntersect)
            {
                minimumIntersectIndex = 3;
                minimumIntersect = intersectLeft;
            }
        }
        else if (playerRect.top < collided.bottom
            && playerRect.top > collided.top
            && ((playerRect.right < collided.right && playerRect.right > collided.left) || (playerRect.left > collided.left && playerRect.left < collided.right)))
        {
            float intersectBot = collided.bottom - playerRect.top;
            float intersectRight = playerRect.right - collided.left;
            float intersectLeft = collided.right - playerRect.left;
            minimumIntersectIndex = 0;
            float minimumIntersect = intersectBot;
            if(intersectRight > 0 && intersectRight < minimumIntersect)
            {
                minimumIntersectIndex = 1;
                minimumIntersect = intersectRight;
            }
            if(intersectLeft > 0 && intersectLeft < minimumIntersect)
            {
                minimumIntersectIndex = 3;
                minimumIntersect = intersectLeft;
            }
        }
        return minimumIntersectIndex;
    }


    public void fixIntersection(RectF other, int whichSide)
    {
        if (whichSide == 0) // top
        {
            playerRect.top = other.bottom; // + 10;
            playerRect.bottom = playerRect.top + height;
            vy = 0;
            py = playerRect.centerY();
        }
        else if (whichSide == 1) // right
        {
            playerRect.right = other.left;
            playerRect.left = playerRect.right - height;
            vx = 0;
            px = playerRect.centerX();
        }
        else if (whichSide == 2) // bottom
        {
            playerRect.bottom = other.top;
            playerRect.top = playerRect.bottom - height;
            vy = 0;
            py = playerRect.centerY();
            Log.d("CENTER", py+"");
        }
        else if (whichSide == 3) // left
        {
            playerRect.left = other.right;
            playerRect.right = playerRect.left + width;
            vx = 0;
            px = playerRect.centerX();
        }
    }


    public void adjustPosition(int deltaT)
    {
        float pytemp =
            py + vy * (deltaT / 1000.0f)
                + (-ay * (deltaT / 1000.0f) * (deltaT / 1000.0f));
        vy += ay * (deltaT / 1000.0f);
        float pxtemp = px + vx * (deltaT / 1000.0f);
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


    public void setXAccel(float fok)
    {
        // Log.d("FOK", "" + fok);
        // ax = -100 * fok;
        vx = -100 * fok;
    }


    public void jump()
    {
        vy -= 1500;
        // playerRect.offset(0, -canvasHeight / 5);
    }
}
