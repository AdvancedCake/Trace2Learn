package edu.upenn.cis573.Trace2Win;

import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class CharacterDisplayPane extends CharacterViewPane {

	protected LessonCharacter _character;
	
	public CharacterDisplayPane(Context context) {
		super(context);
	}
	
	public void setCharacter(LessonCharacter character)
	{
		_character = character;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(_backgroundColor);

		if(_character != null)
		{
			drawCharacter(canvas, _character);
		}
	}
	
	public void clearPane()
	{
		_character = null;
	}

}
