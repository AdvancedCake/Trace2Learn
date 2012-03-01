package edu.upenn.cis350.Trace2Learn;

import java.util.List;

import edu.upenn.cis350.Trace2Learn.Characters.LessonCharacter;
import edu.upenn.cis350.Trace2Learn.Characters.Stroke;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

public abstract class DrawingPane extends View {
	
	private Path _path;
	protected Paint _paint;
	
	protected int _backgroundColor = Color.GRAY;
	
	private float _prevX, _prevY;
	private static final float TOUCH_TOLERANCE = 4;

	public DrawingPane(Context c, Paint paint) {
		super(c);

		_paint = paint;
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
	
	protected void drawStroke(Canvas canvas, Stroke stroke)
	{
		canvas.drawPath(stroke.toPath(), _paint);
	}
	
	protected void drawCharacter(Canvas canvas, LessonCharacter character)
	{
		List<Stroke> strokes = character.getStrokes();
		for(Stroke s : strokes)
		{
			drawStroke(canvas, s);
		}
	}

	protected abstract void beginStroke(float newX, float newY);
	protected abstract void updateStroke(float newX, float newY);
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

	private void touchUp() {
		completeStroke(_prevX, _prevY);
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
			touchUp();
			invalidate();
			break;
		}
		return true;
	}
}