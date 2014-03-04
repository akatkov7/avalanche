package com.example.avalanchegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

// -------------------------------------------------------------------------
/**
 * The pause screen that opens when the phone is locked or when the user presses
 * back on their device. Provides the option to exit the game or to resume play.
 * Used to maintain the state of the game on device lock.
 */
public class PauseScreen
    extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pausescreen);
    }


    public void onBackPressed()
    {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


    // ----------------------------------------------------------
    /**
     * If the user wishes to return to the game, then simply invoke the back
     * press which would return to the previous activity (the game).
     */
    public void backToGame(View v)
    {
        onBackPressed();
    }


    // ----------------------------------------------------------
    /**
     * If the user wishes to exit the game, then send the user to the main menu
     */
    public void exit(View v)
    {
        Intent i = new Intent(this, GameSelect.class);
        setResult(2);
        startActivity(i);
        finish();
    }
}
