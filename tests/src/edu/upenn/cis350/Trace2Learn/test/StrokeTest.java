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
		assertEquals("Number of samples reported is wrong",0,s.getNumSamples());
		
		List<PointF> points = s.getSamplePoints();
		assertEquals("Number of samples in list is wrong",0,points.size());
		
		Path p = new Path();
		assertTrue("Path should be empty from stroke with no points",s.toPath().isEmpty());
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
		assertEquals("Expected Point not received",1,points.get(0).x,.00001);
		assertEquals("Expected Point not received",1,points.get(0).y,.00001);
		assertEquals("Expected Point not received",2,points.get(1).x,.00001);
		assertEquals("Expected Point not received",2,points.get(1).y,.00001);
		assertEquals("Expected Point not received",3,points.get(2).x,.00001);
		assertEquals("Expected Point not received",10,points.get(2).y,.00001);
	}

	public void testOnePoint()
	{
		Stroke s = new Stroke(1,1);
		assertEquals("Number of samples reported is wrong",1,s.getNumSamples());
		
		List<PointF> points = s.getSamplePoints();
		assertEquals("Number of samples in list is wrong",1,points.size());
	}
	
	public void testToPath()
	{
		//cannot test toPath well, path does not provide a good Equals method.
		//testing for no exception, nothing else I can do
		//zero points
		Stroke s = new Stroke();
		s.toPath();
		
		//one points
		s.addPoint(2, 2);
		s.toPath();
		
		//two points
		s.addPoint(3,3);
		s.toPath();
	}
	
}
