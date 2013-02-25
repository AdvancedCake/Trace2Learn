package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.DbAdapter;
import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonItem;
import com.trace2learn.TraceLibrary.Database.LessonWord;

public class BrowseWordsActivity extends ListActivity {
    private DbAdapter dba;
    private List<LessonItem> source;  // list of all characters
    private List<LessonItem> display; // list of items being displayed
    private LessonItemListAdapter adapter;

    private ListView list;
    private TextView title;
    private Button   filterButton;
    private TextView filterStatus;
    private boolean  filtered;

    private LayoutInflater vi;
    private String lessonID;

    // Lesson popup views
    private View layout;
    private PopupWindow window;
    private LessonWord lw;

    private static final String[] menuItems = { "Add to Lesson",
                                                "Edit Tags",
                                                "Move Up",
                                                "Move Down",
                                                "Delete" };
    private static enum menuItemsInd { Add2Lesson,
                                       EditTags,
                                       MoveUp,
                                       MoveDown,
                                       Delete }
    private static enum requestCodeENUM { EditTag, PhrasePractice }; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_lesson);
        dba = new DbAdapter(this);
        dba.open();
        vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        getViews();
        
        getWords();
        displayAllWords();

        registerForContextMenu(list);
        
        filtered = false;
    }
    
    private void getViews() {
        list = getListView();
        title        = (TextView) findViewById(R.id.instructions);
        filterButton = (Button)   findViewById(R.id.filterButton);
        filterStatus = (TextView) findViewById(R.id.filterStatus);
    }
    
    /**
     * Populate the source list with words
     */
    private void getWords() {
        lessonID = this.getIntent().getStringExtra("ID");
        if(lessonID == null){
            List<String> ids = dba.getAllWordIds();
            source = new ArrayList<LessonItem>(ids.size());
            for(String id : ids){
                LessonWord word = dba.getWordById(id);
                source.add(word);
            }
        }
        else{
            Lesson les = dba.getLessonById(lessonID);
            String name = les.getLessonName();
            int size = les.length();

            // set lesson title
            if (size == 1) { title.setText(name + ": " + size + " word"); }
            else { title.setText(name + ": " + size + " words"); }

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
        Collections.sort(display);
        adapter = new LessonItemListAdapter(this, display, vi);
        setListAdapter(adapter);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dba.close();
    };


    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        super.onListItemClick(l, v, position, id);
        clickOnItem(display.get(position), position, "trace"); // start in trace mode
    }  
 
    /**
     * Opens the selected word in the practice activity
     * 
     * @param li the word
     * @param position the index of the word in the lesson or list
     * @param mode the mode to open, "trace" or "display"
     */
    private void clickOnItem(LessonItem li, int position, String mode){
        Intent intent = new Intent();
        Bundle bun = new Bundle();

        bun.putString("mode", mode);
        bun.putString("wordId", li.getStringId());
        bun.putString("lessonID", lessonID);
        bun.putInt("index", position + 1);
        bun.putInt("collectionSize", display.size());

        intent.setClass(this, PhrasePracticeActivity.class);
        intent.putExtras(bun);
        startActivityForResult(intent, requestCodeENUM.PhrasePractice.ordinal());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Options");
        for (int i = 0; i<menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        lw = (LessonWord) display.get(info.position);
        Log.e("MenuIndex",Integer.toString(menuItemIndex));
        Log.e("ListIndex",Integer.toString(info.position));

        Context context = getApplicationContext();
        
        // add to collection
        if(menuItemIndex == menuItemsInd.Add2Lesson.ordinal()){
            initiatePopupWindow();
            return true;
        }

        else if(menuItemIndex == menuItemsInd.EditTags.ordinal()){
            Intent i = new Intent(this, TagActivity.class);
            i.putExtra("ID", lw.getStringId());
            i.putExtra("TYPE", "WORD");
            startActivityForResult(i, requestCodeENUM.EditTag.ordinal());
            return true;
        }

        // delete
        else if(menuItemIndex == menuItemsInd.Delete.ordinal()){
            String id = lw.getStringId();
            Boolean success = dba.deleteWord(id);
            Log.d("Result",success.toString());
            if(!success){
                Toolbox.showToast(context, "Could not delete the word");
                return false;
            }
            else{
                Toolbox.showToast(context, "Successfully deleted");
                startActivity(getIntent()); 
                finish();
                return true;
            }
        }

        // move
        else if (menuItemIndex == menuItemsInd.MoveUp.ordinal() ||
                 menuItemIndex == menuItemsInd.MoveDown.ordinal()) {
            // going to swap sort values with the item above or below

            // need to get other item
            int otherPos;
            if (menuItemIndex == menuItemsInd.MoveUp.ordinal()) {
                otherPos = info.position - 1;
            } else {
                otherPos = info.position + 1;
            }

            // check that item exists
            if (otherPos < 0) {
                Toolbox.showToast(context, "Cannot move this word up");
                return false;
            } else if (otherPos >= display.size()) {
                Toolbox.showToast(context, "Cannot move this word down");
                return false;
            }

            LessonWord other = (LessonWord) display.get(otherPos);
            boolean result;
            if (lessonID == null) { // browsing all words
                result = dba.swapWords(lw.getStringId(), lw.getSort(), 
                                       other.getStringId(), other.getSort());
                if (result) {
                    // success, so update the local copy
                    double temp = lw.getSort();
                    lw.setSort(other.getSort());
                    other.setSort(temp);
                    Collections.sort(display);
                    adapter._items = display;
                    adapter.notifyDataSetChanged();
                    return true;
                }
            } else { // viewing a specific lesson
                result = dba.swapWordsInLesson(lessonID, lw.getStringId(), 
                                               other.getStringId());
                if (result) {
                    // success, so update the local copy
                    LessonItem[] arr = new LessonItem[display.size()];
                    arr = display.toArray(arr);
                    
                    int lwIndex = display.indexOf(lw);
                    int otherIndex = display.indexOf(other);
                    LessonItem temp = arr[lwIndex];
                    arr[lwIndex] = arr[otherIndex];
                    arr[otherIndex] = temp;
                    
                    display = new ArrayList<LessonItem>(Arrays.asList(arr)); 
                    adapter._items = display;
                    adapter.notifyDataSetChanged();
                    return true;
                }
            }
            Log.e("Move result", Boolean.toString(result));
            Toolbox.showToast(context, "Move failed");
            return false;
        }

        return false;
    }

    private void initiatePopupWindow(){
        try {
            Display display = getWindowManager().getDefaultDisplay(); 
            display.getWidth();
            int height = display.getHeight();  // deprecated
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            layout = inflater.inflate(R.layout.add_to_collection_popup,(ViewGroup) findViewById(R.id.popup_layout));
            layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            // create a 300px width and 470px height PopupWindow
            List<String> allLessons = dba.getAllLessonNames();
            Log.e("numLessons",Integer.toString(allLessons.size()));
            final ListView lessonList = (ListView)layout.findViewById(R.id.collectionlist);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,allLessons); 
            lessonList.setAdapter(adapter);
            window = new PopupWindow(layout, layout.getMeasuredWidth(), (int)(height*.8), true);
            // display the popup in the center
            window.showAtLocation(layout, Gravity.CENTER, 0, 0);

            lessonList.setOnItemClickListener(new OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position,long id) {     
                    String name = ((String)lessonList.getItemAtPosition(position));
                    Log.e("name",name);
                    String success = dba.addWordToLesson(name, lw.getStringId());
                    Log.e("adding word",success);
                    Toolbox.showToast(getApplicationContext(), "Successfully Added");
                    window.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSkipButtonClick(View view){
        window.dismiss();
    }

    public void onNewCollectionButtonClick(View view){
        Context  context  = getApplicationContext();
        EditText editText = (EditText)layout.findViewById(R.id.newcollection);
        
        String name = editText.getText().toString();
        if(name.equals("")){
            Toolbox.showToast(context, "You must name the lesson!");
            return;
        }
        Lesson lesson = new Lesson();
        lesson.setName(name);
        lesson.addWord(lw.getStringId());
        dba.addLesson(lesson);
        Toolbox.showToast(context, "Successfully Created");
        window.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestCodeENUM.EditTag.ordinal() 
                && resultCode == RESULT_OK) {
            startActivity(getIntent());
            finish();
        } else if (requestCode == requestCodeENUM.PhrasePractice.ordinal() && 
                resultCode == RESULT_OK) {
            int next = data.getExtras().getInt("next");
            if (next < display.size()) {
                clickOnItem(display.get(next), next, "trace");
            }
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
                    hideKeyboard(filterText);
                    return;
                }

                // Filter action: keep matching items from display list
                // Note that it should be partial match for search terms 3
                // characters or more.
                ArrayList<LessonItem> newList = new ArrayList<LessonItem>();
                topLoop: for (LessonItem item : display) {
                    List<String> tags = item.getTags();
                    for (String tag : tags) {
                        if (Toolbox.containsMatch(2, tag, search)) {
                            newList.add(item);
                            continue topLoop;
                        }
                    }
                    Collection<String> values = item.getKeyValues().values();
                    for (String value : values) {
                        if (Toolbox.containsMatch(2, value, search)) {
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
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
