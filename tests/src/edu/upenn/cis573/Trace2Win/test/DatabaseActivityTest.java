package edu.upenn.cis573.Trace2Win.test;

import edu.upenn.cis573.Trace2Win.MainMenuActivity;
import edu.upenn.cis573.Trace2Win.R;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import edu.upenn.cis573.Trace2Win.Database.LessonWord;
import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;


public class DatabaseActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity>{
	
	public DatabaseActivityTest(){
		super("edu.upenn.cis573.Trace2Win",MainMenuActivity.class);
	}
	
	private Activity activity;
	private ListView list;
	
	
	public void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		list = (ListView)activity.findViewById(R.id.charslist);
	}
	
}
