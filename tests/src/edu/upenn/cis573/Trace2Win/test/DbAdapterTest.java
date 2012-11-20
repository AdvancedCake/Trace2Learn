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

    public void testSwapCharacters() {
        LessonCharacter a = new LessonCharacter();
        LessonCharacter b = new LessonCharacter();
        db.addCharacter(a);
        db.addCharacter(b);
        double aOrigSort = a.getSort();
        double bOrigSort = b.getSort();
        assertFalse(aOrigSort == bOrigSort);
        
        assertTrue(db.swapCharacters(a.getId(), aOrigSort,
                                     b.getId(), bOrigSort));
        
        LessonCharacter a1 = db.getCharacterById(a.getId());
        LessonCharacter b1 = db.getCharacterById(b.getId());
        a.setSort(bOrigSort);
        b.setSort(aOrigSort);
        
        LessonCharacterTest.compareCharacters(a, a1);
        LessonCharacterTest.compareCharacters(b, b1);
    }
    
    public void testSwapWords() {
        LessonWord a = new LessonWord();
        LessonWord b = new LessonWord();
        db.addWord(a);
        db.addWord(b);
        double aOrigSort = a.getSort();
        double bOrigSort = b.getSort();
        assertFalse(aOrigSort == bOrigSort);
        
        assertTrue(db.swapWords(a.getId(), aOrigSort,
                                b.getId(), bOrigSort));
        
        LessonWord a1 = db.getWordById(a.getId());
        LessonWord b1 = db.getWordById(b.getId());
        a.setSort(bOrigSort);
        b.setSort(aOrigSort);
        
        LessonWordTest.compareWords(a, a1);
        LessonWordTest.compareWords(b, b1);
    }

}
