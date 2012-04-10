package edu.upenn.cis350.Trace2Learn;

import java.util.List;

import edu.upenn.cis350.Trace2Learn.R.id;
import edu.upenn.cis350.Trace2Learn.Database.DbAdapter;
import edu.upenn.cis350.Trace2Learn.Database.LessonCharacter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CharacterCreationActivity extends Activity {

	private LinearLayout _characterViewSlot;
	private CharacterCreationPane _creationPane;
	private CharacterPlaybackPane _playbackPane;
	private Button _contextButton;

	private TextView _tagText;

	private DbAdapter _dbHelper;

	private Mode _currentMode = Mode.INVALID;

	private long id_to_pass = -1;

	private enum Mode {
		CREATION, DISPLAY, ANIMATE, SAVE, INVALID;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test_char_display);

		_characterViewSlot =(LinearLayout)findViewById(id.character_view_slot);
		_contextButton = (Button) this.findViewById(id.context_button);
		_creationPane = new CharacterCreationPane(this);
		_playbackPane = new CharacterPlaybackPane(this, false, 2);

		setCharacter(new LessonCharacter());

		_tagText = (TextView) this.findViewById(id.tag_list);

		_dbHelper = new DbAdapter(this);
		_dbHelper.open();

		initializeMode();

	}

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
			_contextButton.setText("Clear");
		}
	}

	/**
	 * Switches the display mode to display
	 */
	private synchronized void setCharacterDisplayPane()
	{
		_playbackPane.setAnimated(false);
		if (_currentMode != Mode.DISPLAY) 
		{
			LessonCharacter curChar = _creationPane.getCharacter();
			setCharacter(curChar);
			_currentMode = Mode.DISPLAY;
			_characterViewSlot.removeAllViews();
			_characterViewSlot.addView(_playbackPane);
			_contextButton.setText("Animate");
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
	}

	private void updateTags()
	{
		if (id_to_pass >= 0)
		{
			List<String> tags = _dbHelper.getTags(id_to_pass);
			this._tagText.setText(tagsToString(tags));
			setCharacter(_dbHelper.getCharacterById(id_to_pass));
		}
	}

	public void onContextButtonClick(View view)
	{
		switch (_currentMode)
		{
		case CREATION:
			_creationPane.clearPane();
			break;
		case DISPLAY:
			_currentMode = Mode.ANIMATE;
			_playbackPane.setAnimated(true);
			_contextButton.setText("Stop");
			break;
		case ANIMATE:
			_currentMode = Mode.DISPLAY;
			_playbackPane.setAnimated(false);
			_contextButton.setText("Animate");
			break;
		case SAVE:
			break;
		}
	}

	@Override
	public void onRestart()
	{
		super.onRestart();
		updateTags();
	}

	private String tagsToString(List<String> tags)
	{
		StringBuffer buf = new StringBuffer();
		for (String str : tags)
		{
			buf.append(str + ", ");
		}

		return buf.toString();
	}

	public void onCreateButtonClick(View view)
	{
		setCharacterCreationPane();
	}

	public void onSaveButtonClick(View view)
	{
		LessonCharacter character = _creationPane.getCharacter();
		_dbHelper.addCharacter(character);
		Log.e("Adding to DB", Long.toString(character.getId()));
		id_to_pass = character.getId();
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

	public void onDisplayButtonClick(View view) 
	{
		Log.i("CLICK", "DISPLAY");
		setCharacterDisplayPane();
	}

}
