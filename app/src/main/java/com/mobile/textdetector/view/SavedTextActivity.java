package com.mobile.textdetector.view;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.mobile.textdetector.R;
import com.mobile.textdetector.adapters.RecyclerViewSavedTextAdapter;
import com.mobile.textdetector.recyclerviewmodels.SavedTextItem;
import com.mobile.textdetector.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SavedTextActivity extends AppCompatActivity implements RecyclerViewSavedTextAdapter.OnItemClick, RecyclerViewSavedTextAdapter.OnItemLongClick {

    private RecyclerViewSavedTextAdapter textAdapter;
    private List<SavedTextItem> savedTextList, selectedText, selectedTextItem;
    private Snackbar snackBar;
    private List<String> deletedText, deletedTextTranslated, deletedTextTranslatedCode, deletedTextImage, deletedDate;
    private List<Integer> deletedID;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ActionMode actionMode;
    public static final String ITEM_IS_LONG_CLICKED = "ITEM_IS_LONG_CLICKED";
    private AppCompatTextView textViewNoData;
    private RecyclerView recyclerView;
    private ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_text);

        recyclerView = findViewById(R.id.recyclerViewSavedText);
        CoordinatorLayout coordinatorLayout = findViewById(R.id.recyclerParent);
        textViewNoData = findViewById(R.id.textViewNoData);
        Toolbar toolbar = findViewById(R.id.toolbar);
        AdView adView = findViewById(R.id.adViewSavedText);

        setSupportActionBar(toolbar);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        selectedText = new ArrayList<>();
        selectedTextItem = new ArrayList<>();

        deletedID = new ArrayList<>();
        deletedText = new ArrayList<>();
        deletedTextImage = new ArrayList<>();
        deletedTextTranslated = new ArrayList<>();
        deletedTextTranslatedCode = new ArrayList<>();
        deletedDate = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        textAdapter = new RecyclerViewSavedTextAdapter(this, selectedText);

        recyclerView.setAdapter(textAdapter);

        textAdapter.setActionModeReceiver(this, this);

        snackBar = Snackbar.make(coordinatorLayout, "Item Deleted", Snackbar.LENGTH_LONG);
        snackBar.setActionTextColor(Color.WHITE);

        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        viewModel.getAllDetectedText().observe(this, new Observer<List<SavedTextItem>>() {
            @Override
            public void onChanged(List<SavedTextItem> savedTextItems) {
                Log.d("MY_TAG","Activity Started");
                if (savedTextItems!=null){
                    Log.d("MY_TAG","Saved Text Items not null");
                    selectedText.clear();

                    textAdapter.setSavedTextList(savedTextItems);
                    savedTextList=textAdapter.getSavedTextList();

                    textAdapter.updateFilterList(savedTextList);
                    setRecyclerViewVisibility();
                }else {
                    Log.d("MY_TAG","Saved Text Items is null");
                    setRecyclerViewVisibility();
                }
            }
        });

        //Swipe Left or Right to Delete
        //Display Snack bar to undo delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(SavedTextActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(Color.RED)
                        .addActionIcon(R.drawable.ic_delete)
                        .addSwipeLeftLabel("Delete")
                        .addSwipeRightLabel("Delete")
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .setSwipeRightLabelColor(Color.WHITE)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
                //add each item to its own list and remove it from the main list
                deletedID.clear();
                deletedText.clear();
                deletedTextTranslated.clear();
                deletedTextTranslatedCode.clear();
                deletedTextImage.clear();

                final int position = viewHolder.getAdapterPosition();

                deletedID.add(textAdapter.getTextAt(position).getTextID());
                deletedText.add(textAdapter.getTextAt(position).getText());
                deletedTextTranslated.add(textAdapter.getTextAt(position).getTextTranslated());
                deletedTextTranslatedCode.add(textAdapter.getTextAt(position).getTextTranslatedCode());
                deletedTextImage.add(textAdapter.getTextAt(position).getImage());
                deletedDate.add(textAdapter.getTextAt(position).getDate());

                savedTextList.remove(position);
                textAdapter.notifyItemRemoved(position);
                textAdapter.updateFilterList(savedTextList);

                //delete the item from database only when the snack bar is Dismissed
                //and Undo button is not clicked
                snackBar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event != DISMISS_EVENT_ACTION) {
                            deleteDetectedText();
                        }
                    }
                });

                //add item back to the exact position of the main list when "Undo" is clicked
                snackBar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        savedTextList.add(position, new SavedTextItem(
                                deletedID.get(0),
                                deletedText.get(0),
                                deletedTextImage.get(0),
                                deletedTextTranslated.get(0),
                                deletedTextTranslatedCode.get(0),
                                deletedDate.get(0)));

                        textAdapter.notifyItemInserted(position);
                        textAdapter.updateFilterList(savedTextList);
                        Toast.makeText(SavedTextActivity.this, "Deleted Item Restored", Toast.LENGTH_SHORT).show();
                    }
                });
                snackBar.show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void deleteDetectedText(){
        viewModel.isTextDeleted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d("MY_TAG","Text Actually Deleted!!!");
                setRecyclerViewVisibility();
            }
        });

        viewModel.deleteDetectedText(
                selectedTextItem,
                deletedID,
                deletedText,
                deletedTextTranslated,
                deletedTextTranslatedCode,
                deletedTextImage,
                deletedDate);
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        viewModel.getAllDetectedText();
        //new GetTextFromDB(this).execute();
    }*/

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                setSearchBarVisibility(menu, searchItem, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setSearchBarVisibility(menu, searchItem, true);
                return true;
            }
        });

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                textAdapter.getFilter().filter(s);

                return true;
            }
        });

        return true;
    }

    //This make sure the expended search view takes the entire Action Bar space
    private void setSearchBarVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) {
                item.setVisible(visible);
            }
        }
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.delete_item, menu);
            actionMode.setTitle("Delete Item");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.deleteIcon:

                    for (int i = 0; i < selectedText.size(); i++) {
                        savedTextList.remove(selectedText.get(i));
                        textAdapter.updateFilterList(savedTextList);
                    }

                    /*
                    pass all selected text item to a new array list
                    because "actionMode.finish()" removes the Action Mode form tool bar
                    and calls "onDestroyActionMode" where "textAdapter.unSelectAllText()" is called
                    which will clear the Selected text item where by resulting to an empty list item
                    when Delete thread is running
                    so this new array list will get all selected text item before it's cleared
                    and will be used when Delete thread is running
                     */
                    selectedTextItem = new ArrayList<>(selectedText);

                    //delete the item from database only when the snack bar is Dismissed
                    //and Undo button is not clicked
                    snackBar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if (event != DISMISS_EVENT_ACTION) {
                                deleteDetectedText();
                            }
                        }
                    });

                    snackBar.setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //onResume();
                            viewModel.getAllDetectedText();
                            selectedText.clear();
                            //textAdapter.notifyDataSetChanged();
                            Toast.makeText(SavedTextActivity.this, "Deleted Item Restored", Toast.LENGTH_SHORT).show();
                        }
                    });
                    snackBar.show();

                    actionMode.finish();
                    disableItemClick();

                    return true;

                case R.id.selectAll:
                    textAdapter.selectAllText();
                    return true;

                case R.id.unSelectAll:
                    textAdapter.unSelectAllText();
                    actionMode.finish();

                    disableItemClick();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            textAdapter.unSelectAllText();
            actionMode = null;
            disableItemClick();
        }
    };

    @Override
    public void onItemLongClick() {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);

            editor = sharedPreferences.edit();
            editor.putBoolean(ITEM_IS_LONG_CLICKED, true);
            editor.apply();
        } else {
            if (selectedText.isEmpty()) {
                actionMode.finish();

                disableItemClick();
            }
        }
    }

    @Override
    public void onItemClick() {
        if (selectedText.isEmpty()) {
            actionMode.finish();

            disableItemClick();
        }
    }

    /**
     * when item on the recycler is unselected/unHighlighted
     * this change the shared Preferences value to false
     * which means no item is long clicked
     */
    private void disableItemClick() {
        editor = sharedPreferences.edit();
        editor.putBoolean(ITEM_IS_LONG_CLICKED, false);
        editor.apply();
    }

    private void setRecyclerViewVisibility() {
        if (!savedTextList.isEmpty()) {
            textViewNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            textViewNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    /*private static class GetTextFromDB extends AsyncTask<Void, Void, Void> {

        private WeakReference<SavedTextActivity> weakReference;

        GetTextFromDB(SavedTextActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            SavedTextActivity activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return null;
            }

            activity.savedTextList.clear();
            activity.selectedText.clear();

            LiveData<List<TextDetectorDbTable>> list = MainActivity.textDetectorDb.detectorDao().getAllDetectedText();
            for (TextDetectorDbTable text : list) {
                activity.savedTextList.add(new SavedTextItem(
                        text.getId(),
                        text.getText(),
                        text.getImage(),
                        text.getTranslatedText(),
                        text.getTranslatedTextCode(),
                        text.getDate()));
            }
            activity.textAdapter.updateFilterList(activity.savedTextList);
            //This Displays items on descending order in recycler view
            Collections.reverse(activity.savedTextList);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SavedTextActivity activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.textAdapter.notifyDataSetChanged();
            activity.setRecyclerViewVisibility();
        }
    }*/

    /*private static class DeleteTextFromDB extends AsyncTask<Void, Void, Void> {

        private WeakReference<SavedTextActivity> weakReference;

        DeleteTextFromDB(SavedTextActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            final SavedTextActivity activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return null;
            }

            //user clicks delete icon in action menu
            if (!activity.selectedTextItem.isEmpty()) {
                for (int i = 0; i < activity.selectedTextItem.size(); i++) {

                    TextDetectorDbTable detectorDbTable = new TextDetectorDbTable(
                            activity.selectedTextItem.get(i).getTextID(),
                            activity.selectedTextItem.get(i).getText(),
                            activity.selectedTextItem.get(i).getTextTranslated(),
                            activity.selectedTextItem.get(i).getTextTranslatedCode(),
                            activity.selectedTextItem.get(i).getImage(),
                            activity.selectedTextItem.get(i).getDate());

                    //Delete the actual image in storage/emulated/0/Android/data/com.mobile.textdetector/files/Pictures
                    if (activity.selectedTextItem.get(i).getImage() != null) {
                        File file = new File(activity.selectedTextItem.get(i).getImage());
                        file.delete();
                    }
                    MainActivity.textDetectorDb.detectorDao().deleteDetectedText(detectorDbTable);
                }
            } else {
                //User swipe to delete
                TextDetectorDbTable detectorDbTable = new TextDetectorDbTable(
                        activity.deletedID.get(0),
                        activity.deletedText.get(0),
                        activity.deletedTextTranslated.get(0),
                        activity.deletedTextTranslatedCode.get(0),
                        activity.deletedTextImage.get(0),
                        activity.deletedDate.get(0));

                //Delete the actual image in storage/emulated/0/Android/data/com.mobile.textdetector/files/Pictures
                if (activity.deletedTextImage.get(0) != null) {
                    File file = new File(activity.deletedTextImage.get(0));
                    file.delete();
                }
                MainActivity.textDetectorDb.detectorDao().deleteDetectedText(detectorDbTable);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final SavedTextActivity activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            if (activity.actionMode != null) {
                activity.actionMode.finish();
            }

            activity.textAdapter.notifyDataSetChanged();
            activity.setRecyclerViewVisibility();
            activity.selectedText.clear();
            activity.selectedTextItem.clear();
        }
    }*/
}
