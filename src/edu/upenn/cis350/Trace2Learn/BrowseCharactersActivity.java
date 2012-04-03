package edu.upenn.cis350.Trace2Learn;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis350.Trace2Learn.Database.DbAdapter;
import edu.upenn.cis350.Trace2Learn.Database.LessonItem;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ListView;

public class BrowseCharactersActivity extends Activity {
	private DbAdapter dba;
	private ListView list;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_chars);
        dba = new DbAdapter(this);
        dba.open();
        
        list = (ListView)findViewById(R.id.allcharslist);
        
        ArrayList<LessonItem> items = new ArrayList<LessonItem>();
        List<Long> ids = dba.getAllCharIds();
        for(long id : ids){
        	LessonItem character = dba.getCharacterById(id);
        	items.add(character);
        }
        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list.setAdapter(new LessonItemListAdapter(this, items, vi));
	}
}