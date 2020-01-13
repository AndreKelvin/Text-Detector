package com.mobile.textdetector.model;

import android.os.AsyncTask;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;

import com.mobile.textdetector.view.MainActivity;
import com.mobile.textdetector.recyclerviewmodels.SavedTextItem;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import RoomDB.TextDetectorDbTable;

public class Repository {

    private static Repository instance;
    private List<SavedTextItem> savedTextList = new ArrayList<>();
    private List<SavedTextItem> selectedTextItem;
    private List<Integer> deletedID;
    private List<String> deletedText, deletedTextTranslated, deletedTextTranslatedCode, deletedTextImage, deletedDate;
    private MutableLiveData<List<SavedTextItem>> liveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isTextDeleted = new MutableLiveData<>();
    private MutableLiveData<Boolean> isTextSaved = new MutableLiveData<>();
    private ConstraintLayout constraintLayout;
    private boolean fromSavedTextActivity;
    private String textID,imagePath;
    private AppCompatEditText textDisplay, textTranslatedDisplay;
    private AppCompatTextView textLanguageTranslatedCode;

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public MutableLiveData<List<SavedTextItem>> getAllDetectedText() {
        new GetTextFromDB(this).execute();

        return liveData;
    }

    public void deleteDetectedText(List<SavedTextItem> selectedTextItem, List<Integer> deletedID, List<String> deletedText, List<String> deletedTextTranslated, List<String> deletedTextTranslatedCode, List<String> deletedTextImage, List<String> deletedDate) {
        this.selectedTextItem=selectedTextItem;
        this.deletedID=deletedID;
        this.deletedText=deletedText;
        this.deletedTextTranslated=deletedTextTranslated;
        this.deletedTextTranslatedCode=deletedTextTranslatedCode;
        this.deletedTextImage=deletedTextImage;
        this.deletedDate=deletedDate;

        new DeleteTextFromDB(this).execute();
    }

    public MutableLiveData<Boolean> getIsTextDeleted(){
        return isTextDeleted;
    }

    public void saveDetectedText(ConstraintLayout constraintLayout, boolean fromSavedTextActivity, String textID, String imagePath, AppCompatEditText textDisplay, AppCompatEditText textTranslatedDisplay, AppCompatTextView textLanguageTranslatedCode) {
        this.constraintLayout = constraintLayout;
        this.fromSavedTextActivity = fromSavedTextActivity;
        this.textID = textID;
        this.imagePath = imagePath;
        this.textDisplay = textDisplay;
        this.textTranslatedDisplay = textTranslatedDisplay;
        this.textLanguageTranslatedCode = textLanguageTranslatedCode;

        new SaveTextDB(this).execute();
    }

    public MutableLiveData<Boolean> getIsTextSaved(){
        return isTextSaved;
    }

    private static class GetTextFromDB extends AsyncTask<Void, Void, Void> {

        private WeakReference<Repository> weakReference;

        GetTextFromDB(Repository repo) {
            weakReference = new WeakReference<>(repo);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Repository repo = weakReference.get();
            if (repo == null) {
                return null;
            }

            repo.savedTextList.clear();
            List<TextDetectorDbTable> list = MainActivity.textDetectorDb.detectorDao().getAllDetectedText();
            for (TextDetectorDbTable text : list) {
                repo.savedTextList.add(new SavedTextItem(
                        text.getId(),
                        text.getText(),
                        text.getImage(),
                        text.getTranslatedText(),
                        text.getTranslatedTextCode(),
                        text.getDate()));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Repository repo = weakReference.get();
            if (repo == null) {
                return;
            }
            repo.liveData.setValue(repo.savedTextList);
        }
    }

    private static class DeleteTextFromDB extends AsyncTask<Void, Void, Void> {

        private WeakReference<Repository> weakReference;

        DeleteTextFromDB(Repository repo) {
            weakReference = new WeakReference<>(repo);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Repository repo = weakReference.get();
            if (repo == null) {
                return null;
            }

            String image;
            //user clicks delete icon in action menu
            if (!repo.selectedTextItem.isEmpty()) {
                for (int i = 0; i < repo.selectedTextItem.size(); i++) {

                    TextDetectorDbTable detectorDbTable = new TextDetectorDbTable(
                            repo.selectedTextItem.get(i).getTextID(),
                            repo.selectedTextItem.get(i).getText(),
                            repo.selectedTextItem.get(i).getTextTranslated(),
                            repo.selectedTextItem.get(i).getTextTranslatedCode(),
                            repo.selectedTextItem.get(i).getImage(),
                            repo.selectedTextItem.get(i).getDate());

                    //Delete the actual image in storage/emulated/0/Android/data/com.mobile.textdetector/files/Pictures
                    image = repo.selectedTextItem.get(i).getImage();
                    if (image != null) {
                        File file = new File(image);
                        file.delete();
                    }
                    MainActivity.textDetectorDb.detectorDao().deleteDetectedText(detectorDbTable);
                }
            } else {
                //User swipe to delete
                TextDetectorDbTable detectorDbTable = new TextDetectorDbTable(
                        repo.deletedID.get(0),
                        repo.deletedText.get(0),
                        repo.deletedTextTranslated.get(0),
                        repo.deletedTextTranslatedCode.get(0),
                        repo.deletedTextImage.get(0),
                        repo.deletedDate.get(0));

                //Delete the actual image in storage/emulated/0/Android/data/com.mobile.textdetector/files/Pictures
                image = repo.deletedTextImage.get(0);
                if (image != null) {
                    File file = new File(image);
                    file.delete();
                }
                MainActivity.textDetectorDb.detectorDao().deleteDetectedText(detectorDbTable);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Repository repo = weakReference.get();
            if (repo == null) {
                return;
            }
            repo.isTextDeleted.setValue(true);
            repo.selectedTextItem.clear();
        }
    }

    private static class SaveTextDB extends AsyncTask<Void, Void, Void> {

        private WeakReference<Repository> weakReference;

        SaveTextDB(Repository repo) {
            weakReference = new WeakReference<>(repo);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Repository repo = weakReference.get();
            if (repo == null) {
                return null;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a dd-MMM-yy", Locale.UK);
            String dateTime = dateFormat.format(new Date());

            if (repo.constraintLayout.isShown()) {
                //if this activity was started from SavedTextActivity
                //and saveText(save to db) was clicked
                //it will update the text in db, not actually saving them
                if (repo.fromSavedTextActivity) {
                    //Update
                    TextDetectorDbTable detectorDbTable = new TextDetectorDbTable(
                            Integer.parseInt(repo.textID),
                            repo.textDisplay.getText().toString(),
                            repo.textTranslatedDisplay.getText().toString(),
                            repo.textLanguageTranslatedCode.getText().toString(),
                            repo.imagePath,
                            dateTime);

                    MainActivity.textDetectorDb.detectorDao().updateDetectedText(detectorDbTable);
                } else {
                    //Save
                    TextDetectorDbTable detectorDbTable = new TextDetectorDbTable(
                            repo.textDisplay.getText().toString(),
                            repo.textTranslatedDisplay.getText().toString(),
                            repo.textLanguageTranslatedCode.getText().toString(),
                            repo.imagePath,
                            dateTime);

                    MainActivity.textDetectorDb.detectorDao().addDetectedText(detectorDbTable);
                }
            } else {
                //if this activity was started from SavedTextActivity
                //and saveText(save to db) was clicked
                //it will update/edit the text in db, not actually saving them
                if (repo.fromSavedTextActivity) {
                    //Update
                    TextDetectorDbTable detectorDbTable = new TextDetectorDbTable(
                            Integer.parseInt(repo.textID),
                            repo.textDisplay.getText().toString(),
                            null,
                            null,
                            repo.imagePath,
                            dateTime);

                    MainActivity.textDetectorDb.detectorDao().updateDetectedText(detectorDbTable);
                } else {
                    //Save
                    TextDetectorDbTable detectorDbTable = new TextDetectorDbTable(
                            repo.textDisplay.getText().toString(),
                            null,
                            null,
                            repo.imagePath,
                            dateTime);

                    MainActivity.textDetectorDb.detectorDao().addDetectedText(detectorDbTable);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Repository repo = weakReference.get();
            if (repo == null) {
                return;
            }
            repo.isTextSaved.setValue(true);
        }
    }

}
