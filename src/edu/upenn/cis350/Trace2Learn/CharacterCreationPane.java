package edu.upenn.cis350.Trace2Learn;

import edu.upenn.cis350.Trace2Learn.Characters.LessonCharacter;
import edu.upenn.cis350.Trace2Learn.Characters.Stroke;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class CharacterCreationPane extends DrawingPane {

	private LessonCharacter _character;
	private Stroke _currentStroke;
	
	public CharacterCreationPane(Context c, Paint paint) {
		super(c, paint);

		_character = new LessonCharacter();
	}

	@Override
	protected void beginStroke(float newX, float newY) {
		_currentStroke = new Stroke(newX, newY);
	}
	
	@Override
	protected void updateStroke(float newX, float newY)
	{
		_currentStroke.addPoint(newX, newY);
	}

	@Override
	protected void completeStroke(float newX, float newY) {
		// consider drawing just one stroke
		_currentStroke.addPoint(newX, newY);
		_character.addStroke(_currentStroke);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(_backgroundColor);

		// Consider using a bitmap buffer so only new strokes are drawn.
		drawCharacter(canvas, _character);
	}
	
}
