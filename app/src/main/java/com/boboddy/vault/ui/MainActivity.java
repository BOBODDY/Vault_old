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

import com.boboddy.vault.db.PhotoDataSource;
import com.boboddy.vault.R;
import com.boboddy.vault.model.PhotoModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

    private Random rand;

    ImageView image;

    private String photoPath = "";

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.image);
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
                File f = null;
                try {
                    f = createExtImageFile();
                } catch (IOException e) {
                    Log.d("Vault", "Error creating image file", e);
                }
                if(f != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    takePictureIntent.putExtra("photo_path", f.getAbsolutePath());
                }

                if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
                   startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap;
            String filename = "";

            Log.d("Vault", "photoPath = " + photoPath);

            if(!photoPath.equals("")) {
                filename = photoPath;
            } else {
                Log.w("Vault", "No photo path specified");
                return;
            }

            imageBitmap = BitmapFactory.decodeFile(filename);
            if(imageBitmap != null)
            {
                //Scale the Bitmap so that it fits on canvas.
                Bitmap scaled = imageBitmap.createScaledBitmap(imageBitmap, (imageBitmap.getWidth()/4), (imageBitmap.getHeight()/4),false);
                image.setImageBitmap(scaled);

                File internalFile = null;
                try {
                    internalFile = createIntImageFile();
                } catch (IOException e) {
                    Log.w("Vault", "Error creating internal file", e);
                }

                if(internalFile != null) {
                    copyBitmapToFile(imageBitmap, internalFile);
                } else {
                    Log.d("Vault", "Internal file was null");
                    return;
                }

                File tmp = new File(filename);
                tmp.delete();

                //Add the image to the database
                int numBytes = imageBitmap.getByteCount();
                ByteBuffer buf = ByteBuffer.allocate(numBytes);
                imageBitmap.copyPixelsToBuffer(buf);

                PhotoModel model = new PhotoModel();
                model.setData(buf.array());
                model.setFilepath(internalFile.getAbsolutePath());

                PhotoDataSource photoDataSource = new PhotoDataSource(this);
//                try {
//                    photoDataSource.open();
//                    photoDataSource.insertPhoto(model);
//                    photoDataSource.close();
//                } catch(SQLException e) {
//                    Log.d("Vault", "Caught a SQLException", e);
////                    Log.d("Vault", e.getMessage());
//                }
                Log.d("Vault", "Would have inserted photo in db: " + filename);
            }
        }
    }

    private void copyBitmapToFile(Bitmap bmp, File dest) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dest);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch(FileNotFoundException e) {
            Log.w("Error copying bitmap to internal file", e);
        } finally {
            try {
                if(fos != null) {
                    fos.close();
                }
            } catch(IOException e) {
                Log.w("Vault", "Error closing output stream", e);
            }
        }
    }

    private File createIntImageFile() throws IOException {
        String filename = "image";
        if(rand == null) {
            rand = new Random();
        }
        long id = rand.nextLong();
        filename += id + ".png";
        Log.d("Vault", "Creating image: " + filename);

        File image = new File(getApplicationContext().getFilesDir(), filename);

//        photoPath = image.getAbsolutePath();
        return image;
    }

    private File createExtImageFile() throws IOException {
        String filename = "image";
        if(rand == null) {
            rand = new Random();
        }
        long id = rand.nextLong();
        filename += id;
        Log.d("Vault", "Creating image: " + filename);

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(filename, ".jpg", storageDir);

        photoPath = image.getAbsolutePath();
        return image;
    }
}
