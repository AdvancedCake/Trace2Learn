package edu.upenn.cis573.Trace2Win.library;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.upenn.cis573.Trace2Win.library.Database.Lesson;

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
			v = _vi.inflate(R.layout.lesson_desc, null);
		}
		
		Lesson item = _items.get(position);
		TextView nameView = (TextView) v.findViewById(R.id.nameView);
		TextView sizeView = (TextView) v.findViewById(R.id.sizeView);
		
		int count = item.getNumWords();
        nameView.setText(item.getLessonName());
        sizeView.setText(count + (count == 1 ? " word" : " words"));
		
		return v;
	}
	
}
