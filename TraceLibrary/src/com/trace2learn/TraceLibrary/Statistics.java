package com.trace2learn.TraceLibrary;


import java.util.List;

import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.DbAdapter;
import com.trace2learn.TraceLibrary.Database.LessonCharacter;

public class Statistics extends TraceBaseActivity {

    
    private DbAdapter dba;
    
    private TextView	countCollections;
    private TextView	countPhrases;
    private TextView	countCharacters;
    private TextView	countStrokes;
    private ImageView	exitButton;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		
		setContentView(R.layout.stats);
		
        // Initialize Views and Handlers
        getViews();
        getHandlers();
        
        // Initialize database adapter
        dba = new DbAdapter(this);
        dba.open();
        
        String txt = "";
        int numStrokes = 0;
 		
        List<String> lids = dba.getAllLessonIds();
        txt += lids.size();
        countCollections.setText(txt);
                
        List<String> wids = dba.getAllWordIds();
        txt = "";
        txt += wids.size();
        countPhrases.setText(txt);

        List<String> cids = dba.getAllCharIds();
        txt = "";
        txt += cids.size();
        countCharacters.setText(txt);

        for(String id : cids){
        	LessonCharacter ch = dba.getCharacterById(id);
        	numStrokes += ch.getNumStrokes();
        }
        txt = "";
        txt += numStrokes;
        countStrokes.setText(txt);
        
	}

    private void getViews() {
    	countCollections	= (TextView) findViewById(R.id.countCollections);
    	countPhrases		= (TextView) findViewById(R.id.countPhrases);
    	countCharacters		= (TextView) findViewById(R.id.countCharacters);
    	countStrokes		= (TextView) findViewById(R.id.countStrokes);
        exitButton			= (ImageView) findViewById(R.id.exit_button);
        
    }
    
    private void getHandlers() {
        exitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
    }
    	
}
