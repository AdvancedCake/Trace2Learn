package edu.upenn.cis350.Trace2Learn.test;

import edu.upenn.cis350.Trace2Learn.Characters.LessonCharacter;
import edu.upenn.cis350.Trace2Learn.Characters.Stroke;
import junit.framework.TestCase;

public class LessonCharacterTest extends TestCase {
	
	Stroke s1, s2, s3;


	protected void setUp() throws Exception {
		super.setUp();

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
	
	
	
	
}
