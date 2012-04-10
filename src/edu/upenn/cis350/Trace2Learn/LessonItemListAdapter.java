package edu.upenn.cis350.Trace2Learn;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis350.Trace2Learn.Database.LessonItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
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
		TextView text = (TextView)v.findViewById(R.id.li_description);
		TextView text2 = (TextView)v.findViewById(R.id.li_description2);
		Bitmap bitmap = BitmapFactory.buildBitmap(item, 64);
		image.setImageBitmap(bitmap);
		
		text.setText(item.getPrivateTag());
		ArrayList<String> tags = new ArrayList<String>(item.getTags());
		StringBuilder sb = new StringBuilder();
		for(String tag : tags){
			Log.e("Tag","Found");
			sb.append(", "+tag);
		}
		String s = "";
		if(sb.length()>0){
			s = sb.substring(2);
			Log.e("Printing Tags",s);
		}
		text2.setText(s);
		return v;
	}
	
}
