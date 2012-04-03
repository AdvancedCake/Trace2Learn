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
	
	public synchronized void addCharacter(Long character){
		_characters.add(character);
	}
	
	public synchronized List<Long> getCharacterIds(){
		return new ArrayList<Long>(_characters);
	}
	
	public synchronized long getCharacterId(int i){
		return _characters.get(i).longValue();
	}
	
	public synchronized List<LessonCharacter> getCharacters()
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
	
	public synchronized boolean removeCharacter(Long character){
		return _characters.remove(character);
	}
	
	public synchronized long removeCharacter(int i){
		return _characters.remove(i).longValue();
	}
	
	public synchronized void clearCharacters(){
		_characters.clear();
	}
	
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
	
	
}