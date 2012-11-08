package edu.upenn.cis573.Trace2Win.test;

import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.Database.LessonWord;
import edu.upenn.cis573.Trace2Win.Database.Stroke;
import junit.framework.TestCase;

public class LessonWordTest extends TestCase {

	LessonCharacter c1, c2, c3;
	
	protected void setUp() throws Exception {
		super.setUp();

		c1 = new LessonCharacter();
		c2 = new LessonCharacter();
		c3 = new LessonCharacter();
		
		c1.setId(1);
		c2.setId(2);
		c3.setId(3);
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

}
