package edu.upenn.cis350.Trace2Learn.Characters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.upenn.cis350.Trace2Learn.Characters.LessonItem.ItemType;

public class LessonWord extends LessonItem {
	
	private List<Integer> _characters;
	
	public LessonWord(){
		_type = ItemType.WORD;
		_characters = new ArrayList<Integer>();
	}
	
	public synchronized void addCharacter(Integer character){
		_characters.add(character);
	}
	
	public synchronized List<Integer> getCharacters(){
		return new ArrayList<Integer>(_characters);
	}
	
	public synchronized int getCharacter(int i){
		return _characters.get(i).intValue();
	}
	
	public synchronized boolean removeCharacter(Integer character){
		return _characters.remove(character);
	}
	
	public synchronized int removeCharacter(int i){
		return _characters.remove(i).intValue();
	}
	
	public synchronized void clearCharacters(){
		_characters.clear();
	}
	
}