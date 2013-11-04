package com.trace2learn.TraceLibrary;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.LessonCategory;

public class IntroductionActivity extends TraceBaseActivity {

    private String           lessonName;
    private LessonCategory[] categories;
    
    private TextView     nameView;
    private TextView     narrativeTop;
    private TextView     narrativeBottom;
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
        String narrative1  = "\n\nLearning to read and write Chinese has a lot to do with learning to recognize patterns.  "
        		+ "Patterns are present in every language spoken around the globe – in the sounds, in the grammar, in the writing.  "
        		+ "Chinese characters are no different.  At first they may seem complex and impenetrable, but in fact they are packed with repeating patterns.  "
        		+ "Our eyes and ears can pick up on these rich hints and clues to make learning easier.\n\n"
        		+ "Trasee! is an innovative pattern-based system designed to help you quickly start to read and write, or further improve your skills.  "
        		+ "The application is organized into collections that start with the basics and build on each other, gradually introducing new patterns along the way.\n\n"
        		+ "Characters and phrases are grouped together in different ways, according to their shape, their meaning, their sound and the role they play in the language.  "
        		+ "The collections offer one set of associations.  Trasee! also encourages you to discover the associations that make sense to you, and lets you build your own collections.\n\n"
        		+ "Each collection is tagged according to one or more pattern types that it contains:";
        
        String narrative2 = "\nStart by browsing the existing collections.  "
        		+ "You can press down on any phrase to add it to a new custom collection.  "
        		+ "Collections that you create will appear in a different color, and you will be able to reorder or delete them.  "
        		+ "You can also use the catalog of characters to create your own phrases.\n\n"        		
        		+"A note on the style of characters used:  "
        		+ "The goal was a balance between the rigid, formal fonts you may see in print or on a computer screen, and a more natural handwritten script.  "
        		+ "There are countless fonts and styles used to write Chinese - calligraphy artists spend a lifetime mastering the art.  "
        		+ "Trasee! does not teach calligraphy.  What it will do is let you discover for yourself how characters are composed, and how they interact with each other.  "
        		+ "This in turn will lead to increased reading fluency and a broader vocabulary.\n\n";
        
        nameView.setText(lessonName);
        narrativeTop.setText(narrative1);
        narrativeBottom.setText(narrative2);
        initCategoriesList();
    }
    
    private void getViews() {
        nameView           = (TextView)     findViewById(R.id.name);
        narrativeTop       = (TextView)     findViewById(R.id.narrative_top);
        narrativeBottom    = (TextView)     findViewById(R.id.narrative_bottom);
        categoryLayout     = (LinearLayout) findViewById(R.id.categories);
        exitButton         = (ImageView)    findViewById(R.id.exit_button);
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
