package edu.upenn.cis350.Trace2Learn;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import edu.upenn.cis350.Trace2Learn.Database.LessonItem;
import edu.upenn.cis350.Trace2Learn.Database.LessonItem.ItemType;
import edu.upenn.cis350.Trace2Learn.Database.LessonWord;

public class BitmapFactory {
	
	private static final int _default_width = 64;
	private static final int _default_height = 64;
	
	public static Bitmap buildBitmap(LessonItem item)
	{
		return buildBitmap(item, _default_width, _default_height);
	}
	
	/**
	 * Builds a bitmap composed of the provided items, each item with the given height
	 * @param items
	 * @param height
	 * @return a bitmap which is size (items.size()*height)x(height) in dimensions
	 */
	public static Bitmap buildBitmap(LessonItem item, int height)
	{
		if(item.getItemType() == ItemType.WORD)
		{
			int width = ((LessonWord)item).length()*height;
			if(width == 0) width = height;
			return buildBitmap(item, width, height);
		}
		else
		{
			return buildBitmap(item, height, height);
		}
	}
	
	public static Bitmap buildBitmap(LessonItem item, int width, int height)
	{
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLUE);
        item.draw(canvas);
        return bitmap;
	}
	
	
	
}
