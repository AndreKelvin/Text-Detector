package com.mobile.textdetector;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;

import RoomDB.TextDetectorDao;
import RoomDB.TextDetectorDb;

public abstract class TextDetectorDumbDBTest {

    public TextDetectorDb detectorDb;

    public TextDetectorDao getDetectorDao() {
        return detectorDb.detectorDao();
    }

    //Build or connect to dumbing db
    @Before
    public void initTestDB(){
        detectorDb = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                TextDetectorDb.class
        ).build();
    }

    @After
    public void finish(){
        detectorDb.close();
    }

}
