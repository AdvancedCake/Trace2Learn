package edu.upenn.cis573.Trace2Win.test;

import java.util.LinkedHashMap;

import android.test.AndroidTestCase;
import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.Database.LessonItem;

public class LessonItemTest extends AndroidTestCase {

    public void testCompareTo() {
        LessonItem a = new LessonCharacter();
        LessonItem b = new LessonCharacter();
        LessonItem c = new LessonCharacter();
        a.setSort(1);
        b.setSort(2);
        c.setSort(1);
        
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertTrue(c.compareTo(a) == 0);
    }
    
    public void testSetKeyValues() {
        LessonItem a = new LessonCharacter();
        LinkedHashMap<String, String> kv = new LinkedHashMap<String, String>();
        kv.put("k1", "v1");
        kv.put("k2", "v2");
        
        a.setKeyValues(kv);
        assertEquals(a.getKeyValues(), kv);
    }
}
