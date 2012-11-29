package edu.upenn.cis573.Trace2Win;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.upenn.cis573.Trace2Win.Database.DbAdapter;
import edu.upenn.cis573.Trace2Win.Database.Lesson;
import edu.upenn.cis573.Trace2Win.Database.LessonCharacter;
import edu.upenn.cis573.Trace2Win.Database.LessonItem;
import edu.upenn.cis573.Trace2Win.Database.LessonItem.ItemType;
import edu.upenn.cis573.Trace2Win.Database.LessonWord;

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
                } else {
                    if (cart.contains(item)) {
                        cart.remove(item);
                    } else {
                        cart.add(item);
                    }
                    adapter.notifyDataSetChanged();
                }
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
    }

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
            display = source;
            adapter = new ShoppingCartListAdapter(this, display, vi);
            list.setAdapter(adapter);
            registerForContextMenu(list);

        } else {
            showToast("No type specified");
            finish();
        }
    }

    private void getChars() {
        List<Long> ids = dba.getAllCharIds();
        source = new ArrayList<LessonItem>(ids.size());
        for (long id : ids) {
            LessonCharacter character = dba.getCharacterById(id);
            source.add(character);
        }
    }

    private void getWords() {
        List<Long> ids = dba.getAllWordIds();
        source = new ArrayList<LessonItem>(ids.size());
        for(long id : ids){
            LessonWord word = dba.getWordById(id);
            source.add(word);
        }
    }

    private void getLessons() {
        List<Long> ids = dba.getAllLessonIds();
        source = new ArrayList<LessonItem>(ids.size());
        for(long id : ids){
            Lesson le = dba.getLessonById(id);
            le.setTagList(dba.getLessonTags(id));
            source.add(le);
        }
    }
    
    public void onClickViewCart(View view) {
        if (viewingCart) { // go back to list view
            viewingCart = false;
            switch (type) {
                case CHARACTER:
                    title.setText(R.string.instruction_export_chars);
                case WORD:
                    title.setText(R.string.instruction_export_words);
                case LESSON:
                    title.setText(R.string.instruction_export_lessons);
            }
            cartButton.setText(R.string.cart);
            exportButton.setVisibility(View.INVISIBLE);
            filterButton.setVisibility(View.VISIBLE);
            filterStatus.setVisibility(View.VISIBLE);
            selectButton.setVisibility(View.VISIBLE);
            deselectButton.setVisibility(View.VISIBLE);
            
            adapter = new ShoppingCartListAdapter(this, display, vi);
        } else { // go to cart view
            viewingCart = true;
            String t;
            if (cart.size() == 1) {
                t = " item in cart";
            } else {
                t = " items in cart";
            }
            title.setText(cart.size() + t);
            cartButton.setText(R.string.back);
            exportButton.setVisibility(View.VISIBLE);
            filterButton.setVisibility(View.INVISIBLE);
            filterStatus.setVisibility(View.INVISIBLE);
            selectButton.setVisibility(View.INVISIBLE);
            deselectButton.setVisibility(View.INVISIBLE);
            
            Collections.sort(cart);
            adapter = new ShoppingCartListAdapter(this, cart, vi);
        }
        list.setAdapter(adapter);
    }
    
    public void onClickSelectAll(View view) {
        showToast("SelectAll!!");
    }

    public void onClickDeselectAll(View view) {
        showToast("DeselectAll!!");
    }

    public void onClickFilter(View view) {
        showToast("Filter!!");
    }

    public void onClickExport(View view) {
        showToast("Export!!");
    }

    private final void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    
    private class ShoppingCartListAdapter extends LessonItemListAdapter {

        public ShoppingCartListAdapter(Context context,
                List<LessonItem> objects, LayoutInflater vi) {
            super(context, objects, vi);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = _vi.inflate(R.layout.shopping_cart_item, null);
            }
            LessonItem item     = _items.get(position);
            ImageView  image    = (ImageView) v.findViewById(R.id.li_image);
            TextView   idView   = (TextView) v.findViewById(R.id.idView);
            TextView   tagView  = (TextView) v.findViewById(R.id.tagView);
            CheckBox   checkbox = (CheckBox) v.findViewById(R.id.checkbox);
            Bitmap     bitmap   = BitmapFactory.buildBitmap(item, 64);
            image.setImageBitmap(bitmap);

            // ids
            switch (item.getItemType())
            {
                case CHARACTER:
                case WORD:
                    LinkedHashMap<String, String> keyValues = item.getKeyValues();
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, String> entry : keyValues.entrySet()) {
                        sb.append(", " + entry.getKey() + ": " + entry.getValue());
                    }       
                    String s = sb.length()>0 ? sb.substring(2) : "";
                    idView.setText(s);
                    break;
                case LESSON:
                    idView.setText(item.getPrivateTag());
                    break;      
            }

            // tags
            ArrayList<String> tags = new ArrayList<String>(item.getTags());
            StringBuilder sb = new StringBuilder();
            for(String tag : tags){
                Log.e("Tag","Found");
                sb.append(", "+tag);
            }
            String s = "";
            if(sb.length()>0){
                s = sb.substring(2);
                Log.e("Printing Tags",s);
            }
            tagView.setText(s);
            
            // checkbox
            checkbox.setChecked(cart.contains(item));
            
            return v;
        }
    }
}
