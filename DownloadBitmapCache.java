package com.caliente.android.vod;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import com.caliente.vod.util.ImageCache;

public class DownloadBitmapCache extends AsyncTask<Video[], Void, Video[]>
{
	private Activity myContext;
	private ImageCache mImageCache;

	public DownloadBitmapCache(Activity _myContext, ImageCache imageCache)
	{
		myContext=_myContext;
		mImageCache=imageCache;
	}
	
	protected Bitmap loadBitmap(String nameBitmap, String urlBitmap) 
	{
		Bitmap bitmap = mImageCache.getBitmapFromDiskCache(nameBitmap);
	    if (bitmap != null)
	    {
	    	//System.out.println("loadBitmap:cache");
	    	return bitmap;
	    }
	    else 
	    {
	    	try 
	    	{
	    		System.out.println("loadBitmap:download");
				URL url = new URL(urlBitmap);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				
				InputStream input = connection.getInputStream();
				bitmap=BitmapFactory.decodeStream(input);
				
				mImageCache.addBitmapToCache(nameBitmap, new BitmapDrawable(myContext.getResources(), bitmap));
				
				return bitmap;
			} 
	    	catch (IOException e) 
	    	{
				e.printStackTrace();
				return null;
			}
	    }
	}
	
	@Override
	protected Video[] doInBackground(Video[]... _setVideo) 
	{
		Video[] setVideo = new Video[_setVideo[0].length];
		Video video;
		for (int i = 0; i < _setVideo[0].length; i++) 
		{
			video=_setVideo[0][i];
			video.setBitmap(loadBitmap(video.getIdName(), video.getPathImg()));
			setVideo[i]=video;
		}
		
		return setVideo;
	}
}