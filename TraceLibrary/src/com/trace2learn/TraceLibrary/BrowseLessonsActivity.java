package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.trace2learn.TraceLibrary.Database.DbAdapter;
import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonCategory;
import com.trace2learn.TraceLibrary.Database.LessonItem;

public class BrowseLessonsActivity extends ListActivity {
	private Lesson le;
	private DbAdapter dba; 
	private ArrayList<Lesson> items;
	ArrayAdapter<String> arrAdapter;
	
	private enum ContextMenuItem {
	    DELETE            ("Delete"),
	    ASSIGN_CATEGORIES ("Assign Categories");
	    
	    public final String text;
	    
	    ContextMenuItem(String text) {
	        this.text = text;
	    }
	}
	
	private enum RequestCode {
	    ASSIGN_CATEGORIES;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_lessons);
        dba = new DbAdapter(this);
        dba.open(); //opening the connection to database        
        
        items = new ArrayList<Lesson>(); //items to show in ListView to choose from 
        List<String> ids = dba.getAllLessonIds();
        for(String id : ids){
        	Lesson le = dba.getLessonById(id);
        	le.setTagList(dba.getLessonTags(id));
        	items.add(le);
        }
        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LessonListAdapter la = new LessonListAdapter(this,items,vi);
        setListAdapter(la);
        registerForContextMenu(getListView());
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dba.close();
    };

	@Override  
	protected void onListItemClick(ListView l, View v, int position, long id) {  
	  super.onListItemClick(l, v, position, id);  

	  clickOnItem(items.get(position));
	} 

	//when character is clicked, it starts the display mode for that char
	public void clickOnItem(LessonItem li){
		Lesson le = ((Lesson)li);
        Intent i = new Intent(this, LessonNarrativeActivity.class);
//        Intent i = new Intent(this, BrowseWordsActivity.class);
		i.putExtra("ID", le.getStringId());
		startActivity(i);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
	    menu.setHeaderTitle("Options");
	    for (ContextMenuItem item : ContextMenuItem.values()) {
	        int ord = item.ordinal();
	        menu.add(Menu.NONE, ord, ord, item.text);
	    }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = item.getItemId();
	  le = (Lesson)items.get(info.position);
	  Log.e("MenuIndex",Integer.toString(menuItemIndex));
	  Log.e("ListIndex",Integer.toString(info.position));

	  // Delete lesson
	  if (menuItemIndex == ContextMenuItem.DELETE.ordinal()) {
	      Context context = getApplicationContext();
		  String id = le.getStringId();
		  String result = dba.deleteLesson(id);
		  Log.e("Result", result);
		  if (result == null) {
			  Toolbox.showToast(context, "Could not delete the collection");
			  return false;
		  }
		  else {
			  Toolbox.showToast(context, "Successfully deleted");
			  startActivity(getIntent()); 
			  finish();
			  return true;
		  }
	  }
	  
	  // Assign Categories
	  else if (menuItemIndex == ContextMenuItem.ASSIGN_CATEGORIES.ordinal()) {
	      Intent i = new Intent(getApplicationContext(),
	              ChooseLessonCategoryActivity.class);
	      i.putExtra("ID",   le.getStringId());
	      i.putExtra("name", le.getLessonName());
	      
	      boolean[] original = new boolean[] {false, false, false, false};
	      SortedSet<LessonCategory> categories = le.getCategories();
	      if (categories != null) {
	          for (LessonCategory category : categories) {
	              original[category.ordinal()] = true;
	          }
	      }
	      i.putExtra("categories", original);
	      
          startActivityForResult(i, RequestCode.ASSIGN_CATEGORIES.ordinal());
          return true;
	  }
	  
	  return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == RequestCode.ASSIGN_CATEGORIES.ordinal() &&
	            resultCode == RESULT_OK) {
	        startActivity(getIntent());
	        finish();
	    }
	}

}