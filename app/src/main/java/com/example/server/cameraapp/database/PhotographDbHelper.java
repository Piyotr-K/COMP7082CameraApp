package com.example.server.cameraapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PhotographDbHelper extends SQLiteOpenHelper {
    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "photographDB.db";
    public static final String TABLE_NAME = "Photograph";
    public static final String COLUMN_ID = "photoID";
    public static final String COLUMN_PATH = "photoPath";
    public static final String COLUMN_LAT = "locLat";
    public static final String COLUMN_LONG = "locLong";
    public static final String COLUMN_DATE = "photoDate";
    public static final String COLUMN_CAP = "photoCaption";

    //initialize the database
    public PhotographDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + COLUMN_PATH + " TEXT,"
                + COLUMN_LAT + " REAL,"
                + COLUMN_LONG + " REAL,"
                + COLUMN_DATE + " INT,"
                + COLUMN_CAP + " TEXT )";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public List<String> loadPathsHandler() {
        List<String> result = new ArrayList<String>();
        String query = "Select * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            result.add(cursor.getString(1));
        }
        cursor.close();
        db.close();
        return result;
    }

    public Long loadDateByIdHandler(int id) {
        Long result = null;

        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = '" + id + "'";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst())
        {
            c.moveToFirst();
            result = c.getLong(4);
        }

        c.close();
        db.close();

        return result;
    }

    public String loadCaptionByIdHandler(int id) {
        String result = null;

        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = '" + id + "'";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst())
        {
            c.moveToFirst();
            result = c.getString(5);
        }

        c.close();
        db.close();

        return result;
    }

    public void addHandler(Photograph photo) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PATH, photo.getPhotoPath());
        values.put(COLUMN_LAT, photo.getLocLat());
        values.put(COLUMN_LONG, photo.getLocLong());
        values.put(COLUMN_DATE, photo.getPhotoDate());
        values.put(COLUMN_CAP, photo.getPhotoCaption());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<String> filterByDateHandler(Long dateFrom, Long dateTo) {
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " BETWEEN " + "'" + dateFrom + "' AND '" + dateTo + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> photos = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            while (cursor.moveToNext()) {
                photos.add(cursor.getString(1));
            }
            cursor.close();
        }
        db.close();
        try {
            Log.d("Db filterbydate result", "Photos size: " + photos.size());
        } catch (NullPointerException e) {
            Log.d("Db filterbydate result", "NULL");
        }
        return photos;
    }

    public List<Photograph> filterByLocationHandler(Float locLat, Float locLong) {
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_LAT + " = '" + locLat + "' AND " + COLUMN_LONG + " = '" + locLong + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Photograph> photos = new ArrayList<Photograph>();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            while (cursor.moveToNext()) {
                Photograph photo = new Photograph();
                photo.setID(Integer.parseInt(cursor.getString(0)));
                photo.setPhotoPath(cursor.getString(1));
                photo.setLocLat(cursor.getFloat(2));
                photo.setLocLong(cursor.getFloat(3));
                photo.setPhotoDate(cursor.getLong(4));
                photo.setPhotoCaption(cursor.getString(5));
                photos.add(photo);
            }
            cursor.close();
        }
        db.close();
        try {
            Log.d("Db filterbyloc result", "Photos size: " + photos.size());
        } catch (NullPointerException e) {
            Log.d("Db filterbyloc result", "NULL");
        }
        return photos;
    }

    public List<Photograph> filterByCaptionHandler(String caption) {
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_CAP + " = '" + caption + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Photograph> photos = new ArrayList<Photograph>();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            while (cursor.moveToNext()) {
                Photograph photo = new Photograph();
                photo.setID(Integer.parseInt(cursor.getString(0)));
                photo.setPhotoPath(cursor.getString(1));
                photo.setLocLat(cursor.getFloat(2));
                photo.setLocLong(cursor.getFloat(3));
                photo.setPhotoDate(cursor.getLong(4));
                photo.setPhotoCaption(cursor.getString(5));
                photos.add(photo);
            }
            cursor.close();
        } else {
            photos = null;
        }
        db.close();
        try {
            Log.d("Db filterbycap result", "Photos size: " + photos.size());
        } catch (NullPointerException e) {
            Log.d("Db filterbycap result", "NULL");
        }
        return photos;
    }

    public boolean updateHandler(int ID, String caption) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_ID, ID);
        args.put(COLUMN_CAP, caption);
        return db.update(TABLE_NAME, args, COLUMN_ID + "=" + ID, null) > 0;
    }
}
