package com.example.eruption;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

// -------------------------------------------------------------------------
/**
 * The view on which the game will take place.
 * 
 * @author Andriy
 * @version Jan 10, 2014
 */
public class GameSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {

	/** The thread that actually draws the animation */
	private GameThread thread;
	private Context context;

	public GameSurfaceView(Context context) {
		super(context);
		this.context = context;
		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		setFocusable(true); // make sure we get key events
	}

	/**
	 * Fetches the animation thread corresponding to this LunarView.
	 * 
	 * @return the animation thread
	 */
	public GameThread getThread() {
		return thread;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return thread.onTouchEvent(e);
	}

	/* Callback invoked when the surface dimensions change. */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);
	}

	/*
	 * Callback invoked when the Surface has been created and is ready to be
	 * used.
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		// start the thread here so that we don't busy-wait in run()
		// waiting for the surface to be created
		// if (thread.getState() == Thread.State.TERMINATED)
		// {
		// System.out.println("creating a new thread because terminated");
		// thread = new GameThread(getHolder());
		// }
		System.out.println("NEW THREAD");
		thread = new GameThread(getHolder());

		// SharedPreferences prefs = context.getSharedPreferences(
		// MainActivity.PREF_FILE, 0);
		// if (prefs.getBoolean("gameSaved", false)) {
		// System.out.println("restoring");
		// thread.restoreState();
		// }
		thread.setRunning(true);
		thread.start();
	}

	/*
	 * Callback invoked when the Surface has been destroyed and must no longer
	 * be touched. WARNING: after this method returns, the Surface/Canvas must
	 * never be touched again!
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		System.out.println("TIME TO DIE!");
		boolean retry = true;
		thread.setRunning(false);
		thread.saveState();
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// don't worry about it
			}
		}
	}

}
