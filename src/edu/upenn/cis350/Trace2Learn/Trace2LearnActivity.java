package edu.upenn.cis350.Trace2Learn;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Trace2LearnActivity extends Activity {
	Button butt;
	TextView right;
	
	EditText editText;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        butt = (Button) findViewById(R.id.butt);
        right = (TextView) findViewById(R.id.numClicks);

    }

    public void onButtonClick (View view) {
    	if (view == butt)
    	{
    		String txt = right.getText().toString();
    		int count = Integer.parseInt(txt);
    		count++;
    		String increased = Integer.toString(count);
    		right.setText(increased);
    	}
    }
    
}