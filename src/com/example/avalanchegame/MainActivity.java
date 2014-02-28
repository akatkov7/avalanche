package com.example.avalanchegame;

import android.content.Intent;
import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;
import com.example.avalanchegame.CustomSurfaceView.GameThread;

public class MainActivity
    extends Activity
    implements SensorEventListener
{

    private CustomSurfaceView screenContainer;
    private GameThread        gameThread;
    private SensorManager     sensorManager;
    private Sensor            accelerometer;
    private boolean           firstLoad;


    /**
     * Invoked when the Activity is created.
     *
     * @param savedInstanceState
     *            a Bundle containing state saved from a previous execution, or
     *            null if this is a new execution
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
        {
            gameThread.restoreState(savedInstanceState);
        }

        // tell system to use the layout defined in our XML file
        setContentView(R.layout.activity_main);

        firstLoad = true;

        // lock in portrait mode? TODO: test this, really diafetus, landscape?
// super
// .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initialize framework for getting accelerometer tilt events.
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer =
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        screenContainer = (CustomSurfaceView)findViewById(R.id.screenContainer);
        gameThread = screenContainer.getThread();
        // gameThread.doStart();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
        gameThread.saveState(outState);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2)
        {
            finish();
        }
    }


    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
        screenContainer.getThread().pause(); // pause game when Activity pauses
        Intent i = new Intent(this, PauseScreen.class);
        startActivityForResult(i, 2);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL);
        System.out.println(gameThread.isRunning());
        screenContainer.getThread().unpause();// TODO:make this open paused
// menu
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // TODO Auto-generated method stub
        return;
    }


    public void onSensorChanged(SensorEvent event)
    {
        // TODO Auto-generated method stub

        gameThread.onSensorChanged(event);
    }

}
