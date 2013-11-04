package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.trace2learn.TraceLibrary.Database.DbAdapter;
import com.trace2learn.TraceLibrary.Database.LessonItem;


public class Toolbox {

    // SharedPreferences
    public static final String PREFS_FILE        = "T2L_prefs";
    public static final String PREFS_IS_ADMIN    = "isAdmin";
    public static final String PREFS_QUIZ_MODE   = "quizMode";
    public static final String PREFS_FIRST_START = "firstStart";
    public static final String PREFS_PHRASE_MODE = "phraseMode";
    public static final String PREFS_IS_FULL_VER = "isFullVersion";
    public static final String PREFS_LAST_VER_NUMBER = "lastVersionNumber";
    public static final String PREFS_LAST_VIEWED_LESSON = "lastViewedLesson";
    public static final String PREFS_LAST_USER_QUERY = "lastUserQuery";
    public static final String PREFS_SHOW_WORD_DEFS = "showWordDefs";
    public static final String PREFS_QUIZ_DIFFICULTY = "quizDifficulty";
    public static final String PREFS_QUIZ_LIFETIME_ATTEMPTED = "quizLifeAttempted";
    public static final String PREFS_QUIZ_LIFETIME_CORRECT = "quizLifeCorrect";
    
    // Key-Value Pairs
    public static final String PINYIN_KEY = "pinyin";
    public static final String SOUND_KEY  = "sound";
    public static final String ID_KEY     = "id";
    
    // Initializing Database
    public static final String INIT_DB_NAME = "initial.jet";
    
    // Package names
    public static final String PACKAGE_TRIAL = "com.trace2learn.TraceMe.Free";
    public static final String PACKAGE_CH_TRADITIONAL = "com.trace2learn.Trasee";
    public static final String PACKAGE_CH_SIMPLIFIED = "com.trace2learn.Trasee.Simplified";
    
    // URLs
    public static final String LINK_APP_STORE = "market://search?q=pub:Aggria";
    public static final String LINK_FACEBOOK = "http://www.facebook.com/TraseeChinese";
    
    // Character Cache - all previously queried characters
    private static List<LessonItem> characterCache;
    private static List<String> characterIdCache;
    
    //Character Query Cache
    private static HashMap<String, List<LessonItem>> characterQueryCache;
     
    // All characters - will be used for admin functions only
    private static List<LessonItem> allChars;
    
    // Screen size, ideal bitmap height & fallback stroke width
    private static String detectedScreenSize = "Generic Screen";
    private static String detectedScreenDensity = "Default";
    private static int bitmapHeight = 64;
    private static float traceStrokeWidth = 1.0F;
    private static float penStrokeWidth = 1.0F;
    
    
    public static DbAdapter dba;
    public static boolean dbaOpened = false;
    
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
    
    public static void showToast(String msg){
    	Toolbox.showToast(dba.mCtx, msg);
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

    public static void showAboutPopup(Activity parentActy){
        try {

        	AlertDialog.Builder builder = new AlertDialog.Builder(parentActy);
            PackageManager pm = parentActy.getPackageManager();
            builder.setIcon(parentActy.getApplicationInfo().loadIcon(pm));
            builder.setTitle(parentActy.getApplicationInfo().loadLabel(pm));

            Resources rc = parentActy.getResources();  
            
            String msg = (String) rc.getText(R.string.aboutCredits);
            msg += "\n" + Toolbox.LINK_FACEBOOK;
            msg += "\n\nVersion: ";
            msg += pm.getPackageInfo(parentActy.getPackageName(), 0).versionName;
            msg += "\nDetected: ";
            msg += detectedScreenSize;
            msg += "\nScreen Density: ";
            msg += detectedScreenDensity;
            
            SpannableString span = new SpannableString(msg);
            Linkify.addLinks(span, Linkify.WEB_URLS);
            builder.setMessage(span);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    }
            });
            
            AlertDialog dlg = builder.show();            
            // make text view clickable.  must be called after show()
            TextView dlgMsg = (TextView)dlg.findViewById(android.R.id.message);
            dlgMsg.setMovementMethod(LinkMovementMethod.getInstance());            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void initDbAdapter(Context context, boolean initializeChars) {
        if (dbaOpened) {
            return;
        }
        dbaOpened = true;
        dba = new DbAdapter(context);
        dba.open();
        if (initializeChars) getAllCharacters();
        
        // initialize cache lists
        characterCache = new ArrayList<LessonItem>();
        characterIdCache = new ArrayList<String>();
        characterQueryCache = new HashMap<String, List<LessonItem>>();
    }
    
    public static void resetDbAdapter() {
    	dba.close();
    	dba = null;
    	dbaOpened = false;
    	
        characterCache.clear();
        characterIdCache.clear();
        characterQueryCache.clear(); 
    }
    
    // only to be used by admin functions - user-facing functions should use the
    // character cache which is faster and less memory-intensive
    public static List<LessonItem> getAllCharacters() {
        if(allChars == null || allChars.size() == 0)
        	allChars = dba.getAllChars();
    	return allChars;
    }

    public static List<LessonItem> getMatchingChars(String queryString) {
    	// look in query cache first
    	if(characterQueryCache.containsKey(queryString))
    		return characterQueryCache.get(queryString);

    	// run query against the database
    	List<LessonItem> newList = dba.getMatchingChars(queryString);
    	characterQueryCache.put(queryString, newList);
    	addToCharCache(newList);
    	return newList;
    }
    
    // character cache size will vary based on recent queries
    public static List<LessonItem> getCachedCharacters() {
    	return characterCache;
    }
    
    private static void addToCharCache(List<LessonItem> newList) {
    	for(LessonItem c : newList) {
	    	String id = c.getStringId();
	    	if(!characterIdCache.contains(id)) {
	    		characterIdCache.add(id);
	    		characterCache.add(c);    		
	    	}
    	}
    }
    
    public static void promptAppUpgrade(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.upgrade_app);
        builder.setMessage(R.string.upgrade_app_text);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	Intent intent = new Intent(Intent.ACTION_VIEW);
            	intent.setData(Uri.parse(LINK_APP_STORE));
            	context.startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.show();
    }
    
    public static String xmlEncode(String src) {
        return src.replace("&", "&amp;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    }
    
    public static String xmlDecode(String xml) {
        String out = xml;
        while (out.contains("&amp;") ||
                out.contains("&quot;") || 
                out.contains("&apos;") ||
                out.contains("&lt;") ||
                out.contains("&gt;")) {
            out = out.replace("&amp;", "&")
                    .replace("&quot;", "\"")
                    .replace("&apos;", "'")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">");
        }
        return out;
    }
    
    public static void determineScreenSize(final Context ctx) {
    	
    	Configuration conf = ctx.getResources().getConfiguration();
    	DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
    	int longDim = (metrics.heightPixels > metrics.widthPixels) ? metrics.heightPixels : metrics.widthPixels;

        if ((conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {     
        	detectedScreenSize = "XLarge Screen";
            bitmapHeight = longDim / 15;
        }
        else if ((conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {     
        	detectedScreenSize = "Large Screen";
            bitmapHeight = longDim / 15;
        }
        else if ((conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {     
        	detectedScreenSize = "Normal Screen";
            bitmapHeight = longDim / 12;
        } 
        else if ((conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {     
        	detectedScreenSize = "Small Screen";
            bitmapHeight = longDim / 8;
        }
        else {
        	// default to normal
        	bitmapHeight = longDim / 12;
        }
        
        int densityDpi = metrics.densityDpi;
        switch(densityDpi) {
        case DisplayMetrics.DENSITY_LOW:
        	detectedScreenDensity = "Low (" + densityDpi + "dpi)";
        	break;
        case DisplayMetrics.DENSITY_MEDIUM:
        	detectedScreenDensity = "Medium (" + densityDpi + "dpi)";
        	break;
        case DisplayMetrics.DENSITY_HIGH:
        	detectedScreenDensity = "High (" + densityDpi + "dpi)";
        	break;
        case DisplayMetrics.DENSITY_XHIGH:
        	detectedScreenDensity = "XHigh (" + densityDpi + "dpi)";
        	break;
        }
        
        // set fallback stroke dimensions, in case deriving from canvas size fails
        traceStrokeWidth = longDim /50F;
        penStrokeWidth = longDim / 75F;
    	
    }
    
    public static boolean isSmallScreen(){return detectedScreenSize.equals("Small Screen");}
    
    public static String screenSize(){return detectedScreenSize;}
    
    public static int bitmapHeight(){return bitmapHeight;}
    
    public static float traceStrokeWidth(){return traceStrokeWidth;}
    
    public static float penStrokeWidth(){return penStrokeWidth;}
    
}
