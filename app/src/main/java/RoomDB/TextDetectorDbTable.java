package RoomDB;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class TextDetectorDbTable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String text;

    @ColumnInfo
    private String translatedText;

    @ColumnInfo
    private String image;

    @ColumnInfo
    private String translatedTextCode;

    @ColumnInfo
    private String date;

    public TextDetectorDbTable(String text, String translatedText, String translatedTextCode, String image, String date) {
        this.text = text;
        this.translatedText = translatedText;
        this.translatedTextCode = translatedTextCode;
        this.image = image;
        this.date = date;
    }

    @Ignore
    public TextDetectorDbTable(int id,String text, String translatedText, String translatedTextCode, String image, String date) {
        this.id=id;
        this.text = text;
        this.translatedText = translatedText;
        this.translatedTextCode = translatedTextCode;
        this.image = image;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public String getTranslatedTextCode() {
        return translatedTextCode;
    }

    public void setTranslatedTextCode(String translatedTextCode) {
        this.translatedTextCode = translatedTextCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    //This make sure you are comparing the actual value you want.
    //according to your code you will be comparing ID and Text when Testing
    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null){
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }
        TextDetectorDbTable dbTable = (TextDetectorDbTable) obj;
        return dbTable.getId() == getId() && dbTable.getText().equals(getText());
    }
}
