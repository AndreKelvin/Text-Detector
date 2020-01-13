package com.mobile.textdetector.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.languageid.IdentifiedLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.mobile.textdetector.R;
import com.mobile.textdetector.adapters.RecyclerViewLanguageAdapter;
import com.mobile.textdetector.adapters.RecyclerViewSavedTextAdapter;
import com.mobile.textdetector.viewmodel.ViewModel;

import java.io.File;
import java.util.List;

public class DisplayTextActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private AppCompatEditText textDisplay, textTranslatedDisplay;
    private AppCompatTextView textLanguageCode, textLanguageTranslatedCode, textSelectedTranslateLanguage;
    private AppCompatImageView imageTextToSpeech, imageTextTranslatedToSpeech, imageLanguage;
    private RelativeLayout relativeLayout,relativeLayoutTranslateProgress;
    private FloatingActionButton floatingActionButton;
    private FrameLayout frameLayout;
    private boolean isRotate = false;
    private String selectedLanguageCode, text, textID, imagePath;
    private TextToSpeech textToSpeech, textToSpeechTranslated;
    private FirebaseTranslator translator;
    private ViewModel viewModel;

    //this boolean is to indicate weather "saveText" method is called(when save to db)
    //if it's called the boolean will be true
    private boolean imageAndTextSaved = false;

    //this boolean is to indicate weather this activity was started from SavedTextActivity
    //then when "saveText" method is called(when save to db)
    //text will be updated in db, not actually saving them
    private boolean fromSavedTextActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_text);

        constraintLayout = findViewById(R.id.constraintLayout2);
        textDisplay = findViewById(R.id.editTextDisplay);
        textTranslatedDisplay = findViewById(R.id.editTextDisplay2);
        textLanguageCode = findViewById(R.id.textViewLanguageCode);
        textLanguageTranslatedCode = findViewById(R.id.textViewLanguageCode2);
        textSelectedTranslateLanguage = findViewById(R.id.textViewSelectedTranslateLanguage);
        imageTextToSpeech = findViewById(R.id.imageViewTextToSpeech);
        imageTextTranslatedToSpeech = findViewById(R.id.imageViewTextToSpeech2);
        imageLanguage = findViewById(R.id.imageViewLanguage);
        relativeLayout = findViewById(R.id.relativeLayout);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        frameLayout = findViewById(R.id.frameLayout);
        relativeLayoutTranslateProgress = findViewById(R.id.relativeLayoutTranslate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        AdView adView = findViewById(R.id.adViewDisplayText);

        setSupportActionBar(toolbar);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        if (getIntent().getStringExtra(RecyclerViewSavedTextAdapter.SAVED_TEXT) != null) {
            text = getIntent().getStringExtra(RecyclerViewSavedTextAdapter.SAVED_TEXT);
            textID = getIntent().getStringExtra(RecyclerViewSavedTextAdapter.SAVED_TEXT_ID);
            imagePath = getIntent().getStringExtra(RecyclerViewSavedTextAdapter.SAVED_IMAGE_PATH);

            //to know if the selected item text has been translated
            if (!getIntent().getStringExtra(RecyclerViewSavedTextAdapter.SAVED_TEXT_TRANSLATED).isEmpty()) {
                String textTranslated = getIntent().getStringExtra(RecyclerViewSavedTextAdapter.SAVED_TEXT_TRANSLATED);
                String textTranslatedCode = getIntent().getStringExtra(RecyclerViewSavedTextAdapter.SAVED_TEXT_TRANSLATED_CODE);

                constraintLayout.setVisibility(View.VISIBLE);
                textTranslatedDisplay.setText(textTranslated);
                textLanguageTranslatedCode.setText(textTranslatedCode);
            }
            fromSavedTextActivity = true;
        } else {
            text = getIntent().getStringExtra("TEXT");

            //The actual image is already saved in storage/emulated/0/Android/data/com.mobile.textdetector/files/Pictures
            //so the url is now passed to be saved to db
            imagePath = getIntent().getStringExtra(ImageTextDetectorActivity.IMAGE_PATH);
        }

        //to identify the language that will be displayed
        FirebaseLanguageIdentification languageIdentifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();

        //identify the language that will be displayed and display the language code
        languageIdentifier.identifyPossibleLanguages(text)
                .addOnSuccessListener(new OnSuccessListener<List<IdentifiedLanguage>>() {
                    @Override
                    public void onSuccess(List<IdentifiedLanguage> identifiedLanguages) {

                        for (IdentifiedLanguage identifiedLanguage : identifiedLanguages) {
                            String languageCode = identifiedLanguage.getLanguageCode();
                            textLanguageCode.setText(languageCode);
                            break;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DisplayTextActivity.this, "Failed to identify language", Toast.LENGTH_LONG).show();
                    }
                });

        textDisplay.setText(text);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRotate = ViewAnimation.rotateFab(v, !isRotate);

                if (isRotate) {
                    frameLayout.setVisibility(View.VISIBLE);
                } else {
                    frameLayout.setVisibility(View.GONE);
                }
            }
        });

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionButton.callOnClick();
            }
        });

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(textLanguageCode.getTextLocale());

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.i("TextToSpeech", "Language Not Supported");
                        imageTextToSpeech.setEnabled(false);
                    }

                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Log.i("TextToSpeech", "On Start");
                            imageTextToSpeech.setImageResource(R.drawable.ic_volume_up);
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            Log.i("TextToSpeech", "On Done");
                            imageTextToSpeech.setImageResource(R.drawable.ic_volume_mute);
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }
                    });

                } else {
                    Log.i("TextToSpeech", "Initialization Failed");
                }
            }
        });

        textToSpeechTranslated = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeechTranslated.setLanguage(textLanguageTranslatedCode.getTextLocale());

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.i("TextToSpeech", "Language Not Supported");
                        imageTextTranslatedToSpeech.setEnabled(false);
                    }

                    textToSpeechTranslated.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Log.i("TextToSpeech", "On Start");
                            imageTextTranslatedToSpeech.setImageResource(R.drawable.ic_volume_up);
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            Log.i("TextToSpeech", "On Done");
                            imageTextTranslatedToSpeech.setImageResource(R.drawable.ic_volume_mute);
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }
                    });

                } else {
                    Log.i("TextToSpeech", "Initialization Failed");
                }
            }
        });

        viewModel = ViewModelProviders.of(this).get(ViewModel.class);

    }

    public void openTranslate(View view) {
        floatingActionButton.callOnClick();
        startActivity(new Intent(this, TranslateActivity.class));
    }

    public void saveText(View view) {
        viewModel.isTextSaved().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                imageAndTextSaved=true;
                Toast.makeText(DisplayTextActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();

                //after text has been saved or updated. refresh data in recycler view
                viewModel.getAllDetectedText();
            }
        });
        viewModel.saveDetectedText(
                constraintLayout,
                fromSavedTextActivity,
                textID,
                imagePath,
                textDisplay,
                textTranslatedDisplay,
                textLanguageTranslatedCode
        );
        floatingActionButton.callOnClick();
        //new SaveTextDB(this).execute();
    }

    public void shareText(View view) {
        String text = textDisplay.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        if (constraintLayout.isShown()) {
            String textTranslated = textTranslatedDisplay.getText().toString();
            intent.putExtra(Intent.EXTRA_TEXT, TextUtils.concat(text, textTranslated));
        } else {
            intent.putExtra(Intent.EXTRA_TEXT, text);
        }
        Intent shareIntent = Intent.createChooser(intent, null);
        startActivity(shareIntent);
    }

    public void translate(View view) {
        //Check if the language model has already been downloaded
        final FirebaseModelManager modelManager = FirebaseModelManager.getInstance();

        final FirebaseTranslateRemoteModel checkModel = new FirebaseTranslateRemoteModel
                .Builder(FirebaseTranslateLanguage.languageForLanguageCode(selectedLanguageCode))
                .build();

        modelManager.isModelDownloaded(checkModel).addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                showProgressBar();

                String languageCode = textLanguageCode.getText().toString();

                FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.languageForLanguageCode(languageCode))
                        .setTargetLanguage(FirebaseTranslateLanguage.languageForLanguageCode(selectedLanguageCode))
                        .build();

                translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

                if (aBoolean) {
                    //language is downloaded
                    //translate text
                    translateText();

                } else {
                    //language isn't downloaded
                    //check if there is an internet connection and download language model
                    //else toast "No internet connection"
                    if (isNetworkAvailable(DisplayTextActivity.this)) {

                        translator.downloadModelIfNeeded().addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void v) {
                                        // Model downloaded successfully. Okay to start translating.
                                        translateText();
                                    }
                                });

                    } else {
                        hideProgressBar();
                        Toast.makeText(DisplayTextActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    private void translateText() {
        translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(@NonNull String translatedText) {
                hideProgressBar();

                constraintLayout.setVisibility(View.VISIBLE);
                textTranslatedDisplay.setText(translatedText);
                textLanguageTranslatedCode.setText(selectedLanguageCode);
            }
        });
    }

    private void showProgressBar() {
        relativeLayoutTranslateProgress.setVisibility(View.VISIBLE);
        //this make sure views wouldn't be clickable when text translation is in progress
        //and progress bar is visible
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgressBar() {
        relativeLayoutTranslateProgress.setVisibility(View.GONE);
        //this make sure views will be clickable when text translation is done
        //and progress bar is Gone
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    //This only checks if the network interface is available
    //doesn't guarantee a particular network service is available
    //for mobile, there could be low signal or no data to access the internet
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    //This makes a real connection to an url and checks if you can connect to this url
    //this needs to be wrapped in a background thread
    /*private boolean isAbleToConnect() {
        try {
            URL myUrl = new URL("http://www.google.com");
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(1000);
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int selectedLanguageFlag = sharedPreferences.getInt(RecyclerViewLanguageAdapter.SELECTED_LANGUAGE_FLAG, 0);
        String selectedLanguageText = sharedPreferences.getString(RecyclerViewLanguageAdapter.SELECTED_LANGUAGE_TEXT, "");
        selectedLanguageCode = sharedPreferences.getString(RecyclerViewLanguageAdapter.SELECTED_LANGUAGE_CODE, "");

        if (selectedLanguageFlag != 0) {
            relativeLayout.setVisibility(View.VISIBLE);
            imageLanguage.setImageResource(selectedLanguageFlag);
            textSelectedTranslateLanguage.setText(selectedLanguageText);
            textLanguageTranslatedCode.setText(selectedLanguageCode);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
        Note: the actual image has already been saved in storage/emulated/0/Android/data/com.mobile.textdetector/files/Pictures
        so if "saveText" method isn't called(when save to db)
        delete the saved image
         */

        if (imagePath != null) {
            File file = new File(imagePath);
            if (!imageAndTextSaved) {
                file.delete();
            }
        }
    }

    public void speakText(View view) {
        textToSpeech.setPitch(1.0f);
        textToSpeech.setSpeechRate(1.0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(textDisplay.getText(), TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
        }
    }

    public void speakTranslatedText(View view) {
        textToSpeechTranslated.setPitch(1.0f);
        textToSpeechTranslated.setSpeechRate(1.0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeechTranslated.speak(textTranslatedDisplay.getText(), TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (textToSpeechTranslated != null) {
            textToSpeechTranslated.stop();
            textToSpeechTranslated.shutdown();
        }
        super.onDestroy();
    }

    /*private static class SaveTextDB extends AsyncTask<Void, Void, Void> {

        private WeakReference<DisplayTextActivity> weakReference;

        SaveTextDB(DisplayTextActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            DisplayTextActivity activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return null;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a dd-MMM-yy", Locale.UK);
            String dateTime = dateFormat.format(new Date());

            if (activity.constraintLayout.isShown()) {
                //if this activity was started from SavedTextActivity
                //and saveText(save to db) was clicked
                //it will update the text in db, not actually saving them
                if (activity.fromSavedTextActivity) {
                    //Update
                    TextDetectorDbTable detectorDbTable = new TextDetectorDbTable(
                            Integer.parseInt(activity.textID),
                            activity.textDisplay.getText().toString(),
                            activity.textTranslatedDisplay.getText().toString(),
                            activity.textLanguageTranslatedCode.getText().toString(),
                            activity.imagePath,
                            dateTime);

                    MainActivity.textDetectorDb.detectorDao().updateDetectedText(detectorDbTable);
                } else {
                    //Save
                    TextDetectorDbTable detectorDbTable = new TextDetectorDbTable(
                            activity.textDisplay.getText().toString(),
                            activity.textTranslatedDisplay.getText().toString(),
                            activity.textLanguageTranslatedCode.getText().toString(),
                            activity.imagePath,
                            dateTime);

                    MainActivity.textDetectorDb.detectorDao().addDetectedText(detectorDbTable);
                }
            } else {
                //if this activity was started from SavedTextActivity
                //and saveText(save to db) was clicked
                //it will update/edit the text in db, not actually saving them
                if (activity.fromSavedTextActivity) {
                    //Update
                    TextDetectorDbTable detectorDbTable = new TextDetectorDbTable(
                            Integer.parseInt(activity.textID),
                            activity.textDisplay.getText().toString(),
                            null,
                            null,
                            activity.imagePath,
                            dateTime);

                    MainActivity.textDetectorDb.detectorDao().updateDetectedText(detectorDbTable);
                } else {
                    //Save
                    TextDetectorDbTable detectorDbTable = new TextDetectorDbTable(
                            activity.textDisplay.getText().toString(),
                            null,
                            null,
                            activity.imagePath,
                            dateTime);

                    MainActivity.textDetectorDb.detectorDao().addDetectedText(detectorDbTable);
                }
            }
            activity.imageAndTextSaved = true;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            DisplayTextActivity activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            Toast.makeText(activity, "Saved Successfully", Toast.LENGTH_SHORT).show();
            activity.floatingActionButton.callOnClick();
        }
    }*/

}
