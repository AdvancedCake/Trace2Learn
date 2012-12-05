package edu.upenn.cis573.Trace2Win;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.upenn.cis573.Trace2Win.Database.Lesson;
import edu.upenn.cis573.Trace2Win.Database.LessonItem;

public class LessonItemListAdapter extends ArrayAdapter<LessonItem> {

	protected List<LessonItem> _items;
	
	protected LayoutInflater _vi;
	
	public LessonItemListAdapter(
			Context context,
			List<LessonItem> objects,
			LayoutInflater vi) 
	{
		super(context, 0, objects);
		_items = new ArrayList<LessonItem>(objects);
		_vi = vi;
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
			v = _vi.inflate(R.layout.lesson_item_desc, null);
		}
		LessonItem item = _items.get(position);
		ImageView image = (ImageView)v.findViewById(R.id.li_image);
		TextView text = (TextView)v.findViewById(R.id.idView);
		TextView text2 = (TextView)v.findViewById(R.id.tagView);
		Bitmap bitmap = BitmapFactory.buildBitmap(item, 64);
		image.setImageBitmap(bitmap);
		
		// text
		switch (item.getItemType())
		{
		case CHARACTER:
		case WORD:
			text.setText(item.getKeyValuesToString());
			break;
		case LESSON:
			text.setText(((Lesson)item).getLessonName());
			break;		
		}
		
		// text2
		text2.setText(item.getTagsToString());
		return v;
	}
		
	public void add(LessonItem lessonItem) {
		super.add(lessonItem);
		_items.add(lessonItem);
	}
}
