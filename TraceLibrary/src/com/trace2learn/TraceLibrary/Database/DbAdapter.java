package com.trace2learn.TraceLibrary.Database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PointF;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.trace2learn.TraceLibrary.Database.LessonItem.ItemType;

public class DbAdapter {
	
    public static final String CHAR_ID = "_id";
    public static final String WORDS_ID = "_id";
    public static final String LESSONS_ID = "_id";
    public static final String LESSONTAG_ID = "_id";
    
    public static final String CHARTAG_ID = "_id";
    public static final String CHARTAG_TAG= "tag";
    public static final String CHARKEYVALUES_ID ="_id";
    public static final String CHARKEYVALUES_KEY = "key";
    public static final String CHARKEYVALUES_VALUE = "value";
    public static final String WORDTAG_ID = "_id";
    public static final String WORDTAG_TAG= "tag";
    public static final String WORDKEYVALUES_ID ="_id";
    public static final String WORDKEYVALUES_KEY = "key";
    public static final String WORDKEYVALUES_VALUE = "value";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private static final HashMap<LessonCategory, String> categoryColumns =
            new HashMap<LessonCategory, String>();

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE_CHAR =
    		"CREATE TABLE Character (_id TEXT PRIMARY KEY," +
    		"sort INTEGER);";
    
    private static final String DATABASE_CREATE_CHARTAG =
            "CREATE TABLE CharacterTag (_id TEXT, " +
            "tag TEXT NOT NULL, " +
            "sort INTEGER, " +
            "FOREIGN KEY(_id) REFERENCES Character(_id));";

    private static final String DATABASE_CREATE_CHARKEYVALUES =
            "CREATE TABLE CharKeyValues (_id TEXT, " +
            "key TEXT NOT NULL, " +
            "value TEXT NOT NULL, " +
            "sort INTEGER, " +
            "PRIMARY KEY (_id, key), " +
            "FOREIGN KEY(_id) REFERENCES Character(_id));";
    
    private static final String DATABASE_CREATE_CHAR_DETAILS =
            "CREATE TABLE CharacterDetails (_id TEXT PRIMARY KEY, " +
            "CharId TEXT, " +
            "Stroke INTEGER NOT NULL, " +
            "PointX DOUBLE NOT NULL, " +
            "PointY DOUBLE NOT NULL," +
            "OrderPoint INTEGER NOT NULL, " +
            "FOREIGN KEY(CharId) REFERENCES Character(_id));";
    
    private static final String DATABASE_CREATE_WORDS = 
    		"CREATE TABLE Words (_id TEXT PRIMARY KEY," +
    		"sort INTEGER" +
    		"userDefined INTEGER);";
    
    private static final String DATABASE_CREATE_WORDS_DETAILS =
            "CREATE TABLE WordsDetails (_id TEXT," +
            "CharId TEXT," +
            "WordOrder INTEGER NOT NULL," +
            "FOREIGN KEY(CharId) REFERENCES Character(_id)," +
            "FOREIGN KEY(_id) REFERENCES Words(_id));";
    
    private static final String DATABASE_CREATE_WORDSTAG =
            "CREATE TABLE WordsTag (_id TEXT, " +
            "tag TEXT NOT NULL, " +
            "sort INTEGER, " +
            "FOREIGN KEY(_id) REFERENCES Words(_id));";
    
    private static final String DATABASE_CREATE_WORDKEYVALUES =
            "CREATE TABLE WordKeyValues (_id TEXT, " +
            "key TEXT NOT NULL, " +
            "value TEXT NOT NULL, " +
            "sort INTEGER, " +
            "PRIMARY KEY (_id, key), " +
            "FOREIGN KEY(_id) REFERENCES Words(_id));";    

    private static final String DATABASE_CREATE_LESSONS=
            "CREATE TABLE Lessons (_id TEXT PRIMARY KEY,"+
            "name TEXT, " +
            "sort INTEGER, " +
            "narrative TEXT, " +
            "userDefined INTEGER, " +
            "catShapeAndStructure INTEGER, " +
            "catMeaning INTEGER, " +
            "catPhonetic INTEGER, " +
            "catGrammar INTEGER);";
        
    private static final String DATABASE_CREATE_LESSONS_DETAILS =
            "CREATE TABLE LessonsDetails (" +
            "LessonId TEXT, " +
            "WordId TEXT," +
            "LessonOrder INTEGER NOT NULL, " +
            "FOREIGN KEY(LessonId) REFERENCES Lessons(_id)," +
            "FOREIGN KEY(WordId) REFERENCES Words(_id));";
    
    private static final String DATABASE_CREATE_LESSONTAG =
            "CREATE TABLE LessonTag (_id TEXT, " +
            "tag TEXT NOT NULL, " +
            "sort INTEGER, " +
            "FOREIGN KEY(_id) REFERENCES Lessons(_id));";
    
    //DB Drop Statements
    
    private static final String DATABASE_DROP_CHAR = 
    		"DROP TABLE IF EXISTS Character";
    private static final String DATABASE_DROP_CHARTAG = 
    		"DROP TABLE IF EXISTS CharacterTag";
    private static final String DATABASE_DROP_CHARKEYVALUES = 
    		"DROP TABLE IF EXISTS CharKeyValues";
    private static final String DATABASE_DROP_CHAR_DETAILS = 
    		"DROP TABLE IF EXISTS CharacterDetails";
    private static final String DATABASE_DROP_WORDS = 
    		"DROP TABLE IF EXISTS Words";
    private static final String DATABASE_DROP_WORDKEYVALUES = 
    		"DROP TABLE IF EXISTS WordKeyValues";
    private static final String DATABASE_DROP_WORDS_DETAILS = 
    		"DROP TABLE IF EXISTS WordsDetails";
    private static final String DATABASE_DROP_WORDSTAG = 
    		"DROP TABLE IF EXISTS WordsTag";
    private static final String DATABASE_DROP_LESSONS = 
    		"DROP TABLE IF EXISTS Lessons";
    private static final String DATABASE_DROP_LESSONS_DETAILS = 
    		"DROP TABLE IF EXISTS LessonsDetails";
    private static final String DATABASE_DROP_LESSONTAG = 
    		"DROP TABLE IF EXISTS LessonTag";
    
    
    public static final String DATABASE_NAME         = "CharTags";
    public static final String CHAR_TABLE            = "Character";
    public static final String CHAR_DETAILS_TABLE    = "CharacterDetails";
    public static final String CHARTAG_TABLE         = "CharacterTag";
    public static final String CHARKEYVALUES_TABLE   = "CharKeyValues";
    public static final String WORDTAG_TABLE         = "WordsTag";
    public static final String WORDS_TABLE           = "Words";
    public static final String WORDKEYVALUES_TABLE   = "WordKeyValues";
    public static final String WORDS_DETAILS_TABLE   = "WordsDetails";
    public static final String LESSONS_TABLE         = "Lessons";
    public static final String LESSONS_DETAILS_TABLE = "LessonsDetails";
    public static final String LESSONTAG_TABLE       = "LessonTag";
    
    
    private static final int DATABASE_VERSION = 20130502;

    
    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_CHAR);
            db.execSQL(DATABASE_CREATE_CHARTAG);
            db.execSQL(DATABASE_CREATE_CHARKEYVALUES);
            db.execSQL(DATABASE_CREATE_CHAR_DETAILS);
            db.execSQL(DATABASE_CREATE_WORDS);
            db.execSQL(DATABASE_CREATE_WORDKEYVALUES);
            db.execSQL(DATABASE_CREATE_WORDS_DETAILS);
            db.execSQL(DATABASE_CREATE_WORDSTAG);
            db.execSQL(DATABASE_CREATE_LESSONS);
            db.execSQL(DATABASE_CREATE_LESSONS_DETAILS);
            db.execSQL(DATABASE_CREATE_LESSONTAG);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
            Log.w("DbAdapter.onUpgrade",
                    "Upgrading database from version " + oldVer + " to " +
                    newVer + ", which will destroy all old data");
            db.execSQL(DATABASE_DROP_CHAR);
            db.execSQL(DATABASE_DROP_CHARTAG);
            db.execSQL(DATABASE_DROP_CHARKEYVALUES);
            db.execSQL(DATABASE_DROP_CHAR_DETAILS);
            db.execSQL(DATABASE_DROP_WORDS);
            db.execSQL(DATABASE_DROP_WORDKEYVALUES);
            db.execSQL(DATABASE_DROP_WORDS_DETAILS);
            db.execSQL(DATABASE_DROP_WORDSTAG);
            db.execSQL(DATABASE_DROP_LESSONS);
            db.execSQL(DATABASE_DROP_LESSONS_DETAILS);
            db.execSQL(DATABASE_DROP_LESSONTAG);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    /**
     * Open the CharTags database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        
        // Initialize LessonCategory column names
        if (categoryColumns.size() == 0) {
            categoryColumns.put(LessonCategory.SHAPE_AND_STRUCTURE, "catShapeAndStructure");
            categoryColumns.put(LessonCategory.MEANING,             "catMeaning");
            categoryColumns.put(LessonCategory.PHONETIC,            "catPhonetic");
            categoryColumns.put(LessonCategory.GRAMMAR,             "catGrammar");
        }
        
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    /**
     * Create a new word tag. If the word tag is
     * successfully created return the new Id for that tag, otherwise return
     * a null to indicate failure.
     * 
     * @param id the id of the tag
     * @param tag the text of the tag
     * @return id or null if failed
     */
    public String createWordTags(String id, String tag) {
        Cursor cur = mDb.query(WORDTAG_TABLE, new String[] {"sort"}, 
                               "_id='" + id + "'", null, null, null, "sort DESC", "1");
        int sort = 1;
        if (cur != null) {
            if (cur.moveToFirst()) {
                sort = cur.getInt(cur.getColumnIndexOrThrow("sort")) + 1;
            }
        }
        else {
            return null;
        }
        cur.close();

        ContentValues initialValues = new ContentValues();
        initialValues.put(WORDTAG_ID, id);
        initialValues.put(WORDTAG_TAG, tag);
        initialValues.put("sort", sort);

        long rowid = mDb.insert(WORDTAG_TABLE, null, initialValues);
        if(rowid==-1){
        	return null;
        }
        else return id;
    }
    
    /**
     * Create a new lesson tag. If the lesson tag is
     * successfully created return the new rowId for that tag, otherwise sreturn
     * a -1 to indicate failure.
     * 
     * @param id the id of the Lesson the tag belongs to
     * @param tag the text of the tag
     * @return  the id of the Lesson the tag belongs to, or null if failed
     */
    public String createLessonTags(String id, String tag) {
        Cursor cur = mDb.query(LESSONTAG_TABLE, new String[] {"sort"}, 
                               "_id='" + id + "'", null, null, null, "sort DESC", "1");
        int sort = 1;
        if (cur != null) {
            if (cur.moveToFirst()) {
                sort = cur.getInt(cur.getColumnIndexOrThrow("sort")) + 1;
            }
        }
        else {
            return null;
        }
        cur.close();


        ContentValues initialValues = new ContentValues();
        initialValues.put(LESSONTAG_ID, id);
        initialValues.put("tag", tag);
        initialValues.put("sort", sort);

        long rowid = mDb.insert(LESSONTAG_TABLE, null, initialValues);
        if(rowid==-1) return null;
        else return id;
    }
    
    /**
     * Create a new character tag. 
     * 
     * @param id the id of the char
     * @param tag the text of the tag
     * @return true if tags were created successfully, false otherwise
     */
    public boolean createCharTags(String id, String tag) {
        Cursor cur = mDb.query(CHARTAG_TABLE, new String[] {"sort"}, 
                               "_id='" + id + "'", null, null, null, "sort DESC", "1");
        int sort = 1;
        if (cur != null) {
            if (cur.moveToFirst()) {
                sort = cur.getInt(cur.getColumnIndexOrThrow("sort")) + 1;
            }
        }
        else {
            return false;
        }
        cur.close();

        
        ContentValues initialValues = new ContentValues();
        initialValues.put(CHARTAG_ID, id);
        initialValues.put(CHARTAG_TAG, tag);
        initialValues.put("sort", sort);

        return mDb.insert(CHARTAG_TABLE, null, initialValues) != -1;
    }

    /**
     * Create a new (Key, Value) pair for a character/word. If the pair is
     * successfully created return the new rowId for that pair, otherwise return
     * -1 to indicate failure.
     * 
     * @param id the row_id of the item
     * @param key the text of the key
     * @param value the text of the value
     * @return whether creating was successful or unsuccessful
     */
    public boolean createKeyValue(String stringId, LessonItem.ItemType itemType, 
                               String key, String value) {
    	String table = "";
    	String idColumn = "";
    	String keyColumn = "";
    	String valueColumn = "";
    	switch (itemType)
    	{
    	case CHARACTER:
    		table = CHARKEYVALUES_TABLE;
    		keyColumn = CHARKEYVALUES_KEY;
    		valueColumn = CHARKEYVALUES_VALUE;
    		idColumn = CHARTAG_ID;
    		break;
    	case WORD:
    		table = WORDKEYVALUES_TABLE;
    		keyColumn = WORDKEYVALUES_KEY;
    		valueColumn = WORDKEYVALUES_VALUE;
    		idColumn = WORDTAG_ID;
    		break;
    	default:
    		Log.e("DbAdapter.createKeyValue",
    		        "This type does NOT support (Key, Value) pairs.");
    		return false;
    	}
    	
	    	// find new sort value
	        Cursor cur = mDb.query(table, new String[] {"sort"}, 
	                "_id='" + stringId + "'",
	                null, null, null, "sort DESC", "1");
	        int sort = 1;
	        if (cur != null) {
	            if (cur.moveToFirst()) {
	                sort = cur.getInt(cur.getColumnIndexOrThrow("sort")) + 1;
	            }
	        }
	        else {
	            return false;
	        }
	        cur.close();
	
	        
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(idColumn, stringId);
	        initialValues.put(keyColumn, key);
	        initialValues.put(valueColumn, value);
	        initialValues.put("sort", sort);
	
	        return mDb.insert(table, null, initialValues)!= -1;
    }

    
   /**
     * Delete the tag with the given rowId and tag
     * 
     * @param rowId id of tag to delete
     * @param tag text of tag to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteCharTag(String id, String tag) {
        return mDb.delete(CHARTAG_TABLE, CHARTAG_ID + "='" + id +
                "' AND " + CHARTAG_TAG + "='" + tag + "'", null) > 0;
    }
    
    /**
     * Delete the word tag with the given id and tag
     * 
     * @param id id of word tag belongs to
     * @param tag text of tag to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteWordTag(String id, String tag) {
        return mDb.delete(WORDTAG_TABLE,
                WORDTAG_ID + "='" + id + "' AND " + WORDTAG_TAG + "='" + tag + "'",
                null) > 0;
    }
   
    /**
     * Delete the (Key, Value) pair for a character/word with the given rowId and key
     * 
     * @param Id character/word id of keyvalue pair
     * @param key text of key to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteKeyValue(String stringId, LessonItem.ItemType itemType,
            String key) {
    	String table = "";
    	String idColumn = "";
    	String keyColumn = "";
    	switch (itemType)
    	{
    	case CHARACTER:
    		table = CHARKEYVALUES_TABLE;
    		keyColumn = CHARKEYVALUES_KEY;
    		idColumn = CHARTAG_ID;
    		break;
    	case WORD:
    		table = WORDKEYVALUES_TABLE;
    		keyColumn = WORDKEYVALUES_KEY;
    		idColumn = WORDTAG_ID;
    		break;
    	default:
    		Log.e("DbAdapter.deleteKeyValue",
    		        "This type does NOT support (Key, Value) pairs.");
    		return false;
    	}
        return mDb.delete(table, idColumn + "='" + stringId + "' AND " + keyColumn +
                "='" + key + "'", null) > 0;
    }      

    /**
     * Modify a character already in the database
     * @param c character to be modified to the database
     * @return true if change is pushed to DB.  False on error.
     */
    public boolean modifyCharacter(LessonCharacter c)
    {
    	mDb.beginTransaction();
    	String charId = c.getStringId();
    	//drop the current details
    	mDb.delete(CHAR_DETAILS_TABLE, "CharId = '" + charId + "'", null);
    	
    	//add each stroke to CHAR_DETAILS_TABLE
    	List<Stroke> l = c.getStrokes();
    	//stroke ordering
    	int strokeNumber=0;
    	for(Stroke s:l)
    	{
    		ContentValues strokeValues = new ContentValues();
    		strokeValues.put("CharId", charId);
    		strokeValues.put("Stroke", strokeNumber);
    		//point ordering
    		int pointNumber=0;
    		for(PointF p : s.getSamplePoints())
    		{
    			strokeValues.put("PointX", p.x);
        		strokeValues.put("PointY", p.y);
        		strokeValues.put("OrderPoint", pointNumber);
        		long success = mDb.insert(CHAR_DETAILS_TABLE, null, strokeValues);
        		if(success == -1)
        		{	
        			//if error
        			Log.e(CHAR_DETAILS_TABLE,"cannot add stroke");
        			mDb.endTransaction();
        			return false;
        		}
        		pointNumber++;
    		}
    		strokeNumber++;
    	}
    	
    	mDb.setTransactionSuccessful();
    	mDb.endTransaction();
    	return true;
    	
    }
    
    
    
    /**
     * Add a character to the database
     * @param c character to be added to the database
     * @return true if character is added to DB.  False on error.
     */
    public boolean addCharacter(LessonCharacter c)
    {
    	ContentValues initialCharValues = new ContentValues();
    	String charId = c.getStringId();
    	if (charId != null) { // id already initialized, keep it
    	    initialCharValues.put(CHAR_ID, charId);
            if (getCharacterById(charId) != null) {
                deleteCharacter(charId);
            }
    	}
    	else{
    		charId = makeUniqueId();
    		initialCharValues.put(CHAR_ID, charId);
    	}
    	
        mDb.beginTransaction();
    	long rowid = mDb.insert(CHAR_TABLE, "sort", initialCharValues);
    	if(rowid == -1)
    	{
    		//if error
    		Log.e(CHAR_TABLE, "cannot add new character to table " + CHAR_TABLE);
    		mDb.endTransaction();
    		return false;
    	}

        if (c.getStringId() == null) { // id not initialized
            c.setStringId(charId);
        }
        
        // To make the sort order the same as the ID, we need to update the row
        // after we get the ID, i.e. now.
        c.setSort(rowid); // sort value initialized to ID.
        initialCharValues.put("sort", c.getSort());
        mDb.update(CHAR_TABLE, initialCharValues, CHAR_ID + "='" + charId + "'", null);
    	
    	// if the given character has tags, copy them
    	for (String tag : c.getTags()) {
    		if (!createCharTags(charId, tag)) {
        		Log.e(CHAR_TABLE, 
        				"cannot add character's tag(" + tag + ") "
        				+ "to table " + CHARTAG_TABLE);
    			mDb.endTransaction();
        		return false;
    		}
    	}
    	
    	// if the given character has keyValues, copy them
    	for (Map.Entry<String, String> entry : c.getKeyValues().entrySet()) {
    		if (!createKeyValue(charId, c.getItemType(), entry.getKey(), entry.getValue())) {
        		Log.e(CHAR_TABLE, 
        				"cannot add character's Key-Value pair(" 
        				+ entry.getKey() + ", " + entry.getValue() + ") "
        				+ "to table " + CHARKEYVALUES_TABLE);
    			mDb.endTransaction();
        		return false;
    		}
    	}
    	
    	//add each stroke to CHAR_DETAILS_TABLE
    	List<Stroke> l = c.getStrokes();
    	//stroke ordering
    	int strokeNumber=0;
    	for(Stroke s : l)
    	{
    		ContentValues strokeValues = new ContentValues();
    		strokeValues.put("CharId", charId);
    		strokeValues.put("Stroke", strokeNumber);
    		//point ordering
    		int pointNumber=0;
    		for(PointF p : s.getSamplePoints())
    		{
    			strokeValues.put("PointX", p.x);
        		strokeValues.put("PointY", p.y);
        		strokeValues.put("OrderPoint", pointNumber);
        		long success = mDb.insert(CHAR_DETAILS_TABLE, null, strokeValues);
        		if(success == -1)
        		{	
        			//if error
        			Log.e(CHAR_DETAILS_TABLE,"cannot add stroke");
        			mDb.endTransaction();
        			return false;
        		}
        		pointNumber++;
    		}
    		strokeNumber++;
    	}    	 
    	
    	mDb.setTransactionSuccessful();
    	mDb.endTransaction();
    	return true;
    	
    }
    
    public boolean deleteCharacter(String id){
    	Cursor mCursor =
                mDb.query(true, CHAR_TABLE, new String[] {CHAR_ID}, CHAR_ID + "='" + id + "'", null,
                        null, null, null, null);
    	 if (mCursor == null) {
             return false;
         }
         mCursor.close();
    	 
    	 mCursor = mDb.query(true, WORDS_DETAILS_TABLE, new String[] {"CharId"}, "CharId ='" + id, null,
                 null, null, null, null);
    	 if(mCursor.getCount()>0){
    		 //Some word is using the character
    	     mCursor.close();
    		 return false;
    	 }
    	 else{
    	     mCursor.close();

    	     mDb.delete(CHAR_TABLE, CHAR_ID + "='" + id + "'", null);
    		 mDb.delete(CHAR_DETAILS_TABLE, "CharId = '" + id + "'", null);
    		 mCursor =  mDb.query(true, WORDS_DETAILS_TABLE, new String[] {WORDS_ID}, "CharId ='" + id + "'", null,
                     null, null, null, null);
    		 mCursor.moveToFirst();
    		 do {
 	        	if(mCursor.getCount()==0){
 	        		break;
 	        	}
 	        	String wordId = (mCursor.getString(mCursor.getColumnIndexOrThrow(WORDS_ID)));
 	        	mDb.delete(WORDS_TABLE, WORDS_ID + "='" + wordId + "'", null);
 	         }
 	         while(mCursor.moveToNext());
    		 mDb.delete(WORDS_DETAILS_TABLE, "CharId='"+id + "'", null);
    		 mDb.delete(CHARTAG_TABLE, CHAR_ID + "='" + id + "'", null);
    		 mDb.delete(CHARKEYVALUES_TABLE, CHAR_ID + "='" + id + "'", null);
    	 }
    	 mCursor.close();
    	 return true;
    }
    
    
    /**
     * Get a LessonCharacter from the database
     * @param id id of the LessonCharacter
     * @return The LessonCharacter if id exists, null otherwise.
     */
    public LessonCharacter getCharacterById(String id)
    {
    	Cursor mCursor =
    			mDb.query(true, CHAR_TABLE, new String[] {CHAR_ID}, CHAR_ID + "='" + id + "'", null,
    					null, null, null, null);
    	LessonCharacter c = new LessonCharacter();
    	//if the character doesn't exists
    	if (mCursor == null) {
    		return null;
    	} else if (mCursor.getCount() == 0) {
    	    mCursor.close();
    	    return null;
    	}
    	mCursor.close();

    	//grab its details (step one might not be necessary and might cause slow downs
    	// but it is for data consistency.
    	mCursor =
    			mDb.query(true, CHAR_DETAILS_TABLE, new String[] {"CharId", "Stroke","PointX","PointY"}, "CharId = '"+ id + "'", null,
    					null, null, "Stroke ASC, OrderPoint ASC", null);
    	mCursor.moveToFirst();
    	Stroke s = new Stroke();
    	if (mCursor.getCount() > 0) {
    		int strokeNumber = mCursor.getInt(mCursor.getColumnIndexOrThrow("Stroke"));

    		do {
    			if(mCursor.getCount()==0){
    				c.addStroke(s);
    				break;
    			}
    			if(strokeNumber != mCursor.getInt(mCursor.getColumnIndexOrThrow("Stroke")))
    			{
    				c.addStroke(s);
    				strokeNumber = mCursor.getInt(mCursor.getColumnIndexOrThrow("Stroke"));
    				s = new Stroke();
    			}
    			s.addPoint(mCursor.getFloat(mCursor.getColumnIndexOrThrow("PointX")),
    					mCursor.getFloat(mCursor.getColumnIndexOrThrow("PointY")));
    		}
    		while(mCursor.moveToNext());
    		c.addStroke(s);
    	}
    	c.setStringId(id);
    	mCursor.close();

    	mCursor =
    			mDb.query(true, CHAR_TABLE, new String[] {"sort"},
    					CHAR_ID + " = '"+ id + "'", null, null, null, null, null);
    	mCursor.moveToFirst();
    	if (mCursor.getCount() > 0) {
    		long sort = mCursor.getLong(mCursor.getColumnIndexOrThrow("sort"));
    		c.setSort(sort);
    	}
    	mCursor.close();

    	// get tags as well
    	c.setTagList(getCharacterTags(id));

    	// get keyValues as well
    	c.setKeyValues(getKeyValues(id, LessonItem.ItemType.CHARACTER));
    	
    	return c;
    }
    


    /**
     * Get a LessonWord from the database
     * @param id id of the LessonWord
     * @return The LessonWord if id exists, null otherwise.
     */
    public LessonWord getWordById(String id)
    {
        Cursor mCursor =
            mDb.query(true, WORDS_TABLE, new String[] {WORDS_ID}, WORDS_ID + "='" + id + "'", null,
                    null, null, null, null);
        LessonWord w = new LessonWord();
        //if the character doesn't exist
        if (mCursor == null) {
            return null;
        } else if (mCursor.getCount() == 0) {
            mCursor.close();
            return null;
        }
        mCursor.close();
        
        //grab its details (step one might not be necessary and might cause slow downs
        // but it is for data consistency.
        mCursor =
            mDb.query(true, WORDS_DETAILS_TABLE, new String[] {WORDS_ID, "CharId", "WordOrder"}, WORDS_ID + "='" + id + "'", null,
                    null, null, "WordOrder ASC", null);
        mCursor.moveToFirst();
        do {
        	if(mCursor.getCount()==0){
        		break;
        	}
        	String charId = mCursor.getString(mCursor.getColumnIndexOrThrow("CharId"));
        	Log.i("LOAD", "Char: " + charId);
        	w.addCharacter(charId);
        } while(mCursor.moveToNext());
        w.setStringId(id);
        mCursor.close();
        
        mCursor =
                mDb.query(true, WORDS_TABLE, new String[] {"sort"},
                        WORDS_ID + "='" + id + "'", null, null, null, null, null);
        mCursor.moveToFirst();
        long sort = mCursor.getLong(mCursor.getColumnIndexOrThrow("sort"));
        w.setSort(sort);
        
        w.setDatabase(this);

        mCursor.close();
        
    	// get tags as well
    	w.setTagList(getWordTags(id));

    	// get keyValues as well
    	w.setKeyValues(getKeyValues(id, LessonItem.ItemType.WORD));        

        return w;
    }
     
    /**
     * Add a word to the database
     * @param w word to be added to the database
     * @return true if word is added to DB.  False on error.
     */
    public boolean addWord(LessonWord w)
    {
    	mDb.beginTransaction();
    	//add to WORDS_TABLE
    	ContentValues initialWordsValues = new ContentValues();
    	String wordId = w.getStringId();
    	if (wordId != null) { // id already initialized, keep it
        	initialWordsValues.put(WORDS_ID, wordId);    	
            if (getWordById(wordId) != null) {
                deleteWord(wordId);
            }
    	}
    	else {
    		wordId = makeUniqueId();
    		initialWordsValues.put(WORDS_ID, wordId);
    	}
    	
    	long rowid = mDb.insert(WORDS_TABLE, null, initialWordsValues);
    	if (rowid == -1) {
    		//if error
    		Log.e(WORDS_TABLE, "cannot add new character to table "+WORDS_TABLE);
    		mDb.endTransaction();
    		return false;
    	}
    	
    	if (w.getStringId() == null) { // id not initialized, set it now
    	    w.setStringId(wordId);
    	}

        // To make the sort order the same as the ID, we need to update the row
        // after we get the ID, i.e. now.
        w.setSort(rowid); // sort value initialized to ID.
        initialWordsValues.put("sort", w.getSort());
        mDb.update(WORDS_TABLE, initialWordsValues, WORDS_ID + "='" + wordId + "'", null);
        
    	// if the given word has tags, copy them
    	for (String tag : w.getTags()) {
    		if (null == createWordTags(wordId, tag)) {
        		Log.e(CHAR_TABLE, 
        				"cannot add word's tag(" + tag + ") "
        				+ "to table " + WORDTAG_TABLE);
    			mDb.endTransaction();
        		return false;
    		}
    	}
    	
    	// if the given word has keyValues, copy them
    	for (Map.Entry<String, String> entry : w.getKeyValues().entrySet()) {
    		if (!createKeyValue(wordId, w.getItemType(), entry.getKey(), entry.getValue())) {
        		Log.e(WORDKEYVALUES_TABLE, 
        				"cannot add word's Key-Value pair(" 
        				+ entry.getKey() + ", " + entry.getValue() + ") "
        				+ "to table " + WORDKEYVALUES_TABLE);
    			mDb.endTransaction();
        		return false;
    		}
    	}    	        
    	
    	//add each character to WORDS_DETAILS_TABLE
    	List<String> l = w.getCharacterIds();
    	//character ordering
    	int charNumber=0;
    	for(String c:l)
    	{
    		ContentValues characterValues = new ContentValues();
    		characterValues.put("_id", wordId);
    		characterValues.put("CharId", c);
    		characterValues.put("WordOrder", charNumber);
    		long success = mDb.insert(WORDS_DETAILS_TABLE, null, characterValues);
    		if(success == -1)
    		{	
    			//if error
    			Log.e(WORDS_DETAILS_TABLE,"cannot add to table");
    			mDb.endTransaction();
    			return false;
    		}
    		charNumber++;
    	}
    	
    	mDb.setTransactionSuccessful();
    	mDb.endTransaction();
    	return true;
    }
    
    public boolean deleteWord(String id){
    	Cursor mCursor =
                mDb.query(true, WORDS_TABLE, new String[] {WORDS_ID}, WORDS_ID + "='" + id + "'", null,
                        null, null, null, null);
    	 if (mCursor == null) {
             return false;
         }
    	 
		 mDb.delete(WORDS_TABLE, WORDS_ID + "='" + id + "'", null);
		 mDb.delete(WORDS_DETAILS_TABLE, "_id = '" + id + "'", null);
		 mDb.delete(WORDTAG_TABLE, "_id='"+id+"'", null);
		 mDb.delete(LESSONS_DETAILS_TABLE, "WordId='"+id+ "'", null);
		 mDb.delete(WORDKEYVALUES_TABLE, WORDS_ID + "='" + id + "'", null);
    	 
		 mCursor.close();

		 return true;
    }       
    
    /**
     * Return a List of tags that matches the given character's charId
     * 
     * @param charId id of character whose tags we want to retrieve
     * @return List of tags
     * @throws SQLException if character could not be found/retrieved
     */
    public List<String> getCharacterTags(String charId) throws SQLException {
        //TODO: make just one method getTags(id, type) for char, word, and lesson (Seunghoon)
    	//TODO: that would solve the duplicate code problem, but create the ugly switch case problem (Angela)
        Cursor mCursor =
            mDb.query(true, CHARTAG_TABLE, new String[] {CHARTAG_TAG}, CHARTAG_ID + "='" + charId + "'", null,
                    null, null, "sort ASC", null);
        List<String> tags = new ArrayList<String>();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        do {
        	if(mCursor.getCount()==0){
        		break;
        	}
        	tags.add(mCursor.getString(mCursor.getColumnIndexOrThrow(CHARTAG_TAG)));
        }
        while(mCursor.moveToNext());
        mCursor.close();

        return tags;

    }

    /**
     * Return a Map of (Key,Value) pairs that matches the given LessonItem's ID
     * 
     * @param itemId id of LessonItem whose (Key,Value) pairs we want to retrieve
     * @param itemType type of LessonItem. 
     * 		NOTE: (Key, Value) pairs are supported only for characters and words
     * @return Map of (Key,Value) pairs, or null if the given item is not supported.
     * @throws SQLException if the item could not be found/retrieved
     */
    public LinkedHashMap<String, String> getKeyValues(String stringId, 
            LessonItem.ItemType itemType) throws SQLException {
    	String table = ""; 
    	String keyColumn = ""; 
    	String valueColumn = ""; 
    	String idColumn = "";
    	String orderBy = "sort ASC"; 
    	switch (itemType)
    	{
    	case CHARACTER:
    		table = CHARKEYVALUES_TABLE;
    		keyColumn = CHARKEYVALUES_KEY;
    		valueColumn = CHARKEYVALUES_VALUE;
    		idColumn = CHARKEYVALUES_ID;
    		break;
    	case WORD:
    		table = WORDKEYVALUES_TABLE;
    		keyColumn = WORDKEYVALUES_KEY;
    		valueColumn = WORDKEYVALUES_VALUE;
    		idColumn = WORDKEYVALUES_ID;
    		break;
    	default:
    		Log.e("DbAdapter.getKeyValues",
    		        "This type does NOT support (Key, Value) pairs.");
    		return null;
    	}
    	Cursor mCursor = mDb.query(true, table, new String[] {keyColumn, valueColumn}, idColumn + "='" + stringId + "'", null,
		                    null, null, orderBy, null);
	        LinkedHashMap<String, String> keyValues = new LinkedHashMap<String, String>();
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	        do {
	        	if(mCursor.getCount()==0){
	        		break;
	        	}
	        	keyValues.put(mCursor.getString(mCursor.getColumnIndexOrThrow(keyColumn)),
	                              mCursor.getString(mCursor.getColumnIndexOrThrow(valueColumn)));
	        }
	        while(mCursor.moveToNext());
	        mCursor.close();
	
	        return keyValues;
    }    
    
    /**
     * THIS METHOD DOES NOT WORK CORRECTLY.
     * A match is only returned if the item has both a tag and an ID.
     * 
     * Return a Cursor positioned at the item that matches the partial tag
     * if the tag is more than 2 chars, or the entire tag for 1 or 2 chars.
     * 
     * @param tag text of tag to match
     * @return Cursor positioned to matching character, if found
     * @throws SQLException if character could not be found/retrieved
     */
    public Cursor browseByTag(ItemType type, String tag) throws SQLException {

        Cursor mCursor;
        String tagsTable, tagsTableID, tagsTableTag, idTable, idTableID, idTableValue;
        switch(type){
        	case CHARACTER:
        		tagsTable = CHARTAG_TABLE;
        		tagsTableID = CHARTAG_ID;
        		tagsTableTag = CHARTAG_TAG;
        		idTable = CHARKEYVALUES_TABLE;
        		idTableID = CHARKEYVALUES_ID;
        		idTableValue = CHARKEYVALUES_VALUE;
        		break;
        	case WORD:
        		tagsTable = WORDTAG_TABLE;
        		tagsTableID = WORDTAG_ID;
        		tagsTableTag = WORDTAG_TAG;
        		idTable = WORDKEYVALUES_TABLE;
        		idTableID = WORDKEYVALUES_ID;
        		idTableValue = WORDKEYVALUES_VALUE;
        		break;
        	default:
            	Log.e("Tag", "Unsupported Type");
            	return null;
        
        }
        if(tag.length() < 2){
        	mCursor = mDb.query(true, tagsTable + ", " + idTable, 
        	        new String[] {tagsTable + "." + tagsTableID},
        	        tagsTable + "." + tagsTableID + "=" + idTable + "." + idTableID + " and (" +
        	        tagsTableTag + " LIKE '" + tag + "' or " +
        	        idTableValue + " LIKE '" + tag + "')", 
        	        null, null, null,tagsTable + "." + tagsTableID + " ASC", null);
        }
        else{
        	mCursor = mDb.query(true, tagsTable + ", " + idTable, 
        	        new String[] {tagsTable + "." + tagsTableID}, 
        	        tagsTable + "." + tagsTableID + "=" + idTable + "." + idTableID + " and (" +
        	        tagsTableTag + " LIKE '%" + tag + "%' or " +
        	        idTableValue + " LIKE '%" + tag + "%')",
        	        null, null, null, tagsTable + "." + tagsTableID + " ASC", null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    /**
     * Returns a cursor with all chars that partially match tag
     * @param tag a partial tag
     * @return a Cursor
     * @throws SQLException
     */
    public Cursor getAllChars(String tag) throws SQLException {
        Cursor mCursor;
        mCursor = mDb.query(true, CHARTAG_TABLE, 
                new String[] {CHARTAG_ID}, 
                CHARTAG_TAG + " LIKE '" + tag + "%'", 
                null, null, null, CHARTAG_ID + " ASC", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
   
    /**
     * Return a List of tags that matches the given Lesson's id
     * 
     * @param lessonId id of lesson whose tags we want to retrieve
     * @return List of tags
     * @throws SQLException if lesson could not be found/retrieved
     */
    public List<String> getLessonTags(String lessonId) throws SQLException {
    	//TODO: make just one method getTags(id, type) for char, word, and lesson (Seunghoon)
    	//TODO: separate methods was easier to modify (Angela)
        Cursor mCursor =
            mDb.query(true, LESSONTAG_TABLE, new String[] {"tag"}, "_id" + "= '" + lessonId + "'", null,
                    null, null, "tag"+" ASC", null);
        List<String> tags = new ArrayList<String>();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        do {
        	if(mCursor.getCount()==0){
        		break;
        	}
        	tags.add(mCursor.getString(mCursor.getColumnIndexOrThrow("tag")));
        }
        while(mCursor.moveToNext());
        mCursor.close();

        return tags;

    }
    
    /**
     * Return a List of tags that matches the given word's wordId
     * 
     * @param wordId id of word whose tags we want to retrieve
     * @return List of tags
     * @throws SQLException if word could not be found/retrieved
     */
    public List<String> getWordTags(String wordId) throws SQLException {
    	//TODO: make just one method getTags(id, type) for char, word, and lesson (Seunghoon)
        Cursor mCursor =

            mDb.query(true, WORDTAG_TABLE, new String[] {WORDTAG_TAG}, WORDTAG_ID + "='" + wordId + "'", null,
                    null, null, "sort ASC", null);
        List<String> tags = new ArrayList<String>();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        do {
        	if(mCursor.getCount()==0){
        		break;
        	}
        	tags.add(mCursor.getString(mCursor.getColumnIndexOrThrow(WORDTAG_TAG)));
        }
        while(mCursor.moveToNext());
        mCursor.close();

        return tags;

    }
    
    /**
     * Return a Cursor positioned at the words that matches the given tag
     * 
     * @param tag text of tag to match
     * @return Cursor positioned to matching words, if found
     * @throws SQLException if word could not be found/retrieved
     */
    public Cursor getWords(String tag) throws SQLException {

        Cursor mCursor =
            mDb.query(true, WORDTAG_TABLE, new String[] {WORDTAG_ID}, WORDTAG_TAG + "='" + tag+"'", null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    /**
     * Return a list of char ids from the database
     * @return ids list of all char ids
     */
    public List<String> getAllCharIds(){
    	 Cursor mCursor =
	            mDb.query(true, CHAR_TABLE, new String[] {CHAR_ID}, null, null,
	                    null, null, CHAR_ID +" ASC", null);
    	 List<String> ids = new ArrayList<String>();
    	 if (mCursor != null) {
    	     mCursor.moveToFirst();
    	 } else {
    	     return null;
    	 }
    	 do {
    	     if(mCursor.getCount()==0){
    	         break;
    	     }
    	     ids.add(mCursor.getString(mCursor.getColumnIndexOrThrow(CHAR_ID)));
    	 }
    	 while(mCursor.moveToNext());
         mCursor.close();

    	 return ids;
    }
    
    /**
     * Return a Cursor positioned at all characters
     * @return Cursor positioned to characters
     */
    public Cursor getAllCharIdsCursor(){
        Cursor mCursor =
                mDb.query(true, CHAR_TABLE, new String[] {CHAR_ID}, null, null,
                        null, null, CHAR_ID+" ASC", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }    
    
    /**
     * Return a list of word ids from the database
     * @return ids list of all word ids
     */
    public List<String> getAllWordIds() {
        Cursor mCursor = mDb.query(true, WORDS_TABLE, new String[] {WORDS_ID},
                null, null, null, null, "sort ASC", null);
        List<String> ids = new ArrayList<String>();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        do {
            if(mCursor.getCount()==0){
                break;
            }
            ids.add(mCursor.getString(mCursor.getColumnIndexOrThrow(WORDS_ID)));
        }
        while(mCursor.moveToNext());
        mCursor.close();

        return ids;
    }
    
    public List<String> getAllLessonNames() {
        Cursor mCursor = mDb.query(true, LESSONS_TABLE, new String[] {"name"},
                null, null, null, null, null, null);
        List<String> names = new ArrayList<String>();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        do {
            if(mCursor.getCount()==0){
                break;
            }
            names.add(mCursor.getString((mCursor.getColumnIndexOrThrow("name"))));
        }
        while(mCursor.moveToNext());
        mCursor.close();

        return names;
    }
    
    public List<String> getAllUserLessonNames() {
        Cursor mCursor = mDb.query(true, LESSONS_TABLE, new String[] {"name"},
                "userDefined=1", null, null, null, null, null);
        List<String> names = new ArrayList<String>();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        do {
            if(mCursor.getCount()==0){
                break;
            }
            names.add(mCursor.getString((mCursor.getColumnIndexOrThrow("name"))));
        }
        while(mCursor.moveToNext());
        mCursor.close();

        return names;
    }
    
    /*
     * returns theid of the word in table LESSONS_DETAILS_TABLE after being inserted
     * perhaps the return value should be changed, artifact of once using rowids for all ids
     */
    //TODO what if two lessons have the same name? should use lesson ID instead
    public String addWordToLesson(String lessonName, String wordId){
    	mDb.beginTransaction();
    	// Find the lesson
    	Cursor x = mDb.query(LESSONS_TABLE, new String[]{"_id"}, "name='"+lessonName+"'", null, null, null, null, null);
    	if (x != null) {
            x.moveToFirst();
        }
    	else{
    		return null;
    	}
    	String lessonId = x.getString(x.getColumnIndexOrThrow("_id"));
        x.close();

        // Find the next lessonOrder value
    	x = mDb.query(LESSONS_DETAILS_TABLE, new String[] {"LessonOrder"}, 
    	              "LessonId='" + lessonId + "'", 
    	              null, null, null, "LessonOrder DESC", "1");
    	int lessonOrder;
    	if (x == null) {
    	    lessonOrder = -1;
        } else if (x.getCount() == 0) {
            x.close();
            lessonOrder = -1;
        } else {
            x.moveToFirst();
            lessonOrder = x.getInt(x.getColumnIndexOrThrow("LessonOrder"));
            x.close();
    	}

    	ContentValues values = new ContentValues();
    	values.put("LessonID", lessonId);
    	values.put("WordId", wordId);
    	values.put("LessonOrder", lessonOrder + 1);
    	long ret = mDb.insert(LESSONS_DETAILS_TABLE, null, values);
    	//TODO check for errors on insert
    	mDb.setTransactionSuccessful();
    	mDb.endTransaction();
    	return wordId;
    }
    
    /**
     * Add a Lesson to the database
     * @param les lesson to be added to the database
     * @return true if lesson is added to DB.  False on error.
     */
    public boolean addLesson(Lesson les) {
        
    	mDb.beginTransaction();
    	
    	// add to LESSON_TABLE
    	ContentValues initialLessonValues = new ContentValues();
    	String id;
    	if (les.getStringId() != null) { // id already initialized, keep it
    	    id = les.getStringId();
            if (getLessonById(id) != null) {
                deleteLesson(id);
            }
    	} else { // make new id
    	    id = makeUniqueId();
    	}
    	initialLessonValues.put(LESSONS_ID, id);
    	initialLessonValues.put("name", les.getLessonName());
    	SortedSet<LessonCategory> categories = les.getCategories();
    	if (categories != null) {
    	    for (LessonCategory category : categories) {
    	        initialLessonValues.put(categoryColumns.get(category), 1);
    	    }
    	}
    	initialLessonValues.put("narrative", les.getNarrative());
    	initialLessonValues.put("userDefined", les.isUserDefined());
    	
    	// Attempt the insert
    	long rowid = mDb.insert(LESSONS_TABLE, null, initialLessonValues);
    	if (rowid == -1) {
    		// error
    		Log.e(LESSONS_TABLE, "cannot add new character to table " + LESSONS_TABLE);
    		mDb.endTransaction();
    		return false;
    	}
    	
    	// Get sort value
    	if (les.isUserDefined()) {
    	    // new collections should be on top
    	    les.setSort(-1 * rowid);
    	} else {
    	    les.setSort(rowid);
    	}
    	initialLessonValues.put("sort", les.getSort());
    	mDb.update(LESSONS_TABLE, initialLessonValues,
    	        LESSONS_ID + "='" + id + "'", null);
    	
    	les.setStringId(id);

    	//add each word to LESSONS_DETAILS_TABLE
    	List<String> l = les.getWordIds();
    	//word ordering
    	int wordNumber = 0;
    	for(String wordId:l)
    	{
    		ContentValues lessonValues = new ContentValues();
    		lessonValues.put("LessonId", id);
    		lessonValues.put("WordId", wordId);
    		lessonValues.put("LessonOrder", wordNumber);
    		long success = mDb.insert(LESSONS_DETAILS_TABLE, null, lessonValues);
    		if(success == -1)
    		{	
    			// error
    			Log.e(LESSONS_DETAILS_TABLE, "cannot add to table");
    			mDb.endTransaction();
    			return false;
    		}
    		wordNumber++;
    	}
    	
    	les.setDatabase(this);
    	
    	mDb.setTransactionSuccessful();
    	mDb.endTransaction();
    	return true;
    }
    
    public List<LessonItem> getWordsFromLesson(String lessonId){
    	Cursor mCursor = mDb.query(true, LESSONS_DETAILS_TABLE,
    	        new String[] {"WordId"}, "LessonId='" + lessonId + "'",
    	        null, null, null, "LessonOrder ASC", null);
    	List<String> ids = new ArrayList<String>();
    	if (mCursor == null) {
    	    Log.e("DbAdapter.getWordsFromLesson",
    	            "Cursor is null. lessonId = " + lessonId);
    		return null;
    	}
    	
    	if (mCursor.getCount() == 0) {
    	    return new ArrayList<LessonItem>();
    	}
    	
    	mCursor.moveToFirst();
    	do {
    		ids.add(mCursor.getString(mCursor.getColumnIndexOrThrow("WordId")));
    	}
    	while(mCursor.moveToNext());
        mCursor.close();
        
        List<LessonItem> words = new ArrayList<LessonItem>(ids.size());
        for (String id : ids) {
            LessonWord word = getWordById(id);
            if (word != null) {
                words.add(word);
                System.out.println(word.getTagsToString());
            }
        }

    	return words;
    }
    
    /**
     * Removes the word from the lesson, if it exists.
     * @param lessonId The ID of the lesson
     * @param wordId The ID of the word
     * @return true if the word was removed, false otherwise
     */
    public boolean removeWordFromLesson(String lessonId, String wordId) {
        int count = mDb.delete(LESSONS_DETAILS_TABLE,
                "LessonId=\"" + lessonId + "\" AND WordId=\"" + wordId + "\"",
                null);
        return count > 0;
    }
    
    /**
     * Return a list of lesson ids from the database
     * @return ids list of all lesson ids
     */
    public List<String> getAllLessonIds() {
        Cursor mCursor = mDb.query(true, LESSONS_TABLE,
                new String[] {LESSONS_ID},
                null, null, null, null, "userDefined DESC, sort ASC", null);
        List<String> ids = new ArrayList<String>();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        do {
            if(mCursor.getCount()==0){
                break;
            }
            ids.add(mCursor.getString(mCursor.getColumnIndexOrThrow(LESSONS_ID)));
        }
        while(mCursor.moveToNext());
        mCursor.close();

        return ids;
    }
    
    /**
     * Deletes the lesson by lesson id
     * @param id 
     * @return id if found, null if not
     */
    public String deleteLesson(String id){
        Cursor mCursor = mDb.query(true, LESSONS_TABLE,
                new String[] {LESSONS_ID}, LESSONS_ID + "='" + id + "'",
                null, null, null, null, null);
        int rowsDeleted=0;
        if (mCursor == null) {
            return null;
        }
        else{
            mCursor.close();

            rowsDeleted += mDb.delete(LESSONS_TABLE, LESSONS_ID + "='" + id + "'", null);
            rowsDeleted += mDb.delete(LESSONS_DETAILS_TABLE, "LessonId = '" + id + "'", null);
            rowsDeleted += mDb.delete(LESSONTAG_TABLE, LESSONTAG_ID + "='" + id + "'", null);
        }
        
        if (rowsDeleted > 0) {
            return id;
        } else {
            return null;
        }
    }
    
    /**
     * @param id
     * @return
     */
    public Lesson getLessonById(String id) {
        String[] columns = new String[] {LESSONS_ID, "name", "narrative",
                "userDefined", "sort", "catShapeAndStructure", "catMeaning",
                "catPhonetic", "catGrammar"};
    	Cursor mCursor = mDb.query(true, LESSONS_TABLE, columns,
    	        LESSONS_ID + "='" + id + "'", null, null, null, null, null);
    	
    	if (mCursor == null) {
    		return null;
    	} else if (mCursor.getCount() == 0) {
    	    mCursor.close();
    	    return null;
    	}
    	mCursor.moveToFirst();
    	boolean userDefined = mCursor.getInt(mCursor.getColumnIndexOrThrow("userDefined")) == 1;
        Lesson le = new Lesson(userDefined);
        le.setName(mCursor.getString(mCursor.getColumnIndexOrThrow("name")));
    	le.setNarrative(mCursor.getString(mCursor.getColumnIndexOrThrow("narrative")));
    	le.setSort(mCursor.getInt(mCursor.getColumnIndexOrThrow("sort")));
    	if (mCursor.getInt(mCursor.getColumnIndexOrThrow("catShapeAndStructure")) == 1) {
            le.addCategory(LessonCategory.SHAPE_AND_STRUCTURE);
        }
    	if (mCursor.getInt(mCursor.getColumnIndexOrThrow("catMeaning")) == 1) {
            le.addCategory(LessonCategory.MEANING);
        }
    	if (mCursor.getInt(mCursor.getColumnIndexOrThrow("catPhonetic")) == 1) {
            le.addCategory(LessonCategory.PHONETIC);
        }
    	if (mCursor.getInt(mCursor.getColumnIndexOrThrow("catGrammar")) == 1) {
            le.addCategory(LessonCategory.GRAMMAR);
        }
    	mCursor.close();

    	//SUSPECT: grab its details (step one might not be necessary and might cause slow downs
    	// but it is for data consistency.
    	mCursor = mDb.query(true, LESSONS_DETAILS_TABLE,
    	        new String[] { "LessonId", "WordId", "LessonOrder"},
    	        "LessonId" + "='" + id + "'",
    	        null, null, null, "LessonOrder ASC", null);
    	mCursor.moveToFirst();
    	do {
    		if(mCursor.getCount()==0){
    			break;
    		}
    		String wordId = mCursor.getString(mCursor.getColumnIndexOrThrow("WordId"));
    		Log.i("LOAD", "Word: " + wordId);
    		le.addWord(wordId);
    	} while(mCursor.moveToNext());
        mCursor.close();

    	le.setStringId(id);
    	le.setDatabase(this);
    	
    	return le;
    }
    
    public boolean saveLessonCategories(String lessonId, boolean[] categories) {
        ContentValues values = new ContentValues();
        values.put("catShapeAndStructure", categories[0]);
        values.put("catMeaning",           categories[1]);
        values.put("catPhonetic",          categories[2]);
        values.put("catGrammar",           categories[3]);
        return mDb.update(LESSONS_TABLE, values,
                LESSONS_ID + "='" + lessonId + "'", null) == 1;
    }
    
    public boolean saveLessonNarrative(String lessonId, String narrative) {
        ContentValues values = new ContentValues();
        values.put("narrative", narrative);
        return mDb.update(LESSONS_TABLE, values,
                LESSONS_ID + "='" + lessonId + "'", null) == 1;
    }
    
    public boolean saveLessonUserDefined(String lessonId, boolean userDefined,
            long sort) {
        ContentValues values = new ContentValues();
        values.put("userDefined", userDefined);
        values.put("sort", sort);
        return mDb.update(LESSONS_TABLE, values,
                LESSONS_ID + "='" + lessonId + "'", null) == 1;
    }
    
    /**
     * Swap the display order of two characters.
     * @param aId id of first character
     * @param aSort sort value of first character
     * @param bId id of second character
     * @param bSort sort value of second character
     * @return true if the transaction was successful, false otherwise
     */
    public boolean swapCharacters(String aId, double aSort, String bId, double bSort) {
        mDb.beginTransaction();
        ContentValues aValues = new ContentValues();
        ContentValues bValues = new ContentValues();
        aValues.put(CHAR_ID, aId);
        bValues.put(CHAR_ID, bId);
        aValues.put("sort", bSort);
        bValues.put("sort", aSort);
        Log.e("Swapping positions", aId + " and " + bId);
        
        int result;
        result = mDb.update(CHAR_TABLE, aValues, CHAR_ID + "='" + aId + "'", null);
        if (result != 1) {
            Log.e(CHAR_TABLE, "id " + aId + ": write failed");
            mDb.endTransaction();
            return false;
        }
        result = mDb.update(CHAR_TABLE, bValues, CHAR_ID + "='" + bId + "'", null);
        if (result != 1) {
            Log.e(CHAR_TABLE, "id " + bId + ": write failed");
            mDb.endTransaction();
            return false;
        }
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        return true;
    }
    
    
    /**
     * Swap the display order of two words.
     * @param aId id of first word
     * @param aSort sort value of first word
     * @param bId id of second word
     * @param bSort sort value of second word
     * @return true if the transaction was successful, false otherwise
     */
    public boolean swapWords(String aId, long aSort, String bId, long bSort) {
        mDb.beginTransaction();
        ContentValues aValues = new ContentValues();
        ContentValues bValues = new ContentValues();
        aValues.put(WORDS_ID, aId);
        bValues.put(WORDS_ID, bId);
        aValues.put("sort", bSort);
        bValues.put("sort", aSort);
        Log.d("Swapping positions", aId + " and " + bId);
        
        int result;
        result = mDb.update(WORDS_TABLE, aValues, WORDS_ID + "='" + aId + "'", null);
        if (result != 1) {
            Log.e(WORDS_TABLE, "id " + aId + ": write failed");
            mDb.endTransaction();
            return false;
        }
        result = mDb.update(WORDS_TABLE, bValues, WORDS_ID + "='" + bId + "'", null);
        if (result != 1) {
            Log.e(WORDS_TABLE, "id " + bId + ": write failed");
            mDb.endTransaction();
            return false;
        }
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        return true;
    }
    
    /**
     * Swap the display order of two words.
     * @param lessonId the lesson ID of the two words
     * @param aId the first word ID
     * @param bId the second word ID
     * @return true if the transaction was successful, false otherwise
     */
    public boolean swapWordsInLesson(String lessonId, String aId, String bId) {
        String wordCol  = "WordId";
        String lesCol   = "LessonId";
        String orderCol = "LessonOrder";
        
        // get LessonOrder values for a and b
        int aSort = 0, bSort = 0;
        Cursor cur = mDb.query(true, LESSONS_DETAILS_TABLE, 
                               new String[] {wordCol, orderCol}, 
                               lesCol + "='" + lessonId + "' AND (" + wordCol + "='" + aId + "' OR " + wordCol + "='" + bId + "')", 
                               null, null, null, null, null);
        cur.moveToFirst();
        if (cur.getCount() != 2) {
            Log.e("Swapping positions", "Could not find words in " + LESSONS_DETAILS_TABLE);
            return false;
        }
        if (cur.getString(cur.getColumnIndexOrThrow(wordCol)).equals(aId)) {
            aSort = cur.getInt(cur.getColumnIndexOrThrow(orderCol));
            cur.moveToNext();
            bSort = cur.getInt(cur.getColumnIndexOrThrow(orderCol));
        } else { // word B is first
            bSort = cur.getInt(cur.getColumnIndexOrThrow(orderCol));
            cur.moveToNext();
            aSort = cur.getInt(cur.getColumnIndexOrThrow(orderCol));
        }
        System.out.println("1st word sort " + aSort);
        System.out.println("2nd word sort " + bSort);
        cur.close();
        // insert new values
        mDb.beginTransaction();
        ContentValues aValues = new ContentValues();
        ContentValues bValues = new ContentValues();
        aValues.put(lesCol, lessonId);
        bValues.put(lesCol, lessonId);
        aValues.put(wordCol, aId);
        bValues.put(wordCol, bId);
        aValues.put(orderCol, bSort);
        bValues.put(orderCol, aSort);
        Log.e("Swapping positions", aId + " and " + bId + " in lesson " + lessonId);
        
        // update database
        int result;
        result = mDb.update(LESSONS_DETAILS_TABLE, aValues, 
                            lesCol + "='" + lessonId + "' AND " + wordCol + "='" + aId + "'", 
                            null);
        if (result != 1) {
            Log.e(LESSONS_DETAILS_TABLE, "id " + aId + ": write failed");
            mDb.endTransaction();
            return false;
        }
        result = mDb.update(LESSONS_DETAILS_TABLE, bValues, 
                            lesCol + "='" + lessonId + "' AND " + wordCol + "='" + bId + "'", 
                            null);
        if (result != 1) {
            Log.e(LESSONS_DETAILS_TABLE, "id " + bId + ": write failed");
            mDb.endTransaction();
            return false;
        }
        
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        return true;
    }
    
    public boolean swapLessons(String aId, long aSort, String bId, long bSort) {
        mDb.beginTransaction();
        ContentValues aValues = new ContentValues();
        ContentValues bValues = new ContentValues();
        aValues.put(LESSONS_ID, aId);
        bValues.put(LESSONS_ID, bId);
        aValues.put("sort", bSort);
        bValues.put("sort", aSort);
        Log.d("DbAdapter.swapLessons", aId + " and " + bId);
        
        int result;
        result = mDb.update(LESSONS_TABLE, aValues, LESSONS_ID + "='" + aId + "'", null);
        if (result != 1) {
            Log.e("DbAdapter.swapLessons", "id " + aId + ": write failed");
            mDb.endTransaction();
            return false;
        }
        result = mDb.update(LESSONS_TABLE, bValues, LESSONS_ID + "='" + bId + "'", null);
        if (result != 1) {
            Log.e("DbAdapter.swapLessons", "id " + bId + ": write failed");
            mDb.endTransaction();
            return false;
        }
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        return true;
    }
    
    /**
     * Swaps the display order of the two tags for the given item
     * @param table the table containing the tags
     * @param id the id of the item associated with the tags
     * @param a the first tag
     * @param b the second tag
     * @return
     */
    public boolean swapTags(String table, String id, String a, String b) {
        String idCol    = "_id";
        String tagCol   = "tag";
        String orderCol = "sort";
        
        // get sort values for a and b
        int aSort = 0, bSort = 0;
        Cursor cur = mDb.query(true, table, 
                               new String[] {tagCol, orderCol}, 
                               idCol + "='" + id + "' AND (" + tagCol + "='" + a + "' OR " + tagCol + "='" + b + "')", 
                               null, null, null, null, null);
        cur.moveToFirst();
        if (cur.getCount() != 2) {
            Log.e("Swapping positions", "Could not find tags in " + table);
            return false;
        }
        if (cur.getString(cur.getColumnIndexOrThrow(tagCol)).equals(a)) {
            aSort = cur.getInt(cur.getColumnIndexOrThrow(orderCol));
            cur.moveToNext();
            bSort = cur.getInt(cur.getColumnIndexOrThrow(orderCol));
        } else { // tag B is first
            bSort = cur.getInt(cur.getColumnIndexOrThrow(orderCol));
            cur.moveToNext();
            aSort = cur.getInt(cur.getColumnIndexOrThrow(orderCol));
        }
        
        // insert new values
        mDb.beginTransaction();
        ContentValues aValues = new ContentValues();
        ContentValues bValues = new ContentValues();
        aValues.put(idCol, id);
        bValues.put(idCol, id);
        aValues.put(tagCol, a);
        bValues.put(tagCol, b);
        aValues.put(orderCol, bSort);
        bValues.put(orderCol, aSort);
        Log.e("Swapping positions", a + " and " + b + " in table " + table + " item " + id);
        
        // update database
        int result;
        result = mDb.update(table, aValues, 
                            idCol + "='" + id + "' AND " + tagCol + "='" + a + "'", 
                            null);
        if (result != 1) {
            Log.e(table, "id " + id + " tag " + a + ": write failed");
            mDb.endTransaction();
            return false;
        }
        result = mDb.update(table, bValues, 
                            idCol + "='" + id + "' AND " + tagCol + "='" + b + "'", 
                            null);
        if (result != 1) {
            Log.e(table, "id " + id + " tag " + b + ": write failed");
            mDb.endTransaction();
            return false;
        }
        
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        return true;
    }
    
    /**
     * Swaps the display order of the two IDs for the given item
     * @param table the table containing the Key-Value pairs
     * @param id the id of the item associated with the tags
     * @param aKey the first key
     * @param bKey the second key
     * @return
     */
    public boolean swapKeyValues(String table, String id, String aKey,
                                 String bKey) {
        String idCol    = "_id";
        String keyCol   = "key";
        String valCol   = "value";
        String orderCol = "sort";
        
        // get sorts and values for a and b
        int aSort = 0, bSort = 0;
        String aVal = "", bVal = "";
        Cursor cur = mDb.query(true, table, new String[] {keyCol, valCol, orderCol},
                               idCol + "='" + id + "' AND (" + keyCol + "='" + aKey + "' OR " + keyCol + "='" + bKey + "')",
                               null, null, null, null, null);
        cur.moveToFirst();
        if (cur.getCount() != 2) {
            Log.e("Swapping positions", "Could not find keys in " + table);
            return false;
        }
        if (cur.getString(cur.getColumnIndexOrThrow(keyCol)).equals(aKey)) {
            aSort = cur.getInt(cur.getColumnIndexOrThrow(orderCol));
            aVal = cur.getString(cur.getColumnIndexOrThrow(valCol));
            cur.moveToNext();
            bSort = cur.getInt(cur.getColumnIndexOrThrow(orderCol));
            bVal = cur.getString(cur.getColumnIndexOrThrow(valCol));
        } else { // key B is first
            bSort = cur.getInt(cur.getColumnIndexOrThrow(orderCol));
            bVal = cur.getString(cur.getColumnIndexOrThrow(valCol));
            cur.moveToNext();
            aSort = cur.getInt(cur.getColumnIndexOrThrow(orderCol));
            aVal = cur.getString(cur.getColumnIndexOrThrow(valCol));
        }
        cur.close();
        // insert new values
        mDb.beginTransaction();
        ContentValues aValues = new ContentValues();
        ContentValues bValues = new ContentValues();
        aValues.put(idCol, id);
        bValues.put(idCol, id);
        aValues.put(keyCol, aKey);
        bValues.put(keyCol, bKey);
        aValues.put(valCol, aVal);
        bValues.put(valCol, bVal);
        aValues.put(orderCol, bSort);
        bValues.put(orderCol, aSort);
        Log.e("Swapping positions", aKey + " and " + bKey + " in table " + table + " item " + id);
        
        // update database
        int result;
        result = mDb.update(table,
                            aValues, idCol + "='" + id + "' AND " + keyCol + "='" + aKey + "'",
                            null);
        if (result != 1) {
            Log.e(table, "id " + id + " key " + aKey + ": write failed");
            mDb.endTransaction();
            return false;
        }
        result = mDb.update(table,
                            bValues, idCol + "='" + id + "' AND " + keyCol + "='" + bKey + "'",
                            null);
        if (result != 1) {
            Log.e(table, "id " + id + " key " + bKey + ": write failed");
            mDb.endTransaction();
            return false;
        }
        
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        return true;
    }
    
    public String makeUniqueId() {
        TelephonyManager tMgr = (TelephonyManager) mCtx.getSystemService(
                Context.TELEPHONY_SERVICE);
        String sIMEI = tMgr.getDeviceId(); // Requires READ_PHONE_STATE
        SimpleDateFormat dtFmt = new SimpleDateFormat("ddMMyyyyhhmmssSSS",
                Locale.US);
        String sDate = dtFmt.format(new Date());
        Log.d("uniqueID", sIMEI + "_" + sDate);

        return sIMEI + "_" + sDate;
    }
}
