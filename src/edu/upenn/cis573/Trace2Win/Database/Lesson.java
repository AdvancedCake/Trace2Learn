package edu.upenn.cis573.Trace2Win.Database;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Lesson extends LessonItem {
	
	private List<Long> _words; // list of word IDs
	private String name; // lesson name

	public Lesson(){
		_type = ItemType.LESSON;
		_words = new ArrayList<Long>();
	}

	public synchronized void addWord(Long word){
		_words.add(word);
	}

	public synchronized List<Long> getWordIds(){
		return new ArrayList<Long>(_words);
	}

	public synchronized long getWordId(int i){
		return _words.get(i).longValue();
	}
	
	public String getLessonName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	/**
	 * Get the list of items that compose this lesson
	 * @return the list of characters that compose this word
	 */
	public synchronized List<LessonItem> getWords()
	{
		ArrayList<LessonItem> words = new ArrayList<LessonItem>(_words.size());
		for(Long id : _words)
		{
			if(_db == null)
			{
				words.add(new LessonWord());
			}
			else
			{
				LessonWord word = _db.getWordById(id);
				words.add(word);
			}

		}
		return words;
	}

	public int length()
	{
		return _words.size();
	}

	public synchronized boolean removeWord(Long word){
		return _words.remove(word);
	}

	public synchronized long removeWord(int i){
		return _words.remove(i).longValue();
	}

	public synchronized void clearWords(){
		_words.clear();
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
	public void draw(Canvas canvas, Paint paint, float left, float top, float width, float height, float time)
	{
		draw(canvas, paint, left, top, width, height);
	}

	@Override
	protected boolean updateTypeData() {
		// TODO Auto-generated method stub
		return false;
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
	public void draw(Canvas canvas, Paint paint, float left, float top, float width, float height)
	{
		// TODO
		/*int i = 0;
		float charWidth = width/length();
		for(Long id : _phrases)
		{
			LessonWord word;
			if(_db == null)
			{
				word = new LessonWord(id);
			}
			else
			{
				word = _db.getCharacterById(id);
			}
			word.draw(canvas, paint, left + charWidth*i, top, charWidth, height);
			i++;
		}*/
	}

    @Override
    public String toXml() {
        // TODO delete me!
        return null;
    }


}