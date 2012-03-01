package edu.upenn.cis350.Trace2Learn.Characters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Path;
import android.graphics.PointF;

public class Stroke {
	
	List<PointF> _points;
	Path _cachedPath = null;
	
	public Stroke()
	{
		_points = new LinkedList<PointF>();
	}
	
	public Stroke(float startX, float startY)
	{
		this();
		addPoint(startX, startY);
	}
	
	public Stroke(PointF startP)
	{
		this(startP.x, startP.y);
	}
	
	public int getNumSamples()
	{
		return _points.size();
	}
	
	public List<PointF> getSamplePoints()
	{
		List<PointF> samples = new ArrayList<PointF>();
		for(PointF p : _points)
		{
			PointF p1 = new PointF(p.x, p.y);
			samples.add(p1);
		}
		return samples;
	}
	
	public void addPoint(float x, float y)
	{
		_points.add(new PointF(x, y));
		_cachedPath = null;
	}
	
	public void addPoint(PointF p)
	{
		addPoint(p.x, p.y);
	}
	
	public Path toPath()
	{
		if(_cachedPath != null)
		{
			return _cachedPath;
		}
		Path path = new Path();
		if(_points.size() <= 0) 
		{
			return path;
		}
		else if(_points.size() == 1) 
		{
			PointF p = _points.get(0);
			path.moveTo(p.x, p.y);
		}
		Iterator<PointF> iter = _points.iterator();
		PointF p1 = iter.next();
		PointF p2 = iter.next();
		path.moveTo(p1.x, p1.y);
		
		for(p1 = p2, p2 = iter.next();
			iter.hasNext();
		    p1 = p2, p2 = iter.next())
		{
			path.quadTo(p1.x, p1.y, (p2.x + p1.x) / 2, (p2.y + p1.y) / 2);
		}
		
		path.lineTo(p2.x, p2.y);
		_cachedPath = path;
		return path;
	}
	
}
