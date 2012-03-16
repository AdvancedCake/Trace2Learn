package edu.upenn.cis350.Trace2Learn.Characters;

import java.util.ArrayList;
import java.util.List;

public abstract class LessonItem {
	
	protected List<String> _tags;
	
	enum ItemType
	{
		CHARACTER,
		WORD,
		LESSON
	}
	
	protected ItemType _type;
	
	protected LessonItem()
	{
		_tags = new ArrayList<String>();
	}
	
	public ItemType getItemType()
	{
		return _type;
	}
	
	public synchronized boolean hasTag(String tag)
	{
		return _tags.contains(tag);
	}
	
	public synchronized void addTag(String tag)
	{
		_tags.add(tag);
	}
	
	public synchronized List<String> getTags()
	{
		return new ArrayList<String>(_tags);
	}
}
