package edu.upenn.cis350.Trace2Learn;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import edu.upenn.cis350.Trace2Learn.Database.DbAdapter;
import edu.upenn.cis350.Trace2Learn.Database.Lesson;
import edu.upenn.cis350.Trace2Learn.Database.LessonCharacter;
import edu.upenn.cis350.Trace2Learn.Database.LessonItem;
import edu.upenn.cis350.Trace2Learn.Database.LessonWord;

public class CreateWordActivity extends Activity {
    
    private DbAdapter dba;
    private LessonWord newWord;
    private ListView charList, lessonList;
    private ArrayList<LessonItem> items;
    private LessonItemListAdapter charAdapter;
    private ArrayList<Bitmap> currentChars;
    private Gallery gallery;
    private ImageAdapter imgAdapter;
    private int numChars;
    private PopupWindow window;
    private View layout;
    private boolean saved;
    
    
    //initializes the list if all characters in the database
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saved = false;
        numChars = 0;
        currentChars = new ArrayList<Bitmap>();
        setContentView(R.layout.create_word);
        dba = new DbAdapter(this);
        dba.open();
        
        imgAdapter = new ImageAdapter(this,currentChars);
        gallery = (Gallery)findViewById(R.id.gallery);
        gallery.setSpacing(0);
        
        gallery.setAdapter(imgAdapter);
        
        charList = (ListView)findViewById(R.id.charslist);

        newWord = new LessonWord();
        items = new ArrayList<LessonItem>();
        List<Long> ids = dba.getAllCharIds();
        for(long id : ids) {
            LessonItem character = dba.getCharacterById(id);
            character.setTagList(dba.getCharacterTags(id));
            items.add(character);
        }
        LayoutInflater vi = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        charAdapter = new LessonItemListAdapter(this, items, vi);
        charList.setAdapter(charAdapter);
        //dba.close();
        
        // adding a TextWatcher to detect when user inputs text
        ((EditText) findViewById(R.id.searchBox)).addTextChangedListener(
                new TextWatcher() {
            public void afterTextChanged(Editable arg0) {
                // not used
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // not used
            }

            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                String search = s.toString();
                if (search.equals("")) {
                    // clear the filter
                    onClickClear(null);
                }
                Cursor c = dba.getAllChars(search);
                List<Long> ids = new LinkedList<Long>();
                do {
                    if (c.getCount() == 0) {
                        Log.d(ACTIVITY_SERVICE, "zero rows");
                        break;
                    }
                    ids.add(c.getLong(c.getColumnIndexOrThrow(
                            DbAdapter.CHARTAG_ROWID)));
                } while (c.moveToNext());
                setCharList(ids);
            }
        });
        
        //when a char is clicked, it is added to the new word and added to the gallery
        charList.setOnItemClickListener(new OnItemClickListener() {
    
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {     
                numChars++;
                long charId = ((LessonCharacter)charList.getItemAtPosition(position)).getId();
                newWord.addCharacter(charId);
                LessonItem item = (LessonCharacter)charList.getItemAtPosition(position);
                Bitmap bitmap = BitmapFactory.buildBitmap(item, 64, 64);
                currentChars.add(bitmap);
                imgAdapter.update(currentChars);
                imgAdapter.notifyDataSetChanged();
                gallery.setSelection(numChars/2);
            }
        });
        
    }
    
    private void initiatePopupWindow(){
        try {
            Display display = getWindowManager().getDefaultDisplay(); 
            int width = display.getWidth();  // deprecated
            int height = display.getHeight();  // deprecated
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            layout = inflater.inflate(R.layout.add_to_collection_popup,(ViewGroup) findViewById(R.id.popup_layout));
            layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            // create a 300px width and 470px height PopupWindow
            List<String> allLessons = dba.getAllLessonNames();
            Log.e("numLessons",Integer.toString(allLessons.size()));
            lessonList = (ListView)layout.findViewById(R.id.collectionlist);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,allLessons); 
            lessonList.setAdapter(adapter);
            window = new PopupWindow(layout, layout.getMeasuredWidth(), (int)(height*.8), true);
            // display the popup in the center
            window.showAtLocation(layout, Gravity.CENTER, 0, 0);
    
            lessonList.setOnItemClickListener(new OnItemClickListener() {
                
                public void onItemClick(AdapterView<?> parent, View view, int position,long id) {     
                   String name = ((String)lessonList.getItemAtPosition(position));
                   Log.e("name",name);
                   long success = dba.addWordToLesson(name, newWord.getId());
                   Log.e("adding word",Long.toString(success));
                   window.dismiss();
                }
            });
            
            
            /*mResultText = (TextView) layout.findViewById(R.id.server_status_text);
            Button cancelButton = (Button) layout.findViewById(R.id.end_data_send_button);
            makeBlack(cancelButton);
            cancelButton.setOnClickListener(cancel_button_click_listener);*/
     
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
        lesson.addWord(newWord.getId());
        dba.addLesson(lesson);
        window.dismiss();
    }
    
    //adds the new word to the database
    public void onSaveWordButtonClick(View view){
        if(newWord.length() > 0 && dba.addWord(newWord)){
            saved = true;
            TextView word = (TextView)findViewById(R.id.characters);
            word.setText("Successfully added!");
            initiatePopupWindow();
            return;
        }
        showToast("Word is empty");
        //return to home screen
    }
    
    //brings the user to the tag screen
    public void onAddTagButtonClick(View view){
        if(!saved){
            showToast("Save the word first");
            return;
        }
        Intent i = new Intent(this, TagActivity.class);
        i.putExtra("ID", newWord.getId());
        i.putExtra("TYPE", newWord.getItemType().toString());
        startActivity(i);
    }
    
    //for testing purposes
    public LessonWord getWord(){
        return newWord;
    }
    
    public void onClickClear(View view) {
        setCharList(dba.getAllCharIds());
        
        if (view != null) {
            // Hides the keyboard. Who knows...
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    
    private void setCharList(List<Long> ids) {
        items = new ArrayList<LessonItem>();
        for(long id : ids) {
            Log.i("Found", "id: "+id);
            LessonItem character;
            try {
                character = dba.getCharacterById(id);
            } catch(Exception e) {
                character = new LessonCharacter(id);
                Log.d("SEARCH", "Character " + id + " not found in db");
            }
            character.setTagList(dba.getCharacterTags(id));
            items.add(character);
        }
        LayoutInflater vi = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        charAdapter = new LessonItemListAdapter(this, items, vi);
        charList.setAdapter(charAdapter);
    }
    
    public void showToast(String msg){
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}