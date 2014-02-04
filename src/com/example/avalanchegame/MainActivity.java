package com.example.avalanchegame;

import android.util.Log;
import android.app.Activity;
import android.content.Context;
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

        // tell system to use the layout defined in our XML file
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SensorManager sensorManager;
        Sensor accelerometer;
        // Initialize framework for getting accelerometer tilt events.
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer =
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL);

        screenContainer = (CustomSurfaceView)findViewById(R.id.screenContainer);
        gameThread = screenContainer.getThread();

        gameThread.doStart();
    }


    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause()
    {
        screenContainer.getThread().pause(); // pause game when Activity pauses
        super.onPause();
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
