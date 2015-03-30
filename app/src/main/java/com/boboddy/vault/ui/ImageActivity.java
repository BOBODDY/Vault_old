package com.boboddy.vault.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.boboddy.vault.R;

public class ImageActivity extends ActionBarActivity {

    ImageView mainImage;

    private String filepath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mainImage = (ImageView) findViewById(R.id.mainImage);

        filepath = getIntent().getExtras().getString("filepath");
    }

    @Override
    protected void onStart() {
        super.onStart();

        filepath = getIntent().getExtras().getString("filepath");

        if(!filepath.equals("")) {
            Bitmap bmp = BitmapFactory.decodeFile(filepath);
            if(bmp != null) {
                //TODO: scale image if necessary
                mainImage.setImageBitmap(bmp);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);
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
