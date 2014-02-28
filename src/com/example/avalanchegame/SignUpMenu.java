package com.example.avalanchegame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public class SignUpMenu
    extends Activity
{
    private static final Pattern VALID_USERNAME_REGEX =
                                                          Pattern
                                                              .compile(
                                                                  "^[A-Z0-9._]+$",
                                                                  Pattern.CASE_INSENSITIVE);
    private EditText             nameField;
    private EditText             usernameField;
    private EditText             passwordField;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signupmenu);

        nameField = (EditText)findViewById(R.id.nameField);
        usernameField = (EditText)findViewById(R.id.usernameField);
        passwordField = (EditText)findViewById(R.id.passwordField);
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }


    public void signUp(View v)
    {
        String name = nameField.getText().toString();
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        // check if any are blank
        if (!(name.equals("") || username.equals("") || password.equals("")))
        {
            // check the password is long enough
            if (password.length() >= 6)
            {
                // check the username is long enough
                if (username.length() >= 4 || username.length() <= 16)
                {
                    // check that username is valid
                    if (VALID_USERNAME_REGEX.matcher(username).find())
                    {
                        // TODO: actually sign up
                        // new AsyncURLLoader().execute();
                        Intent i = new Intent(this, LoginMenu.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        Toast
                            .makeText(
                                this,
                                "Please only use alphanumeric characters (. _ also allowed).",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(
                        this,
                        "Your username must be between 4 and 16 characters.",
                        Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(
                    this,
                    "Your password must be at least 6 characters.",
                    Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(
                this,
                "Please enter all your information.",
                Toast.LENGTH_SHORT).show();
        }
    }
}
