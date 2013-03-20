package com.trace2learn.TraceMe;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class MainMenuActivity extends ListActivity {
	
	static final String[] APPS = new String[]
		{ 
			"Browse All Characters",
			"Browse All Words",
			"Browse All Collections",
			"Import From File"
		};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
}
