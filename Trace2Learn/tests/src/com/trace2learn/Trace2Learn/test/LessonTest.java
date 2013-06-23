package com.trace2learn.Trace2Learn.test;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.test.AndroidTestCase;
import android.util.Log;

import com.trace2learn.TraceLibrary.Toolbox;
import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonWord;
import com.trace2learn.TraceLibrary.Database.Parser;

public class LessonTest extends AndroidTestCase {

    static public void compareLessons(Lesson expected, Lesson actual)
    {
        LessonItemTest.compareLessonItem(expected, actual);
        assertEquals(expected.getLessonName(), actual.getLessonName());
        assertEquals(expected.getWordIds(), actual.getWordIds());
    }	

    protected void dumpDBs()
    {
        for(String str : this.getContext().databaseList())
        {
            Log.i("DELETE", str);
            this.getContext().deleteDatabase(str);
        }
    }

    //private LessonCharacter c1, c2;
    private LessonWord w1, w2;
    protected void setUp() throws Exception {
        Toolbox.initDbAdapter(this.getContext(), false);
    }

    protected void tearDown()
    {
        Toolbox.resetDbAdapter();
    }

    public void testTwoWords()
    {
        Lesson exp = new Lesson();
        exp.setStringId("lesson_unique_id");
        exp.setName("lesson_name");
        exp.addWord(w1.getStringId());
        exp.addWord(w2.getStringId());

        Toolbox.dba.addLesson(exp);
        Lesson lesson = Toolbox.dba.getLessonById("lesson_unique_id");

        compareLessons(exp, lesson);
    }


    public void testToXml() {
        LessonWord word1 = new LessonWord("id1");
        word1.addTag("\"English\"");
        word1.addTag("Color & Shape");
        word1.addKeyValue("Word", "Red");
        word1.addCharacter("1234");
        word1.addCharacter("1234");
        Toolbox.dba.addWord(word1);

        LessonWord word1_db = Toolbox.dba.getWordById("id1");
        LessonWordTest.compareWords(word1, word1_db);		

        LessonWord word2 = new LessonWord("id2");
        word2.addTag("English");
        word2.addTag("Color");
        word2.addKeyValue("Word", "Blue");
        word2.addCharacter("1235");
        word2.addCharacter("1234");
        Toolbox.dba.addWord(word2);		


        Lesson lesson = new Lesson(false);
        lesson.setStringId("unique_lesson_id");
        lesson.setName("Colors");
        lesson.setNarrative("3.  Diagonal strokes are drawn top to bottom, and\nwith a slight downward curvature, as in 人, 木, 手 and 文.\n&'<");
        lesson.addWord(word1.getStringId());
        lesson.addWord(word2.getStringId());
        Toolbox.dba.addLesson(lesson);

        String exp = "<lesson id=\"unique_lesson_id\" name=\"Colors\" author=\"admin\">\n" +
        	"<narrative>3.  Diagonal strokes are drawn top to bottom, and\nwith a slight downward curvature, as in 人, 木, 手 and 文.\n&amp;&apos;&lt;</narrative>\n" +
                "<word id=\"id1\" position=\"0\">\n" +
                "<tag tag=\"&quot;English&quot;\" />\n" +
                "<tag tag=\"Color &amp; Shape\" />\n" +
                "<id key=\"Word\" value=\"Red\" />\n" +
                "<character id=\"1234\" position=\"0\" />\n" +
                "<character id=\"1234\" position=\"1\" />\n" +
                "</word>\n" +
                "<word id=\"id2\" position=\"1\">\n" +
                "<tag tag=\"English\" />\n" +
                "<tag tag=\"Color\" />\n" +
                "<id key=\"Word\" value=\"Blue\" />\n" +
                "<character id=\"1235\" position=\"0\" />\n" +
                "<character id=\"1234\" position=\"1\" />\n" +
                "</word>\n" +
                "</lesson>\n";

        assertEquals(exp, lesson.toXml());
    }

    public void testImportFromXml() throws SAXException, IOException {
        String xml = "<lesson id=\"unique_lesson_id\" name=\"Colors\">\n" +
                "<narrative>3.  Diagonal strokes are drawn top to bottom, and\nwith a slight downward curvature, as in 人, 木, 手 and 文.\n&amp;&apos;&lt;</narrative>" +
                "<word id=\"900\" position=\"0\">\n" +
                "<tag tag=\"&quot;English&amp;quot;\" />\n" +
                "<tag tag=\"Color &amp; Shape\" />\n" +
                "<id key=\"Word\" value=\"Red&apos;\" />\n" +
                "<character id=\"1234\" position=\"0\" />\n" +
                "<character id=\"1234\" position=\"1\" />\n" +
                "</word>\n" +
                "<word id=\"901\" position=\"1\">\n" +
                "<tag tag=\"English\" />\n" +
                "<tag tag=\"Color\" />\n" +
                "<id key=\"Word\" value=\"Blue\" />\n" +
                "<character id=\"1235\" position=\"0\" />\n" +
                "<character id=\"1234\" position=\"1\" />\n" +
                "</word>\n" +
                "</lesson>\n";
        Element elem = Parser.parse(xml).getDocumentElement();

        LessonWord word1 = new LessonWord("900");
        word1.addTag("\"English\"");
        word1.addTag("Color & Shape");
        word1.addKeyValue("Word", "Red'");
        word1.addCharacter("1234");
        word1.addCharacter("1234");

        LessonWord word2 = new LessonWord("901");
        word2.addTag("English");
        word2.addTag("Color");
        word2.addKeyValue("Word", "Blue");
        word2.addCharacter("1235");
        word2.addCharacter("1234");		

        Lesson lesson = new Lesson();
        lesson.setStringId("unique_lesson_id");
        lesson.setName("Colors");
        lesson.setNarrative("3.  Diagonal strokes are drawn top to bottom, and\nwith a slight downward curvature, as in 人, 木, 手 and 文.\n&'<");
        lesson.addWord(word1.getStringId());
        lesson.addWord(word2.getStringId());

        compareLessons(lesson, Lesson.importFromXml(elem));
    }	
}
