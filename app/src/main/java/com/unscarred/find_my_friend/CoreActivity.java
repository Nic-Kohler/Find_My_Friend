package com.unscarred.find_my_friend;

import com.unscarred.find_my_friend.Common.FileHandler;
import com.unscarred.find_my_friend.UI.UICore;
import com.unscarred.find_my_friend.CreateProfile.CreateProfile;
import com.unscarred.find_my_friend.Service.FMFService;

import android.widget.FrameLayout;
import android.widget.Toast;
import android.app.ActivityManager;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.v4.app.FragmentActivity;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.*;

import org.json.JSONException;
import org.json.JSONObject;


public class CoreActivity extends FragmentActivity
{
	public  static      UICore          UI_Core = null;
	public  static      CreateProfile   Create_Profile = null;
	public              FMFService      FMF_Service = null;
	public              Intent          Service_Intent;

	public              int             SELECTED_FILE_CODE = 0;


	private boolean Is_Service_Running(Class<?> Service_Class)
	{
		ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);

		for(ActivityManager.RunningServiceInfo service:manager.getRunningServices(Integer.MAX_VALUE))
			if(Service_Class.getName().equals(service.service.getClassName())) return true;

		return false;
	}

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setContentView(R.layout.main);

		FileHandler File_Handler = new FileHandler(this);
		File_Handler.Read_FMF_Settings();

		try
		{
			if(File_Handler.FMF_Settings_Data.getString("Step").equals("Setup"))
			{
				Create_Profile = new CreateProfile(this, Message_Handler);

				Create_Profile.Show_Log_In_Screen();
			}

			if(File_Handler.FMF_Settings_Data.getString("Step").equals("Email_Verification"))
			{
				Create_Profile = new CreateProfile(this, Message_Handler);

				Create_Profile.Show_Email_Verification_Screen();
			}

			if(File_Handler.FMF_Settings_Data.getString("Step").equals("Run"))
			{
				UI_Core = new UICore(this, this.findViewById(android.R.id.content), Message_Handler);

				UI_Core.Show_Splash_Screen();

				Service_Intent = new Intent(this, FMFService.class);

				if(!Is_Service_Running(FMFService.class)) startService(Service_Intent);

				onNewIntent(getIntent());
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in CoreActivity: onCreate:");
			System.err.println("Caught JSONException: " + e.getMessage());
		}
	}
	@Override protected void onStart()
	{
		super.onStart();

		if(UI_Core != null) bindService(Service_Intent, UI_Core.Service_Connection, Context.BIND_AUTO_CREATE);
	}

	@Override protected void onResume()
	{
		super.onResume();

		Log.d("Nic Says", "onResume HAS RUN.");

		if(UI_Core != null) UI_Core.Enter_FMF();
	}

	@Override protected void onPause()
	{
		super.onPause();

		Log.d("Nic Says", "onPause HAS RUN.");

		if(UI_Core != null) UI_Core.Exit_FMF();
	}

	@Override protected void onDestroy()
	{
		super.onDestroy();

		Log.d("Nic Says", "onDestroy HAS RUN.");
	}

	@Override public void onBackPressed()
	{
		/*
		UI_Core.FMF_Service.Service_Core.File_Handler.Save_FMF_Settings();

		if(UI_Core.FMF_Alert.Screen_Is_Visible)
		{
			UI_Core.Main_View_Switcher.showPrevious();
			UI_Core.Main_View_Switcher.removeViewAt(1);

			UI_Core.FMF_Alert.Kill_Screen();
		}
		else if(UI_Core.Settings_Handler.Screen_Is_Visible)
		{
			UI_Core.Settings_Handler.Hide_Screen();
		}
		else finish();
		*/

		UI_Core.Screen_Handler.Show_Previous_Screen();
	}

	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_MENU)
		{
			if(UI_Core.Settings_Handler.Screen_Is_Visible)
			{
				UI_Core.Settings_Handler.Hide_Screen();

				UI_Core.FMF_Service.Service_Core.File_Handler.Save_FMF_Settings();
			}
			else if(!UI_Core.is_Initializing) UI_Core.Settings_Handler.Show_Screen();

			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override protected void onActivityResult(int requestCode, int responseCode, Intent data)
	{
		if(requestCode == SELECTED_FILE_CODE)
		{
			if(responseCode == RESULT_OK)
			{
				Create_Profile.Log_In_Handler.Profile_Settings_Handler.Save_Editor_Profile_Pic_to_File(data);

				String path = getFilesDir().getPath() + "/profile_pic.jpg";

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(path, options);

				int width  = options.outWidth;
				int height = options.outHeight;

				if(Create_Profile != null)
				{
					Create_Profile.Log_In_Handler.Profile_Settings_Handler.Profile_Pic_Editor.Load_Screen(path, width, height);

					Create_Profile.Log_In_Handler.Profile_Settings_Handler.View_Switcher.addView(Create_Profile.Log_In_Handler.Profile_Settings_Handler.Profile_Pic_Editor.Profile_Pic_Editor_View);
					Create_Profile.Log_In_Handler.Profile_Settings_Handler.View_Switcher.showNext();
				}
			}
		}

		super.onActivityResult(requestCode, responseCode, data);

		if(UI_Core != null && !UI_Core.is_Initializing)
			UI_Core.FMF_Service.Service_Core.Facebook_Handler.Callback_Manager.onActivityResult(requestCode, responseCode, data);
	}

	@Override protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);

		Bundle extras = intent.getExtras();

		if(extras != null && extras.containsKey("user_id")) UI_Core.Intent_User_ID = extras.getInt("user_id");
	}

	private CoreActivity Get_Instance(){ return this; }

	public Handler Message_Handler = new Handler()
	{
		@Override public void handleMessage(Message message)
		{
			try
			{
				JSONObject json_response;

				if(message.getData().getString("json") != null)
				{
					json_response = new JSONObject(message.getData().getString("json"));
				}
				else
				{
					json_response = new JSONObject();
					json_response.put("action", message.getData().getInt("action"));
				}

				switch(json_response.getInt("action"))
				{
					case 100:
						Create_Profile = null;

						UI_Core = new UICore(Get_Instance(), findViewById(android.R.id.content), Message_Handler);

						FrameLayout Main_Frame_Layout = (FrameLayout)findViewById(R.id.main_frame_layout);
						Main_Frame_Layout.removeAllViews();

						UI_Core.Show_Splash_Screen();

						Service_Intent = new Intent(getApplicationContext(), FMFService.class);

						if(!Is_Service_Running(FMFService.class)) startService(Service_Intent);

						onNewIntent(getIntent());
						break;

					case 101:
						if(!UI_Core.is_Initializing) bindService(Service_Intent, UI_Core.Service_Connection, Context.BIND_AUTO_CREATE);
						break;

					case 102:
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("image/*");
						intent.addCategory(Intent.CATEGORY_OPENABLE);

						try
						{
							startActivityForResult(Intent.createChooser(intent, "Select an Image"), SELECTED_FILE_CODE);
						}
						catch(ActivityNotFoundException e)
						{
							Toast.makeText(Get_Instance(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();

							System.err.println("Error reading Response in CoreActivity: showFileChooser:");
							System.err.println("Caught Activity Not Found Exception: " + e.getMessage());
						}
						break;

					case 200:
						if(json_response.getBoolean("success"))
						{
							Log.d("Nic Says", "File Upload Success: TRUE");
						}
						else
						{
							Log.d("Nic Says", "File Upload Success: FALSE");
							Log.d("Nic Says", "Error Message:       " + json_response.getString("error_message"));
						}
						break;
				}
			}
			catch(JSONException e)
			{
				System.err.println("Error in CoreActivity: Message_Handler:");
				System.err.println("Caught JSON Exception: " + e.getMessage());
			}

		}
	};
}
