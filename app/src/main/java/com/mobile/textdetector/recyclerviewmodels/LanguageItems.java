package com.mobile.textdetector.recyclerviewmodels;

public class LanguageItems {
    private int languageFlagImage;
    private String languageText;
    private String languageCode;

    public LanguageItems(int languageFlagImage, String languageText, String languageCode) {
        this.languageFlagImage = languageFlagImage;
        this.languageText = languageText;
        this.languageCode = languageCode;
    }

    public int getLanguageFlagImage() {
        return languageFlagImage;
    }

    public void setLanguageFlagImage(int languageFlagImage) {
        this.languageFlagImage = languageFlagImage;
    }

    public String getLanguageText() {
        return languageText;
    }

    public void setLanguageText(String languageText) {
        this.languageText = languageText;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}
