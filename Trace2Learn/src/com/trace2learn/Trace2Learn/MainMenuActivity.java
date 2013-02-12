package com.trace2learn.Trace2Learn;

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
import com.trace2learn.TraceLibrary.CreateWordActivity;
import com.trace2learn.TraceLibrary.FilePickerActivity;
import com.trace2learn.TraceLibrary.ShoppingCartActivity;
import com.trace2learn.TraceLibrary.ViewCharacterActivity;

public class MainMenuActivity extends ListActivity {
	
	static final String[] APPS = new String[]
		{ 
			"Create Character", 
			"Create Word",
			"Browse All Characters",
			"Browse All Words",
			"Browse All Lessons",
            "Export To File",
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
					if(clicked.equals(APPS[0])) // "Create Character"
					{
						Intent i = new Intent().setClass(c, ViewCharacterActivity.class);
						startActivity(i);
					}
					else if(clicked.equals(APPS[1])) // "Create Word"
					{
	
						Intent i = new Intent(c, CreateWordActivity.class);
						startActivity(i);
					
					}
					else if(clicked.equals(APPS[2])) // "Browse All Characters"
					{
						Intent i = new Intent(c, BrowseCharactersActivity.class);
						startActivity(i);
					}
					else if(clicked.equals(APPS[3])) // "Browse All Words"
					{
						Intent i = new Intent(c, BrowseWordsActivity.class);
						startActivity(i);
					}
                    else if (clicked.equals(APPS[4]))// "Browse All Lessons"
                    {
                        Intent i = new Intent(c, BrowseLessonsActivity.class);
                        startActivity(i);
                    }
                    else if (clicked.equals(APPS[5]))// "Export To File"
                    {
                        Intent intent = new Intent(c, ShoppingCartActivity.class);
                        Bundle bun = new Bundle();
                        bun.putString("type", "lesson");
                        intent.putExtras(bun);
                        startActivity(intent);
                    }
                    else if (clicked.equals(APPS[6])) // "Import From File"
                    {
                        Intent intent = new Intent(c, FilePickerActivity.class);
                        startActivity(intent);
                    }
				}
			}
		);
	}
}
