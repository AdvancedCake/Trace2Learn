package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.trace2learn.TraceLibrary.Database.LessonCharacter;
import com.trace2learn.TraceLibrary.Database.LessonItem;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class BrowseCharactersActivity extends TraceListActivity {
	private List<LessonItem> source;  // list of all characters
	private List<LessonItem> display; // list of items being displayed
	private LessonItemListAdapter adapter;
	
	private ListView list;
	private Button   filterButton;
	private TextView filterStatus;
    private boolean  filtered;
	
	private LayoutInflater vi;
	
	private static final String[] menuItems = {"Edit Tags",
	                                           "Move Up",
	                                           "Move Down",
	                                           "Delete"};
	private static enum menuItemsInd { EditTags,
	                                   MoveUp,
	                                   MoveDown,
	                                   Delete }
	private static enum requestCodeENUM { EditTag,
	                                      ViewCharacter };
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_chars);
        vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        getViews();
        
        getChars();
        displayAllChars();
        
        registerForContextMenu(list);
        
        filtered = false;
	}

	private void getViews() {
	    list = getListView();
	    filterButton = (Button)   findViewById(R.id.filterButton);
        filterStatus = (TextView) findViewById(R.id.filterStatus);
	}
	
    /**
     * Populate the source list with characters
     */
    private void getChars() {
        source = Toolbox.characters;
    }
    
    /**
     * Set display list to source list, thus displaying all characters
     */
    private void displayAllChars() {
        display = source;
        displayChars();      
    }
    
    /**
     * Display the current display list
     */
    private void displayChars() {
        Collections.sort(display);
        adapter = new LessonItemListAdapter(this, display, vi);
        setListAdapter(adapter);   
    }
	
	@Override
	protected void onDestroy() {
        super.onDestroy();
	};
	
	@Override  
	protected void onListItemClick(ListView l, View v, int position, long id) {  
	  super.onListItemClick(l, v, position, id);  
	  clickOnItem(display.get(position));
	}  

	//when character is clicked, it starts the display mode for that char
	public void clickOnItem(LessonItem li){
		Intent intent = new Intent();
		Bundle bun = new Bundle();

		bun.putString("mode", "trace");
		bun.putString("charId", li.getStringId());

		intent.setClass(this, ViewCharacterActivity.class);
		intent.putExtras(bun);
		startActivityForResult(intent, requestCodeENUM.ViewCharacter.ordinal());
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
	  LessonCharacter lc = (LessonCharacter)display.get(info.position);
	  Log.e("MenuIndex",Integer.toString(menuItemIndex));
	  Log.e("ListIndex",Integer.toString(info.position));
	  
	  Context context = getApplicationContext();
	  
	  // edit tags
	  if (menuItemIndex == menuItemsInd.EditTags.ordinal()) {
		  Intent i = new Intent(this, TagActivity.class);
		  i.putExtra("ID", lc.getStringId());
		  i.putExtra("TYPE", "CHARACTER");
		  startActivityForResult(i, requestCodeENUM.EditTag.ordinal());
		  return true;
	  }
	  
	  // delete
	  else if (menuItemIndex == menuItemsInd.Delete.ordinal()) {
		  String id = lc.getStringId();
		  boolean result = Toolbox.dba.deleteCharacter(id);
		  Log.d("Result", Boolean.toString(result));
		  if (result == false) {
			  Toolbox.showToast(context, "This character belongs to a phrase!");
			  return false;
		  } else {
			  Toolbox.showToast(context, "Successfully deleted");
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
	          Toolbox.showToast(context, "Cannot move this character up");
	          return false;
	      } else if (otherPos >= display.size()) {
              Toolbox.showToast(context, "Cannot move this character down");
              return false;
	      }
	      
	      LessonCharacter other = (LessonCharacter) display.get(otherPos);
	      boolean result = Toolbox.dba.swapCharacters(lc.getStringId(), lc.getSort(), 
	                                          other.getStringId(), other.getSort());
	      Log.e("Move result", Boolean.toString(result));
	      if (result) {
	          // success, so update the local copy
	          long temp = lc.getSort();
	          lc.setSort(other.getSort());
	          other.setSort(temp);
	          Collections.sort(display);
	          adapter.items = display;
              adapter.notifyDataSetChanged();
              return true;
	      }
	      else {
	          Toolbox.showToast(context, "Move failed");
	          return false;
	      }
	  }
	  
	  return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == requestCodeENUM.EditTag.ordinal() &&
	            resultCode == RESULT_OK) {
	        startActivity(getIntent());
            finish();
        } else if (requestCode == requestCodeENUM.ViewCharacter.ordinal() &&
                resultCode == RESULT_OK) {
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
    private void showFilterPopup() {
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

                // Filter action: keep matching items from display list
                // Note that it should be partial match for search terms 3
                // characters or more.
                ArrayList<LessonItem> newList = new ArrayList<LessonItem>();
                topLoop: for (LessonItem item : display) {
                    List<String> tags = item.getTags();
                    for (String tag : tags) {
                        if (Toolbox.containsMatch(1, tag, search)) {
                            newList.add(item);
                            continue topLoop;
                        }
                    }
                    Collection<String> values = item.getKeyValues().values();
                    for (String value : values) {
                        if (Toolbox.containsMatch(1, value, search)) {
                            newList.add(item);
                            continue topLoop;
                        }
                    }
                }
                display = newList;
                displayChars();
                
                // Set state to filtered
                filterButton.setText(R.string.clear_filter);
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
    private void clearFilter() {
        displayAllChars();
        filterButton.setText(R.string.filter);
        filtered = false;
        filterStatus.setText(R.string.filter_none);
    }
    
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    
}