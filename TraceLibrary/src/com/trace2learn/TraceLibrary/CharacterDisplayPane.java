package com.trace2learn.TraceLibrary;

import com.trace2learn.TraceLibrary.Database.LessonCharacter;

import android.content.Context;
import android.graphics.Canvas;

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
