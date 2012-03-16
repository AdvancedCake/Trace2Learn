package edu.upenn.cis350.Trace2Learn.Characters;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis350.Trace2Learn.DbAdapter;

public abstract class LessonItem {
	
	protected List<String> _tags;
	protected long _id;
	
	public enum ItemType
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
	
	public void setId(long id)
	{
		_id = id;
	}
	
	public long getId()
	{
		return _id;
	}
	
	/**
	 * Updates the LessonItem's tags to represent those in the database
	 */
	public synchronized void updateTags(DbAdapter db)
	{
		switch(_type)
		{
		case CHARACTER:
			_tags = db.getTags(_id);
			break;
		case WORD:
			_tags = db.getWordTags(_id);
			break;
		case LESSON:
			// TODO uncomment when there is lesson support
			//_tags = db.getLessonTags(_id);
			break;
		}
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
