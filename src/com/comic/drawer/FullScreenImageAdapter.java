package com.comic.drawer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
 
public class FullScreenImageAdapter extends PagerAdapter {
 
    private Context _activity;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;
 
    // constructor
    public FullScreenImageAdapter(Context activity, ArrayList<String> imagePaths) 
    {
        _activity = activity;
        _imagePaths = imagePaths;
    }
 
    @Override
    public int getCount() {
        return _imagePaths.size();
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) 
    {
        return view == ((RelativeLayout) object);
    }
     
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;
  
        inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,false);
  
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
         
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
        imgDisplay.setImageBitmap(bitmap);
         
        
  
        ((ViewPager) container).addView(viewLayout);
  
        return viewLayout;
    }
     
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
  
    }
}