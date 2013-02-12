package com.trace2learn.TraceLibrary;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FileArrayAdapter extends ArrayAdapter<FileItem> {
    
    private Context c;
    private int id;
    private List<FileItem> items;
    private LayoutInflater vi;

    public FileArrayAdapter(Context context, int textViewResourceId,
            List<FileItem> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        id = textViewResourceId;
        items = objects;
        vi = (LayoutInflater) c.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = vi.inflate(id, null);
        }
        
        final FileItem item = items.get(position);
        if (item != null) {
            TextView name = (TextView) v.findViewById(R.id.nameView);
            TextView desc = (TextView) v.findViewById(R.id.descView);
            name.setText(item.getName());
            desc.setText(item.getDesc());
        }
        
        return v;
    }

}
