package RoomDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities={TextDetectorDbTable.class},version = 1)
public abstract class TextDetectorDb extends RoomDatabase {

    public abstract TextDetectorDao detectorDao();

}
