package com.comic.drawer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;
import com.google.gson.Gson;

public class CbrFragment extends Fragment {



	private ProgressBar pb;
	private File root;
	private ArrayList<File> files = new ArrayList<File>();
	private File directory1 = new File(Environment.getExternalStorageDirectory()+File.separator+"ComicDrawer/zip");
	private File directory2 = new File(Environment.getExternalStorageDirectory()+File.separator+"ComicDrawer/cbr");

	File file;
	ListView l;


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
		Gson gson = new Gson();
		String json = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("MyFiles1", "");
		ArrayList<String> MyFiles = new ArrayList<String>();
		MyFiles=gson.fromJson(json, ArrayList.class);
		String json1 = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("MyImages1", "");
		ArrayList<String> MyImages = new ArrayList<String>();
		MyImages=gson.fromJson(json1,  ArrayList.class);
		String json2 = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("MyPath1", "");
		ArrayList<String> MyPath = new ArrayList<String>();
		MyPath=gson.fromJson(json2,  ArrayList.class);
		if(MyFiles==null)
		{
			refresh();
			//Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_LONG).show();
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
				else refresh();
			}
			else
			{
				l.setAdapter(new CustomAdapter(getActivity(),MyFiles,MyImages,MyPath));
				pb.setVisibility(View.INVISIBLE);
			}
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

				if (listFile[i].isDirectory()&&!(listFile[i].getName().endsWith(".cbr")||listFile[i].getName().endsWith(".rar"))) {
					//fileList.add(listFile[i]);
					getfile(listFile[i],fileList);

				} else {
					if (listFile[i].getName().endsWith(".cbr")||listFile[i].getName().endsWith(".rar"))
					{
						fileList.add(listFile[i]);
						file = new File(Environment.getExternalStorageDirectory() + "/ComicDrawer/cbr/"+listFile[i].getName()+"/");
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
			//ArrayList<File> def = new ArrayList<File>();
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
				Toast.makeText(getActivity(), "No cbr/rar files found", Toast.LENGTH_LONG).show();
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
			//	edit.putStringSet("MyFiles1", new HashSet<String>(MyFiles));
			//edit.putStringSet("MyImages1", new HashSet<String>(MyImages));
			//edit.putStringSet("MyPath1", new HashSet<String>(MyPath));
			Gson gson = new Gson();
			String json = gson.toJson(MyImages);
			edit.putString("MyImages1", json);
			String json1 = gson.toJson(MyFiles);
			edit.putString("MyFiles1", json1);
			String json2 = gson.toJson(MyPath);
			edit.putString("MyPath1", json2);
			edit.commit(); 


			return dir[0];
		}

		protected void onPreExecute() 
		{

		}

		protected void onPostExecute(ArrayList<File> dir) 
		{




			l.setAdapter(new CustomAdapter(getActivity(),MyFiles,MyImages,MyPath));
			pb.setVisibility(View.INVISIBLE);
			// l.setAdapter(new ArrayAdapter<String>(getActivity(),
			//      android.R.layout.simple_list_item_1, MyFiles));


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
	public static void unzip(String rarFile, String location) throws IOException 
	{File f = new File(rarFile);
	Archive a = null;
	try {
		a = new Archive(new FileVolumeManager(f));
	} catch (RarException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	if (a != null) {
		a.getMainHeader().print();
		FileHeader fh = a.nextFileHeader();
		while (fh != null) {
			try {
				File out = new File(location+"/"+fh.getFileNameString().trim());
				System.out.println(out.getAbsolutePath());
				FileOutputStream os = new FileOutputStream(out);
				a.extractFile(fh, os);
				os.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fh = a.nextFileHeader();
		}
	}}


}
