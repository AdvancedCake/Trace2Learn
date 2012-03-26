package edu.upenn.cis350.Trace2Learn;

import java.util.List;

import edu.upenn.cis350.Trace2Learn.Database.LessonCharacter;
import edu.upenn.cis350.Trace2Learn.Database.Stroke;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

public abstract class DrawingPane extends CharacterViewPane {
	
	private Path _path;
	
	
	private float _prevX, _prevY;
	private static final float TOUCH_TOLERANCE = 4;

	public DrawingPane(Context c, Paint paint) {
		super(c, paint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(_backgroundColor);
		canvas.drawPath(_path, _paint);
	}

	/**
	 * The method that is called every time a new stroke begins
	 * This is where the creation of a new stroke should be handled
	 * @param newX - the x coordinate where the stroke begins
	 * @param newY - the y coordinate where the stroke ends
	 */
	protected abstract void beginStroke(float newX, float newY);
	/**
	 * The method that is called every time a new point is sampled for the current stroke
	 * @param newX - the x coordinate of the sample point
	 * @param newY - the y coordinate of the sample point
	 */
	protected abstract void updateStroke(float newX, float newY);
	/**
	 * The method that is called every when the stroke is completed
	 * This is where the completion of a stroke should be handled
	 * @param newX - the x coordinate of the sample point
	 * @param newY - the y coordinate of the sample point
	 */
	protected abstract void completeStroke(float newX, float newY);
	
	private void touchStart(float x, float y) {
		beginStroke(x, y);
	}

	private void touchMove(float x, float y) {
		float dx = Math.abs(x - _prevX);
		float dy = Math.abs(y - _prevY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			updateStroke(x, y);
		}
		
	}

	private void touchUp(float x, float y) {
		completeStroke(x, y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchStart(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touchMove(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touchUp(x, y);
			invalidate();
			break;
		}
		_prevX = x;
		_prevY = y;
		return true;
	}
}