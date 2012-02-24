package edu.upenn.cis350.Trace2Learn;

import java.util.LinkedList;
import java.util.List;

import edu.upenn.cis350.Trace2Learn.Characters.Stroke;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class CharacterCreationPane extends DrawingPane {

	private List<Path> mStrokePaths;
	private List<Stroke> mStrokes;
	private Stroke mCurrentStroke;
	
	public CharacterCreationPane(Context c, Paint paint) {
		super(c, paint);

		mStrokePaths = new LinkedList<Path>();
		mStrokes = new LinkedList<Stroke>();
	}

	@Override
	protected void beginStroke(float newX, float newY, Path startPath) {
		mStrokePaths.add(startPath);
		mCurrentStroke = new Stroke(newX, newY);
	}
	
	@Override
	protected void updateStroke(float newX, float newY)
	{
		mCurrentStroke.addPoint(newX, newY);
	}

	@Override
	protected void completeStroke(float newX, float newY, Path endPath) {
		// consider drawing just one stroke
		mCurrentStroke.addPoint(newX, newY);
		mStrokes.add(mCurrentStroke);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(mBackgroundColor);

		// Consider using a bitmap buffer so only new strokes are drawn.
		
		for(Path stroke : mStrokePaths)
		{
			canvas.drawPath(stroke, mPaint);
		}
		
	}
	
}
