package com.mobile.textdetector.recyclerviewmodels;

public class SavedTextItem {

    private int textID;
    private String text;
    private String textTranslated;
    private String textTranslatedCode;
    private String image;
    private String date;

    public SavedTextItem(int textID, String text, String image, String textTranslated, String textTranslatedCode, String date) {
        this.textID = textID;
        this.text = text;
        this.image = image;
        this.textTranslated=textTranslated;
        this.textTranslatedCode=textTranslatedCode;
        this.date= date;
    }

    public int getTextID() {
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

    public String getTextTranslated() {
        return textTranslated;
    }

    public void setTextTranslated(String textTranslated) {
        this.textTranslated = textTranslated;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTextTranslatedCode() {
        return textTranslatedCode;
    }

    public void setTextTranslatedCode(String textTranslatedCode) {
        this.textTranslatedCode = textTranslatedCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
