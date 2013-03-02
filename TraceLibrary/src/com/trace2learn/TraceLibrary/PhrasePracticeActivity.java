package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.trace2learn.TraceLibrary.Database.DbAdapter;
import com.trace2learn.TraceLibrary.Database.LessonCharacter;
import com.trace2learn.TraceLibrary.Database.LessonWord;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.ViewAnimator;

@SuppressLint("HandlerLeak")
public class PhrasePracticeActivity extends Activity {
		
	private TextView _tagText;
	private TextView _phraseTitle;

	private DbAdapter _dbHelper;

	private Mode _currentMode = Mode.INVALID;
	private String _lessonID = null;
	private int _wordIndex;
	private int _collectionSize;
	private String _lessonName;

	private LessonWord _word;

	private ArrayList<LessonCharacter> _characters;
	private ArrayList<Bitmap> _bitmaps;
	
	private ArrayList<SquareLayout> _displayLayouts;
	private ArrayList<SquareLayout> _traceLayouts;
	
	private ArrayList<CharacterPlaybackPane> _playbackPanes;
	private ArrayList<CharacterTracePane> _tracePanes;
	
	private ImageAdapter _imgAdapter;
	
	private Gallery _gallery;
	
	private ViewAnimator _animator;
	
	private Button playButton;
	private Button traceButton;
	
	private final String PINYIN_KEY = "pinyin";
	
	private enum Mode {
		CREATION, DISPLAY, ANIMATE, SAVE, INVALID, TRACE;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.practice_phrase);

		_animator = (ViewAnimator) findViewById(R.id.view_slot);
		
		_characters = new ArrayList<LessonCharacter>();
		_bitmaps = new ArrayList<Bitmap>();
		
		_displayLayouts = new ArrayList<SquareLayout>();
		_traceLayouts = new ArrayList<SquareLayout>();
		
		_playbackPanes = new ArrayList<CharacterPlaybackPane>();
		_tracePanes = new ArrayList<CharacterTracePane>();
		
		
		_imgAdapter = new ImageAdapter(this,_bitmaps);
        _gallery = (Gallery) findViewById(R.id.gallery);
        _gallery.setSpacing(0);
        
        _gallery.setAdapter(_imgAdapter);
		_gallery.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				setSelectedCharacter(position);
			}
			
		});

		_tagText     = (TextView) findViewById(R.id.tag_list);
		_phraseTitle = (TextView) findViewById(R.id.phraseTitle);
		
		playButton  = (Button) findViewById(R.id.animate_button);
		traceButton = (Button) findViewById(R.id.trace_button);

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
		Bundle bun      = getIntent().getExtras();
		Context context = getApplicationContext();
		if (bun != null && bun.containsKey("wordId")) 
		{
			String wordId = bun.getString("wordId");
			_word = _dbHelper.getWordById(wordId);
            setWord(_word);
			updateTags();
			
			_lessonID = bun.getString("lessonID");
			if (_lessonID == null) {
				_phraseTitle.setText("");
			} else {
				_wordIndex = bun.getInt("index");
				_collectionSize = bun.getInt("collectionSize");
				_lessonName = _dbHelper.getLessonById(_lessonID).getLessonName();
				_phraseTitle.setText(_lessonName + " - " + _wordIndex + 
				                     " of " + _collectionSize);
			}
			
			String mode = bun.getString("mode");
			if (mode.equals("display")) {
	            setDisplayPane();
			} else if (mode.equals("trace")) {
			    setCharacterTracePane();
			}
		}
		else
		{
			Toolbox.showToast(context, "No word selected");
			finish();
		}
	}

	private void setSelectedCharacter(int position) {
		_animator.setDisplayedChild(position);
		_tracePanes.get(position).clearPane();
		updateTags();
	}
	
	private void setWord(LessonWord word) {
		setCharacterList(word.getCharacterIds());
		setSelectedCharacter(0);
	}

	private void setCharacterList(List<String> ids)
	{
		_characters.clear();
		_bitmaps.clear();
		_tracePanes.clear();
		_playbackPanes.clear();
		_traceLayouts.clear();
		_displayLayouts.clear();
		for(String id : ids)
		{
			LessonCharacter ch = _dbHelper.getCharacterById(id);
			Bitmap bmp = BitmapFactory.buildBitmap(ch, 64, 64);
			this._characters.add(ch);
			this._bitmaps.add(bmp);
			SquareLayout disp = new SquareLayout(_animator.getContext());
			CharacterPlaybackPane dispPane = new CharacterPlaybackPane(disp.getContext(), false, 2);
			dispPane.setCharacter(ch);
			disp.addView(dispPane);
			
			this._displayLayouts.add(disp);
			this._playbackPanes.add(dispPane);
			
			SquareLayout trace = new SquareLayout(_animator.getContext());
			CharacterTracePane tracePane = new CharacterTracePane(disp.getContext());
			tracePane.setTemplate(ch);
			tracePane.addMoveToNextHandler(moveToNext);
			trace.addView(tracePane);
			
			this._traceLayouts.add(trace);
			this._tracePanes.add(tracePane);
		}
		_imgAdapter.update(_bitmaps);
        _imgAdapter.notifyDataSetChanged();
	}
	
	/**
	 * Switches the display mode to display
	 */
	private synchronized void setDisplayPane()
	{
		int child = _animator.getDisplayedChild();
		_playbackPanes.get(child).setAnimated(true);
		
		if (_currentMode != Mode.DISPLAY) 
		{
			int curInd = _animator.getDisplayedChild();
			_animator.removeAllViews();
			for(SquareLayout disp : this._displayLayouts)
			{
				_animator.addView(disp);
			}
			_animator.setDisplayedChild(curInd);
			_currentMode = Mode.DISPLAY;
            playButton.setText(Html.fromHtml("<b>" +
                    getString(R.string.animate) + "</b>"));
            traceButton.setText(getString(R.string.practice));
		}
	}

	/**
	 * Switches the display mode to display
	 */
	private synchronized void setCharacterTracePane()
	{
		// this used to be onClearButtonClick
		int child = _animator.getDisplayedChild();
		this._tracePanes.get(child).clearPane();

		if (_currentMode != Mode.TRACE) 
		{
			int curInd = _animator.getDisplayedChild();
			_animator.removeAllViews();
			for(SquareLayout trace : this._traceLayouts)
			{
				_animator.addView(trace);
			}
			_animator.setDisplayedChild(curInd);
			_currentMode = Mode.TRACE;
            traceButton.setText(Html.fromHtml("<b>" +
                    getString(R.string.practice) + "</b>"));
            playButton.setText(getString(R.string.animate));
		}
	}
	
	public void setContentView(View view)
	{
		super.setContentView(view);
	}

//	private void setCharacter(LessonCharacter character)
//	{
//		_playbackPane.setCharacter(character);
//		_tracePane.setTemplate(character);
//	}
	
	private void updateTags()
	{
		if (_word != null) {
			StringBuilder sb = new StringBuilder();
			// display any tags
			sb.append(_word.getTagsToString());
			HashMap<String, String> map = _word.getKeyValues();
			if (map.containsKey(PINYIN_KEY)) {
				if (sb.length() > 0) sb.append("\n");
				sb.append("(");
				sb.append(map.get(PINYIN_KEY));
				sb.append(")");
			}
			
			_tagText.setText(sb.toString());
		}
	}

	
	public void onTraceButtonClick(View view)
	{
		setCharacterTracePane();
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
		updateTags();
	}

	public void onAnimateButtonClick(View view) 
	{
		Log.i("CLICK", "DISPLAY");
		setDisplayPane();
	}
	
	Handler moveToNext = new Handler() {
		@Override
		public void handleMessage(Message m) {
			int index = _animator.getDisplayedChild();
			if (index + 1 < _characters.size()) {
				setSelectedCharacter(index + 1);
			} else {
				// this is the end of the word
				if (_lessonID != null) {
					if (_wordIndex < _collectionSize) { // still more words to come
						// shutdown and notify parent activity
						Bundle bundle = new Bundle();
						bundle.putInt("next", _wordIndex);
						Intent intent = new Intent();
						intent.putExtras(bundle);
						setResult(RESULT_OK, intent);
						finish();
					} else {
						// the last word in the collection
						Toolbox.showToast(getApplicationContext(),
						        "Reached the last word in " + _lessonName);
					}
				}
			}
		}
	};
}
