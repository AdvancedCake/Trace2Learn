package edu.upenn.cis350.Trace2Learn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TagsDbAdapter {
	
    public static final String CHAR_ROWID = "_id";
        
    public static final String CHARTAG_ROWID = "_id";
    public static final String CHARTAG_TAG= "tag";

    private static final String TAG = "TagsDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE_CHAR =
        "CREATE TABLE Character (_id INTEGER PRIMARY KEY AUTOINCREMENT);";
    
    private static final String DATABASE_CREATE_CHARTAG =
            "CREATE TABLE CharacterTag (_id INTEGER, tag TEXT NOT NULL, FOREIGN KEY(_id) REFERENCES Character(_id));";

    private static final String DATABASE_NAME = "CharTags";
    private static final String CHAR_TABLE = "Character";
    private static final String CHARTAG_TABLE = "CharacterTag";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE_CHAR);
            db.execSQL(DATABASE_CREATE_CHARTAG);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS Character");
            db.execSQL("DROP TABLE IF EXISTS CharacterTag");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public TagsDbAdapter(Context ctx) {
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
    public TagsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    /**
     * Create a new character. If the character is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param id the row_id of the tag
     * @param tag the text of the tag
     * @return rowId or -1 if failed
     */
    public long createTags(long id, String tag) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(CHARTAG_ROWID, id);
        initialValues.put(CHARTAG_TAG, tag);

        return mDb.insert(CHARTAG_TABLE, null, initialValues);
    }
    
    /**
     * Delete the tag with the given rowId and tag
     * 
     * @param rowId id of tag to delete
     * @param tag text of tag to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteTag(long rowId, String tag) {

        return mDb.delete(CHARTAG_TABLE, CHARTAG_ROWID + "=" + rowId + " AND " + CHARTAG_TAG+"="+tag, null) > 0;
    }
    
    /**
     * Return a Cursor positioned at the tag that matches the given character's charId
     * 
     * @param charId id of character whose tags we want to retrieve
     * @return Cursor positioned to matching character, if found
     * @throws SQLException if character could not be found/retrieved
     */
    public Cursor getTags(long charId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, CHARTAG_TABLE, new String[] {CHARTAG_TAG}, CHARTAG_ROWID + "=" + charId, null,
                    null, null, CHARTAG_TAG+" ASC", null);
        if (mCursor != null) {
        	Log.d(CHARTAG_TABLE, "not null");
            mCursor.moveToFirst();
            Log.d(CHARTAG_TABLE, Integer.toString(mCursor.getCount()));
        }
        return mCursor;

    }
    
    /**
     * Return a Cursor positioned at the character that matches the given tag
     * 
     * @param tag text of tag to match
     * @return Cursor positioned to matching character, if found
     * @throws SQLException if character could not be found/retrieved
     */
    public Cursor getChars(String tag) throws SQLException {

        Cursor mCursor =

            mDb.query(true, CHARTAG_TABLE, new String[] {CHARTAG_ROWID}, CHARTAG_TAG + "='" + tag+"'", null,
                    null, null, CHARTAG_ROWID + " ASC", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
}
