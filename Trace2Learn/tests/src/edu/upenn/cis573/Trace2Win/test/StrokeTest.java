package edu.upenn.cis573.Trace2Win.test;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.graphics.PointF;

import com.trace2learn.TraceLibrary.Database.Parser;
import com.trace2learn.TraceLibrary.Database.Stroke;

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
	
	public void testToXml() {
	    Stroke s = new Stroke(1, 1);
        s.addPoint(2, 3);
        s.addPoint(4, 9);
        
        String exp = "<stroke position=\"3\">\n" +
        		"<point position=\"0\" x=\"1.0\" y=\"1.0\" />\n" +
        		"<point position=\"1\" x=\"2.0\" y=\"3.0\" />\n" +
        		"<point position=\"2\" x=\"4.0\" y=\"9.0\" />\n" +
        		"</stroke>\n";
        
        assertEquals(exp, s.toXml(3));
	}
	
	public void testImportFromXml() throws SAXException, IOException {
	    String xml = "<stroke position=\"3\">\n" +
                "<point position=\"0\" x=\"1.0\" y=\"1.0\" />\n" +
                "<point position=\"1\" x=\"2.0\" y=\"3.0\" />\n" +
                "<point position=\"2\" x=\"4.0\" y=\"9.0\" />\n" +
                "</stroke>\n";
	    Element elem = Parser.parse(xml).getDocumentElement();
	    
	    Stroke exp = new Stroke(1, 1);
        exp.addPoint(2, 3);
        exp.addPoint(4, 9);
        
	    assertEquals(exp, Stroke.importFromXml(elem));
	}

	public void testImportFromXmlOnePoint() throws SAXException, IOException {
	    String xml = "<stroke position=\"3\">\n" +
	            "<point position=\"0\" x=\"1.0\" y=\"1.0\" />\n" +
	            "</stroke>\n";
	    Element elem = Parser.parse(xml).getDocumentElement();

	    Stroke exp = new Stroke(1, 1);

	    assertEquals(exp, Stroke.importFromXml(elem));
	}

	public void testImportFromXmlNoPoints() throws SAXException, IOException {
	    String xml = "<stroke position=\"3\">\n" +
	            "</stroke>\n";
	    Element elem = Parser.parse(xml).getDocumentElement();

	    Stroke exp = new Stroke();

	    assertEquals(exp, Stroke.importFromXml(elem));
	}
	
}
