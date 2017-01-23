package com.example.eruption;

import android.graphics.RectF;

public interface Collidable {

	public enum Collision {
		NONE, TOP, RIGHT, BOTTOM, LEFT, SWITCH
	};

	public Collision intersects(RectF collided);

	public void fixIntersection(RectF other, Collision whichSide);

	public void adjustPosition(int deltaT);
}
