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
        //gallery.setUnselectedAlpha(1.0f);
        gallery.setSpacing(0);
        
        gallery.setAdapter(imgAdapter);
        
        list = (ListView)findViewById(R.id.charslist);
        /*Cursor c = dba.getAllCharIdsCursor();
        Log.e("getCharIDs",Integer.toString(c.getCount()));
        ListAdapter adapter = new SimpleCursorAdapter(
        		this,
        		android.R.layout.simple_list_item_1,
        		c,
        		new String[]{DbAdapter.CHAR_ROWID},
        		new int[]{android.R.id.text1});*/
        newWord = new LessonWord();
        ArrayList<LessonItem> items = new ArrayList<LessonItem>();
        List<Long> ids = dba.getAllCharIds();
        for(long id : ids){
        	LessonItem character = dba.getCharacterById(id);
        	items.add(character);
        }
        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list.setAdapter(new LessonItemListAdapter(this, items, vi));
        //dba.close();
        list.setOnItemClickListener(new OnItemClickListener() {
    
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {     
            	numChars++;
                Log.e("Position",Long.toString(position));
                Log.e("Type",list.getItemAtPosition(position).getClass().getName());
                long charId = ((LessonCharacter)list.getItemAtPosition(position)).getId();
                Log.e("Id",Long.toString(charId));
                newWord.addCharacter(charId);
                LessonItem item = (LessonCharacter)list.getItemAtPosition(position);
                //LessonItem item = dba.getCharacterById(((LessonCharacter)list.getItemAtPosition(position)).getId());
                Bitmap bitmap = BitmapFactory.buildBitmap(item, 64, 64);
                currentChars.add(bitmap);
                imgAdapter.update(currentChars);
                imgAdapter.notifyDataSetChanged();
                //gallery.setAdapter(imgAdapter);
                gallery.setSelection(numChars/2);
            }
        });
        
    }
	
	/*@Override
	protected void onListItemClick(ListView l, View v, int position, long id){
		super.onListItemClick(l, v, position, id);
		
		Cursor c = (Cursor) getListView().getItemAtPosition(position);
		long index = c.getLong(0);
		Log.e("Clicked",c.getString(0));
		newWord.addCharacter((Long)index);
		
		TextView word = (TextView)findViewById(R.id.characters);
		CharSequence cs = word.getText();
		String newString = cs.toString() + Long.toString(index);
		word.setText(newString);
	}*/
	
	public void onSaveWordButtonClick(View view){
		if(dba.addWord(newWord)){
			TextView word = (TextView)findViewById(R.id.characters);
			word.setText("Successfully added!");
		}
		//return to home screen
	}
	
	public void onAddTagButtonClick(View view){
		
		Intent i = new Intent(this, TagActivity.class);
		i.putExtra("ID", newWord.getId());
		i.putExtra("TYPE", newWord.getItemType().toString());
		startActivity(i);
	}
}