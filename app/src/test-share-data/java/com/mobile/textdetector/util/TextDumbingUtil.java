package com.mobile.textdetector.util;

import com.mobile.textdetector.recyclerviewmodels.SavedTextItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextDumbingUtil {

    public static int ID=1;
    public static int ID2=2;
    public static String TEXT="this text is a dumbing data";
    public static String TEXT_UPDATED="this text is a dumbing updated data";
    public static String TRANSLATED_TEXT="english";
    public static String TRANSLATED_TEXT_UPDATED="french";
    public static String TRANSLATED_TEXT_CODE="en";
    public static String TRANSLATED_TEXT_CODE_UPDATED="fr";
    public static String IMAGE="the image url";
    public static String IMAGE_UPDATED="the image url updated";
    public static String DATE="25/12/2019";
    public static String DATE_UPDATED="26/12/2019";
    public static List<SavedTextItem> SAVED_TEXT_ITEMS= Collections.unmodifiableList(
            new ArrayList<SavedTextItem>(){{
                add(new SavedTextItem(
                        1,
                        "Take out the trash",
                        null,
                        null,
                        null,
                        "01/01/2020"));
                add(new SavedTextItem(
                        2,
                        "Anniversary gift",
                        "Buy an anniversary gift.",
                        null,
                        null,
                        "01/01/2020"));
                add(new SavedTextItem(
                        3,
                        "Anniversary gift",
                        "Buy an anniversary gift.",
                        "timmy timmy turner",
                        "fr",
                        "01/01/2020"));
            }}
    );
}
