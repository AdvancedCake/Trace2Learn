package com.trace2learn.TraceLibrary.Database;

import java.util.HashMap;

import com.trace2learn.TraceLibrary.R;

public enum LessonCategory {
    SHAPE_AND_STRUCTURE (R.drawable.shape_and_structure, "Shape and Structure"),
    MEANING             (R.drawable.meaning,             "Meaning"),
    PHONETIC            (R.drawable.phonetic,            "Phonetic"),
    GRAMMAR             (R.drawable.grammar,             "Grammar");
    
    public final int rid;
    public final String name;
    
    LessonCategory(int rid, String name) {
        this.rid  = rid;
        this.name = name;
    }
    
    
    public static HashMap<String, LessonCategory> lookup;
    
    public static LessonCategory lookup(String cat) {
        if (lookup == null) {
            lookup = new HashMap<String, LessonCategory>(4);
            lookup.put("Shape and Structure", SHAPE_AND_STRUCTURE);
            lookup.put("Meaning", MEANING);
            lookup.put("Phonetic", PHONETIC);
            lookup.put("Grammar", GRAMMAR);
        }
        return lookup.get(cat);
    }
}
