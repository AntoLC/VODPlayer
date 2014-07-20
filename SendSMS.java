package com.caliente.android.vod;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class SendSMS {
	
	private Context myContext;
	private boolean isFinished=false;
	private static final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	private boolean sms_thread_send_en_cours = true;
	private String smsSendInformation = null;
	
	public SendSMS(Context _myContext)
	{
		myContext=_myContext;
	}
	
	public BroadcastReceiver SMSRegisterReceiver()
	{
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				// TODO Auto-generated method stub
				switch (getResultCode()) 
				{
					case Activity.RESULT_OK:
						System.out.println("SMSRegisterReceiver: OK");
						// ON STOP LE THREAD DATTENTE DE RECEPTION SMS
						sms_thread_send_en_cours=false;
						smsSendInformation=intent.getStringExtra("sms_key");
					break;
				}
			}
		};
		
		myContext.registerReceiver(broadcastReceiver, new IntentFilter(SENT_SMS_ACTION));
		
		return broadcastReceiver;
	}
	
	public void send_sms(final String smsInformation, final String numero, final String message)
	{
		// TODO Auto-generated method stub
		TelephonyManager telephonyManager=(TelephonyManager) myContext.getSystemService(Context.TELEPHONY_SERVICE);
		int simState = telephonyManager.getSimState();
		
		switch (simState) 
        {
        	case TelephonyManager.SIM_STATE_READY:
        		System.out.println("SEND_SMS");
        		//ENVOIE SMS
        		SmsManager smsManager=SmsManager.getDefault();
				sms_thread_send_en_cours=true;
        		
				Intent sentIntent= new Intent(SENT_SMS_ACTION);
        		sentIntent.putExtra("sms_key", smsInformation);
	        	PendingIntent sentPi = PendingIntent.getBroadcast(myContext.getApplicationContext(), 0, sentIntent, PendingIntent.FLAG_ONE_SHOT);
	        	smsManager.sendTextMessage(numero, null, message, sentPi, null);
        		
        		// INIT THREAD ATTENTE RECEPTION SMS
        		final Handler handler=new Handler();
        		final Runnable runnable = new Runnable()
        		{
        			int cpteur = 0;
        		    @Override
					public void run() 
        		    {
        		    	// TODO Auto-generated method stub
			        	if(sms_thread_send_en_cours && cpteur < 15)
			        	{
			        		cpteur++;
			        		handler.postDelayed(this, 1000);
			        	}
			        	else
			        		isFinished=true;
        		    }
        		};
        		handler.post(runnable);
	        break;
	        
	        default:
	        	Toast.makeText(myContext.getApplicationContext(), "SMS NON ENVOY�", Toast.LENGTH_SHORT).show();
	        break;
        }
	}
	
	public boolean isFinished(){
		return isFinished;
	}
	
	public String getSmsSendInformation(){
		return smsSendInformation;
	}
}