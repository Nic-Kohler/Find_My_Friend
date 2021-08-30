package com.unscarred.find_my_friend.Service;

import com.unscarred.find_my_friend.CoreActivity;
import com.unscarred.find_my_friend.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FriendsHandler
{
	public  DataHandler     Data_Handler;

	public  JSONArray       Friends_In_Range = new JSONArray();
	public  JSONArray       Blocked_Users = new JSONArray();
	public  JSONArray       Poke_List = new JSONArray();


	public FriendsHandler(DataHandler data_handler){ Data_Handler = data_handler; }

	public void Update_Friends_In_Range()
	{
		try
		{
			JSONArray Friends = Data_Handler.Service_Core.Data_Handler.Friends;
			JSONArray New_Friends_In_Range = new JSONArray();

			for(int i = 0; i < Data_Handler.Service_Core.Data_Handler.Friends.length(); i++)
				if(Friends.getJSONObject(i).getInt("distance") <= Data_Handler.Service_Core.File_Handler.FMF_Settings_Data.getInt("Current_Range"))
					New_Friends_In_Range.put(Data_Handler.Service_Core.Data_Handler.Friends.getJSONObject(i));

			for(int i = 0; i < New_Friends_In_Range.length(); i++)
			{
				boolean friend_not_found = true;

				for(int j = 0; j < Friends_In_Range.length() && friend_not_found; j++)
					if(New_Friends_In_Range.getJSONObject(i).getInt("profile_id") == Friends_In_Range.getJSONObject(j).getInt("profile_id"))
						friend_not_found = false;

				if(friend_not_found)
					if(!Data_Handler.Service_Core.UI_Is_Present)
						Send_Notification(New_Friends_In_Range.getJSONObject(i).getInt("profile_id"), " is in range.");
			}

			Friends_In_Range = Sort_By_Distance_ASC(New_Friends_In_Range);
		}
		catch(JSONException e)
		{
			System.err.println("Error in FriendsHandler: Update_Friends_In_Range:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	private JSONArray Sort_By_Distance_ASC(JSONArray New_Friends_In_Range)
	{
		try
		{
			for(int i = 0; i < New_Friends_In_Range.length(); i++)
			{
				for(int j = 0; j < New_Friends_In_Range.length(); j++)
				{
					if(New_Friends_In_Range.getJSONObject(i).getInt("distance") < New_Friends_In_Range.getJSONObject(j).getInt("distance"))
					{
						JSONObject Temp_Object;

						Temp_Object = New_Friends_In_Range.getJSONObject(j);
						New_Friends_In_Range.put(j, New_Friends_In_Range.getJSONObject(i));
						New_Friends_In_Range.put(i, Temp_Object);
					}
				}
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in FriendsHandler: Sort_By_Distance_ASC:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}

		return New_Friends_In_Range;
	}

	public void Add_New_Pokes(JSONArray Pokes) throws JSONException
	{
		boolean poke_is_new;

		for(int i = 0; i < Pokes.length(); i++)
		{
			poke_is_new = true;

			for(int j = 0; j < Poke_List.length() && poke_is_new; j++)
				if(Pokes.getJSONObject(i).getInt("from_profile_id") == Poke_List.getJSONObject(j).getInt("from_profile_id"))
					poke_is_new = false;

			if(poke_is_new)
			{
				Poke_List.put(Pokes.getJSONObject(i));

				if(!Data_Handler.Service_Core.UI_Is_Present && !Data_Handler.Service_Core.UI_Core.is_Initializing)
					Send_Notification(Pokes.getJSONObject(i).getInt("from_profile_id"),
					                  " has poked you.");

				if(Data_Handler.Service_Core.UI_Is_Present && !Data_Handler.Service_Core.UI_Core.is_Initializing)
					Data_Handler.Service_Core.UI_Core.Tab_Layout_Handler.Poke_UI_Handler.Add_Poke_Alert_Popup_Window(Pokes.getJSONObject(i).getInt("from_profile_id"));
			}
		}
	}

	private void Send_Notification(int user_id, String message)
	{
		Intent intent = new Intent(Data_Handler.Service_Core.Base_Context, CoreActivity.class);
		intent.putExtra("user_id", user_id);
		intent.setAction(Long.toString(System.currentTimeMillis()));
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(Data_Handler.Service_Core.Base_Context, user_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		String Friend_Name = Get_Friend_Name(user_id);

		String ticker_message = Friend_Name + message;
		String html_message   = "<i><b>" + Friend_Name + "</b></i>" + message;

		Notification notification = new Notification.InboxStyle(new Notification.Builder(Data_Handler.Service_Core.Base_Context)
				                                                        .setContentIntent(pendingIntent).setSmallIcon(R.drawable.find_my_friend)
				                                                        .setTicker(ticker_message)
				                                                        .setContentTitle("Find My Friend")
				                                                        .setContentText((Html.fromHtml(html_message)))
				                                                        .setAutoCancel(true)
				                                                        .setDefaults(Notification.DEFAULT_SOUND |
				                                                                     Notification.DEFAULT_VIBRATE |
				                                                                     Notification.DEFAULT_LIGHTS)).build();

		Data_Handler.Service_Core.Notification_Manager.notify(user_id, notification);
	}

	public void Clear_User_Lists()
	{
		Friends_In_Range = new JSONArray();

		if(Data_Handler.Service_Core.UI_Is_Present) Data_Handler.Service_Core.UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Clear_Friend_Items_Layout();

	}

	public String Get_Friend_Name(int User_ID)
	{
		String friend_name = "";

		try
		{
			boolean friend_not_found = true;
			int     index            = -1;

			for(int i = 0; i < Data_Handler.Service_Core.Data_Handler.Friends.length() && friend_not_found; i++)
			{
				if(Data_Handler.Service_Core.Data_Handler.Friends.getJSONObject(i).getInt("profile_id") == User_ID)
				{
					friend_not_found = false;
					index = i;
				}
			}

			friend_name = Data_Handler.Service_Core.Data_Handler.Friends.getJSONObject(index).getString("name") +
			              " " +
			              Data_Handler.Service_Core.Data_Handler.Friends.getJSONObject(index).getString("surname");

			Log.d("Nic Says", "Friend Name: " + friend_name);
		}
		catch(JSONException e)
		{
			System.err.println("Error FriendsHandler: Get_Friend_Name:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}

		return friend_name;
	}
}
