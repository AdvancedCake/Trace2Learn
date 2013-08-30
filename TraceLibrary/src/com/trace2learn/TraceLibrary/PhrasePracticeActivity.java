package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.trace2learn.TraceLibrary.Database.LessonCharacter;
import com.trace2learn.TraceLibrary.Database.LessonWord;

public class PhrasePracticeActivity extends TraceBaseActivity {

    private Mode    currentMode = Mode.VOID;
    private String  lessonID = null;
    private int     phraseIndex; // index of current phrase in collection
    private int     collectionSize;
    private String  lessonName;
    private boolean quizMode;
    private int     currentChar;

    private LessonWord                 word;
    private ArrayList<LessonCharacter> characters;

    private TextView			tagView;
    private TextView            countView;
    private TextView			titleView;
    private Button				playbackButton;
    private Button				traceButton;
    private ToggleButton		quizToggle;
    private ImageView			quizIcon;
    private LinearLayout		thumbnails;
    private HorizontalScrollView thumbscroll;
    private FrameLayout  		charSlot;
    private ImageView    		soundIcon;
    private ImageView   		 prevIcon;
    private ImageView    nextIcon;

    private ArrayList<CharacterPlaybackPane> playbackPanes;
    private ArrayList<CharacterTracePane>    tracePanes;
    
    private SharedPreferences prefs;
    
    private SoundPool soundPool;
    private int       soundId;
    
    private int thumbBg;
    private int thumbBgSelected;

    private enum Mode {
        DISPLAY, TRACE, VOID;
    }
    
    private enum Scroll {
    	LEFT, RIGHT, NONE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.practice_phrase);

        getViews();
        getHandlers();

        characters = new ArrayList<LessonCharacter>();

        playbackPanes  = new ArrayList<CharacterPlaybackPane>();
        tracePanes     = new ArrayList<CharacterTracePane>();
        
        prefs = getSharedPreferences(Toolbox.PREFS_FILE, MODE_PRIVATE);
        
        thumbBg         = getResources().getColor(R.color.thumb_background);
        thumbBgSelected = getResources().getColor(R.color.thumb_background_selected);

        initializeMode();
        
        // set first char as current, will be picked up in onStart()
        currentChar = 0;
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
        setSelectedCharacter(currentChar, Scroll.NONE);  
        //try { Thread.sleep(500); } catch (InterruptedException e) {}
        //setSelectedCharacter(currentChar, Scroll.NONE);  
        
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Toolbox.PREFS_QUIZ_MODE, quizMode);
        editor.commit();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null)
            soundPool.release();
    };

    private void getViews() {
        titleView   = (TextView)     findViewById(R.id.phrase_title);
        countView  = (TextView)      findViewById(R.id.phrase_count);
        tagView     = (TextView)     findViewById(R.id.tag_list);
        playbackButton  = (Button)       findViewById(R.id.animate_button);
        traceButton = (Button)       findViewById(R.id.trace_button);
        quizToggle  = (ToggleButton) findViewById(R.id.quiz_toggle);
        quizIcon    = (ImageView)    findViewById(R.id.quiz_icon);
        charSlot    = (FrameLayout)  findViewById(R.id.character_slot);
        thumbnails  = (LinearLayout) findViewById(R.id.thumbnail_gallery);
        thumbscroll = (HorizontalScrollView) findViewById(R.id.thumbnail_gallery_scroll_view);
        soundIcon   = (ImageView)    findViewById(R.id.sound_button);
        prevIcon	= (ImageView)    findViewById(R.id.go_prev);
        nextIcon	= (ImageView)    findViewById(R.id.go_next);
    }

    private void getHandlers() {
        // Trace Button
        traceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCharacterTracePane(false);
            }
        });

        // Play Button
        playbackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setDisplayPane(false);
            }
        });
        
        // Quiz Mode Toggle
        quizToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setQuizMode(!quizMode);
            }
        });

        // Clicking on the tags while in quiz mode
        tagView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTagView(false);
            }
        });
        
        // Clicking on the quiz icon while in quiz mode
        quizIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTagView(true);
            }
        });

        // Clicking on the sound icon
        soundIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
            }
        });
        
        // Clicking next icon
        nextIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNextPhrase(/*swipe*/ false);
            }
        });    
        
        // Clicking previous icon
        prevIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPrevPhrase(/*swipe*/ false);
            }
        });
        
        // handle swipe left/right gestures
        charSlot.setOnTouchListener(new OnSwipeTouchListener(this.getApplicationContext()) {
            public void onSwipeRight() {
            	// L>R swipe, proceed to previous char or next phrase
            	if (currentMode == Mode.DISPLAY) {
            		if (currentChar > 0) 
            			setSelectedCharacter(currentChar - 1, Scroll.LEFT);
            		else
            			moveToPrevPhrase(/*swipe*/ true);
            	}
            }
            public void onSwipeLeft() {
            	// R>L swipe, proceed to next char or next phrase
            	if (currentMode == Mode.DISPLAY) {
            		if (currentChar + 1 < characters.size()) 
            			setSelectedCharacter(currentChar + 1, Scroll.RIGHT);
            		else
            			moveToNextPhrase(/*swipe*/ true);
            	}
            }

        });
        
        
    }
    
    private void moveToNextPhrase(boolean swipe) {
        if (phraseIndex < collectionSize) { 
        	// not at end of collection yet
            Bundle bundle = new Bundle();
            bundle.putInt("next", phraseIndex);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
            
            Vibrator v = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
            v.vibrate(300); //milliseconds
        }
        else if(!swipe) {
        	// if at end of collection, return to browse collections
        	// but only if action is not a result of a swipe gesture
        	Intent i = new Intent(this, BrowseLessonsActivity.class);
        	startActivity(i);
        }
        	
    }
    
    private void moveToPrevPhrase(boolean swipe) {
        if (phraseIndex > 1) { 
        	// not at beginning of collection yet
            Bundle bundle = new Bundle();
            bundle.putInt("next", phraseIndex - 2);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
            
            Vibrator v = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
            v.vibrate(300); //milliseconds
        }
    }    
    /**
     * Initialize the display mode, if the activity was started with intent to
     * display a character, that character should be displayed
     */
    private void initializeMode() {
        Bundle bun      = getIntent().getExtras();
        Context context = getApplicationContext();
        if (bun != null && bun.containsKey("wordId")) {
            characters.clear();
            tracePanes.clear();
            playbackPanes.clear();
            
            String wordId = bun.getString("wordId");
            word = Toolbox.dba.getWordById(wordId);
            setCharacterList(word.getCharacterIds());
            updateTags();

            lessonID = bun.getString("lessonID");
            if (lessonID == null) {
                titleView.setText("");
                countView.setText("");
            } else {
                phraseIndex = bun.getInt("index");
                collectionSize = bun.getInt("collectionSize");
                lessonName = Toolbox.dba.getLessonById(lessonID).getLessonName();
                titleView.setText(lessonName);
                countView.setText(phraseIndex + " of " + collectionSize);
            }

            // Activity Mode
            String mode = prefs.getString(Toolbox.PREFS_PHRASE_MODE, "trace"); 
            if (mode.equals("display")) {
                currentMode = Mode.DISPLAY;
            } else if (mode.equals("trace")) {
                currentMode = Mode.TRACE;
            }
            
            // Quiz Mode
            quizMode = prefs.getBoolean(Toolbox.PREFS_QUIZ_MODE, true);
            setQuizMode(quizMode);
            
            // Sound
            soundIcon.setVisibility(View.GONE);            
            int soundFile = 0;
            if (word.hasKey(Toolbox.SOUND_KEY) || word.hasKey(Toolbox.PINYIN_KEY)) {
                soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
                // use 'sound' tag to locate audio if it exists, otherwise use 'pinyin' tag
                String audioKey = word.hasKey(Toolbox.SOUND_KEY) ? Toolbox.SOUND_KEY : Toolbox.PINYIN_KEY;
	            // replace spaces with underscore
	            String audioString = word.getValue(audioKey);
	            audioString = audioString.replace(' ','_');
	            soundFile = getResources().getIdentifier(
                        audioString, "raw", getPackageName());
	            if (soundFile != 0) { // check to make sure resource exists
	                soundId = soundPool.load(getApplicationContext(), soundFile, 1);
	                soundIcon.setVisibility(View.VISIBLE);
	            }
            }
        } else {
            Toolbox.showToast(context, "No word selected");
            finish();
        }
    }

    private void setSelectedCharacter(int position, Scroll scrollage) {
        //charSlot.removeAllViews();
        thumbnails.getChildAt(currentChar).setBackgroundColor(thumbBg);

        currentChar = position;
        if (currentMode == Mode.DISPLAY) {
        //    playbackPanes.get(currentChar).setAnimated(true);
        //    charSlot.addView(playbackPanes.get(currentChar));
        	setDisplayPane(true);
        } else if (currentMode == Mode.TRACE) {
        //    tracePanes.get(currentChar).clearPane();
        //    charSlot.addView(tracePanes.get(currentChar));
        	setCharacterTracePane(true);
        }

        // set selected state, make sure it's visible in the thumbnail gallery
        thumbnails.getChildAt(currentChar).setBackgroundColor(thumbBgSelected);
       
        // scroll the gallery, if necessary, to ensure that new selection is visible
        int thumbWidth = thumbnails.getChildAt(currentChar).getWidth();
        int selectionLeftEdge = thumbWidth * currentChar;
        int selectionRightEdge = thumbWidth * (currentChar + 1);
        int visibleLeftEdge = thumbscroll.getScrollX();
        int visibleRightEdge = visibleLeftEdge = thumbscroll.getWidth();
        		
        if(scrollage == Scroll.LEFT &&
        		selectionLeftEdge < visibleLeftEdge)
        	thumbscroll.scrollBy(thumbWidth * (-1), 0);
        if(scrollage == Scroll.RIGHT &&
        		selectionRightEdge > visibleRightEdge)
        	thumbscroll.scrollBy(thumbWidth, 0);

    }


    private void setCharacterList(List<String> ids) {
        Context context = getApplicationContext();
        int index = 0;
        for(String id : ids) {
            LessonCharacter ch = Toolbox.dba.getCharacterById(id);
            ImageView iv = new ImageView(context);
            iv.setBackgroundColor(thumbBg);
            iv.setImageBitmap(BitmapFactory.buildBitmap(ch));
            final int i = index;
            index++;
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectedCharacter(i, Scroll.NONE);
                }
            });
            this.characters.add(ch);
            this.thumbnails.addView(iv);
            
            // duration of rendering is dynamic based on # of strokes, 2 strokes per second
            int duration = ch.getNumStrokes() / 2 + 1;
            
            CharacterPlaybackPane dispPane = new CharacterPlaybackPane(
                    context, false, duration);
            dispPane.setCharacter(ch);
            this.playbackPanes.add(dispPane);

            CharacterTracePane tracePane = new CharacterTracePane(context);
            
            tracePane.setTemplate(ch);
            tracePane.addMoveToNextHandler(moveToNext);
            this.tracePanes.add(tracePane);
        }
    }

    /**
     * Switches the display mode to display
     */
    private synchronized void setDisplayPane(boolean forceRedraw) {
        playbackPanes.get(currentChar).setAnimated(true);

        if (currentMode != Mode.DISPLAY  || forceRedraw) {
            currentMode = Mode.DISPLAY;
            charSlot.removeAllViews();
            charSlot.addView(playbackPanes.get(currentChar));
            
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Toolbox.PREFS_PHRASE_MODE, "display");
            editor.commit();
            
            playbackButton.setText(Html.fromHtml("<b>" +
                    getString(R.string.animate) + "</b>"));
            traceButton.setText(getString(R.string.practice));
        }
    }

    /**
     * Switches the display mode to display
     */
    private synchronized void setCharacterTracePane(boolean forceRedraw) {
        this.tracePanes.get(currentChar).clearPane();

        if (currentMode != Mode.TRACE  || forceRedraw) {
            currentMode = Mode.TRACE;
            charSlot.removeAllViews();
            charSlot.addView(tracePanes.get(currentChar));
            
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Toolbox.PREFS_PHRASE_MODE, "trace");
            editor.commit();

            
            traceButton.setText(Html.fromHtml("<b>" +
                    getString(R.string.practice) + "</b>"));
            playbackButton.setText(getString(R.string.animate));
        }
    }

    public void setContentView(View view) {
        super.setContentView(view);
    }

    private void updateTags() {
        if (word != null) {
            StringBuilder sb = new StringBuilder();
            // display any tags
            sb.append(word.getTagsToString());

            // display the pinyin value, if it exists
            if (word.hasKey(Toolbox.PINYIN_KEY)) {
                if (sb.length() > 0) sb.append("\n");
                sb.append("(");
                sb.append(word.getValue(Toolbox.PINYIN_KEY));
                sb.append(")");
            }

            tagView.setText(sb.toString());
        }
    }
    
    private void setQuizMode(boolean state) {
        if (state) {
            quizMode = true;
            tagView.setClickable(true);
            tagView.setVisibility(View.INVISIBLE);
            quizIcon.setVisibility(View.VISIBLE);
        } else {
            quizMode = false;
            tagView.setClickable(false);
            tagView.setVisibility(View.VISIBLE);
            quizIcon.setVisibility(View.INVISIBLE);
        }
        
        quizToggle.setChecked(quizMode);
    }
    
    private void toggleTagView(boolean show) {
        if (!quizMode) { return; }
        
        if (show) {
            tagView.setVisibility(View.VISIBLE);
            quizIcon.setVisibility(View.INVISIBLE);
        } else {
            tagView.setVisibility(View.INVISIBLE);
            quizIcon.setVisibility(View.VISIBLE);
        }
    }
    
    private void playSound() {
    	// determine appropriate volume level from system voice call settings
        AudioManager audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        float volumeMax = audio.getStreamMaxVolume(AudioManager.STREAM_RING);
        float volumeSet = audio.getStreamVolume(AudioManager.STREAM_RING);
        float volume = (volumeMax == 0)? 0 : volumeSet / volumeMax;        
        soundPool.play(soundId, volume, volume, 1, 0, 1);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        updateTags();
    }

    Handler moveToNext = new Handler() {
        @Override
        public void handleMessage(Message m) {
            if (currentChar + 1 < characters.size()) {
                setSelectedCharacter(currentChar + 1, Scroll.NONE);
            } else {
                // this is the end of the word
                if (lessonID != null) {
                    if (phraseIndex < collectionSize) { // still more words
                       
                    } else if (phraseIndex == collectionSize) {
                        // the last word in the collection
                        Toolbox.showToast(getApplicationContext(),
                                "Completed " + lessonName);
                    }
                }
            }
        }
    };
}
