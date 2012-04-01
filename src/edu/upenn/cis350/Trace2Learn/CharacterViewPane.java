/**
 * 
 */
package edu.upenn.cis350.Trace2Learn;

import java.util.List;

import edu.upenn.cis350.Trace2Learn.Database.LessonCharacter;
import edu.upenn.cis350.Trace2Learn.Database.Stroke;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * @author Ryan
 *
 */
public abstract class CharacterViewPane extends View {

	protected Paint _paint;
	
	protected int _backgroundColor = Color.GRAY;
	
	public CharacterViewPane(Context context, Paint paint) {
		super(context);
		_paint = paint;
	}

	/**
	 * Draws the given stroke onto the screen
	 * @param canvas - the canvas on which to draw the stroke
	 * @param stroke - the stroke that should be drawn
	 */
	protected void drawStroke(Canvas canvas, Stroke stroke)
	{
		canvas.drawPath(stroke.toPath(), _paint);
	}
	
	/**
	 * Draws the given character onto the screen
	 * @param canvas - the canvas on which to draw the stroke
	 * @param character - the character that should be drawn
	 */
	protected void drawCharacter(Canvas canvas, LessonCharacter character)
	{
		character.draw(canvas);
		/*List<Stroke> strokes = character.getStrokes();
		for(Stroke s : strokes)
		{
			drawStroke(canvas, s);
		}*/
	}
	
	public abstract void clearPane();
	
}
