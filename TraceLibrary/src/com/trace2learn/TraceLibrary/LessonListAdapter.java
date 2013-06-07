package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonCategory;

public class LessonListAdapter extends ArrayAdapter<Lesson> {

    private ArrayList<Lesson> items;

    private Handler handler;
    private LayoutInflater vi;

    boolean isFull;
    
    private int defaultColor = -1;
    private int userColor;
    private int lockColor;

    public LessonListAdapter(Context context, List<Lesson> objects,
            LayoutInflater vi, Handler handler, boolean isFull) {
        super(context, 0, objects);
        this.items     = new ArrayList<Lesson>(objects);
        this.vi        = vi;
        this.handler   = handler;
        this.isFull    = isFull;
        this.userColor = context.getResources().getColor(
                R.color.user_collection);
        this.lockColor = context.getResources().getColor(
        		R.color.locked_collection);
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
            v = vi.inflate(R.layout.lesson_desc, null);
        }

        final Lesson item       = items.get(position);
        TextView     nameView   = (TextView)  v.findViewById(R.id.nameView);
        TextView     sizeView   = (TextView)  v.findViewById(R.id.sizeView);
        ImageView    infoButton = (ImageView) v.findViewById(R.id.infoButton);
        ImageView    category1  = (ImageView) v.findViewById(R.id.category1);
        ImageView    category2  = (ImageView) v.findViewById(R.id.category2);
        ImageView    category3  = (ImageView) v.findViewById(R.id.category3);
        ImageView    category4  = (ImageView) v.findViewById(R.id.category4);
        ImageView[]  categories = {category1, category2, category3, category4};

        int    count = item.getNumWords();
        String name  = item.getLessonName();
        nameView.setText(name);
        sizeView.setText(count + (count == 1 ? " phrase" : " phrases"));
        
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
        }
        
        // Save default text color
        if (defaultColor == -1) {
            defaultColor = nameView.getTextColors().getDefaultColor();
        }
        
        // Check if this is an admin lesson or user lesson
        if (item.isUserDefined()) {
            nameView.setTextColor(userColor);
            sizeView.setTextColor(userColor);
            nameView.setTypeface(null, Typeface.ITALIC);
        } else { // admin-created
            nameView.setTextColor(defaultColor);
            sizeView.setTextColor(defaultColor);
            nameView.setTypeface(null, Typeface.NORMAL);
        }
        
        // Check if this is the full version of the app
        try {
			if (!isFull && !item.isUserDefined() &&
					Integer.valueOf(name.substring(0, name.indexOf(':'))) > 10) {
				nameView.setTextColor(lockColor);
				sizeView.setTextColor(lockColor);
			}
		} catch (Exception e) {}

        // Set onClick listener for info button
        infoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = Message.obtain(handler, 0, item.getStringId());
                handler.sendMessage(msg);
            }
        });

        return v;
    }

}
