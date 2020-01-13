package com.mobile.textdetector;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.mobile.textdetector.recyclerviewmodels.SavedTextItem;
import com.mobile.textdetector.model.Repository;
import com.mobile.textdetector.util.LiveDataTestUtil;
import com.mobile.textdetector.util.TextDumbingUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import RoomDB.TextDetectorDao;
import RoomDB.TextDetectorDbTable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RepositoryTest extends TextDetectorDumbDBTest{

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private Repository repo;
    private TextDetectorDao detectorDao;

    @Before
    public void initBeforeTest(){
        detectorDao=mock(TextDetectorDao.class);
    }

    @Test
    public void checkRepo_init(){
        repo= Repository.getInstance();
        Assert.assertNotNull(repo);
        System.out.println("Repository isn't null");
    }

    @Test
    public void getAllDetectedText() throws Exception{
        TextDetectorDbTable dbTable=new TextDetectorDbTable("Anniversary gift",
                "Buy an anniversary gift.",
                "timmy timmy turner",
                "fr",
                "01/01/2020");
        List<TextDetectorDbTable> dummyData=new ArrayList<>();
        dummyData.add(dbTable);

        when(detectorDao.getAllDetectedText()).thenReturn(dummyData);

        MutableLiveData<List<SavedTextItem>> observedData=repo.getAllDetectedText();

        /*List<SavedTextItem> notes = TextDumbingUtil.SAVED_TEXT_ITEMS;

        LiveDataTestUtil<List<SavedTextItem>> liveDataTestUtil = new LiveDataTestUtil<>();

        MutableLiveData<List<SavedTextItem>> returnedData = new MutableLiveData<>();
        returnedData.setValue(notes);

        when(repo.getAllDetectedText()).thenReturn(returnedData);

        List<SavedTextItem> observedData = liveDataTestUtil.getValue(repo.getAllDetectedText());*/

        assertEquals(dbTable, observedData);
    }

    @Test
    public void deleteDetectedText() throws Exception{

    }

    @Test
    public void saveDetectedText() throws Exception{

    }

}
