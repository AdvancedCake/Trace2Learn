package com.trace2learn.TraceLibrary.Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.trace2learn.TraceLibrary.Toolbox;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class LessonWord extends LessonItem {
	
	private List<String> _characters;
	private int lessonOrder;
	
	public LessonWord() {
        this(null);
	}
	
	public LessonWord(String id) {
        _type       = ItemType.WORD;
        _characters = new ArrayList<String>();
		_stringid   = id;
	}
	
    protected synchronized void initialize() {
        // Not needed unless we ever pull lessons without associated details
        initialized = true;
    }
	
	//takes the id of a character, adds the id to internal characterid list
	public void addCharacter(String charId){
		_characters.add(charId);
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
	public List<LessonCharacter> getCharacters() {
		ArrayList<LessonCharacter> chars = new ArrayList<LessonCharacter>(_characters.size());
		for(String id : _characters) {
		    LessonCharacter ch = Toolbox.dba.getCharacterById(id);
		    chars.add(ch);
		}
		return chars;
	}
	
	public int length()	{
		return _characters.size();
	}
	
	public boolean removeCharacter(String character) {
		return _characters.remove(character);
	}
	
	public String removeCharacter(int i) {
		return _characters.remove(i);
	}
	
	public String removeLastCharacter() {
	    return _characters.remove(_characters.size() - 1);
	}
	
	public void clearCharacters() {
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
	public void draw(Canvas canvas, Paint paint, float left, float top, float width, float height) {
		int i = 0;
		float charWidth = width/length();
		for(String id : _characters) {
			LessonCharacter character = Toolbox.dba.getCharacterById(id);
			character.draw(canvas, paint, left + charWidth*i, top, charWidth, height);
			i++;
		}
	}
	
	public int getLessonOrder() {
	    return lessonOrder;
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
	        xml += "<tag tag=\"" + Toolbox.xmlEncode(tag) + "\" />\n";
	    }

	    for (Map.Entry<String, String> entry : keyValues.entrySet()) {
	        xml += "<id key=\"" + Toolbox.xmlEncode(entry.getKey()) + "\" value=\"" +
	                Toolbox.xmlEncode(entry.getValue()) + "\" />\n";
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
            String id    = elem.getAttribute("id");
            String order = elem.getAttribute("position");
            
            LessonWord w = new LessonWord(id);
            try {
                w.lessonOrder = Integer.parseInt(order);
            } catch(NumberFormatException e) {
                Log.e("LessonWord.importFromXml",
                        "Bad position attribute: -" + order + "-");
            }
            
            NodeList tags = elem.getElementsByTagName("tag");
            for (int i = 0; i < tags.getLength(); i++) {
                String tag = ((Element) tags.item(i)).getAttribute("tag");
                tag = Toolbox.xmlDecode(tag);
                w.addTag(tag);
            }
            
            NodeList ids = elem.getElementsByTagName("id");
            for (int i = 0; i < ids.getLength(); i++) {
                String key = ((Element) ids.item(i)).getAttribute("key");
                String value = ((Element) ids.item(i)).getAttribute("value");
                key = Toolbox.xmlDecode(key);
                value = Toolbox.xmlDecode(value);
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
            Log.e("LessonWord.importFromXml", e.getMessage());
            return null;
        }
	}

}