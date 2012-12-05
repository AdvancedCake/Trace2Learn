package edu.upenn.cis573.Trace2Win.test;

import java.io.IOException;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.test.AndroidTestCase;
import android.util.Log;
import edu.upenn.cis573.Trace2Win.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.Database.Parser;
import edu.upenn.cis573.Trace2Win.Database.Stroke;

public class LessonCharacterTest extends AndroidTestCase {
	
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
		// TODO: Clear Database
		
		s1 = new Stroke(1,1);
		s1.addPoint(2, 2);
		s1.addPoint(3, 3);
		
		s2 = new Stroke(1,10);
		s2.addPoint(2, 20);
		s2.addPoint(3, 30);

		s3 = new Stroke(10,1);
		s3.addPoint(20, 2);
		s3.addPoint(30, 3);

	}

	protected void tearDown()
	{
		dumpDBs();
	}
	
	static public void compareCharacters(LessonCharacter expected, LessonCharacter actual)
	{
		LessonItemTest.compareLessonItem(expected, actual);		
		assertEquals(expected.getNumStrokes(), actual.getNumStrokes());
		assertEquals(expected.getStrokes(), actual.getStrokes());
	}
	
	public void testNoStrokes()
	{
		LessonCharacter c = new LessonCharacter();
		assertEquals(0,c.getNumStrokes());
		assertEquals(0,c.getStrokes().size());
		try{
			c.getStroke(0);
		}catch(IndexOutOfBoundsException e){}
		try{
			c.removeStroke(0);
		}catch(IndexOutOfBoundsException e){}
		
	}
	
	public void testNoStrokesSave()
	{
		LessonCharacter c = new LessonCharacter();
		db.addCharacter(c);
		compareCharacters(c, db.getCharacterById(c.getId()));
	}
	
	public void testOneStroke()
	{
		LessonCharacter c = new LessonCharacter();
		c.addStroke(s1);
		assertEquals(1,c.getNumStrokes());
		assertEquals(1,c.getStrokes().size());
		assertEquals(s1,c.getStroke(0));
		assertTrue(!c.removeStroke(s2));
		assertTrue(c.removeStroke(s1));
		assertTrue(!c.removeStroke(s1));
		
		c.addStroke(s2);
		assertEquals(s2,c.removeStroke(0));
		assertTrue(!c.removeStroke(s2));
		
	}
	
	public void testOneStrokeSave()
	{
		LessonCharacter c = new LessonCharacter();
		c.addStroke(s1);
		db.addCharacter(c);
		compareCharacters(c, db.getCharacterById(c.getId()));
	}
	
	public void testAddStrokeAfterSave()
	{
		LessonCharacter c = new LessonCharacter();
		c.addStroke(s1);
		db.addCharacter(c);
		compareCharacters(c, db.getCharacterById(c.getId()));
		c.addStroke(s2);
		db.addCharacter(c);
		compareCharacters(c, db.getCharacterById(c.getId()));
	}
	
	
	public void testRemovalByIndex()
	{
		LessonCharacter c = new LessonCharacter();
		c.addStroke(s1);
		c.addStroke(s2);
		c.addStroke(s2);
		c.addStroke(s3);
		assertEquals(4,c.getNumStrokes());
		
		assertEquals(s3,c.getStroke(3));
		assertEquals(s2,c.removeStroke(1));
		assertEquals(s3,c.removeStroke(2));
		assertEquals(s1,c.removeStroke(0));
		assertEquals(s2,c.removeStroke(0));
		assertEquals(0,c.getNumStrokes());
	}
	
	public void testRemovalByRefernece()
	{
		LessonCharacter c = new LessonCharacter();
		c.addStroke(s1);
		c.addStroke(s2);
		c.addStroke(s2);
		c.addStroke(s3);
		assertEquals(4,c.getNumStrokes());

		assertTrue(c.removeStroke(s3));
		assertTrue(c.removeStroke(s2));
		assertEquals(s2,c.removeStroke(1));
		assertTrue(c.removeStroke(s1));
		assertEquals(0,c.getNumStrokes());
		
	}
	
	public void testReorder()
	{
		LessonCharacter c = new LessonCharacter();
		c.addStroke(s1);
		c.addStroke(s2);
		c.addStroke(s2);
		c.addStroke(s3);
		assertEquals(4,c.getNumStrokes());
		
		//old>new
		assertEquals(s2,c.getStroke(2));
		c.reorderStroke(2, 0);
		assertEquals(s2,c.getStroke(0));
		assertEquals(s1,c.getStroke(1));
		assertEquals(s2,c.getStroke(2));
		assertEquals(s3,c.getStroke(3));
		
		//new>old
		c.reorderStroke(0, 3);
		assertEquals(s2,c.getStroke(3));
		assertEquals(s1,c.getStroke(0));
		assertEquals(s2,c.getStroke(1));
		assertEquals(s3,c.getStroke(2));
		
	}
	
	public void testSaveTags()
	{
		LessonCharacter c = new LessonCharacter();
		c.addTag("Tag1");
		db.addCharacter(c);
		LessonCharacter c1 = db.getCharacterById(c.getId());
		compareCharacters(c, c1);
	}
	
	public void testSaveKeyValues()
	{
		LessonCharacter c = new LessonCharacter();
		c.addKeyValue("key1", "value1");
		db.addCharacter(c);
		LessonCharacter c1 = db.getCharacterById(c.getId());
		compareCharacters(c, c1);
	}	
	
	public void testHasKeyValue()
	{
		LessonCharacter c = new LessonCharacter();
		c.addKeyValue("key1", "value1");
		db.addCharacter(c);
		assertTrue(c.hasKeyValue("key1", "value1"));		
	}

	public void testGetKeyValues()
	{
		LessonCharacter c = new LessonCharacter();
		c.addKeyValue("key1", "value1");		
		HashMap<String, String> keyValues = c.getKeyValues();
		
		assertEquals(1, keyValues.size());
		assertTrue(keyValues.containsKey("key1"));
		assertTrue(keyValues.containsValue("value1"));
	}
	
	public void testToXml() {
	    LessonCharacter c = new LessonCharacter(100);
	    c.addTag("tag!");
	    c.addTag("another tag?");
        c.addKeyValue("k1", "v1");
        c.addKeyValue("k2", "v2");
        c.addStroke(s1);
        c.addStroke(s2);
        c.addStroke(s3);
        
        String exp = "<character id=\"100\">\n" +
        		"<tag tag=\"tag!\" />\n" +
        		"<tag tag=\"another tag?\" />\n" +
        		"<id key=\"k1\" value=\"v1\" />\n" +
        		"<id key=\"k2\" value=\"v2\" />\n" +
        		"<stroke position=\"0\">\n" +
                "<point position=\"0\" x=\"1.0\" y=\"1.0\" />\n" +
                "<point position=\"1\" x=\"2.0\" y=\"2.0\" />\n" +
                "<point position=\"2\" x=\"3.0\" y=\"3.0\" />\n" +
        		"</stroke>\n" +
        		"<stroke position=\"1\">\n" +
                "<point position=\"0\" x=\"1.0\" y=\"10.0\" />\n" +
                "<point position=\"1\" x=\"2.0\" y=\"20.0\" />\n" +
                "<point position=\"2\" x=\"3.0\" y=\"30.0\" />\n" +
        		"</stroke>\n" +
        		"<stroke position=\"2\">\n" +
                "<point position=\"0\" x=\"10.0\" y=\"1.0\" />\n" +
                "<point position=\"1\" x=\"20.0\" y=\"2.0\" />\n" +
                "<point position=\"2\" x=\"30.0\" y=\"3.0\" />\n" +
        		"</stroke>\n" +
        		"</character>\n";
        
        assertEquals(exp, c.toXml());
	}
	
    public void testImportFromXml() throws SAXException, IOException {
        String xml = "<character id=\"100\">\n" +
                "<tag tag=\"tag!\" />\n" +
                "<tag tag=\"another tag?\" />\n" +
                "<id key=\"k1\" value=\"v1\" />\n" +
                "<id key=\"k2\" value=\"v2\" />\n" +
                "<stroke position=\"0\">\n" +
                "<point position=\"0\" x=\"1.0\" y=\"1.0\" />\n" +
                "<point position=\"1\" x=\"2.0\" y=\"2.0\" />\n" +
                "<point position=\"2\" x=\"3.0\" y=\"3.0\" />\n" +
                "</stroke>\n" +
                "<stroke position=\"1\">\n" +
                "<point position=\"0\" x=\"1.0\" y=\"10.0\" />\n" +
                "<point position=\"1\" x=\"2.0\" y=\"20.0\" />\n" +
                "<point position=\"2\" x=\"3.0\" y=\"30.0\" />\n" +
                "</stroke>\n" +
                "<stroke position=\"2\">\n" +
                "<point position=\"0\" x=\"10.0\" y=\"1.0\" />\n" +
                "<point position=\"1\" x=\"20.0\" y=\"2.0\" />\n" +
                "<point position=\"2\" x=\"30.0\" y=\"3.0\" />\n" +
                "</stroke>\n" +
                "</character>\n";
        Element elem = Parser.parse(xml).getDocumentElement();
        
        LessonCharacter exp = new LessonCharacter(100);
        exp.addTag("tag!");
        exp.addTag("another tag?");
        exp.addKeyValue("k1", "v1");
        exp.addKeyValue("k2", "v2");
        exp.addStroke(s1);
        exp.addStroke(s2);
        exp.addStroke(s3);
        
        compareCharacters(exp, (LessonCharacter) LessonCharacter.importFromXml(elem));
    }
    
    public void testImportFromXmlNoTags() throws SAXException, IOException {
        String xml = "<character id=\"100\">\n" +
                "<id key=\"k1\" value=\"v1\" />\n" +
                "<id key=\"k2\" value=\"v2\" />\n" +
                "<stroke position=\"0\">\n" +
                "<point position=\"0\" x=\"1.0\" y=\"1.0\" />\n" +
                "<point position=\"1\" x=\"2.0\" y=\"2.0\" />\n" +
                "<point position=\"2\" x=\"3.0\" y=\"3.0\" />\n" +
                "</stroke>\n" +
                "<stroke position=\"1\">\n" +
                "<point position=\"0\" x=\"1.0\" y=\"10.0\" />\n" +
                "<point position=\"1\" x=\"2.0\" y=\"20.0\" />\n" +
                "<point position=\"2\" x=\"3.0\" y=\"30.0\" />\n" +
                "</stroke>\n" +
                "<stroke position=\"2\">\n" +
                "<point position=\"0\" x=\"10.0\" y=\"1.0\" />\n" +
                "<point position=\"1\" x=\"20.0\" y=\"2.0\" />\n" +
                "<point position=\"2\" x=\"30.0\" y=\"3.0\" />\n" +
                "</stroke>\n" +
                "</character>\n";
        Element elem = Parser.parse(xml).getDocumentElement();
        
        LessonCharacter exp = new LessonCharacter(100);
        exp.addKeyValue("k1", "v1");
        exp.addKeyValue("k2", "v2");
        exp.addStroke(s1);
        exp.addStroke(s2);
        exp.addStroke(s3);
        
        compareCharacters(exp, (LessonCharacter) LessonCharacter.importFromXml(elem));
    }
    
    public void testImportFromXmlNoIds() throws SAXException, IOException {
        String xml = "<character id=\"100\">\n" +
                "<tag tag=\"tag!\" />\n" +
                "<tag tag=\"another tag?\" />\n" +
                "<stroke position=\"0\">\n" +
                "<point position=\"0\" x=\"1.0\" y=\"1.0\" />\n" +
                "<point position=\"1\" x=\"2.0\" y=\"2.0\" />\n" +
                "<point position=\"2\" x=\"3.0\" y=\"3.0\" />\n" +
                "</stroke>\n" +
                "<stroke position=\"1\">\n" +
                "<point position=\"0\" x=\"1.0\" y=\"10.0\" />\n" +
                "<point position=\"1\" x=\"2.0\" y=\"20.0\" />\n" +
                "<point position=\"2\" x=\"3.0\" y=\"30.0\" />\n" +
                "</stroke>\n" +
                "<stroke position=\"2\">\n" +
                "<point position=\"0\" x=\"10.0\" y=\"1.0\" />\n" +
                "<point position=\"1\" x=\"20.0\" y=\"2.0\" />\n" +
                "<point position=\"2\" x=\"30.0\" y=\"3.0\" />\n" +
                "</stroke>\n" +
                "</character>\n";
        Element elem = Parser.parse(xml).getDocumentElement();
        
        LessonCharacter exp = new LessonCharacter(100);
        exp.addTag("tag!");
        exp.addTag("another tag?");
        exp.addStroke(s1);
        exp.addStroke(s2);
        exp.addStroke(s3);
        
        compareCharacters(exp, (LessonCharacter) LessonCharacter.importFromXml(elem));
    }
    
    public void testImportFromXmlOneStroke() throws SAXException, IOException {
        String xml = "<character id=\"100\">\n" +
                "<tag tag=\"tag!\" />\n" +
                "<tag tag=\"another tag?\" />\n" +
                "<id key=\"k1\" value=\"v1\" />\n" +
                "<id key=\"k2\" value=\"v2\" />\n" +
                "<stroke position=\"0\">\n" +
                "<point position=\"0\" x=\"1.0\" y=\"1.0\" />\n" +
                "<point position=\"1\" x=\"2.0\" y=\"2.0\" />\n" +
                "<point position=\"2\" x=\"3.0\" y=\"3.0\" />\n" +
                "</stroke>\n" +
                "</character>\n";
        Element elem = Parser.parse(xml).getDocumentElement();
        
        LessonCharacter exp = new LessonCharacter(100);
        exp.addTag("tag!");
        exp.addTag("another tag?");
        exp.addKeyValue("k1", "v1");
        exp.addKeyValue("k2", "v2");
        exp.addStroke(s1);
        
        compareCharacters(exp, (LessonCharacter) LessonCharacter.importFromXml(elem));
    }
    
    public void testImportFromXmlNoStrokes() throws SAXException, IOException {
        String xml = "<character id=\"100\">\n" +
                "<tag tag=\"tag!\" />\n" +
                "<tag tag=\"another tag?\" />\n" +
                "<id key=\"k1\" value=\"v1\" />\n" +
                "<id key=\"k2\" value=\"v2\" />\n" +
                "</character>\n";
        Element elem = Parser.parse(xml).getDocumentElement();
        
        LessonCharacter exp = new LessonCharacter(100);
        exp.addTag("tag!");
        exp.addTag("another tag?");
        exp.addKeyValue("k1", "v1");
        exp.addKeyValue("k2", "v2");
        
        compareCharacters(exp, (LessonCharacter) LessonCharacter.importFromXml(elem));
    }
}
