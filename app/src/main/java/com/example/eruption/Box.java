package com.example.eruption;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class Box extends RectF implements Collidable {
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
	public Box(float x, float y, float size, float initVY) {
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

	public void updateBounds() {
		set(x - size / 2, y + size / 2, x + size / 2, y - size / 2);
	}

	public void draw(Canvas c, float playerY) {
		float ground = c.getHeight() * 0.8f;
		float cutoff = c.getHeight() * 0.5f;
		RectF localBox = new RectF(left, ground - top, right, ground - bottom);
		localBox.offset(0, playerY - (ground - cutoff));
		c.drawRect(localBox, fillPaint);
		c.drawRect(localBox, outlinePaint);
	}

	public float getVy() {
		return vy;
	}

	public void setVy(float vy) {
		this.vy = vy;
	}

	// ----------------------------------------------------------
	/**
	 * @return the x
	 */
	public float getX() {
		return this.centerX();
	}

	// ----------------------------------------------------------
	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(float x) {
		this.x = x;
		updateBounds();
	}

	// ----------------------------------------------------------
	/**
	 * @return the y
	 */
	public float getY() {
		return this.centerY();
	}

	// ----------------------------------------------------------
	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(float y) {
		this.y = y;
		updateBounds();
	}

	// ----------------------------------------------------------
	/**
	 * @return the size
	 */
	public float getSize() {
		return size;
	}

	// ----------------------------------------------------------
	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(float size) {
		this.size = size;
		updateBounds();
	}

	public void adjustPosition(int deltaT) {
		this.offset(0, vy * deltaT / 1000);
		y += vy * deltaT / 1000;
	}

	public boolean isMoving() {
		return Math.abs(vy) > 0.01f;
	}

	public Collision intersects(RectF collided) {
		Collision minimumIntersectIndex = Collision.NONE;
		float minimumIntersect = Float.MAX_VALUE;
		if (this.bottom < collided.top
				&& this.bottom > collided.bottom
				&& ((this.right < collided.right && this.right > collided.left)
						|| (this.left > collided.left && this.left < collided.right) 
						|| (this.right > collided.right && this.left < collided.left))) {
			float intersectBot = collided.top - this.bottom;
			float intersectRight = this.right - collided.left;
			float intersectLeft = collided.right - this.left;
			if (intersectBot > 0 && intersectBot < minimumIntersect) {
				minimumIntersectIndex = Collision.BOTTOM;
				minimumIntersect = intersectBot;
			}
			if (intersectRight > 0 && intersectRight < minimumIntersect) {
				minimumIntersectIndex = Collision.RIGHT;
				minimumIntersect = intersectRight;
			}
			if (intersectLeft > 0 && intersectLeft < minimumIntersect) {
				minimumIntersectIndex = Collision.LEFT;
				minimumIntersect = intersectLeft;
			}
		}
		return minimumIntersectIndex;
	}

	public void fixIntersection(RectF other, Collision whichSide) {
		float amount;
		switch (whichSide) {
			case RIGHT:
				amount = other.left - this.right - 30.5f;
				offset(amount, 0);
				x += amount;
				break;
			case BOTTOM:
				amount = other.top - this.bottom + 0.5f;
				offset(0, amount);
				vy = 0;
				y += amount;
				break;
			case LEFT:
				amount = other.right - this.left + 30.5f;
				offset(amount, 0);
				x += amount;
				break;
			case TOP:
			case SWITCH:
			case NONE:
			default:
				break;
		}
	}

}
