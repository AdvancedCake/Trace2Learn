package edu.upenn.cis350.Trace2Learn.Characters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonCharacter {

	private List<Stroke> _strokes;
	
	Map<String, String> _tags;
	
	public LessonCharacter()
	{
		_tags = new HashMap<String, String>();
		_strokes = new ArrayList<Stroke>();
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
	
	public void removeStroke(Stroke stroke)
	{
		_strokes.remove(stroke);
	}
	
	public void removeStroke(int i)
	{
		_strokes.remove(i);
	}

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
	
	public void setTag(String tag, String value)
	{
		_tags.put(tag, value);
	}
	
	public String getTag(String tag)
	{
		return _tags.get(tag);
	}
	
	public List<String> getTagNames()
	{
		return new ArrayList<String>(_tags.keySet());
	}
	
}
