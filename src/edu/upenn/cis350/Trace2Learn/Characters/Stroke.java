package edu.upenn.cis350.Trace2Learn.Characters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Path;
import android.graphics.PointF;

public class Stroke {
	
	List<PointF> mPoints;
	
	public Stroke()
	{
		mPoints = new LinkedList<PointF>();
	}
	
	public Stroke(float startX, float startY)
	{
		this();
		addPoint(startX, startY);
	}
	
	public Stroke(PointF startP)
	{
		this();
		addPoint(startP.x, startP.y);
	}
	
	public int getNumSamples()
	{
		return mPoints.size();
	}
	
	public List<PointF> getSamplePoints()
	{
		List<PointF> samples = new ArrayList<PointF>();
		for(PointF p : mPoints)
		{
			PointF p1 = new PointF(p.x, p.y);
			samples.add(p1);
		}
		return samples;
	}
	
	public void addPoint(float x, float y)
	{
		mPoints.add(new PointF(x, y));
	}
	
	public void addPoint(PointF p)
	{
		addPoint(p.x, p.y);
	}
	
	public Path toPath()
	{
		Path path = new Path();
		if(mPoints.size() <= 0) 
		{
			return path;
		}
		else if(mPoints.size() == 1) 
		{
			PointF p = mPoints.get(0);
			path.moveTo(p.x, p.y);
		}
		Iterator<PointF> iter = mPoints.iterator();
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
		
		return path;
	}
	
}
