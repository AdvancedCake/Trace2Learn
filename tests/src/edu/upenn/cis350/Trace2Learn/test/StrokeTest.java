package edu.upenn.cis350.Trace2Learn.test;

import java.util.List;

import android.graphics.PointF;
import android.graphics.Path;
import junit.framework.TestCase;
import edu.upenn.cis350.Trace2Learn.Characters.*;

public class StrokeTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testNoPoints()
	{
		Stroke s = new Stroke();
		assertEquals("Number of samples reported isList<E>ng",0,s.getNumSamples());
		
		List<PointF> points = s.getSamplePoints();
		assertEquals("Number of samples in list is wrong",0,points.size());
		
		Path p = new Path();
		assertEquals("Path should be empty from stroke with no points",p,s.toPath());
	}
	
	public void testAddPoint()
	{
		Stroke s = new Stroke(1,1);
		s.addPoint(2, 2);
		PointF p = new PointF(3,10);
		s.addPoint(p);
		//test number of points
		assertEquals("Number of samples reported is wrong",3,s.getNumSamples());
		List<PointF> points = s.getSamplePoints();
		assertEquals("Number of samples in list is wrong",3,points.size());
		
		//test that points gotten in expected order
		assertEquals("Expected Point not received",1,points.get(0).x);
		assertEquals("Expected Point not received",1,points.get(0).y);
		assertEquals("Expected Point not received",2,points.get(1).x);
		assertEquals("Expected Point not received",2,points.get(1).y);
		assertEquals("Expected Point not received",3,points.get(2).x);
		assertEquals("Expected Point not received",10,points.get(2).y);
	}

	//TODO: Possibly test toPath() normal behavior....might be hard to do that though.
	
}
