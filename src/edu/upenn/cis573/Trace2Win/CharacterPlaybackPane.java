package edu.upenn.cis573.Trace2Win;

import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;
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
	protected float _animationLength;
	
	protected Thread _refreshTimer;
	protected Handler _handler;
	
	protected boolean _timerOff = true;
	private float _elapsedTime;
	
	public CharacterPlaybackPane(Context context, boolean animated, float animationLength)
	{
		super(context);
		resetPlayback();
		_animated = animated;
		_animationLength = animationLength;
		
		_handler = new Handler();
		
	}
	public CharacterPlaybackPane(Context context, boolean animated)
	{
		this(context, false, 60);
	}
	
	public CharacterPlaybackPane(Context context) {
		this(context, false);
	}
	
	public void setCharacter(LessonCharacter character)
	{
		super.setCharacter(character);
		resetPlayback();
	}
	
	/**
	 * Builds and starts the animation timer
	 */
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
	
	/**
	 * If the timer is running, stop the timer
	 */
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
	
	/**
	 * Toggles whether or not the character should be drawn stroke by stroke
	 * @param animate whether the character should be animated
	 */
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
	
	/**
	 * Toggles whether or not the character should be drawn stroke by stroke
	 * @param animate whether the character should be animated
	 * @param length the duration of the animation
	 */
	public void setAnimated(boolean animate, float length)
	{
		setAnimated(animate);
		setAnimationLength(length);
	}
	
	public void setAnimationLength(float length)
	{
		if(length > 0) _animationLength = length;
	}
	
	/**
	 * Resets playback to the first stroke (nothing shown).
	 */
	public void resetPlayback()
	{
		_currentStroke = 0;
		_lastTick = System.currentTimeMillis();
		_elapsedTime = 0;
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
	
	public void animate()
	{
		long ticks = System.currentTimeMillis() - _lastTick;
		_elapsedTime += ticks/1000F;
		_lastTick = System.currentTimeMillis();
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		animate();
		
		float time = _elapsedTime/_animationLength;
		
		canvas.drawColor(_backgroundColor);
		
		if(_character != null)
		{
			if(_animated)
			{
				_character.draw(canvas, time);
			}
			else
			{
				_character.draw(canvas);
			}
			
		}
	}
	
}
