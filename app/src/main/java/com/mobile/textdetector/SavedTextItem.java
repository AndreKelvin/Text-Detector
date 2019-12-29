package com.mobile.textdetector;

public class SavedTextItem {

    private int textID;
    private String text;
    private String textTranslated;
    private String textTranslatedCode;
    private String image;
    private String date;

    SavedTextItem(int textID, String text, String image, String textTranslated, String textTranslatedCode, String date) {
        this.textID = textID;
        this.text = text;
        this.image = image;
        this.textTranslated=textTranslated;
        this.textTranslatedCode=textTranslatedCode;
        this.date= date;
    }

    int getTextID() {
        return textID;
    }

    public void setTextID(int textID) {
        this.textID = textID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    String getTextTranslated() {
        return textTranslated;
    }

    public void setTextTranslated(String textTranslated) {
        this.textTranslated = textTranslated;
    }

    String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String getTextTranslatedCode() {
        return textTranslatedCode;
    }

    public void setTextTranslatedCode(String textTranslatedCode) {
        this.textTranslatedCode = textTranslatedCode;
    }

    String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
