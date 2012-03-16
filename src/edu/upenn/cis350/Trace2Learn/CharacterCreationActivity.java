package edu.upenn.cis350.Trace2Learn;

import edu.upenn.cis350.Trace2Learn.R.id;
import edu.upenn.cis350.Trace2Learn.Characters.LessonCharacter;
import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class CharacterCreationActivity extends Activity {
	
	private LinearLayout _characterViewSlot;
	private CharacterCreationPane _creationPane;
	private CharacterPlaybackPane _playbackPane;
	private Button _contextButton;
	
	private Mode _currentMode = Mode.INVALID;
	
	private enum Mode
	{
		CREATION,
		DISPLAY,
		ANIMATE,
		SAVE,
		INVALID;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPaint = new Paint();
		setContentView(R.layout.test_char_display);

		_characterViewSlot = (LinearLayout)this.findViewById(id.character_view_slot);
		_contextButton = (Button)this.findViewById(id.context_button);
		_creationPane = new CharacterCreationPane(this, mPaint);
		_playbackPane = new CharacterPlaybackPane(this, mPaint, false, 2);

		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);
		
		setCharacterCreationPane();

	}
	
	private synchronized void setCharacterCreationPane()
	{
		if(_currentMode != Mode.CREATION)
		{
			_currentMode = Mode.CREATION;
			_characterViewSlot.removeAllViews();
			_characterViewSlot.addView(_creationPane);	
			_contextButton.setText("Clear");
		}
	}
	
	private synchronized void setCharacterDisplayPane()
	{
		_playbackPane.setAnimated(false);
		if(_currentMode != Mode.DISPLAY)
		{
			LessonCharacter curChar = _creationPane.getCharacter();
			_currentMode = Mode.DISPLAY;
			_playbackPane.setCharacter(curChar);
			_characterViewSlot.removeAllViews();
			_characterViewSlot.addView(_playbackPane);
			_contextButton.setText("Animate");
		}
	}
	
	private synchronized void setCharacterSavePane()
	{
		if(_currentMode != Mode.SAVE)
		{
			_currentMode = Mode.SAVE;
			_characterViewSlot.removeAllViews();
			//_characterViewSlot.addView(_savePane);	
			_contextButton.setText("Commit");
		}
	}

	public void setContentView(View view)
	{
        super.setContentView(view);
    }
	
	private Paint mPaint;

	public void colorChanged(int color) {
		mPaint.setColor(color);
	}
	
	
	public void onContextButtonClick(View view)
	{
		switch(_currentMode)
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

	public void onCreateButtonClick(View view)
	{
		setCharacterCreationPane();
	}
	
	public void onSaveButtonClick(View view)
	{
		//setCharacterSavePane();
	}
	
	public void onDisplayButtonClick(View view)
	{
		Log.i("CLICK", "DISPLAY");
		setCharacterDisplayPane();
	}

}