package edu.upenn.cis573.Trace2Win.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;
import edu.upenn.cis573.Trace2Win.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.Database.Lesson;
import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.Database.LessonItem;
import edu.upenn.cis573.Trace2Win.Database.LessonItem.ItemType;
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
    
    public void testAddCharacter() {
        LessonCharacter c = new LessonCharacter();
        db.addCharacter(c);
        LessonCharacterTest.compareCharacters(c, db.getCharacterById(c.getId()));
    }
    
    public void testAddWord() {
        LessonWord c = new LessonWord();
        db.addWord(c);
        LessonWordTest.compareWords(c, db.getWordById(c.getId()));
    }
    
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
    
    public void testSwapWordsInLesson() {
        LessonWord a = new LessonWord();
        LessonWord b = new LessonWord();
        db.addWord(a);
        db.addWord(b);
        Lesson lesson = new Lesson();
        lesson.addWord(a.getId());
        lesson.addWord(b.getId());
        db.addLesson(lesson);
        
        List<Long> exp1 = Arrays.asList(new Long[] {a.getId(), b.getId()});
        assertEquals(exp1, db.getWordsFromLessonId(lesson.getId()));
        assertEquals(exp1, lesson.getWordIds());
        
        db.swapWordsInLesson(lesson.getId(), a.getId(), b.getId());
        
        List<Long> exp2 = Arrays.asList(new Long[] {b.getId(), a.getId()});
        assertEquals(exp2, db.getWordsFromLessonId(lesson.getId()));
        
        Lesson lesson2 = db.getLessonById(lesson.getId());
        assertEquals(exp2, lesson2.getWordIds());
    }
    
    public void testSwapTagsCharacter() {
        LessonCharacter c = new LessonCharacter();
        c.addTag("1");
        c.addTag("2");
        db.addCharacter(c);
        
        List<String> exp1 = Arrays.asList(new String[] {"1", "2"});
        assertEquals(exp1, db.getCharacterTags(c.getId()));
        assertEquals(exp1, c.getTags());
        
        db.swapTags(DbAdapter.CHARTAG_TABLE, c.getId(), "1", "2");
        List<String> exp2 = Arrays.asList(new String[] {"2", "1"});
        assertEquals(exp2, db.getCharacterTags(c.getId()));
        
        LessonCharacter c1 = db.getCharacterById(c.getId());
        assertEquals(exp2, c1.getTags());
    }
    
    public void testSwapTagsWord() {
        LessonWord c = new LessonWord();
        c.addTag("1");
        c.addTag("2");
        db.addWord(c);
        
        List<String> exp1 = Arrays.asList(new String[] {"1", "2"});
        assertEquals(exp1, db.getWordTags(c.getId()));
        assertEquals(exp1, c.getTags());
        
        db.swapTags(DbAdapter.WORDTAG_TABLE, c.getId(), "1", "2");
        List<String> exp2 = Arrays.asList(new String[] {"2", "1"});
        assertEquals(exp2, db.getWordTags(c.getId()));
        
        LessonWord c1 = db.getWordById(c.getId());
        assertEquals(exp2, c1.getTags());
    }
    
    public void testSwapKeyValuesCharacter() {
        LessonCharacter c = new LessonCharacter();
        c.addKeyValue("k1", "v1");
        c.addKeyValue("k2", "v2");
        db.addCharacter(c);
        
        LinkedHashMap<String, String> exp1 = new LinkedHashMap<String, String>();
        exp1.put("k1", "v1");
        exp1.put("k2", "v2");
        assertEquals(exp1, db.getKeyValues(c.getId(), LessonItem.ItemType.CHARACTER));
        assertEquals(exp1, c.getKeyValues());
        
        db.swapKeyValues(DbAdapter.CHARKEYVALUES_TABLE, c.getId(), "k1", "k2");
        LinkedHashMap<String, String> exp2 = new LinkedHashMap<String, String>();
        exp2.put("k2", "v2");
        exp2.put("k1", "v1");
        assertEquals(exp2, db.getKeyValues(c.getId(), LessonItem.ItemType.CHARACTER));
        
        LessonCharacter c1 = db.getCharacterById(c.getId());
        assertEquals(exp2, c1.getKeyValues());
    }
    
    public void testSwapKeyValuesWord() {
        LessonWord c = new LessonWord();
        c.addKeyValue("k1", "v1");
        c.addKeyValue("k2", "v2");
        db.addWord(c);
        
        LinkedHashMap<String, String> exp1 = new LinkedHashMap<String, String>();
        exp1.put("k1", "v1");
        exp1.put("k2", "v2");
        assertEquals(exp1, db.getKeyValues(c.getId(), LessonItem.ItemType.WORD));
        assertEquals(exp1, c.getKeyValues());
        
        db.swapKeyValues(DbAdapter.WORDKEYVALUES_TABLE, c.getId(), "k1", "k2");
        LinkedHashMap<String, String> exp2 = new LinkedHashMap<String, String>();
        exp2.put("k2", "v2");
        exp2.put("k1", "v1");
        assertEquals(exp2, db.getKeyValues(c.getId(), LessonItem.ItemType.WORD));
        
        LessonWord c1 = db.getWordById(c.getId());
        assertEquals(exp2, c1.getKeyValues());
    }	

    public void testBrowseByTag(){
    	LessonCharacter a = new LessonCharacter();
    	LessonCharacter b = new LessonCharacter();
    	a.addTag("A");
    	b.addTag("B");
    	a.addTag("English");
    	a.addTag("Vowel");
    	b.addTag("English");
    	b.addTag("Consonant");
    	a.addKeyValue("Pronunciation", "a");
    	b.addKeyValue("Pronunciation", "batt");
    	db.addCharacter(a);
    	db.addCharacter(b);
    	
    	//Test Case: equivalence class: tag, 1 letter, character
    	Cursor result = db.browseByTag(ItemType.CHARACTER, "A");
    	assertEquals(1, result.getCount());
    	assertEquals(a.getId(), result.getLong(result.getColumnIndexOrThrow(DbAdapter.CHARTAG_ROWID)));
    	
    	//Test Case: equivalence class: tag, multiple character, character, case insensitive, partial match
    	result = db.browseByTag(ItemType.CHARACTER, "SON");
    	assertEquals(1, result.getCount());
    	assertEquals(b.getId(), result.getLong(result.getColumnIndexOrThrow(DbAdapter.CHARTAG_ROWID)));
    	
    	//Test Case: equivalence class: keyvalue, single character, character, case insensitive
    	result = db.browseByTag(ItemType.CHARACTER, "A");
    	assertEquals(1, result.getCount());
    	assertEquals(a.getId(), result.getLong(result.getColumnIndexOrThrow(DbAdapter.CHARTAG_ROWID)));
    	
    	//Test Case: equivalence class: keyvalue, multiple character, character, exact match
    	result = db.browseByTag(ItemType.CHARACTER, "batt");
    	assertEquals(1, result.getCount());
    	assertEquals(b.getId(), result.getLong(result.getColumnIndexOrThrow(DbAdapter.CHARTAG_ROWID)));
    	
    	//Need more tests for words
    	result.close();
    }
    
    public void testDeleteCharacter()
    {
    	LessonCharacter c = new LessonCharacter();
    	db.addCharacter(c);
    	db.deleteCharacter(c.getId());
    	assertNull(db.getCharacterById(c.getId()));
    }

    public void testDeleteWord()
    {
    	LessonWord w = new LessonWord();
    	db.addWord(w);
    	db.deleteWord(w.getId());
    	assertNull(db.getWordById(w.getId()));
    }
    
}
