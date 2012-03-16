package edu.upenn.cis350.Trace2Learn.Characters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.upenn.cis350.Trace2Learn.Characters.LessonItem.ItemType;

public class LessonWord extends LessonItem {
	
	private List<LessonCharacter> _characters;
	
	private long _id;
	
	public LessonWord(){
		_type = ItemType.WORD;
		_characters = new ArrayList<LessonCharacter>();
	}
	
	public void setId(long id)
	{
		_id = id;
	}
	
	public long getId()
	{
		return _id;
	}
	
	public synchronized void addCharacter(LessonCharacter character){
		_characters.add(character);
	}
	
	public synchronized List<LessonCharacter> getCharacters(){
		return new ArrayList<LessonCharacter>(_characters);
	}
	
	public synchronized LessonCharacter getCharacter(int i){
		return _characters.get(i);
	}
	
	public synchronized boolean removeCharacter(LessonCharacter character){
		return _characters.remove(character);
	}
	
	public synchronized LessonCharacter removeCharacter(int i){
		return _characters.remove(i);
	}
	
	public synchronized void clearCharacters(){
		_characters.clear();
	}
	
}