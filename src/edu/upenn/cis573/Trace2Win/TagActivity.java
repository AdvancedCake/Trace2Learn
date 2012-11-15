package edu.upenn.cis573.Trace2Win;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import edu.upenn.cis573.Trace2Win.Database.LessonItem;
import edu.upenn.cis573.Trace2Win.Database.LessonItem.ItemType;

public class TagActivity extends Activity {

    private final String PRIVATE_PREFIX = "Private: ";
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
    private EditText privateTagEntry;
    private ListView lv;
    private Button addTagButton;

    //Variables
    private long id; // item ID
    private List<String> currentTags;
    private boolean isChanged;

    ItemType type;
    ArrayAdapter<String> arrAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tag); //tag.xml

        tagEntry = (EditText) findViewById(R.id.edittext);
        privateTagEntry = (EditText) findViewById(R.id.editkey);
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
        currentTags.add(0, PRIVATE_PREFIX + mDbHelper.getPrivateTag(id, type));

        //Populate the ListView
        arrAdapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_list_item_1, currentTags);
        arrAdapter.notifyDataSetChanged();

        lv.setAdapter(arrAdapter);

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
        }

        // move
        else if (menuItemIndex == menuItemsInd.MoveUp.ordinal() ||
                menuItemIndex == menuItemsInd.MoveDown.ordinal()) {
            // going to swap sort values with the item above or below

            if (info.position == 0) { // this is the private tag
                showToast("Cannot move the private tag");
                return false;
            }

            // need to get other item
            int otherPos;
            if (menuItemIndex == menuItemsInd.MoveUp.ordinal()) {
                otherPos = info.position - 1;
            } else {
                otherPos = info.position + 1;
            }

            // check that item exists
            if (otherPos < 1) { // Private tag is item 0
                showToast("Cannot move this tag up");
                return false;
            } else if (otherPos >= currentTags.size()) {
                showToast("Cannot move this tag down");
                return false;
            }

            String tag   = (String) lv.getItemAtPosition(info.position);
            String other = (String) currentTags.get(otherPos);
            String table = null;

            switch(type) {
                case CHARACTER:
                    table = DbAdapter.CHARTAG_TABLE;
                    break;
                case WORD:
                    table = DbAdapter.WORDTAG_TABLE;
                    break;
                case LESSON:
                    table = DbAdapter.LESSONTAG_TABLE;
                    break;
                default:
                    Log.e("Tag", "Unsupported Type");
                    return false;
            }

            boolean result = mDbHelper.swapTags(table, id, tag, other);
            Log.e("Move result", Boolean.toString(result));
            if (!result) {
                showToast("Move failed");
                return false;
            }

            // update local copy
            // success, so update the local copy
            String[] arr = new String[currentTags.size()];
            arr = currentTags.toArray(arr);

            int tagIndex = currentTags.indexOf(tag);
            int otherIndex = currentTags.indexOf(other);
            String temp = arr[tagIndex];
            arr[tagIndex] = arr[otherIndex];
            arr[otherIndex] = temp;

            currentTags = new ArrayList<String>(Arrays.asList(arr)); 
            arrAdapter = new ArrayAdapter<String>(this, 
                    android.R.layout.simple_list_item_1, currentTags);
            lv.setAdapter(arrAdapter);
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
            arrAdapter.notifyDataSetChanged();

            //Set edit text back to nothing
            tagEntry.setText("");			
            isChanged = true;
        }
    }

    public void onAddPrivateTagButtonClick(View view){
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
        if(currentTags.get(0).contains(PRIVATE_PREFIX))
            currentTags.remove(0);
        currentTags.add(0,PRIVATE_PREFIX+input);
        arrAdapter.notifyDataSetChanged();
        privateTagEntry.setText("");
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
