package edu.upenn.cis573.Trace2Win;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import edu.upenn.cis573.Trace2Win.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.Database.Lesson;
import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.Database.LessonItem;
import edu.upenn.cis573.Trace2Win.Database.LessonWord;
import edu.upenn.cis573.Trace2Win.Database.LessonItem.ItemType;

public class BrowseWordsActivity extends ListActivity {
    private DbAdapter dba;
    private LessonItemListAdapter adapter;
    private List<LessonItem> items;
    private View layout;
    private PopupWindow window;
    private LessonWord lw;
    private long lessonID;
    
    private boolean filtered;
	private TextView filterStatus;
	
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

        //Set up the ListView
        items = new ArrayList<LessonItem>(); //items to show in ListView to choose from 
        lessonID = this.getIntent().getLongExtra("ID", -1);
        if(lessonID==-1){

            List<Long> ids = dba.getAllWordIds();
            for(long id : ids){
                LessonItem word = dba.getWordById(id);
                items.add(word);
            }
            Collections.sort(items);
        }
        else{
            Lesson les = dba.getLessonById(lessonID);
            String name = les.getLessonName();
            int size = les.length();

            // set lesson title
            TextView title = (TextView)findViewById(R.id.instructions);
            if (size == 1) { title.setText(name + ": " + size + " word"); }
            else { title.setText(name + ": " + size + " words"); }

            // populate words
            items = les.getWords();
        }
        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adapter = new LessonItemListAdapter(this, items, vi);
        setListAdapter(adapter);

        registerForContextMenu(getListView());
        
        filtered = false;
        filterStatus = (TextView) findViewById(R.id.filterStatus);
    }


    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        super.onListItemClick(l, v, position, id);
        String mode = "display";
        if (lessonID != -1) { // looking at a lesson - start in trace mode
            mode = "trace";
        }
        clickOnItem(items.get(position), position, mode);
    }  

    //when character is clicked, it starts the display mode for that char
    public void clickOnItem(LessonItem li, int position, String mode){
        Intent intent = new Intent();
        Bundle bun = new Bundle();

        bun.putString("mode", mode);
        bun.putLong("wordId", li.getId());
        bun.putLong("lessonID", lessonID);
        bun.putInt("index", position + 1);
        bun.putInt("collectionSize", items.size());

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
        lw = (LessonWord) items.get(info.position);
        Log.e("MenuIndex",Integer.toString(menuItemIndex));
        Log.e("ListIndex",Integer.toString(info.position));

        // add to collection
        if(menuItemIndex == menuItemsInd.Add2Lesson.ordinal()){
            initiatePopupWindow();
            return true;
        }

        else if(menuItemIndex == menuItemsInd.EditTags.ordinal()){
            Intent i = new Intent(this, TagActivity.class);
            i.putExtra("ID", lw.getId());
            i.putExtra("TYPE", "WORD");
            startActivityForResult(i, requestCodeENUM.EditTag.ordinal());
            return true;
        }

        // delete
        else if(menuItemIndex == menuItemsInd.Delete.ordinal()){
            long id = lw.getId();
            long result = dba.deleteWord(id);
            Log.e("Result",Long.toString(result));
            if(result<0){
                showToast("Could not delete the word");
                return false;
            }
            else{
                showToast("Successfully deleted");
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
                showToast("Cannot move this word up");
                return false;
            } else if (otherPos >= items.size()) {
                showToast("Cannot move this word down");
                return false;
            }

            LessonWord other = (LessonWord) items.get(otherPos);
            boolean result;
            if (lessonID == -1) { // browsing all words
                result = dba.swapWords(lw.getId(), lw.getSort(), 
                                       other.getId(), other.getSort());
                if (result) {
                    // success, so update the local copy
                    double temp = lw.getSort();
                    lw.setSort(other.getSort());
                    other.setSort(temp);
                    Collections.sort(items);
                    adapter._items = items;
                    adapter.notifyDataSetChanged();
                    return true;
                }
            } else { // viewing a specific lesson
                result = dba.swapWordsInLesson(lessonID, lw.getId(), 
                                               other.getId());
                if (result) {
                    // success, so update the local copy
                    LessonItem[] arr = new LessonItem[items.size()];
                    arr = items.toArray(arr);
                    
                    int lwIndex = items.indexOf(lw);
                    int otherIndex = items.indexOf(other);
                    LessonItem temp = arr[lwIndex];
                    arr[lwIndex] = arr[otherIndex];
                    arr[otherIndex] = temp;
                    
                    items = new ArrayList<LessonItem>(Arrays.asList(arr)); 
                    adapter._items = items;
                    adapter.notifyDataSetChanged();
                    return true;
                }
            }
            Log.e("Move result", Boolean.toString(result));
            showToast("Move failed");
            return false;
        }

        return false;
    }

    public void showToast(String msg){
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
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
                    long success = dba.addWordToLesson(name, lw.getId());
                    Log.e("adding word",Long.toString(success));
                    showToast("Successfully Added");
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
        EditText editText = (EditText)layout.findViewById(R.id.newcollection);
        Editable edit = editText.getText();
        String name = edit.toString();
        if(name.equals("")){
            showToast("You must name the lesson!");
            return;
        }
        Lesson lesson = new Lesson();
        lesson.setPrivateTag(name);
        lesson.addWord(lw.getId());
        dba.addLesson(lesson);
        showToast("Successfully Created");
        window.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestCodeENUM.EditTag.ordinal() 
                && resultCode == RESULT_OK) {
            // re-launch the current activity to reflect the changes of tags
            // we could use recreate() instead, but note that it is supported since API 11.
            // TODO: This implementation is quite wasting times. We could just update ArrayList and ListView 
            startActivity(getIntent());
            finish();
        } else if (requestCode == requestCodeENUM.PhrasePractice.ordinal() && 
                resultCode == RESULT_OK) {
            int next = data.getExtras().getInt("next");
            if (next < items.size()) {
                clickOnItem(items.get(next), next, "trace");
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
    public void showFilterPopup() {
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

                // Filter action: query for words and set word list
                Cursor c = dba.browseByTag(ItemType.WORD, search);
                List<Long> ids = new LinkedList<Long>();
                do {
                    if (c.getCount() == 0) {
                        Log.d(ACTIVITY_SERVICE, "zero rows");
                        break;
                    }
                    ids.add(c.getLong(c.getColumnIndexOrThrow(
                            DbAdapter.WORDTAG_ROWID)));
                } while (c.moveToNext());
                c.close();
                setWordList(ids);
                
                // Set state to filtered
                ((Button)findViewById(R.id.filterButton)).setText(R.string.clear_filter);
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
    public void clearFilter() {
    	if(lessonID == -1)
    		setWordList(dba.getAllWordIds());
    	else
    		setWordList(dba.getWordsFromLessonId(lessonID));
    	
    	((Button)findViewById(R.id.filterButton)).setText(R.string.filter);
        filtered = false;
        filterStatus.setText(R.string.filter_none);
    }
    
    // sets the list of items
    private void setWordList(List<Long> ids) {
        items = new ArrayList<LessonItem>();
        for(long id : ids) {
            Log.i("Found", "id: "+id);
            LessonItem word;
            try {
                word = dba.getWordById(id);
            } catch(Exception e) {
                word = new LessonWord();
                Log.d("SEARCH", "Word " + id + " not found in db");
            }
            items.add(word);
        }
        LayoutInflater vi = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        adapter = new LessonItemListAdapter(this, items, vi);
        setListAdapter(adapter);
    }
    
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
