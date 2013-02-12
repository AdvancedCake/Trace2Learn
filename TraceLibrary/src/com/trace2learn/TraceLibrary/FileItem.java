package com.trace2learn.TraceLibrary;

public class FileItem implements Comparable<FileItem> {

    private String name;
    private String desc;
    private String path;
    
    public FileItem(String name, String desc, String path) {
        this.name = name;
        this.desc = desc;
        this.path = path;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public String getPath() {
        return path;
    }
    
    public int compareTo(FileItem other) {
        if (name == null) { return 1; }
        else if (other.name == null) { return -1; }
        return name.toLowerCase().compareTo(other.getName().toLowerCase());
    }

}
