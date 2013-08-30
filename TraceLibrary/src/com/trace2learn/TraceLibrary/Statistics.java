package com.trace2learn.TraceLibrary;

import java.util.List;
import java.util.LinkedHashMap;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Align;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import org.achartengine.renderer.XYSeriesRenderer;

import com.trace2learn.TraceLibrary.Database.LessonItem;
import com.trace2learn.TraceLibrary.Database.LessonCharacter;

public class Statistics extends TraceBaseActivity {
    
    private TextView		countCollections;
    private TextView		countPhrases;
    private TextView		countCharacters;
    private ImageView		exitButton;
    private LinearLayout	chartLayout;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		
		setContentView(R.layout.stats);
		
        // Initialize Views and Handlers
        getViews();
        getHandlers();
        
        String txt = "";
        int maxStrokes = 0;
        int maxTally = 0;
 		
        List<String> lids = Toolbox.dba.getAllLessonIds();
        txt = "" + lids.size();
        countCollections.setText(txt);
                
        List<String> wids = Toolbox.dba.getAllWordIds();
        txt = "" + wids.size();
        countPhrases.setText(txt);

        List<String> chids = Toolbox.dba.getAllCharIds();
        txt = "" + chids.size();
        countCharacters.setText(txt);
        
        SharedPreferences prefs = getSharedPreferences(Toolbox.PREFS_FILE, MODE_PRIVATE);
        boolean isAdmin = prefs.getBoolean(Toolbox.PREFS_IS_ADMIN, false);
        

        // Distribution of stroke counts across characters
        LinkedHashMap<Integer, Integer> strokeDistrib = new LinkedHashMap<Integer, Integer>();
        
        if(isAdmin) {
	        List<LessonItem> chars = Toolbox.getAllCharacters();
	        for(LessonItem item : chars) {
	        	LessonCharacter ch = (LessonCharacter) item;
	        	int numStrokes = ch.getNumStrokes();
	        	if(numStrokes > maxStrokes) maxStrokes = numStrokes;
	        	// build a map of stroke count to # of matching characters
	        	if(!strokeDistrib.containsKey(numStrokes)){
	        		strokeDistrib.put(numStrokes, 1);
	        	}
	        	else {
	        		int prevTally = strokeDistrib.get(numStrokes);
	        		strokeDistrib.put(numStrokes, prevTally+1);
	        		if(prevTally+1 > maxTally) maxTally = prevTally+1;
	        	}
	        }
        }
        else /*non-admin, user mode*/
        {
        	// optimization, hard-code the distribution since will not change in
        	// user mode, and building it from stroke data is very time-intensive
        	strokeDistrib.put(1, 1);
        	strokeDistrib.put(2, 12);
        	strokeDistrib.put(3, 22);
        	strokeDistrib.put(4, 43);
        	strokeDistrib.put(5, 53);
        	strokeDistrib.put(6, 69);
        	strokeDistrib.put(7, 60);
        	strokeDistrib.put(8, 98);
        	strokeDistrib.put(9, 70);
        	strokeDistrib.put(10, 77);
        	strokeDistrib.put(11, 85);
        	strokeDistrib.put(12, 77);
        	strokeDistrib.put(13, 58);
        	strokeDistrib.put(14, 47);
        	strokeDistrib.put(15, 42);
        	strokeDistrib.put(16, 30);
        	strokeDistrib.put(17, 24);
        	strokeDistrib.put(18, 19);
        	strokeDistrib.put(19, 12);
        	strokeDistrib.put(20, 5);
        	strokeDistrib.put(21, 5);
        	strokeDistrib.put(22, 5);
        	strokeDistrib.put(23, 3);
        	strokeDistrib.put(24, 0);
        	strokeDistrib.put(25, 3);
        	strokeDistrib.put(26, 2);
        	strokeDistrib.put(27, 1);
        	maxStrokes = 27;
        	maxTally= 98;
        }
        
        Log.i("Statistics chart data: ", strokeDistrib.toString());
        
        // Creating an  XYSeries for the bar chart
        XYSeries distribSeries = new XYSeries("Stroke Distribution");
        for(int i = 1; i <= maxStrokes; i++) {
        	if(strokeDistrib.containsKey(Integer.valueOf(i)))
        		distribSeries.add(i, strokeDistrib.get(Integer.valueOf(i)));
        	else
        		distribSeries.add(i, 0);
        }

        Log.i("Statistics chart data: ", "max strokes " + maxStrokes);

        
        // Creating a data set
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(distribSeries);
 
        // Creating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer distribRenderer = new XYSeriesRenderer();
        distribRenderer.setColor(Color.MAGENTA);
        distribRenderer.setFillPoints(true);
        distribRenderer.setDisplayChartValues(true);
        distribRenderer.setChartValuesTextAlign(Align.CENTER);
        // make chart value labels dynamically sized depending on device
        distribRenderer.setChartValuesTextSize(getResources().getDimension(R.dimen.barchart_values_text_size));
        distribRenderer.setLineWidth(1);        
        
        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabelsColor(Color.WHITE);
        multiRenderer.setXTitle("Stroke Count");
        multiRenderer.setXAxisMin(0);
        multiRenderer.setXAxisMax(maxStrokes+1);
        multiRenderer.setYAxisMin(0);
        multiRenderer.setYAxisMax(maxTally+5);
        // render vertically, to achieve horizontal bar chart
        multiRenderer.setOrientation(Orientation.VERTICAL);
        multiRenderer.setBarSpacing(0.2);
        multiRenderer.setShowLegend(false);
        multiRenderer.setAxisTitleTextSize(getResources().getDimension(R.dimen.barchart_values_text_size));
        multiRenderer.setLabelsTextSize(getResources().getDimension(R.dimen.barchart_values_text_size));
        multiRenderer.addSeriesRenderer(distribRenderer);
        
        GraphicalView chartView = 
        	ChartFactory.getBarChartView(getApplicationContext(), dataset, multiRenderer, BarChart.Type.DEFAULT);
        
        try {
        	chartLayout.addView(chartView);
        }
        catch (Exception e) {
        	Log.i("Statistics chart data exception: ", e.toString());
        }
                
	}

    private void getViews() {
    	countCollections	= (TextView) findViewById(R.id.countCollections);
    	countPhrases		= (TextView) findViewById(R.id.countPhrases);
    	countCharacters		= (TextView) findViewById(R.id.countCharacters);
        exitButton			= (ImageView) findViewById(R.id.exit_button);
        chartLayout			= (LinearLayout) findViewById(R.id.chart);        
    }
    
    private void getHandlers() {
        exitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    	
}
