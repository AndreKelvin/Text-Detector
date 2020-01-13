package com.mobile.textdetector;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.languageid.IdentifiedLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel;
import com.mobile.textdetector.view.DisplayTextActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static com.mobile.textdetector.adapters.RecyclerViewSavedTextAdapter.SAVED_IMAGE_PATH;
import static com.mobile.textdetector.adapters.RecyclerViewSavedTextAdapter.SAVED_TEXT;
import static com.mobile.textdetector.adapters.RecyclerViewSavedTextAdapter.SAVED_TEXT_ID;
import static com.mobile.textdetector.adapters.RecyclerViewSavedTextAdapter.SAVED_TEXT_TRANSLATED;
import static com.mobile.textdetector.adapters.RecyclerViewSavedTextAdapter.SAVED_TEXT_TRANSLATED_CODE;
import static org.junit.Assert.assertEquals;

public class DisplayTextActivityTest {

    private FirebaseLanguageIdentification languageIdentifier;
    private final String ENGLISH_LANGUAGE_CODE="en";
    private final String ENGLISH_LANGUAGE="Skipping duplicate class check due to unsupported classloader";
    private final String FRENCH_LANGUAGE_CODE="fr";
    private final String FRENCH_LANGUAGE="Ignorer la vérification de classe en double en raison d'un chargeur de classe non pris en charge";
    private final String GERMAN_LANGUAGE_CODE="de";
    private final String GERMAN_LANGUAGE="Überspringen der doppelten Klassenprüfung aufgrund eines nicht unterstützten Klassenladeprogramms";
    private final String SPANISH_LANGUAGE_CODE="es";
    private final String SPANISH_LANGUAGE="Omitir verificación de clase duplicada debido a un cargador de clases no compatible";

    @Before
    public void inti(){
        languageIdentifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
    }

    @Rule
    public ActivityTestRule<DisplayTextActivity> mainActivityActivityTestRule = new ActivityTestRule<DisplayTextActivity>(DisplayTextActivity.class){};

    @Test
    public void getIntent(){
        Intent intent = new Intent();
        intent.putExtra(SAVED_TEXT, ENGLISH_LANGUAGE);
        intent.putExtra(SAVED_TEXT_ID, "1");
        intent.putExtra(SAVED_IMAGE_PATH, "lnjsfkd");
        intent.putExtra(SAVED_TEXT_TRANSLATED, SPANISH_LANGUAGE);
        intent.putExtra(SAVED_TEXT_TRANSLATED_CODE, SPANISH_LANGUAGE_CODE);
        mainActivityActivityTestRule.launchActivity(intent);
        String intentExtra = mainActivityActivityTestRule.getActivity().getIntent().getStringExtra(SAVED_TEXT);
        assertEquals(intentExtra,"Skipping duplicate class check due to unsupported classloader");
    }

    @Test
    public void identifyLanguageCode(){
        languageIdentifier.identifyPossibleLanguages(FRENCH_LANGUAGE)
                .addOnSuccessListener(new OnSuccessListener<List<IdentifiedLanguage>>() {
                    @Override
                    public void onSuccess(List<IdentifiedLanguage> identifiedLanguages) {

                        for (IdentifiedLanguage identifiedLanguage : identifiedLanguages) {
                            String languageCode = identifiedLanguage.getLanguageCode();
                            assertEquals(FRENCH_LANGUAGE_CODE,languageCode);
                            System.out.println("____________the identified language is French "+languageCode+"______________");
                            break;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to identify language");
                    }
                });
    }

    @Test
    public void isLanguageModelDownloaded(){
        final FirebaseModelManager modelManager = FirebaseModelManager.getInstance();

        final FirebaseTranslateRemoteModel checkModel = new FirebaseTranslateRemoteModel
                .Builder(FirebaseTranslateLanguage.languageForLanguageCode(SPANISH_LANGUAGE_CODE))
                .build();

        modelManager.isModelDownloaded(checkModel).addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

                /*FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.languageForLanguageCode(SPANISH_LANGUAGE_CODE))
                        .setTargetLanguage(FirebaseTranslateLanguage.languageForLanguageCode(ENGLISH_LANGUAGE_CODE))
                        .build();*/

                if (aBoolean) {
                    //language is downloaded
                    //translate text
                    assertEquals("es",SPANISH_LANGUAGE_CODE);
                    System.out.println("_____________Language is downloaded________________");

                } else {
                    //language isn't downloaded
                    //check if there is an internet connection and download language model
                    //else toast "No internet connection"
                    System.out.println("_____________Language is not downloaded_____________");
                }

            }
        });
    }

}
