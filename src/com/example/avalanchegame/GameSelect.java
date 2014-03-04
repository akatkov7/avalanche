package com.example.avalanchegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GameSelect
    extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameselect);
    }

    public void launchSinglePlayer(View v)
    {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
