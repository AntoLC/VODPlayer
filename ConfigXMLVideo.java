package com.caliente.android.vod;

import java.util.concurrent.ExecutionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.caliente.vod.util.ImageCache;
import com.caliente.android.vod.R;

public class ConfigXMLVideo extends AsyncTask<String, Void, String>  
{
	//VARIABLE CONFIG XML
	//private String URL="http://pt.90d.mobi/app/xml_videos_by_voca.php";
	
	// VARIABLE DE CLASS
	private boolean connexion_internet=true;
	private int dureePaiement=60*60; //1HEURE EN SECONDE
	
	// LOAD AND SAVE IN CACHE IMG
	private DownloadBitmapCache downloadBitmapCache;
	
	private Activity mContext=null;
	private SharedPreferences preferences;
	
	private String numero, keyApp, motcle="";
	
	public ConfigXMLVideo(Activity _mContext, ImageCache imageCache, String ref,  String mc)
	{
		mContext=_mContext;
		preferences = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);
		String url=mContext.getString(R.string.urlXML);
		String url_first_install=mContext.getString(R.string.urlFirst);
		
		downloadBitmapCache=new DownloadBitmapCache(mContext, imageCache);
		
		// Gets the URL from the UI's text field.
        ConnectivityManager connMgr = (ConnectivityManager) _mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
        {
        	url+="?ref="+ref;
        	url_first_install+="?mc="+mc;
        	execute(url_first_install, url);
        }
        else
        	connexion_internet=false;
	}
	
	@Override
    protected String doInBackground(String... urls){
		if(preferences.getInt("FIRST_INSTALL", 0) == 0)
			new XMLParser().getXmlFromUrl(urls[0]);
		
		return new XMLParser().getXmlFromUrl(urls[1]);
    }
	
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String xml_config_video) 
    {
    	preferences.edit().putInt("FIRST_INSTALL", 1).commit();
    	
    	if(xml_config_video != null) 
			preferences.edit().putString("xml_config_video", xml_config_video).commit();
		else
			xml_config_video=preferences.getString("xml_config_video", null);
		
		if(xml_config_video != null) 
			xml_result_treatment(xml_config_video);
    }
    
    public String getNumero(){
    	return numero;
    }
    
    public String getMotcle(){
    	return motcle;
    }
    
    public String getKeyApp(){
    	return keyApp;
    }
    
    protected void xml_result_treatment(String xml_config_video) 
	{
		if (xml_config_video != null) 
		{
			XMLParser localXMLParser = new XMLParser();
			Document localDocument = localXMLParser.getDomElement(xml_config_video);
			if (localDocument != null) 
			{
				BDVod bdVod = new BDVod(mContext);
				bdVod.open();
		        
				// CONFIG SH/MC 
				NodeList localNodeListPayment = localDocument.getElementsByTagName("payment");
				if(bdVod.countCategories() != localNodeListPayment.getLength())
				{
					for (int i = 0; i < localNodeListPayment.getLength(); i++) 
					{
						Element localElement = (Element) localNodeListPayment.item(i);
						
						numero=localXMLParser.getStringValue(localElement, "sh");
						motcle=localXMLParser.getStringValue(localElement, "mc");
						keyApp=localXMLParser.getStringValue(localElement, "key");
					}
				}
				
				// INSERTION EN BD 
				NodeList localNodeListCategories = localDocument.getElementsByTagName("categories");
				if(bdVod.countCategories() != localNodeListCategories.getLength())
				{
					for (int i = 0; i < localNodeListCategories.getLength(); i++) 
					{
						Element localElement = (Element) localNodeListCategories.item(i);
						int tid=Integer.valueOf(localXMLParser.getStringValue(localElement, "tid"));
						String nameCategorie=localXMLParser.getStringValue(localElement, "name");
						
						bdVod.insertCategorie(tid, nameCategorie);
					}
				}
				
				NodeList localNodeListVideo = localDocument.getElementsByTagName("video");
		        Video[] setVideo = new Video[localNodeListVideo.getLength()];
		        int countVideo = bdVod.countVideo();
				for (int i = 0; i < localNodeListVideo.getLength(); i++) 
				{
					Element localElement = (Element) localNodeListVideo.item(i);
					String id_name=localXMLParser.getStringValue(localElement, "id_name");
					
					int timestamp_actuel=(int) (System.currentTimeMillis()/1000);
					int lastPaiement=preferences.getInt(id_name, 0);
					boolean paye=false;
					
					//System.out.println("lastPaiement:"+lastPaiement);
					//System.out.println("dureePaiement:"+(lastPaiement + dureePaiement));
					//System.out.println("timestamp_actuel:"+timestamp_actuel);
					
					if(lastPaiement != 0 && (lastPaiement + dureePaiement) < timestamp_actuel)
						paye=true;
					
					int nid = Integer.valueOf(localXMLParser.getStringValue(localElement, "nid"));
					
					if(countVideo != localNodeListVideo.getLength())
					{
						// INSERTION EN BD
						bdVod.insertVideo(nid);
						
						String listTid = localXMLParser.getStringValue(localElement, "list_tid");
						String[] tabTid = listTid.split(",");

						for (int j = 0; j < tabTid.length; j++) {
							bdVod.insertVideoCategorie(nid, Integer.valueOf(tabTid[j]));
						}
					}
					
					// CREATION OBJET VIDEO
					Video video = new Video(
						id_name,
						nid, 
						localXMLParser.getStringValue(localElement, "titre"),
						localXMLParser.getStringValue(localElement, "desc"),
						localXMLParser.getStringValue(localElement, "path_img"),
						localXMLParser.getStringValue(localElement, "path_mp4"),
						paye);
					
					setVideo[i]=video;
				}
				
				bdVod.close();
				
				downloadBitmapCache.execute(setVideo);
			}
		}
	}
    
    public boolean configXMLIsFinished()
    {
    	boolean isFinished=false;
    	
    	System.out.println("getStatus:"+downloadBitmapCache.getStatus());
    	
    	String statusdownloadBitmapCache = downloadBitmapCache.getStatus().toString();
    	if(statusdownloadBitmapCache.equals("FINISHED"))
    		isFinished=true;
    	
    	return isFinished;
    }
    
    public Video[] getSetVideo(){
		try {
			return downloadBitmapCache.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
    
	public boolean getConnexionInternet(){
		return connexion_internet;
	}
}