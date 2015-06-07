package com.comic.drawer;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.sax.StartElementListener;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
public class CustomAdapter extends BaseAdapter

{   
	ArrayList<String> files;
	Context context;
	private LruCache<String, Bitmap> mMemoryCache;
	ArrayList<String> imgs;
	final int maxMemory ;
	final int cacheSize;
	//File file= new File("abc");
	ArrayList<String> MyPathh;
	//	ArrayList<String> Listfile = new ArrayList<String>();
	DisplayImageOptions options;
	private static LayoutInflater inflater=null;
	public CustomAdapter(Context mainActivity, ArrayList<String> files2, ArrayList<String> imgs2, ArrayList<String> myPath) {
		// TODO Auto-generated constructor stub
		files=files2;
		context=mainActivity;
		MyPathh=myPath;
		imgs=imgs2;
		inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


		maxMemory= (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@SuppressLint("NewApi") @Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};


	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return files.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public class Holder
	{
		TextView tv;
		ImageView img;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 

	{
		// TODO Auto-generated method stub
		Holder holder=new Holder();
		View rowView;       
		rowView = inflater.inflate(R.layout.listview_layout, null);
		holder.tv=(TextView) rowView.findViewById(R.id.txt);
		holder.img=(ImageView) rowView.findViewById(R.id.flag);       
		holder.tv.setText(files.get(position));

		File file = new File(imgs.get(position));
		BitmapWorkerTask task = new BitmapWorkerTask(holder.img);
		task.execute(file.getAbsolutePath());
		rowView.setOnClickListener(new OnClickListener() {            
			@Override
			public void onClick(View v) 

			{
				File file = new File(MyPathh.get(position));
				ArrayList<String> Listfile = new ArrayList<String>();
				Listfile=getimgs(file,Listfile);
				//Toast.makeText(context, Listfile.get(0), Toast.LENGTH_LONG).show();
				Intent i =  new Intent(context,ComicView.class);
				i.putStringArrayListExtra("paths", Listfile);
				context.startActivity(i);
				//Start a swipe activity for images at location of mypath

			}
		});   
		return rowView;
	}


	public ArrayList<String> getimgs(File dir, ArrayList<String> fileList) 
	{
		//ArrayList<File> fileList= new ArrayList<File>();
		File listFile[] = dir.listFiles();
		if (listFile != null && listFile.length > 0) {
			for (int i = 0; i < listFile.length; i++) {

				if (listFile[i].isDirectory()) 
				{
					//  fileList.add(listFile[i]);
					getimgs(listFile[i],fileList);

				} else 
					if (listFile[i].getName().endsWith(".png")
							|| listFile[i].getName().endsWith(".jpg")
							|| listFile[i].getName().endsWith(".jpeg"))

					{
						fileList.add(listFile[i].getPath());
					}


			}
		}
		return fileList;
	}

	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private String data = null;

		public BitmapWorkerTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			data = params[0];
			if(getBitmapFromMemCache(data)!=null)
			{
				return getBitmapFromMemCache(data);
			}
			else
			{
				if(decodeSampledBitmapFromResource(data)!=null)
				{
				addBitmapToMemoryCache(data, decodeSampledBitmapFromResource(data));
				return decodeSampledBitmapFromResource(data);
				}
				else return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}
	public static Bitmap decodeSampledBitmapFromResource(String path) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize =2;// calculateInSampleSize(options, 100, 150);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path,options);
	}
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}
	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}
}
