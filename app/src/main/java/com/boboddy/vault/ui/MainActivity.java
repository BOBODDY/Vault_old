package com.boboddy.vault.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.boboddy.vault.PhotoDataSource;
import com.boboddy.vault.R;
import com.boboddy.vault.model.PhotoModel;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;


public class MainActivity extends ActionBarActivity {

    ImageView image;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.image);

        mPhotoPath = "";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.add_item:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        Log.e("Vault", "COULD NOT CREATE IMAGE FILE", e);
//                        Log.e("Vault", e.getMessage());
                    }
                    if(photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Vault", "Photo path: " + mPhotoPath);
        if(!mPhotoPath.equals(""))
        {
            Bitmap imageBitmap = BitmapFactory.decodeFile(mPhotoPath);
            //Scale the Bitmap so that it fits on canvas.
            if(imageBitmap != null)
            {
                Bitmap scaled = imageBitmap.createScaledBitmap(imageBitmap, (imageBitmap.getWidth()/4), (imageBitmap.getHeight()/4),false);
                image.setImageBitmap(scaled);

                //Add the image to the database
                int numBytes = scaled.getByteCount();
                ByteBuffer buf = ByteBuffer.allocate(numBytes);
                scaled.copyPixelsToBuffer(buf);

                PhotoModel model = new PhotoModel();
                model.setFilepath(mPhotoPath);
                model.setData(buf.array());

                PhotoDataSource photoDataSource = new PhotoDataSource(this);
                try {
                    photoDataSource.open();
                    photoDataSource.insertPhoto(model);
                    photoDataSource.close();
                } catch(SQLException e) {
                    Log.d("Vault", "Caught a SQLException", e);
//                    Log.d("Vault", e.getMessage());
                }
                Log.d("Vault", "Inserted photo in db: " + mPhotoPath);
            }
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File storageDir = getFilesDir();

        File image = File.createTempFile("image", ".jpg", storageDir);
        mPhotoPath = image.getAbsolutePath();
//        File image;
//        image = new File(getFilesDir(), "image_" + Calendar.getInstance().getTimeInMillis() + ".jpg");
//        mPhotoPath = image.getAbsolutePath();
//        Log.d("Vault", "Image file created at " + mPhotoPath);
//        mPhotoPath = image.getAbsolutePath();
        return image;
    }
}
