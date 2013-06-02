package com.trace2learn.TraceLibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.DbAdapter;

public class MainUserActivity extends TraceBaseActivity {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    
    ImageView introduction;
    ImageView createPhrase;
    ImageView browseCollections;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_user);
        
        getViews();
        getHandlers();
        
        prefs = getSharedPreferences(Toolbox.PREFS_FILE, MODE_PRIVATE);
        editor = prefs.edit();

        checkFirstStart();

        // Set user permissions
        boolean isFullVer = getApplicationContext().getPackageName().equals("com.trace2learn.TraceMe");
        editor.putBoolean(Toolbox.PREFS_IS_FULL_VER, isFullVer);
        editor.putBoolean(Toolbox.PREFS_IS_ADMIN, false);
        editor.commit();
        
        // Character Cache
        Toolbox.dba = new DbAdapter(getApplicationContext());
        Toolbox.dba.open();
        Toolbox.characters = Toolbox.dba.getAllChars();
        
        if (!isFullVer) {
            ((TextView) findViewById(R.id.title)).setText(R.string.user_app_name_free);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    private void getViews() {
        introduction      = (ImageView) findViewById(R.id.introduction);
        createPhrase      = (ImageView) findViewById(R.id.create_phrase);
        browseCollections = (ImageView) findViewById(R.id.browse_collections);
    }
    
    private void getHandlers() {
        introduction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        IntroductionActivity.class);
                startActivity(i);
            }
        });
        
        createPhrase.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        CreateWordActivity.class);
                startActivity(i);
            }
        });
        
        browseCollections.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        BrowseLessonsActivity.class);
                startActivity(i);
            }
        });
    }
    
    private void checkFirstStart() {
        boolean firstStart = prefs.getBoolean(Toolbox.PREFS_FIRST_START, true);
        if (firstStart) {
             if (initializeDatabase()) {
                 // Log previously started
                 editor.putBoolean(Toolbox.PREFS_FIRST_START, false);
                 editor.commit();
             }
        }
    }
    
    private boolean initializeDatabase() {
        Log.i("Initialize DB", "Attempting to import database");

        String dbPath = getDatabasePath(DbAdapter.DATABASE_NAME).getAbsolutePath();

        try {
            // Open the .db file in your assets directory
            InputStream is = getBaseContext().getAssets().open("initial.jet");

            // Copy the database into the destination
            File out = new File(dbPath);
            out.mkdirs();
            out.delete();
            OutputStream os = new FileOutputStream(dbPath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0){
                os.write(buffer, 0, length);
            }
            os.flush();

            os.close();
            is.close();
            Log.i("Initialize DB", "Database successfully imported");
            return true;
        } catch (Exception e) {
            Log.e("Initialize DB", e.getClass().getName() + ": " +
                    e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
