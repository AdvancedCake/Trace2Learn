package com.trace2learn.TraceLibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Hashtable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.R;
import com.trace2learn.TraceLibrary.Database.DbAdapter;
import com.trace2learn.TraceLibrary.Database.LessonCharacter;
import com.trace2learn.TraceLibrary.Database.LessonItem.ItemType;
import com.trace2learn.TraceLibrary.Database.LessonWord;

public class MainAdminActivity extends TraceListActivity {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    static final String[] APPS = new String[] {
        "Create Character", 
        "Create Phrase",
        "Browse All Characters",
        "Browse All Phrases",
        "Browse All Collections",
        "Export To File",
        "Import From File",
        "Export SQLite Database",
        "Integrity Check"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        prefs = getSharedPreferences(Toolbox.PREFS_FILE, MODE_PRIVATE);
        editor = prefs.edit();

        // Set admin permissions
        editor.putBoolean(Toolbox.PREFS_IS_ADMIN, true);
        editor.commit();
        
        // Character Cache
        Toolbox.initDba(getApplicationContext(), true);

        setListAdapter(new ArrayAdapter<String>(this, R.layout.main_menu, APPS));

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        final Context c = this;

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, 
                    int position, long id) {
                CharSequence clicked = ((TextView) view).getText();
                if(clicked.equals(APPS[0])) { // "Create Character"
                    Intent i = new Intent().setClass(c, ViewCharacterActivity.class);
                    startActivity(i);
                }
                else if(clicked.equals(APPS[1])) { // "Create Word"
                    Intent i = new Intent(c, CreateWordActivity.class);
                    startActivity(i);
                }
                else if(clicked.equals(APPS[2])) { // "Browse All Characters"
                    Intent i = new Intent(c, BrowseCharactersActivity.class);
                    startActivity(i);
                }
                else if(clicked.equals(APPS[3])) { // "Browse All Words"
                    Intent i = new Intent(c, BrowseWordsActivity.class);
                    startActivity(i);
                }
                else if (clicked.equals(APPS[4])) { // "Browse All Collections"
                    Intent i = new Intent(c, BrowseLessonsActivity.class);
                    startActivity(i);
                }
                else if (clicked.equals(APPS[5])) { // "Export To File"
                    Intent intent = new Intent(c, ShoppingCartActivity.class);
                    Bundle bun = new Bundle();
                    bun.putString("type", "lesson");
                    intent.putExtras(bun);
                    startActivity(intent);
                }
                else if (clicked.equals(APPS[6])) { // "Import From File"
                    Intent intent = new Intent(c, FilePickerActivity.class);
                    startActivity(intent);
                }
                else if (clicked.equals(APPS[7])) { // "Export SQLite DB"
                    exportDatabase();
                }
                else if (clicked.equals(APPS[8])) { // "Integrity Chek"
                    integrityCheck();
                }

            }
        }
        	);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void exportDatabase() {
        try {
            Log.i("Export DB", "Attempting to export database");
            // Open the .db file
            String dbPath = getDatabasePath(
                    DbAdapter.DATABASE_NAME).getAbsolutePath();
            InputStream is = new FileInputStream(dbPath);

            // Copy the database into the destination
            String outPath =
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/data/" + getString(R.string.file_dir_name);
            File out = new File(outPath);
            out.mkdirs();
            out = new File(outPath + "/" + Toolbox.INIT_DB_NAME);
            if (out.exists()) {
                out.delete();
            }
            OutputStream os = new FileOutputStream(outPath + "/" + Toolbox.INIT_DB_NAME);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0){
                os.write(buffer, 0, length);
            }
            os.flush();

            os.close();
            is.close();
            Log.i("Export DB", "Successful");
            Toolbox.showToast(getApplicationContext(), "Database exported");
        } catch (Exception e) {
            Log.e("Export DB", e.getClass().getName() + ": " +
                    e.getMessage());
            e.printStackTrace();
        }
    }

    private void integrityCheck() {
    	// - flag characters without id tag
    	// - flag characters without pinyin tag
    	// - flag duplicate pinyin tags
    	// - flag phrases without pinyin tag
    	
       
        String msg = ""; 		
        Hashtable<String, String> idMap = new Hashtable<String, String> ();
        List<String> cids = Toolbox.dba.getAllCharIds();
        for(String id : cids){
        	LessonCharacter ch = Toolbox.dba.getCharacterById(id);
        	if(!ch.hasKey(Toolbox.PINYIN_KEY))
        		msg = msg + "Missing pinyin for " + ch.getTagsToString() + "\n";
        	if(!ch.hasKey(Toolbox.ID_KEY))
        		msg = msg + "Missing id for " + ch.getTagsToString() + "\n";
        	// store id on map & check uniqueness
        	else {
        		if(idMap.contains(ch.getValue(Toolbox.ID_KEY))) 
        			msg = msg + "Repeat id for " + ch.getKeyValuesToString() + "\n";
        		idMap.put(ch.getValue(Toolbox.ID_KEY),"found");
        	}	
        }
        
        List<String> wids = Toolbox.dba.getAllWordIds();
        for(String wid : wids){
        	LessonWord wd = Toolbox.dba.getWordById(wid);
        	if(!wd.hasKey(Toolbox.PINYIN_KEY))
        		msg = msg + "Missing pinyin for phrase " + 
        				Toolbox.dba.getKeyValues(wid, ItemType.WORD) + "/" +
        				wd.getTagsToString() + "\n";        	
        }
        
        if(msg == "") msg = "No issues found!";
        
        AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.setTitle("Integrity Alert");
        dlg.setMessage(msg);
        dlg.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                }
        });	
        dlg.setIcon(R.drawable.logo);
        dlg.show();
    }        

}
