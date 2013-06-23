package com.trace2learn.TraceLibrary;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.trace2learn.TraceLibrary.Database.Lesson;

public class AddToCollectionActivity extends Activity {

    private String               word;
    private List<String>         lessons;
    private ArrayAdapter<String> adapter;

    private ListView list;
    private Button   newButton;
    private Button   cancelButton;

    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_collection);

        initViews();
        initHandlers();

        // Set admin privilege
        SharedPreferences prefs = getSharedPreferences(Toolbox.PREFS_FILE,
                MODE_PRIVATE);
        isAdmin = prefs.getBoolean(Toolbox.PREFS_IS_ADMIN, false);

        getLessons();
        
        // get word ID
        word = getIntent().getExtras().getString("word");
    }

    private void initViews() {
        list         = (ListView) findViewById(R.id.list);
        newButton    = (Button)   findViewById(R.id.new_button);
        cancelButton = (Button)   findViewById(R.id.cancel_button);
    }

    private void initHandlers() {
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                String name = lessons.get(position);
                Toolbox.dba.addWordToLesson(name, word);
                setResult(RESULT_OK);
                finish();
            }
        });

        newButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNewCollection();
            }
        });

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void getLessons() {
        if (isAdmin) {
            lessons = Toolbox.dba.getAllLessonNames();
        } else {
            lessons = Toolbox.dba.getAllUserLessonNames();
        }
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, lessons);
        list.setAdapter(adapter);
    }

    private void makeNewCollection() {
        final Activity parent = this;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Collection");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toolbox.hideKeyboard(parent, input);
                String name = input.getText().toString();
                
                // don't allow end users to create collections starting with integers, this
                // will guarantee no collision with admin-defined collections which are numbered
                if(!isAdmin && name.matches("^[0-9].*")) {
                	Toolbox.showToast(getApplicationContext(), "User collection names cannot start with a number");
                	return;
                }
                if (name.length() == 0) {
                    Toolbox.showToast(getApplicationContext(), "Invalid name");
                    return;
                }
                
                Lesson lesson = new Lesson(!isAdmin);
                lesson.setName(name);
                lesson.addWord(word);
                Toolbox.dba.addLesson(lesson);

                setResult(RESULT_OK);
                finish();
            }
        });
        
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toolbox.hideKeyboard(parent, input);
            }
        });
        
        AlertDialog dialog = builder.create();
        
        Toolbox.showKeyboard(dialog);
        dialog.show();
    }
}
