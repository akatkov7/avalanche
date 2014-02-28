package com.example.avalanchegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class TitleMenu
    extends Activity
{
    // TODO: change once we have a server
    public static final String SERVER = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.titlemenu);
    }


    @Override
    protected void onActivityResult(
        int requestCode,
        int resultCode,
        Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == 3)
        {
            finish();
        }
    }


    public void signUp(View v)
    {
        Intent i = new Intent(getApplicationContext(), SignUpMenu.class);
        startActivity(i);
    }


    public void login(View v)
    {
        Intent i = new Intent(getApplicationContext(), LoginMenu.class);
        startActivityForResult(i, 3);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
