package com.coursera.wfernandes.dailyselfie;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SelfieListViewActivity extends ListActivity {

    private AlarmManager mAlarmManager;
    private PendingIntent mSelfiePendingIntent;
    private Intent mSelfieNotificationIntent;

    private static final String TAG = "SelfieActivity";
    private static final long ONE_MINUTE = 30 * 1000L;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView mImageView;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfies);

        mImageView = (ImageView) findViewById(R.id.selfie_image);

        createPendingIntents();

        createSelfieReminders();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            Log.i(TAG, "Getting the image thumbnail");
//            Bundle extras = data.getExtras();
//            Bitmap imageThumbnail = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageThumbnail);

        }
    }

    private void createSelfieReminders(){
        Log.i(TAG, "Create selfie reminders");
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Broadcast the notification intent at specified intervals
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP
                , System.currentTimeMillis() + ONE_MINUTE
                , ONE_MINUTE
                , mSelfiePendingIntent);

    }

    private void createPendingIntents(){
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
        }else if(id == R.id.action_camera){
            takePicture();
        }
        return super.onOptionsItemSelected(item);
    }

    private void takePicture() {
        Log.i(TAG, "Opening the camera to take picture");

        // Dispatch take picture intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check to see if there is an application available to handle capturing images.
        // This is required else if no camera handling application is found, the Selfie app will crash
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){

            // Create the file where the photo should be saved to
            File selfieFile = null;

            try {
                selfieFile = createImageFile();
            } catch (IOException e) {
                Log.e(TAG, "Error creating selfie file", e);
            }

            if(selfieFile != null){
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(selfieFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

        }
    }

    private File createImageFile() throws IOException{

        // Create a unique image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SELFIE_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // Make sure the directory exists
        storageDir.mkdirs();

        Log.i(TAG, storageDir.getAbsolutePath());
        Log.i(TAG, storageDir.toString());
        Log.i(TAG, imageFileName);
        File image = File.createTempFile(
                imageFileName
                ,".jpg"
                ,storageDir
        );

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
}
