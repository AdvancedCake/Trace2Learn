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
	
	public synchronized List<Long> getCharacters(){
		return new ArrayList<Long>(_characters);
	}
	
	public synchronized long getCharacter(int i){
		return _characters.get(i).longValue();
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
		// TODO determine how LessonWords should be shown
	}
	
	
}