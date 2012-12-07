package edu.upenn.cis573.Trace2Win.library;

import android.content.Context;
import android.graphics.Canvas;
import edu.upenn.cis573.Trace2Win.library.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.library.Database.Stroke;

public class CharacterCreationPane extends DrawingPane {

	protected LessonCharacter _character;
	private Stroke _currentStroke;
	
	public CharacterCreationPane(Context c) {
		super(c);

		_character = new LessonCharacter();
		
	}

	/**
	 * The method that is called every time a new stroke begins
	 * This is where the creation of a new stroke should be handled
	 * @param newX - the x coordinate where the stroke begins
	 * @param newY - the y coordinate where the stroke ends
	 */
	@Override
	protected void beginStroke(float newX, float newY) {
		_currentStroke = new Stroke(newX, newY);
		_character.addStroke(_currentStroke);
	}
	
	/**
	 * The method that is called every time a new point is sampled for the current stroke
	 * @param newX - the x coordinate of the sample point
	 * @param newY - the y coordinate of the sample point
	 */
	@Override
	protected void updateStroke(float newX, float newY)
	{
		_currentStroke.addPoint(newX, newY);
	}

	/**
	 * The method that is called every when the stroke is completed
	 * This is where the completion of a stroke should be handled
	 * @param newX - the x coordinate of the sample point
	 * @param newY - the y coordinate of the sample point
	 */
	@Override
	protected void completeStroke(float newX, float newY) {
		// consider drawing just one stroke
		_currentStroke.addPoint(newX, newY);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(_backgroundColor);

		// Consider using a bitmap buffer so only new strokes are drawn.
		drawCharacter(canvas, _character);
	}
	
	/**
	 * Returns a copy on the drawn character
	 * We make a copy so that we do not inadvertently make changes to a saved
	 * character if the pane is revisited
	 * @return a copy of the on screen character
	 */
	public LessonCharacter getCharacter()
	{
		return _character;
	}
	
	public void setCharacter(LessonCharacter character)
	{
		_character = character;
		invalidate();
	}
	
	public void clearPane()
	{
		_character = new LessonCharacter();
		invalidate();
	}
	
}
