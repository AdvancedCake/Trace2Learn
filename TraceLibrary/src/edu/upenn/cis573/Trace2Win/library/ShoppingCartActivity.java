package edu.upenn.cis573.Trace2Win.library;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.upenn.cis573.Trace2Win.library.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.library.Database.Lesson;
import edu.upenn.cis573.Trace2Win.library.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.library.Database.LessonItem;
import edu.upenn.cis573.Trace2Win.library.Database.LessonItem.ItemType;
import edu.upenn.cis573.Trace2Win.library.Database.LessonWord;

public class ShoppingCartActivity extends Activity {

    private ItemType type; // determines the type of items being displayed
    private List<LessonItem> source; // all items of the specified type
    private List<LessonItem> display; // items to be displayed
    private List<LessonItem> cart;
    private ShoppingCartListAdapter adapter;
    private boolean filtered;
    private boolean viewingCart;

    private ListView list;
    private TextView title;
    private Button exportButton;
    private Button cartButton;
    private Button filterButton;
    private Button selectButton;
    private Button deselectButton;
    private TextView filterStatus;

    private DbAdapter dba;
    private LayoutInflater vi;
    
    private Lesson allChars = new Lesson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart);

        list           = (ListView) findViewById(R.id.list);
        title          = (TextView) findViewById(R.id.title);
        exportButton   = (Button)   findViewById(R.id.exportButton);
        cartButton     = (Button)   findViewById(R.id.cartButton);
        filterButton   = (Button)   findViewById(R.id.filterButton);
        selectButton   = (Button)   findViewById(R.id.selectAllButton);
        deselectButton = (Button)   findViewById(R.id.deselectAllButton);
        filterStatus   = (TextView) findViewById(R.id.filterStatus);

        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                LessonItem item = (LessonItem) parent.getItemAtPosition(position);
                if (viewingCart) {
                    cart.remove(item);
                    adapter = new ShoppingCartListAdapter(
                            ShoppingCartActivity.this, cart, vi);
                    list.setAdapter(adapter);
                    setCartTitle();
                } else {
                    if (cart.contains(item)) {
                        cart.remove(item);
                    } else {
                        cart.add(item);
                    }
                    adapter.notifyDataSetChanged();
                }
                cartButton.setText("Cart: " + cart.size());
            }
        });

        dba = new DbAdapter(this);
        dba.open();
        vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        getType();

        cart        = new ArrayList<LessonItem>();
        filtered    = false;
        viewingCart = false;
        exportButton.setVisibility(View.INVISIBLE);
        allChars.setName("All Characters");
        allChars.setStringId("ALL_CHARACTERS");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dba.close();
    };

    /**
     * Initializes the type field based on the bundle passed with it, then
     * populates the source and display lists.
     */
    private void getType() {
        Bundle bun = getIntent().getExtras();
        if (bun != null && bun.containsKey("type")) {

            String type = bun.getString("type");
            if (type.equals("character")) {
                this.type = ItemType.CHARACTER;
                title.setText(R.string.instruction_export_chars);
                getChars();
            } else if (type.equals("word")) {
                this.type = ItemType.WORD;
                title.setText(R.string.instruction_export_words);
                getWords();
            } else if (type.equals("lesson")) {
                this.type = ItemType.LESSON;
                title.setText(R.string.instruction_export_lessons);
                getLessons();
            } else {
                showToast("Invalid type");
                finish();
            }

            Collections.sort(source);
            if (this.type == ItemType.LESSON) {
                // want this to be at the end
                source.add(allChars);
            }
            display = source;
            adapter = new ShoppingCartListAdapter(this, display, vi);
            list.setAdapter(adapter);
            registerForContextMenu(list);

        } else {
            showToast("No type specified");
            finish();
        }
    }

    /**
     * Populate the list with characters
     */
    private void getChars() {
        List<Long> ids = dba.getAllCharIds();
        source = new ArrayList<LessonItem>(ids.size());
        for (long id : ids) {
            LessonCharacter character = dba.getCharacterById(id);
            source.add(character);
        }
    }

    /**
     * Populate the list with words
     */
    private void getWords() {
        List<String> ids = dba.getAllWordIds();
        source = new ArrayList<LessonItem>(ids.size());
        for(String id : ids){
            LessonWord word = dba.getWordById(id);
            source.add(word);
        }
    }

    /**
     * Populate the list with lessons
     */
    private void getLessons() {
        List<String> ids = dba.getAllLessonIds();
        source = new ArrayList<LessonItem>(ids.size());
        for(String id : ids){
            Lesson le = dba.getLessonById(id);
            le.setTagList(dba.getLessonTags(id));
            source.add(le);
        }
    }

    /**
     * Click handler for "Cart"/"Back" button
     * @param view The button
     */
    public void onClickViewCart(View view) {
        if (viewingCart) { // go back to list view
            showList();
        } else { // go to cart view
            showCart();
        }
        list.setAdapter(adapter);
    }

    /**
     * Display the current cart
     */
    private void showCart() {
        viewingCart = true;
        setCartTitle();
        cartButton.setText(R.string.back);
        exportButton.setVisibility(View.VISIBLE);
        filterButton.setVisibility(View.INVISIBLE);
        filterStatus.setVisibility(View.INVISIBLE);
        selectButton.setVisibility(View.INVISIBLE);
        deselectButton.setVisibility(View.INVISIBLE);

        Collections.sort(cart);
        adapter = new ShoppingCartListAdapter(this, cart, vi);
    }

    /**
     * Display the selection list of items
     */
    private void showList() {
        viewingCart = false;
        switch (type) {
            case CHARACTER:
                title.setText(R.string.instruction_export_chars);
                break;
            case WORD:
                title.setText(R.string.instruction_export_words);
                break;
            case LESSON:
                title.setText(R.string.instruction_export_lessons);
                break;
        }
        cartButton.setText("Cart: " + cart.size());
        exportButton.setVisibility(View.INVISIBLE);
        filterButton.setVisibility(View.VISIBLE);
        filterStatus.setVisibility(View.VISIBLE);
        selectButton.setVisibility(View.VISIBLE);
        deselectButton.setVisibility(View.VISIBLE);

        adapter = new ShoppingCartListAdapter(this, display, vi);
    }

    /**
     * Click handler for "Select All" button
     * @param view The button
     */
    public void onClickSelectAll(View view) {
        for (LessonItem item : display) {
            if (!cart.contains(item)) {
                cart.add(item);
            }
        }
        adapter.notifyDataSetChanged();
        cartButton.setText("Cart: " + cart.size());
    }

    /**
     * Click handler for "Deselect All" button
     * @param view The button
     */
    public void onClickDeselectAll(View view) {
        for (LessonItem item : display) {
            if (cart.contains(item)) {
                cart.remove(item);
            }
        }
        adapter.notifyDataSetChanged();
        cartButton.setText("Cart: " + cart.size());
    }

    /**
     * Click handler for "Filter" button
     * @param view The button
     */
    public void onClickFilter(View view) {
        if (filtered) { // clear the filter
            clearFilter();
        } else { // prompt and set new filter
            showFilterPopup();
        }
    }

    /**
     * Displays the filter popup and contains the code to filter 
     */
    public void showFilterPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Apply Filter");

        final EditText filterText = new EditText(this);
        builder.setView(filterText);

        builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String search = filterText.getText().toString().toLowerCase();
                if (search.equals("")) {
                    hideKeyboard(filterText);
                    return;
                }

                // Filter action: keep matching items from display list
                // Note that it should be partial match for search terms 2
                // characters or more.
                List<LessonItem> newList = new ArrayList<LessonItem>();
                switch (type) {
                    case CHARACTER:
                    case WORD:
                        topLoop: for (LessonItem item : display) {
                            List<String> tags = item.getTags();
                            for (String tag : tags) {
                                if ((search.length() >= 2 && tag.toLowerCase().contains(search)) ||
                                        tag.equalsIgnoreCase(search)) {
                                    newList.add(item);
                                    continue topLoop;
                                }
                            }
                            Collection<String> values = item.getKeyValues().values();
                            for (String value : values) {
                                if ((search.length() >= 2 && value.toLowerCase().contains(search)) ||
                                        value.equalsIgnoreCase(search)) {
                                    newList.add(item);
                                    continue topLoop;
                                }
                            }
                        }
                        break;
                    case LESSON:
                        for (LessonItem item : display) {
                            String name = ((Lesson) item).getLessonName();
                            if ((search.length() >= 2 && name.toLowerCase().contains(search)) ||
                                    name.equalsIgnoreCase(search)) {
                                newList.add(item);
                            }
                        }
                        break;
                }
                display = newList;
                adapter = new ShoppingCartListAdapter(
                        ShoppingCartActivity.this, display, vi);
                list.setAdapter(adapter);

                // Set state to filtered
                filterButton.setText(R.string.clear_filter);
                filtered = true;
                filterStatus.setText("Filter: " + search);
                hideKeyboard(filterText);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                hideKeyboard(filterText);
            }
        });

        AlertDialog dialog = builder.create();

        // show the keyboard
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();

    }

    /**
     * Clears the current filter
     */
    public void clearFilter() {
        display = source;
        adapter = new ShoppingCartListAdapter(this, display, vi);
        list.setAdapter(adapter);

        filterButton.setText(R.string.filter);
        filtered = false;
        filterStatus.setText(R.string.filter_none);
    }

    /**
     * Hides the keyboard
     * @param view The current view
     */
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Click handler for "Export" button
     * @param view The button
     */
    public void onClickExport(final View view) {
        export("");
    }
    
    private void export(String defaultText) {
        if (cart.size() == 0) {
            showToast("Add items to your cart first!");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save as...");

        final EditText text = new EditText(this);
        text.setText(defaultText);
        builder.setView(text);

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String filename = text.getText().toString();
                if (filename.equals("")) {
                    showToast("Please enter a filename");
                    export("");
                    return;
                }

                if (filename.contains(" ") || filename.contains(".")) {
                    showToast("Spaces and periods are not allowed");
                    export(filename.replaceAll("[\\. ]", "_"));
                    return;
                }

                // to ensure that all character dependencies are met
                List<LessonCharacter> dependencies = new ArrayList<LessonCharacter>();

                String xml = "<ttw name=\"" + filename + "\">\n";
                for (LessonItem item : cart) {
                    if (item == allChars) {
                        List<Long> ids = dba.getAllCharIds();
                        source = new ArrayList<LessonItem>(ids.size());
                        for (long id : ids) {
                            LessonCharacter character = dba.getCharacterById(id);
                            if (!cart.contains(character) &&
                                    !dependencies.contains(character)) {
                                dependencies.add(character);
                            }
                        }
                        continue;
                    }
                    
                    xml += item.toXml();

                    // if it's a lesson, we need to make sure dependencies are
                    // met
                    if (item instanceof Lesson) {
                        Lesson lesson = (Lesson) item;
                        List<LessonItem> words = lesson.getWords();

                        for (LessonItem word : words) {
                            List<LessonCharacter> characters = ((LessonWord) word).getCharacters();

                            for (LessonCharacter character : characters) {
                                if (!cart.contains(character) &&
                                        !dependencies.contains(character)) {
                                    dependencies.add(character);
                                }
                            }
                        }
                    }
                }

                for (LessonCharacter character : dependencies) {
                    xml += character.toXml();
                }

                xml += "</ttw>\n";
                writeStringToFile(xml, filename);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showToast("Not saved");
            }
        });

        AlertDialog dialog = builder.create();

        // show the keyboard
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();

    }

    /**
     * Write the given String to the device's external file system
     * location: external_root/data/"file_dir_name", file_dir_name from resource 
     * @param xml The string that you want to write to the device
     * @param filename the filename, ".ttw" will be automatically attached to the end
     */
    public void writeStringToFile(String xml, String filename) {
        if (filename == null || filename.length() == 0 || xml == null) {
            return;
        }

        String extFilesDir = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/data/" + getString(R.string.file_dir_name);
        // make sure that directory is created.
        (new File(extFilesDir)).mkdirs();
        File outFile = new File(extFilesDir, filename + ".ttw");

        try {
            FileWriter outFileWriter = new FileWriter(outFile, false);

            synchronized (xml) {
                outFileWriter.write(xml);
            }
            outFileWriter.flush();
            outFileWriter.close();

            showToast("Exported " + extFilesDir + "/" + filename + ".ttw successfully.");
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Error while writing a file to the device!");
            return;
        }
    }
    
    private void setCartTitle() {
        String t;
        if (cart.size() == 1) {
            t = " item in cart";
        } else {
            t = " items in cart";
        }
        title.setText(cart.size() + t);
    }

    /**
     * Display a toast message
     * @param msg The message
     */
    private final void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private class ShoppingCartListAdapter extends LessonItemListAdapter {

        public ShoppingCartListAdapter(Context context,
                List<LessonItem> objects, LayoutInflater vi) {
            super(context, objects, vi);
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            return _items.get(position).getItemType().ordinal();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LessonItem item = _items.get(position);
            View v = convertView;
            if (v == null) {
                switch (item.getItemType()) {
                    case CHARACTER:
                    case WORD:
                        v = _vi.inflate(R.layout.shopping_cart_item, null);
                        break;
                    case LESSON:
                        v = _vi.inflate(R.layout.shopping_cart_lesson, null);
                        break;
                }
            }

            // character and word views
            ImageView image   = (ImageView) v.findViewById(R.id.li_image);
            TextView  idView  = (TextView)  v.findViewById(R.id.idView);
            TextView  tagView = (TextView)  v.findViewById(R.id.tagView);

            // lesson views
            TextView nameView = (TextView) v.findViewById(R.id.nameView);
            TextView sizeView = (TextView) v.findViewById(R.id.sizeView);

            // both
            CheckBox checkbox = (CheckBox) v.findViewById(R.id.checkbox);

            // ids
            switch (item.getItemType())
            {
                case CHARACTER:
                case WORD:
                    // image
                    Bitmap bitmap = BitmapFactory.buildBitmap(item, 64);
                    image.setImageBitmap(bitmap);

                    // ids
                    LinkedHashMap<String, String> keyValues = item.getKeyValues();
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, String> entry : keyValues.entrySet()) {
                        sb.append(", " + entry.getKey() + ": " + entry.getValue());
                    }
                    String s = sb.length() > 0 ? sb.substring(2) : "";
                    idView.setText(s);

                    // tags
                    ArrayList<String> tags = new ArrayList<String>(item.getTags());
                    sb = new StringBuilder();
                    for (String tag : tags) {
                        sb.append(", "+tag);
                    }
                    s = sb.length() > 0 ? sb.substring(2) : "";
                    tagView.setText(s);

                    break;
                case LESSON:
                    Lesson lesson = (Lesson) item;
                    int count = lesson.getNumWords();
                    nameView.setText(lesson.getLessonName());
                    if (lesson != allChars) {
                        sizeView.setText(count + (count == 1 ? " word" : " words"));
                    } else {
                        sizeView.setText("");
                    }
                    break;
            }

            // checkbox
            checkbox.setChecked(cart.contains(item));

            return v;
        }
    }
}
