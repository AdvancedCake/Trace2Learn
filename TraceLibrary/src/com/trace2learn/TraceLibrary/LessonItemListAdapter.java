package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.List;

import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
		if (v == null) {
			v = vi.inflate(R.layout.lesson_item_desc, null);
		}
		LessonItem item   = items.get(position);
		ImageView  image  = (ImageView)v.findViewById(R.id.li_image);
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
