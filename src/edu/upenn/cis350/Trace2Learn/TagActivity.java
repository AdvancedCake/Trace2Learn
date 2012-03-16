package edu.upenn.cis350.Trace2Learn;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class TagActivity extends Activity {

	//Should be able to take BOTH character and word
	
	private DbAdapter mDbHelper;
	
	//Controls
	private EditText editText;
	private ListView tagList;
	private Button addTagButton;
	
	//Variables
	private long id;
	private String lessonItemType;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag); //tag.xml
        
        editText = (EditText) findViewById(R.id.edittext);
        tagList = (ListView) findViewById(R.id.list);
        addTagButton = (Button) findViewById(R.id.add_tag_button);
        
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        
        //Grab the intent/extras. This should be called from CharacterCreation
        id = this.getIntent().getLongExtra("ID", -1); 
        
        //Populate the ListView
        
        //Assuming that it is a character
        tagList = mDbHelper.
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
			
			//if the LessonItem is a character
			mDbHelper.createTags(id, input2); //added it to db
			
			//else if the LessonItem is a word
			//mDbHelper.createWordTags(word id, input2);
			
			//update the listview --> update the entire view
			//Refactor this, because refreshing the view is inefficient
			
			
			//Set edit text back to nothing
			editText.setText("");
		}
    }
	

}
