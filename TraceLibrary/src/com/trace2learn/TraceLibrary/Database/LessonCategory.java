package com.trace2learn.TraceLibrary.Database;

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
}
