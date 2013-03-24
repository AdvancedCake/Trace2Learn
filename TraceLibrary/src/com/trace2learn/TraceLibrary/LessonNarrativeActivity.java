package com.trace2learn.TraceLibrary;

import java.util.SortedSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.DbAdapter;
import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonCategory;

public class LessonNarrativeActivity extends Activity {

    private Intent                    intent;
    private String                    lessonId;
    private Lesson                    lesson;
    private String                    lessonName;
    private String                    narrative;
    private SortedSet<LessonCategory> categories;
    
    private DbAdapter dba;
    
    private TextView     nameView;
    private TextView     narrativeView;
    private LinearLayout categoryLayout;
    private Button       exitButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_narrative);
        
        // Initialize Views and Handlers
        getViews();
        getHandlers();
        
        // Initialize database adapter
        dba = new DbAdapter(this);
        dba.open();
        
        // Grab the extras
        intent     = getIntent();
        lessonId   = intent.getStringExtra("ID");
        lesson     = dba.getLessonById(lessonId);
        lessonName = lesson.getLessonName();
        categories = lesson.getCategories();
        narrative  = lesson.getNarrative();
        
        nameView.setText(lessonName);
        narrativeView.setText(narrative);
        initCategoriesList();
    }
    
    private void getViews() {
        nameView       = (TextView)     findViewById(R.id.lesson_name);
        narrativeView  = (TextView)     findViewById(R.id.narrative);
        categoryLayout = (LinearLayout) findViewById(R.id.categories);
        exitButton     = (Button)       findViewById(R.id.exit_button);
    }
    
    private void getHandlers() {
        exitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private void initCategoriesList() {
         if (categories != null) {
             LayoutInflater vi = (LayoutInflater)
                     getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             for (LessonCategory category : categories) {
                 View view = vi.inflate(R.layout.lesson_category, null);
                 
                 ImageView icon = (ImageView) view.findViewById(R.id.icon);
                 TextView  name = (TextView)  view.findViewById(R.id.name);
                 
                 icon.setImageResource(category.rid);
                 name.setText(category.name);
                 
                 categoryLayout.addView(view);
             }
         }
    }
    
}
