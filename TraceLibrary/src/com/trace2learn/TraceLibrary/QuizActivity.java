package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonCharacter;
import com.trace2learn.TraceLibrary.Database.LessonWord;

public class QuizActivity extends TraceBaseActivity {

	private int phraseIndex; // index of current phrase in collection
	private int quizLength;
	private int currentChar;

	private LessonWord quizWord;

	private TextView titleView;
	private TextView scoreView;
	private TextView countView;
	private LinearLayout thumbnails;
	private ImageView soundIcon;
	private ImageView nextIcon;

	private Button tellmeButton;
	private Button fiftyButton;
	private Spinner quizDifficulty;

	private List<TextView> choiceViewList;
	private int correctChoiceIndex = -1;
	private boolean rightAnswerSubmitted = false;
	private boolean wrongAnswerSubmitted = false;
	private boolean fiftyButtonClicked = false;

	private int lifetimeAttempted = 0;
	private float lifetimeScore = 0;
	private int sessionAttempted = 0;
	private float sessionScore = 0;

	private SharedPreferences prefs;
	private boolean isFullVersion;

	private SoundPool soundPool;
	private int soundId;

	private int thumbBg;
	private int thumbBgSelected;

	private enum Scroll {
		LEFT, RIGHT, NONE;
	}

	private static final int LEVEL_BASIC = 0;
	private static final int LEVEL_INTERMED = 1;
	private static final int LEVEL_ADVANCED = 2;

	private static List<String> basicWordIds = null;
	private static List<String> intermediateWordIds = null;
	private static List<String> allWordIds = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.quiz_main);

		prefs = getSharedPreferences(Toolbox.PREFS_FILE, MODE_PRIVATE);

		isFullVersion = prefs.getBoolean(Toolbox.PREFS_IS_FULL_VER, false);

		getViews();
		getHandlers();

		thumbBg = getResources().getColor(R.color.thumb_background);
		thumbBgSelected = getResources().getColor(
				R.color.thumb_background_selected);

		phraseIndex = 1;
		quizLength = 8;

		initializeWordLists();
		initializeQuizPage();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (soundPool != null)
			soundPool.release();
	};

	private void getViews() {
		titleView = (TextView) findViewById(R.id.phrase_title);
		scoreView = (TextView) findViewById(R.id.phrase_score);
		countView = (TextView) findViewById(R.id.phrase_count);
		tellmeButton = (Button) findViewById(R.id.tellme_button);
		fiftyButton = (Button) findViewById(R.id.fifty_button);
		thumbnails = (LinearLayout) findViewById(R.id.thumbnail_gallery);
		soundIcon = (ImageView) findViewById(R.id.sound_button);
		nextIcon = (ImageView) findViewById(R.id.go_next);

		choiceViewList = new ArrayList<TextView>();
		choiceViewList.add((TextView) findViewById(R.id.choice1_text));
		choiceViewList.add((TextView) findViewById(R.id.choice2_text));
		choiceViewList.add((TextView) findViewById(R.id.choice3_text));
		choiceViewList.add((TextView) findViewById(R.id.choice4_text));

		// set up difficulty 'spinner' - a.k.a. comb box
		quizDifficulty = (Spinner) findViewById(R.id.quiz_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.quiz_difficulty,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// apply the adapter to the spinner
		quizDifficulty.setAdapter(adapter);

		// initialize spinner value
		int difficulty = prefs.getInt(Toolbox.PREFS_QUIZ_DIFFICULTY,
				QuizActivity.LEVEL_INTERMED);
		quizDifficulty.setSelection(difficulty, /* animate */true);

	}

	private void getHandlers() {

		// Handlers for choice clicks
		choiceViewList.get(0).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handleChoiceClick((TextView) v, 0);
			}
		});
		choiceViewList.get(1).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handleChoiceClick((TextView) v, 1);
			}
		});
		choiceViewList.get(2).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handleChoiceClick((TextView) v, 2);
			}
		});
		choiceViewList.get(3).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handleChoiceClick((TextView) v, 3);
			}
		});

		// Trace Button
		tellmeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				revealAnswer();
			}
		});

		// Play Button
		fiftyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideTwoWrongChoices();
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
				moveToNextPhrase();
			}
		});

		quizDifficulty.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int pos,
					long id) {

				if (!isFullVersion && pos == QuizActivity.LEVEL_ADVANCED) {
					// restore previous selection, show Upgrade window
					int previous = prefs.getInt(Toolbox.PREFS_QUIZ_DIFFICULTY,
							QuizActivity.LEVEL_BASIC);
					quizDifficulty.setSelection(previous);
					Toolbox.promptAppUpgrade(v.getContext());
				} else {
					// change difficulty level & re-initialize the quiz, keeping
					// session score
					Editor ped = prefs.edit();
					ped.putInt(Toolbox.PREFS_QUIZ_DIFFICULTY, pos);
					ped.commit();
					initializeQuizPage();
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}

	private void initializeQuizPage() {
		// Bundle bun = getIntent().getExtras();
		Context context = getApplicationContext();
		Random gen = new Random();

		// retrieve preferred difficulty level - default to intermediate
		int difficulty = prefs.getInt(Toolbox.PREFS_QUIZ_DIFFICULTY,
				QuizActivity.LEVEL_INTERMED);

		// retrieve lifetime score
		lifetimeAttempted = prefs.getInt(Toolbox.PREFS_QUIZ_LIFETIME_ATTEMPTED,
				0);
		lifetimeScore = prefs.getFloat(Toolbox.PREFS_QUIZ_LIFETIME_CORRECT, 0);

		List<String> wordIds = basicWordIds;
		if (difficulty == QuizActivity.LEVEL_INTERMED)
			wordIds = intermediateWordIds;
		if (difficulty == QuizActivity.LEVEL_ADVANCED)
			wordIds = Toolbox.dba.getAllWordIds();
		int wordCount = wordIds.size();

		// pick a word at random
		int randomId = gen.nextInt(wordCount);
		quizWord = Toolbox.dba.getWordById(wordIds.get(randomId));
		int quizWordLength = quizWord.getCharacterIds().size();

		// randomly select 3 alternate answers
		List<LessonWord> choices = new ArrayList<LessonWord>();
		while (true) {
			randomId = gen.nextInt(wordCount);
			LessonWord choice = Toolbox.dba.getWordById(wordIds.get(randomId));
			if (choice.getStringId() != quizWord.getStringId()) {
				if ((choice.getCharacterIds().size() < 4 && quizWordLength < 4)
						|| (choice.getCharacterIds().size() > 3 && quizWordLength > 3)) {
					choices.add(choice);
				}
			}
			// break once 3 suitable answer choices had been found
			if (choices.size() >= 3)
				break;
		}

		// place the correct quiz answer in a random slot 0 through 3
		correctChoiceIndex = gen.nextInt(3);
		int insertAt = correctChoiceIndex;
		choiceViewList.get(insertAt).setText(
				"  " + quizWord.getTagsToString() + "  ");
		choiceViewList.get(insertAt).setBackgroundColor(Color.BLACK);
		insertAt = (insertAt + 1) % 4;
		choiceViewList.get(insertAt).setText(
				"  " + choices.get(0).getTagsToString() + "  ");
		choiceViewList.get(insertAt).setBackgroundColor(Color.BLACK);
		insertAt = (insertAt + 1) % 4;
		choiceViewList.get(insertAt).setText(
				"  " + choices.get(1).getTagsToString() + "  ");
		choiceViewList.get(insertAt).setBackgroundColor(Color.BLACK);
		insertAt = (insertAt + 1) % 4;
		choiceViewList.get(insertAt).setText(
				"  " + choices.get(2).getTagsToString() + " ");
		choiceViewList.get(insertAt).setBackgroundColor(Color.BLACK);

		titleView.setText(getResources().getString(R.string.quiz));
		scoreView.setText("");
		countView.setText(phraseIndex + " of " + quizLength);
		rightAnswerSubmitted = false;
		wrongAnswerSubmitted = false;
		fiftyButtonClicked = false;

		// initialize thumbnails list view
		setCharacterList(quizWord.getCharacterIds());
		setSelectedCharacter(currentChar, Scroll.NONE);
		// Sound
		soundIcon.setVisibility(View.GONE);
		int soundFile = 0;
		if (quizWord.hasKey(Toolbox.SOUND_KEY)
				|| quizWord.hasKey(Toolbox.PINYIN_KEY)) {
			soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
			// use 'sound' tag to locate audio if it exists, otherwise use
			// 'pinyin' tag
			String audioKey = quizWord.hasKey(Toolbox.SOUND_KEY) ? Toolbox.SOUND_KEY
					: Toolbox.PINYIN_KEY;
			// replace spaces with underscore
			String audioString = quizWord.getValue(audioKey);
			audioString = audioString.replace(' ', '_');
			soundFile = getResources().getIdentifier(audioString, "raw",
					getPackageName());
			if (soundFile != 0) { // check to make sure resource exists
				soundId = soundPool.load(context, soundFile, 1);
				soundIcon.setVisibility(View.VISIBLE);
			}
		}

	}

	private void initializeWordLists() {

		// set up word lists from which quizzes will be generated

		if (allWordIds == null) {
			allWordIds = Toolbox.dba.getAllWordIds();
			basicWordIds = new ArrayList<String>();
			intermediateWordIds = new ArrayList<String>();
			// iterate through all (admin) collections, and assign each set
			// of characters
			// to the Basic (1 thru 9) or Intermediate (1 thru 24) word
			// lists
			int maxBasicIndex = 9;
			int maxIntermediateIndex = 24;
			for (String lid : Toolbox.dba.getAllLessonIds()) {
				Lesson lesson = Toolbox.dba.getLessonById(lid);
				if (!lesson.isUserDefined()) {
					try {
						String[] tokens = lesson.getLessonName().split(":");
						int index = Integer.parseInt(tokens[0]);
						if (index <= maxBasicIndex)
							basicWordIds.addAll(lesson.getWordIds());
						if (index <= maxIntermediateIndex)
							intermediateWordIds.addAll(lesson.getWordIds());
					} catch (Exception e) {/* ignore */
					}
				}
			}
		}

	}

	private void handleChoiceClick(TextView v, int choiceIndex) {

		if (rightAnswerSubmitted)
			return;

		// increment attempted count, will factor into score
		if (!wrongAnswerSubmitted)
			sessionAttempted++;

		// set answer to GREEN or RED - make it flicker for effect
		if (choiceIndex == correctChoiceIndex) {
			v.setBackgroundColor(Color.GREEN);
			rightAnswerSubmitted = true;
			if (!wrongAnswerSubmitted) {
				if (fiftyButtonClicked) {
					scoreView.setText("1/2 point");
					sessionScore += 0.5;
				} else {
					scoreView.setText("1 point");
					sessionScore += 1.0;
				}
			}
		} else {
			v.setBackgroundColor(Color.RED);
			wrongAnswerSubmitted = true;
			scoreView.setText("0 point");
		}

	}

	private void moveToNextPhrase() {

		// increment attempted count, if user clicked 'next' without making an
		// answer choice
		if (!wrongAnswerSubmitted && !rightAnswerSubmitted)
			sessionAttempted++;

		if (phraseIndex < quizLength) {

			Vibrator v = (Vibrator) getApplicationContext().getSystemService(
					Service.VIBRATOR_SERVICE);
			v.vibrate(300); // milliseconds

			// not at end of collection yet
			phraseIndex++;
			initializeQuizPage();

		} else {

			lifetimeAttempted += sessionAttempted;
			lifetimeScore += sessionScore;
			showScore();

			Editor ed = prefs.edit();
			ed.putInt(Toolbox.PREFS_QUIZ_LIFETIME_ATTEMPTED, lifetimeAttempted);
			ed.putFloat(Toolbox.PREFS_QUIZ_LIFETIME_CORRECT, lifetimeScore);
			ed.commit();

		}

	}

	private void setSelectedCharacter(int position, Scroll scrollage) {
		// charSlot.removeAllViews();
		thumbnails.getChildAt(currentChar).setBackgroundColor(thumbBg);

		currentChar = position;

		// set selected state, make sure it's visible in the thumbnail gallery
		thumbnails.getChildAt(currentChar).setBackgroundColor(thumbBgSelected);

	}

	private void setCharacterList(List<String> ids) {
		Context context = getApplicationContext();
		thumbnails.removeAllViews();
		int index = 0;
		for (String id : ids) {
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
			thumbnails.addView(iv);

		}
	}

	public void setContentView(View view) {
		super.setContentView(view);
	}

	private void playSound() {
		// determine appropriate volume level from system voice call settings
		AudioManager audio = (AudioManager) getSystemService(AUDIO_SERVICE);
		float volumeMax = audio.getStreamMaxVolume(AudioManager.STREAM_RING);
		float volumeSet = audio.getStreamVolume(AudioManager.STREAM_RING);
		float volume = (volumeMax == 0) ? 0 : volumeSet / volumeMax;
		soundPool.play(soundId, volume, volume, 1, 0, 1);
	}

	private void hideTwoWrongChoices() {

		// hide the two choices to the left & right of the correct answer
		int hideIndex = (correctChoiceIndex + 1) % 4;
		choiceViewList.get(hideIndex).setText("");
		hideIndex = (correctChoiceIndex + 3) % 4;
		choiceViewList.get(hideIndex).setText("");

		// remember that the user clicked 50-50, this has an impact on score
		fiftyButtonClicked = true;
	}

	private void revealAnswer() {

		if (rightAnswerSubmitted)
			return;
		rightAnswerSubmitted = true;

		TextView answer = choiceViewList.get(correctChoiceIndex);
		answer.setBackgroundColor(Color.GREEN);
		scoreView.setText("0 point");
	}

	private void showScore() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		PackageManager pm = getPackageManager();
		builder.setIcon(getApplicationInfo().loadIcon(pm));
		builder.setTitle(getApplicationInfo().loadLabel(pm));

		// calculate % scores, be careful not to devide by zero
		int sessionPct = (sessionAttempted == 0) ? 0
				: (int) (100 * sessionScore / sessionAttempted);
		int lifetimePct = (lifetimeAttempted == 0) ? 0
				: (int) (100 * lifetimeScore / lifetimeAttempted);
		QuizActivity.setQuizDialogMsg(builder, lifetimePct, sessionPct,
				sessionScore, sessionAttempted);

		// builder.setNeutralButton("Clear Score", null);
		builder.setPositiveButton("Next Quiz",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// new quiz with new counter
						phraseIndex = 1;
						initializeQuizPage();
					}
				});

		builder.setNegativeButton("Exit",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// finish activity and kick back to previous activity
						finish();
					}
				});
		builder.show();

	}

	private static void setQuizDialogMsg(AlertDialog.Builder builder,
			int lifetimePct, int sessionPct, float sessionAttempt,
			int sessionScore) {
		String msg = "Lifetime Score: " + lifetimePct + "%";
		msg += "\n" + "Current: " + sessionAttempt + " of " + sessionScore
				+ ", " + sessionPct + "%";
		builder.setMessage(msg);
	}

}
