package edu.upenn.cis573.Trace2Win.test;

import java.util.HashMap;

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
	public void testCreateWordKeyValues()
	{
		LessonWord w = new LessonWord();
		db.addWord(w);
		db.createKeyValue(w.getId(), w.getItemType(), "key1", "value1");
		w.addKeyValue("key1", "value1");
		LessonWord w1 = db.getWordById(w.getId());
		LessonWordTest.compareWords(w, w1);
	}		

	public void testCreateCharKeyValues()
	{
		LessonCharacter c = new LessonCharacter();
		db.addCharacter(c);
		db.createKeyValue(c.getId(), c.getItemType(), "key1", "value1");
		c.addKeyValue("key1", "value1");
		LessonCharacter c1 = db.getCharacterById(c.getId());
		LessonCharacterTest.compareCharacters(c, c1);
	}		
	
	public void testDeleteWordKeyValues()
	{
		LessonWord w = new LessonWord();
		db.addWord(w);
		db.createKeyValue(w.getId(), w.getItemType(), "key1", "value1");
		db.deleteKeyValue(w.getId(), w.getItemType(), "key1");
		LessonWord w1 = db.getWordById(w.getId());
		LessonWordTest.compareWords(w, w1);
	}		
	
	public void testDeleteCharKeyValues()
	{
		LessonCharacter c = new LessonCharacter();
		db.addCharacter(c);
		db.createKeyValue(c.getId(), c.getItemType(), "key1", "value1");
		db.deleteKeyValue(c.getId(), c.getItemType(), "key1");
		LessonCharacter c1 = db.getCharacterById(c.getId());
		LessonCharacterTest.compareCharacters(c, c1);
	}	
	
	public void testGetWordKeyValues()
	{
		LessonWord w = new LessonWord();
		db.addWord(w);
		db.createKeyValue(w.getId(), w.getItemType(), "key1", "value1");
		HashMap<String, String> keyValues = db.getKeyValues(w.getId(), w.getItemType());
		
		assertEquals(1, keyValues.size());
		assertTrue(keyValues.containsKey("key1"));
		assertTrue(keyValues.containsValue("value1"));
	}		
	
	public void testGetCharKeyValues()
	{
		LessonCharacter c = new LessonCharacter();
		db.addCharacter(c);
		db.createKeyValue(c.getId(), c.getItemType(), "key1", "value1");
		HashMap<String, String> keyValues = db.getKeyValues(c.getId(), c.getItemType());
		
		assertEquals(1, keyValues.size());
		assertTrue(keyValues.containsKey("key1"));
		assertTrue(keyValues.containsValue("value1"));
	}		
}
