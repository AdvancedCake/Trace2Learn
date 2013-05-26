package com.trace2learn.TraceLibrary;

import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.Toast;


public class Toolbox {

    // SharedPreferences
    public static final String PREFS_FILE        = "T2L_prefs";
    public static final String PREFS_IS_ADMIN    = "isAdmin";
    public static final String PREFS_QUIZ_MODE   = "quizMode";
    public static final String PREFS_FIRST_START = "firstStart";
    public static final String PREFS_PHRASE_MODE = "phraseMode";
    public static final String PREFS_IS_FULL_VER = "isFullVersion";
    
    // Key-Value Pairs
    public static final String PINYIN_KEY = "pinyin";
    public static final String SOUND_KEY  = "sound";
    public static final String ID_KEY  = "id";
    
    // Sound Playback
    public static final float VOLUME = 1;
    
    public static final Locale locale = Locale.getDefault();
    
    public static PopupWindow popup;
    
    /**
     * If query is less than or equal to exact characters long, containsMatch
     * will return true if and only if source.equals(query). Otherwise,
     * containsMatch will return true if query is a substring of source. The
     * function ignores case.
     * 
     * @param exact The maximum length for which an exact match is required
     * @param source The original string to be matched to
     * @param query The string in question
     * @return
     */
    public static boolean containsMatch(int exact, String source, String query) {
        if (query.length() <= exact) {
            return query.equalsIgnoreCase(source);
        } else  {
            source = source.toLowerCase(locale);
            query  = query.toLowerCase(locale);
            
            return source.contains(query);
        }
    }
    
    /**
     * Show a toast message. Duration is Toast.LENGTH_SHORT.
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * @param dialog
     */
    public static void showKeyboard(Dialog dialog) {
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
    
    /**
     * @param activity
     * @param view
     */
    public static void hideKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) 
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showAboutPopup(Activity parentActivity){
        try {

            AlertDialog dlg = new AlertDialog.Builder(parentActivity).create();
            Resources rc = parentActivity.getResources();
            dlg.setIcon(R.drawable.logo);
            dlg.setTitle(rc.getString(R.string.user_app_name) + "\n\n" + rc.getString(R.string.app_subtitle));
            dlg.setMessage(rc.getText(R.string.aboutCredits));
            dlg.setButton("M'kay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    }
            });	
            dlg.show();        	
        	
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}
