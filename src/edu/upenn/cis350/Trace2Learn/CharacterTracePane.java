/**
 * 
 */
package edu.upenn.cis350.Trace2Learn;

import edu.upenn.cis350.Trace2Learn.Database.LessonCharacter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author Ryan
 *
 */
public class CharacterTracePane extends CharacterCreationPane {

	protected LessonCharacter _template;
	
	protected Paint _style;
	
	public CharacterTracePane(Context context) {
		super(context);
		_style = new Paint();
		_style.setColor(Color.BLUE);
		_style.setAntiAlias(true);
		_style.setDither(true);
		_style.setStyle(Paint.Style.STROKE);
		_style.setStrokeJoin(Paint.Join.ROUND);
		_style.setStrokeCap(Paint.Cap.ROUND);
		_style.setStrokeWidth(32);
	}

	public void setTemplate(LessonCharacter template)
	{
		_template = template;
	}
	
	public void onDraw(Canvas canvas)
	{	
		canvas.drawColor(_backgroundColor);
		_template.draw(canvas, _style);
		// Consider using a bitmap buffer so only new strokes are drawn.
		drawCharacter(canvas, _character);
	}
	
}
