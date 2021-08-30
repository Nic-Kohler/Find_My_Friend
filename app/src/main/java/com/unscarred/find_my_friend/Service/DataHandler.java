package com.unscarred.find_my_friend.Service;

import com.unscarred.find_my_friend.CoreActivity;
import com.unscarred.find_my_friend.R;
import com.unscarred.find_my_friend.Service.FMFService.ServiceCore;
import com.unscarred.find_my_friend.UI.PicHandler;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DataHandler
{
	public  Context             Base_Context;
	public  ServiceCore         Service_Core;

	public FriendsHandler       Friends_Handler;
	public NotificationsHandler Notifications_Handler;
	public PicHandler           Pic_Handler;

	public  JSONArray           Friends = new JSONArray();
	public  JSONArray           Notifications = new JSONArray();

	public  boolean             First_Run_Complete = false;


	public DataHandler(Context context)
	{
		Base_Context = context;

		Friends_Handler       = new FriendsHandler(this);
		Notifications_Handler = new NotificationsHandler(this);
		Pic_Handler           = new PicHandler(this);
	}

	public void Set_Service_Core(FMFService.ServiceCore core){ Service_Core = core; }

	public void Update_Friends_Handler(JSONArray friends)
	{
		Friends = friends;

		Service_Core.Data_Handler.Friends_Handler.Update_Friends_In_Range();
	}

	public JSONObject Get_Friend(int Friend_ID)
	{
		JSONArray Friends_In_Range = Friends_Handler.Friends_In_Range;
		JSONObject Friend = null;
		boolean friend_not_found = true;

		try
		{

			for(int i = 0; i < Friends_In_Range.length() && friend_not_found; i++)
			{
				if(Friend_ID == Friends_In_Range.getJSONObject(i).getInt("fmf_profile_id"))
				{
					friend_not_found = false;

					Friend = Friends_In_Range.getJSONObject(i);
				}
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in DataHandler: Get_Friend:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}

		return Friend;
	}

	public void Update_Poke_List(JSONArray Pokes)
	{
		try
		{
			Service_Core.Data_Handler.Friends_Handler.Add_New_Pokes(Pokes);
		}
		catch(JSONException e)
		{
			System.err.println("Error in FriendsHandler: Update_Poke_List:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	private void Send_Notification(int user_id, String message)
	{
		try
		{
			Intent intent = new Intent(Service_Core.Base_Context, CoreActivity.class);
			intent.putExtra("user_id", user_id);
			intent.setAction(Long.toString(System.currentTimeMillis()));
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

			PendingIntent pendingIntent = PendingIntent.getActivity(Service_Core.Base_Context, user_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			String Friend_Name = Service_Core.File_Handler.FMF_Settings_Data.getString("Name") +
			                     " " +
			                     Service_Core.File_Handler.FMF_Settings_Data.getString("Surname");

			String ticker_message = Friend_Name + message;
			String html_message   = "<i><b>" + Friend_Name + "</b></i>" + message;

			Notification notification = new Notification.InboxStyle(new Notification.Builder(Service_Core.Base_Context)
					                                                        .setContentIntent(pendingIntent).setSmallIcon(R.drawable.find_my_friend)
					                                                        .setTicker(ticker_message)
					                                                        .setContentTitle("Find My Friend")
					                                                        .setContentText((Html.fromHtml(html_message)))
					                                                        .setAutoCancel(true)
					                                                        .setDefaults(Notification.DEFAULT_SOUND |
					                                                                     Notification.DEFAULT_VIBRATE |
					                                                                     Notification.DEFAULT_LIGHTS)).build();

			Service_Core.Notification_Manager.notify(user_id, notification);
		}
		catch(JSONException e)
		{
			System.err.println("Error FriendsHandler: Send_Notification:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	private Runnable Get_Data_Runnable = new Runnable(){ @Override public void run(){ Get_Data(); } };

	public void Get_Data()
	{
		//if(Service_Core.UI_Is_Present || Service_Core.UI_Core.is_Initializing)
		//{
			int user_update_interval = Service_Core.DATA_UPDATE_INTERVAL;

			if(Service_Core.User_Latitude != 666 && Service_Core.User_Longitude != 666)
			{
				try
				{
					JSONObject jsonSend = new JSONObject();

					jsonSend.accumulate("action", "Get_Data");
					jsonSend.accumulate("user_id", Service_Core.File_Handler.FMF_Settings_Data.getInt("User_ID"));
					jsonSend.accumulate("measurement_system", Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System"));

					Service_Core.Server_Handler.Queue_Server_Request(Service_Core.SERVER_URL, "find_my_friend.php", jsonSend, Service_Core.Server_Response_Handler);
				}
				catch(JSONException e)
				{
					System.err.println("Error FriendsHandler: Get_Friends:");
					System.err.println("Caught JSON Exception: " + e.getMessage());
				}
			}
			else user_update_interval = 0;

			Service_Core.Server_Response_Handler.removeCallbacks(Get_Data_Runnable);
			Service_Core.Server_Response_Handler.postDelayed(Get_Data_Runnable, user_update_interval);
		//}
	}
}
