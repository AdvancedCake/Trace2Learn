package edu.upenn.cis573.Trace2Win.Database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;

public class Stroke {
	
	List<PointF> _points;
	Path _cachedPath = null;
	
	public Stroke()
	{
		_points = new LinkedList<PointF>();
	}
	
	/**
	 * creates a new strokes which starts at (startX,  startY)
	 */
	public Stroke(float startX, float startY)
	{
		this(new PointF(startX, startY));
	}
	
	/**
	 * creates a new strokes which starts at startP
	 * @param startP
	 */
	public Stroke(PointF startP)
	{
		this();
		addPoint(startP);
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
	
}
