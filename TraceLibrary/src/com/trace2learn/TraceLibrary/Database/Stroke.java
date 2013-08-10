package com.trace2learn.TraceLibrary.Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

public class Stroke {
	
	List<PointF> _points;
	Path _cachedPath = null;
	
	public Stroke()
	{
		_points = new ArrayList<PointF>();
	}
	
	/**
	 * creates a new Stroke which starts at (startX,  startY)
	 */
	public Stroke(float startX, float startY)
	{
		this(new PointF(startX, startY));
	}
	
	/**
	 * creates a new Stroke which starts at startP
	 * @param startP
	 */
	public Stroke(PointF startP)
	{
		this();
		addPoint(startP);
	}
	
    public Stroke(List<PointF> points) {
        this();
        _points = points;
    }
    
    public Stroke(PointF[] points) {
        this(new ArrayList<PointF>(Arrays.asList(points)));
    }
	
	public synchronized int getNumSamples()
	{
		return _points.size();
	}
	
	/**
	 * @return a list of points sampled to represent the stroke
	 */
	public synchronized List<PointF> getSamplePoints()
	{
		List<PointF> samples = new ArrayList<PointF>();
		for(PointF p : _points)
		{
			PointF p1 = new PointF(p.x, p.y);
			samples.add(p1);
		}
		return samples;
	}
	
	public synchronized PointF getPoint(int i){
		return _points.get(i);
	}
	
	public synchronized void addPoint(float x, float y)
	{
		addPoint(new PointF(x, y));
	}
	
	public synchronized void addPoint(PointF p)
	{
		_points.add(p);
		_cachedPath = null;
	}
	
	/**	
	 * @return A path representation of the stroke which can be drawn on-screen
	 */
	public synchronized Path toPath(float time)
	{
		if(time > 1) return toPath();
		Path path = new Path();
		if(_points.size() <= 0) 
		{
			return path;
		}
		else if(_points.size() == 1) 
		{
			PointF p = _points.get(0);
			path.moveTo(p.x, p.y);
			return path;
		}
		float pTime = 1F/_points.size();
		if(time < pTime) return path;
		Iterator<PointF> iter = _points.iterator();
		PointF p1 = iter.next();
		PointF p2 = iter.next();
		path.moveTo(p1.x, p1.y);
		float covered = pTime;
		if(covered <= time && iter.hasNext())
		{
			p1 = p2;
			p2 = iter.next();
			covered+=pTime;
		}
		
		while(covered <= time && iter.hasNext())
		{
			path.quadTo(p1.x, p1.y, (p2.x + p1.x) / 2, (p2.y + p1.y) / 2);
			p1 = p2;
			p2 = iter.next();
			covered+=pTime;
		}
		
		path.lineTo(p2.x, p2.y);
		return new Path(path);
	}
	
	public Path toPath(Matrix transform)
	{
		Path p = toPath(1);
		p.transform(transform);
		return p;
	}
	
	public Path toPath(Matrix transform, float time)
	{
		Path p = toPath(time);
		p.transform(transform);
		return p;
	}
	
	public Path toPath()
	{
		return toPath(1);
	}
	
	/**
	 * Creates an XML representation of this stroke.
	 * @return an XML string
	 */
	public String toXml(int position) {
	    String xml = "<stroke position=\"" + position + "\">\n";

	    int numPoints = _points.size();
	    for (int i = 0; i < numPoints; i++) {
	        PointF point = _points.get(i);
	        xml += "<point position=\"" + i + "\" x=\"" + point.x +
	                "\" y=\"" + point.y + "\" />\n";
	    }
	    
	    xml += "</stroke>\n";
	    
	    return xml;
	}
	
    /**
     * Converts a parsed XML element to a Stroke
     * @param elem XML DOM element where the root element is <stroke>
     * @return the Stroke represented by the XML element, or null if there was
     * an error
     */	
	public static Stroke importFromXml(Element elem) {
        try {
            if (!elem.getNodeName().equals("stroke")) {
                return null;
            }
            
            NodeList points = elem.getElementsByTagName("point");
            PointF[] pointArr = new PointF[points.getLength()];
            for (int j = 0; j < points.getLength(); j++) {
                Element pointElem = (Element) points.item(j);
                int pointPosition = Integer.parseInt(
                        pointElem.getAttribute("position"));
                float x = Float.parseFloat(pointElem.getAttribute("x"));
                float y = Float.parseFloat(pointElem.getAttribute("y"));
                pointArr[pointPosition] = new PointF(x, y);
            }
            
            return new Stroke(pointArr);
        } catch (NumberFormatException e) {
            Log.e("Import Stroke", e.getMessage());
            return null;
        }
	}
	
	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof Stroke)) { return false; }
	    
	    List<PointF> otherPoints = ((Stroke) other)._points;
	    if (_points.size() != otherPoints.size()) { return false; }
	    
	    for (int i = 0; i < _points.size(); i++) {
	        PointF point = _points.get(i);
	        PointF otherPoint = otherPoints.get(i);
	        if (!point.equals(otherPoint.x, otherPoint.y)) { return false; }
	    }
	    
	    return true;
	}
	
}
