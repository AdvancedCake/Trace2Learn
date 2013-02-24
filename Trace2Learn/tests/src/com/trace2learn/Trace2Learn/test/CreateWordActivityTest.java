package com.trace2learn.Trace2Learn.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.trace2learn.TraceLibrary.CreateWordActivity;
import com.trace2learn.TraceLibrary.LessonItemListAdapter;
import com.trace2learn.TraceLibrary.Database.LessonCharacter;
import com.trace2learn.TraceLibrary.Database.LessonWord;


public class CreateWordActivityTest extends ActivityInstrumentationTestCase2<CreateWordActivity>{
	
	public CreateWordActivityTest(){
		super("com.trace2learn.Trace2Learn.TraceLibrary",CreateWordActivity.class);
	}
	
	private CreateWordActivity activity;
	private ListView list;
	
	
	public void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		list = (ListView)activity.findViewById(R.id.charList);
	}
	
	public void testOneChar(){
		activity.runOnUiThread(new Runnable() {
			public void run(){
				// ensure there is at least one character
				LessonItemListAdapter lessonItemListAdapter = (LessonItemListAdapter)list.getAdapter();
				lessonItemListAdapter.add(new LessonCharacter());
				
				list.performItemClick(null, 0, lessonItemListAdapter.getItemId(0));
			}
		});
		
		getInstrumentation().waitForIdleSync();
		
		LessonWord word = activity.getWord();
		assertEquals(word.getCharacterId(0),((LessonCharacter)list.getItemAtPosition(0)).getStringId());
	}
}
