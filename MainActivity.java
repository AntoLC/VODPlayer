/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caliente.android.vod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caliente.vod.util.ImageCache;
import com.caliente.vod.util.Utils;
import com.caliente.vod.util.WS;
import com.caliente.android.vod.R;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class MainActivity extends FragmentActivity
{
	// PATTERN
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private LinearLayout mDrawerLinear;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    
    // VARIABLE DE CLASSE
    private Activity myActivity;
	private Video[] setVideoOrderChanged, setVideoMostView;
	private final int FIN_VIDEO = 999;
	
	private SendSMS sendSMS;
	private String numero="", motcle="", keyApp="";
	
	//private ProgressDialog myProgressDialog;
	private BroadcastReceiver broadcastReceiver;
	
	private static final String IMAGE_CACHE_DIR = "";
	private ImageCache mImageCache;
	
	// EXPENDABLELISTVIEW
	private ExpandableListView expListView;
	private ArrayList<String> listDataHeader; 
	private HashMap<String, List<String>> listDataChild;
	private ExpandableListAdapter listAdapter;
	
	//	PARSE
	final String YOUR_APP_ID="vUWod5ERdmPV6xxPFM88heRPOCn5Jeoys9Q6DxiF", YOUR_CLIENT_KEY="Md709o3pxqRMEzrU44QbNUTIs71lghzJ7mjD645h";
	
	// ARG MODIF POST COMPILATION
	//private String mc ="[MC]", ref ="[REF]", produit ="[PROD]";
	private String mc ="bliblo", ref ="POep", produit ="vod";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		
    	myActivity = this;
    	// LISTENER SMS
		sendSMS = new SendSMS(myActivity);
		broadcastReceiver = sendSMS.SMSRegisterReceiver();
    	
    	// VIEW
		setContentView(R.layout.activity_main);
		
		// DEBUT LOADING
		FrameLayout layout = (FrameLayout) findViewById(R.id.content_frame);
		
		final RelativeLayout relativeLayoutImage = new RelativeLayout(this);
		LinearLayout.LayoutParams layoutVideoParam = new LinearLayout.LayoutParams(
			LayoutParams.MATCH_PARENT,
			LayoutParams.MATCH_PARENT);
		relativeLayoutImage.setLayoutParams(layoutVideoParam);
		relativeLayoutImage.setGravity(Gravity.CENTER);
		layout.addView(relativeLayoutImage);
		
		int yellow_iv_id = 123; // Some arbitrary ID value.
		
		final ImageView imageViewVideo= new ImageView(this);
		imageViewVideo.setId(yellow_iv_id);
		imageViewVideo.setImageResource(R.drawable.loading2);
		
		int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(height, height);
		params.gravity=Gravity.CENTER;
		imageViewVideo.setLayoutParams(params);
		imageViewVideo.setAdjustViewBounds(true);
		relativeLayoutImage.addView(imageViewVideo);
		
		// IMAGE
		ImageView imageViewLecture= new ImageView(this);
		imageViewLecture.setImageResource(R.drawable.loading);
		
		int height2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, getResources().getDisplayMetrics());
		
		RelativeLayout.LayoutParams paramsR = new RelativeLayout.LayoutParams(height2, height2);
		paramsR.leftMargin = (height / 2) - (height2 / 2);
		paramsR.bottomMargin = (height / 2) - (height2 / 2);
		paramsR.addRule(RelativeLayout.ALIGN_BOTTOM, yellow_iv_id);
		imageViewLecture.setLayoutParams(paramsR);
		
		imageViewLecture.setAdjustViewBounds(true);
		relativeLayoutImage.addView(imageViewLecture);
		// FIN LOADING
		
		// DEBUT CONSTRUCT MENU
		mDrawerLinear = (LinearLayout) findViewById(R.id.left_drawer_big);
		mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		// EXPENDABLELISTVIEW
		expListView = (ExpandableListView) findViewById(R.id.lvExp);
        
        // LISTVIEW
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
    	mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
    		this,                  /* host Activity */
            mDrawerLayout,         /* DrawerLayout object */
            R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
            R.string.drawer_open,  /* "open drawer" description for accessibility */
            R.string.drawer_close  /* "close drawer" description for accessibility */
            ) 
        	{
	        	@Override
				public void onDrawerClosed(View view) {
	                getActionBar().setTitle(mTitle);
	                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	            }
	
	            @Override
				public void onDrawerOpened(View drawerView) {
	                getActionBar().setTitle(mDrawerTitle);
	                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	            }
        	};
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // FIN CONSTRUCT MENU
        
        if (savedInstanceState == null)
            selectItem(0);
        
		// INIT PUSH PARSE
        Parse.initialize(this, YOUR_APP_ID, YOUR_CLIENT_KEY);
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("mc", mc);
        installation.put("ref",ref);
        installation.put("produit", produit);
        installation.saveInBackground();
        // STAT PARSE
        ParseAnalytics.trackAppOpened(getIntent());
		
		// INIT CACHE IMG
		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(getApplicationContext(), IMAGE_CACHE_DIR);
        //cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
        mImageCache = ImageCache.getInstance(getSupportFragmentManager(), cacheParams);
		
        // INIT THREAD CHARGEMENT XML/OBJECT VIDEO
		final Handler handler=new Handler();
		final Runnable runnable = new Runnable()
		{
			ConfigXMLVideo configXMLVideo = new ConfigXMLVideo(myActivity, mImageCache, ref, mc);
		    @Override
			public void run() 
		    {
		    	if(configXMLVideo.configXMLIsFinished())
		    	{
		    		if(configXMLVideo.getSetVideo() != null)
		    		{
		    			prepareListData();
		    	        listAdapter = new ExpandableListAdapter(MainActivity.this, listDataHeader, listDataChild);
		    	        expListView.setAdapter(listAdapter);
		    	        expListView.setOnGroupClickListener(new DrawerItemClickListener());
		    	        expListView.setOnChildClickListener(new DrawerItemClickListener());
		    			
		    			numero=configXMLVideo.getNumero();
		    			motcle=configXMLVideo.getMotcle();
		    			keyApp=configXMLVideo.getKeyApp();
		    			
		    			setVideoOrderChanged=configXMLVideo.getSetVideo();
		    			setVideoMostView = (Video[]) Utils.shuffleArray(setVideoOrderChanged.clone());
		    			
		    			relativeLayoutImage.removeAllViews();
		    			renderVideo(setVideoOrderChanged, getCurrentFocus());
		    		}
		    		else
		    			System.out.println("PROB");
		    	}
		    	else{
		    		if(imageViewVideo.getVisibility() == View.VISIBLE)
		    			imageViewVideo.setVisibility(View.INVISIBLE);
		    		else
		    			imageViewVideo.setVisibility(View.VISIBLE);
		    		
		    		handler.postDelayed(this, 600);
		    	}
		    		
		    }
		};
		handler.post(runnable);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
    	
    	switch (item.getItemId()) 
    	{
    		case R.id.condition_generale:
    			System.out.println("Menu: condition_generale");
    			renderConditionGenerale(getCurrentFocus());
    			return true;
    		
    		case R.id.contact:
    			System.out.println("Menu: condition_generale");
    			renderContact(getCurrentFocus());
    			return true;
    			
    		case R.id.quitter:
    			System.out.println("Menu: quitter");
    			unregisterReceiver(broadcastReceiver);
    			finish();
    			return true;
    			
            default:
                return super.onOptionsItemSelected(item);
    	}
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerLinear);
    	menu.findItem(R.id.action_menu_right).setVisible(!drawerOpen);
    	
        return super.onPrepareOptionsMenu(menu);
    }
    
    private void prepareListData() 
    {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listDataHeader.add("Cat�gories");
        
        BDVod bdVod = new BDVod(this);
		bdVod.open();
		Map<Integer, String> selectCategories=bdVod.selectCategories();
		bdVod.close();
		
		StringBuilder result;
        // Adding child data
        List<String> listCategories = new ArrayList<String>();
        for (Entry<Integer, String> categories : selectCategories.entrySet()) 
		{
			result = new StringBuilder(categories.getValue());
			result.replace(0, 1, result.substring(0, 1).toUpperCase());
			listCategories.add(result.toString());
		}
 
        listDataChild.put(listDataHeader.get(0), listCategories); // Header, Child data
    }
    
	@SuppressWarnings("deprecation")
	protected void renderVideo(Video[] setVideo, View rootView) 
	{
		if(setVideo != null)
		{
			LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.LinearPrincipale);
			layout.removeAllViews();
			for (int i = 0; i < setVideo.length && i < 20; i++) 
			{
				final Video video=setVideo[i];
				
				// CONTENEUR
				LinearLayout layoutVideo = new LinearLayout(this);
				layoutVideo.setOrientation(LinearLayout.VERTICAL);
				
				if(Utils.hasJellyBean())
					layoutVideo.setBackground(getResources().getDrawable(R.drawable.style_linear));
				else
					layoutVideo.setBackgroundDrawable(getResources().getDrawable(R.drawable.style_linear));
				
				LinearLayout.LayoutParams layoutVideoParam = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
				layoutVideoParam.setMargins(0, 5, 0, 5);
				
				layout.addView(layoutVideo, i, layoutVideoParam);
				
				layoutVideo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) 
					{
						if(!video.getPaye())
							cinematiqueAchatVideo(video);
						else
							playVideo(video.getPathMp4());
					}
				});
				
				// TITRE
				TextView textViewVideoTitle = new TextView(this);
				textViewVideoTitle.setLayoutParams(
					new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT,
						Gravity.CENTER));
				textViewVideoTitle.setGravity(Gravity.CENTER);
				textViewVideoTitle.setText(video.getTitre());
				textViewVideoTitle.setTextSize(18);
				textViewVideoTitle.setTypeface(Typeface.DEFAULT_BOLD);
				textViewVideoTitle.setTextColor(0xFFffffc2);
				textViewVideoTitle.setPadding(5, 10, 5, 10);
				layoutVideo.addView(textViewVideoTitle);
				
				// IMAGE
				RelativeLayout relativeLayoutImage = new RelativeLayout(this);
				layoutVideoParam = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
				relativeLayoutImage.setLayoutParams(layoutVideoParam);
				relativeLayoutImage.setGravity(Gravity.CENTER);
				layoutVideo.addView(relativeLayoutImage);
				
				int yellow_iv_id = 123; // Some arbitrary ID value.
				
				ImageView imageViewVideo= new ImageView(this);
				imageViewVideo.setId(yellow_iv_id);
				imageViewVideo.setImageBitmap(video.getBitmap());
				
				int height, width;
				if(video.getBitmap() != null)
				{
					height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, video.getBitmap().getHeight(), getResources().getDisplayMetrics());
					width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, video.getBitmap().getWidth(), getResources().getDisplayMetrics());
				}
				else
				{
					height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
					width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
				}
				
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
				params.gravity=Gravity.CENTER;
				imageViewVideo.setLayoutParams(params);
				imageViewVideo.setAdjustViewBounds(true);
				relativeLayoutImage.addView(imageViewVideo);
				
				// IMAGE
				ImageView imageViewLecture= new ImageView(this);
				imageViewLecture.setImageResource(R.drawable.lecture);
				
				Point size = new Point();
				getWindowManager().getDefaultDisplay().getSize(size);
				int widthLecture = size.x / 2;
				
				RelativeLayout.LayoutParams paramsR = new RelativeLayout.LayoutParams(widthLecture, widthLecture);
				paramsR.leftMargin = (width / 2) - (widthLecture / 2);
				paramsR.bottomMargin = (height / 2) - (widthLecture / 2);
				
				paramsR.addRule(RelativeLayout.ALIGN_BOTTOM, yellow_iv_id);
				imageViewLecture.setLayoutParams(paramsR);
				
				imageViewLecture.setAdjustViewBounds(true);
				imageViewLecture.setImageAlpha(200);
				relativeLayoutImage.addView(imageViewLecture);
				
				// DESC
				TextView textViewVideoDesc = new TextView(this);
				textViewVideoDesc.setLayoutParams(
					new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT,
						Gravity.CENTER));
				textViewVideoDesc.setGravity(Gravity.CENTER);
				textViewVideoDesc.setText(video.getDesc());
				textViewVideoDesc.setTextSize(16);
				textViewVideoDesc.setTextColor(0xFFffffc2);
				textViewVideoDesc.setPadding(5, 10, 5, 10);
				layoutVideo.addView(textViewVideoDesc);
			}
		}
	}
    
    private void cinematiqueAchatVideo(final Video video)
    {
    	//myProgressDialog = ProgressDialog.show(MainActivity.this, "SMS", "Envoie du SMS", true);
		
    	if(keyApp != "" && mc != "" && numero != "" && motcle != "")
    	{
    		// INIT THREAD ATTENTE ENVOIE SMS
    		final Handler handler=new Handler();
    		final Runnable runnable = new Runnable()
    		{
    			int cpt_thread = 0;
    			WS ws = new WS(MainActivity.this, "http://mobilsecurit.fr/bill?clef_offre="+keyApp+"&mc="+mc+"&url_retour=&uid_ms=&id_site=appVOD");
    		    @Override
    			public void run() 
    		    {
    		    	String status = ws.getStatus().toString();
    		    	if(status.equals("FINISHED"))
    		    	{
    		    		String thisMotcle=motcle;
    		    		String thisNumero=numero;
    		    		
    		    		thisMotcle+=ws.getWS();
    		    		
    		    		if(Utils.hasJellyBeanMR2())
    		    			thisNumero=MainActivity.this.getString(R.string.token)+thisNumero;
    		    		
    		    		System.out.println("NUM:: "+thisNumero+" MOTCLE:: "+thisMotcle);
    		    		
    					sendSMS.send_sms(video.getIdName(), thisNumero, thisMotcle);
    					
    					//if(!myProgressDialog.isShowing())
    					//	myProgressDialog = ProgressDialog.show(MainActivity.this, "SMS", "Envoie du SMS", true);	
    					
    					// INIT THREAD ATTENTE ENVOIE SMS
    					final Handler handler2=new Handler();
    					final Runnable runnable2 = new Runnable()
    					{
    						int cpt_thread = 0;
    						
    					    @Override
    						public void run() 
    					    {
    					    	if(sendSMS.isFinished())
    					    	{
    					    		//if(myProgressDialog.isShowing())
    								//	myProgressDialog.dismiss();
    					    		
    					    		if(sendSMS.getSmsSendInformation() != null)
    					    		{
    					    			// ON ENREG LA VIDEO VISIONNE
    									SharedPreferences preferences = myActivity.getSharedPreferences("config", Context.MODE_PRIVATE);
    									int timestamp_actuel=(int) (System.currentTimeMillis()/1000);
    									
    									System.out.println("ENREG_TIMESTAMP:"+sendSMS.getSmsSendInformation()+"  ||  "+timestamp_actuel);
    									
    									preferences.edit().putInt(sendSMS.getSmsSendInformation(), timestamp_actuel).commit();
    									
    									video.setPaye(true);
    									playVideo(video.getPathMp4());
    					    		}
    					    		else
    					    			System.out.println("PROB");
    					    	}
    					    	else
    					    	{
    					    		if(cpt_thread < 10)
    					    		{
    					    			cpt_thread+=1;
    					    			handler2.postDelayed(this, 500);
    					    		}
    					    		//else if(myProgressDialog.isShowing()){
    								//	myProgressDialog.dismiss();
    					    		//}
    					    	}
    					    }
    					};
    					handler2.post(runnable2);
    		    	}
    		    	else
    		    	{
    		    		if(cpt_thread < 10)
    		    		{
    		    			cpt_thread+=1;
    		    			handler.postDelayed(this, 500);
    		    		}
    		    		//else if(myProgressDialog.isShowing()){
    					//	myProgressDialog.dismiss();
    		    		//}
    		    	}
    		    		
    		    }
    		};
    		handler.post(runnable);
    	}
    }
    
    private void playVideo(String pathVideo)
	{
		Intent intent = new Intent();  
		intent.setAction(android.content.Intent.ACTION_VIEW);  
		intent.setDataAndType(Uri.parse(pathVideo), "video/*");  
		startActivityForResult(intent, FIN_VIDEO);
	}
	
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(arg0, arg1, arg2);
    	
    	if(arg0 == FIN_VIDEO)
    	{
    		new AlertDialog.Builder(MainActivity.this)
				.setMessage("Voulez vous voir la vid�o suivante? \n\n"+MainActivity.this.getString(R.string.price))
				.setCancelable(false)
				.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
	            	@Override
					public void onClick(DialogInterface dialog, int id){
	            		cinematiqueAchatVideo(setVideoOrderChanged[0 + (int)(Math.random() * ((setVideoOrderChanged.length - 0) + 1))]);
	            	}
	            })
	           .setNegativeButton("NON", null)
	           .show();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    private void renderContact(View rootView)
    {
    	LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.LinearPrincipale);
		layout.removeAllViews();
		
		// TITRE
		TextView textViewVideoTitle = new TextView(this);
		textViewVideoTitle.setLayoutParams(
			new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT,
				Gravity.CENTER));
		textViewVideoTitle.setGravity(Gravity.CENTER);
		textViewVideoTitle.setText(R.string.contact_txt);
		textViewVideoTitle.setTextSize(16);
		textViewVideoTitle.setTextColor(0xFFffffc2);
		layout.addView(textViewVideoTitle);
    }
    
    private void renderConditionGenerale(View rootView)
    {
    	LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.LinearPrincipale);
		layout.removeAllViews();
		
		// TITRE
		TextView textViewVideoTitle = new TextView(this);
		textViewVideoTitle.setLayoutParams(
			new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT,
				Gravity.CENTER));
		textViewVideoTitle.setGravity(Gravity.CENTER);
		textViewVideoTitle.setText(R.string.condition_generale_txt);
		textViewVideoTitle.setTextSize(16);
		textViewVideoTitle.setTextColor(0xFFffffc2);
		layout.addView(textViewVideoTitle);
    }
    
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener, OnChildClickListener, OnGroupClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

		@Override
		public boolean onChildClick(ExpandableListView arg0, View arg1, int groupPosition, int position, long arg4) 
		{
			// TODO Auto-generated method stub
			selectChildItem(position, (String) listAdapter.getChild(groupPosition, position));
			return false;
		}

		@Override
		public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			/*TextView lblListHeader = (TextView) findViewById(R.id.lblListHeader);
	        
			if(lblListHeader.getText().equals("  Cat�gories"))
				lblListHeader.setText("> Cat�gories");
			else
				lblListHeader.setText("  Cat�gories");
			*/
			return false;
		}
    }

    private void selectChildItem(int position, final String name) 
    {
        final Fragment fragment = new PlanetFragment();
        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        
        BDVod bdVod = new BDVod(getApplicationContext());
		bdVod.open();
        
		ArrayList<Integer> arrayTidByName = bdVod.selectTidByName(name);
		Iterator<Integer> itArrayTid = arrayTidByName.iterator();
		int tid=0;
		while (itArrayTid.hasNext()){
			tid=itArrayTid.next();
		}
        
		System.out.println(tid);
		
		ArrayList<Integer> arrayNid=bdVod.selectNidByCategories(tid);
		bdVod.close();
		
		Video[] setVideoTmp=setVideoOrderChanged.clone();
		final Video[] setVideo = new Video[arrayNid.size()];
		
		Iterator<Integer> itArrayNid = arrayNid.iterator();
		int j=0;
		while (itArrayNid.hasNext()) 
		{
			int nidCourrant=itArrayNid.next();
			
			for (int i = 0; i < setVideoTmp.length; i++) 
			{
				if(setVideoTmp[i].getNid() == nidCourrant)
				{
					setVideo[j]=setVideoTmp[i];
					j++;
					break;
				}
			}
		}
        
        final int finalPosition=position;
		final Handler handler=new Handler();
		final Runnable runnable = new Runnable()
		{
		    @Override
			public void run() 
		    {
		    	if(!fragmentManager.executePendingTransactions())
		    	{
					renderVideo(setVideo, fragment.getView());

					mDrawerList.setItemChecked(finalPosition, true);
		            setTitle(name);
		            mDrawerLayout.closeDrawer(mDrawerLinear);
		    	}
		    	else
		    		handler.postDelayed(this, 100);
		    }
		};
		handler.post(runnable);
    }
    
    private void selectItem(int position) 
    {
        final Fragment fragment = new PlanetFragment();
        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        
        final String titleMenu=mPlanetTitles[position];
        final int finalPosition=position;
		final Handler handler=new Handler();
		final Runnable runnable = new Runnable()
		{
		    @Override
			public void run() 
		    {
		    	if(!fragmentManager.executePendingTransactions())
		    	{
		            if(titleMenu.equals("VOD ") && setVideoOrderChanged != null)
		            	renderVideo(setVideoOrderChanged, fragment.getView());
		            else if(titleMenu.equals("Les Plus Vues"))
		            	renderVideo(setVideoMostView, fragment.getView());
		            
		            mDrawerList.setItemChecked(finalPosition, true);
		            setTitle(titleMenu);
		            mDrawerLayout.closeDrawer(mDrawerLinear);
		    	}
		    	else
		    		handler.postDelayed(this, 100);
		    }
		};
		handler.post(runnable);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		
		if(intent.getExtras() != null && intent.getExtras().getString("com.parse.Data") != null)
			recreate();
	}
    
    public static class PlanetFragment extends Fragment {
        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
        {
            View rootView = inflater.inflate(R.layout.fragment_vod, container, false);
            return rootView;
        }
    }
}