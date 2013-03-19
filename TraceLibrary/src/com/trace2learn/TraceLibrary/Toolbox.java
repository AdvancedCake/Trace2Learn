package com.trace2learn.TraceLibrary;

import java.util.Locale;

import android.content.Context;
import android.widget.Toast;

public class Toolbox {

    public static final String PREFS_FILE      = "T2L_prefs";
    public static final String PREFS_QUIZ_MODE = "quizMode";
    
    public static final String PINYIN_KEY = "pinyin";
    
    public static final Locale locale = Locale.getDefault();
    
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
    
}
