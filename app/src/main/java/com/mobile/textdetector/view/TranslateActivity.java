package com.mobile.textdetector.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mobile.textdetector.R;
import com.mobile.textdetector.adapters.RecyclerViewLanguageAdapter;
import com.mobile.textdetector.recyclerviewmodels.LanguageItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TranslateActivity extends AppCompatActivity implements RecyclerViewLanguageAdapter.LanguageSelected {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewLanguage);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayoutSelectedLanguage);
        AppCompatImageView imageViewSelectedLanguage = findViewById(R.id.imageViewSelectedLanguage);
        TextView textViewSelectedLanguage = findViewById(R.id.textViewSelectedLanguage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        AdView adView = findViewById(R.id.adViewTranslate);

        setSupportActionBar(toolbar);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        List<LanguageItems> languageItems = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        languageItems.add(new LanguageItems(R.drawable.englishflag, "English", "en"));
        languageItems.add(new LanguageItems(R.drawable.spain_flag, "Spanish", "es"));
        languageItems.add(new LanguageItems(R.drawable.china_flag, "Chinese", "zh"));
        languageItems.add(new LanguageItems(R.drawable.germany_flag, "German", "de"));
        languageItems.add(new LanguageItems(R.drawable.france_flag, "French", "fr"));
        languageItems.add(new LanguageItems(R.drawable.vietnam, "Vietnamese", "vi"));
        languageItems.add(new LanguageItems(R.drawable.ukraine, "Ukrainian", "uk"));
        languageItems.add(new LanguageItems(R.drawable.turkey, "Turkish", "tr"));
        languageItems.add(new LanguageItems(R.drawable.thailand, "Thai", "th"));
        languageItems.add(new LanguageItems(R.drawable.india, "Telugu", "te"));
        languageItems.add(new LanguageItems(R.drawable.tamil, "Tamil", "ta"));
        languageItems.add(new LanguageItems(R.drawable.sweden, "Swedish", "sv"));
        languageItems.add(new LanguageItems(R.drawable.slovenia, "Slovenian", "sl"));
        languageItems.add(new LanguageItems(R.drawable.slovakia, "Slovak", "sk"));
        languageItems.add(new LanguageItems(R.drawable.serbia, "Serbian", "sr"));
        languageItems.add(new LanguageItems(R.drawable.russia, "Russian", "ru"));
        languageItems.add(new LanguageItems(R.drawable.romania, "Romanian", "ro"));
        languageItems.add(new LanguageItems(R.drawable.pakistan, "Punjabi", "pa"));
        languageItems.add(new LanguageItems(R.drawable.portugal, "Portuguese", "pt"));
        languageItems.add(new LanguageItems(R.drawable.poland, "Polish", "pl"));
        languageItems.add(new LanguageItems(R.drawable.arab, "Persian", "fa"));
        languageItems.add(new LanguageItems(R.drawable.norway, "Norwegian", "no"));
        languageItems.add(new LanguageItems(R.drawable.nepal, "Nepali", "ne"));
        languageItems.add(new LanguageItems(R.drawable.india, "Marathi", "mr"));
        languageItems.add(new LanguageItems(R.drawable.india, "Malayalam", "ml"));
        languageItems.add(new LanguageItems(R.drawable.malaysia, "Malay", "ms"));
        languageItems.add(new LanguageItems(R.drawable.macedonia, "Macedonian", "mk"));
        languageItems.add(new LanguageItems(R.drawable.lithuania, "Lithuanian", "lt"));
        languageItems.add(new LanguageItems(R.drawable.latvia, "Latvian", "lv"));
        languageItems.add(new LanguageItems(R.drawable.laos, "Lao", "lo"));
        languageItems.add(new LanguageItems(R.drawable.south_korea, "Korean", "ko"));
        languageItems.add(new LanguageItems(R.drawable.cambodia, "Khmer", "km"));
        languageItems.add(new LanguageItems(R.drawable.india, "Kannada", "kn"));
        languageItems.add(new LanguageItems(R.drawable.japan, "Japanese", "ja"));
        languageItems.add(new LanguageItems(R.drawable.italy, "Italian", "it"));
        languageItems.add(new LanguageItems(R.drawable.indonesia, "Indonesian", "id"));
        languageItems.add(new LanguageItems(R.drawable.iceland, "Icelandic", "is"));
        languageItems.add(new LanguageItems(R.drawable.hungary, "Hungarian", "hu"));
        languageItems.add(new LanguageItems(R.drawable.india, "Hindi", "hi"));
        languageItems.add(new LanguageItems(R.drawable.israel, "Hebrew", "iw"));
        languageItems.add(new LanguageItems(R.drawable.india, "Gujarati", "gu"));
        languageItems.add(new LanguageItems(R.drawable.greece, "Greek", "el"));
        languageItems.add(new LanguageItems(R.drawable.finland, "Finnish", "fi"));
        languageItems.add(new LanguageItems(R.drawable.philippines, "Filipino", "fil"));
        languageItems.add(new LanguageItems(R.drawable.estonia, "Estonian", "et"));
        languageItems.add(new LanguageItems(R.drawable.netherlands_dutch, "Dutch", "nl"));
        languageItems.add(new LanguageItems(R.drawable.denmark_danish, "Danish", "da"));
        languageItems.add(new LanguageItems(R.drawable.czech, "Czech", "cs"));
        languageItems.add(new LanguageItems(R.drawable.croatia, "Croatian", "hr"));
        languageItems.add(new LanguageItems(R.drawable.catalan, "Catalan", "ca"));
        languageItems.add(new LanguageItems(R.drawable.bulgaria, "Bulgarian", "bg"));
        languageItems.add(new LanguageItems(R.drawable.bangladesh, "Bengali", "bn"));
        languageItems.add(new LanguageItems(R.drawable.belarus, "Belorussian", "be"));
        languageItems.add(new LanguageItems(R.drawable.armenia, "Armenian", "hy"));
        languageItems.add(new LanguageItems(R.drawable.saudi_arabia, "Arabic", "ar"));
        languageItems.add(new LanguageItems(R.drawable.albania_flag, "Albanian", "sq"));
        languageItems.add(new LanguageItems(R.drawable.south_africa, "Afrikaans", "af"));
        languageItems.add(new LanguageItems(R.drawable.brazil, "Portuguese", "pt"));
        languageItems.add(new LanguageItems(R.drawable.mexico, "Spanish", "es"));
        languageItems.add(new LanguageItems(R.drawable.israel, "Yiddish", "yi"));

        //Displays items alphabetically in recycler view
        Collections.sort(languageItems, new Comparator<LanguageItems>() {
            @Override
            public int compare(LanguageItems o1, LanguageItems o2) {
                return o1.getLanguageText().compareToIgnoreCase(o2.getLanguageText());
            }
        });

        RecyclerViewLanguageAdapter recyclerViewLanguageAdapter = new RecyclerViewLanguageAdapter(this, this, languageItems);

        recyclerView.setAdapter(recyclerViewLanguageAdapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int selectedLanguageFlag = sharedPreferences.getInt(RecyclerViewLanguageAdapter.SELECTED_LANGUAGE_FLAG, 0);
        String selectedLanguageText = sharedPreferences.getString(RecyclerViewLanguageAdapter.SELECTED_LANGUAGE_TEXT, "");

        if (selectedLanguageFlag != 0) {
            relativeLayout.setVisibility(View.VISIBLE);
            imageViewSelectedLanguage.setImageResource(selectedLanguageFlag);
            textViewSelectedLanguage.setText(selectedLanguageText);
        }
    }

    @Override
    public void onLanguageSelected() {
        finish();
    }
}
