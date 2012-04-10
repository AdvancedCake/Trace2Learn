package edu.upenn.cis350.Trace2Learn;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import edu.upenn.cis350.Trace2Learn.Database.DbAdapter;
import edu.upenn.cis350.Trace2Learn.Database.Lesson;
import edu.upenn.cis350.Trace2Learn.Database.LessonItem;
import edu.upenn.cis350.Trace2Learn.Database.LessonWord;

public class CreateLessonActivity extends Activity {
	
	private DbAdapter dba; 
	private ListView list; //list of words to display
	private Gallery gallery; //gallery for every word
	private ImageAdapter imgAdapter;
	private Lesson newLesson; //should we create a new object type?
	private ArrayList<Bitmap> currentWords;
	private int numWords;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        numWords = 0;
        currentWords = new ArrayList<Bitmap>();
        setContentView(R.layout.create_lesson);
        dba = new DbAdapter(this);
        dba.open();
        
        imgAdapter = new ImageAdapter(this,currentWords);
        gallery = (Gallery)findViewById(R.id.gallery);
        //gallery.setUnselectedAlpha(1.0f);
        gallery.setSpacing(0);
        
        gallery.setAdapter(imgAdapter);
    	
        list = (ListView)findViewById(R.id.wordlist);
     
        newLesson = new Lesson();
        
        //Set up the ListView
        ArrayList<LessonItem> items = new ArrayList<LessonItem>(); //items to show in ListView to choose from 
        List<Long> ids = dba.getAllWordIds();
        for(long id : ids){
        	LessonItem word = dba.getWordById(id);
        	word.setTagList(dba.getTags(id));
        	items.add(word);
        }
        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list.setAdapter(new LessonItemListAdapter(this, items, vi));

        list.setOnItemClickListener(new OnItemClickListener() {    
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {     
            	numWords++;
                Log.e("Position",Long.toString(position));
                Log.e("Type",list.getItemAtPosition(position).getClass().getName());
                long wordId = ((LessonWord)list.getItemAtPosition(position)).getId();
                Log.e("Id",Long.toString(wordId));
                newLesson.addWord(wordId);
                LessonItem item = (LessonWord)list.getItemAtPosition(position);
                Bitmap bitmap = BitmapFactory.buildBitmap(item, 64, 64);
                currentWords.add(bitmap);
                imgAdapter.update(currentWords);
                imgAdapter.notifyDataSetChanged();
                //gallery.setAdapter(imgAdapter);
                gallery.setSelection(numWords/2);
            }
        });
    }
	
	public void onSaveLessonButtonClick(View view){
		if(dba.addLesson(newLesson)){
			TextView word = (TextView)findViewById(R.id.words);
			word.setText("Successfully added!");
		}
	}
	
	public void onAddTagButtonClick(View view){
		/*Intent i = new Intent(this, TagActivity.class);
		i.putExtra("ID", newLesson.getId());
		i.putExtra("TYPE", newLesson.getItemType().toString());
		startActivity(i);*/
	}
}