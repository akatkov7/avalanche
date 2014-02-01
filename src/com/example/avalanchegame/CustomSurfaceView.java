package com.example.avalanchegame;

import java.util.ArrayList;
import java.util.List;
import android.hardware.SensorEvent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
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
public class CustomSurfaceView
    extends SurfaceView
    implements SurfaceHolder.Callback
{

    // -------------------------------------------------------------------------
    /**
     * This is the thread on which the game runs. The thread will be
     * continuously updating the screen and redrawing the game.
     *
     * @author Andriy
     * @version Jan 10, 2014
     */
    class GameThread
        extends Thread
    {
        /*
         * These are used for frame timing
         */
        private final static int MAX_FPS         = 50;
        private final static int MAX_FRAME_SKIPS = 5;
        private final static int FRAME_PERIOD    = 1000 / MAX_FPS;

        /*
         * State-tracking constants These are from the game i copied this from,
         * should probably make this an enum if we use it
         */
        public static final int  STATE_LOSE      = 1;
        public static final int  STATE_PAUSE     = 2;
        public static final int  STATE_READY     = 3;
        public static final int  STATE_RUNNING   = 4;
        public static final int  STATE_WIN       = 5;

        /** The drawable to use as the background of the animation canvas */
        private Bitmap           mBackgroundImage;

        /**
         * Current height of the surface/canvas.
         *
         * @see #setSurfaceSize
         */
        private int              mCanvasHeight   = 1920;                                  // 1;

        /**
         * Current width of the surface/canvas.
         *
         * @see #setSurfaceSize
         */
        private int              mCanvasWidth    = 1080;                                  // 1;

        /** the current scroll offset */
        private int              scrollX         = 0;

        /** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
        private int              mMode;

        /** Indicate whether the surface has been created & is ready to draw */
        private boolean          mRun            = false;

        /** Prevents multiple threads from accessing the canvas */
        private final Object     mRunLock        = new Object();

        /** Handle to the surface manager object we interact with */
        // this is how we get the canvas
        private SurfaceHolder    mSurfaceHolder;

        /** Temporary green paint for grass */
        private Paint            mGrassPaint     = new Paint();
        /** Temporary rect for the grass */
        private RectF            mGrassRect      = new RectF(
                                                     0,
                                                     (int)(.8 * mCanvasHeight),
                                                     mCanvasWidth,
                                                     mCanvasHeight);

        private Paint            mBlackPaint        = new Paint();
        private boolean          grounded        = false;
        private Player           player          =
                                                     new Player(
                                                         new RectF(
                                                             mCanvasWidth / 2 - 50,
                                                             (int)(.8 * mCanvasHeight) - 100 - 600,
                                                             mCanvasWidth / 2 + 50,
                                                             (int)(.8 * mCanvasHeight) - 600),
                                                         1080,
                                                         1920);
        private List<Box> boxes = new ArrayList<Box>();
        /**
         * Defines the N predominant column structures that govern where new
         * boxes spawn.
         */
        private Box[] columns = new Box[4];

        private int minWidth = 2 * (mCanvasWidth / 30); // Even number
        private int maxWidth = 2 * (mCanvasWidth / 15); // Even number

        private RectF testBlock = new RectF(mCanvasWidth / 2 - 150,
            (int)(.8 * mCanvasHeight) - 300 - 300,
            mCanvasWidth / 2 + 150,
            (int)(.8 * mCanvasHeight) - 300);
        private RectF[] listOfBlocks = {mGrassRect, testBlock};

        private long lastTime = System.currentTimeMillis();


        // ----------------------------------------------------------
        /**
         * Create a new GameThread object.
         *
         * @param surfaceHolder
         */
        public GameThread(SurfaceHolder surfaceHolder)
        {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;

            // load background image as a Bitmap instead of a Drawable b/c
            // we don't need to transform it and it's faster to draw this way
// mBackgroundImage =
// BitmapFactory.decodeResource(
// getContext().getResources(),
// R.drawable.gamebackground);

            mGrassPaint.setColor(Color.GREEN);
            mGrassPaint.setStyle(Style.FILL);

            mBlackPaint.setColor(Color.BLACK);
            mBlackPaint.setStyle(Style.FILL);
        }

        private int randInt(int min, int max) {
            return (int) (Math.random() * (max - min) + min);
        }
        public void generateInitialBoxes() {
            for (int i = 0; i < columns.length; i++) {
                int width = randInt(minWidth, maxWidth) * 2;
                int x = randInt(0, mCanvasWidth);
                boolean collisions = true;
                while (collisions) {
                    collisions = false;
                    for (Box rect : columns) {
                        if (rect == null) continue;
                        if (x + width/2 > rect.left && x - width/2 < rect.right) {
                            x = randInt(0, mCanvasWidth);
                            collisions = true;
                            break;
                        }
                    }
                }

                Box box = new Box(x, width / 2, width);
                boxes.add(box);
                columns[i] = box;
            }
        }

        /**
         * @todo make it not spawn blocks off the screen
         * (when generating random x, cap the minimum and maximum x coordinate)
         * @todo figure out why it spawns them inside each other
         */
        public void generateNextBox() {
            int width = randInt(minWidth, maxWidth) * 2;

            // Pick a column to add a rectangle to
            // Not purely random, somewhat weighted by how low the highest box
            // in each column is (prefer adding boxes to shorter columns)
            float[] possibilities = new float[columns.length];
            final int PAD = 5;

            float highestColumnBoxHeight = 0f;
            for (int i = 0; i < columns.length; i++) {
                Box prect = columns[i];

                for (Box poss : columns) {
                    if (poss.getY() > highestColumnBoxHeight) {
                        highestColumnBoxHeight = poss.getY();
                    }
                }
                float p = prect.getY()+ randInt(0,
                    (int) (highestColumnBoxHeight - prect.getY() + PAD));
                possibilities[i] = p;
            }

            int columnIndex = 0;
            float smallestPossibility = Float.MAX_VALUE;
            for (int i = 0; i < possibilities.length; i++) {
                if (possibilities[i] < smallestPossibility) {
                    columnIndex = i;
                    smallestPossibility = possibilities[i];
                }
            }

            Box baseBox = columns[columnIndex];

            // Create a new box on top of baseBox
            System.out.println("" + baseBox.top + " " + (baseBox.getY() + baseBox.getSize() / 2));
            float y = baseBox.top + width/2;
            float x = randInt((int) (baseBox.left - width/2 + 1),
                (int) (baseBox.right + width/2 - 1));
            Box newBox = new Box(x, y, width);

            // Add this box to the list of boxes & replace baseBox as the column head
            boxes.add(newBox);
            columns[columnIndex] = newBox;
        }


        /**
         * Starts the game, setting parameters for the current difficulty.
         * Copied from old game, might use or not
         */
        public void doStart()
        {
            synchronized (mSurfaceHolder)
            {
                setState(STATE_RUNNING);
            }
        }


        /**
         * Pauses the physics update & animation.
         */
        public void pause()
        {
            synchronized (mSurfaceHolder)
            {
                if (mMode == STATE_RUNNING)
                    setState(STATE_PAUSE);
            }
        }


        /**
         * Restores game state from the indicated Bundle. Typically called when
         * the Activity is being restored after having been previously
         * destroyed.
         *
         * @param savedState
         *            Bundle containing the game state
         */
        public synchronized void restoreState(Bundle savedState)
        {
            synchronized (mSurfaceHolder)
            {
                setState(STATE_PAUSE);
            }
        }


        private long beginTime; // the time when the cycle began
        @Override
        public void run()
        {
            long timeDiff; // the time it took for the cycle to execute
            int sleepTime = 0; // ms to sleep (<0 if we're behind)
            int framesSkipped; // number of frames being skipped

            // while its running, which is determined by the mode constants
            // defined at the beginning
            while (mRun)
            {
                Canvas c = null;
                try
                {
                    // get a reference to the canvas
                    c = mSurfaceHolder.lockCanvas();
                    synchronized (mSurfaceHolder)
                    {
                        beginTime = System.currentTimeMillis();
                        framesSkipped = 0;
                        /*
                         * Critical section. Do not allow mRun to be set false
                         * until we are sure all canvas draw operations are
                         * complete. If mRun has been toggled false, inhibit
                         * canvas operations.
                         */
                        synchronized (mRunLock)
                        {
                            // if its running, update the canvas through doDraw
                            if (mRun)
                            {
                                updateLogic(); // moves everything without
                                // drawing it yet (basically a buffer)
                                doDraw(c); // renders everything

                                timeDiff =
                                    System.currentTimeMillis() - beginTime;
                                sleepTime = (int)(FRAME_PERIOD - timeDiff);

                                if (sleepTime > 0)
                                {
                                    try
                                    {
                                        Thread.sleep(sleepTime);
                                    }
                                    catch (InterruptedException e)
                                    {
                                    }
                                }
                                while (sleepTime < 0
                                    && framesSkipped < MAX_FRAME_SKIPS)
                                {
                                    // catch up, so update without rendering
                                    updateLogic();
                                    sleepTime += FRAME_PERIOD;
                                    framesSkipped++;
                                }
                            }
                        }
                    }
                }
                finally
                {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null)
                    {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }


        /**
         * Dump game state to the provided Bundle. Typically called when the
         * Activity is being suspended.
         *
         * @return Bundle with this view's state
         */
        public Bundle saveState(Bundle map)
        {
            synchronized (mSurfaceHolder)
            {
                if (map != null)
                {
                    // Intentionally left blank, to be filled later
                }
            }
            return map;
        }


        /**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         *
         * @param b
         *            true to run, false to shut down
         */
        public void setRunning(boolean b)
        {
            // Do not allow mRun to be modified while any canvas operations
            // are potentially in-flight. See doDraw().
            synchronized (mRunLock)
            {
                mRun = b;
            }
        }


        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         *
         * @param mode
         *            one of the STATE_* constants
         */
        public void setState(int mode)
        {
            synchronized (mSurfaceHolder)
            {
                mMode = mode;
            }
        }


        /** Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height)
        {
            Log.d("CHANGING SURFACE SIZE", width + ", " + height);
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder)
            {
                mCanvasWidth = width;
                mCanvasHeight = height;

                // don't forget to resize the background image
// mBackgroundImage =
// Bitmap.createScaledBitmap(
// mBackgroundImage,
// width,
// height,
// true);
            }
        }


        /**
         * Resumes from a pause.
         */
        public void unpause()
        {
            // Move the real time clock up to now
            synchronized (mSurfaceHolder)
            {
            }
            setState(STATE_RUNNING);
        }

        boolean firstTime = true;
        /**
         * TODO: FILL THIS IN WITH GAME LOGIC. (move, attack, etc)
         */
        private void updateLogic()
        {
            if (firstTime) {
                generateInitialBoxes();
                for (int i = 0; i < 5; i++) {
                    generateNextBox();
                }
            }
            firstTime = false;
            player.adjustPosition((int)(System.currentTimeMillis() - lastTime));
            // mBlockRect.offset(0, mCanvasHeight / 200);

            long timeElapsed = lastTime - beginTime;

            for (RectF brock : listOfBlocks)
                if (player.intersects(brock) > -1)
                {
                    // Log.d("INTERSECTION", "IM INTERSECTING " + player.intersects(brock));
                    player.fixIntersection(brock, player.intersects(brock));
                    // mBlockRect.bottom = mGrassRect.top;
                    // mBlockRect.top = mBlockRect.bottom - 100;
                    grounded = true;
                }

            lastTime = System.currentTimeMillis();
            // Log.d("HIT", mBlockRect.flattenToString());
        }


        /**
         * Draws the minions, towers, and background to the provided Canvas.
         */
        private void doDraw(Canvas canvas)
        {
            // Log.d("doDraw", "drawing");
            // Draw the background image. Operations on the Canvas accumulate
            // so this is like clearing the screen.
            // canvas.drawBitmap(mBackgroundImage, 0, 0, null);
            canvas.drawColor(Color.WHITE);
            canvas.drawRect(mGrassRect, mGrassPaint);
            canvas.drawRect(testBlock, mBlackPaint);
            player.draw(canvas);

            for (Box box : boxes) {
                box.draw(canvas);
            }
            // canvas.drawRect(mBlockRect, mBlockPaint);
        }

        /**
         * variables to store previous touch down positions as well as calculate
         * scroll
         */
        private float downX, downY, currentX;


        // ----------------------------------------------------------
        /**
         * This is where we will check where the user touches and respond
         * accordingly
         *
         * @param e
         *            the motion event
         * @return true
         */
        public boolean onTouchEvent(MotionEvent e)
        {
            synchronized (mSurfaceHolder)
            {
                switch (e.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("onTouch", "touch down");
                        if (grounded)
                        {
                            player.jump();
                            // mBlockRect.offset(0, -mCanvasHeight / 15);
                            grounded = false;
                        }
                        break;
                }
                return true;
            }
        }


        public void onSensorChanged(SensorEvent event)
        {
            // TODO Auto-generated method stub
            player.setXAccel(event.values[0]);

        }
    }

    /** The thread that actually draws the animation */
    private GameThread thread;


    // ----------------------------------------------------------
    /**
     * Create a new CustomSurfaceView object.
     *
     * @param context
     * @param attrs
     */
    public CustomSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new GameThread(holder);

        setFocusable(true); // make sure we get key events
    }


    /**
     * Fetches the animation thread corresponding to this LunarView.
     *
     * @return the animation thread
     */
    public GameThread getThread()
    {
        return thread;
    }


    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        return thread.onTouchEvent(e);
    }


    /**
     * Standard window-focus override. Notice focus lost so we can pause on
     * focus lost. e.g. user switches to take a call.
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus)
    {
        if (!hasWindowFocus)
            thread.pause();
    }


    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(
        SurfaceHolder holder,
        int format,
        int width,
        int height)
    {
        // width * 2 because our background is twice the width of the screen
        thread.setSurfaceSize(width, height);
    }


    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder)
    {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }


    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry)
        {
            try
            {
                thread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {
            }
        }
    }
}
