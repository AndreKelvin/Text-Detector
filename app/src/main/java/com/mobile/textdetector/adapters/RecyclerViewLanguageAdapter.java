package com.mobile.textdetector.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.textdetector.recyclerviewmodels.LanguageItems;
import com.mobile.textdetector.R;

import java.util.List;

public class RecyclerViewLanguageAdapter extends RecyclerView.Adapter<RecyclerViewLanguageAdapter.LanguageViewHolder> {

    private List<LanguageItems> languageList;
    private Context context;
    private LanguageSelected languageSelected;
    public static final String SELECTED_LANGUAGE_FLAG="SELECTED_LANGUAGE_FLAG";
    public static final String SELECTED_LANGUAGE_TEXT="SELECTED_LANGUAGE_TEXT";
    public static final String SELECTED_LANGUAGE_CODE="SELECTED_LANGUAGE_CODE";

    public RecyclerViewLanguageAdapter(Context context, LanguageSelected languageSelected, List<LanguageItems> languageList) {
        this.context = context;
        this.languageSelected = languageSelected;
        this.languageList = languageList;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.languageitems, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        //Apply Animation to views
        holder.relativeLayout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));

        final LanguageItems items = languageList.get(position);

        holder.imageViewLanguage.setImageResource(items.getLanguageFlagImage());
        holder.textCountry.setText(items.getLanguageText());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SELECTED_LANGUAGE_FLAG, items.getLanguageFlagImage());
                editor.putString(SELECTED_LANGUAGE_TEXT, items.getLanguageText());
                editor.putString(SELECTED_LANGUAGE_CODE, items.getLanguageCode());
                editor.apply();

                languageSelected.onLanguageSelected();
            }
        });
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    class LanguageViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView imageViewLanguage;
        private TextView textCountry;
        private RelativeLayout relativeLayout;

        LanguageViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relativeLayoutLanguage);
            imageViewLanguage = itemView.findViewById(R.id.imageViewRecyclerLanguage);
            textCountry = itemView.findViewById(R.id.textViewRecyclerLanguage);

        }
    }

    //this interface will be called when ever a language is selected on the recycler view
    public interface LanguageSelected {
        void onLanguageSelected();
    }


}
