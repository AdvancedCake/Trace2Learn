package edu.upenn.cis573.Trace2Win;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.upenn.cis573.Trace2Win.R.id;
import edu.upenn.cis573.Trace2Win.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;

public class CharacterCreationActivity extends Activity {

	private LinearLayout _characterViewSlot;
	private CharacterCreationPane _creationPane;
	private CharacterPlaybackPane _playbackPane;	
	private CharacterTracePane _tracePane;
	private Button left1;
	private Button left2;
	private Button right1;
	private Button right2;
	
	private TextView _tagText;

	private DbAdapter _dbHelper;

	private Mode _currentMode = Mode.INVALID;

	private long id_to_pass = -1;

	public enum Mode {
		CREATION, DISPLAY, ANIMATE, SAVE, INVALID, TRACE;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.display_char);

		_characterViewSlot = (LinearLayout) findViewById(R.id.character_view_slot);
		_creationPane = new CharacterCreationPane(this);
		_playbackPane = new CharacterPlaybackPane(this, false, 2);
		_tracePane = new CharacterTracePane(this);
		
        left1  = (Button) findViewById(R.id.left1Button);
        left2  = (Button) findViewById(R.id.left2Button);
        right1 = (Button) findViewById(R.id.right1Button);
        right2 = (Button) findViewById(R.id.right2Button);

		setCharacter(new LessonCharacter());

		_tagText = (TextView) this.findViewById(id.tag_list);

		_dbHelper = new DbAdapter(this);
		_dbHelper.open();

		initializeMode();

	}
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        _dbHelper.close();
    };

	/**
	 * Initialize the display mode, if the activity was started with intent to
	 * display a character, that character should be displayed
	 */
	private void initializeMode() 
	{
		Bundle bun = getIntent().getExtras();
		if (bun != null && bun.containsKey("mode")) 
		{
			String mode = bun.getString("mode");
			if (mode.equals("display")) 
			{
				setCharacter(_dbHelper.getCharacterById(bun.getLong("charId")));
				setCharacterDisplayPane();
				id_to_pass = bun.getLong("charId");
				updateTags();
			}
		} else 
		{
			setCharacterCreationPane();
		}
	}

	/**
	 * Switches the display mode to creation
	 */
	private synchronized void setCharacterCreationPane() 
	{
		if (_currentMode != Mode.CREATION) 
		{
			_currentMode = Mode.CREATION;
			_characterViewSlot.removeAllViews();
			_characterViewSlot.addView(_creationPane);
            left1.setVisibility(View.VISIBLE);
            left2.setVisibility(View.INVISIBLE);
            right2.setVisibility(View.INVISIBLE);
            right1.setVisibility(View.VISIBLE);
			left1.setText(R.string.clear);
			right1.setText(R.string.save);
		}
	}

	/**
	 * Switches the display mode to display
	 */
	private synchronized void setCharacterDisplayPane()
	{
		_playbackPane.setAnimated(true);
		if (_currentMode != Mode.DISPLAY) 
		{
			LessonCharacter curChar = _creationPane.getCharacter();
			setCharacter(curChar);
			_currentMode = Mode.DISPLAY;
			_characterViewSlot.removeAllViews();
			_characterViewSlot.addView(_playbackPane);
            left1.setVisibility(View.VISIBLE);
            left2.setVisibility(View.INVISIBLE);
            right2.setVisibility(View.VISIBLE);
            right1.setVisibility(View.VISIBLE);
            left1.setText(R.string.edit);
            right2.setText(R.string.practice);
            right1.setText(R.string.animate);
		}
	}

	/**
	 * Switches the display mode to display
	 */
	private synchronized void setCharacterTracePane()
	{
		_tracePane.clearPane();
		if (_currentMode != Mode.TRACE) 
		{
			LessonCharacter curChar = _creationPane.getCharacter();
			setCharacter(curChar);
			_currentMode = Mode.TRACE;
			_characterViewSlot.removeAllViews();
			_characterViewSlot.addView(_tracePane);
            left1.setVisibility(View.VISIBLE);
            left2.setVisibility(View.INVISIBLE);
            right2.setVisibility(View.VISIBLE);
            right1.setVisibility(View.VISIBLE);
            left1.setText(R.string.edit);
            right2.setText(R.string.practice);
            right1.setText(R.string.animate);
		}
	}
	
	public void setContentView(View view)
	{
		super.setContentView(view);
	}

	private void setCharacter(LessonCharacter character)
	{
		_creationPane.setCharacter(character);
		_playbackPane.setCharacter(character);
		_tracePane.setTemplate(character);
	}

	private void updateTags()
	{
		if (id_to_pass >= 0)
		{
			LessonCharacter ch = _dbHelper.getCharacterById(id_to_pass);
			
			StringBuilder sb = new StringBuilder();
			sb.append(ch.getKeyValuesToString());
			if (ch.getKeyValues().size() > 0 && ch.getTags().size() > 0) {
				sb.append(", ");
			}
			sb.append(ch.getTagsToString());
			
			_tagText.setText(sb.toString());
			setCharacter(ch); // why here? doesn't match with the method name
		}
	}

	public void onClearButtonClick(View view)
	{
		_creationPane.clearPane();
		_tracePane.clearPane();
		_playbackPane.clearPane();
	}
	
	public void onRight2ButtonClick(View view)
	{
		setCharacterTracePane();
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
		updateTags();
	}

	public void onLeft1ButtonClick(View view)
	{
		setCharacterCreationPane();
	}

	public void onLeft2ButtonClick(View view)
	{
		LessonCharacter character = _creationPane.getCharacter();
		if(character.getNumStrokes()==0){
			showToast("Please add a stroke");
			return;
		}
		long id = character.getId();
		if(id==-1)
			_dbHelper.addCharacter(character);
		else
			_dbHelper.modifyCharacter(character);
		Log.e("Adding to DB", Long.toString(character.getId()));
		id_to_pass = character.getId();
		//if(id >= 0)
		//{
			onTagButtonClick(view);
		//}
		updateTags();
	}

	public void onTagButtonClick(View view) 
	{
		LessonCharacter character = _creationPane.getCharacter();
		if (id_to_pass >= 0) 
		{
			Log.e("Passing this CharID", Long.toString(id_to_pass));
			Intent i = new Intent(this, TagActivity.class);

			i.putExtra("ID", id_to_pass);
			i.putExtra("TYPE", character.getItemType().toString());

			startActivity(i);
		} else
		{
			_tagText.setText("Error: Save the character before adding tags");
		}

	}

	public void onRight1ButtonClick(View view) 
	{
		Log.i("CLICK", "DISPLAY");
		setCharacterDisplayPane();
	}
	
	public void showToast(String msg){
		Context context = getApplicationContext();
		CharSequence text = msg;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

}
