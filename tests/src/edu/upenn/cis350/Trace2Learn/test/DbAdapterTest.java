package edu.upenn.cis350.Trace2Learn.test;

import junit.framework.TestCase;
import edu.upenn.cis350.Trace2Learn.*;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAdapterTest extends TestCase {

	DbAdapter a;
	protected void setUp() throws Exception 
	{
		a = new DbAdapter(CharacterCreationActivity.class.newInstance());
		
	}
	
	public void test()
	{
		
	}
}
