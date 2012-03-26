package edu.upenn.cis350.Trace2Learn;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.upenn.cis350.Trace2Learn.Database.DbAdapter;
import edu.upenn.cis350.Trace2Learn.Database.LessonWord;

public class CreateWordActivity extends ListActivity {
	
	private DbAdapter dba;
	private LessonWord newWord;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_word);
        dba = new DbAdapter(this);
        dba.open();
        Cursor c = dba.getAllCharIdsCursor();
        Log.e("getCharIDs",Integer.toString(c.getCount()));
        ListAdapter adapter = new SimpleCursorAdapter(
        		this,
        		android.R.layout.simple_list_item_1,
        		c,
        		new String[]{DbAdapter.CHAR_ROWID},
        		new int[]{android.R.id.text1});
        setListAdapter(adapter);
        //dba.close();
        newWord = new LessonWord();
    }
	
	@Override
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
	}
	
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