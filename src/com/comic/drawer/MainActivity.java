package com.comic.drawer;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {


	DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
	ViewPager mViewPager;
	File directory1 = new File(Environment.getExternalStorageDirectory()+File.separator+"ComicDrawer/zip");
	File directory2 = new File(Environment.getExternalStorageDirectory()+File.separator+"ComicDrawer/cbr");
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);

		mViewPager.setAdapter(mDemoCollectionPagerAdapter);
		
		if(!directory1.exists())
		directory1.mkdirs();
		else if (!directory2.exists())
			directory2.mkdirs();


	}

	public class DemoCollectionPagerAdapter extends FragmentPagerAdapter 

	{

		public DemoCollectionPagerAdapter(FragmentManager fm)     
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int i) 
		{
			switch(i)
			{
			case 0 :
				Fragment fragment = new ZipFragment();
				Bundle args = new Bundle();
				// Our object is just an integer :-P
				args.putInt(ZipFragment.ARG_OBJECT, i + 1);
				fragment.setArguments(args);
				return fragment;	        
			case 1 :
				Fragment fragment1 = new CbrFragment();
				Bundle args1 = new Bundle();
				// Our object is just an integer :-P
				args1.putInt(ZipFragment.ARG_OBJECT, i + 1);
				fragment1.setArguments(args1);
				return fragment1;		        
		
				default: Fragment fragment4 = new ZipFragment();
				Bundle args4 = new Bundle();
				// Our object is just an integer :-P
				args4.putInt(ZipFragment.ARG_OBJECT, i + 1);
				fragment4.setArguments(args4);
				return fragment4;

			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) 
		{
			switch(position)
			{
				case 0: return "Compressed Files";
				case 1: return "Comic Files";
				default: return "Something fishy";
			}
			
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			
			final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create(); //Read Update
	        alertDialog.setTitle("Comic Drawer");
	        alertDialog.setMessage("Comic Drawer App. Read cbr, cbz, zip, rar, jar, tar and war format Comics.");
	        alertDialog.setButton("Cool!", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int which) {
	             alertDialog.cancel();
	           }
	        });

	        alertDialog.show(); 
			return true;
		case R.id.action_like:
				final String appPackageName = getApplicationContext().getPackageName(); // getPackageName() from Context or Activity object
				try {
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
				    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=com.comic.drawer")));
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+ appPackageName)));
				}
			return true;
		case R.id.open:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
			    + "/ComicDrawer/");
			intent.setDataAndType(uri, "text/csv");
			startActivity(Intent.createChooser(intent, "Open folder"));
			return true;
			
		case R.id.refresh:
			 FragmentManager fragManager = getSupportFragmentManager();
			    ZipFragment fragment = (ZipFragment)fragManager.getFragments().get(0);
			    fragment.refresh();
			    CbrFragment fragment1 = (CbrFragment)fragManager.getFragments().get(1);
			    fragment1.refresh();
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}

	}

}
