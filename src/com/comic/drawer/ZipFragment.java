package com.comic.drawer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

public class ZipFragment extends Fragment {




	private File root;
	private ArrayList<File> files = new ArrayList<File>();
	File file;
	ListView l;
	private ProgressBar pb;
	private File directory1 = new File(Environment.getExternalStorageDirectory()+File.separator+"ComicDrawer/zip");
	private File directory2 = new File(Environment.getExternalStorageDirectory()+File.separator+"ComicDrawer/cbr");
	
	


	public static final String ARG_OBJECT = "object";

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) 
	{

		View rootView = inflater.inflate(R.layout.activity_zipfragment, container, false);
		Bundle args = getArguments();		

		Toast.makeText(getActivity(), "Comics are loading", Toast.LENGTH_LONG).show();
		pb=(ProgressBar)rootView.findViewById(R.id.progressBar1);
		l=(ListView)rootView.findViewById(R.id.listView1);
		root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		
		pb.setVisibility(View.VISIBLE);
		//ArrayList<String> MyFiles = new ArrayList<String>(PreferenceManager.getDefaultSharedPreferences(getActivity()).getStringSet("MyFiles", new LinkedHashSet<String>()));
	//	ArrayList<String> MyImages= new ArrayList<String>(PreferenceManager.getDefaultSharedPreferences(getActivity()).getStringSet("MyImages", new LinkedHashSet<String>()));
		//ArrayList<String> MyPath = new ArrayList<String>(PreferenceManager.getDefaultSharedPreferences(getActivity()).getStringSet("MyPath", new LinkedHashSet<String>()));
		
		
		 Gson gson = new Gson();
		 String json = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("MyFiles", "");
		 ArrayList<String> MyFiles = new ArrayList<String>();
		 MyFiles=gson.fromJson(json, ArrayList.class);
		 String json1 = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("MyImages", "");
		 ArrayList<String> MyImages = new ArrayList<String>();
		 MyImages=gson.fromJson(json1,  ArrayList.class);
		 String json2 = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("MyPath", "");
		 ArrayList<String> MyPath = new ArrayList<String>();
		 MyPath=gson.fromJson(json2,  ArrayList.class);
		//File f = new File(MyPath.get(0));
		 try{
		if(MyFiles==null)
		{
			
			refresh();
			
		}
		else
		{
			if(MyPath!=null)
			{
				if(!MyPath.contains(null))
				{
					File f = new File(MyPath.get(0));
					if(!f.exists())			
					{
						refresh();
					}
					else
					{
						l.setAdapter(new CustomAdapter(getActivity(),MyFiles,MyImages,MyPath));
						pb.setVisibility(View.INVISIBLE);
					}
				}
				else
				refresh();	
			}
			else
			{
			l.setAdapter(new CustomAdapter(getActivity(),MyFiles,MyImages,MyPath));
			pb.setVisibility(View.INVISIBLE);
			}
		}
		 }
		 catch(Exception e)
		 {
			 refresh();
		 }
		
		return rootView;
	}

	public void refresh()

	{
		directory1.delete();
		directory2.delete();
		directory1.mkdirs();
		directory2.mkdirs();
		new DownloadFilesTask().execute(root);
	}
	public ArrayList<File> getfile(File dir,ArrayList<File> fileList) {
		//	fileList = new ArrayList<File>();
		File listFile[] = dir.listFiles();
		if (listFile != null && listFile.length > 0) {
			for (int i = 0; i < listFile.length; i++) {

				if (listFile[i].isDirectory()&&!(listFile[i].getName().endsWith(".zip")||listFile[i].getName().endsWith(".cbz")||listFile[i].getName().endsWith(".war")
						||listFile[i].getName().endsWith(".jar")||listFile[i].getName().endsWith(".tar"))) {
					//fileList.add(listFile[i]);
					getfile(listFile[i],fileList);

				} else {
					if (listFile[i].getName().endsWith(".zip")||listFile[i].getName().endsWith(".cbz")||listFile[i].getName().endsWith(".war")
							||listFile[i].getName().endsWith(".jar")||listFile[i].getName().endsWith(".tar"))
					{
						fileList.add(listFile[i]);
						file = new File(Environment.getExternalStorageDirectory() + "/ComicDrawer/zip/"+listFile[i].getName()+"/");
						files.add(file); //Store location
						if(!file.exists())
						{
							file.mkdir();
							try {
								unzip(listFile[i].getPath()+"/",String.valueOf(file+"/"));

							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

			}
		}
		return fileList;
	}
	private class DownloadFilesTask extends AsyncTask<File, Void, ArrayList<File>> {

		protected ArrayList<File> doInBackground(File... dir) 
		{
			ArrayList<File> abc = new ArrayList<File>();
		//	ArrayList<String> retrieved = new ArrayList<String>(PreferenceManager.getDefaultSharedPreferences(getActivity()).getStringSet("SAVEDATA", new HashSet<String>()));
			
			abc=getfile(dir[0],abc);
			
			return abc;
		}

		protected void onPreExecute() 
		{
			pb.setVisibility(View.VISIBLE);
		}

		protected void onPostExecute(ArrayList<File> def) 
		{
			if(def.isEmpty())
			{
				Toast.makeText(getActivity(), "No cbz/zip/war/tar/jar files found", Toast.LENGTH_LONG).show();
				pb.setVisibility(View.INVISIBLE);
			}
			else
			{
				new ListFiles().execute(files);
			}
		}
	}

	private class ListFiles extends AsyncTask<ArrayList<File>, Void, ArrayList<File>> {

		ArrayList<String> MyFiles = new ArrayList<String>();  //Store the folder name
		ArrayList<String> MyImages = new ArrayList<String>(); //Store first image of each folder
		ArrayList<String> MyPath = new ArrayList<String>();
		ArrayList<File> listFile=new ArrayList<File>(); // Store list of files in the folder
		protected ArrayList<File> doInBackground(ArrayList<File>... dir) 
		{
			//dir has all current file paths
			for(int i=0;i<dir[0].size();i++) //Parse each folder
			{
				listFile=getimgs(dir[0].get(i),listFile); //Peek into each folder to get all paths

				if(listFile.size()!=0) // If we got any jpg or jpeg
				{	
					//Drawable d = Drawable.createFromPath(listFile.get(0).getPath()); //Convert path of first image to Drawable
					MyImages.add(listFile.get(0).getPath()); //Add to the ArrayList
					MyFiles.add(String.valueOf(dir[0].get(i).getName())); //Add name to the ArrayList
					MyPath.add(String.valueOf(dir[0].get(i).getPath()));
					listFile=new ArrayList<File>();
				}
			}
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			SharedPreferences.Editor edit = prefs.edit();
			/* Set<String> set = new LinkedHashSet();
			 Set<String> set1 = new LinkedHashSet();
			 Set<String> set2 = new LinkedHashSet();
		        for(String name : MyImages){
		            set.add(name);
		            
		        }
		        for(String name : MyFiles){
		            set1.add(name);
		            
		        }
		        for(String name : MyPath){
		            set2.add(name);
		            
		        }
		        edit.putStringSet("MyFiles", set1).commit();
		        edit.putStringSet("MyImages", set).commit();
		        edit.putStringSet("MyPath", set2).commit();*/
			Gson gson = new Gson();
			  String json = gson.toJson(MyImages);
			  edit.putString("MyImages", json);
			  String json1 = gson.toJson(MyFiles);
			  edit.putString("MyFiles", json1);
			  String json2 = gson.toJson(MyPath);
			  edit.putString("MyPath", json2);
			  edit.commit(); 
			
			return dir[0];
		}

		protected void onPreExecute() 
		{

		}

		protected void onPostExecute(ArrayList<File> dir) 
		{
			l.setAdapter(new CustomAdapter(getActivity(),MyFiles,MyImages,MyPath));
			// l.setAdapter(new ArrayAdapter<String>(getActivity(),
			//      android.R.layout.simple_list_item_1, MyFiles));
			
			pb.setVisibility(View.INVISIBLE);

		}
	}
	public ArrayList<File> getimgs(File dir, ArrayList<File> fileList) 
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
						fileList.add(listFile[i]);
					}


			}
		}
		return fileList;

	}
	public static void unzip(String zipFile, String location) throws IOException {
		int size;
		byte[] buffer = new byte[4096];

		try {
			if ( !location.endsWith("/") ) {
				location += "/";
			}
			File f = new File(location);
			if(!f.isDirectory()) {
				f.mkdirs();
			}
			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), 4096));
			try {
				ZipEntry ze = null;
				while ((ze = zin.getNextEntry()) != null) {
					String path = location + ze.getName();
					File unzipFile = new File(path);

					if (ze.isDirectory()) {
						if(!unzipFile.isDirectory()) {
							unzipFile.mkdirs();
						}
					} else {
						// check for and create parent directories if they don't exist
						File parentDir = unzipFile.getParentFile();
						if ( null != parentDir ) {
							if ( !parentDir.isDirectory() ) {
								parentDir.mkdirs();
							}
						}

						// unzip the file
						FileOutputStream out = new FileOutputStream(unzipFile, false);
						BufferedOutputStream fout = new BufferedOutputStream(out, 4096);
						try {
							while ( (size = zin.read(buffer, 0, 4096)) != -1 ) {
								fout.write(buffer, 0, size);
							}

							zin.closeEntry();
						}
						finally {
							fout.flush();
							fout.close();
						}
					}
				}
			}
			finally {
				zin.close();
			}
		}
		catch (Exception e) {
			Log.e("blah", "Unzip exception", e);
		}
	}


}
