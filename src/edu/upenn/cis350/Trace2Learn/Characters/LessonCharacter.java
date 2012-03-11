package edu.upenn.cis350.Trace2Learn.Characters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonCharacter {

	private List<Stroke> _strokes;
	
	private long _id;
	
	private Map<String, Object> _tags;
	
	public LessonCharacter()
	{
		_tags = new HashMap<String, Object>();
		_strokes = new ArrayList<Stroke>();
	}
	
	public LessonCharacter(long id)
	{
		this();
		_id = id;
	}
	
	public void setId(long id)
	{
		_id = id;
	}
	
	public long getId()
	{
		return _id;
	}
	
	public void addStroke(Stroke stroke)
	{
		_strokes.add(stroke);
	}
	
	public List<Stroke> getStrokes()
	{
		List<Stroke> l = new ArrayList<Stroke>();
		l.addAll(_strokes);
		return l;
	}
	
	public Stroke getStroke(int i)
	{
		return _strokes.get(i);
	}
	
	/**
	 * removes the known stroke for the character
	 * @param stroke - The stroke to be removed from the character
	 * @return - true if the stroke was removed, false otherwise
	 */
	public boolean removeStroke(Stroke stroke)
	{
		return _strokes.remove(stroke);
	}
	
	/**
	 * removes the ith stroke of the character
	 * @param i - the index of the stroke to be removed
	 * @return - the stroke that was removed from the character
	 */
	public Stroke removeStroke(int i)
	{
		return _strokes.remove(i);
	}
	
	/**
	 * removes all of the strokes from the character
	 */
	public void clearStrokes()
	{
		_strokes.clear();
	}

	/**
	 * Moves the stroke to a new position in the stroke order.
	 * The other strokes will be shifted as appropriate 
	 * 
	 * @param oldIndex - the original ordering of the stroke
	 * @param newIndex - the new position of the stroke in the order
	 */
	public void reorderStroke(int oldIndex, int newIndex)
	{
		if(oldIndex < 0 || oldIndex >= _strokes.size())
		{
			throw new IndexOutOfBoundsException("Index: " + oldIndex + " is not within the bounds of a " + _strokes.size() + " length list");
		}
		if(newIndex < 0 || newIndex >= _strokes.size())
		{
			throw new IndexOutOfBoundsException("Index: " + newIndex + " is not within the bounds of a " + _strokes.size() + " length list");
		}
		
		Stroke movedStroke = _strokes.get(oldIndex);
		
		if(oldIndex < newIndex)
		{
			for(int i = oldIndex; i + 1 <= newIndex; i++)
			{
				_strokes.set(i, _strokes.get(i+1));
			}
			_strokes.set(newIndex, movedStroke);
		}
		else if(oldIndex > newIndex)
		{
			for(int i = oldIndex; i - 1 >= newIndex; i--)
			{
				_strokes.set(i, _strokes.get(i-1));
			}
			_strokes.set(newIndex, movedStroke);
		}
		
	}
	
	public boolean hasTag(String tag)
	{
		return _tags.containsKey(tag);
	}
	
	public void setTag(String tag, Object value)
	{
		_tags.put(tag, value);
	}
	
	public Object getTag(String tag)
	{
		return _tags.get(tag);
	}
	
	public List<String> getTagNames()
	{
		return new ArrayList<String>(_tags.keySet());
	}
	
}
