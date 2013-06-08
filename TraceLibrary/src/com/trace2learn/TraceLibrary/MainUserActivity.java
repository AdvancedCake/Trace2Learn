package com.trace2learn.TraceLibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.DbAdapter;

public class MainUserActivity extends TraceBaseActivity {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    
    ImageView introduction;
    ImageView createPhrase;
    ImageView browseCollections;
    ImageView upgrade;
    
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
        Toolbox.initDba(getApplicationContext(), true);
        
        if (!isFullVer) {
            ((TextView) findViewById(R.id.title)).setText(R.string.user_app_name_free);
            findViewById(R.id.upgrade).setVisibility(View.VISIBLE);
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
        upgrade           = (ImageView) findViewById(R.id.upgrade);
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
                buildPhrase();
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
        
        upgrade.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Toolbox.APP_STORE_LINK));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
    }
    
    private void checkFirstStart() {

    	// get currently installed app version
    	int newVersion = 0;
    	try {
    		PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        	newVersion = packageInfo.versionCode;
    	}
    	catch (Exception e){}
    	
    		
    	// initialize the db if either it's a fresh install or there is a version change
        boolean firstStart = prefs.getBoolean(Toolbox.PREFS_FIRST_START, true);
        int lastVersion = prefs.getInt(Toolbox.PREFS_LAST_VER_NUMBER, 0);
        
        if (firstStart || newVersion > lastVersion) {
             if (initializeDatabase(lastVersion, newVersion)) {
                 editor.putBoolean(Toolbox.PREFS_FIRST_START, false);
             }
             editor.putInt(Toolbox.PREFS_LAST_VER_NUMBER, newVersion);
             editor.commit();
        }
    }
    
    private boolean initializeDatabase(int lastVersion, int newVersion) {
        Log.i("Initialize DB", "Attempting to import database");

        String dbPath = getDatabasePath(DbAdapter.DATABASE_NAME).getAbsolutePath();

        try {
        	if(lastVersion > 0) {
		        // check for any user-defined collections - need to temporarily initialize the old database
		        Toolbox.initDba(getApplicationContext(), false);
		        List<String> userCollNames = Toolbox.dba.getAllUserLessonNames();
		        List<Lesson> userColls = Toolbox.dba.getAllUserLessons();
		        Toolbox.resetDba();
		        
		        String initMsg = "Installing the latest database" + "\n"
		        		+ "Previous version: " + lastVersion + "\n"
		        		+ "New version: " + newVersion + "\n";
		        
		        if(userCollNames.size() > 0)
		        	initMsg = initMsg + "Existing custom collections will be retained: " + userCollNames;
	
	        	// notify user of db upgrade
	            AlertDialog.Builder builder = new AlertDialog.Builder(this);
	            Resources rc = getResources();
	            builder.setIcon(R.drawable.logo);
	            builder.setTitle(rc.getString(R.string.user_app_name) + " " + rc.getString(R.string.app_subtitle));
	            builder.setMessage(initMsg);
	            builder.setPositiveButton("Sounds Good", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}
	            });	
	            builder.show();  
        	}
            
            // Open the db file in your assets directory
            InputStream is = getBaseContext().getAssets().open(Toolbox.INIT_DB_NAME);

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
    
    /**
     * Checks that this is the full version of the app, then starts the
     * activity to build a phrase.
     */
    private void buildPhrase() {
        if (prefs.getBoolean(Toolbox.PREFS_IS_FULL_VER, false)) {
            Intent i = new Intent(getApplicationContext(),
                    CreateWordActivity.class);
            startActivity(i);
        } else {
            Toolbox.promptAppUpgrade(this);
        }
    }

}
