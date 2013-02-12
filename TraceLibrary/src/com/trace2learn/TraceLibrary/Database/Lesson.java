package com.trace2learn.TraceLibrary.Database;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Lesson extends LessonItem {
	
	private List<String> _words; // list of word IDs
	private String name; // lesson name
	private List<LessonWord> wordObjects;

	public Lesson(){
		_type = ItemType.LESSON;
		_words = new ArrayList<String>();
	}
	
	public Lesson(String id)
	{
		this();
		_stringid = id;
	}	

	public synchronized void addWord(String word){
		_words.add(word);
	}

	public synchronized List<String> getWordIds(){
		return new ArrayList<String>(_words);
	}

	public synchronized String getWordId(int i){
		return _words.get(i);
	}
	
	public synchronized int getNumWords() {
	    return _words.size();
	}
	
	public synchronized List<LessonWord> getWordObjects() {
	    return wordObjects;
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
		for(String id : _words)
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

	public synchronized boolean removeWord(String word){
		return _words.remove(word);
	}

	public synchronized String removeWord(int i){
		return _words.remove(i);
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

	/**
	 * Creates an XML representation of this Lesson.
	 * @return an XML string
	 */
	public String toXml() {
		StringBuffer sb = new StringBuffer();
		sb.append("<lesson id=\"").append(_stringid).append("\" ");
		sb.append("name=\"").append(name).append("\">\n");

		synchronized (_words) {
			int word_position = 0;
			for (String word_id : _words) {
				if (_db == null) {
					Log.d("Lesson.toXml()", "_db is null");
					return null;
				} 
				LessonWord w = _db.getWordById(word_id);
				Log.d("Lesson.toXml()", " " + word_id);
				if (w == null) Log.d("Lesson.toXml()", "word is null");
				else sb.append(w.toXml(word_position++));
			}
		}

		sb.append("</lesson>\n");	    	    

		return sb.toString();
	}	
	
	
	/**
	 * Converts a parsed XML element to a Lesson
	 * @param elem XML DOM element
	 * @return the Lesson represented by the XML element, or null if
	 * there was an error
	 */
	public static Lesson importFromXml(Element elem) {
        try {
            String id = elem.getAttribute("id");
            String name = elem.getAttribute("name");

            Log.i("Import Lesson", "id: " + id);
            Log.i("Import Lesson", "  name: " + name);
            
            Lesson lesson = new Lesson();
            lesson.setStringId(id);
            lesson.setName(name);
            
            NodeList words = elem.getElementsByTagName("word");
            lesson.wordObjects = new ArrayList<LessonWord>(words.getLength());
            for (int i = 0; i < words.getLength(); i++) {
                LessonWord word = LessonWord.importFromXml((Element) words.item(i));
                String word_id = word.getStringId();
                lesson.addWord(word_id);
                lesson.wordObjects.add(word);
                Log.i("Import Lesson", "  word: " + word_id);
            }
            
            return lesson;
        } catch (Exception e) {
            Log.e("Import lesson", e.getMessage());
            return null;
        }
	}
	
	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof Lesson)) {
	        return false;
	    }

	    return ((Lesson) other).getStringId().equals(_stringid);
	}
}