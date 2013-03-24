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

import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonCategory;

public class LessonNarrativeActivity extends Activity {

    private Intent                    intent;
    private String                    lessonId;
    private Lesson                    lesson;
    private String                    lessonName;
    private String                    narrative;
    private SortedSet<LessonCategory> categories;
    
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
        
        // Grab the extras
        intent = getIntent();
        lessonId = intent.getStringExtra("ID");
        // get lesson from the database
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
             Context context = getApplicationContext();
             for (LessonCategory category : categories) {
                 View view = new View(context);
                 vi.inflate(R.layout.lesson_category, categoryLayout);
                 
                 ImageView icon = (ImageView) view.findViewById(R.id.icon);
                 TextView  name = (TextView)  view.findViewById(R.id.name);
                 
                 icon.setImageResource(category.rid);
                 name.setText(category.name);
                 
                 categoryLayout.addView(view);
             }
         }
    }
    
}
