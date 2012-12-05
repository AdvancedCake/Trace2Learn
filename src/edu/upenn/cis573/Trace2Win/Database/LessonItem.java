package edu.upenn.cis573.Trace2Win.Database;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;


public abstract class LessonItem implements Comparable<LessonItem> {
	
	private final String logTAG = "LessonItem";
	
	/** The tag cache*/
	protected List<String> _tags;
	
	/** The (Key, Value) cache */
	protected LinkedHashMap<String, String> _keyValues;
	
	/** The id of the item */
	protected long _id; // TODO remove this when string IDs are working.
	
	/** The stringid of the item */
	protected String _stringid;
	
	/** The sort order of the item */
	protected double _sort;
	
	/** Reference to the database in which the item is stored */
	protected DbAdapter _db;
	
	/** The last time the item was synched with the database */
	protected Date _lastUpdate;
	
	/** Identifier for type of character **/
	protected ItemType _type;
	
	public enum ItemType
	{
		CHARACTER,
		WORD,
		LESSON
	}
	
	protected LessonItem()
	{
		_id = -1;
		_stringid = null;
		_tags = new ArrayList<String>();
		_keyValues = new LinkedHashMap<String, String>();
		
		_lastUpdate = new Date(0);
	}
	
    /**
     * Comparison used for determining the display order of the LessonItems in
     * a list.
     */
    public int compareTo(LessonItem other) {
        return Double.compare(this._sort, other._sort);
    }
	
	/**
	 * Synchs the LessonItem to match the database
	 * TODO: Uses a timestamp to check for any new updates that need to be
	 * pulled from the database
	 * @return True - if the item was updated
	 * 		   False - otherwise
	 */
	protected boolean update()
	{
		if(_db == null || _id < 0) return false;
		if(updateTypeData())
		{
			_lastUpdate = new Date();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Synchs the type specific data from the database
	 * TODO: Uses a timestamp to check for any new updates that need to be
	 * pulled from the database
	 * @return True - if the item was updated
	 * 		   False - otherwise
	 */
	protected abstract boolean updateTypeData();
	
	public ItemType getItemType()
	{
		return _type;
	}
	
	public void setId(long id)
	{
		_id = id;
	}
	public void setStringId(String id)
	{
		_stringid = id;
	}
	public void setDatabase(DbAdapter db)
	{
		_db = db;
	}
	
	public long getId()
	{
		return _id;
	}
	
	public String getStringId()
	{
		return _stringid;
	}
	
	public void setTagList(List<String> tags){
		_tags.addAll(tags);
	}
	
	public void setKeyValues(LinkedHashMap<String, String> keyValues){
		_keyValues.putAll(keyValues);
	}	
	
    public void setSort(double sort) {
        _sort = sort;
    }
    
    public double getSort() {
        return _sort;
    }
    
	/**
	 * Updates the LessonItem's contents to match those in the database
	 */
	public synchronized void update(DbAdapter db)
	{
		updateTags(db);
	}
	
	/**
	 * Updates the LessonItem's tags to represent those in the database
	 */
	protected synchronized void updateTags(DbAdapter db)
	{
		switch(_type)
		{
		case CHARACTER:
			_tags = db.getCharacterTags(_id);
			break;
		case WORD:
			_tags = db.getWordTags(_id);
			break;
		case LESSON:
			_tags = db.getLessonTags(_stringid);
			break;
		}
	}

	/**
	 * Updates the LessonItem's (key, value) pairs to represent those in the database
	 */
	protected synchronized void updateKeyValues(DbAdapter db)
	{
		switch(_type)
		{
		case CHARACTER:
		case WORD:
			_keyValues = db.getKeyValues(_id, _type);
			break;
		case LESSON:
			Log.e(logTAG, "(Key, Value) pairs are NOT supported for LESSON");
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
	
	public synchronized String getTagsToString()
	{
		StringBuilder sb = new StringBuilder();
		for(String tag : _tags){
			sb.append(", "+tag);
		}
		return new String(sb.length()>0 ? sb.substring(2) : "");
	}
	
	public synchronized boolean hasKeyValue(String key, String value)
	{
		return _keyValues.containsKey(key) && _keyValues.containsValue(value);
	}
	
	public synchronized void addKeyValue(String key, String value)
	{
		_keyValues.put(key, value);
	}	
	
	public synchronized LinkedHashMap<String, String> getKeyValues()
	{
		return new LinkedHashMap<String, String>(_keyValues);
	}	
	
	public synchronized String getKeyValuesToString()
	{
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : _keyValues.entrySet()) {
			sb.append(", " + entry.getKey() + ": " + entry.getValue());
		}    	
		return new String(sb.length()>0 ? sb.substring(2) : "");
	}
	
	/** 
	 * The ratio for determining how large a stroke should be given the size
	 * of the canvas
	 */
	private static final float _heightToStroke = 8F/400F;
	
	/**
	 * Configures the paint options given the size of the canvas
	 * @param height - the height of the canvas on which the paint options will
	 * 				   be used
	 * @return The configured paint options
	 */
	private Paint buildPaint(float height) {
		
		Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(0xFFFF0000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(height*_heightToStroke);
        
        return paint;
	}	
	
	/**
	 * Draws the item in the canvas provided
	 * @param canvas
	 */
	public void draw(Canvas canvas)
	{
		Paint paint = buildPaint(canvas.getHeight());
        
        draw(canvas, paint);
	}
	
	/**
	 * Draws the item in the canvas provided in a animation percentage
	 * The time is a normalized step from 0 to 1, 0 being not shown at all
	 * and 1 being completely drawn.
	 * @param canvas - the canvas to draw on
	 * @param time - the time in the animation from 0 to 1
	 */
	public void draw(Canvas canvas, float time)
	{
		Paint paint = buildPaint(canvas.getHeight());
        
        draw(canvas, paint, time);
	}
	
	/**
	 * Draws the item in the canvas provided, using the provided paint brush
	 * @param canvas - the canvas to draw on
	 * @param paint - the drawing settings for the item
	 */
	public void draw(Canvas canvas, Paint paint)
	{
		Rect bounds = canvas.getClipBounds();
		draw(canvas, paint, bounds.left, bounds.top, bounds.width(), bounds.height());
	}
	
	/**
	 * Draws the item in the canvas provided in a animation percentage, using the provided paint brush
	 * The time is a normalized step from 0 to 1, 0 being not shown at all
	 * and 1 being completely drawn.
	 * @param canvas - the canvas to draw on
	 * @param paint - the drawing settings for the item
	 * @param time - the time in the animation from 0 to 1
	 */
	public void draw(Canvas canvas, Paint paint, float time)
	{
		Rect bounds = canvas.getClipBounds();
		draw(canvas, paint, bounds.left, bounds.top, bounds.width(), bounds.height(), time);
	}
	
	/**
	 * Draws the item in the canvas provided within the provided bounding box
	 * The time is a normalized step from 0 to 1, 0 being not shown at all
	 * and 1 being completely drawn.
	 * @param canvas - the canvas to draw on
	 * @param left - the left bound in which the item should be drawn
	 * @param top - the top bound in which the item should be drawn
	 * @param width - the width of the bounding box in which the item should be drawn
	 * @param height - the height of the bounding box in which the item should be drawn
	 * @param time - the time in the animation from 0 to 1
	 */
	public void draw(Canvas canvas, float left, float top, float width, float height, float time)
	{
		Paint paint = buildPaint(height);
        
        draw(canvas, paint, left, top, width, height, 1);
	}
	
	/**
	 * Draws the item in the canvas provided within the provided bounding box
	 * @param canvas - the canvas to draw on
	 * @param left - the left bound in which the item should be drawn
	 * @param top - the top bound in which the item should be drawn
	 * @param width - the width of the bounding box in which the item should be drawn
	 * @param height - the height of the bounding box in which the item should be drawn
	 */
	public void draw(Canvas canvas, float left, float top, float width, float height)
	{
		Paint paint = buildPaint(height);
        
        draw(canvas, paint, left, top, width, height);
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
	public void draw(Canvas canvas, Paint paint, float left, float top, float width, float height)
	{
		draw(canvas, paint, left, top, width, height, 1F);
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
	public abstract void draw(Canvas canvas, Paint paint, float left, float top, float width, float height, float time);
	
	public abstract String toXml();

}
