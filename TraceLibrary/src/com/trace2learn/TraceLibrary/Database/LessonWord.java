package com.trace2learn.TraceLibrary.Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class LessonWord extends LessonItem {
	
	private List<String> _characters;
	
	public LessonWord(){
		_type = ItemType.WORD;
		_characters = new ArrayList<String>();
	}
	
	public LessonWord(String id)
	{
		this();
		_stringid = id;
	}		
	
	//takes the id of a character, adds the id to internal characterid list
	public void addCharacter(String character){
		_characters.add(character);
	}
	
	public List<String> getCharacterIds(){
		return new ArrayList<String>(_characters);
	}
	
	public String getCharacterId(int i){
		return _characters.get(i);
	}
	
	/**
	 * Gets a list of the Characters that make up the word
	 * @return
	 */
	public List<LessonCharacter> getCharacters()
	{
		ArrayList<LessonCharacter> chars = new ArrayList<LessonCharacter>(_characters.size());
		for(String id : _characters)
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
	
	public boolean removeCharacter(String character){
		return _characters.remove(character);
	}
	
	public String removeCharacter(int i){
		return _characters.remove(i);
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
		for(String id : _characters)
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
	
	/**
	 * Creates an XML representation of this word.
	 * @return an XML string
	 */
	public String toXml(int word_position) {
		String xml = "";
		if (word_position == -1)
			xml = "<word id=\"" + _stringid + "\">\n";
		else 
			xml = "<word id=\"" + _stringid + "\" position=\"" + word_position + "\">\n";

	    for (String tag : _tags) {
	        xml += "<tag tag=\"" + tag + "\" />\n";
	    }

	    for (Map.Entry<String, String> entry : _keyValues.entrySet()) {
	        xml += "<id key=\"" + entry.getKey() + "\" value=\"" +
	                entry.getValue() + "\" />\n";
	    }

	    int numChars = _characters.size();
	    for (int i = 0; i < numChars; i++) {
	        xml += "<character id=\"" + _characters.get(i) + "\" position=\"" + i + "\" />\n";
	    }

	    xml += "</word>\n";

	    return xml;
	}
	
	public String toXml() {
		return toXml(-1);
	}
	
		
	/**
	 * Converts a parsed XML element to a LessonWord
	 * @param elem XML DOM element
	 * @return the LessonWord represented by the XML element, or null if
	 * there was an error
	 */
	public static LessonWord importFromXml(Element elem) {
        try {
            String id = elem.getAttribute("id");
            
            LessonWord w = new LessonWord();
            w._stringid = id;
            
            NodeList tags = elem.getElementsByTagName("tag");
            for (int i = 0; i < tags.getLength(); i++) {
                String tag = ((Element) tags.item(i)).getAttribute("tag");
                w.addTag(tag);
            }
            
            NodeList ids = elem.getElementsByTagName("id");
            for (int i = 0; i < ids.getLength(); i++) {
                String key = ((Element) ids.item(i)).getAttribute("key");
                String value = ((Element) ids.item(i)).getAttribute("value");
                w.addKeyValue(key, value);
            }
            
            NodeList chars = elem.getElementsByTagName("character");
            String[] charArr = new String[chars.getLength()];
            for (int i = 0; i < chars.getLength(); i++) {
                Element charElem = (Element) chars.item(i);
                int position = Integer.parseInt(charElem.getAttribute("position"));
                String charId = charElem.getAttribute("id");
                charArr[position] = charId;
            }
            w._characters = new ArrayList<String>(Arrays.asList(charArr));

            return w;
        } catch (Exception e) {
            Log.e("Import Word", e.getMessage());
            return null;
        }
	}

	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof LessonWord)) {
	        return false;
	    }

	    return ((LessonWord) other).getStringId() == _stringid;
	}

}