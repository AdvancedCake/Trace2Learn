package edu.upenn.cis573.Trace2Win.library;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.Toast;
import edu.upenn.cis573.Trace2Win.library.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.library.Database.Lesson;
import edu.upenn.cis573.Trace2Win.library.Database.LessonItem;

public class BrowseLessonsActivity extends ListActivity {
	private Lesson le;
	private DbAdapter dba; 
	private ArrayList<Lesson> items;
	ArrayAdapter<String> arrAdapter;

	final Context c = this;

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
		Intent i = new Intent(this, BrowseWordsActivity.class);
		i.putExtra("ID", le.getStringId());
		startActivity(i);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
	    menu.setHeaderTitle("Options");
	    String[] menuItems = {"Delete"};
	    for (int i = 0; i<menuItems.length; i++) {
	        menu.add(Menu.NONE, i, i, menuItems[i]);
	    }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = item.getItemId();
	  le = (Lesson)items.get(info.position);
	  Log.e("MenuIndex",Integer.toString(menuItemIndex));
	  Log.e("ListIndex",Integer.toString(info.position));

	  //delete lesson
	  if(menuItemIndex==0){
		  String id = le.getStringId();
		  String result = dba.deleteLesson(id);
		  Log.e("Result", result);
		  if(result == null){
			  showToast("Could not delete the lesson");
			  return false;
		  }
		  else{
			  showToast("Successfully deleted");
			  startActivity(getIntent()); 
			  finish();
			  return true;
		  }
	  }
	  return false;
	}

	public void showToast(String msg){
		Context context = getApplicationContext();
		CharSequence text = msg;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

}