package com.trace2learn.TraceLibrary.Database;

import com.trace2learn.TraceLibrary.R;

public enum LessonCategory {
    SHAPE_AND_STRUCTURE (R.drawable.shape_and_structure),
    MEANING             (R.drawable.meaning),
    PHONETIC            (R.drawable.phonetic),
    GRAMMAR             (R.drawable.grammar);
    
    public final int rid;
    
    LessonCategory(int rid) {
        this.rid = rid;
    }
}
