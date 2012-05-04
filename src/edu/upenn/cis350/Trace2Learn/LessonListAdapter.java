package edu.upenn.cis350.Trace2Learn;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis350.Trace2Learn.Database.Lesson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LessonListAdapter extends ArrayAdapter<Lesson> {

	private ArrayList<Lesson> _items;
	
	private LayoutInflater _vi;
	
	public LessonListAdapter( Context context, List<Lesson> objects, LayoutInflater vi) 
	{
		super(context, 0, objects);
		_items = new ArrayList<Lesson>(objects);
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
		Lesson item = _items.get(position);
		TextView text = (TextView)v.findViewById(R.id.li_description);
		text.setText(item.getLessonName());
		
		return v;
	}
	
}
