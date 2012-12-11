package edu.upenn.cis573.Trace2Win.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;
import edu.upenn.cis573.Trace2Win.library.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.library.Database.Lesson;
import edu.upenn.cis573.Trace2Win.library.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.library.Database.LessonItem;
import edu.upenn.cis573.Trace2Win.library.Database.LessonItem.ItemType;
import edu.upenn.cis573.Trace2Win.library.Database.LessonWord;

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
        db.close();
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
        LessonCharacterTest.compareCharacters(c, db.getCharacterById(c.getStringId()));
    }
    
    public void testAddWord() {
        LessonWord c = new LessonWord();
        db.addWord(c);
        LessonWordTest.compareWords(c, db.getWordById(c.getStringId()));
        LessonWord d = new LessonWord();
        db.addWord(d);
        LessonWordTest.compareWords(c, db.getWordById(c.getStringId()));
        LessonWordTest.compareWords(d, db.getWordById(d.getStringId()));
    }
    
	public void testCreateWordKeyValues()
	{
		LessonWord w = new LessonWord();
		db.addWord(w);
		db.createKeyValue(w.getStringId(), w.getItemType(), "key1", "value1");
		w.addKeyValue("key1", "value1");
		LessonWord w1 = db.getWordById(w.getStringId());
		LessonWordTest.compareWords(w, w1);
	}		

	public void testCreateCharKeyValues()
	{
		LessonCharacter c = new LessonCharacter();
		db.addCharacter(c);
		db.createKeyValue(c.getStringId(), c.getItemType(), "key1", "value1");
		c.addKeyValue("key1", "value1");
		LessonCharacter c1 = db.getCharacterById(c.getStringId());
		LessonCharacterTest.compareCharacters(c, c1);
	}		
	
	public void testDeleteWordKeyValues()
	{
		LessonWord w = new LessonWord();
		db.addWord(w);
		db.createKeyValue(w.getStringId(), w.getItemType(), "key1", "value1");
		db.deleteKeyValue(w.getStringId(), w.getItemType(), "key1");
		LessonWord w1 = db.getWordById(w.getStringId());
		LessonWordTest.compareWords(w, w1);
	}		
	
	public void testDeleteCharKeyValues()
	{
		LessonCharacter c = new LessonCharacter();
		db.addCharacter(c);
		db.createKeyValue(c.getStringId(), c.getItemType(), "key1", "value1");
		db.deleteKeyValue(c.getStringId(), c.getItemType(), "key1");
		LessonCharacter c1 = db.getCharacterById(c.getStringId());
		LessonCharacterTest.compareCharacters(c, c1);
	}	
	
	public void testGetWordKeyValues()
	{
		LessonWord w = new LessonWord();
		db.addWord(w);
		db.createKeyValue(w.getStringId(), w.getItemType(), "key1", "value1");
		HashMap<String, String> keyValues = db.getKeyValues(w.getStringId(), w.getItemType());
		
		assertEquals(1, keyValues.size());
		assertTrue(keyValues.containsKey("key1"));
		assertTrue(keyValues.containsValue("value1"));
	}		
	
	public void testGetCharKeyValues()
	{
		LessonCharacter c = new LessonCharacter();
		db.addCharacter(c);
		db.createKeyValue(c.getStringId(), c.getItemType(), "key1", "value1");
		HashMap<String, String> keyValues = db.getKeyValues(c.getStringId(), c.getItemType());
		
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
        
        assertTrue(db.swapCharacters(a.getStringId(), aOrigSort,
                                     b.getStringId(), bOrigSort));
        
        LessonCharacter a1 = db.getCharacterById(a.getStringId());
        LessonCharacter b1 = db.getCharacterById(b.getStringId());
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
        
        assertTrue(db.swapWords(a.getStringId(), aOrigSort,
                                b.getStringId(), bOrigSort));
        
        LessonWord a1 = db.getWordById(a.getStringId());
        LessonWord b1 = db.getWordById(b.getStringId());
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
        lesson.addWord(a.getStringId());
        lesson.addWord(b.getStringId());
        db.addLesson(lesson);
        
        List<String> exp1 = Arrays.asList(new String[] {a.getStringId(), b.getStringId()});
        assertEquals(exp1, db.getWordsFromLessonId(lesson.getStringId()));
        assertEquals(exp1, lesson.getWordIds());
        
        db.swapWordsInLesson(lesson.getStringId(), a.getStringId(), b.getStringId());
        
        List<String> exp2 = Arrays.asList(new String[] {b.getStringId(), a.getStringId()});
        assertEquals(exp2, db.getWordsFromLessonId(lesson.getStringId()));
        
        Lesson lesson2 = db.getLessonById(lesson.getStringId());
        assertEquals(exp2, lesson2.getWordIds());
    }
    
    public void testSwapTagsCharacter() {
        LessonCharacter c = new LessonCharacter();
        c.addTag("1");
        c.addTag("2");
        db.addCharacter(c);
        
        List<String> exp1 = Arrays.asList(new String[] {"1", "2"});
        assertEquals(exp1, db.getCharacterTags(c.getStringId()));
        assertEquals(exp1, c.getTags());
        
        db.swapTags(DbAdapter.CHARTAG_TABLE, c.getStringId(), "1", "2");
        List<String> exp2 = Arrays.asList(new String[] {"2", "1"});
        assertEquals(exp2, db.getCharacterTags(c.getStringId()));
        
        LessonCharacter c1 = db.getCharacterById(c.getStringId());
        assertEquals(exp2, c1.getTags());
    }
    
    public void testSwapTagsWord() {
        LessonWord c = new LessonWord();
        c.addTag("1");
        c.addTag("2");
        db.addWord(c);
        
        List<String> exp1 = Arrays.asList(new String[] {"1", "2"});
        assertEquals(exp1, db.getWordTags(c.getStringId()));
        assertEquals(exp1, c.getTags());
        
        db.swapTags(DbAdapter.WORDTAG_TABLE, c.getStringId(), "1", "2");
        List<String> exp2 = Arrays.asList(new String[] {"2", "1"});
        assertEquals(exp2, db.getWordTags(c.getStringId()));
        
        LessonWord c1 = db.getWordById(c.getStringId());
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
        assertEquals(exp1, db.getKeyValues(c.getStringId(), LessonItem.ItemType.CHARACTER));
        assertEquals(exp1, c.getKeyValues());
        
        db.swapKeyValues2(DbAdapter.CHARKEYVALUES_TABLE, c.getStringId(), "k1", "k2");
        LinkedHashMap<String, String> exp2 = new LinkedHashMap<String, String>();
        exp2.put("k2", "v2");
        exp2.put("k1", "v1");
        assertEquals(exp2, db.getKeyValues(c.getStringId(), LessonItem.ItemType.CHARACTER));
        
        LessonCharacter c1 = db.getCharacterById(c.getStringId());
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
        assertEquals(exp1, db.getKeyValues(c.getStringId(), LessonItem.ItemType.WORD));
        assertEquals(exp1, c.getKeyValues());
        
        db.swapKeyValues2(DbAdapter.WORDKEYVALUES_TABLE, c.getStringId(), "k1", "k2");
        LinkedHashMap<String, String> exp2 = new LinkedHashMap<String, String>();
        exp2.put("k2", "v2");
        exp2.put("k1", "v1");
        assertEquals(exp2, db.getKeyValues(c.getStringId(), LessonItem.ItemType.WORD));
        
        LessonWord c1 = db.getWordById(c.getStringId());
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
    	assertEquals(a.getStringId(), result.getString(result.getColumnIndexOrThrow(DbAdapter.CHARTAG_ID)));
    	
    	//Test Case: equivalence class: tag, multiple character, character, case insensitive, partial match
    	result = db.browseByTag(ItemType.CHARACTER, "SON");
    	assertEquals(1, result.getCount());
    	assertEquals(b.getStringId(), result.getString(result.getColumnIndexOrThrow(DbAdapter.CHARTAG_ID)));
    	
    	//Test Case: equivalence class: keyvalue, single character, character, case insensitive
    	result = db.browseByTag(ItemType.CHARACTER, "A");
    	assertEquals(1, result.getCount());
    	assertEquals(a.getStringId(), result.getString(result.getColumnIndexOrThrow(DbAdapter.CHARTAG_ID)));
    	
    	//Test Case: equivalence class: keyvalue, multiple character, character, exact match
    	result = db.browseByTag(ItemType.CHARACTER, "batt");
    	assertEquals(1, result.getCount());
    	assertEquals(b.getStringId(), result.getString(result.getColumnIndexOrThrow(DbAdapter.CHARTAG_ID)));
    	
    	//Need more tests for words
    	result.close();
    }
    
    public void testDeleteCharacter()
    {
    	LessonCharacter c = new LessonCharacter();
    	db.addCharacter(c);
    	db.deleteCharacter(c.getStringId());
    	assertNull(db.getCharacterById(c.getStringId()));
    }

    public void testDeleteWord()
    {
    	LessonWord w = new LessonWord();
    	db.addWord(w);
    	db.deleteWord(w.getStringId());
    	assertNull(db.getWordById(w.getStringId()));
    }
}
