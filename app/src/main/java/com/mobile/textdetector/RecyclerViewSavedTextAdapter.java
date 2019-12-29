package com.mobile.textdetector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewSavedTextAdapter extends RecyclerView.Adapter<RecyclerViewSavedTextAdapter.SavedTextViewHolder> implements Filterable {

    private Context context;
    private List<SavedTextItem> savedTextList, selectedText, filterTextList;
    private OnItemLongClick onItemLongClickReceiver;
    private OnItemClick onItemClickReceiver;
    static final String SAVED_TEXT="SAVED_TEXT";
    static final String SAVED_TEXT_ID="SAVED_TEXT_ID";
    static final String SAVED_IMAGE_PATH="SAVED_IMAGE_PATH";
    static final String SAVED_TEXT_TRANSLATED="SAVED_TEXT_TRANSLATED";
    static final String SAVED_TEXT_TRANSLATED_CODE="SAVED_TEXT_TRANSLATED_CODE";

    RecyclerViewSavedTextAdapter(Context context, List<SavedTextItem> savedTextList, List<SavedTextItem> selectedText) {
        this.context = context;
        this.savedTextList = savedTextList;
        this.selectedText = selectedText;
        filterTextList = new ArrayList<>();
    }

    @NonNull
    @Override
    public SavedTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.savedtextitem, parent, false);
        return new SavedTextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SavedTextViewHolder holder, int position) {
        //Apply Animation to views
        holder.linearLayoutCompat.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));

        final SavedTextItem item = savedTextList.get(position);

        if (item.getImage() == null) {
            holder.imageView.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(item.getImage())
                    .centerCrop()
                    //.placeholder(R.drawable.loading_spinner)
                    .into(holder.imageView);
        }
        holder.textViewID.setText(String.valueOf(item.getTextID()));
        holder.textView.setText(item.getText());
        holder.textViewTranslated.setText(item.getTextTranslated());
        holder.textViewTranslatedCode.setText(item.getTextTranslatedCode());
        holder.textViewDate.setText(item.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this will make sure when user long click to select an item
                //user will be able to just click to select next item in recycler view
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean itemIsLongClicked = sharedPreferences.getBoolean(SavedTextActivity.ITEM_IS_LONG_CLICKED, false);

                if (itemIsLongClicked) {
                    if (selectedText.contains(item)) {
                        selectedText.remove(item);
                        unHighlightView(holder);
                    } else {
                        selectedText.add(item);
                        highlightView(holder);
                    }
                    onItemClickReceiver.onItemClick();
                } else {
                    Intent intent = new Intent(context, DisplayTextActivity.class);
                    intent.putExtra(SAVED_TEXT, holder.textView.getText());
                    intent.putExtra(SAVED_TEXT_ID, holder.textViewID.getText());
                    intent.putExtra(SAVED_IMAGE_PATH, item.getImage());
                    intent.putExtra(SAVED_TEXT_TRANSLATED, holder.textViewTranslated.getText());
                    intent.putExtra(SAVED_TEXT_TRANSLATED_CODE, holder.textViewTranslatedCode.getText());
                    context.startActivity(intent);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (selectedText.contains(item)) {
                    selectedText.remove(item);
                    unHighlightView(holder);
                } else {
                    selectedText.add(item);
                    highlightView(holder);
                }
                onItemLongClickReceiver.onItemLongClick();

                return true;
            }
        });

        //when all item is selected
        if (selectedText.contains(item))
            highlightView(holder);
        else
            unHighlightView(holder);
    }

    @Override
    public int getItemCount() {
        return savedTextList.size();
    }

    SavedTextItem getTextAt(int position) {
        return savedTextList.get(position);
    }

    private void highlightView(SavedTextViewHolder holder) {
        holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.selected_item_background));
    }

    private void unHighlightView(SavedTextViewHolder holder) {
        holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.corner_radius));
    }

    void setActionModeReceiver(OnItemLongClick longClickReceiver, OnItemClick clickReceiver) {
        this.onItemLongClickReceiver = longClickReceiver;
        this.onItemClickReceiver = clickReceiver;
    }

    void selectAllText() {
        selectedText.clear();
        selectedText.addAll(savedTextList);
        notifyDataSetChanged();
    }

    void unSelectAllText() {
        selectedText.clear();
        notifyDataSetChanged();
    }

    void updateFilterList(List<SavedTextItem> filterTextList) {
        this.filterTextList.clear();
        this.filterTextList.addAll(filterTextList);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SavedTextItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(filterTextList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (SavedTextItem textItem : filterTextList) {
                    if (textItem.getText().toLowerCase().contains(filterPattern)) {
                        filteredList.add(textItem);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            savedTextList.clear();
            savedTextList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class SavedTextViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView imageView;
        private TextView textView, textViewTranslated, textViewTranslatedCode, textViewID, textViewDate;
        private RelativeLayout linearLayoutCompat;

        SavedTextViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayoutCompat = itemView.findViewById(R.id.linearLayout);
            imageView = itemView.findViewById(R.id.imageViewSavedText);
            textView = itemView.findViewById(R.id.textViewSavedText);
            textViewTranslated = itemView.findViewById(R.id.textViewSavedTextTranslated);
            textViewTranslatedCode = itemView.findViewById(R.id.textViewSavedTextTranslatedCode);
            textViewID = itemView.findViewById(R.id.textViewSavedTextID);
            textViewDate = itemView.findViewById(R.id.textViewSavedTextDate);
        }

    }

    /**
     * This interface will be called when an item is long clicked
     * to be able to highlight the selected item view
     */
    public interface OnItemLongClick {
        void onItemLongClick();
    }

    /**
     * This interface will be called when an item is clicked
     * and the Contextual Action Mode is Visible.
     * this will make sure when user long click to select an item
     * user will be able to click to select next item in recycler view
     */
    public interface OnItemClick {
        void onItemClick();
    }

}
