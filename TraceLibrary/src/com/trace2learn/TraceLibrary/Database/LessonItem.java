package com.trace2learn.TraceLibrary.Database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.trace2learn.TraceLibrary.Toolbox;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;


public abstract class LessonItem implements Comparable<LessonItem> {
	
	/** The tag cache*/
	protected List<String> _tags;
	
	/** The (Key, Value) cache */
	protected LinkedHashMap<String, String> keyValues;
		
	/** The stringid of the item */
	protected String _stringid;
	
	/** The sort order of the item */
	protected long _sort;
	
	/** Reference to the database in which the item is stored */
	protected DbAdapter _db;
	
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
		_stringid = null;
		_tags = new ArrayList<String>();
		keyValues = new LinkedHashMap<String, String>();
	}
	
    /**
     * Comparison used for determining the display order of the LessonItems in
     * a list.
     */
    public int compareTo(LessonItem other) {
        if (this._type != other._type) {
            return other._type.ordinal() - this._type.ordinal();
        }
        
        if (this._type == ItemType.LESSON) {
            return ((Lesson) this).compareTo((Lesson) other);
        }
        
        return Double.compare(this._sort, other._sort);
    }
	
	public ItemType getItemType()
	{
		return _type;
	}
	
	public void setStringId(String id)
	{
		_stringid = id;
	}
	public void setDatabase(DbAdapter db)
	{
		_db = db;
	}
	
	public String getStringId()
	{
		return _stringid;
	}
	
	public void setTagList(List<String> tags){
		_tags.addAll(tags);
	}
	
	public void setKeyValues(Map<String, String> kv){
		keyValues.putAll(kv);
	}	
	
    public void setSort(long sort) {
        _sort = sort;
    }
    
    public long getSort() {
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
			_tags = db.getCharacterTags(_stringid);
			break;
		case WORD:
			_tags = db.getWordTags(_stringid);
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
			keyValues = db.getKeyValues(_stringid, _type);
			break;
		case LESSON:
			Log.e("Update IDs", "(Key, Value) pairs are NOT supported for LESSON");
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
	
	public synchronized boolean hasKey(String key) {
	    return keyValues.containsKey(key);
	}
	
	public synchronized boolean hasKeyValue(String key, String value)
	{
		return keyValues.containsKey(key) && keyValues.containsValue(value);
	}
	
	public synchronized void addKeyValue(String key, String value)
	{
		keyValues.put(key, value);
	}	
	
	public synchronized LinkedHashMap<String, String> getKeyValues()
	{
		return new LinkedHashMap<String, String>(keyValues);
	}	
	
	public synchronized String getKeyValuesToString() {
		StringBuilder sb = new StringBuilder();

		if (keyValues.containsKey(Toolbox.PINYIN_KEY)) {
		    sb.append("(" + keyValues.get(Toolbox.PINYIN_KEY) + ")");
		}
		
		for (Map.Entry<String, String> entry : keyValues.entrySet()) {
		    if (!entry.getKey().equals(Toolbox.PINYIN_KEY)) {
	            sb.append(", " + entry.getKey() + ": " + entry.getValue());    
		    }
		}
		
		if (sb.length() > 0 && sb.charAt(0) == ',') {
		    return sb.substring(2);
		}
		return sb.toString();
	}
	
	public synchronized String getValue(String key) {
	    return keyValues.get(key);
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
