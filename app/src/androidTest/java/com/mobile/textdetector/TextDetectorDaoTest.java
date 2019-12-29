package com.mobile.textdetector;

import android.text.TextUtils;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.mobile.textdetector.util.TextDumbingUtil;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import RoomDB.TextDetectorDao;
import RoomDB.TextDetectorDbTable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TextDetectorDaoTest extends TextDetectorDumbDBTest {

    //this make sure you can run test on background
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    /*
        Insert, Read, Delete
     */
    @Test
    public void insertReadDelete() throws Exception{

        TextDetectorDbTable detectedText = new TextDetectorDbTable(
                TextDumbingUtil.TEXT,
                TextDumbingUtil.TRANSLATED_TEXT,
                TextDumbingUtil.TRANSLATED_TEXT_CODE,
                TextDumbingUtil.IMAGE,
                TextDumbingUtil.DATE);

        // insert
        getDetectorDao().addDetectedText(detectedText);

        // read
        List<TextDetectorDbTable> allDetectedText = getDetectorDao().getAllDetectedText();
        for (TextDetectorDbTable insertedText : allDetectedText){
            //to make sure data was actually retrieve form db to avoid null values
            assertNotNull(insertedText);

            assertEquals(detectedText.getText(), insertedText.getText());
            assertEquals(detectedText.getTranslatedText(), insertedText.getTranslatedText());
            assertEquals(detectedText.getTranslatedTextCode(), insertedText.getTranslatedTextCode());
            assertEquals(detectedText.getImage(), insertedText.getImage());
            assertEquals(detectedText.getDate(), insertedText.getDate());

            int insertedTextId = insertedText.getId();
            detectedText.setId(insertedTextId);
            assertEquals(detectedText.getId(), insertedTextId);
        }

        // delete
        getDetectorDao().deleteDetectedText(detectedText);

        // confirm the database is empty
        allDetectedText = getDetectorDao().getAllDetectedText();
        assertEquals(0, allDetectedText.size());
    }

    /*
        Insert, Read, Update, Read, Delete,
     */
    @Test
    public void insertReadUpdateReadDelete() throws Exception{
        TextDetectorDbTable detectedText = new TextDetectorDbTable(
                TextDumbingUtil.TEXT,
                TextDumbingUtil.TRANSLATED_TEXT,
                TextDumbingUtil.TRANSLATED_TEXT_CODE,
                TextDumbingUtil.IMAGE,
                TextDumbingUtil.DATE);

        // insert
        getDetectorDao().addDetectedText(detectedText);

        int insertedTextId = 0;
        // read
        List<TextDetectorDbTable> allDetectedText = getDetectorDao().getAllDetectedText();
        for (TextDetectorDbTable insertedText : allDetectedText){
            //to make sure data was actually retrieve form db to avoid null values
            assertNotNull(insertedText);

            assertEquals(detectedText.getText(), insertedText.getText());
            assertEquals(detectedText.getTranslatedText(), insertedText.getTranslatedText());
            assertEquals(detectedText.getTranslatedTextCode(), insertedText.getTranslatedTextCode());
            assertEquals(detectedText.getImage(), insertedText.getImage());
            assertEquals(detectedText.getDate(), insertedText.getDate());

            insertedTextId = insertedText.getId();
            detectedText.setId(insertedTextId);
            assertEquals(detectedText.getId(), insertedTextId);
        }

        //update
        TextDetectorDbTable detectedTextUpdate = new TextDetectorDbTable(
                insertedTextId,
                TextDumbingUtil.TEXT_UPDATED,
                TextDumbingUtil.TRANSLATED_TEXT_UPDATED,
                TextDumbingUtil.TRANSLATED_TEXT_CODE_UPDATED,
                TextDumbingUtil.IMAGE_UPDATED,
                TextDumbingUtil.DATE_UPDATED);
        getDetectorDao().updateDetectedText(detectedTextUpdate);

        // read updated data to make sure data is actually updated
        List<TextDetectorDbTable> allDetectedTextUpdated = getDetectorDao().getAllDetectedText();
        for (TextDetectorDbTable updatedText : allDetectedTextUpdated){
            //to make sure data was actually retrieve form db to avoid null values
            assertNotNull(updatedText);

            assertEquals(TextDumbingUtil.TEXT_UPDATED, updatedText.getText());
            assertEquals(TextDumbingUtil.TRANSLATED_TEXT_UPDATED, updatedText.getTranslatedText());
            assertEquals(TextDumbingUtil.TRANSLATED_TEXT_CODE_UPDATED, updatedText.getTranslatedTextCode());
            assertEquals(TextDumbingUtil.IMAGE_UPDATED, updatedText.getImage());
            assertEquals(TextDumbingUtil.DATE_UPDATED, updatedText.getDate());

            detectedText.setId(insertedTextId);
            assertEquals(detectedText.getId(), insertedTextId);
        }

        // delete
        getDetectorDao().deleteDetectedText(detectedText);

        // confirm the database is empty
        allDetectedText = getDetectorDao().getAllDetectedText();
        assertEquals(0, allDetectedText.size());
    }
}
