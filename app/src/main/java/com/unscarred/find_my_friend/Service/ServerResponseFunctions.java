package com.unscarred.find_my_friend.Service;

import com.unscarred.find_my_friend.Service.FMFService.ServiceCore;
import com.unscarred.find_my_friend.UI.FriendHandler;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class ServerResponseFunctions
{
	private ServiceCore Service_Core;
	private JSONObject  jsonReceive;

	public void Set_Service_Core(ServiceCore core){ Service_Core = core; }

    public void Set_jsonReceive(JSONObject jr) { jsonReceive = jr; }
    public int Get_Action() throws JSONException { return jsonReceive.getInt("action"); }

    public void Test() throws JSONException{ Log.i("Read from server: json", jsonReceive.getString("response")); }

	public void Add_Location()
	{
		try
		{
			if(jsonReceive.getBoolean("success"))
			{
				Log.d("Nic Says", "sql:            " + jsonReceive.getString("sql"));
				Log.d("Nic Says", "location_id:    " + jsonReceive.getInt("location_id"));
				Log.d("Nic Says", "fmf_profile_id: " + jsonReceive.getInt("fmf_profile_id"));
				Log.d("Nic Says", "location_name:  " + jsonReceive.getString("location_name"));
				Log.d("Nic Says", "latitude:       " + jsonReceive.getDouble("latitude"));
				Log.d("Nic Says", "longitude:      " + jsonReceive.getDouble("longitude"));
				Log.d("Nic Says", "address:        " + jsonReceive.getString("address"));
				Log.d("Nic Says", "friend_count:   " + jsonReceive.getInt("friend_count"));
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in Server_Response_Handler: Add_Location");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	public void Update_Location()
	{
		try
		{
			if(jsonReceive.getBoolean("success"))
			{
				Service_Core.Location_Service_Handler.First_Run_Complete = true;

				if(Service_Core.UI_Core.is_Initializing) Service_Core.Finalize_Initialization();
			}
			else
			{
				JSONObject jsonSend = new JSONObject();

				jsonSend.accumulate("action", "Update_Location");
				jsonSend.accumulate("user_id", Service_Core.File_Handler.FMF_Settings_Data.getInt("User_ID"));
				jsonSend.accumulate("city", Service_Core.User_City);
				jsonSend.accumulate("latitude", Service_Core.User_Latitude);
				jsonSend.accumulate("longitude", Service_Core.User_Longitude);
				jsonSend.accumulate("altitude", Service_Core.User_Altitude);

				Service_Core.Server_Handler.Queue_Server_Request(Service_Core.SERVER_URL, "init.php", jsonSend, Service_Core.Server_Response_Handler);
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in Server_Response_Handler: Update_GPS");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	public void Get_Data()
	{
		try
		{
			if(jsonReceive.getBoolean("success"))
			{
				Log.d("Nic Says", "friends:   " + jsonReceive.getJSONArray("friends").toString());
				Log.d("Nic Says", "pokes:     " + jsonReceive.getJSONArray("pokes").toString());
				Log.d("Nic Says", "Service_Core.UI_Is_Present:   " + Service_Core.UI_Is_Present);

				Service_Core.Data_Handler.Update_Friends_Handler(jsonReceive.getJSONArray("friends"));
				Service_Core.Data_Handler.Update_Poke_List(jsonReceive.getJSONArray("pokes"));

				if(Service_Core.UI_Is_Present)
				{
					Service_Core.UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Update_Friends();
				}
			}
			else
			{
				Log.d("Nic Says", "No Friends Found.");

				Service_Core.Data_Handler.Friends_Handler.Clear_User_Lists();
			}

			if(!Service_Core.Data_Handler.First_Run_Complete)
			{
				Service_Core.Data_Handler.Pic_Handler.Load_Pic_Array();
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in Server_Response_Handler: Get_Data");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	public void Get_Friend_Data()
	{
		try
		{
			FriendHandler Friend_Handler = Service_Core.UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Friend_Handler;

			if(jsonReceive.getBoolean("success"))
			{
				Log.d("Nic Says", "*** SERVICE: UPDATING OTHER USER INFO ***");

				if(jsonReceive.getInt("friend_id") == Friend_Handler.Friend_ID && Friend_Handler.Screen_Is_Visible)
				{
					Friend_Handler.Distance_To_Friend = jsonReceive.getInt("distance");
					Friend_Handler.Friend_Latitude = jsonReceive.getDouble("latitude");
					Friend_Handler.Friend_Longitude = jsonReceive.getDouble("longitude");
					Friend_Handler.Friend_Altitude = jsonReceive.getDouble("altitude");

					Friend_Handler.Update_Friend_UI();
				}
			}
			else
			{
				try
				{
					JSONObject jsonSend = new JSONObject();

					jsonSend.accumulate("action", "Get_Users_In_Range");
					jsonSend.accumulate("user_id", Service_Core.File_Handler.FMF_Settings_Data.getInt("User_ID"));
					jsonSend.accumulate("city", Service_Core.User_City);
					jsonSend.accumulate("latitude", Service_Core.User_Latitude);
					jsonSend.accumulate("longitude", Service_Core.User_Longitude);
					jsonSend.accumulate("measurement_system", Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System"));

					if(Service_Core.UI_Is_Present)
						jsonSend.accumulate("fmf_range", Service_Core.File_Handler.FMF_Settings_Data.getInt("Max_Range"));
					else
						jsonSend.accumulate("fmf_range", Service_Core.File_Handler.FMF_Settings_Data.getInt("Current_Range"));

					Service_Core.Server_Handler.Queue_Server_Request(Service_Core.SERVER_URL, "find_my_friend.php", jsonSend, Service_Core.Server_Response_Handler);
				}
				catch(JSONException e)
				{
					System.err.println("Error sending parsing JSONObject to SendServerRequest():");
					System.err.println("Caught JSON Exception: " + e.getMessage());
				}

				//Service_Core.UI_Core.Main_View_Switcher.showPrevious();
				//Service_Core.UI_Core.Main_View_Switcher.removeViewAt(1);

				//Friend_Handler.Kill_Screen();

				Log.d("Nic Says", "Other User not found.");
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in Server_Response_Handler: Get_Other_User_Info");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}
}
