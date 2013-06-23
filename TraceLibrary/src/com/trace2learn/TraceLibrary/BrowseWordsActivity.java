package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;

import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonItem;
import com.trace2learn.TraceLibrary.Database.LessonWord;

public class BrowseWordsActivity extends TraceListActivity {
    private List<LessonItem> source;  // list of all words
    private List<LessonItem> display; // list of items being displayed
    private LessonItemListAdapter adapter;

    private ListView    list;
    private TextView    lessonName;
    private Button      filterButton;
    private TextView    filterStatus;
    private ImageView   infoButton;
    private boolean    filtered;

    private LayoutInflater vi;
    
    private String  lessonId;
    private boolean userDefined;

    private boolean isAdmin;
    
    private boolean collectionsChanged = false; // does the browse collections page need to be refreshed?
    
    private enum ContextMenuItem {
        ADD_TO_LESSON (0, "Add to Collection"),
        EDIT_TAGS     (2, "Edit Tags"),
        MOVE_UP       (1, "Move Up"),
        MOVE_DOWN     (1, "Move Down"),
        DELETE        (1, "Remove from Collection");
        
        public final String text;
        public final int    privilege; // 0: anyone, 1: owner, 2: admin
        
        ContextMenuItem(int privilege, String text) {
            this.privilege = privilege;
            this.text      = text;
        }
    }

    private enum RequestCode {
        EDIT_TAGS,
        PHRASE_PRACTICE,
        ADD_TO_COLLECTION;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_words);
        vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        // Set admin privilege
        SharedPreferences prefs = getSharedPreferences(Toolbox.PREFS_FILE,
                MODE_PRIVATE);
        isAdmin = prefs.getBoolean(Toolbox.PREFS_IS_ADMIN, false);
        
        getViews();
        getHandlers();
        
        getWords();
        displayAllWords();
        registerForContextMenu(list);
        
        filtered = false;
    }
    
    private void getViews() {
        list = getListView();
        lessonName   = (TextView)  findViewById(R.id.lesson_name);
        filterButton = (Button)    findViewById(R.id.filterButton);
        filterStatus = (TextView)  findViewById(R.id.filterStatus);
        infoButton   = (ImageView) findViewById(R.id.infoButton);
    }
    
    private void getHandlers() {
        // set handler for info button - must be called after getViews and getWords
        infoButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), LessonNarrativeActivity.class);
                i.putExtra("ID", lessonId);
                startActivity(i);
            }
        });        
    }
    
    /**
     * Populate the source list with words
     */
    private void getWords() {
        lessonId = this.getIntent().getStringExtra("ID");
        if (lessonId == null) {
            userDefined = false;
            source = Toolbox.dba.getAllWords();
            lessonName.setText(R.string.all_words);
            infoButton.setVisibility(View.GONE);
        } else {
            Lesson les = Toolbox.dba.getLessonById(lessonId);
            String name = les.getLessonName();
            userDefined = les.isUserDefined();
            int size = les.length();

            // set lesson title
            if (size == 1) { lessonName.setText(name + " - " + size + " phrase"); }
            else { lessonName.setText(name + " - " + size + " phrases"); }

            // populate words
            source = les.getWords();
        }
    }
    
    /**
     * Set display list to source list, thus displaying all words
     */
    private void displayAllWords() {
        display = source;
        displayWords();
    }
    
    /**
     * Display the current display list
     */
    private void displayWords() {
        adapter = new LessonItemListAdapter(this, display, vi);
        setListAdapter(adapter);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    };


    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        super.onListItemClick(l, v, position, id);
        clickOnItem(display.get(position), position);
    }  
 
    /**
     * Opens the selected word in the practice activity
     * 
     * @param li the word
     * @param position the index of the word in the lesson or list
     * @param mode the mode to open, "trace" or "display"
     */
    private void clickOnItem(LessonItem li, int position) {
        Intent intent = new Intent();
        Bundle bun = new Bundle();

        bun.putString("wordId", li.getStringId());
        bun.putString("lessonID", lessonId);
        bun.putInt("index", position + 1);
        bun.putInt("collectionSize", display.size());

        intent.setClass(this, PhrasePracticeActivity.class);
        intent.putExtras(bun);
        startActivityForResult(intent, RequestCode.PHRASE_PRACTICE.ordinal());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Options");
        for (ContextMenuItem item : ContextMenuItem.values()) {
            if (!isAdmin && !userDefined && item.privilege >= 1) {
                // user does not own the lesson
                continue;
            } else if (!isAdmin && item.privilege >= 2) {
                // admin only option
                continue;
            }
            
            int ord = item.ordinal();
            if (item.equals(ContextMenuItem.DELETE) && lessonId == null) {
                menu.add(Menu.NONE, ord, ord, "Delete");
            } else {
                menu.add(Menu.NONE, ord, ord, item.text);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        LessonWord lw = (LessonWord) display.get(info.position);
        Log.e("MenuIndex",Integer.toString(menuItemIndex));
        Log.e("ListIndex",Integer.toString(info.position));

        final Context context = getApplicationContext();
        
        // Add to Collection
        if (menuItemIndex == ContextMenuItem.ADD_TO_LESSON.ordinal()) {
            addToCollection(lw.getStringId());
            return true;
        }

        // Edit Tags
        else if (menuItemIndex == ContextMenuItem.EDIT_TAGS.ordinal()) {
            Intent i = new Intent(this, TagActivity.class);
            i.putExtra("ID", lw.getStringId());
            i.putExtra("TYPE", "WORD");
            startActivityForResult(i, RequestCode.EDIT_TAGS.ordinal());
            return true;
        }

        // Delete
        else if (menuItemIndex == ContextMenuItem.DELETE.ordinal()) {
            final String id = lw.getStringId();
            if (lessonId == null) { // browsing all phrases - delete the phrase
                Boolean success = Toolbox.dba.deleteWord(id);
                if (!success) {
                    Toolbox.showToast(context, "Could not delete the phrase");
                    return false;
                } else {
                    Toolbox.showToast(context, "Successfully deleted");
                    startActivity(getIntent()); 
                    close();
                    return true;
                }
            } else { // remove from this lesson
                // check if phrase is part of any other collections
                if (!Toolbox.dba.isWordInManyLessons(id)) { // phrase is only in this collection
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.phrase_deletion);
                    builder.setMessage(R.string.phrase_deletion_text);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            collectionsChanged = true;
                            Boolean success = Toolbox.dba.deleteWord(id);
                            if (!success) {
                                Toolbox.showToast(context, "Could not remove the phrase");
                            } else {
                                Toolbox.showToast(context, "Successfully removed");
                                startActivity(getIntent());
                                close();
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    builder.show();
                    return true;
                } else {
                    collectionsChanged = true;
                    Boolean success = Toolbox.dba.removeWordFromLesson(lessonId, id);
                    if (!success) {
                        Toolbox.showToast(context, "Could not remove the phrase");
                        return false;
                    } else {
                        Toolbox.showToast(context, "Successfully removed");
                        startActivity(getIntent());
                        close();
                        return true;
                    }
                }
            }
        }

        // Move
        else if (menuItemIndex == ContextMenuItem.MOVE_UP.ordinal() ||
                 menuItemIndex == ContextMenuItem.MOVE_DOWN.ordinal()) {
            // going to swap sort values with the item above or below

            // need to get other item
            int otherPos;
            if (menuItemIndex == ContextMenuItem.MOVE_UP.ordinal()) {
                otherPos = info.position - 1;
            } else {
                otherPos = info.position + 1;
            }

            // check that item exists
            if (otherPos < 0) {
                Toolbox.showToast(context, "Cannot move this phrase up");
                return false;
            } else if (otherPos >= display.size()) {
                Toolbox.showToast(context, "Cannot move this phrase down");
                return false;
            }

            LessonWord other = (LessonWord) display.get(otherPos);
            boolean result;
            if (lessonId == null) { // browsing all words
                Log.i("Move", "Attempting to swap " + lw.getStringId() +
                        " and " + other.getStringId());
                result = Toolbox.dba.swapWords(lw.getStringId(), lw.getSort(), 
                                       other.getStringId(), other.getSort());
                if (result) {
                    // success, so update the local copy
                    Log.i("Move", "Success");
                    long temp = lw.getSort();
                    lw.setSort(other.getSort());
                    other.setSort(temp);
                    Collections.sort(display);
                    adapter.items = display;
                    adapter.notifyDataSetChanged();
                    return true;
                }
            } else { // viewing a specific lesson
                Log.i("Move", "Attempting to swap " + lw.getStringId() +
                        " and " + other.getStringId());
                result = Toolbox.dba.swapWordsInLesson(lessonId, lw.getStringId(), 
                                               other.getStringId());
                if (result) {
                    // success, so update the local copy
                    Log.i("Move", "Success");
                    LessonItem[] arr = new LessonItem[display.size()];
                    arr = display.toArray(arr);
                    
                    int lwIndex = display.indexOf(lw);
                    int otherIndex = display.indexOf(other);
                    LessonItem temp = arr[lwIndex];
                    arr[lwIndex] = arr[otherIndex];
                    arr[otherIndex] = temp;
                    
                    display = new ArrayList<LessonItem>(Arrays.asList(arr)); 
                    adapter.items = display;
                    adapter.notifyDataSetChanged();
                    
                    return true;
                }
            }
            Log.e("Move", "Failure!");
            Toolbox.showToast(context, "Move failed");
            return false;
        }

        return false;
    }
    
    private void addToCollection(String word) {
        Intent i = new Intent(getApplicationContext(), AddToCollectionActivity.class);
        i.putExtra("word", word);
        startActivityForResult(i, RequestCode.ADD_TO_COLLECTION.ordinal());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == RequestCode.EDIT_TAGS.ordinal() 
                && resultCode == RESULT_OK) {
            startActivity(getIntent());
            close();
        } else if (requestCode == RequestCode.PHRASE_PRACTICE.ordinal() && 
                resultCode == RESULT_OK) {
            int next = data.getExtras().getInt("next");
            if (next < display.size()) {
                clickOnItem(display.get(next), next);
            }
        } else if (requestCode == RequestCode.ADD_TO_COLLECTION.ordinal()) {
            collectionsChanged = (resultCode == RESULT_OK);
        }
    }
    
    // FILTER METHODS
    
    // depending on the state, shows the filter pop up or clears the filter
    public void onClickFilter(View view) {
        if (filtered) {
            clearFilter();
        } else {
            showFilterPopup();
        }
    }
    
    // displays the filter pop up
    private void showFilterPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Apply Filter");
        
        final EditText filterText = new EditText(this);
        builder.setView(filterText);
        
        builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String search = filterText.getText().toString();
                if (search.equals("")) {
                    hideKeyboard( filterText);
                    return;
                }

                // Filter action: keep matching items from display list
                // Note that it should be partial match for search terms 3
                // characters or more.
                ArrayList<LessonItem> newList = new ArrayList<LessonItem>();
                topLoop: for (LessonItem item : display) {
                    List<String> tags = item.getTags();
                    for (String tag : tags) {
                        if (Toolbox.containsMatch(1, tag, search)) {
                            newList.add(item);
                            continue topLoop;
                        }
                    }
                    Collection<String> values = item.getKeyValues().values();
                    for (String value : values) {
                        if (Toolbox.containsMatch(1, value, search)) {
                            newList.add(item);
                            continue topLoop;
                        }
                    }
                }
                display = newList;
                displayWords();
                
                // Set state to filtered
                filterButton.setText(R.string.clear_filter);
                filtered = true;
                filterStatus.setText("Filter: " + search);
                hideKeyboard(filterText);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                hideKeyboard(filterText);
            }
        });
        
        AlertDialog dialog = builder.create();
        
        // show the keyboard
        Toolbox.showKeyboard(dialog);
        dialog.show();

    }
    
    // clears the filter
    private void clearFilter() {
        displayAllWords();
        filterButton.setText(R.string.filter);
        filtered = false;
        filterStatus.setText(R.string.filter_none);
    }
    
    private void hideKeyboard(View view) {
        Toolbox.hideKeyboard(this, view);
    }    
    
    @Override
    public void onBackPressed() {
        close();
    }

    private void close() {
        if (collectionsChanged) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

}
