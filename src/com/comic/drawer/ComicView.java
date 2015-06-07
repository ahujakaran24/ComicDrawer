package com.comic.drawer;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

	public class ComicView extends Activity {
	    /**
	     * The number of pages (wizard steps) to show in this demo.
	     */
	    private int NUM_PAGES = 5;
	   
	    /**
	     * The pager widget, which handles animation and allows swiping horizontally to access previous
	     * and next wizard steps.
	     */
	    private ViewPager mPager;
	    View mdecorView;
	    /**
	     * The pager adapter, which provides the pages to the view pager widget.
	     */
	    private PagerAdapter mPagerAdapter;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) 
	    {
	    	
	    	
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_comic_view);
	        mdecorView = getWindow().getDecorView();
	        mdecorView.setSystemUiVisibility(
	                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
	                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
	                | View.SYSTEM_UI_FLAG_IMMERSIVE);
	    

	       // Intent i = new Intent(); //This should be getIntent();
	        ArrayList<String> paths= getIntent().getStringArrayListExtra("paths");
	     
	        
	        // Instantiate a ViewPager and a PagerAdapter.
	        mPager = (ViewPager) findViewById(R.id.pager);
	     //  ComicView c = new ComicView();
	       
	        mPagerAdapter = new FullScreenImageAdapter(getApplicationContext(),paths);
	        mPager.setAdapter(mPagerAdapter);
	       
	    }
	    
	    @Override
	    public void onWindowFocusChanged(boolean hasFocus) {
	            super.onWindowFocusChanged(hasFocus);
	        if (hasFocus) {
	        	mdecorView.setSystemUiVisibility(
	                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	                    | View.SYSTEM_UI_FLAG_FULLSCREEN
	                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
	    }
	}
