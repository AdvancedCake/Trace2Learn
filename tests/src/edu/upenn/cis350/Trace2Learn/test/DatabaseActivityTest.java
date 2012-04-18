package edu.upenn.cis350.Trace2Learn.test;

import edu.upenn.cis350.Trace2Learn.MainMenuActivity;
import edu.upenn.cis350.Trace2Learn.R;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import edu.upenn.cis350.Trace2Learn.Database.LessonWord;
import edu.upenn.cis350.Trace2Learn.Database.LessonCharacter;


public class DatabaseActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity>{
	
	public DatabaseActivityTest(){
		super("edu.upenn.cis350.Trace2Learn",MainMenuActivity.class);
	}
	
	private Activity activity;
	private ListView list;
	
	
	public void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		list = (ListView)activity.findViewById(R.id.charslist);
	}
	
}