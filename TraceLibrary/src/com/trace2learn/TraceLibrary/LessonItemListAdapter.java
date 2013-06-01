package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonItem;

public class LessonItemListAdapter extends ArrayAdapter<LessonItem> {

	protected List<LessonItem> items;
	
	private LayoutInflater vi;
	private int thumbBg;
	
	public LessonItemListAdapter(Context context,
	        List<LessonItem> objects, LayoutInflater vi) {
		super(context, 0, objects);
		items = new ArrayList<LessonItem>(objects);
		this.vi = vi;
		thumbBg = context.getResources().getColor(R.color.thumb_background);
	}
	
	/**
	 * Configures the view for the given item in the list
	 * @param position - the index of the item in the list
	 * @param convertView - the constructed view that should be modified
	 * @param parent - The contained of the list
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ImageView image;
		if (v == null) {
			v = vi.inflate(R.layout.lesson_item_desc, null);
			image = (ImageView) v.findViewById(R.id.li_image);
		} else {
	        image = (ImageView) v.findViewById(R.id.li_image);
	        BitmapDrawable bd = (BitmapDrawable) image.getDrawable();
	        if (bd != null) {
	            bd.getBitmap().recycle();
	        }
		}
		LessonItem item   = items.get(position);
		TextView   text   = (TextView)v.findViewById(R.id.idView);
		TextView   text2  = (TextView)v.findViewById(R.id.tagView);
		image.setBackgroundColor(thumbBg);
		image.setImageBitmap(BitmapFactory.buildBitmap(item, 64));
		
		// text
		switch (item.getItemType())
		{
		case CHARACTER:
		case WORD:
	        text.setText(item.getTagsToString());
	        text2.setText(item.getKeyValuesToString());
			break;
		case LESSON:
			text.setText(((Lesson) item).getLessonName());
			text2.setText(item.getTagsToString());
			break;
		}
		
		return v;
	}
		
	public void add(LessonItem lessonItem) {
		super.add(lessonItem);
		items.add(lessonItem);
	}
}
