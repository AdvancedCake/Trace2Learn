package com.trace2learn.TraceLibrary;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.LessonCategory;

public class IntroductionActivity extends Activity {

    private String           lessonName;
    private String           narrative;
    private LessonCategory[] categories;
    
    private TextView     nameView;
    private TextView     narrativeView;
    private LinearLayout categoryLayout;
    private ImageView    exitButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduction);
        
        // Initialize Views and Handlers
        getViews();
        getHandlers();
        
        // Initialize values
        lessonName = "Introduction";
        categories = LessonCategory.values();
        narrative  = "Learning to read and write Chinese has a lot to do with learning to recognize patterns.  Patterns are present in every language spoken around the globe – in the sounds, in the grammar, in the writing.  Chinese characters are no different.  At first they may seem complex and impenetrable, but in fact they are packed with repeating patterns.  Our eyes and ears can pick up on these rich hints and clues to make learning easier.\n\nTraceMe! for Traditional Chinese is organized into collections that start with the basics and build on each other, gradually introducing new patterns along the way.  Characters and phrases are grouped together in different ways, according to their shape, their meaning, their sound and the role they play in the language.  The collections offer one set of associations, but an infinite number of others exist.  TraceMe! for Traditional Chinese also allows you to build your own collections.\n\nLearning Chinese should be fun.  Hopefully, you’ll find these collections relevant, informative, and at times amusing.  But since no two people learn the same way – use them as a jumping-off point, make the associations that work best for you, chart your own course, and have fun!\n\nEach collection is tagged according to one or more pattern types that it builds on –";
        
        nameView.setText(lessonName);
        narrativeView.setText(narrative);
        initCategoriesList();
    }
    
    private void getViews() {
        nameView       = (TextView)     findViewById(R.id.name);
        narrativeView  = (TextView)     findViewById(R.id.narrative);
        categoryLayout = (LinearLayout) findViewById(R.id.categories);
        exitButton     = (ImageView)    findViewById(R.id.exit_button);
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
