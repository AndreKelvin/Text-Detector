package com.mobile.textdetector.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.mobile.textdetector.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageTextDetectorActivity extends AppCompatActivity {

    private FirebaseVisionImage firebaseVisionImage;
    private FirebaseVisionTextRecognizer textDetector;
    private File chooserImageFile, capturedImageFile;
    public static final String IMAGE_PATH="IMAGE_PATH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_text_detector);

        AppCompatImageView imageView = findViewById(R.id.imageView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        AdView adView = findViewById(R.id.adViewTextDetector);

        setSupportActionBar(toolbar);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        if (getIntent().getStringExtra(MainActivity.CAPTURED_IMAGE_URI) != null) {
            String uriExtra = getIntent().getStringExtra(MainActivity.CAPTURED_IMAGE_URI);

            Bitmap bitmapImage = BitmapFactory.decodeFile(uriExtra);
            imageView.setImageBitmap(bitmapImage);
            firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmapImage);

            capturedImageFile = new File(uriExtra);
        } else {

            try {
                Uri imageUri = getIntent().getParcelableExtra(MainActivity.FILE_CHOOSER_IMAGE_URI);
                imageView.setImageURI(imageUri);

                //Save image gotten from file chooser to storage/emulated/0/Android/data/com.mobile.textdetector/files/Pictures
                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                final Bitmap bitmap = bitmapDrawable.getBitmap();

                //getting uri of the file
           /* File file = getImageFile();
            Uri chosenImageUri= FileProvider.getUriForFile(this,"com.mobile.textdetector.fileprovider",file);*/

                FileOutputStream outputStream = new FileOutputStream(getImageFilePath());

                HandlerThread handlerThread = new HandlerThread("The Handler");
                handlerThread.start();

                Handler handler = new Handler(handlerThread.getLooper());
                final FileOutputStream finalOutputStream = outputStream;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, finalOutputStream);
                    }
                });

                firebaseVisionImage = FirebaseVisionImage.fromFilePath(this, imageUri);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        textDetector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
    }

    /**
     * this method will create and return the path to the image file
     */
    private String getImageFilePath() {
        String chosenImagePath = null;
        File folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String imageFileName = System.currentTimeMillis() + "_chooser_image";

        try {
            chooserImageFile = File.createTempFile(imageFileName, ".jpg", folder);
            chosenImagePath = chooserImageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return chosenImagePath;
    }

    public void grabPhotoText(View view) {
        textDetector.processImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        if (firebaseVisionText.getText().isEmpty()) {
                            Toast.makeText(ImageTextDetectorActivity.this, "No Text Found...", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), DisplayTextActivity.class);
                            intent.putExtra("TEXT", firebaseVisionText.getText());
                            if (capturedImageFile != null) {
                                intent.putExtra(IMAGE_PATH, capturedImageFile.getAbsolutePath());
                            } else {
                                intent.putExtra(IMAGE_PATH, chooserImageFile.getAbsolutePath());
                            }
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ImageTextDetectorActivity.this, "Fail to Grab Text!!!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (capturedImageFile != null) {
            capturedImageFile.delete();
        } else {
            chooserImageFile.delete();
        }
    }

    public void cancel(View view) {
        onBackPressed();
    }
}
