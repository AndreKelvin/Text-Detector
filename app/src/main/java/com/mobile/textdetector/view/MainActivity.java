package com.mobile.textdetector.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.room.Room;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.tabs.TabLayout;
import com.mobile.textdetector.R;

import java.io.File;
import java.io.IOException;

import RoomDB.TextDetectorDb;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private final int REQUEST_CAMERA_PERMISSION_ID = 1001;
    private final int PICK_IMAGE_REQUEST = 1996;
    private final int CAPTURE_IMAGE_REQUEST = 6991;
    private boolean detect = false;
    private Button buttonGrabText;
    private TabLayout.Tab tab;
    private String capturedImagePath;
    public static TextDetectorDb textDetectorDb;
    private ProgressBar progressBar;
    public static final String CAPTURED_IMAGE_URI="CAPTURED_IMAGE_URI";
    public static final String FILE_CHOOSER_IMAGE_URI="FILE_CHOOSER_IMAGE_URI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        surfaceView = findViewById(R.id.surfaceView);
        buttonGrabText = findViewById(R.id.buttonGrabText);
        progressBar=findViewById(R.id.progressBar);

        //Init Admob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                //Log.d("MainActivity","Admob initialized");
            }
        });
        AdView adView=findViewById(R.id.adViewMain);

        AdRequest adRequest=new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        textDetectorDb= Room.databaseBuilder(this,TextDetectorDb.class,"textdetectordb").fallbackToDestructiveMigration().build();

        surfaceView.getHolder().addCallback(this);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();

        cameraSource = new CameraSource.Builder(this, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    //getting uri of the file
                    File file = getImageFile();
                    Uri imageUri= FileProvider.getUriForFile(MainActivity.this,"com.mobile.textdetector.fileprovider",file);

                    //Set the file Uri to my photo
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    MainActivity.this.tab = tab;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies are not yet available");
        } else {
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                    Log.w("MainActivity", "On Text Recognizer Released");
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0) {

                        buttonGrabText.post(new Runnable() {
                            @Override
                            public void run() {
                                while (detect) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int i = 0; i < items.size(); i++) {
                                        TextBlock item = items.valueAt(i);
                                        stringBuilder.append(item.getValue());
                                        //stringBuilder.append("\n");
                                    }
                                    Intent intent = new Intent(getApplicationContext(), DisplayTextActivity.class);
                                    intent.putExtra("TEXT", stringBuilder.toString());
                                    startActivity(intent);

                                    detect = false;
                                }
                            }
                        });
                    } else {
                        buttonGrabText.post(new Runnable() {
                            @Override
                            public void run() {
                                while (detect) {
                                    progressBar.setVisibility(View.GONE);
                                    //this make sure views will be clickable when text detector is empty
                                    //and progress bar is Gone
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Image Text")
                                            .setMessage("No Text Found...")
                                            .show();
                                    detect = false;
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * this method will create and return the path to the image file
     */
    private File getImageFile() {
        File folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String imageFileName = System.currentTimeMillis() + "_image";
        File imageFile = null;

        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        capturedImagePath =imageFile.getAbsolutePath();

        return imageFile;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CAMERA_PERMISSION_ID);
                return;
            }
            cameraSource.start(surfaceView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        cameraSource.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            //Bitmap bitmapImage = (Bitmap) data.getExtras().get("data");
            /*Bitmap bitmapImage= null;
            try {
                bitmapImage = getBitmap(getContentResolver(),file);
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            Intent intent = new Intent(getApplicationContext(), ImageTextDetectorActivity.class);
            intent.putExtra(CAPTURED_IMAGE_URI, capturedImagePath);
            startActivity(intent);
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            Intent intent = new Intent(getApplicationContext(), ImageTextDetectorActivity.class);
            intent.putExtra(FILE_CHOOSER_IMAGE_URI, imageUri);
            startActivity(intent);
        } else {
            if (tab != null) {
                tab.select();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        progressBar.setVisibility(View.GONE);
        //this make sure views will be clickable when text detector is done
        //and progress bar is Gone
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (tab != null) {
            tab.select();
        }
    }

    public void grabText(View view) {
        progressBar.setVisibility(View.VISIBLE);
        //this make sure views wouldn't be clickable when text detector is in progress
        //and progress bar is visible
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        detect = true;
    }

    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void viewDetectedText(View view) {
        startActivity(new Intent(this,SavedTextActivity.class));
    }
}
