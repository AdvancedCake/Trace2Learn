package edu.upenn.cis350.Trace2Learn;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import edu.upenn.cis350.Trace2Learn.Database.LessonItem;

public class BitmapFactory {
	
	private static final int _default_width = 256;
	private static final int _default_height = 256;
	
	public static Bitmap buildBitmap(LessonItem item)
	{
		return buildBitmap(item, _default_width, _default_height);
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
