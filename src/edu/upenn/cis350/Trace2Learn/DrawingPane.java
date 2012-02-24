package edu.upenn.cis350.Trace2Learn;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

public abstract class DrawingPane extends View {

	private static final float MINP = 0.25f;
	private static final float MAXP = 0.75f;

	private Path mPath;
	protected Paint mPaint;
	
	protected int mBackgroundColor = Color.GRAY;

	public DrawingPane(Context c, Paint paint) {
		super(c);

		mPaint = paint;
		mPath = new Path();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(mBackgroundColor);
		canvas.drawPath(mPath, mPaint);
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private void touchStart(float x, float y) {
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
		beginStroke(x, y, mPath);
	}

	private void touchMove(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
		updateStroke(x, y);
	}

	private void touchUp() {
		mPath.lineTo(mX, mY);
		mPath = new Path();
		completeStroke(mX, mY, mPath);
	}
	
	protected abstract void beginStroke(float newx, float newy, Path startPath);
	protected abstract void updateStroke(float newX, float newY);
	protected abstract void completeStroke(float newX, float newY, Path endPath);

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