package edu.upenn.cis350.Trace2Learn;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquareLayout extends LinearLayout {

    public SquareLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SquareLayout(Context context) {
		super(context);
	}

	/**
	 * Restricts the view to be square in shape
	 */
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	if(widthMeasureSpec > heightMeasureSpec)
    	{
    		super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    	}
    	else
    	{
    		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    	}
    }
}
