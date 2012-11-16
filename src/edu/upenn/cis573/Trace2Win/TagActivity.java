package edu.upenn.cis573.Trace2Win;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
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
import edu.upenn.cis573.Trace2Win.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.Database.LessonItem.ItemType;

public class TagActivity extends Activity {

    private static final String[] menuItems = { "Move Up",
                                                "Move Down",
                                                "Delete"};
    private static enum menuItemsInd { MoveUp,
                                       MoveDown,
                                       Delete }
    private static final String TagDeleteSuccessMsg = "Deleted the tag successfully.";
    private static final String TagDeleteErrorMsg = "Failed to delete the tag.";

    //Should be able to take BOTH character and word

    private DbAdapter mDbHelper;

    //Controls
    private EditText tagEntry;
    private EditText keyEntry;
    private EditText valueEntry;
    private ListView id_lv;
    private ListView tag_lv;
    private Button addTagButton;
    private Button addIdButton;

    //Variables
    private long id; // item ID
    private Map<String, String> keyValMap;
    private List<String> currentKeys;
    private List<String> currentKeyVals;
    private List<String> currentTags;
    private boolean isChanged;

    ItemType type;
    ArrayAdapter<String> tagArrAdapter;
    ArrayAdapter<String> idArrAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Grab the intent/extras. This should be called from CharacterCreation
        id = this.getIntent().getLongExtra("ID", -1); 
        type = ItemType.valueOf(getIntent().getStringExtra("TYPE"));        

        // assign layout and cache views 
        switch(type)
        {
        case CHARACTER:
        	setContentView(R.layout.id_and_tag);
        	id_lv = (ListView) findViewById(R.id.id_list);
        	keyEntry = (EditText) findViewById(R.id.editkey);
        	valueEntry = (EditText) findViewById(R.id.editvalue);
        	addIdButton = (Button) findViewById(R.id.add_key_value_pair_button);
        	break;
        case WORD:
        case LESSON:
        	setContentView(R.layout.tag);
        	break;
        default:
        	Log.e("Tag", "Unsupported Type");
        }
        tagEntry = (EditText) findViewById(R.id.edittext);
        tag_lv = (ListView) findViewById(R.id.tag_list);
        addTagButton = (Button) findViewById(R.id.add_tag_button);

        mDbHelper = new DbAdapter(this);
        mDbHelper.open();     

        Log.e("ID",Long.toString(id));
        Log.e("TYPE",type.toString());

        switch(type)
        {
            case CHARACTER:
                currentTags = mDbHelper.getCharacterTags(id);
                currentKeys = new ArrayList<String>();
                currentKeyVals = new ArrayList<String>();
                keyValMap = mDbHelper.getCharKeyValues(id);
                for(String key: keyValMap.keySet()){
                	currentKeys.add(key);
                	currentKeyVals.add(key + ": " + keyValMap.get(key));
                }
                idArrAdapter = new ArrayAdapter<String>(this, 
                        android.R.layout.simple_list_item_1, currentKeyVals);
                idArrAdapter.notifyDataSetChanged();
                id_lv.setAdapter(idArrAdapter);
                id_lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                registerForContextMenu(id_lv);
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

        //Populate the ListView
        tagArrAdapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_list_item_1, currentTags);
        tagArrAdapter.notifyDataSetChanged();
        tag_lv.setAdapter(tagArrAdapter);

        tag_lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        registerForContextMenu(tag_lv);

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
        ListView view = (ListView)info.targetView.getParent();
        Log.d("Tag", view.toString());
        int menuItemIndex = item.getItemId();
        boolean isID = view == id_lv; // true is ID, otherwise it's a tag
        if (menuItemIndex == menuItemsInd.Delete.ordinal()) {
            String selectedItem = (String)view.getItemAtPosition(info.position);
            
            boolean isSqlQuerySuccessful = false;

            switch(type)
            {
                case CHARACTER:
                    if (isID) {
                        isSqlQuerySuccessful = mDbHelper.deleteCharKeyValue(id,'"' + currentKeys.get(info.position) + '"');
                    }
                    else {
                        isSqlQuerySuccessful = mDbHelper.deleteTag(id,'"' + selectedItem + '"');
                    }
                    break;
                case WORD:
//                    if (isPrivateTag) {
//                        isSqlQuerySuccessful = (mDbHelper.updatePrivateWordTag(id, "") > 0);
//                    }
//                    else {
                        isSqlQuerySuccessful = mDbHelper.deleteWordTag(id, '"' + selectedItem + '"');
//                    }
                    break;
                default:
                    Log.e("Tag", "Unsupported Type");
                    return false;
            }

            // show pop-up message and update ListView 
            if (isSqlQuerySuccessful) {
                showToast(TagDeleteSuccessMsg);
                if (isID) {
                	keyValMap.remove(currentKeys.get(info.position));
                	currentKeyVals.remove(info.position);
                	currentKeys.remove(info.position);
                    idArrAdapter.notifyDataSetChanged();
                } else {
                    currentTags.remove(info.position);
                    tagArrAdapter.notifyDataSetChanged();
                }
            } else {
                showToast(TagDeleteErrorMsg);
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

            String table = null;

            switch(type) {
                case CHARACTER:
                    if (isID) {
                        table = DbAdapter.CHARKEYVALUES_TABLE;
                    } else { // it's a tag
                        table = DbAdapter.CHARTAG_TABLE;
                    }
                    break;
                case WORD:
                    if (isID) {
//                        table = DbAdapter.WORDKEYVALUES_TABLE;
                        // TODO implement for words.
                        return false;
                    } else { // it's a tag
                        table = DbAdapter.WORDTAG_TABLE;
                    }
                    break;
                case LESSON:
                    table = DbAdapter.LESSONTAG_TABLE;
                    break;
                default:
                    Log.e("Tag", "Unsupported Type");
                    return false;
            }

            boolean result = false;
            if (isID) {
                // check that item exists
                if (otherPos < 0) {
                    showToast("Cannot move this ID up");
                    return false;
                } else if (otherPos >= currentKeys.size()) {
                    showToast("Cannot move this ID down");
                    return false;
                }
                
                String key   = currentKeys.get(info.position);
                String other = currentKeys.get(otherPos);
                
                result = mDbHelper.swapKeyValues(table, id, key, other);
                
                Log.e("Move result", Boolean.toString(result));
                if (!result) {
                    showToast("Move failed");
                    return false;
                }
                
                // success, so update the local copy
                String[] kArr  = new String[currentKeys.size()];
                String[] kvArr = new String[currentKeyVals.size()];
                kArr  = currentKeys.toArray(kArr);
                kvArr = currentKeyVals.toArray(kvArr);
                
                int keyIndex   = currentKeys.indexOf(key);
                int otherIndex = currentKeys.indexOf(other);
                String temp      = kArr[keyIndex];
                kArr[keyIndex]   = kArr[otherIndex];
                kArr[otherIndex] = temp;
                temp              = kvArr[keyIndex];
                kvArr[keyIndex]   = kvArr[otherIndex];
                kvArr[otherIndex] = temp;
                
                currentKeys = new ArrayList<String>(Arrays.asList(kArr));
                currentKeyVals = new ArrayList<String>(Arrays.asList(kvArr));
                idArrAdapter = new ArrayAdapter<String>(this, 
                        android.R.layout.simple_list_item_1, currentKeyVals);
                id_lv.setAdapter(idArrAdapter);
            } else {
                // check that item exists
                if (otherPos < 0) {
                    showToast("Cannot move this tag up");
                    return false;
                } else if (otherPos >= currentTags.size()) {
                    showToast("Cannot move this tag down");
                    return false;
                }
                
                String tag   = currentTags.get(info.position);
                String other = currentTags.get(otherPos);
                result = mDbHelper.swapTags(table, id, tag, other);
                
                Log.e("Move result", Boolean.toString(result));
                if (!result) {
                    showToast("Move failed");
                    return false;
                }
                
                // success, so update the local copy
                String[] arr = new String[currentTags.size()];
                arr = currentTags.toArray(arr);

                int tagIndex = currentTags.indexOf(tag);
                int otherIndex = currentTags.indexOf(other);
                String temp = arr[tagIndex];
                arr[tagIndex] = arr[otherIndex];
                arr[otherIndex] = temp;

                currentTags = new ArrayList<String>(Arrays.asList(arr)); 
                tagArrAdapter = new ArrayAdapter<String>(this, 
                        android.R.layout.simple_list_item_1, currentTags);
                tag_lv.setAdapter(tagArrAdapter);
            }
            
            isChanged = true;
            return true;
        }

        else {
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
            String input = tagEntry.getText().toString();
            if (input.length() == 0) return;

            // check duplicate tag
            if (currentTags.contains(input)) {
                showToast("Tag already exists");
                return;
            }

            switch(type)
            {
                case CHARACTER:
                    mDbHelper.createTags(id, input);
                    break;
                case WORD:
                    mDbHelper.createWordTags(id, input);
                    break;
                case LESSON:
                    mDbHelper.createLessonTags(id, input);
                    break;
                default:
                    Log.e("Tag", "Unsupported Type");
                    return;
            }
            //update the listview --> update the entire view
            //Refactor this, because refreshing the view is inefficient
            currentTags.add(input);
            tagArrAdapter.notifyDataSetChanged();

            //Set edit text back to nothing
            tagEntry.setText("");			
            isChanged = true;
        }
    }

    /*public void onAddPrivateTagButtonClick(View view){
        String input = privateTagEntry.getText().toString();
        if (input.length() == 0) return;		

        if (type == ItemType.CHARACTER)
        {
            mDbHelper.updatePrivateTag(id, input); //added it to db
        }
        else if (type == ItemType.WORD)
        {		
            mDbHelper.updatePrivateWordTag(id, input);	
        }
        if(currentTags1.get(0).contains(PRIVATE_PREFIX))
            currentTags1.remove(0);
        currentTags1.add(0,PRIVATE_PREFIX+input);
        arrAdapter.notifyDataSetChanged();
        privateTagEntry.setText("");
        isChanged = true;
    }*/

    public void onAddKeyValuePairButtonClick(View view){
    	if (view == addIdButton)
    	{
    		String keyInput = keyEntry.getText().toString(); 
    		String valueInput = valueEntry.getText().toString();
    		if (keyInput.length() == 0 || valueInput.length() == 0) return;		
    		
    		// check duplicate tag
            if (keyValMap.get(keyInput) != null) {
                showToast("Key already exists");
                return;
            }
    		
    		// what do we do about other types?
            // R: Add functionality for word. Lessons don't have tags anyway so whatever.
    		if (type == ItemType.CHARACTER)
    		{
    			mDbHelper.createCharKeyValue(id, keyInput, valueInput);
    			// added it to db
    		}
    		keyValMap.put(keyInput, valueInput);
    		currentKeys.add(keyInput);
    		currentKeyVals.add(keyInput + ": " + valueInput);
        	idArrAdapter.notifyDataSetChanged();
        	keyEntry.setText("");			
        	valueEntry.setText("");
        	isChanged = true;
    	}
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
