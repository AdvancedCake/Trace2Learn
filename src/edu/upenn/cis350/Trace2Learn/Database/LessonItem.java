package edu.upenn.cis350.Trace2Learn.Database;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public abstract class LessonItem {
	
	protected List<String> _tags;
	protected long _id;
	protected String private_tag;
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
	
	public void setDatabase(DbAdapter db)
	{
		_db = db;
	}
	
	public long getId()
	{
		return _id;
	}
	
	public void setPrivateTag(String tag){
		private_tag = tag;
	}
	
	public String getPrivateTag(){
		return private_tag;
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
	
	public void draw(Canvas canvas)
	{
		Paint paint = buildPaint(canvas.getHeight());
        
        draw(canvas, paint);
	}
	
	public void draw(Canvas canvas, float time)
	{
		Paint paint = buildPaint(canvas.getHeight());
        
        draw(canvas, paint, time);
	}
	
	public void draw(Canvas canvas, Paint paint)
	{
		Rect bounds = canvas.getClipBounds();
		draw(canvas, paint, bounds.left, bounds.top, bounds.width(), bounds.height());
	}
	
	public void draw(Canvas canvas, Paint paint, float time)
	{
		Rect bounds = canvas.getClipBounds();
		draw(canvas, paint, bounds.left, bounds.top, bounds.width(), bounds.height(), time);
	}
	
	public void draw(Canvas canvas, float left, float top, float width, float height, float time)
	{
		Paint paint = buildPaint(height);
        
        draw(canvas, paint, left, top, width, height, 1);
	}
	
	public void draw(Canvas canvas, float left, float top, float width, float height)
	{
		Paint paint = buildPaint(height);
        
        draw(canvas, paint, left, top, width, height);
	}
	
	private static final float _heightToStroke = 12F/400F;
	
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
	
	public void draw(Canvas canvas, Paint paint, float left, float top, float width, float height)
	{
		draw(canvas, paint, left, top, width, height, 1F);
	}
	
	public abstract void draw(Canvas canvas, Paint paint, float left, float top, float width, float height, float time);
}
