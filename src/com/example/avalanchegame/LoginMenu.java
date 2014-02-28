package com.example.avalanchegame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public class LoginMenu
    extends Activity
{
    private EditText usernameField;
    private EditText passwordField;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginmenu);

        usernameField = (EditText)findViewById(R.id.usernameField);
        passwordField = (EditText)findViewById(R.id.passwordField);

        if (getIntent().getStringExtra("username") != null)
            usernameField.setText(getIntent().getStringExtra("username"));
    }


    public void login(View v)
    {
        if (!(usernameField.getText().toString().equals("") || passwordField
            .getText().toString().equals("")))
        {
            // TODO: actually login
            // new AsyncURLLoader().execute();
            Intent i = new Intent(this, GameSelect.class);
            this.setResult(3);
            startActivity(i);
            finish();
        }
        else
        {
            Toast.makeText(
                this,
                "Please enter all your information.",
                Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

}
