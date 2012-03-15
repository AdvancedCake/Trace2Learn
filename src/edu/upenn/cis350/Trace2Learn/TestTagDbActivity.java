package edu.upenn.cis350.Trace2Learn;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TestTagDbActivity extends Activity {

	private DbAdapter mDbHelper;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.test_tag_db);
        setContentView(R.layout.tag); //Isabel
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
    }
	
	public void onSubmitButtonClick(View view){
		EditText charEt = (EditText)findViewById(R.id.character);
		String charText = charEt.getText().toString();
		long charId = Long.valueOf(charText);
		
		EditText tagEt = (EditText)findViewById(R.id.tag);
		String tagText = tagEt.getText().toString();
		
		mDbHelper.createTags(charId,tagText);
	}
	
	public void onCharSearchButtonClick(View view){
		EditText charEt = (EditText)findViewById(R.id.search_char);
		String charText = charEt.getText().toString();
		long charId = Long.valueOf(charText);
		Log.d(ACTIVITY_SERVICE, "here");
		
		Cursor c = mDbHelper.getTags(charId);
		StringBuilder builder = new StringBuilder();
		do{
			if(c.getCount()==0){
				Log.d(ACTIVITY_SERVICE, "zeroRows");
				builder.append("No results");
				break;
			}
			builder.append(c.getString(c.getColumnIndexOrThrow(DbAdapter.CHARTAG_TAG))+"\n");			
		}
		while(c.moveToNext());
		String output = builder.toString();
		
		Log.d(ACTIVITY_SERVICE, output);
		
		TextView results = (TextView)findViewById(R.id.results);
		results.setText(output);
	}
	
	public void onTagSearchButtonClick(View view){
		EditText tagEt = (EditText)findViewById(R.id.search_tag);
		String tagText = tagEt.getText().toString();
		
		Cursor c = mDbHelper.getChars(tagText);
		StringBuilder builder = new StringBuilder();
		do{
			if(c.getCount()==0){
				Log.d(ACTIVITY_SERVICE, "zeroRows");
				builder.append("No results");
				break;
			}
			builder.append(c.getString(c.getColumnIndexOrThrow(DbAdapter.CHARTAG_ROWID))+"\n");			
		}
		while(c.moveToNext());
		String output = builder.toString();
		
		Log.d(ACTIVITY_SERVICE, output);
		
		TextView results = (TextView)findViewById(R.id.results);
		results.setText(output);
	}
}
