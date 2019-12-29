package com.mobile.textdetector.model;

import org.junit.Test;

import RoomDB.TextDetectorDbTable;

import static com.mobile.textdetector.util.TextDumbingUtil.DATE;
import static com.mobile.textdetector.util.TextDumbingUtil.ID;
import static com.mobile.textdetector.util.TextDumbingUtil.IMAGE;
import static com.mobile.textdetector.util.TextDumbingUtil.TEXT;
import static com.mobile.textdetector.util.TextDumbingUtil.TRANSLATED_TEXT;
import static com.mobile.textdetector.util.TextDumbingUtil.TRANSLATED_TEXT_CODE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TextDetectorDbTableTest {

    @Test
    public void isTextEqual_returnTrue(){
        TextDetectorDbTable dbTable=new TextDetectorDbTable(ID,TEXT,TRANSLATED_TEXT,TRANSLATED_TEXT_CODE,IMAGE,DATE);
        TextDetectorDbTable dbTable2=new TextDetectorDbTable(ID,TEXT,TRANSLATED_TEXT,TRANSLATED_TEXT_CODE,IMAGE,DATE);

        assertEquals(dbTable,dbTable2);
        System.out.println("ID and Text are Equal");
    }

    @Test
    public void isTextNotEqual_returnTrue(){
        TextDetectorDbTable dbTable=new TextDetectorDbTable(ID,TEXT,TRANSLATED_TEXT,TRANSLATED_TEXT_CODE,IMAGE,DATE);
        TextDetectorDbTable dbTable2=new TextDetectorDbTable(2,TEXT,TRANSLATED_TEXT,TRANSLATED_TEXT_CODE,IMAGE,DATE);

        assertNotEquals(dbTable,dbTable2);
        System.out.println("ID and Text are Not Equal");
    }
}
