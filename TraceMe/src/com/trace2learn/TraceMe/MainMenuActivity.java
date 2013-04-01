package com.trace2learn.TraceMe;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.BrowseCharactersActivity;
import com.trace2learn.TraceLibrary.BrowseLessonsActivity;
import com.trace2learn.TraceLibrary.BrowseWordsActivity;
import com.trace2learn.TraceLibrary.FilePickerActivity;
import com.trace2learn.TraceLibrary.Database.DbAdapter;

public class MainMenuActivity extends ListActivity {
	
    private DbAdapter dba;
    
	static final String[] APPS = new String[] { 
		"Browse All Characters",
		"Browse All Words",
		"Browse All Collections",
		"Import From File"};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dba = new DbAdapter(this);
		dba.open();
		if (dba.firstStart()) {
	        initializeDatabase();
		}
		dba.recordAppStart();
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.main_menu,APPS));

		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		final Context c = this;
		
		listView.setOnItemClickListener(
			new OnItemClickListener() 
			{
				public void onItemClick(AdapterView<?> parent, View view, 
				        int position, long id) 
				{
					CharSequence clicked = ((TextView) view).getText();
				
					if(clicked.equals(APPS[0])) // "Browse All Characters"
					{
						Intent i = new Intent(c, BrowseCharactersActivity.class);
						startActivity(i);
					}
					else if(clicked.equals(APPS[1])) // "Browse All Words"
					{
						Intent i = new Intent(c, BrowseWordsActivity.class);
						startActivity(i);
					}
                    else if (clicked.equals(APPS[2]))// "Browse All Collections"
                    {
                        Intent i = new Intent(c, BrowseLessonsActivity.class);
                        startActivity(i);
                    }
                    else if (clicked.equals(APPS[3])) // "Import From File"
                    {
                        Intent intent = new Intent(c, FilePickerActivity.class);
                        startActivity(intent);
                    }
				}
			}
		);
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    dba.close();
	}
	
	private void initializeDatabase() {
        Log.i("Initialize DB", "Attempting to import database");

        String dbPath = getDatabasePath(
	            DbAdapter.DATABASE_NAME).getAbsolutePath();

	    try {
	        // Open the .db file in your assets directory
	        InputStream is = getBaseContext().getAssets().open("initial.db");

	        // Copy the database into the destination
	        OutputStream os = new FileOutputStream(dbPath);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0){
	            os.write(buffer, 0, length);
	        }
	        os.flush();

	        os.close();
	        is.close();
	        Log.i("Initialize DB", "Database successfully imported");
	    } catch (Exception e) {
	        Log.e("Initialize DB", e.getClass().getName() + ": " +
	                e.getMessage());
	    }
	}
}
