package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.trace2learn.TraceLibrary.Database.DbAdapter;
import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonCategory;
import com.trace2learn.TraceLibrary.Database.LessonItem;

public class BrowseLessonsActivity extends TraceListActivity {

	private DbAdapter dba; 
	private ArrayList<Lesson> items;
	private LessonListAdapter adapter;
	
	private boolean isAdmin;
	
	private enum ContextMenuItem {
	    DELETE              (1, "Delete"),
	    ASSIGN_CATEGORIES   (1, "Assign Categories"),
	    TOGGLE_USER_DEFINED (2, "Toggle User-Defined");
	    
	    public final String text;
	    public final int    privilege; // 0: anyone, 1: owner, 2: admin
	    
	    ContextMenuItem(int privilege, String text) {
	        this.privilege = privilege;
	        this.text      = text;
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
        
        items = new ArrayList<Lesson>(); 
        List<String> ids = dba.getAllLessonIds();
        for(String id : ids){
        	Lesson le = dba.getLessonById(id);
        	le.setTagList(dba.getLessonTags(id));
        	items.add(le);
        }
        LayoutInflater vi = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        adapter = new LessonListAdapter(this, items, vi, infoButtonHandler);
        setListAdapter(adapter);
        registerForContextMenu(getListView());
        
        // Set admin privilege
        SharedPreferences prefs = getSharedPreferences(Toolbox.PREFS_FILE,
                MODE_PRIVATE);
        isAdmin = prefs.getBoolean(Toolbox.PREFS_IS_ADMIN, false);
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
	private void clickOnItem(LessonItem li){
		Lesson le = ((Lesson)li);
        Intent i = new Intent(this, BrowseWordsActivity.class);
		i.putExtra("ID", le.getStringId());
		startActivity(i);
	}

	private void getItemInfo(String lessonId) {
        Intent i = new Intent(this, LessonNarrativeActivity.class);
        i.putExtra("ID", lessonId);
        startActivity(i);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	        ContextMenuInfo menuInfo) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	    Lesson le = items.get(info.position);
	    
	    menu.setHeaderTitle("Options");
	    for (ContextMenuItem item : ContextMenuItem.values()) {
	        if (!isAdmin && !le.isUserDefined() && item.privilege >= 1) {
	            // user does not own the lesson
	            continue;
	        } else if (!isAdmin && item.privilege >= 2) {
	            // admin only option
	            continue;
	        }
	        int ord = item.ordinal();
	        menu.add(Menu.NONE, ord, ord, item.text);
	    }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = item.getItemId();
	  Lesson le = items.get(info.position);
      Context context = getApplicationContext();
	  Log.e("MenuIndex",Integer.toString(menuItemIndex));
	  Log.e("ListIndex",Integer.toString(info.position));

	  // Delete lesson
	  if (menuItemIndex == ContextMenuItem.DELETE.ordinal()) {
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
	      Intent i = new Intent(context, ChooseLessonCategoryActivity.class);
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
	  
	  // Toggle User-Defined
	  else if (menuItemIndex == ContextMenuItem.TOGGLE_USER_DEFINED.ordinal()) {
	      le.setUserDefined(!le.isUserDefined());
	      boolean result = dba.saveLessonUserDefined(le.getStringId(),
	              le.isUserDefined());
	      if (result) {
	          adapter.notifyDataSetChanged();
	          return true;
	      } else {
	          Toolbox.showToast(context, "Could not edit the collection");
	          return false;
	      }
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

	private Handler infoButtonHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        String lessonId = (String) msg.obj;
	        getItemInfo(lessonId);
	    }
	};

}
