package com.coursera.wfernandes.dailyselfie;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SelfieListViewActivity extends ListActivity {

    private AlarmManager mAlarmManager;
    private PendingIntent mSelfiePendingIntent;
    private Intent mSelfieNotificationIntent;

    private static final String TAG = "SelfieActivity";
    private static final int THUMB_DIM = 100;
    private static final long ONE_MINUTE = 60 * 1000L;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private List<Selfie> SELFIES = new ArrayList<Selfie>();
    private static CustomAdapter SELFIE_ADAPTER;
    private static final File STORAGE_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create new list adapter
        getSelfies(SELFIES);
        SELFIE_ADAPTER = new CustomAdapter(this, R.layout.list_item, R.id.item_txt, SELFIES);
        SELFIE_ADAPTER.setNotifyOnChange(true);
        setListAdapter(SELFIE_ADAPTER);
        ListView listView = getListView();
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent fullSelfieIntent = new Intent(Intent.ACTION_VIEW);
                fullSelfieIntent.setDataAndType(Uri.parse("file://" + SELFIES.get(i).getSelfiePath()), "image/*");
                startActivity(fullSelfieIntent);
            }
        });


        createPendingIntents();

        createSelfieReminders();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.i(TAG, "Getting the image thumbnail");
//            Bundle extras = data.getExtras();
//            Bitmap imageThumbnail = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageThumbnail);

        }
    }

    private void createSelfieReminders() {
        Log.i(TAG, "Create selfie reminders");
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Broadcast the notification intent at specified intervals
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP
                , System.currentTimeMillis() + ONE_MINUTE
                , ONE_MINUTE
                , mSelfiePendingIntent);

    }

    private void createPendingIntents() {
        Log.i(TAG, "Create pending intents");
        // Create the notification pending intent
        mSelfieNotificationIntent = new Intent(SelfieListViewActivity.this, SelfieNotificationReceiver.class);
        mSelfiePendingIntent = PendingIntent.getBroadcast(SelfieListViewActivity.this, 0, mSelfieNotificationIntent, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.selfies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_camera) {
            takePicture();
        } else if (id == R.id.action_refresh) {
            updateSelfieList();
        }
        return super.onOptionsItemSelected(item);
    }

    private void takePicture() {
        Log.i(TAG, "Opening the camera to take picture");

        // Dispatch take picture intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check to see if there is an application available to handle capturing images.
        // This is required else if no camera handling application is found, the Selfie app will crash
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the file where the photo should be saved to
            File selfieFile = null;

            try {
                selfieFile = createImageFile();
            } catch (IOException e) {
                Log.e(TAG, "Error creating selfie file", e);
            }

            if (selfieFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(selfieFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

        }
    }

    private File createImageFile() throws IOException {
        // Create a unique image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SELFIE_" + timeStamp;
        // Make sure the directory exists
        STORAGE_DIR.mkdirs();

        File image = File.createTempFile(
                imageFileName
                , ".jpg"
                , STORAGE_DIR
        );

        return image;
    }

    private void getSelfies(List<Selfie> selfieList) {
        if (selfieList != null && STORAGE_DIR.exists()) {
            // For now we are starting from fresh and rebuilding the list.
            selfieList.clear();
            Log.i(TAG, "Storage directory exists!!");
            for (File file : STORAGE_DIR.listFiles(new SelfieFileFilter())) {
                selfieList.add(
                        new Selfie(
                                file.getName()
                                , file.getAbsolutePath()
                                , getSelfieThumbnail(file.getAbsolutePath())
                        )
                );
            }

        }
    }

    private Bitmap getSelfieThumbnail(String photoPath) {
        Log.i(TAG, "Getting selfie thumbnails");
        // Get the dimensions of the View
        int targetW = THUMB_DIM;
        int targetH = THUMB_DIM;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

    private void updateSelfieList() {
        Log.i(TAG, "old selfies list size..." + SELFIES.size());
        Log.i(TAG, "UPDATING!!");
        getSelfies(SELFIES);
        Log.i(TAG, "updating selfies list..." + SELFIES.size());
        SELFIE_ADAPTER.notifyDataSetChanged();
    }
}
