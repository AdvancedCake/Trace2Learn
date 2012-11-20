package edu.upenn.cis573.Trace2Win.test;

import android.test.AndroidTestCase;
import android.util.Log;
import edu.upenn.cis573.Trace2Win.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;

public class DbAdapterTest extends AndroidTestCase {

    DbAdapter db;

    protected void setUp() throws Exception {
        super.setUp();
        dumpDBs();
        db = new DbAdapter(this.getContext());
        db.open();
    }

    protected void tearDown()
    {
        dumpDBs();
    }

    protected void dumpDBs()
    {
        for(String str : this.getContext().databaseList())
        {
            Log.i("DELETE", str);
            this.getContext().deleteDatabase(str);
        }
    }

    protected void compareCharacters(LessonCharacter expected, LessonCharacter actual)
    {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getNumStrokes(), actual.getNumStrokes());
        assertEquals(expected.getItemType(), actual.getItemType());
        assertEquals(expected.getTags(), actual.getTags());
        assertEquals(expected.getKeyValues(), actual.getKeyValues());
    }


    // TESTS


}
