package edu.upenn.cis573.Trace2Win.test;

import java.io.IOException;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.test.AndroidTestCase;
import android.util.Log;
import edu.upenn.cis573.Trace2Win.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.Database.LessonWord;
import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.Database.Parser;

public class LessonWordTest extends AndroidTestCase {

	LessonCharacter c1, c2, c3;
	private DbAdapter db;
	
	static public void compareWords(LessonWord expected, LessonWord actual)
	{
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getItemType(), actual.getItemType());
		assertEquals(expected.getCharacterIds(), actual.getCharacterIds());
		assertEquals(expected.getTags(), actual.getTags());
		assertEquals(expected.getKeyValues(), actual.getKeyValues());
		assertEquals(expected.getSort(), actual.getSort());
		assertEquals(expected.getStringId(), actual.getStringId());
	}	
	
	protected void setUp() throws Exception {
		super.setUp();

		c1 = new LessonCharacter();
		c2 = new LessonCharacter();
		c3 = new LessonCharacter();
		
		c1.setId(1);
		c2.setId(2);
		c3.setId(3);
		
		db = new DbAdapter(this.getContext());
		db.open();
	}
	
	protected void dumpDBs()
	{
		for(String str : this.getContext().databaseList())
		{
			Log.i("DELETE", str);
			this.getContext().deleteDatabase(str);
		}
	}
	
	protected void tearDown()
	{
		dumpDBs();
		db.close();
	}	
	
	public void testNoStrokes()
	{
		LessonWord w = new LessonWord();
		assertEquals(0,w.getCharacters().size());
		try{
			w.getCharacterId(0);
		}catch(IndexOutOfBoundsException e){}
		try{
			w.removeCharacter(0);
		}catch(IndexOutOfBoundsException e){}
		
	}
	
	public void testOneChar()
	{
		LessonWord w = new LessonWord();
		w.addCharacter(c1.getId());
		assertEquals(1,w.getCharacters().size());
		assertEquals(c1.getId(),w.getCharacterId(0));
		assertTrue(!w.removeCharacter(c2.getId()));
		assertTrue(w.removeCharacter(c1.getId()));
		assertTrue(!w.removeCharacter(c1.getId()));
		
		w.addCharacter(c2.getId());
		assertEquals(c2.getId(),w.removeCharacter(0));
		assertTrue(!w.removeCharacter(c2.getId()));
		
	}
	
	public void testRemovalByIndex()
	{
		LessonWord w = new LessonWord();
		w.addCharacter(c1.getId());
		w.addCharacter(c2.getId());
		w.addCharacter(c2.getId());
		w.addCharacter(c3.getId());
		assertEquals(4,w.getCharacters().size());
		
		assertEquals(c3.getId(),w.getCharacterId(3));
		assertEquals(c2.getId(),w.removeCharacter(1));
		assertEquals(c3.getId(),w.removeCharacter(2));
		assertEquals(c1.getId(),w.removeCharacter(0));
		assertEquals(c2.getId(),w.removeCharacter(0));
		assertEquals(0,w.getCharacters().size());
	}

	public void testSaveTags()
	{
		LessonWord w = new LessonWord();
		w.addTag("Tag1");
		db.addWord(w);
		LessonWord w1 = db.getWordById(w.getId());
		compareWords(w, w1);
	}
	
	public void testSaveKeyValues()
	{
		LessonWord w = new LessonWord();
		w.addKeyValue("key1", "value1");
		db.addWord(w);
		LessonWord w1 = db.getWordById(w.getId());
		compareWords(w, w1);
	}	
	
	public void testHasKeyValue()
	{
		LessonWord w = new LessonWord();
		w.addKeyValue("key1", "value1");
		db.addWord(w);
		assertTrue(w.hasKeyValue("key1", "value1"));		
	}	
	
	public void testGetKeyValues()
	{
		LessonWord w = new LessonWord();
		w.addKeyValue("key1", "value1");		
		HashMap<String, String> keyValues = w.getKeyValues();
		
		assertEquals(1, keyValues.size());
		assertTrue(keyValues.containsKey("key1"));
		assertTrue(keyValues.containsValue("value1"));
	}	
	
	public void testToXml() {
	    LessonWord w = new LessonWord();
	    w.setId(100);
	    w.addTag("tag!");
	    w.addTag("another tag?");
        w.addKeyValue("k1", "v1");
        w.addKeyValue("k2", "v2");
        w.addCharacter(c1.getId());
        w.addCharacter(c2.getId());
        w.addCharacter(c3.getId());
        
        String exp = "<word id=\"100\">\n" +
        		"<tag tag=\"tag!\" />\n" +
        		"<tag tag=\"another tag?\" />\n" +
        		"<id key=\"k1\" value=\"v1\" />\n" +
        		"<id key=\"k2\" value=\"v2\" />\n" +
        		"<character id=\"1\" position=\"0\" />\n" +
        		"<character id=\"2\" position=\"1\" />\n" +
        		"<character id=\"3\" position=\"2\" />\n" +
        		"</word>\n";
        
        assertEquals(exp, w.toXml());
	}
	
    public void testImportFromXml() throws SAXException, IOException {
        String xml = "<word id=\"100\">\n" +
        		"<tag tag=\"tag!\" />\n" +
        		"<tag tag=\"another tag?\" />\n" +
        		"<id key=\"k1\" value=\"v1\" />\n" +
        		"<id key=\"k2\" value=\"v2\" />\n" +
        		"<character id=\"1\" position=\"0\" />\n" +
        		"<character id=\"2\" position=\"1\" />\n" +
        		"<character id=\"3\" position=\"2\" />\n"  +
        		"</word>\n";
        Element elem = Parser.parse(xml).getDocumentElement();
        
        LessonWord exp = new LessonWord();
        exp.setId(100);
        exp.addTag("tag!");
        exp.addTag("another tag?");
        exp.addKeyValue("k1", "v1");
        exp.addKeyValue("k2", "v2");
        exp.addCharacter(c1.getId());
        exp.addCharacter(c2.getId());
        exp.addCharacter(c3.getId());
        
        compareWords(exp, LessonWord.importFromXml(elem));
    }
    
    public void testImportFromXmlNoTags() throws SAXException, IOException {
        String xml = "<word id=\"100\">\n" +
        		"<id key=\"k1\" value=\"v1\" />\n" +
        		"<id key=\"k2\" value=\"v2\" />\n" +
        		"<character id=\"1\" position=\"0\" />\n" +
        		"<character id=\"2\" position=\"1\" />\n" +
        		"<character id=\"3\" position=\"2\" />\n" +
        		"</word>\n";
        Element elem = Parser.parse(xml).getDocumentElement();
        
        LessonWord exp = new LessonWord();
        exp.setId(100);
        exp.addKeyValue("k1", "v1");
        exp.addKeyValue("k2", "v2");
        exp.addCharacter(c1.getId());
        exp.addCharacter(c2.getId());
        exp.addCharacter(c3.getId());
        
        compareWords(exp, LessonWord.importFromXml(elem));
    }
    
    public void testImportFromXmlNoIds() throws SAXException, IOException {
        String xml = "<word id=\"100\">\n" +
        		"<tag tag=\"tag!\" />\n" +
        		"<tag tag=\"another tag?\" />\n" +
        		"<character id=\"1\" position=\"0\" />\n" +
        		"<character id=\"2\" position=\"1\" />\n" +
        		"<character id=\"3\" position=\"2\" />\n" +
        		"</word>\n";
        Element elem = Parser.parse(xml).getDocumentElement();
        
        LessonWord exp = new LessonWord();
        exp.setId(100);
        exp.addTag("tag!");
        exp.addTag("another tag?");
        exp.addCharacter(c1.getId());
        exp.addCharacter(c2.getId());
        exp.addCharacter(c3.getId());
        
        compareWords(exp, LessonWord.importFromXml(elem));
    }
    
    public void testImportFromXmlOneCharacter() throws SAXException, IOException {
        String xml = "<word id=\"100\">\n" +
        		"<tag tag=\"tag!\" />\n" +
        		"<tag tag=\"another tag?\" />\n" +
        		"<id key=\"k1\" value=\"v1\" />\n" +
        		"<id key=\"k2\" value=\"v2\" />\n" +
        		"<character id=\"1\" position=\"0\" />\n" +
        		"</word>\n";
        Element elem = Parser.parse(xml).getDocumentElement();
        
        LessonWord exp = new LessonWord();
        exp.setId(100);
        exp.addTag("tag!");
        exp.addTag("another tag?");
        exp.addKeyValue("k1", "v1");
        exp.addKeyValue("k2", "v2");
        exp.addCharacter(c1.getId());
        
        compareWords(exp, LessonWord.importFromXml(elem));
    }
    
    public void testImportFromXmlNoCharacters() throws SAXException, IOException {
        String xml = "<word id=\"100\">\n" +
        		"<tag tag=\"tag!\" />\n" +
        		"<tag tag=\"another tag?\" />\n" +
        		"<id key=\"k1\" value=\"v1\" />\n" +
        		"<id key=\"k2\" value=\"v2\" />\n" +
        		"</word>\n";
        Element elem = Parser.parse(xml).getDocumentElement();
        
        LessonWord exp = new LessonWord();
        exp.setId(100);
        exp.addTag("tag!");
        exp.addTag("another tag?");
        exp.addKeyValue("k1", "v1");
        exp.addKeyValue("k2", "v2");
        
        compareWords(exp, LessonWord.importFromXml(elem));
    }
}
