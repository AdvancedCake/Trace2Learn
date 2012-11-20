package edu.upenn.cis573.Trace2Win.test;

import android.test.AndroidTestCase;
import android.util.Log;
import edu.upenn.cis573.Trace2Win.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.Database.LessonWord;

public class DbAdapterTest extends AndroidTestCase {

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

    protected void dumpDBs()
    {
        for(String str : this.getContext().databaseList())
        {
            Log.i("DELETE", str);
            this.getContext().deleteDatabase(str);
        }
    }


    // TESTS
	public void testCreateKeyValues()
	{
		LessonWord w = new LessonWord();
		db.addWord(w);
		db.createKeyValue(w.getId(), w.getItemType(), "key1", "value1");
		w.addKeyValue("key1", "value1");
		LessonWord w1 = db.getWordById(w.getId());
		LessonWordTest.compareWords(w, w1);
	}		

}
