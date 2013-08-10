package com.trace2learn.TraceLibrary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.LessonCategory;
import com.trace2learn.TraceLibrary.Database.LessonItem;

public class BrowseLessonsActivity extends TraceListActivity {

    private ArrayList<Lesson> items;
    private LessonListAdapter adapter;
    private LayoutInflater vi;

    private boolean isAdmin;
    private boolean isFull;

    private enum ContextMenuItem {
        DELETE              (1, "Delete"),
        ASSIGN_CATEGORIES   (1, "Assign Categories"),
        MOVE_UP             (1, "Move Up"),
        MOVE_DOWN           (1, "Move Down"),
        TOGGLE_USER_DEFINED (2, "Toggle User-Defined");

        public final String text;
        public final int    privilege; // 0: anyone, 1: owner, 2: admin

        ContextMenuItem(int privilege, String text) {
            this.privilege = privilege;
            this.text      = text;
        }
    }

    private enum RequestCode {
        ASSIGN_CATEGORIES,
        BROWSE_WORDS;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_lessons);   

        // Identify and mark last viewed lesson
        SharedPreferences prefs = getSharedPreferences(Toolbox.PREFS_FILE, MODE_PRIVATE);
        String lastViewedId = prefs.getString(Toolbox.PREFS_LAST_VIEWED_LESSON, "");

        items = new ArrayList<Lesson>(); 
        List<String> ids = Toolbox.dba.getAllLessonIds();
        for(String id : ids){
            Lesson lesson = Toolbox.dba.getLessonById(id);
            lesson.setTagList(Toolbox.dba.getLessonTags(id));
            if(lastViewedId.equals(id)) {
            	lesson.setLastViewed(true);
            }
            items.add(lesson);
        }

        // Retrieve privileges
        isAdmin = prefs.getBoolean(Toolbox.PREFS_IS_ADMIN, false);
        isFull  = prefs.getBoolean(Toolbox.PREFS_IS_FULL_VER, false);

        vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adapter = new LessonListAdapter(this, items, vi, isFull);
        setListAdapter(adapter);
        registerForContextMenu(getListView());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    };

    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        super.onListItemClick(l, v, position, id);  

        clickOnItem(items.get(position));
    } 

    //when character is clicked, it starts the display mode for that char
    private void clickOnItem(LessonItem item) {
        Lesson lesson = (Lesson) item;
        String name = lesson.getLessonName();

        try {
            if (!isAdmin && !isFull && !lesson.isUserDefined() &&
                    Integer.valueOf(name.substring(0, name.indexOf(':'))) > 11) {
                Toolbox.promptAppUpgrade(this);
                return;
            }
        } catch (Exception e) {/* swallow exception, in case collection name didn't start with an integer */}

        // Record most recently viewed collection
        SharedPreferences prefs = this.getSharedPreferences(Toolbox.PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Toolbox.PREFS_LAST_VIEWED_LESSON, lesson.getStringId());
        editor.commit();

        // Launch the selected collection
        Intent i = new Intent(this, BrowseWordsActivity.class);
        i.putExtra("ID", lesson.getStringId());
        startActivityForResult(i, RequestCode.BROWSE_WORDS.ordinal());
    }

    private void getItemInfo(String lessonId) {
        Intent i = new Intent(this, LessonNarrativeActivity.class);
        i.putExtra("ID", lessonId);
        startActivity(i);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Lesson le = items.get(info.position);

        menu.setHeaderTitle("Options");
        for (ContextMenuItem item : ContextMenuItem.values()) {
            if (!isAdmin && !le.isUserDefined() && item.privilege >= 1) {
                // user does not own the lesson
                continue;
            } else if (!isAdmin && item.privilege >= 2) {
                // admin only option
                continue;
            }
            int ord = item.ordinal();
            menu.add(Menu.NONE, ord, ord, item.text);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        Lesson le = items.get(info.position);
        Context context = getApplicationContext();
        Log.e("MenuIndex",Integer.toString(menuItemIndex));
        Log.e("ListIndex",Integer.toString(info.position));

        // Delete lesson
        if (menuItemIndex == ContextMenuItem.DELETE.ordinal()) {
            String id = le.getStringId();
            String result = Toolbox.dba.deleteLesson(id);
            Log.e("Result", result);
            if (result == null) {
                Toolbox.showToast(context, "Could not delete the collection");
                return false;
            }
            else {
                Toolbox.showToast(context, "Successfully deleted");
                startActivity(getIntent()); 
                finish();
                return true;
            }
        }

        // Assign Categories
        else if (menuItemIndex == ContextMenuItem.ASSIGN_CATEGORIES.ordinal()) {
            Intent i = new Intent(context, ChooseLessonCategoryActivity.class);
            i.putExtra("ID",   le.getStringId());
            i.putExtra("name", le.getLessonName());

            boolean[] original = new boolean[] {false, false, false, false};
            SortedSet<LessonCategory> categories = le.getCategories();
            if (categories != null) {
                for (LessonCategory category : categories) {
                    original[category.ordinal()] = true;
                }
            }
            i.putExtra("categories", original);

            startActivityForResult(i, RequestCode.ASSIGN_CATEGORIES.ordinal());
            return true;
        }

        // Toggle User-Defined
        else if (menuItemIndex == ContextMenuItem.TOGGLE_USER_DEFINED.ordinal()) {
            le.setUserDefined(!le.isUserDefined());
            boolean result = Toolbox.dba.saveLessonUserDefined(le.getStringId(),
                    le.isUserDefined(), -1 * le.getSort());
            if (result) {
                le.setSort(-1 * le.getSort());
                Collections.sort(items);
                adapter = new LessonListAdapter(this, items, vi, isFull);
                setListAdapter(adapter);
                return true;
            } else {
                Toolbox.showToast(context, "Could not edit the collection");
                return false;
            }
        }

        // Move
        else if (menuItemIndex == ContextMenuItem.MOVE_UP.ordinal() ||
                menuItemIndex == ContextMenuItem.MOVE_DOWN.ordinal()) {
            int otherPos;
            if (menuItemIndex == ContextMenuItem.MOVE_UP.ordinal()) {
                otherPos = info.position - 1;
            } else {
                otherPos = info.position + 1;
            }

            // Check that other item exists
            if (otherPos < 0) {
                Toolbox.showToast(context, "Cannot move this collection up");
                return false;
            } else if (otherPos >= items.size()) {
                Toolbox.showToast(context, "Cannot move this collection down");
                return false;
            }

            Lesson other = items.get(otherPos);

            // Check that both are user-defined or admin defined
            if (le.isUserDefined() != other.isUserDefined()) {
                Toolbox.showToast(context, "Cannot move this collection");
                return false;
            }

            Log.i("BrowseLessons.move", "Attempting to swap " +
                    le.getLessonName() + " and " + other.getLessonName());
            boolean result = Toolbox.dba.swapLessons(le.getStringId(), le.getSort(),
                    other.getStringId(), other.getSort());
            if (result) { // success, so update the local copy
                Log.i("BrowseLessons.move", "Success");
                long temp = le.getSort();
                le.setSort(other.getSort());
                other.setSort(temp);
                Collections.sort(items);
                adapter = new LessonListAdapter(this, items, vi, isFull);
                setListAdapter(adapter);
                return true;
            }
            Log.e("BrowseLessons.move", "Failure");
            Toolbox.showToast(context, "Move failed");
            return false;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.ASSIGN_CATEGORIES.ordinal() &&
                resultCode == RESULT_OK) {
            startActivity(getIntent());
            finish();
        } else if (requestCode == RequestCode.BROWSE_WORDS.ordinal() &&
                resultCode == RESULT_OK) {
            startActivity(getIntent());
            finish();
        }
    }

    private class LessonListAdapter extends ArrayAdapter<Lesson> {

        private ArrayList<Lesson> items;

        private LayoutInflater vi;

        boolean isFull;

        private int defaultColor = -1;
        private int userColor;
        private int lockColor;

        public LessonListAdapter(Context context, List<Lesson> objects,
                LayoutInflater vi, boolean isFull) {
            super(context, 0, objects);
            this.items     = new ArrayList<Lesson>(objects);
            this.vi        = vi;
            this.isFull    = isFull;
            this.userColor = context.getResources().getColor(
                    R.color.user_collection);
            this.lockColor = context.getResources().getColor(
                    R.color.locked_collection);
        }

        /**
         * Configures the view for the given item in the list
         * @param position - the index of the item in the list
         * @param convertView - the constructed view that should be modified
         * @param parent - The contained of the list
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {     
            View v = convertView;

            if (v == null) {
                v = vi.inflate(R.layout.lesson_desc, null);
            }

            final Lesson lesson     = items.get(position);
            TextView     nameView   = (TextView)  v.findViewById(R.id.nameView);
            TextView     sizeView   = (TextView)  v.findViewById(R.id.sizeView);
            TextView     lastViewed = (TextView)  v.findViewById(R.id.lastViewed);
            ImageView    infoButton = (ImageView) v.findViewById(R.id.infoButton);
            ImageView    category1  = (ImageView) v.findViewById(R.id.category1);
            ImageView    category2  = (ImageView) v.findViewById(R.id.category2);
            ImageView    category3  = (ImageView) v.findViewById(R.id.category3);
            ImageView    category4  = (ImageView) v.findViewById(R.id.category4);
            ImageView[]  categories = {category1, category2, category3, category4};

            int    count = lesson.getNumWords();
            String name = lesson.getLessonName();
            nameView.setText(name);
            sizeView.setText(count + (count == 1 ? " phrase" : " phrases"));
            
            if(lesson.isLastViewed()) lastViewed.setText(R.string.last_viewed); else lastViewed.setVisibility(View.GONE);

            // Display category icons
            int i = 0;
            Set<LessonCategory> itemCategories = lesson.getCategories();
            if (itemCategories != null) {
                for (LessonCategory category : itemCategories) {
                    categories[i].setImageResource(category.rid);
                    i++;
                }
            }

            // Blank out all of the other icons
            for (; i < 4; i++) {
                categories[i].setImageResource(0);
            }

            // Save default text color
            if (defaultColor == -1) {
                defaultColor = nameView.getTextColors().getDefaultColor();
            }

            // Check if this is an admin lesson or user lesson
            if (lesson.isUserDefined()) {
                nameView.setTextColor(userColor);
                sizeView.setTextColor(userColor);
                nameView.setTypeface(null, Typeface.ITALIC);
            } else { // admin-created
                nameView.setTextColor(defaultColor);
                sizeView.setTextColor(defaultColor);
                nameView.setTypeface(null, Typeface.NORMAL);
            }

            // Check if this is the full version of the app
            try {
                if (!isFull && !lesson.isUserDefined() &&
                        Integer.valueOf(name.substring(0, name.indexOf(':'))) > 11) {
                    nameView.setTextColor(lockColor);
                    sizeView.setTextColor(lockColor);
                }
            } catch (Exception e) {}

            // Set onClick listener for info button
            infoButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItemInfo(lesson.getStringId());
                }
            });

            return v;
        }

    }


}
