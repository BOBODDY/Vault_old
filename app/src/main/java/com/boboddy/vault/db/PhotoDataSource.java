package com.boboddy.vault.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.boboddy.vault.db.DatabaseHelper;
import com.boboddy.vault.model.PhotoModel;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by nick on 3/6/15.
 */
public class PhotoDataSource {
    SQLiteDatabase database;
    DatabaseHelper dbHelper;

    private String[] columns = {
            DatabaseHelper.PHOTOS_ID,
            DatabaseHelper.PHOTOS_PATH,
            DatabaseHelper.PHOTOS_DATA
    };

    public PhotoDataSource(Context c) {
        dbHelper = new DatabaseHelper(c);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public PhotoModel insertPhoto(PhotoModel photoModel) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.PHOTOS_PATH, photoModel.getFilepath());
        cv.put(DatabaseHelper.PHOTOS_DATA, photoModel.getData());

        long id = database.insert(DatabaseHelper.TABLE_PHOTOS, null, cv);

        Cursor cursor = database.query(DatabaseHelper.TABLE_PHOTOS, columns,
                DatabaseHelper.PHOTOS_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();

        PhotoModel photo = cursorToPhotoModel(cursor);

        cursor.close();
        return photo;
    }

    public ArrayList<PhotoModel> getPhotoList() {
        ArrayList<PhotoModel> pictures = new ArrayList<PhotoModel>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_PHOTOS, columns,
                null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            PhotoModel photo = cursorToPhotoModel(cursor);
            pictures.add(photo);
            cursor.moveToNext();
        }
        cursor.close();
        return pictures;
    }

    public PhotoModel cursorToPhotoModel(Cursor cursor) {
        PhotoModel photoModel = new PhotoModel();
        photoModel.set_id(cursor.getLong(0));
        photoModel.setFilepath(cursor.getString(1));
        photoModel.setData(cursor.getBlob(2));
        return photoModel;
    }

    public void deletePhoto(PhotoModel photoModel) {
        long id = photoModel.get_id();
        database.delete(DatabaseHelper.TABLE_PHOTOS, DatabaseHelper.PHOTOS_ID + " = " + id, null);
    }
}
