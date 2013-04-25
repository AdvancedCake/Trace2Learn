package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.trace2learn.TraceLibrary.Database.DbAdapter;
import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonCharacter;
import com.trace2learn.TraceLibrary.Database.LessonItem;
import com.trace2learn.TraceLibrary.Database.LessonWord;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class CreateWordActivity extends Activity {
    
    private DbAdapter             dba;
    private LessonWord            newWord;
    private List<LessonItem>      source;  // list of all characters
    private List<LessonItem>      display; // list of items being displayed
    private LessonItemListAdapter charAdapter;
    private ArrayList<Bitmap>     currentChars;
    private ImageAdapter          imgAdapter;
    
    // Activity views
    private Gallery  gallery;
    private ListView charList;
    private TextView filterStatus;
    private Button   clearButton;
    private Button   delButton;
    private Button   cancelButton;
    private Button   saveButton;
    private Button   filterButton;
    
    private LayoutInflater vi;
    
    private boolean isAdmin;
    
    // Lesson popup views
    private PopupWindow window;
    private View        layout;
    
    private int     numChars;
    private boolean filtered;
    
    private static enum requestCodeENUM { EditTag };
    
    
    //initializes the list if all characters in the database
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filtered = false;
        numChars = 0;
        currentChars = new ArrayList<Bitmap>();
        setContentView(R.layout.create_word);
        vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        getViews();
        getHandlers();
        
        dba = new DbAdapter(this);
        dba.open();

        SharedPreferences prefs = getSharedPreferences(Toolbox.PREFS_FILE,
                MODE_PRIVATE);
        isAdmin = prefs.getBoolean(Toolbox.PREFS_IS_ADMIN, false);
        
        filterStatus.setVisibility(View.GONE);
        
        imgAdapter = new ImageAdapter(this, currentChars);
        gallery.setSpacing(0);
        gallery.setAdapter(imgAdapter);

        newWord = new LessonWord();

        getChars();
        displayAllChars();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dba.close();
    }
    
    private void getViews() {
        gallery      = (Gallery)  findViewById(R.id.gallery);
        charList     = (ListView) findViewById(R.id.charList);
        filterStatus = (TextView) findViewById(R.id.filterStatus);
        clearButton  = (Button)   findViewById(R.id.clearButton);
        delButton    = (Button)   findViewById(R.id.delButton);
        cancelButton = (Button)   findViewById(R.id.cancelButton);
        saveButton   = (Button)   findViewById(R.id.saveButton);
        filterButton = (Button)   findViewById(R.id.filterButton);
    }
    
    private void getHandlers() {
        //when a char is clicked, it is added to the new word and added to the gallery
        charList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {     
                numChars++;
                LessonItem item = (LessonCharacter) charList.getItemAtPosition(position);
                String charId = item.getStringId();
                newWord.addCharacter(charId);
                Bitmap bitmap = BitmapFactory.buildBitmap(item, 64, 64);
                currentChars.add(bitmap);
                imgAdapter.update(currentChars);
                imgAdapter.notifyDataSetChanged();
                gallery.setSelection(numChars / 2);
            }
        });
        
        // clear button - clears the current word
        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                numChars = 0;
                newWord.clearCharacters();
                currentChars.clear();
                imgAdapter.update(currentChars);
                imgAdapter.notifyDataSetChanged();
            }
        });
        
        // delete button - deletes the last character in the word
        delButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numChars == 0) {
                    return;
                }
                numChars--;
                newWord.removeLastCharacter();
                currentChars.remove(currentChars.size() - 1);
                imgAdapter.update(currentChars);
                imgAdapter.notifyDataSetChanged();
                gallery.setSelection(numChars / 2);
            }
        });
        
        // filter button - either shows the filter popup or clears the filter
        filterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filtered) {
                    clearFilter();
                } else {
                    showFilterPopup();
                }
            }
        });
        
        // save button - prompts to add word to a lesson
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                if(newWord.length() > 0 && dba.addWord(newWord)){
                    Toolbox.showToast(context, "Word saved");
                    initiatePopupWindow();
                    return;
                }
                Toolbox.showToast(context, "Word is empty!");
            }
        });
        
        // cancel button - finishes this activity
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
    
    private void getChars() {
        List<String> ids = dba.getAllCharIds();
        source = new ArrayList<LessonItem>(ids.size());
        for (String id : ids) {
            LessonCharacter character = dba.getCharacterById(id);
            source.add(character);
        }
    }

    /**
     * Set display list to source list, thus displaying all characters
     */
    private void displayAllChars() {
        display = source;
        displayChars();      
    }

    /**
     * Display the current display list
     */
    private void displayChars() {
        Collections.sort(display);
        charAdapter = new LessonItemListAdapter(this, display, vi);
        charList.setAdapter(charAdapter);   
    }

    private void initiatePopupWindow() {
        try {
            Display display = getWindowManager().getDefaultDisplay();
            int height = display.getHeight();  // deprecated
            
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.add_to_collection_popup,
                    (ViewGroup) findViewById(R.id.popup_layout));
            layout.measure(View.MeasureSpec.UNSPECIFIED,
                    View.MeasureSpec.UNSPECIFIED);
            
            // create a 300px width and 470px height PopupWindow
            List<String> allLessons;
            if (isAdmin) {
                allLessons = dba.getAllLessonNames();
            } else {
                allLessons = dba.getAllUserLessonNames();
            }
            Log.e("numLessons", Integer.toString(allLessons.size()));
            final ListView lessonList = (ListView) layout.findViewById(R.id.collectionlist);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allLessons); 
            lessonList.setAdapter(adapter);
            window = new PopupWindow(layout, layout.getMeasuredWidth(), (int) (height * .8), true);

            // display the popup in the center
            window.showAtLocation(layout, Gravity.CENTER, 0, 0);

            lessonList.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position,long id) {     
                    String name = ((String)lessonList.getItemAtPosition(position));
                    Log.e("name",name);
                    String success = dba.addWordToLesson(name, newWord.getStringId());
                    Log.e("adding word",success);
                    window.dismiss();
                    createTags();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void lessonPopupOnClickSkip(View view) {
        window.dismiss();
        createTags();
    }
    
    public void lessonPopupOnClickNewLesson(View view) {
        EditText editText = (EditText)layout.findViewById(R.id.newcollection);
        Editable edit = editText.getText();
        String name = edit.toString();
        if (name.equals("")) {
            Toolbox.showToast(getApplicationContext(),
                    "You must name the collection!");
            return;
        }
        Lesson lesson = new Lesson(!isAdmin);
        lesson.setName(name);
        lesson.addWord(newWord.getStringId());
        dba.addLesson(lesson);
        window.dismiss();
        createTags();
    }
    
    private void createTags() {
        String id = newWord.getStringId();
        if (id != null) {
            Log.e("Passing this WordID", id);
            Intent i = new Intent(this, TagActivity.class);

            i.putExtra("ID", id);
            i.putExtra("TYPE", newWord.getItemType().toString());

            startActivityForResult(i, requestCodeENUM.EditTag.ordinal());
        } else {
            Toolbox.showToast(getApplicationContext(),
                    "Error: Save the word before adding tags");
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestCodeENUM.EditTag.ordinal()) {
            finish();
        }
    }
    
    // displays the filter popup
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
                displayChars();
                
                // Set state to filtered
                filterButton.setText(R.string.clear_filter);
                filtered = true;
                hideKeyboard(filterText);
                filterStatus.setText("Current filter: " + search);
                filterStatus.setVisibility(View.VISIBLE);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                hideKeyboard(filterText);
            }
        });
        
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();

    }
    
    // clears the filter
    private void clearFilter() {
        displayAllChars();
        filterButton.setText(R.string.filter);
        filtered = false;
        filterStatus.setVisibility(View.GONE);
    }
    
    //for testing purposes
    public LessonWord getWord(){
        return newWord;
    }
    
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}