package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonItem;
import com.trace2learn.TraceLibrary.Database.LessonItem.ItemType;

public class LessonItemListAdapter extends ArrayAdapter<LessonItem> {

	protected List<LessonItem> items;

	private LayoutInflater vi;
	private int thumbBg;

    //Bitmap Cache
    private static HashMap<String, Bitmap> bitmapCache;
    
    
	public LessonItemListAdapter(Context context,
	        List<LessonItem> objects, LayoutInflater vi) {
		super(context, 0, objects);
		items = new ArrayList<LessonItem>(objects);
		this.vi = vi;
		thumbBg = context.getResources().getColor(R.color.thumb_background);
		
		initBitmapCache();
	}
	
	private void initBitmapCache(){
		if(bitmapCache == null) bitmapCache = new HashMap<String, Bitmap>();
	}
	
	/**
	 * Configures the view for the given item in the list
	 * @param position - the index of the item in the list
	 * @param convertView - the constructed view that should be modified
	 * @param parent - The container of the list
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parentView) {

		View v = convertView;

		if (v == null) v = vi.inflate(R.layout.lesson_item_desc, null);

		final LessonItem item   = items.get(position);
		final int itemPosition = position;
		final ImageView gallery = (ImageView) v.findViewById(R.id.li_image);
		TextView   text   = (TextView)v.findViewById(R.id.idView);
		TextView   text2  = (TextView)v.findViewById(R.id.tagView);
		ImageView  sound  = (ImageView)v.findViewById(R.id.sound_button);
		gallery.setBackgroundColor(thumbBg);
		
		// construct Bitmap, look in cache first
		Bitmap bitmap = null;
		if(item.getItemType() == ItemType.WORD) {
			String wordId = item.getStringId();
			if(bitmapCache.containsKey(wordId)) {
				bitmap = bitmapCache.get(wordId);
			}
			else {
				bitmap = BitmapFactory.buildBitmap(item);
				bitmapCache.put(wordId, bitmap);
			}				
		}
		
		if(bitmap == null) bitmap = BitmapFactory.buildBitmap(item);
		gallery.setImageBitmap(bitmap);
		
		// populate text fields
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
		
		if (item.getItemType() == ItemType.WORD) {
			if(getContext() instanceof BrowseWordsActivity) {
				BrowseWordsActivity parent = (BrowseWordsActivity) getContext();
				
				// check whether parent view wants to hide text fields
				boolean showDefs = parent.showDefs();
				text.setVisibility(showDefs ? View.VISIBLE:View.INVISIBLE);
				text2.setVisibility(showDefs ? View.VISIBLE:View.INVISIBLE);

				// check whether parent view has an audio file associated with the word			
				boolean hasAudio = parent.hasAudio(item.getStringId());
				sound.setVisibility( (hasAudio && !showDefs) ? View.VISIBLE:View.GONE);
			}
				
		}
		else {
			sound.setVisibility(View.GONE);
		}
		
        // Set onClick listener for sound icon
        sound.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if(item.getItemType() == ItemType.WORD) {
            		// play audio associated with this word
            		((BrowseWordsActivity)getContext()).playSound(item.getStringId());
            	}
            }
        });	
        
        // setonCLick listener for the image gallery
        gallery.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(item.getItemType() == ItemType.WORD) {
					// launch practice activity for this word
					((BrowseWordsActivity)getContext()).launchPracticeActivity(item, gallery, itemPosition);
				}
			}
        	
        });
        
		return v;
	}
	

		
	public void add(LessonItem lessonItem) {
		super.add(lessonItem);
		items.add(lessonItem);
	}
	
	
}
