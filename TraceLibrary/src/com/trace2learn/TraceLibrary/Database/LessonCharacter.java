package com.trace2learn.TraceLibrary.Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

public class LessonCharacter extends LessonItem {

	private List<Stroke> _strokes;
	
	public LessonCharacter() {
		_type = ItemType.CHARACTER;
		_strokes = new ArrayList<Stroke>();
		_stringid = null;
	}
	
	public LessonCharacter(String id) {
		this();
		_stringid = id;
	}
	
	public synchronized void addStroke(Stroke stroke) {
		_strokes.add(stroke);
	}
	
	public synchronized List<Stroke> getStrokes() {
		List<Stroke> l = new ArrayList<Stroke>();
		l.addAll(_strokes);
		return l;
	}
	
	public synchronized Stroke getStroke(int i) {
		return _strokes.get(i);
	}
	
	/**
	 * removes the known stroke for the character
	 * @param stroke - The stroke to be removed from the character
	 * @return - true if the stroke was removed, false otherwise
	 */
	public synchronized boolean removeStroke(Stroke stroke) {
		return _strokes.remove(stroke);
	}
	
	/**
	 * removes the ith stroke of the character
	 * @param i - the index of the stroke to be removed
	 * @return - the stroke that was removed from the character
	 */
	public synchronized Stroke removeStroke(int i) {
		return _strokes.remove(i);
	}
	
	public synchronized Stroke removeLastStroke() {
	    if (_strokes.size() == 0) {
	        return null;
	    }
	    return _strokes.remove(_strokes.size() - 1);
	}
	
	/**
	 * removes all of the strokes from the character
	 */
	public synchronized void clearStrokes()	{
		_strokes.clear();
	}

	/**
	 * Moves the stroke to a new position in the stroke order.
	 * The other strokes will be shifted as appropriate 
	 * 
	 * @param oldIndex - the original ordering of the stroke
	 * @param newIndex - the new position of the stroke in the order
	 */
	public synchronized void reorderStroke(int oldIndex, int newIndex) {
		if (oldIndex < 0 || oldIndex >= _strokes.size()) {
			throw new IndexOutOfBoundsException("Index: " + oldIndex + " is not within the bounds of a " + _strokes.size() + " length list");
		}
		if (newIndex < 0 || newIndex >= _strokes.size()) {
			throw new IndexOutOfBoundsException("Index: " + newIndex + " is not within the bounds of a " + _strokes.size() + " length list");
		}
		
		Stroke movedStroke = _strokes.get(oldIndex);
		
		if (oldIndex < newIndex) {
			for(int i = oldIndex; i + 1 <= newIndex; i++)
			{
				_strokes.set(i, _strokes.get(i+1));
			}
			_strokes.set(newIndex, movedStroke);
		}
		else if (oldIndex > newIndex) {
			for(int i = oldIndex; i - 1 >= newIndex; i--)
			{
				_strokes.set(i, _strokes.get(i-1));
			}
			_strokes.set(newIndex, movedStroke);
		}
		
	}

	public synchronized int getNumStrokes() {
		return _strokes.size();
	}
	
	/**
	 * Draws the item in the canvas provided, using the provided paint brush
	 * within the provided bounding box
	 * @param canvas - the canvas to draw on
	 * @param paint - the drawing settings for the item
	 * @param left - the left bound in which the item should be drawn
	 * @param top - the top bound in which the item should be drawn
	 * @param width - the width of the bounding box in which the item should be drawn
	 * @param height - the height of the bounding box in which the item should be drawn
	 */
	@Override
	public void draw(Canvas canvas, Paint paint, float left, float top, float width, float height) {
		Matrix matrix = new Matrix();
		Log.i("DRAW", "Scale: " + width + " " + height);
		Log.i("DRAW", "Strokes: " + _strokes.size());
		matrix.postScale(width, height);
		matrix.postTranslate(left,  top);
		List<Stroke> strokes = getStrokes();
		for(Stroke stroke : strokes)
		{
			Path path = stroke.toPath(matrix);
			canvas.drawPath(path, paint);
		}
	}
	
	/**
	 * Draws the item in the canvas provided, using the provided paint brush
	 * within the provided bounding box
	 * The time is a normalized step from 0 to 1, 0 being not shown at all
	 * and 1 being completely drawn.
	 * @param canvas - the canvas to draw on
	 * @param paint - the drawing settings for the item
	 * @param left - the left bound in which the item should be drawn
	 * @param top - the top bound in which the item should be drawn
	 * @param width - the width of the bounding box in which the item should be drawn
	 * @param height - the height of the bounding box in which the item should be drawn
	 * @param time - the time in the animation from 0 to 1
	 */
	@Override
	public void draw(Canvas canvas, Paint paint, float left, float top, float width, float height, float time) {
		Matrix matrix = new Matrix();
		Log.i("DRAW", "Scale: " + width + " " + height);
		Log.i("DRAW", "Strokes: " + _strokes.size());
		Log.i("DRAW", "Time: " + time);
		matrix.postScale(width, height);
		matrix.postTranslate(left,  top);
		
		List<Stroke> strokes = getStrokes();
		
		float strokeTime = 1F/strokes.size();
		float coveredTime = 0;
		for(Stroke stroke : strokes)
		{
			if(coveredTime > time) break;
			float sTime = time - coveredTime;
			if(sTime > strokeTime) sTime = strokeTime;
			Path path = stroke.toPath(matrix, sTime/strokeTime);
			canvas.drawPath(path, paint);
			coveredTime += strokeTime;
		}
	}
	
	/**
	 * Creates an XML representation of this character.
	 * @return an XML string
	 */
	public String toXml() {
	    String xml = "<character id=\"" + _stringid + "\">\n";
	    
	    for (String tag : _tags) {
	        xml += "<tag tag=\"" + tag + "\" />\n";
	    }
	    
	    for (Map.Entry<String, String> entry : keyValues.entrySet()) {
	        xml += "<id key=\"" + entry.getKey() + "\" value=\"" +
	                entry.getValue() + "\" />\n";
	    }
	    
	    int numStrokes = _strokes.size();
	    for (int i = 0; i < numStrokes; i++) {
	        xml += _strokes.get(i).toXml(i);
	    }
	    
	    xml += "</character>\n";
	    
	    return xml;
	}
	
	/**
	 * Converts a parsed XML element to a LessonCharacter
	 * @param elem XML DOM element where the root node is <character>
	 * @return the LessonCharacter represented by the XML element, or null if
	 * there was an error
	 */
	public static LessonCharacter importFromXml(Element elem) {
        try {
            if (!elem.getNodeName().equals("character")) { return null; }
            
            String id = elem.getAttribute("id");
            Log.i("Import Character", "id: " + id);
            
            LessonCharacter c = new LessonCharacter(id);
            
            NodeList tags = elem.getElementsByTagName("tag");
            for (int i = 0; i < tags.getLength(); i++) {
                String tag = ((Element) tags.item(i)).getAttribute("tag");
                c.addTag(tag);
                Log.i("Import Character", "  tag: " + tag);
            }
            
            NodeList ids = elem.getElementsByTagName("id");
            for (int i = 0; i < ids.getLength(); i++) {
                String key = ((Element) ids.item(i)).getAttribute("key");
                String value = ((Element) ids.item(i)).getAttribute("value");
                c.addKeyValue(key, value);
                Log.i("Import Character", "  key: " + key + ", value: " + value);
            }
            
            NodeList strokes = elem.getElementsByTagName("stroke");
            Stroke[] strokeArr = new Stroke[strokes.getLength()];
            for (int i = 0; i < strokes.getLength(); i++) {
                Element strokeElem = (Element) strokes.item(i);
                int position = Integer.parseInt(
                        strokeElem.getAttribute("position"));
                strokeArr[position] = Stroke.importFromXml(strokeElem);
            }
            c._strokes = new ArrayList<Stroke>(Arrays.asList(strokeArr));            
            
            return c;
        } catch (Exception e) {
            Log.e("Import Char", e.getMessage());
            return null;
        }
	}
	
}
