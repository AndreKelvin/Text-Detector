package com.mobile.textdetector.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.mobile.textdetector.recyclerviewmodels.SavedTextItem;
import com.mobile.textdetector.model.Repository;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    private Repository repository;

    public ViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
    }

    public MutableLiveData<List<SavedTextItem>> getAllDetectedText() {
        return repository.getAllDetectedText();
    }

    public void deleteDetectedText(List<SavedTextItem> selectedTextItem, List<Integer> deletedID, List<String> deletedText, List<String> deletedTextTranslated, List<String> deletedTextTranslatedCode, List<String> deletedTextImage, List<String> deletedDate) {
        repository.deleteDetectedText(selectedTextItem, deletedID, deletedText, deletedTextTranslated, deletedTextTranslatedCode, deletedTextImage, deletedDate);
    }

    public MutableLiveData<Boolean> isTextDeleted() {
        return repository.getIsTextDeleted();
    }

    public void saveDetectedText(ConstraintLayout constraintLayout, boolean fromSavedTextActivity, String textID, String imagePath, AppCompatEditText textDisplay, AppCompatEditText textTranslatedDisplay, AppCompatTextView textLanguageTranslatedCode) {
        repository.saveDetectedText(constraintLayout, fromSavedTextActivity, textID, imagePath, textDisplay, textTranslatedDisplay, textLanguageTranslatedCode);
    }

    public MutableLiveData<Boolean> isTextSaved(){
        return repository.getIsTextSaved();
    }

}
