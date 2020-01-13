package RoomDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TextDetectorDao {

    @Insert
    void addDetectedText(TextDetectorDbTable text);

    @Query("Select * from TextDetectorDbTable ORDER BY id DESC")
    List<TextDetectorDbTable> getAllDetectedText();

    @Delete
    void deleteDetectedText(TextDetectorDbTable text);

    @Update
    void updateDetectedText(TextDetectorDbTable text);

}
