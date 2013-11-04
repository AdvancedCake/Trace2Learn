package com.trace2learn.TraceLibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.trace2learn.TraceLibrary.Database.Lesson;
import com.trace2learn.TraceLibrary.Database.DbAdapter;

public class MainUserActivity extends TraceBaseActivity {

	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	ImageView logo;
	Button introButton;
	Button createPhraseButton;
	Button browseButton;
	Button upgradeButton;
	Button quizButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu_user);

		getViews();
		getHandlers();

		prefs = getSharedPreferences(Toolbox.PREFS_FILE, MODE_PRIVATE);
		editor = prefs.edit();

		checkFirstStart();

		// Set user permissions
		String pack = getApplicationContext().getPackageName();
		boolean isFullVer = 
				pack.equals(Toolbox.PACKAGE_CH_TRADITIONAL) ||
				pack.equals(Toolbox.PACKAGE_CH_SIMPLIFIED);
		
		editor.putBoolean(Toolbox.PREFS_IS_FULL_VER, isFullVer);
		editor.putBoolean(Toolbox.PREFS_IS_ADMIN, false);
		editor.commit();

		// init db - optimization: don't load all characters up front
		Toolbox.initDbAdapter(getApplicationContext(), /* initializeChars */
				false);

		Toolbox.determineScreenSize(getApplicationContext());

		// set app title dynamically, read from the manifest file
		PackageManager pm = getPackageManager();
		CharSequence appLabel = getApplicationInfo().loadLabel(pm);
		((TextView) findViewById(R.id.title)).setText(appLabel);
		((TextView) findViewById(R.id.title))
				.setGravity(Gravity.CENTER_HORIZONTAL);

		// hack, remove logo from small screen as it crowds out the menu items
		if (Toolbox.isSmallScreen()) {
			logo.setImageBitmap(null);
		}

		// show Upgrade button if free version
		if (!isFullVer) {
			findViewById(R.id.upgradeButton).setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void getViews() {
		logo = (ImageView) findViewById(R.id.logo);
		introButton = (Button) findViewById(R.id.introButton);
		createPhraseButton = (Button) findViewById(R.id.buildPhraseButton);
		browseButton = (Button) findViewById(R.id.browseButton);
		upgradeButton = (Button) findViewById(R.id.upgradeButton);
		quizButton = (Button) findViewById(R.id.quizButton);
		
		//quizButton.getBackground().setColorFilter(new LightingColorFilter(R.color.button_color1, R.color.button_color2));
	}

	private void getHandlers() {
		introButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						IntroductionActivity.class);
				startActivity(i);
			}
		});

		createPhraseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buildPhrase();
			}
		});

		browseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						BrowseLessonsActivity.class);
				startActivity(i);
			}
		});

		quizButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						QuizActivity.class);
				startActivity(i);
			}
		});
		
		upgradeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(Toolbox.LINK_APP_STORE));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(intent);
			}
		});
	}

	private void checkFirstStart() {

		// set this to true if the current version requires a database upgrade
		boolean shouldUpdateDatabase = true; /*current version 1.9*/

		// get currently installed app version
		int newVersion = 0;
		String newVersionLabel = "";
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			newVersion = packageInfo.versionCode;
			newVersionLabel = packageInfo.versionName;
		} catch (Exception e) {}

		// initialize the db if either it's a fresh install or there is a
		// version change
		boolean firstStart = prefs.getBoolean(Toolbox.PREFS_FIRST_START, true);
		int lastVersion = prefs.getInt(Toolbox.PREFS_LAST_VER_NUMBER, 0);

		if (firstStart || ( shouldUpdateDatabase && ( newVersion > lastVersion) ) ) {
			if ( importDatabase(this, lastVersion, newVersion, newVersionLabel, null)) {
				editor.putBoolean(Toolbox.PREFS_FIRST_START, false);
			}
			editor.putInt(Toolbox.PREFS_LAST_VER_NUMBER, newVersion);
			editor.commit();
		}
	}

	public static boolean importDatabase(Activity acty, int lastVersion,
			int newVersion, String newVersionLabel, File sourceFile) {
		Log.i("Initialize DB", "Attempting to import database");

		String dbPath = acty.getDatabasePath(DbAdapter.DATABASE_NAME)
				.getAbsolutePath();

		try {
			if (lastVersion > 0) {
				// check for any user-defined collections - need to temporarily
				// initialize the old database
				Toolbox.initDbAdapter(acty.getApplicationContext(), false);
				List<String> userCollNames = Toolbox.dba
						.getAllUserLessonNames();
				List<Lesson> userColls = Toolbox.dba.getAllUserLessons();
				Toolbox.resetDbAdapter();

				String initMsg = "Installing the latest database" + "\n"
						+ "Previous version: " + lastVersion + "\n"
						+ "New version: " + newVersionLabel + "\n";

				if (userCollNames.size() > 0)
					initMsg = initMsg
							+ "Existing custom collections will be retained: "
							+ userCollNames;

				// notify user of db upgrade
				AlertDialog.Builder builder = new AlertDialog.Builder(acty);
				PackageManager pm = acty.getPackageManager();
				builder.setIcon(acty.getApplicationInfo().loadIcon(pm));
				builder.setTitle(acty.getApplicationInfo().loadLabel(pm));
				builder.setMessage(initMsg);
				builder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.show();
			}

			// Open the db path if specified, otherwise use /assets as default
			// source
			InputStream is = null;
			if (sourceFile != null && sourceFile.exists())
				is = new FileInputStream(sourceFile);
			else
				is = acty.getBaseContext().getAssets()
						.open(Toolbox.INIT_DB_NAME);

			// Copy the database into the destination
			File out = new File(dbPath);
			out.mkdirs();
			out.delete();
			OutputStream os = new FileOutputStream(dbPath);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.flush();

			os.close();
			is.close();
			Log.i("Initialize DB", "Database successfully imported");
			return true;
		} catch (Exception e) {
			Log.e("Initialize DB",
					e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Checks that this is the full version of the app, then starts the activity
	 * to build a phrase.
	 */
	private void buildPhrase() {
		if (prefs.getBoolean(Toolbox.PREFS_IS_FULL_VER, false)) {
			Intent i = new Intent(getApplicationContext(),
					CreateWordActivity.class);
			startActivity(i);
		} else {
			Toolbox.promptAppUpgrade(this);
		}
	}

}
