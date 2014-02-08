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
    private boolean     grounded                   = false;
    private boolean     canJumpFromLeft            = false;
    private boolean     canJumpFromRight           = false;
    private final float startingSideJumpVelocity   = 1000f;
    private float       additionalSideJumpVelocity = 0f;
    private float       jumpVelocity               = 1500f;
    private boolean     midJump                    = false;


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
            localRect =
                new RectF(
                    playerRect.left,
                    ground - playerRect.top,
                    playerRect.right,
                    ground - playerRect.bottom);
        }
        else
        {
            localRect =
                new RectF(playerRect.left, ground - playerRect.top
                    - (playerRect.bottom - cutoff), playerRect.right, ground
                    - playerRect.bottom - (playerRect.bottom - cutoff));
            System.out.println(localRect + "");
        }

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
        }
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
            vy = -100f;
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
                vy = 0;
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
                vy = 0;
                px = playerRect.centerX();
                canJumpFromLeft = true;
            }
            // Log.d("CENTER", playerRect + "");
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
            float adjustmentAmount = startingSideJumpVelocity * deltaT / 100f;
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


    public void tryToJump()
    {
        if (grounded)
        {
            jump();
        }
        else if (canJumpFromLeft)
        {
            jumpFromLeft();
        }
        else if (canJumpFromRight)
        {
            jumpFromRight();
        }
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


    public void setGrounded(boolean newState)
    {
        grounded = newState;
    }
}
