package edu.upenn.cis350.Trace2Learn;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis350.Trace2Learn.Database.LessonItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LessonItemListAdapter extends ArrayAdapter<LessonItem> {

	private ArrayList<LessonItem> _items;
	
	private LayoutInflater _vi;
	
	public LessonItemListAdapter(
			Context context,
			List<LessonItem> objects,
			LayoutInflater vi) 
	{
		super(context, 0, objects);
		_items = new ArrayList<LessonItem>(objects);
		_vi = vi;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			
			v = _vi.inflate(R.layout.lesson_item_desc, null);
		}
		LessonItem item = _items.get(position);
		ImageView image = (ImageView)v.findViewById(R.id.li_image);
		TextView text = (TextView)v.findViewById(R.id.li_description);
		Bitmap bitmap = BitmapFactory.buildBitmap(item, 64);
		image.setImageBitmap(bitmap);
		// TODO Initialize TextView part
		//StringBuilder string = new StringBuilder();
		//string.append(string)
		text.setText(item.getId()+"");
		return v;
	}
	
}
