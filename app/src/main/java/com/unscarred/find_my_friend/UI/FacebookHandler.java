package com.unscarred.find_my_friend.UI;

import com.unscarred.find_my_friend.Service.FMFService.ServiceCore;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.util.Log;

import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;


public class FacebookHandler
{
	private Context             Base_Context;
	private ServiceCore         Service_Core;
	public  CallbackManager     Callback_Manager;
	public  String              Facebook_ID = "";
	public  Profile             Facebook_Profile;
	public  Bitmap              Facebook_Profile_Pic = null;

	public  boolean             Is_Logged_In = false;
	public  boolean             is_Initializing = true;
	public  int                 Initialize_Process_Count = 0;


	public FacebookHandler(Context context)
	{
		Base_Context = context;

		Callback_Manager = CallbackManager.Factory.create();
		LoginManager.getInstance().registerCallback(Callback_Manager, Facebook_Login_Callback);

		if(AccessToken.getCurrentAccessToken() != null) Is_Logged_In = true;

		if(Is_Logged_In)
		{
			Facebook_Profile = Profile.getCurrentProfile();

			Log.d("Nic Says", "Facebook_Profile: " + Facebook_Profile.toString());
		}
	}

	private AccessTokenTracker Access_Token_Tracker = new AccessTokenTracker()
	{
		@Override protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
		{
			if(currentAccessToken == null)
			{
				Facebook_ID = "";
				Facebook_Profile_Pic = null;
				Is_Logged_In = false;

				if(Service_Core.UI_Core.Settings_Handler.Profile_Settings_Handler.Screen_Is_Visible)
					Service_Core.UI_Core.Settings_Handler.Profile_Settings_Handler.Profile_Pic_Layout.setVisibility(View.GONE);

				if(Service_Core.UI_Core.Settings_Handler.Screen_Is_Visible)
				{
					Service_Core.UI_Core.Settings_Handler.Profile_Pic_Image_View
							.setImageBitmap(Service_Core.Data_Handler.Pic_Handler.Create_Default_Profile_Pic(50));

					Service_Core.UI_Core.Settings_Handler.Invite_Friends_Button.setVisibility(View.GONE);
				}

				if(!Service_Core.UI_Core.is_Initializing && Service_Core.UI_Is_Present)
				{
					Service_Core.Data_Handler.Friends_Handler.Friends_In_Range = new JSONArray();

					Service_Core.Data_Handler.Get_Data();
				}
			}
		}
	};

	public void Set_Service_Core(ServiceCore core){ Service_Core = core; }

	public void Update_Facebook_Credentials()
	{
		if(Is_Logged_In && Facebook_Profile != null)
		{
			Get_Facebook_Profile_Pic();
			Update_Facebook_ID();
			Get_Facebook_Friends();
		}
	}

	public ProfileTracker Profile_Tracker = new ProfileTracker()
	{
		@Override protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile)
		{
			if(AccessToken.getCurrentAccessToken() != null) Is_Logged_In = true; else Is_Logged_In = false;
			Facebook_Profile = Profile.getCurrentProfile();

			if(Is_Logged_In && Facebook_Profile != null)
			{
				Update_Facebook_Credentials();

				if(!Service_Core.UI_Core.is_Initializing && Service_Core.UI_Is_Present)
				{
					Service_Core.Data_Handler.Friends_Handler.Friends_In_Range = new JSONArray();

					Service_Core.Data_Handler.Get_Data();
				}
			}
			else
			{
				Callback_Manager = CallbackManager.Factory.create();
				LoginManager.getInstance().registerCallback(Callback_Manager, Facebook_Login_Callback);
			}
		}
	};

	private void Get_Facebook_Profile_Pic()
	{
		new Thread(new Runnable()
		{
			@Override public void run()
			{
				try
				{
					URL url = new URL(Facebook_Profile.getProfilePictureUri(512, 512).toString());
					Facebook_Profile_Pic = BitmapFactory.decodeStream((InputStream)url.getContent());

					if(Service_Core.UI_Core.Settings_Handler.Profile_Settings_Handler.Screen_Is_Visible)
					{
						Bundle bundle = new Bundle();
						Message message = new Message();

						bundle.putInt("action", 0);
						message.setData(bundle);

						//Service_Core.UI_Core.Settings_Handler.Profile_Settings_Handler.Facebook_Profile_Pic_Update_Handler.sendMessage(message);
					}


					Initialize_Process_Count++;
					if(Initialize_Process_Count == 2)
					{
						Initialize_Process_Count = 0;
						is_Initializing = false;
					}

					if(Service_Core.UI_Core.is_Initializing) Service_Core.Finalize_Initialization();
				}
				catch(MalformedURLException e)
				{
					System.err.println("Error Users: Create_Friends_User_Item() - Profile Pic Thread:");
					System.err.println("Caught Malformed URL Exception: " + e.getMessage());
				}
				catch(IOException e)
				{
					System.err.println("Error Users: Create_Friends_User_Item() - Profile Pic Thread:");
					System.err.println("Caught IO Exception: " + e.getMessage());
				}
			}
		}).start();
	}

	public void Update_Facebook_ID()
	{
		Facebook_ID = Facebook_Profile.getId();

		if(Service_Core != null)
		{
			try
			{
				JSONObject jsonSend = new JSONObject();

				jsonSend.accumulate("action", "Update_Facebook_ID");
				jsonSend.accumulate("user_id", Service_Core.File_Handler.FMF_Settings_Data.getInt("User_ID"));
				jsonSend.accumulate("facebook_id", Facebook_ID);

				Service_Core.Server_Handler.Queue_Server_Request(Service_Core.SERVER_URL, "find_my_friend.php", jsonSend, Service_Core.Server_Response_Handler);
			}
			catch(JSONException e)
			{
				System.err.println("Error in FacebookHandler: Update_Facebook_ID():");
				System.err.println("Caught JSON Exception: " + e.getMessage());
			}
		}
		else new Handler().postDelayed(new Runnable(){ @Override public void run(){ Update_Facebook_ID(); } }, 300);
	}

	private FacebookCallback<LoginResult> Facebook_Login_Callback = new FacebookCallback<LoginResult>()
	{
		@Override public void onSuccess(LoginResult loginResult)
		{
			Log.d("Nic Says", "Facebook_Login_Callback: onSuccess");

			if(AccessToken.getCurrentAccessToken() != null) Is_Logged_In = true; else Is_Logged_In = false;
			Facebook_Profile = Profile.getCurrentProfile();

			if(Is_Logged_In && Facebook_Profile != null)
			{
				if(Service_Core.UI_Core.Settings_Handler.Screen_Is_Visible)
					Service_Core.UI_Core.Settings_Handler.Invite_Friends_Button.setVisibility(View.VISIBLE);

				Update_Facebook_Credentials();
			}
			else
			{
				Callback_Manager = CallbackManager.Factory.create();
				LoginManager.getInstance().registerCallback(Callback_Manager, Facebook_Login_Callback);
			}
		}

		@Override public void onCancel()
		{
			Log.d("Nic Says", "Facebook_Login_Callback: onCancel");
		}

		@Override public void onError(FacebookException exception)
		{
			Log.d("Nic Says", "Facebook_Login_Callback: onError");
		}
	};

	public FacebookCallback<Sharer.Result> Facebook_Send_Callback = new FacebookCallback<Sharer.Result>()
	{
		@Override public void onSuccess(Sharer.Result result)
		{
			Log.d("Nic Says", "Facebook_Send_Callback: onSuccess");
			Log.d("Nic Says", "--- result: " + result.toString());
		}

		@Override public void onCancel()
		{
			Log.d("Nic Says", "Facebook_Send_Callback: onCancel");
		}

		@Override public void onError(FacebookException error)
		{
			Log.d("Nic Says", "Facebook_Send_Callback: onError");
			Log.d("Nic Says", "--- error: " + error.toString());
		}
	};

	public void Get_Facebook_Friends()
	{
		try
		{
			String Graph_Directive = "me/friends";
			Bundle Graph_Parameters = new Bundle();
			Graph_Parameters.putString("fields", "id,name,picture.type(large)");

			new GraphRequest(AccessToken.getCurrentAccessToken(), Graph_Directive, Graph_Parameters, HttpMethod.GET, new GraphRequest.Callback()
			{
				public void onCompleted(GraphResponse Graph_Response)
				{
					//try
					//{
						if(Graph_Response.getError() == null)
						{
							//Service_Core.Users_Handler.Facebook_Friends = Graph_Response.getJSONObject().getJSONArray("data");

							try
							{
								JSONArray Facebook_Friend_IDs = new JSONArray();

								//for(int i = 0; i < Service_Core.Users_Handler.Facebook_Friends.length(); i++)
									//Facebook_Friend_IDs.put(Service_Core.Users_Handler.Facebook_Friends.getJSONObject(i).getString("id"));

								JSONObject jsonSend = new JSONObject();

								jsonSend.accumulate("action", "Update_Facebook_Friends");
								jsonSend.accumulate("user_id", Service_Core.File_Handler.FMF_Settings_Data.getInt("User_ID"));
								jsonSend.accumulate("city", Service_Core.User_City);
								jsonSend.accumulate("facebook_friend_ids", Facebook_Friend_IDs);

								Service_Core.Server_Handler.Queue_Server_Request(Service_Core.SERVER_URL, "init.php", jsonSend, Service_Core.Server_Response_Handler);

								if(!Service_Core.UI_Core.is_Initializing && Service_Core.UI_Is_Present) Service_Core.Data_Handler.Get_Data();
							}
							catch(JSONException e)
							{
								System.err.println("Error in Server_Response_Handler: Update_GPS");
								System.err.println("Caught JSON Exception: " + e.getMessage());
							}

							Initialize_Process_Count++;
							if(Initialize_Process_Count == 2)
							{
								Initialize_Process_Count = 0;
								is_Initializing = false;
							}
							if(Service_Core.UI_Core.is_Initializing) Service_Core.Finalize_Initialization();
						}
						else Get_Facebook_Friends();
					//}
					/*
					catch(JSONException e)
					{
						System.err.println("Error in FacebookHandler: Get_Facebook_Friends:");
						System.err.println("Caught JSONException: " + e.getMessage());
					}
					*/
				}
			}).executeAsync();
		}
		catch(NullPointerException e)
		{
			System.err.println("Error in FacebookHandler: Get_Facebook_Friends:");
			System.err.println("Caught Null Pointer Exception: " + e.getMessage());
		}
	}
}
