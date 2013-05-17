package com.trace2learn.TraceLibrary;

import com.trace2learn.TraceLibrary.Database.LessonItem;
import com.trace2learn.TraceLibrary.Database.LessonWord;
import com.trace2learn.TraceLibrary.Database.LessonItem.ItemType;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

public class BitmapFactory {
	
	private static final int _default_width  = 64;
	private static final int _default_height = 64;
	
	public static Bitmap buildBitmap(LessonItem item)
	{
		return buildBitmap(item, _default_width, _default_height);
	}
	
	/**
	 * Builds a bitmap composed of the provided items, each item with the given height
	 * @param item - The item to be drawn
	 * @param height - the height of the bitmap to be created
	 * @return a bitmap which is size (items.length()*height)x(height) in dimensions
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
	
	/**
	 * Builds a bitmap of the given image which is width x height in size
	 * @param item - The item to be drawn
	 * @param width - The width of the bitmap to be created
	 * @param height - The height of the bitmap to be created
	 * @return
	 */
	public static Bitmap buildBitmap(LessonItem item, int width, int height) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        item.draw(canvas);
        return bitmap;
	}
	
	
	
}
