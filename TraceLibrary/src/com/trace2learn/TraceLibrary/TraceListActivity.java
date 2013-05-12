package com.trace2learn.TraceLibrary;

import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import com.trace2learn.TraceLibrary.R;
import com.trace2learn.TraceLibrary.Statistics;

/**
 * Common base class for all list activities - implements global menu options
 *   - return to start screen from any activity
 *   - show statistics page
 *   - show about pop-up
 */

public abstract class TraceListActivity extends ListActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.menuGoHome) {
        	Intent i1 = getBaseContext().getPackageManager()
            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        	i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(i1);        	        	
            return true;
        }
        else if (item.getItemId() == R.id.menuStats) {
            Intent i2 = new Intent(this, Statistics.class);
            startActivity(i2);
            return true;
        }
        else if(item.getItemId() == R.id.menuAbout) {
        	Toolbox.showAboutPopup(this);
        }

        return super.onOptionsItemSelected(item);

    }
    
    
}
