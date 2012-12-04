package edu.upenn.cis573.Trace2Win.test;

import android.test.ActivityInstrumentationTestCase2;
import edu.upenn.cis573.Trace2Win.ShoppingCartActivity;


public class ShoppingCartActivityTest extends ActivityInstrumentationTestCase2<ShoppingCartActivity>{
	
	public ShoppingCartActivityTest(){
		super("edu.upenn.cis573.Trace2Win",ShoppingCartActivity.class);
	}
	
	private ShoppingCartActivity activity;
	
	
	public void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
	}
	
	public void testWriteAndReadFile(){
		String inputStr = "<tag></tag>";
		String filename = "export1";
		
		activity.writeStringToFile(inputStr, filename);
		
		String outputStr = activity.readStringFromFile(filename + ".ttw");
		
		assertEquals(inputStr, outputStr);
	}
}
