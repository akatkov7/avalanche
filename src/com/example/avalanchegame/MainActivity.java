package com.example.avalanchegame;

import android.content.SharedPreferences;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import com.example.avalanchegame.CustomSurfaceView.GameThread;

public class MainActivity
    extends Activity
    implements SensorEventListener
{
    public final static String PREF_FILE = "EruptionGameState";

    private CustomSurfaceView  screenContainer;
    private GameThread         gameThread;
    private SensorManager      sensorManager;
    private Sensor             accelerometer;
    private Button             startGame;


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

        // Initialize framework for getting accelerometer tilt events.
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer =
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        startGame = (Button)findViewById(R.id.startGame);
        screenContainer = (CustomSurfaceView)findViewById(R.id.screenContainer);
        gameThread = screenContainer.getThread();

        SharedPreferences prefs =
            getSharedPreferences(MainActivity.PREF_FILE, 0);
        // prefs.edit().putBoolean("gameSaved", false).commit();

        if (prefs.getBoolean("gameSaved", false))
        {
            screenContainer.post(new Runnable() {

                public void run()
                {
                    // TODO Auto-generated method stub
                    System.out.println("restoring");
                    startGame.setText("Resume");
                    gameThread.restoreState();
                }
            });
        }
    }


    public void startGame(View v)
    {
        gameThread.start();
        v.setVisibility(View.GONE);
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
        if (gameThread != null)
            gameThread.saveState();
        Intent i = new Intent(this, PauseScreen.class);
        startActivityForResult(i, 2);
        finish(); // this will stop the thread (i think)
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL);
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
        if (gameThread != null)
            gameThread.onSensorChanged(event);
    }

}
