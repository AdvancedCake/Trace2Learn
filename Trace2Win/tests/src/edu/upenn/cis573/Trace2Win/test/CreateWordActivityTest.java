package edu.upenn.cis573.Trace2Win.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import edu.upenn.cis573.Trace2Win.library.CreateWordActivity;
import edu.upenn.cis573.Trace2Win.library.LessonItemListAdapter;
import edu.upenn.cis573.Trace2Win.library.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.library.Database.LessonWord;


public class CreateWordActivityTest extends ActivityInstrumentationTestCase2<CreateWordActivity>{
	
	public CreateWordActivityTest(){
		super("edu.upenn.cis573.Trace2Win.library",CreateWordActivity.class);
	}
	
	private CreateWordActivity activity;
	private ListView list;
	
	
	public void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		list = (ListView)activity.findViewById(R.id.charslist);
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
