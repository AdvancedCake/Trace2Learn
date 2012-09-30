package edu.upenn.cis350.Trace2Learn.Database;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

public class LessonWord extends LessonItem {
	
	private List<Long> _characters;
	
	public LessonWord(){
		_type = ItemType.WORD;
		_characters = new ArrayList<Long>();
	}
	
	public void addCharacter(Long character){
		_characters.add(character);
	}
	
	public List<Long> getCharacterIds(){
		return new ArrayList<Long>(_characters);
	}
	
	public long getCharacterId(int i){
		return _characters.get(i).longValue();
	}
	
	/**
	 * Gets a list of the Characters that make up the word
	 * @return
	 */
	public List<LessonCharacter> getCharacters()
	{
		ArrayList<LessonCharacter> chars = new ArrayList<LessonCharacter>(_characters.size());
		for(Long id : _characters)
		{
			if(_db == null) 
			{
				chars.add(new LessonCharacter(id));
			}
			else
			{
				LessonCharacter ch = _db.getCharacterById(id);
				chars.add(ch);
			}
			
		}
		return chars;
	}
	
	public int length()
	{
		return _characters.size();
	}
	
	public boolean removeCharacter(Long character){
		return _characters.remove(character);
	}
	
	public long removeCharacter(int i){
		return _characters.remove(i).longValue();
	}
	
	public void clearCharacters(){
		_characters.clear();
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
		// TODO add animation code
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
	@Override
	public void draw(Canvas canvas, Paint paint, float left, float top, float width, float height)
	{
		int i = 0;
		float charWidth = width/length();
		for(Long id : _characters)
		{
			LessonCharacter character;
			if(_db == null)
			{
				character = new LessonCharacter(id);
			}
			else
			{
				character = _db.getCharacterById(id);
			}
			character.draw(canvas, paint, left + charWidth*i, top, charWidth, height);
			i++;
		}
	}

	@Override
	protected boolean updateTypeData() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}