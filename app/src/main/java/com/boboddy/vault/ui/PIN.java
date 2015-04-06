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
import android.widget.EditText;
import android.widget.TextView;

import com.boboddy.vault.R;
import com.boboddy.vault.ui.MainActivity;
import com.boboddy.vault.ui.NewPinActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class PIN extends Activity {

    TextView title;
    EditText pin;

    final String PREFS_NAME = "VaultPreferences";

    final static int CREATE_PIN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
		
		// check to see if this is the first run and offer to set up a PIN
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        if(prefs.getBoolean("first_run", true)) {
            Log.d("Vault", "First run!");

            Intent newPinActivity = new Intent(this, NewPinActivity.class);
            startActivity(newPinActivity);

            prefs.edit().putBoolean("first_run", false).apply();
        }


        title = (TextView) findViewById(R.id.unlock_title);
        pin = (EditText) findViewById(R.id.pin_edittext);
        pin.setGravity(Gravity.CENTER);
        pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String enteredPin = s.toString();

                if(checkPin(enteredPin)) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);

                    //Done with the PIN
                    finish();
                } else {
                    if(enteredPin.length() > 3) {
                        title.setText(R.string.wrong_pin);
                        title.setTextColor(Color.RED);
                    }
                }
            }
        });
    }

    private boolean checkPin(String pin) {
        boolean res = true;

        if(pin.length() == 4) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);

            String storedHash = prefs.getString("user_pin", "");

            if (storedHash.equals("")) {
                res = false;
            }
            String inputHash = "";
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(pin.getBytes());
                inputHash = new String(digest.digest());
            } catch (NoSuchAlgorithmException e) {
                Log.e("Vault", "error hashing inputted pin", e);
            }

            if (storedHash.equals(inputHash)) {
                res = true;
            }
        } else {
            res = false;
        }

        return res;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pin, menu);
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
