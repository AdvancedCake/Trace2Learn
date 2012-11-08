/**
 * 
 */
package edu.upenn.cis573.Trace2Win;

import java.util.List;

import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.Database.Stroke;
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
	
	public CharacterViewPane(Context context) {
		super(context);
		_paint = new Paint();
		_paint.setAntiAlias(true);
		_paint.setDither(true);
		_paint.setColor(0xFFFF0000);
		_paint.setStyle(Paint.Style.STROKE);
		_paint.setStrokeJoin(Paint.Join.ROUND);
		_paint.setStrokeCap(Paint.Cap.ROUND);
		_paint.setStrokeWidth(12);
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
