package com.mobile.textdetector;

public class LanguageItems {
    private int languageFlagImage;
    private String languageText;
    private String languageCode;

    LanguageItems(int languageFlagImage, String languageText, String languageCode) {
        this.languageFlagImage = languageFlagImage;
        this.languageText = languageText;
        this.languageCode = languageCode;
    }

    int getLanguageFlagImage() {
        return languageFlagImage;
    }

    public void setLanguageFlagImage(int languageFlagImage) {
        this.languageFlagImage = languageFlagImage;
    }

    String getLanguageText() {
        return languageText;
    }

    public void setLanguageText(String languageText) {
        this.languageText = languageText;
    }

    String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}
