package com.trace2learn.TraceLibrary;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.trace2learn.TraceLibrary.Database.DbAdapter;
import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonCharacter;
import com.trace2learn.TraceLibrary.Database.LessonItem;
import com.trace2learn.TraceLibrary.Database.LessonWord;
import com.trace2learn.TraceLibrary.Database.Parser;

public class FilePickerActivity extends ListActivity {

    private File currentDir;
    private FileArrayAdapter adapter;
    private TextView currentView;
    private DbAdapter dba;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_picker);
        currentView = (TextView) findViewById(R.id.current_dir);
        
        dba = new DbAdapter(this);
        dba.open();
        
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/data/" + getString(R.string.file_dir_name);
        Log.i("File Import", "Opening directory " + path);
        currentDir = new File(path);
        currentDir.mkdirs();
        fill(currentDir);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dba.close();
    };

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
        importFromFile(item.getPath());
    }
    
    /**
     * Read a String from the file whose path is given
     * @param filepath the absolute path to the file
     * @return 
     */
    public void importFromFile(String filepath) {
        File f = new File(filepath);
        try {
            Document doc = Parser.parse(f);
            Element root = doc.getDocumentElement();
            if (!root.getNodeName().equals("ttw")) {
                showToast("This is not a valid " +
                        getString(R.string.app_name) + " file!");
                return;
            }
            String name = root.getAttribute("name");
            
            importCharacters(root);
            importLessons(root);

            showToast("Imported " + name + " successfully");
            finish();
        } catch (Exception e) {
            showToast("This is not a valid " + getString(R.string.app_name) +
                    " file!");
            String message = e.getMessage();
            if (message == null) {
                message = "No error message";
            }
            Log.e("Import", message);
        }
    }
    
    /**
     * Imports characters from the given file
     * @param elem Trace2Learn root element <ttw>
     * @throws Exception indicates that the element was not a valid ttw file
     */
    private void importCharacters(Element elem) throws Exception {
        NodeList characters = elem.getElementsByTagName("character");
        for (int i = 0; i < characters.getLength(); i++) {
            Element e = (Element) characters.item(i);
            
            // only want direct children
            if (e.getParentNode().getNodeName().equals("ttw")) {
                LessonCharacter character = LessonCharacter.importFromXml(e);
                if (dba.getCharacterById(character.getStringId()) == null) {
                    dba.addCharacter(character);
                }
            }
        }
    }
    
    /**
     * Imports words and lessons from the given file
     * @param elem Trace2Learn root element <ttw>
     * @throws Exception indicates that the element was not a valid ttw file
     */
    private void importLessons(Element elem) throws Exception {
        NodeList lessons = elem.getElementsByTagName("lesson");
        for (int i = 0; i < lessons.getLength(); i++) {
            Element e = (Element) lessons.item(i);
            
            // only want direct children
            if (e.getParentNode().getNodeName().equals("ttw")) {
                Lesson lesson = Lesson.importFromXml(e);
                
                if (dba.getLessonById(lesson.getStringId()) == null) {
                    // add all of the words
                    List<LessonItem> words = lesson.getWords();
                    for (LessonItem word : words) {
                        if (dba.getWordById(word.getStringId()) == null) {
                            dba.addWord((LessonWord) word);
                        }
                    }
                    
                    // add the lesson
                    dba.addLesson(lesson);
                }
            }
        }
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
    
    private final void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
