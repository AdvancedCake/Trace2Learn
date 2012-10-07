package edu.upenn.cis350.Trace2Learn.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import edu.upenn.cis350.Trace2Learn.CreateWordActivity;
import edu.upenn.cis350.Trace2Learn.R;
import edu.upenn.cis350.Trace2Learn.Database.LessonCharacter;
import edu.upenn.cis350.Trace2Learn.Database.LessonWord;


public class CreateWordActivityTest extends ActivityInstrumentationTestCase2<CreateWordActivity>{
	
	public CreateWordActivityTest(){
		super("edu.upenn.cis350.Trace2Learn",CreateWordActivity.class);
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
				list.performItemClick(null, 0, list.getAdapter().getItemId(0));
			}
		});
		
		getInstrumentation().waitForIdleSync();
		
		LessonWord word = activity.getWord();
		assertEquals(word.getCharacterId(0),((LessonCharacter)list.getItemAtPosition(0)).getId());
	}
}