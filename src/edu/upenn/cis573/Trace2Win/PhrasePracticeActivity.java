package edu.upenn.cis573.Trace2Win;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import edu.upenn.cis573.Trace2Win.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.Database.LessonWord;
import edu.upenn.cis573.Trace2Win.R.id;

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

	private long _wordId = -1; // TODO what is this for? please document
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

		_tagText = (TextView) findViewById(id.tag_list);
		_phraseTitle = (TextView) findViewById(id.phraseTitle);

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
		if (bun != null && bun.containsKey("wordId")) 
		{
			_wordId = bun.getLong("wordId");
			_word = _dbHelper.getWordById(_wordId);
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
				showToast(_lessonName + " - " + _wordIndex + 
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
			// Yes this is bad form
			// Dont have time to figure out better error handling
			// Should reach here anyway basically just an assert
			throw new NullPointerException("Did not recieve wordId");
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

	private void setCharacterList(List<Long> ids)
	{
		_characters.clear();
		_bitmaps.clear();
		_tracePanes.clear();
		_playbackPanes.clear();
		_traceLayouts.clear();
		_displayLayouts.clear();
		for(long id : ids)
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
			//setCharacter(this._characters.get(curInd));
			_currentMode = Mode.DISPLAY;
		}
	}

	/**
	 * Switches the display mode to display
	 */
	private synchronized void setCharacterTracePane()
	{
		if (_currentMode != Mode.TRACE) 
		{
			int curInd = _animator.getDisplayedChild();
			_animator.removeAllViews();
			for(SquareLayout trace : this._traceLayouts)
			{
				_animator.addView(trace);
			}
			_animator.setDisplayedChild(curInd);
			//setCharacter(this._characters.get(curInd));
			_currentMode = Mode.TRACE;
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
			sb.append(_word.getKeyValuesToString());
			if (_word.getKeyValues().size() > 0 && _word.getTags().size() > 0) {
				sb.append(", ");
			}
			sb.append(_word.getTagsToString());
			
			_tagText.setText(sb.toString());
		} 
	}

	public void onClearButtonClick(View view)
	{
		int child = _animator.getDisplayedChild();
		this._tracePanes.get(child).clearPane();
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
	
	public void showToast(String msg){
		Context context = getApplicationContext();
		CharSequence text = msg;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
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
						showToast("Reached the last word in " + _lessonName);
					}
				}
			}
		}
	};
}
