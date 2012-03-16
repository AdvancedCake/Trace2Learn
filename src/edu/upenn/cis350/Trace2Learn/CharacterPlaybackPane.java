/**
 * 
 */
package edu.upenn.cis350.Trace2Learn;

import java.util.Timer;

import edu.upenn.cis350.Trace2Learn.Characters.LessonCharacter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;

/**
 * @author Ryan
 * This class displays a character which is animated stroke by stroke.
 */
public class CharacterPlaybackPane extends CharacterDisplayPane {

	protected int _currentStroke = 0;
	protected boolean _animated;
	protected long _lastTick;
	protected int _fps;
	
	protected Thread _refreshTimer;
	protected Handler _handler;
	
	protected boolean _timerOff = true;
	
	public CharacterPlaybackPane(Context context, Paint paint, boolean animated, int fps)
	{
		super(context, paint);
		resetPlayback();
		_animated = animated;
		_fps = fps;
		
		_handler = new Handler();
		
	}
	public CharacterPlaybackPane(Context context, Paint paint, boolean animated)
	{
		this(context, paint, false, 30);
	}
	
	public CharacterPlaybackPane(Context context, Paint paint) {
		this(context, paint, false);
	}
	
	public void setCharacter(LessonCharacter character)
	{
		super.setCharacter(character);
		resetPlayback();
	}
	
	public void startTimer()
	{
		if(_refreshTimer == null)
		{
			_refreshTimer = new Thread()
			{
				Runnable _update = new Runnable() {
					public void run() {
						invalidate();
					}
				};
				
				public void run()
				{
					_timerOff = false;
					while(_animated)
					{
						_handler.post(_update);
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							break;
						}
					}
					_timerOff = true;
				}
			};
			_refreshTimer.start();
		}
	}
	
	public void stopTimer()
	{
		if(_refreshTimer != null)
		{
			_refreshTimer.interrupt();
			try {
				_refreshTimer.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			_refreshTimer = null;
		}
	}
	
	public void setAnimated(boolean animate)
	{
		resetPlayback();
		_animated = animate;
		if(_animated)
		{
			startTimer();
		}
		else 
		{
			stopTimer();
		}
	}
	
	public void setAnimated(boolean animate, int fps)
	{
		setAnimated(animate);
		setFrameRate(fps);
	}
	
	public void setFrameRate(int fps)
	{
		if(fps > 0) _fps = fps;
	}
	
	/**
	 * Resets playback to the first stroke (nothing shown).
	 */
	public void resetPlayback()
	{
		_currentStroke = 0;
		_lastTick = System.currentTimeMillis();
	}
	
	/**
	 * Adds the next stroke to the screen.
	 */
	public void stepPlayback()
	{
		_currentStroke++;
		if(_currentStroke > _character.getNumStrokes())
			_currentStroke = _character.getNumStrokes();
	}
	
	/**
	 * Sets the current stroke to be drawn. The value will be clamped to the
	 * actual number of strokes.
	 * @param stroke - the number of strokes that should be drawn
	 * @return the actual stroke the view is set to.
	 */
	public int setCurrentStroke(int stroke)
	{
		if(_character==null || stroke < 0)
		{
			_currentStroke = 0;
		}
		else if(stroke > _character.getNumStrokes())
		{
			_currentStroke = _character.getNumStrokes();
		}
		else
		{
			_currentStroke = stroke;
		}
		
		return _currentStroke;
	}
	
	protected void animate()
	{
		long ticks = System.currentTimeMillis() - _lastTick;
		if(ticks >= 1000/_fps)
		{
			_lastTick = System.currentTimeMillis();
			stepPlayback();
		}
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		animate();
		canvas.drawColor(_backgroundColor);
		
		if(_character != null)
		{
			if(_animated)
			{
				drawStrokes(canvas, _character, _currentStroke);
			}
			else
			{
				drawCharacter(canvas, _character);
			}
			
		}
	}

	/**
	 * Draws the first strokes of a character to the canvas
	 * @param canvas - the canvas to draw the strokes from
	 * @param character - the character being drawn
	 * @param strokes - the number of strokes to be drawn (will draw from stroke 0 to strokes)
	 */
	protected void drawStrokes(Canvas canvas, LessonCharacter character, int strokes)
	{
		for(int i = 0; i < strokes; i++)
		{
			drawStroke(canvas, character.getStroke(i));
		}
	}
	
}
