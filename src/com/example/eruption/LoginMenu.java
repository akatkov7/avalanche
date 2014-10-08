package com.example.eruption;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

}
