package edu.upenn.cis573.Trace2Win;

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
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.upenn.cis573.Trace2Win.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.Database.Parser;

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
            
            importCharacters(root);
            importLessons(root);

            finish();
        } catch (Exception e) {
            showToast("This is not a valid " + getString(R.string.app_name) +
                    " file!");
        }
    }
    
    /**
     * Imports characters from the given file
     * @param elem Trace2Learn root element <ttw>
     */
    private void importCharacters(Element elem) throws Exception {
        NodeList characters = elem.getElementsByTagName("character");
        for (int i = 0; i < characters.getLength(); i++) {
            Element e = (Element) characters.item(i);
            LessonCharacter character = LessonCharacter.importFromXml(e);
            if (dba.getCharacterById(character.getId()) == null) {
                dba.addCharacter(character);
            }
        }
    }
    
    /**
     * Imports words and lessons from the given file
     * @param elem Trace2Learn root element <ttw>
     */
    private void importLessons(Element elem) {
        
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
