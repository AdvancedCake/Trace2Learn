package edu.upenn.cis573.Trace2Win.test;

import android.test.AndroidTestCase;
import android.util.Log;
import edu.upenn.cis573.Trace2Win.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.Database.Lesson;
import edu.upenn.cis573.Trace2Win.Database.LessonWord;
import edu.upenn.cis573.Trace2Win.Database.Stroke;

public class LessonTest extends AndroidTestCase {
	
	Stroke s1, s2, s3;

	protected void dumpDBs()
	{
		for(String str : this.getContext().databaseList())
		{
			Log.i("DELETE", str);
			this.getContext().deleteDatabase(str);
		}
	}
	
	DbAdapter db;
	protected void setUp() throws Exception {
		super.setUp();
		dumpDBs();
		db = new DbAdapter(this.getContext());
		db.open();
	}

	protected void tearDown()
	{
		dumpDBs();
	}

	
	public void testToXml() {
		LessonWord word1 = new LessonWord(900);
		word1.addTag("English");
		word1.addTag("Color");
		word1.addKeyValue("Word", "Red");
		word1.addCharacter((long) 1234);
		word1.addCharacter((long) 1234);
		db.addWord(word1);
		
		LessonWord word1_db = db.getWordById(900);
		LessonWordTest.compareWords(word1, word1_db);		
		
		LessonWord word2 = new LessonWord(901);
		word2.addTag("English");
		word2.addTag("Color");
		word2.addKeyValue("Word", "Blue");
		word2.addCharacter((long) 1235);
		word2.addCharacter((long) 1234);
		db.addWord(word2);		
		
		
	    Lesson lesson = new Lesson();
	    lesson.setStringId("unique_lesson_id");
	    lesson.setName("Colors");
	    lesson.addWord((long) word1.getId());
	    lesson.addWord((long) word2.getId());
	    db.addLesson(lesson);
        
        String exp = "<lesson id=\"unique_lesson_id\" name=\"Colors\">\n" +
        		"<word id=\"900\" position=\"0\">\n" +
        		"<tag tag=\"English\" />\n" +
        		"<tag tag=\"Color\" />\n" +
        		"<id key=\"Word\" value=\"Red\" />\n" +
        		"<character id=\"1234\" position=\"0\" />\n" +
        		"<character id=\"1234\" position=\"1\" />\n" +
        		"</word>\n" +
        		"<word id=\"901\" position=\"1\">\n" +
        		"<tag tag=\"English\" />\n" +
        		"<tag tag=\"Color\" />\n" +
        		"<id key=\"Word\" value=\"Blue\" />\n" +
        		"<character id=\"1235\" position=\"0\" />\n" +
        		"<character id=\"1234\" position=\"1\" />\n" +
        		"</word>\n" +
        		"</lesson>\n";
        
        assertEquals(exp, lesson.toXml());
	}
}
