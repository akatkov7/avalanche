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
    private RectF       playerRect;
    private RectF       startRect;
    private Paint       playerPaint;
    private int         canvasWidth;
    private int         canvasHeight;
    private float       width;
    private float       height;
    private float       ay;
    private float       ax;
    private float       vy                         = 0;
    private float       vx                         = 0;
    private float       py;
    private float       px;
    private boolean     sideSwitched               = false;
    private boolean     grounded                   = false;
    private boolean     canJumpFromLeft            = false;
    private boolean     canJumpFromRight           = false;
    private final float startingSideJumpVelocity   = 750;
    private float       additionalSideJumpVelocity = 0f;
    private float       jumpVelocity               = 1500f;
    private boolean     midJump                    = false;


    public Player(RectF r, int cW, int cH)
    {
        startRect = r;
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


    public void restart()
    {
        playerRect = startRect;
        py = playerRect.centerY();
        px = playerRect.centerX();
        vy = 0;
        vx = 0;
        sideSwitched = false;
        grounded = false;
        canJumpFromLeft = false;
        canJumpFromRight = false;
        additionalSideJumpVelocity = 0f;
        jumpVelocity = 1500f;
        midJump = false;
    }


    public void draw(Canvas c)
    {
// float ground = c.getHeight() * 0.8f;
// float cutoff = c.getHeight() * 0.5f;
//
// RectF localRect;
// if (playerRect.bottom < cutoff)
// {
// localRect =
// new RectF(
// playerRect.left,
// ground - playerRect.top,
// playerRect.right,
// ground - playerRect.bottom);
// }
// else
// {
// localRect =
// new RectF(
// playerRect.left,
// ground - playerRect.top - (playerRect.bottom - cutoff - ground),
// playerRect.right,
// ground - playerRect.bottom - (playerRect.bottom - cutoff - ground));
// System.out.println(localRect + "");
// }
//
// c.drawRect(localRect, playerPaint);

        float ground = c.getHeight() * 0.8f;
        float cutoff = c.getHeight() * 0.5f;
        RectF localRect =
            new RectF(
                playerRect.left,
                ground - playerRect.top,
                playerRect.right,
                ground - playerRect.bottom);
        localRect.offset(0, playerRect.bottom - (ground - cutoff));

        c.drawRect(localRect, playerPaint);
    }


    public RectF getRect()
    {
        return playerRect;
    }


    /**
     * This method is solely for determining whether or not the player has
     * collided with anything. This method should not change anything about the
     * state of the player or the RectF argument.
     *
     * @param collided
     *            the rectangle that should be tested to see if it collides with
     *            the player
     * @return -1 for no collision, 0-4 for a collision on a side of the player,
     *         starting with 0 at the top and going clockwise
     */
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
            }
            if (intersectRight > 0 && intersectRight < minimumIntersect)
            {
                minimumIntersectIndex = 1;
                minimumIntersect = intersectRight;
            }
            if (intersectLeft > 0 && intersectLeft < minimumIntersect)
            {
                minimumIntersectIndex = 3;
                minimumIntersect = intersectLeft;
            }
            if ((intersectRight > 0 || intersectLeft > 0) && sideSwitched)
            {
                minimumIntersectIndex = 4;
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
            }
            if (intersectRight > 0 && intersectRight < minimumIntersect)
            {
                minimumIntersectIndex = 1;
                minimumIntersect = intersectRight;
            }
            if (intersectLeft > 0 && intersectLeft < minimumIntersect)
            {
                minimumIntersectIndex = 3;
                minimumIntersect = intersectLeft;
            }
            if ((intersectRight > 0 || intersectLeft > 0) && sideSwitched)
            {
                minimumIntersectIndex = 4;
            }
        }
        sideSwitched = false;
        return minimumIntersectIndex;
    }


    /**
     * This method should be called when the player collides with a box. It
     * fixes the position of the player based on what side intersected, and then
     * allows it to jump a direction accordingly.
     *
     * @param other
     *            the RectF the player collided with
     * @param whichSide
     *            an integer indicating what side of the player collided. See
     *            documentation for -intersects(RectF)
     */
    public void fixIntersection(RectF other, int whichSide)
    {
        if (whichSide == 0) // top
        {
            playerRect.top = other.bottom - 0.5f; // + 10;
            playerRect.bottom = playerRect.top - height;
            vy = -canvasHeight / 4f;
            py = playerRect.centerY();
            // Log.d("CENTER", playerRect + "");
        }
        else if (whichSide == 1) // right
        {
            playerRect.right = other.left - 0.5f;
            playerRect.left = playerRect.right - width;
            vx = 0;
            if (!midJump)
            {
                vx = 20.0f;
                vy = -230.0f;
                px = playerRect.centerX();
                canJumpFromRight = true;
            }
            // Log.d("CENTER", playerRect + "");
        }
        else if (whichSide == 2) // bottom
        {
            playerRect.bottom = other.top + 0.5f;
            playerRect.top = playerRect.bottom + height;
            vy = 0;
            py = playerRect.centerY();
            midJump = false;
            grounded = true;
            // Log.d("CENTER", playerRect+"");
        }
        else if (whichSide == 3) // left
        {
            playerRect.left = other.right + 0.5f;
            playerRect.right = playerRect.left + width;
            vx = 0;
            if (!midJump)
            {
                vx = -20.0f;
                vy = -230.0f;
                px = playerRect.centerX();
                canJumpFromLeft = true;
            }
            // Log.d("CENTER", playerRect + "");
        }
        else if (whichSide == 4) // switched side and collided
        {
            if (playerRect.right > canvasWidth)
            {
                System.out.println("IM FIXING 1 SHIT");
                playerRect.left = -width / 2;
                playerRect.right = playerRect.left + width;
            }
            else if (playerRect.left < 0)
            {
                System.out.println("IM FIXING 2 SHIT");
                playerRect.right = canvasWidth + width / 2;
                playerRect.left = playerRect.right - width;
            }
        }
    }


    /**
     * Move the player based on the amount of time that passed since the last
     * frame (for smooth movement)
     *
     * @param deltaT
     *            the amount of time in milliseconds since the last time
     *            adjustPosition was called
     */
    public void adjustPosition(int deltaT)
    {
        vy += ay * (deltaT / 1000.0f);
        if (midJump && vy <= 0)
            midJump = false;
        float pytemp =
            py + vy * (deltaT / 1000.0f)
                + (-ay * (deltaT / 1000.0f) * (deltaT / 1000.0f));

        // TODO: add a friction for ground stuff
        // add amount of sideJump from jumping
        float pxtemp =
            px + (vx + additionalSideJumpVelocity) * (deltaT / 1000.0f);
        // TODO: decrement according to deltaT
        if (additionalSideJumpVelocity > 0 || additionalSideJumpVelocity < 0)
        {
            float adjustmentAmount = startingSideJumpVelocity * deltaT / 250f;
            if (Math.abs(additionalSideJumpVelocity) < adjustmentAmount)
                additionalSideJumpVelocity = 0;
            else if (additionalSideJumpVelocity > 0)
                additionalSideJumpVelocity -= adjustmentAmount;
            else
                additionalSideJumpVelocity += adjustmentAmount;
        }
// if (pytemp > py)
// grounded = false;
        playerRect.offset(pxtemp - px, pytemp - py);
        if (px < 0)
        {
            Log.d("OFF SCREEN", "YAYA");
            playerRect.right = canvasWidth + width / 2;
            playerRect.left = playerRect.right - width;
            sideSwitched = true;
        }
        else if (px > canvasWidth)
        {
            Log.d("OFF SCREEN", "YAYA");
            playerRect.left = -width / 2;
            playerRect.right = playerRect.left + width;
            sideSwitched = true;
        }
        py = playerRect.centerY();
        px = playerRect.centerX();
        // TODO: test this line.
        // set grounded every frame, update via a collision. otherwise, you can
        // run off a block then jump (which shouldn't be a thing)
    }


    public void setNotGrounded()
    {
        grounded = false;
        canJumpFromLeft = false;
        canJumpFromRight = false;
    }


    public void setXVelocity(float dvx)
    {
        vx = dvx;
    }


    public boolean tryToJump()
    {
        if (grounded)
        {
            jump();
            return true;
        }
        else if (canJumpFromLeft)
        {
            jumpFromLeft();
            return true;
        }
        else if (canJumpFromRight)
        {
            jumpFromRight();
            return true;
        }
        return false;
    }


    private void jumpFromLeft()
    {
        vy += jumpVelocity;
        midJump = true;
        additionalSideJumpVelocity = startingSideJumpVelocity;
        setNotGrounded();
    }


    private void jumpFromRight()
    {
        vy += jumpVelocity;
        midJump = true;
        additionalSideJumpVelocity = -startingSideJumpVelocity;
        setNotGrounded();
    }


    private void jump()
    {
        vy += jumpVelocity;
        midJump = true;
        setNotGrounded();
    }


    public boolean isGrounded()
    {
        return grounded;
    }


    public float getY()
    {
        return py;
    }


    public float getX()
    {
        return px;
    }
}
