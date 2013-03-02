package com.trace2learn.TraceLibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.DbAdapter;
import com.trace2learn.TraceLibrary.Database.LessonCategory;

public class ChooseLessonCategoryActivity extends Activity {
    
    private Intent    intent;
    private String    lessonID;
    private String    lessonName;
    private boolean[] original;
    private boolean[] selections;
    
    private LessonCategory[] categories;
    private LessonCategoryAdapter adapter;
    
    private TextView nameView;
    private ListView categoryList;
    private Button   saveButton;    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_lesson_category);
        
        // Initialize Views and Handlers
        getViews();
        getHandlers();
        initCategoriesList();
        
        // Grab the extras
        intent = getIntent();
        lessonID = intent.getStringExtra("ID");
        lessonName = intent.getStringExtra("name");
        original = intent.getBooleanArrayExtra("categories");
        selections = original.clone();
        
        nameView.setText(lessonName);
    }
    
    private void getViews() {
        nameView     = (TextView) findViewById(R.id.lessonName);
        categoryList = (ListView) findViewById(R.id.categoryList);
        saveButton   = (Button)   findViewById(R.id.saveButton);
    }
    
    private void getHandlers() {
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }
    
    private void initCategoriesList() {
        categories = LessonCategory.values();
        LayoutInflater vi = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adapter = new LessonCategoryAdapter(getApplicationContext(),
                categories, vi);
        categoryList.setAdapter(adapter);
    }
    
    private boolean writeToDb() {
        DbAdapter dba = new DbAdapter(getApplicationContext());
        dba.open();
        
        boolean result = dba.saveLessonCategories(lessonID, selections);
        
        dba.close();
        return result;
    }
    
    @Override
    public void onBackPressed() {
        close();
    }
    
    private void close() {
        for (boolean b : selections) {
            System.out.println(b);
        }
        if (original.equals(selections)) {
            setResult(RESULT_CANCELED);
        } else {
            if (writeToDb()) {
                setResult(RESULT_OK);                
            } else {
                Toolbox.showToast(getApplicationContext(),
                        "Save failed - please try again");
                setResult(RESULT_CANCELED);
            }
        }
        finish();
    }
    
    
    private class LessonCategoryAdapter extends ArrayAdapter<LessonCategory> {
        
        private LessonCategory[] items;
        private LayoutInflater vi;
        
        public LessonCategoryAdapter(Context context, LessonCategory[] items,
                LayoutInflater vi) {
            super(context, 0, items);
            this.items = items;
            this.vi    = vi;
        }
        
        @Override
        public View getView(final int position, View convertView,
                ViewGroup parent) {
            View v = convertView;
            
            if (v == null) {
                v = vi.inflate(R.layout.lesson_category, null);
            }
            
            LessonCategory category = items[position];
            CheckBox  checkbox = (CheckBox)  v.findViewById(R.id.checkbox);
            ImageView icon     = (ImageView) v.findViewById(R.id.icon);
            TextView  name     = (TextView)  v.findViewById(R.id.name);
            
            checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                        boolean isChecked) {
                    selections[position] = isChecked;
                }
            });
            
            checkbox.setChecked(selections[position]);
            icon.setImageResource(category.rid);
            name.setText(category.name);
            
            return v;
        }
    }
    
}
