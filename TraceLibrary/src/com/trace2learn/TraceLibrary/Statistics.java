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

import com.trace2learn.TraceLibrary.Database.DbAdapter;
import com.trace2learn.TraceLibrary.Database.LessonCharacter;

public class Statistics extends TraceBaseActivity {

    
    private DbAdapter dba;
    
    private TextView		countCollections;
    private TextView		countPhrases;
    private TextView		countCharacters;
    private TextView		countStrokes;
    private ImageView		exitButton;
    private LinearLayout	chartLayout;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		
		setContentView(R.layout.stats);
		
        // Initialize Views and Handlers
        getViews();
        getHandlers();
        
        // Initialize database adapter
        dba = new DbAdapter(this);
        dba.open();
        
        String txt = "";
        int totalStrokes = 0;
        int maxStrokes = 0;
        int maxTally = 0;
 		
        List<String> lids = dba.getAllLessonIds();
        txt += lids.size();
        countCollections.setText(txt);
                
        List<String> wids = dba.getAllWordIds();
        txt = "";
        txt += wids.size();
        countPhrases.setText(txt);

        List<String> cids = dba.getAllCharIds();
        txt = "";
        txt += cids.size();
        countCharacters.setText(txt);

        // Count total strokes, and also keep track of the distribution
        // of stroke counts across characters
        LinkedHashMap<Integer, Integer> strokeDistrib = new LinkedHashMap<Integer, Integer>();
        for(String id : cids){
        	LessonCharacter ch = dba.getCharacterById(id);
        	int numStrokes = ch.getNumStrokes();
        	totalStrokes += numStrokes;
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
        txt = "";
        txt += totalStrokes;
        countStrokes.setText(txt);
        
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
        Log.i("Statistics chart data: ", "max X " + distribSeries.getMaxX());

        
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
        multiRenderer.setYAxisMax(maxTally+1);
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
    	countStrokes		= (TextView) findViewById(R.id.countStrokes);
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
    	
}
