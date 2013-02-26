package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonCategory;

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

        Lesson      item       = _items.get(position);
        TextView    nameView   = (TextView)  v.findViewById(R.id.nameView);
        TextView    sizeView   = (TextView)  v.findViewById(R.id.sizeView);
        ImageView   category1  = (ImageView) v.findViewById(R.id.category1);
        ImageView   category2  = (ImageView) v.findViewById(R.id.category2);
        ImageView   category3  = (ImageView) v.findViewById(R.id.category3);
        ImageView   category4  = (ImageView) v.findViewById(R.id.category4);
        ImageView[] categories = {category1, category2, category3, category4};

        int count = item.getNumWords();
        nameView.setText(item.getLessonName());
        sizeView.setText(count + (count == 1 ? " word" : " words"));
        
        // Display category icons
        int i = 0;
        Set<LessonCategory> itemCategories = item.getCategories();
        if (itemCategories != null) {
            for (LessonCategory category : itemCategories) {
                categories[i].setImageResource(category.rid);
                i++;
            }
        }
        
        // Blank out all of the other icons
        for (; i < 4; i++) {
            categories[i].setImageResource(0);
            i++;
        }

        return v;
    }

}
