package edu.upenn.cis573.Trace2Win.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.view.MotionEvent;

public abstract class DrawingPane extends CharacterViewPane {
	
	private Path _path;
	
	
	private float _prevX, _prevY;
	private static final float TOUCH_TOLERANCE = 4;

	public DrawingPane(Context c) {
		super(c);
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
	
	/**
	 * Notifies the pane that a new stroke has been started on the screen
	 * @param x - the screen coordinate of the point in pixels
	 * @param y - the screen coordinate of the point in pixels
	 */
	private void touchStart(float x, float y) {
		// Scale the pixels to a 1x1 square
		beginStroke(x/getWidth(), y/getHeight());
	}

	/**
	 * Notifies the pane that an additional point has been sampled in the 
	 * current stroke
	 * @param x - the screen coordinate of the point in pixels
	 * @param y - the screen coordinate of the point in pixels
	 */
	private void touchMove(float x, float y) {
		float dx = Math.abs(x - _prevX);
		float dy = Math.abs(y - _prevY);
		// Don't sample if the movement is negligible in size
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			// Scale the pixels to a 1x1 square
			updateStroke(x/getWidth(), y/getHeight());
		}
	}

	/**
	 * Notifies the pane that the last point has been sampled in the 
	 * current stroke
	 * @param x - the screen coordinate of the point in pixels
	 * @param y - the screen coordinate of the point in pixels
	 */
	private void touchUp(float x, float y) {
		completeStroke(x/getWidth(), y/getHeight());
	}

	/**
	 * Handles the touch event and logs a new point in the stroke
	 */
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