package edu.upenn.cis573.Trace2Win;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.upenn.cis573.Trace2Win.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.Database.LessonItem;
import edu.upenn.cis573.Trace2Win.Database.LessonItem.ItemType;

public class BrowseCharactersActivity extends ListActivity {
	private DbAdapter dba;
	private ArrayList<LessonItem> items;
	private LessonItemListAdapter adapter;
	
	private boolean filtered;
	private TextView filterStatus;
	
	private static final String[] menuItems = {"Edit Tags",
	                                           "Move Up",
	                                           "Move Down",
	                                           "Delete"};
	private static enum menuItemsInd { EditTags,
	                                   MoveUp,
	                                   MoveDown,
	                                   Delete }
	private static enum requestCodeENUM { EditTag }; 
	
	//initialized list of all characters
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_chars);
        dba = new DbAdapter(this);
        dba.open();
        
        List<Long> ids = dba.getAllCharIds();
        items = new ArrayList<LessonItem>(ids.size());
        for(long id : ids){
        	LessonItem character = dba.getCharacterById(id);
            items.add(character);
        }
        Collections.sort(items);
        LayoutInflater vi = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        adapter = new LessonItemListAdapter(this, items, vi);
        setListAdapter(adapter);
        registerForContextMenu(getListView());
        
        filtered = false;
        filterStatus = (TextView) findViewById(R.id.filterStatus);
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
		Intent intent = new Intent();
		Bundle bun = new Bundle();

		bun.putString("mode", "display");
		bun.putLong("charId", li.getId());

		intent.setClass(this, ViewCharacterActivity.class);
		intent.putExtras(bun);
		startActivity(intent);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, 
	                                ContextMenuInfo menuInfo) {
	    menu.setHeaderTitle("Options");
	    for (int i = 0; i < menuItems.length; i++) {
	      menu.add(Menu.NONE, i, i, menuItems[i]);
	    }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = item.getItemId();
	  LessonCharacter lc = (LessonCharacter)items.get(info.position);
	  Log.e("MenuIndex",Integer.toString(menuItemIndex));
	  Log.e("ListIndex",Integer.toString(info.position));
	  
	  // edit tags
	  if (menuItemIndex == menuItemsInd.EditTags.ordinal()) {
		  Intent i = new Intent(this, TagActivity.class);
		  i.putExtra("ID", lc.getId());
		  i.putExtra("TYPE", "CHARACTER");
		  startActivityForResult(i, requestCodeENUM.EditTag.ordinal());
		  return true;
	  }
	  
	  // delete
	  else if (menuItemIndex == menuItemsInd.Delete.ordinal()) {
		  long id = lc.getId();
		  long result = dba.deleteCharacter(id);
		  Log.e("Result", Long.toString(result));
		  if(result < 0){
			  showToast("Character is used by a word: cannot delete");
			  return false;
		  }
		  else{
			  showToast("Successfully deleted");
			  startActivity(getIntent()); 
			  finish();
			  return true;
		  }
	  }
	  
	  // move
	  else if (menuItemIndex == menuItemsInd.MoveUp.ordinal() ||
	           menuItemIndex == menuItemsInd.MoveDown.ordinal()) {
	      // going to swap sort values with the item above or below
	      
	      // need to get other item
	      int otherPos;
	      if (menuItemIndex == menuItemsInd.MoveUp.ordinal()) {
	          otherPos = info.position - 1;
	      } else {
	          otherPos = info.position + 1;
	      }
	      
	      // check that item exists
	      if (otherPos < 0) {
	          showToast("Cannot move this character up");
	          return false;
	      } else if (otherPos >= items.size()) {
              showToast("Cannot move this character down");
              return false;
	      }
	      
	      LessonCharacter other = (LessonCharacter) items.get(otherPos);
	      boolean result = dba.swapCharacters(lc.getId(), lc.getSort(), 
	                                          other.getId(), other.getSort());
	      Log.e("Move result", Boolean.toString(result));
	      if (result) {
	          // success, so update the local copy
	          double temp = lc.getSort();
	          lc.setSort(other.getSort());
	          other.setSort(temp);
	          Collections.sort(items);
	          adapter._items = items;
              adapter.notifyDataSetChanged();
              return true;
	      }
	      else {
	          showToast("Move failed");
	          return false;
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == requestCodeENUM.EditTag.ordinal() 
				&& resultCode == RESULT_OK) {
			// re-launch the current activity to reflect the changes of tags
			// we could use recreate() instead, but note that it is supported since API 11.
			// TODO: This implementation is quite wasting times. We could just update ArrayList and ListView 
			startActivity(getIntent());
			finish();
		}
	}
	
	// FILTER METHODS
	
    // depending on the state, shows the filter pop up or clears the filter
    public void onClickFilter(View view) {
        if (filtered) {
            clearFilter();
        } else {
            showFilterPopup();
        }
    }
    
    // displays the filter pop up
    public void showFilterPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Apply Filter");
        
        final EditText filterText = new EditText(this);
        builder.setView(filterText);
        
        builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String search = filterText.getText().toString();
                if (search.equals("")) {
                    hideKeyboard(filterText);
                    return;
                }

                // Filter action: query for chars and set char list
                Cursor c = dba.browseByTag(ItemType.CHARACTER, search);
                List<Long> ids = new LinkedList<Long>();
                do {
                    if (c.getCount() == 0) {
                        Log.d(ACTIVITY_SERVICE, "zero rows");
                        break;
                    }
                    ids.add(c.getLong(c.getColumnIndexOrThrow(
                            DbAdapter.CHARTAG_ROWID)));
                } while (c.moveToNext());
                c.close();
                setCharList(ids);
                
                // Set state to filtered
                ((Button)findViewById(R.id.filterButton)).setText(R.string.clear_filter);
                filtered = true;
                filterStatus.setText("Filter: " + search);
                hideKeyboard(filterText);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                hideKeyboard(filterText);
            }
        });
        
        AlertDialog dialog = builder.create();
        
        // show the keyboard
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();

    }
    
    // clears the filter
    public void clearFilter() {
        setCharList(dba.getAllCharIds());
        ((Button)findViewById(R.id.filterButton)).setText(R.string.filter);
        filtered = false;
        filterStatus.setText(R.string.filter_none);
    }
    
    // sets the list of items
    private void setCharList(List<Long> ids) {
        items = new ArrayList<LessonItem>();
        for(long id : ids) {
            Log.i("Found", "id: "+id);
            LessonItem character;
            try {
                character = dba.getCharacterById(id);
            } catch(Exception e) {
                character = new LessonCharacter(id);
                Log.d("SEARCH", "Character " + id + " not found in db");
            }
            items.add(character);
        }
        LayoutInflater vi = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        adapter = new LessonItemListAdapter(this, items, vi);
        setListAdapter(adapter);
    }
    
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    
    //Methods for exporting and importing temporarily put here, place wherever shopping cart functionality is later
    public void exportCharSet(){
    	
    }
    
    public void importCharSet(){
    
    }
    
}