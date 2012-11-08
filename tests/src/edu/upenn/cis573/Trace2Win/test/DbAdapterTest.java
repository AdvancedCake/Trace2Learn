package edu.upenn.cis573.Trace2Win.test;

import junit.framework.TestCase;
import edu.upenn.cis573.Trace2Win.*;
import edu.upenn.cis573.Trace2Win.Database.DbAdapter;
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
