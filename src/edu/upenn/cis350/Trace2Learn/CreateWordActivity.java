package edu.upenn.cis350.Trace2Learn;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.upenn.cis350.Trace2Learn.Database.DbAdapter;
import edu.upenn.cis350.Trace2Learn.Database.LessonCharacter;
import edu.upenn.cis350.Trace2Learn.Database.LessonItem;
import edu.upenn.cis350.Trace2Learn.Database.LessonWord;

public class CreateWordActivity extends Activity {
	
	private DbAdapter dba;
	private LessonWord newWord;
	private ListView list;
	private ArrayList<Bitmap> currentChars;
	private Gallery gallery;
	private ImageAdapter imgAdapter;
	private int numChars;
	
	//initializes the list if all characters in the database
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        numChars = 0;
        currentChars = new ArrayList<Bitmap>();
        setContentView(R.layout.create_word);
        dba = new DbAdapter(this);
        dba.open();
        
        imgAdapter = new ImageAdapter(this,currentChars);
        gallery = (Gallery)findViewById(R.id.gallery);
        gallery.setSpacing(0);
        
        gallery.setAdapter(imgAdapter);
        
        list = (ListView)findViewById(R.id.charslist);

        newWord = new LessonWord();
        ArrayList<LessonItem> items = new ArrayList<LessonItem>();
        List<Long> ids = dba.getAllCharIds();
        for(long id : ids){
        	LessonItem character = dba.getCharacterById(id);
        	character.setTagList(dba.getTags(id));
        	items.add(character);
        }
        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list.setAdapter(new LessonItemListAdapter(this, items, vi));
        //dba.close();
        
        //when a char is clicked, it is added to the new word and added to the gallery
        list.setOnItemClickListener(new OnItemClickListener() {
    
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {     
            	numChars++;
                long charId = ((LessonCharacter)list.getItemAtPosition(position)).getId();
                newWord.addCharacter(charId);
                LessonItem item = (LessonCharacter)list.getItemAtPosition(position);
                Bitmap bitmap = BitmapFactory.buildBitmap(item, 64, 64);
                currentChars.add(bitmap);
                imgAdapter.update(currentChars);
                imgAdapter.notifyDataSetChanged();
                gallery.setSelection(numChars/2);
            }
        });
        
    }
	
	//adds the new word to the database
	public void onSaveWordButtonClick(View view){
		if(dba.addWord(newWord)){
			TextView word = (TextView)findViewById(R.id.characters);
			word.setText("Successfully added!");
		}
		//return to home screen
	}
	
	//brings the user to the tag screen
	public void onAddTagButtonClick(View view){
		
		Intent i = new Intent(this, TagActivity.class);
		i.putExtra("ID", newWord.getId());
		i.putExtra("TYPE", newWord.getItemType().toString());
		startActivity(i);
	}
	
	//for testing purposes
	public LessonWord getWord(){
		return newWord;
	}
}