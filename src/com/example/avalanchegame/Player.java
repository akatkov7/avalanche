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


    public void adjustPosition(int deltaT)
    {
        float pytemp =
            py + vy * (deltaT / 1000.0f)
                + (-ay * (deltaT / 1000.0f) * (deltaT / 1000.0f));
        vy += ay * (deltaT / 1000.0f);
        float pxtemp =
            px + vx * (deltaT / 1000.0f);
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


    public void fixIntersection(RectF other)
    {
        playerRect.bottom = other.top;
        playerRect.top = playerRect.bottom - height;
        vy = 0;
        py = playerRect.centerY();
    }


    public void jump()
    {
        vy -= 1500;
        // playerRect.offset(0, -canvasHeight / 5);
    }
}
