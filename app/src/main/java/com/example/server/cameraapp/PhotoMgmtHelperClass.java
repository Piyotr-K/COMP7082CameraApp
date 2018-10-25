package com.example.server.cameraapp;

import android.content.Context;
import android.media.ExifInterface;
import java.net.URI;
import android.util.Log;

import com.example.server.cameraapp.database.Photograph;
import com.example.server.cameraapp.database.PhotographDbHelper;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PhotoMgmtHelperClass {
    private Context ctx;
    private PhotographDbHelper db;
    private ExifInterface exif;
    private final String DEFAULT_CAPTION = "default_caption";

    public PhotoMgmtHelperClass(Context ctx) {
        this.ctx = ctx;
        db = new PhotographDbHelper(ctx, null, null, 1);
    }

    public List<String> getPhotos(Date minDate, Date maxDate) {
        List<String> value = new ArrayList<String>();
        value = db.filterByDateHandler(new Long(minDate.getTime()), new Long(maxDate.getTime()));
        return value;
    }

    public Date getPhotoDate(int id) {
        id++;
        if (db.loadDateByIdHandler(id) != null)
        {
            return new Date(db.loadDateByIdHandler(id));
        }
        return null;
    }

    public String getPhotoCaption(int id) {
        id++;
        return db.loadCaptionByIdHandler(id);
    }

    public void save(String photoPath) {
        Float locLat = 0.0f;
        Float locLong = 0.0f;
        try {
            exif = new ExifInterface(photoPath);
            locLat = Float.parseFloat(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
            locLong = Float.parseFloat(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
        } catch (IOException e) {
            Log.d("db save","IO Exception");
        } catch (NullPointerException e) {
            Log.d("db save", "NullException");
        }
        Date photoDate = getDateFromFilePath(photoPath);
        Long dateMillis = photoDate.getTime();
        Photograph photo = new Photograph(photoPath, locLat, locLong, dateMillis, DEFAULT_CAPTION);
        db.addHandler(photo);
    }

    public boolean updateCaption(int id, String caption) {
        return db.updateHandler(id, caption);
    }

    public static Date getDateFromFilePath(String path)
    {
        Date dd = null;
        String fileName = path.substring(path.lastIndexOf("/")+1);
        String fileDate = fileName.substring(fileName.indexOf('_') + 1, fileName.indexOf('_', fileName.indexOf('_') + 1));
        dd = getDateFromString(fileDate);
        return dd;
    }

    public static Date getDateFromString(String str) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            Log.d("date parse", e.getMessage());
        }
        return date;
    }
}
