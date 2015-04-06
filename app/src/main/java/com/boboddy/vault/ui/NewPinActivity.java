package com.boboddy.vault.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.boboddy.vault.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NewPinActivity extends Activity {

    Button doneButton;
    EditText pin1;
    EditText pin2;
    TextView enterPin;
    TextView reenterPin;

    final String PREFS_NAME = "VaultPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pin);

        pin1 = (EditText) findViewById(R.id.pin1);
        pin1.setGravity(Gravity.CENTER);

        pin2 = (EditText) findViewById(R.id.pin2);
        pin2.setGravity(Gravity.CENTER);
        pin2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String base = pin1.getText().toString();
                String cvfm = s.toString();

                if(base.equals("")) {
                    enterPin.setTextColor(Color.RED);
                } else {
                    enterPin.setTextColor(Color.BLACK);
                }

                if(!cvfm.equals(base)) {
                    reenterPin.setTextColor(Color.RED);
                } else {
                    reenterPin.setTextColor(Color.BLACK);
                }

            }
        });

        enterPin = (TextView) findViewById(R.id.enterPin);

        reenterPin = (TextView) findViewById(R.id.reenterPin);

        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String base = pin1.getText().toString();
                String cnfm = pin2.getText().toString();

                if(!base.equals(cnfm)) {
                    enterPin.setTextColor(Color.RED);
                    reenterPin.setTextColor(Color.RED);
                } else {
                    String pin = pin2.getText().toString();
                    savePIN(pin);

                    Intent login = new Intent(getApplicationContext(), PIN.class);
                    startActivity(login);
                    finish();
                }
            }
        });
    }

    private void savePIN(String pin) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(pin.getBytes());
            String hashedPin = new String(digest.digest());
            prefs.edit().putString("user_pin", hashedPin).apply();
        } catch(NoSuchAlgorithmException e) {
            Log.e("Vault", "Error hashing PIN", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_pin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
