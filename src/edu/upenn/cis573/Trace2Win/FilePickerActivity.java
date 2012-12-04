package edu.upenn.cis573.Trace2Win;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FilePickerActivity extends ListActivity {

    private File currentDir;
    private FileArrayAdapter adapter;
    private TextView currentView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_picker);
        currentView = (TextView) findViewById(R.id.current_dir);
        
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/data/" + getString(R.string.app_name);
        currentDir = new File(path);
        currentDir.mkdirs();
        fill(currentDir);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FileItem item = adapter.getItem(position);
        if (item.getDesc().equalsIgnoreCase("folder") ||
                item.getDesc().equalsIgnoreCase("parent directory")) {
            currentDir = new File(item.getPath());
            fill(currentDir);
        } else {
            onFileClick(item);
        }
    }
    
    private void onFileClick(FileItem item) {
        Toast.makeText(this, "File Clicked: "+item.getName(), Toast.LENGTH_SHORT).show();
    }
    
    private void fill(File f) {
        File[] items = f.listFiles();
        currentView.setText("Current Dir: " + f.getName());
        List<FileItem> dirs = new ArrayList<FileItem>();
        List<FileItem> files = new ArrayList<FileItem>();
        
        for (File item : items) {
            if (item.isDirectory()) {
                dirs.add(new FileItem(item.getName(), "Folder", 
                        item.getAbsolutePath()));
            } else {
                files.add(new FileItem(item.getName(), "Size: " + item.length(), 
                        item.getAbsolutePath()));
            }
        }
        
        Collections.sort(dirs);
        Collections.sort(files);
        
        dirs.addAll(files);
        if (!f.getAbsolutePath().equals(
                Environment.getExternalStorageDirectory().getPath())) {
            dirs.add(new FileItem("..", "Parent Directory", f.getParent()));
        }
        
        adapter = new FileArrayAdapter(this, R.layout.file_item_view, dirs);
        setListAdapter(adapter);
    }
}
