package com.trace2learn.Trace2Learn.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Toast;

import com.trace2learn.TraceLibrary.ShoppingCartActivity;


public class ShoppingCartActivityTest extends ActivityInstrumentationTestCase2<ShoppingCartActivity>{
	
	public ShoppingCartActivityTest(){
		super("com.trace2learn.Trace2Learn.TraceLibrary",ShoppingCartActivity.class);
	}
	
	private ShoppingCartActivity activity;
	
	
	public void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
	}
	
    /**
     * Display a toast message
     * @param msg The message
     */
    private final void showToast(String msg){
        Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }	

	/** helper function for testing
	 * Read a String from the file whose name is given     * 
	 * location: external_root/data/"file_dir_name", file_dir_name from resource 
	 * @param filename the filename including ".ttw". 
	 *        assumed to be in the internal storage
	 * @return String The string which is contained in the given file
	 */
	public String readStringFromFile(String filename) { 
		String extFilesDir = Environment.getExternalStorageDirectory().getAbsolutePath() +
				"/data/" + activity.getString(R.string.file_dir_name);
		File outFile = new File(extFilesDir, filename);
		StringBuffer sb = new StringBuffer();

		try {		   
			BufferedReader buf = new BufferedReader(new FileReader(outFile));

			String strTemp = buf.readLine();
			while (strTemp != null) {
				sb.append(strTemp);
				strTemp = buf.readLine();
			}

			buf.close();   
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			showToast("Error while reading '" + filename + "' from the device!");
			return null;
		}    	
	}       	
	
	public void testWriteStringToFile(){
		String inputStr = "<tag></tag>";
		String filename = "export1";
		
		activity.writeStringToFile(inputStr, filename, /*append*/ false);
		
		String outputStr = readStringFromFile(filename + ".ttw");
		
		assertEquals(inputStr, outputStr);
	}
}
