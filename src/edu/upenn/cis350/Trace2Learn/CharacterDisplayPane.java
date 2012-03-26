package edu.upenn.cis350.Trace2Learn;

import java.util.List;

import edu.upenn.cis350.Trace2Learn.Database.LessonCharacter;
import edu.upenn.cis350.Trace2Learn.Database.Stroke;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class CharacterDisplayPane extends CharacterViewPane {

	protected LessonCharacter _character;
	
	public CharacterDisplayPane(Context context, Paint paint) {
		super(context, paint);
	}
	
	public void setCharacter(LessonCharacter character)
	{
		_character = character;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(_backgroundColor);

		// Consider using a bitmap buffer so only new strokes are drawn.
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
