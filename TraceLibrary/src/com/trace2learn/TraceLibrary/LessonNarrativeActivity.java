package com.trace2learn.TraceLibrary;

import java.util.SortedSet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonCategory;

public class LessonNarrativeActivity extends TraceBaseActivity {

    private Intent                    intent;
    private String                    lessonId;
    private Lesson                    lesson;
    private String                    lessonName;
    private String                    narrative;
    private SortedSet<LessonCategory> categories;
    
    private SharedPreferences prefs;
    
    private TextView     nameView;
    private TextView     narrativeView;
    private LinearLayout categoryLayout;
    private ImageView    exitButton;
    private Button       editButton;
    private Button       categoriesButton;

    private enum RequestCode {
        ASSIGN_CATEGORIES;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_narrative);
        
        // Initialize SharedPreferences
        prefs = getSharedPreferences(Toolbox.PREFS_FILE, MODE_PRIVATE);
        
        // Initialize database adapter
        
        // Grab the extras
        intent     = getIntent();
        lessonId   = intent.getStringExtra("ID");
        lesson     = Toolbox.dba.getLessonById(lessonId);
        lessonName = lesson.getLessonName();
        categories = lesson.getCategories();
        narrative  = lesson.getNarrative();
        
        // Initialize Views and Handlers
        getViews();
        getHandlers();
        
        nameView.setText(lessonName);
        narrativeView.setText(narrative);
        initCategoriesList();

        if (narrative == null || narrative.length() == 0) {
            narrativeView.setText("No narrative!");
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    private void getViews() {
        nameView          = (TextView)     findViewById(R.id.lesson_name);
        narrativeView     = (TextView)     findViewById(R.id.narrative);
        categoryLayout    = (LinearLayout) findViewById(R.id.categories);
        exitButton        = (ImageView)    findViewById(R.id.exit_button);
        editButton        = (Button)       findViewById(R.id.edit_button);
        categoriesButton  = (Button)       findViewById(R.id.categories_button);
        
        if (!lesson.isUserDefined() &&
                !prefs.getBoolean(Toolbox.PREFS_IS_ADMIN, false)) {
            // Lesson is admin, but user is not admin
            editButton.setVisibility(View.GONE);
            categoriesButton.setVisibility(View.GONE);
        }
        
        // Because setMovementMethod causes the view to darken when clicked, we
        // must disable the view. But that dims the text color, so we need to
        // save the old text color and change the color back.
        ColorStateList colors = narrativeView.getTextColors();
        int color = colors.getDefaultColor();
        narrativeView.setMovementMethod(new ScrollingMovementMethod());
        narrativeView.setEnabled(false);
        narrativeView.setTextColor(color);
    }
    
    private void getHandlers() {
        exitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        editButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editNarrative();
            }
        });   
        
        categoriesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editCategories();
            }
        });
    }
    
    private void initCategoriesList() {
         if (categories != null && categories.size() > 0) {
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
         } else {
             TextView text = new TextView(getApplicationContext());
             text.setText("No categories!");
             text.setTextSize(18);
             categoryLayout.addView(text);
         }
    }
    
    private void editNarrative() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Narrative");
        
        final EditText edit = new EditText(this);
        edit.setText(narrative);
        builder.setView(edit);
        
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                narrative = edit.getText().toString();
                lesson.setNarrative(narrative);
                narrativeView.setText(narrative);
                Toolbox.dba.saveLessonNarrative(lessonId, narrative);
            }
        });
        
        builder.setNegativeButton(R.string.cancel, null);
        
        AlertDialog dialog = builder.create();
        Toolbox.showKeyboard(dialog);
        dialog.show();
    }
    
    private void editCategories() {
    	
    	Context ctx = getApplicationContext();
        Intent i = new Intent(ctx, ChooseLessonCategoryActivity.class);
        i.putExtra("ID",   lessonId);
        i.putExtra("name", lessonName);

        boolean[] original = new boolean[] {false, false, false, false};
        SortedSet<LessonCategory> categories = lesson.getCategories();
        if (categories != null) {
            for (LessonCategory category : categories) {
                original[category.ordinal()] = true;
            }
        }
        i.putExtra("categories", original);
        startActivityForResult(i, RequestCode.ASSIGN_CATEGORIES.ordinal());
   	
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.ASSIGN_CATEGORIES.ordinal() &&
                resultCode == RESULT_OK) {
        	// refresh categories list
            startActivity(getIntent());
            finish();
        }
    }
}
