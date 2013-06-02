package com.trace2learn.TraceLibrary;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.LessonCharacter;

public class ViewCharacterActivity extends TraceBaseActivity {

	private FrameLayout charSlot;
	private CharacterCreationPane creationPane;
	private CharacterPlaybackPane playbackPane;	
	private CharacterTracePane tracePane;
	private Button left1;
	private Button left2;
	private Button right1;
	private Button right2;
	
	private TextView tagText;

	private Mode currentMode = Mode.INVALID;

	private String id_to_pass = null;
	
	private boolean isChanged = false;

	public enum Mode {
		CREATION, DISPLAY, INVALID, TRACE;
	}

	private static enum requestCodeENUM { EDIT_TAG };

	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.display_char);

		charSlot = (FrameLayout) findViewById(R.id.character_slot);
		creationPane = new CharacterCreationPane(this);
		playbackPane = new CharacterPlaybackPane(this, false, 2);
		tracePane = new CharacterTracePane(this);
		
        left1  = (Button) findViewById(R.id.left1Button);
        left2  = (Button) findViewById(R.id.left2Button);
        right2 = (Button) findViewById(R.id.right2Button);
        right1 = (Button) findViewById(R.id.right1Button);

		tagText = (TextView) this.findViewById(R.id.tag_list);

		initializeMode();

	}
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    };

	/**
	 * Initialize the display mode, if the activity was started with intent to
	 * display a character, that character should be displayed
	 */
	private void initializeMode() 
	{
		Bundle bun = getIntent().getExtras();
		if (bun != null && bun.containsKey("mode")) {
			String mode = bun.getString("mode");
            setCharacter(Toolbox.dba.getCharacterById(bun.getString("charId")));
            id_to_pass = bun.getString("charId");
            updateTags();
            
			if (mode.equals("trace")) {
                setCharacterTracePane();
			} else { // display mode
			    setCharacterDisplayPane();
			}
		} else { // creation mode
		    setCharacter(new LessonCharacter(true));
			setCharacterCreationPane();
		}
	}

	/**
	 * Switches the display mode to creation
	 */
	private synchronized void setCharacterCreationPane() 
	{
		if (currentMode != Mode.CREATION) 
		{
			currentMode = Mode.CREATION;
			charSlot.removeAllViews();
			charSlot.addView(creationPane);
            left1.setVisibility(View.VISIBLE);
            left2.setVisibility(View.VISIBLE);
            right2.setVisibility(View.INVISIBLE);
            right1.setVisibility(View.VISIBLE);
			left1.setText(R.string.clear);
			left2.setText(R.string.undo);
			right1.setText(R.string.save);
		}
	}

	/**
	 * Switches the display mode to display
	 */
	private synchronized void setCharacterDisplayPane()
	{
		playbackPane.setAnimated(true);
		if (currentMode != Mode.DISPLAY) 
		{
			LessonCharacter curChar = creationPane.getCharacter();
			setCharacter(curChar);
			currentMode = Mode.DISPLAY;
			charSlot.removeAllViews();
			charSlot.addView(playbackPane);
            left1.setVisibility(View.VISIBLE);
            left2.setVisibility(View.INVISIBLE);
            right2.setVisibility(View.VISIBLE);
            right1.setVisibility(View.VISIBLE);
            left1.setText(R.string.edit);
            right2.setText(R.string.practice);
            right1.setText(Html.fromHtml("<b>" + getString(R.string.animate) +
                    "</b>"));
		}
	}

	/**
	 * Switches the display mode to display
	 */
	private synchronized void setCharacterTracePane()
	{
		tracePane.clearPane();
		if (currentMode != Mode.TRACE) 
		{
			LessonCharacter curChar = creationPane.getCharacter();
			setCharacter(curChar);
			currentMode = Mode.TRACE;
			charSlot.removeAllViews();
			charSlot.addView(tracePane);
            left1.setVisibility(View.VISIBLE);
            left2.setVisibility(View.INVISIBLE);
            right2.setVisibility(View.VISIBLE);
            right1.setVisibility(View.VISIBLE);
            left1.setText(R.string.edit);
            right2.setText(Html.fromHtml("<b>" + getString(R.string.practice) +
                    "</b>"));
            right1.setText(R.string.animate);
		}
	}
	
	public void setContentView(View view)
	{
		super.setContentView(view);
	}

	private void setCharacter(LessonCharacter character)
	{
		creationPane.setCharacter(character);
		playbackPane.setCharacter(character);
		tracePane.setTemplate(character);
	}

	private void updateTags()
	{
		if (id_to_pass !=null)
		{
			LessonCharacter ch = Toolbox.dba.getCharacterById(id_to_pass);
			
			StringBuilder sb = new StringBuilder();
			sb.append(ch.getKeyValuesToString());
			if (ch.getKeyValues().size() > 0 && ch.getTags().size() > 0) {
				sb.append(", ");
			}
			sb.append(ch.getTagsToString());
			
			tagText.setText(sb.toString());
			setCharacter(ch); // why here? doesn't match with the method name
		}
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
		updateTags();
	}

	public void onLeft1ButtonClick(View view) { // either "Clear" or "Edit"
	    switch (currentMode) {
	        case CREATION: // clear
                creationPane.clearPane();
                break;
	        case DISPLAY:
	        case TRACE:
                setCharacterCreationPane();
                right2.setVisibility(View.VISIBLE);
                right2.setText(R.string.cancel);
                break;
	    }
	}

	public void onLeft2ButtonClick(View view) { // "undo"
	    switch (currentMode) {
	        case CREATION: // undo
	            creationPane.undoLastStroke();
	            break;
	        case DISPLAY:
	        case TRACE:
	            break;
	    }
	}

    public void onRight2ButtonClick(View view) { // "practice" or "cancel"
        switch (currentMode) {
            case CREATION: // "cancel"
                initializeMode();
                break;
            case DISPLAY:
            case TRACE:
                // "practice"
                setCharacterTracePane();
                break;
        }
    }

    public void onRight1ButtonClick(View view) { // "save" or "playback"
        switch (currentMode) {
            case CREATION: // "save"
                LessonCharacter character = creationPane.getCharacter();
                if(character.getNumStrokes() == 0){
                    Toolbox.showToast(getApplicationContext(),
                            "Please add a stroke");
                    return;
                }
                saveChar(character);
                createTags();
                isChanged = true;
                break;
            case DISPLAY:
            case TRACE:
                // "playback"
                setCharacterDisplayPane();
                break;
        }
    }
    
    private void saveChar(LessonCharacter character) {
        String id = character.getStringId();
        if(id == null)
            Toolbox.dba.addCharacter(character);
        else
            Toolbox.dba.modifyCharacter(character);
        Log.e("Adding to DB", character.getStringId());
        id_to_pass = character.getStringId();
        Toolbox.showToast(getApplicationContext(), "Character saved");
    }
    
	public void createTags() {
		LessonCharacter character = creationPane.getCharacter();
		if (id_to_pass != null) {
			Log.e("Passing this CharID", id_to_pass);
			Intent i = new Intent(this, TagActivity.class);

			i.putExtra("ID", id_to_pass);
			i.putExtra("TYPE", character.getItemType().toString());

			startActivityForResult(i, requestCodeENUM.EDIT_TAG.ordinal());
		} else {
			tagText.setText("Error: Save the character before adding tags");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == requestCodeENUM.EDIT_TAG.ordinal()) {
	        isChanged = resultCode == RESULT_OK;
	        close();
	    }
	}
	
	@Override
	public void onBackPressed() {
	    close();
	}
	
	private void close() {
	    if (isChanged) {
            setResult(RESULT_OK);
	    } else {
	        setResult(RESULT_CANCELED);
	    }
	    finish();
	}

}
