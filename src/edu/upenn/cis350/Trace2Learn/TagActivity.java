package edu.upenn.cis350.Trace2Learn;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import edu.upenn.cis350.Trace2Learn.Database.DbAdapter;
import edu.upenn.cis350.Trace2Learn.Database.LessonItem.ItemType;

public class TagActivity extends Activity {

	private final String PRIVATE_PREFIX = "Private: ";
	private static final String[] menuItems = { "Delete" };
	private static enum menuItemsInd { Delete }
	private static final String TagDeleteSuccessMsg = "Deleted the tag successfully.";
	private static final String TagDeleteErrorMsg = "Failed to delete the tag.";
	
	//Should be able to take BOTH character and word
	
	private DbAdapter mDbHelper;
	
	//Controls
	private EditText editText;
	private EditText editPrivateText;
	private ListView lv;
	private Button addTagButton;
	
	//Variables
	private long id;
	private List<String> currentTags;
	private boolean isChanged;
	
	ItemType type;
	ArrayAdapter<String> arrAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.tag); //tag.xml

        editText = (EditText) findViewById(R.id.edittext);
        editPrivateText = (EditText) findViewById(R.id.editprivate);
        lv = (ListView) findViewById(R.id.list);
        addTagButton = (Button) findViewById(R.id.add_tag_button);
        
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        
        //Grab the intent/extras. This should be called from CharacterCreation
        id = this.getIntent().getLongExtra("ID", -1); 
        type = ItemType.valueOf(getIntent().getStringExtra("TYPE"));
        
        Log.e("ID",Long.toString(id));
        Log.e("TYPE",type.toString());
        
        switch(type)
        {
        case CHARACTER:
        	currentTags = mDbHelper.getCharacterTags(id);
        	break;
        case WORD:
        	currentTags = mDbHelper.getWordTags(id);
        	break;
        case LESSON:
        	currentTags = mDbHelper.getLessonTags(id);
        	break;
        default:
    		Log.e("Tag", "Unsupported Type");
        }

        //add private tag
        currentTags.add(0, PRIVATE_PREFIX+mDbHelper.getPrivateTag(id, type));
        
        //Populate the ListView
        arrAdapter = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_list_item_1, currentTags);
        arrAdapter.notifyDataSetChanged();
       
        lv.setAdapter(arrAdapter);
        
        /*final String [] items = new String[]{"boo","hey","wga","Item4"};
        
        ArrayAdapter ad = new ArrayAdapter(this,android.R.layout.simple_list_item_multiple_choice,items);
        lv.setAdapter(ad);*/
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        registerForContextMenu(lv);
        
        isChanged = false;
        }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
	    menu.setHeaderTitle("Options");
	    for (int i = 0; i<menuItems.length; i++) {
	      menu.add(Menu.NONE, i, i, menuItems[i]);
	    }
	}	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		
		if (menuItemIndex == menuItemsInd.Delete.ordinal()) {
			String selectedTag = (String)lv.getItemAtPosition(info.position);	
			boolean isPrivateTag = selectedTag.regionMatches(0, PRIVATE_PREFIX, 0, PRIVATE_PREFIX.length());	
			boolean isSqlQuerySuccessful = false;

			switch(type)
			{
			case CHARACTER:
				if (isPrivateTag) {
					isSqlQuerySuccessful = (mDbHelper.updatePrivateTag(id, "") > 0);
				}
				else {
					isSqlQuerySuccessful = mDbHelper.deleteTag(id,'"' + selectedTag + '"');
				}
				break;
			case WORD:
				if (isPrivateTag) {
					isSqlQuerySuccessful = (mDbHelper.updatePrivateWordTag(id, "") > 0);
				}
				else {
					isSqlQuerySuccessful = mDbHelper.deleteWordTag(id, '"' + selectedTag + '"');
				}
				break;
			default:
				Log.e("Tag", "Unsupported Type");
				return false;
			}
			
			// show pop-up message and update ListView 
			if (isSqlQuerySuccessful) {
				showToast(TagDeleteSuccessMsg);
				if (isPrivateTag) {
					currentTags.set(0, PRIVATE_PREFIX);
				} else {
					currentTags.remove(info.position);
				}
				arrAdapter.notifyDataSetChanged();
			} else {
				showToast(TagDeleteErrorMsg);
			}	
		} else {
			Log.e("Tag", "Unsupported context menu");
			return false;
		}

		isChanged = true;
		
		return true;
	}	
	
	/**
	 * When you want to add a tag to a character/word,
	 * just add to database and then update the list view
	 * to refect that the tag has been added. The tag should 
	 * be at the bottom of the list view.
	 * @param view
	 */
	public void onAddTagButtonClick (View view)
    {
		if (view == addTagButton)
		{
			Editable input = editText.getText();
			String input2 = input.toString(); //This is the string of the tag you typed in
			
			switch(type)
	        {
	        case CHARACTER:
	        	mDbHelper.createTags(id, input2);
	        	break;
	        case WORD:
	        	mDbHelper.createWordTags(id, input2);
	        	break;
	        case LESSON:
	        	mDbHelper.createLessonTags(id, input2);
	        	break;
	        default:
	    		Log.e("Tag", "Unsupported Type");
	    		return;
	        }
			//update the listview --> update the entire view
			//Refactor this, because refreshing the view is inefficient
			currentTags.add(input2);
			//currentTags.clear();
			//currentTags = mDbHelper.getTags(id);
	        arrAdapter.notifyDataSetChanged();

			//Set edit text back to nothing
			editText.setText("");			
			isChanged = true;
		}
    }
	
	public void onAddPrivateTagButtonClick(View view){
		Editable input = editPrivateText.getText();
		String input2 = input.toString();
		if (type == ItemType.CHARACTER)
		{
			mDbHelper.updatePrivateTag(id, input2); //added it to db
		}
		else if (type == ItemType.WORD)
		{		
			mDbHelper.updatePrivateWordTag(id, input2);	
		}
		if(currentTags.get(0).contains(PRIVATE_PREFIX))
			currentTags.remove(0);
		currentTags.add(0,PRIVATE_PREFIX+input2);
		arrAdapter.notifyDataSetChanged();
		editPrivateText.setText("");
		isChanged = true;
	}

	private final void showToast(String msg){
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}	
	
	@Override
	public void onBackPressed() {
		if (isChanged) {
			setResult(RESULT_OK);
		} else {
			setResult(RESULT_CANCELED);
		}
		finish();
	}
}
