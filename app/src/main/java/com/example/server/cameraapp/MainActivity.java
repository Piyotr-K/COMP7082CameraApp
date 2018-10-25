package com.example.server.cameraapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.server.cameraapp.PhotoMgmtHelperClass.getDateFromFilePath;
import static com.example.server.cameraapp.PhotoMgmtHelperClass.getDateFromString;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 0;
    static final int CAMERA_REQUEST_CODE = 1;
    private String currentPhotoPath = null;
    private int currentPhotoIndex = 0;
    private List<String> photoGallery;
    private PhotoMgmtHelperClass dbAccess;
    private Date default_minDate = new Date(Long.MIN_VALUE);
    private Date default_maxDate = new Date(Long.MAX_VALUE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnLeft = (Button)findViewById(R.id.btnLeft);
        Button btnRight = (Button)findViewById(R.id.btnRight);
        Button btnUpdate = (Button)findViewById(R.id.btnCaption);
        Button btnFilter = (Button)findViewById(R.id.btnFilter);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnUpdate.setOnClickListener(updateListener);
        btnFilter.setOnClickListener(filterListener);
        dbAccess = new PhotoMgmtHelperClass(this);

        photoGallery = populateGallery(default_minDate, default_maxDate);
        Log.d("onCreate, size", Integer.toString(photoGallery.size()));
        if (photoGallery.size() > 0)
            currentPhotoPath = photoGallery.get(currentPhotoIndex);
        displayPhoto(currentPhotoPath);
    }

    private View.OnClickListener filterListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, SearchActivity.class);
            startActivityForResult(i, SEARCH_ACTIVITY_REQUEST_CODE);
        }
    };

    private View.OnClickListener updateListener = new View.OnClickListener() {
        public void onClick(View v) {
            updateCaption();
        }
    };

    private List<String> populateGallery(Date minDate, Date maxDate) {
        return dbAccess.getPhotos(minDate, maxDate);
    }

    private void displayPhoto(String path) {
        if (path != null)
        {
            ImageView iv = (ImageView) findViewById(R.id.ivMain);
            Bitmap bm = BitmapFactory.decodeFile(path);
            bm = RotateImage(bm, 90f);
            iv.setImageBitmap(bm);
            changeDisplayPhotoDate();
            changeDisplayPhotoCaption();
        }
    }

    private static Bitmap RotateImage(Bitmap src, float angle)
    {
        Matrix m = new Matrix();
        m.postRotate(angle);
        return Bitmap.createBitmap(src,0,0, src.getWidth(), src.getHeight(), m, true);
    }

    private void changeDisplayPhotoDate() {
        TextView tv = (TextView) findViewById(R.id.text_imgDate);
        Date date = dbAccess.getPhotoDate(currentPhotoIndex);
        String dateToDisplay = new SimpleDateFormat("yyyy-MM-dd").format(date);

        tv.setText(dateToDisplay);
    }

    private void changeDisplayPhotoCaption() {
        TextView tv = (TextView) findViewById(R.id.text_caption);
        String text = dbAccess.getPhotoCaption(currentPhotoIndex);
        tv.setText(text);
    }

    private void updateCaption() {
        EditText edt = (EditText) findViewById(R.id.entry_caption);
        if (dbAccess.updateCaption(currentPhotoIndex, edt.getText().toString())) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Caption update success!",
                    Toast.LENGTH_SHORT);
            toast.show();
            edt.setText("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onClick( View v) {
        switch (v.getId()) {
            case R.id.btnLeft:
                --currentPhotoIndex;
                break;
            case R.id.btnRight:
                ++currentPhotoIndex;
                break;
            case R.id.btnCaption:
                break;
            default:
                break;
        }
        if (currentPhotoIndex < 0)
            currentPhotoIndex = 0;
        if (currentPhotoIndex >= photoGallery.size())
            currentPhotoIndex = photoGallery.size() - 1;

        if (photoGallery.size() > 0)
        {
            currentPhotoPath = photoGallery.get(currentPhotoIndex);
            Log.d("photoleft, size", Integer.toString(photoGallery.size()));
            Log.d("photoleft, index", Integer.toString(currentPhotoIndex));
            displayPhoto(currentPhotoPath);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("createImageFile", data.getStringExtra("STARTDATE"));
                Log.d("createImageFile", data.getStringExtra("ENDDATE"));
                Date minDate = getDateFromString(data.getStringExtra("STARTDATE"));
                Date maxDate = getDateFromString(data.getStringExtra("ENDDATE"));

                if (populateGallery(minDate, maxDate).size() != 0)
                {
                    photoGallery = populateGallery(minDate, maxDate);
                    Log.d("onCreate, size", Integer.toString(photoGallery.size()));
                    currentPhotoIndex = 0;
                    currentPhotoPath = photoGallery.get(currentPhotoIndex);
                    displayPhoto(currentPhotoPath);
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No photos were found within that time frame!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("createImageFile", "Picture Taken");
                dbAccess.save(currentPhotoPath);
                photoGallery = populateGallery(default_minDate, default_maxDate);
                currentPhotoIndex = 0;
                if (photoGallery.size() > 0) {
                    currentPhotoPath = photoGallery.get(currentPhotoIndex);
                    displayPhoto(currentPhotoPath);
                }
            }
        }
    }

    public void takePicture(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("FileCreation", "Failed");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.server.cameraapp.pictures.fileprovider",
                        photoFile);
                Log.d("photo URI", photoURI.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", dir );
        currentPhotoPath = image.getAbsolutePath();
        Log.d("createImageFile", currentPhotoPath);
        return image;
    }

}
