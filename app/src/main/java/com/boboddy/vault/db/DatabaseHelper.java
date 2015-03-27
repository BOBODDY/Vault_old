package com.boboddy.vault.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by nick on 3/6/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_PHOTOS = "photos";
    public static final String PHOTOS_ID = "_id";
    public static final String PHOTOS_PATH = "path";
    public static final String PHOTOS_DATA = "data";

    private static final String DATABASE_NAME = "vault.db";
    private static final int DATABASE_VERSION = 5;
    private SQLiteDatabase database;

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_PHOTOS + " ("
            + PHOTOS_ID + " integer primary key autoincrement, "
            + PHOTOS_PATH + " text not null"
//            + PHOTOS_DATA + " blob not null"
            + ");";

    public DatabaseHelper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase database) {
        Log.d("Vault", "Creating database");

        database.execSQL(DATABASE_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Vault", "Upgrading database, destroying data");
        db.execSQL(DROP_TABLE + TABLE_PHOTOS);

        onCreate(db);
    }
}
