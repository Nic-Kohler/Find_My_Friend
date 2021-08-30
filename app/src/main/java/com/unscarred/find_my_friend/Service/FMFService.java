package com.unscarred.find_my_friend.Service;

import com.unscarred.find_my_friend.UI.UICore;
import com.unscarred.find_my_friend.UI.FacebookHandler;
import com.unscarred.find_my_friend.Common.FileHandler;
import com.unscarred.find_my_friend.Common.ServerHandler;

import com.facebook.FacebookSdk;

import android.app.NotificationManager;
import android.content.Context;
import android.os.*;
import android.os.IBinder;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class FMFService extends Service
{
	public  ServiceCore Service_Core;
	private Binder      Service_Binder;
	public  enum        PendingAction{ NONE, POST_STATUS_UPDATE }

	//public class LocalBinder extends Binder{ FMFService Get_Service(){ return FMFService.this; } }

	@Override public void onCreate()
	{
		super.onCreate();

		Service_Binder = new Binder();
	}

	@Override public IBinder onBind(Intent intent){ return Service_Binder; }

	@Override public int onStartCommand(Intent intent, int flags, int startId)
	{
		FacebookSdk.sdkInitialize(this);

		Service_Core = new ServiceCore(this);

		return Service.START_STICKY;
	}

	public class Binder extends android.os.Binder
	{
		public FMFService Get_Service(){ return FMFService.this; }
	}

	public class ServiceCore
	{
		public  Context                 Base_Context;
		public  UICore                  UI_Core;
		public  boolean                 UI_Is_Present = false;
		public  String                  SERVER_URL = "http://find-my-friend.unscarredtechnology.co.za/v1";
		public  NotificationManager     Notification_Manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

		public  ServerHandler           Server_Handler;
		public  LocationServiceHandler  Location_Service_Handler;
		public  ServerResponseFunctions Server_Response_Functions;
		public  FacebookHandler         Facebook_Handler;
		public  DataHandler             Data_Handler;
		public  FileHandler             File_Handler;

		public  double                  User_Latitude = 666;
		public  double                  User_Longitude = 666;
		public  double                  User_Altitude = 666;
		public  String                  User_Address;
		public  String                  User_City;

		public  final int               DATA_UPDATE_INTERVAL = 10000;


		public ServiceCore(Context context)
		{
			Base_Context = context;

			File_Handler = new FileHandler(FMFService.this);
			File_Handler.Read_FMF_Settings();

			Server_Handler = new ServerHandler();
			Server_Response_Functions = new ServerResponseFunctions();
			Location_Service_Handler = new LocationServiceHandler(FMFService.this);
			Facebook_Handler = new FacebookHandler(FMFService.this);
			Data_Handler = new DataHandler(FMFService.this);
		}

		public void Set_UI_Core(UICore core){ UI_Core = core; }

		public void Finalize_Initialization()
		{
			Log.d("Nic Says", "Service_Core.Location_Service_Handler.First_Run_Complete: " + Service_Core.Location_Service_Handler.First_Run_Complete);
			Log.d("Nic Says", "Service_Core.Data_Handler.First_Run_Complete:             " + Service_Core.Data_Handler.First_Run_Complete);

			if(Data_Handler.First_Run_Complete)
			{
				Bundle bundle = new Bundle();
				Message message = new Message();

				bundle.putInt("action", 0);
				message.setData(bundle);

				UI_Core.Service_Handler.sendMessage(message);
			}
		}

		public Handler Server_Response_Handler = new Handler()
		{
			@Override public void handleMessage(Message message)
			{
				try
				{
					Log.d("*** Nic Says ***", "Response From Server: " + message.getData().toString());

					if(message.getData().getBoolean("network_error"))
					{
						if(UI_Is_Present)
						{
							AlphaAnimation Alpha_Animation = new AlphaAnimation(1.0f, 0.0f);
							Alpha_Animation.setDuration(5000);
							Alpha_Animation.setFillAfter(true);

							UI_Core.Network_Error_Icon.startAnimation(Alpha_Animation);
						}
					}
					else if(!message.getData().getString("json").equals(""))
					{
						Service_Core.Server_Response_Functions.Set_jsonReceive(new JSONObject(message.getData().getString("json")));

						Log.d("Nic Says", "Service_Core.Server_Response_Functions.Get_Action(): " + Service_Core.Server_Response_Functions.Get_Action());

						switch(Service_Core.Server_Response_Functions.Get_Action())
						{
							case 0:   Service_Core.Server_Response_Functions.Test();            break;
							case 100: Service_Core.Server_Response_Functions.Add_Location();    break;
							case 101: Service_Core.Server_Response_Functions.Update_Location(); break;
							case 200: Service_Core.Server_Response_Functions.Get_Data();        break;
							case 201: Service_Core.Server_Response_Functions.Get_Friend_Data(); break;

							default: Log.d("Nic Says", "ERROR: Unknown response from server."); break;
						}
					}
				}
				catch(JSONException e)
				{
					System.err.println("Error reading Response in Handler: Server_Response_Handler:");
					System.err.println("Caught JSON Exception: " + e.getMessage());
				}
			}
		};
	}
}