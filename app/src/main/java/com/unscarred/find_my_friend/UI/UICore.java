package com.unscarred.find_my_friend.UI;

import com.unscarred.find_my_friend.R;
import com.unscarred.find_my_friend.Service.FMFService;
import com.unscarred.find_my_friend.Common.ThemeSettings;
import com.unscarred.find_my_friend.UI.ScreenHandler.Load_Screen_Function;

import android.animation.ValueAnimator;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.*;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class UICore
{
	private Context                 Base_Context;
	public  FMFService              FMF_Service = null;
	public  Handler                 Activity_Message_Handler;
	public  boolean                 Service_Is_Bound = false;
	private View                    Activity_View;
	public  InputMethodManager      Input_Method_Manager;

	public TabLayoutHandler         Tab_Layout_Handler;
	public SettingsHandler          Settings_Handler;
	public ThemeSettings            Theme_Settings;
	public ScreenHandler            Screen_Handler;
	//public Animations               UI_Animations;

	public  boolean                 is_Initializing = true;
	public  boolean                 Display_Navigation_Buttons = true;

	public  FrameLayout             Main_Frame_Layout;
	public  RelativeLayout          Main_Content_Layout;

	public  RelativeLayout          Main_Header_Layout;
	public  RelativeLayout          Button_Layout;
	public  LinearLayout            Menu_Button;
	public  LinearLayout            Back_Button;
	public  TextView                Network_Error_Icon;

	public  int                     Intent_User_ID = -1;
	public  String                  Current_Screen = "Main";


	public UICore(Context context, View view, Handler handler)
	{
		Base_Context = context;
		Activity_View = view;
		Activity_Message_Handler = handler;

		Input_Method_Manager = (InputMethodManager)Base_Context.getSystemService(Context.INPUT_METHOD_SERVICE);

		Theme_Settings = new ThemeSettings(Base_Context);
		Screen_Handler = new ScreenHandler(Base_Context, this);
		//UI_Animations = new Animations(Base_Context, this);

		Main_Header_Layout = (RelativeLayout)Activity_View.findViewById(R.id.main_header_layout);
		Button_Layout = (RelativeLayout)Activity_View.findViewById(R.id.main_button_layout);
		Menu_Button = (LinearLayout)Activity_View.findViewById(R.id.main_menu_button);
		Back_Button = (LinearLayout)Activity_View.findViewById(R.id.main_back_button);
		Network_Error_Icon = (TextView)Activity_View.findViewById(R.id.main_network_error_text_view);
		TextView Title_Text_View = (TextView)Activity_View.findViewById(R.id.main_title_text_view);
		Title_Text_View.setTypeface(Theme_Settings.Font_Light);

		Main_Header_Layout.setOnTouchListener(On_Touch_Listener);
		Button_Layout.setOnTouchListener(On_Touch_Listener);

		Network_Error_Icon.setTypeface(Theme_Settings.Font_Bold);
		Network_Error_Icon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		Network_Error_Icon.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_3));
		Network_Error_Icon.setVisibility(View.INVISIBLE);

		Button_Layout.setVisibility(View.GONE);

		Tab_Layout_Handler = new TabLayoutHandler(Base_Context, this);
		Settings_Handler   = new SettingsHandler(Base_Context, this);

		Main_Frame_Layout = (FrameLayout) Activity_View.findViewById(R.id.main_frame_layout);
		Main_Content_Layout = (RelativeLayout)Activity_View.findViewById(R.id.main_content_layout);

		FacebookSdk.sdkInitialize(Base_Context);
	}

	public boolean is_Network_Available(){ return (((ConnectivityManager)Base_Context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo()).isConnected(); }
	public boolean is_GPS_Available(){ return ((LocationManager)Base_Context.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER); }

	public void Exit_FMF()
	{
		Log.d("Nic Says", "Exit_FMF HAS RUN.");

		Service_Is_Bound = false;

		AppEventsLogger.deactivateApp(Base_Context);

		if(Tab_Layout_Handler.Friends_Tab_Handler.Friend_Handler.Screen_Is_Visible) Tab_Layout_Handler.Friends_Tab_Handler.Friend_Handler.Stop_Sensor_Manager();

		if(!is_Initializing)
		{
			FMF_Service.Service_Core.UI_Is_Present = false;
			FMF_Service.Service_Core.Facebook_Handler.Profile_Tracker.stopTracking();

			if(Service_Connection != null) Base_Context.unbindService(Service_Connection);
		}
	}

	public void Enter_FMF()
	{
		Log.d("Nic Says", "Enter_FMF HAS RUN.");

		if(FMF_Service != null) FMF_Service.Service_Core.UI_Is_Present = true;

		AppEventsLogger.activateApp(Base_Context);

		if(Tab_Layout_Handler.Friends_Tab_Handler.Friend_Handler.Screen_Is_Visible) Tab_Layout_Handler.Friends_Tab_Handler.Friend_Handler.Start_Sensor_Manager();

		if(FMF_Service != null) FMF_Service.Service_Core.Facebook_Handler.Profile_Tracker.startTracking();

		Bundle bundle = new Bundle();
		Message bundle_message = new Message();

		bundle.putInt("action", 101);
		bundle_message.setData(bundle);

		Activity_Message_Handler.sendMessage(bundle_message);
	}

	public void Show_Splash_Screen()
	{
		Screen_Handler.Show_Screen("Splash", Main_Frame_Layout, "Fade_In", "Fade_Out",
		                           new Load_Screen_Function()
		                           {
			                           @Override public View Execute()
			                           {
				                           LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				                           View Splash_Screen_View = layoutInflater.inflate(R.layout.splash, null);

				                           final WebView Splash_Loader = (WebView)Splash_Screen_View.findViewById(R.id.splash_loader_web_view);

				                           Splash_Loader.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
				                           Splash_Loader.loadUrl("file:///android_asset/loader_dialog/loader_dialog.html");
				                           Splash_Loader.setBackgroundColor(Color.parseColor("#00000000"));

				                           ViewTreeObserver View_Tree_Observer = Main_Content_Layout.getViewTreeObserver();
				                           View_Tree_Observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
				                           {
					                           @Override public void onGlobalLayout()
					                           {
						                           if(is_Initializing)
						                           {
							                           int layout_height = Main_Content_Layout.getHeight();
							                           int half_layout_height = Math.round(layout_height / 2);
							                           int half_space_left = Math.round((Theme_Settings.Screen_Height - layout_height) / 2);
							                           int spinner_top = half_layout_height - half_space_left - Theme_Settings.DP(37);
							                           if(spinner_top < 0) spinner_top = 0;

							                           Splash_Loader.setY(spinner_top);
						                           }
						                           else Main_Content_Layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					                           }
				                           });

				                           return Splash_Screen_View;
			                           }
		                           },
		                           null, null);
	}

	public void Swap_Nav_Buttons()
	{
		if(Menu_Button.getVisibility() == View.VISIBLE)
		{
			Menu_Button.setVisibility(View.GONE);
			Back_Button.setVisibility(View.VISIBLE);
		}
		else
		{
			Menu_Button.setVisibility(View.VISIBLE);
			Back_Button.setVisibility(View.GONE);
		}
	}

	public Handler Service_Handler = new Handler()
	{
		@Override public void handleMessage(Message message)
		{
			Log.d("Nic Says", "UICore: Service_Handler: Action: " + message.getData().getInt("action"));

			switch(message.getData().getInt("action"))
			{
				case 0:
					is_Initializing = false;
					FMF_Service.Service_Core.UI_Is_Present = true;

					Screen_Handler.Show_Screen("Tab_Layout", Main_Frame_Layout, "Fade_In", "Fade_Out", Tab_Layout_Handler.Load_Screen, null, null);

					new Handler().postDelayed(new Runnable()
					{
						@Override public void run()
						{
							Menu_Button.setVisibility(View.VISIBLE);
							Back_Button.setVisibility(View.GONE);

							if(Display_Navigation_Buttons) Button_Layout.setVisibility(View.VISIBLE);
							else Button_Layout.setVisibility(View.GONE);

							if(Intent_User_ID > 0)
							{
								//Friend_Handler.Load_Screen(Intent_User_ID, "Friend");
								FMF_Service.Service_Core.Notification_Manager.cancel(Intent_User_ID);
								Intent_User_ID = -1;

								//Main_View_Switcher.addView(FMF_Alert.FMF_View);
								//Main_View_Switcher.showNext();
							}
						}
					}, 550);
					break;

				case 1:
					Bundle bundle = new Bundle();
					Message bundle_message = new Message();

					bundle.putInt("action", 100);
					bundle_message.setData(bundle);

					Activity_Message_Handler.sendMessage(bundle_message);
					break;
			}
		}
	};

	float Menu_Swipe_Initial_X;
	float Menu_Swipe_Initial_Y;

	public View.OnTouchListener On_Touch_Listener = new View.OnTouchListener()
	{
		@Override public boolean onTouch(View view, MotionEvent event)
		{
			float Final_X = event.getRawX();
			float Final_Y = event.getRawY();
			float Delta_X = Final_X - Menu_Swipe_Initial_X;
			float Delta_Y = Final_Y - Menu_Swipe_Initial_Y;

			switch(event.getActionMasked())
			{
				case MotionEvent.ACTION_DOWN:
					Menu_Swipe_Initial_X = event.getRawX();
					Menu_Swipe_Initial_Y = event.getRawY();
					break;

				case MotionEvent.ACTION_UP:
					if(view.getId() == R.id.main_header_layout) Log.d("Nic Says", "Main Header was Swiped.");
					if(view.getId() == R.id.settings_header_layout) Log.d("Nic Says", "Settings Header was Swiped.");

					if(view.getId() == R.id.main_button_layout)
					{
						Tab_Layout_Handler.Value_Animator_Up.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
						{
							@Override public void onAnimationUpdate(ValueAnimator animator)
							{
								if(Tab_Layout_Handler.Friends_Tab_Handler.UI_On_Touch_View != null)
									Tab_Layout_Handler.Friends_Tab_Handler.UI_On_Touch_View.setBackgroundColor((Integer)animator.getAnimatedValue());
							}
						});
						Tab_Layout_Handler.Value_Animator_Up.start();

						if(Settings_Handler.Screen_Is_Visible)
						{
							Settings_Handler.Hide_Screen();

							FMF_Service.Service_Core.File_Handler.Save_FMF_Settings();
						}
						else if(!is_Initializing) Settings_Handler.Show_Screen();
					}

					if(Menu_Swipe_Initial_Y < Final_Y &&
					   Math.abs(Delta_X) < Tab_Layout_Handler.SWIPE_THRESHOLD &&
					   (view.getId() == R.id.main_header_layout ||
					    view.getId() == R.id.settings_header_layout) &&
					   !is_Initializing &&
					   Settings_Handler.Screen_Is_Visible) // Swipe Down to Up
					{
						FMF_Service.Service_Core.File_Handler.Save_FMF_Settings();

						Settings_Handler.Hide_Screen();
					}

					if(Menu_Swipe_Initial_X < Final_X &&
					   Math.abs(Delta_Y) < Tab_Layout_Handler.SWIPE_THRESHOLD &&
					   (view.getId() == R.id.settings_general_header_layout ||
					    view.getId() == R.id.settings_profile_header_layout ||
					    view.getId() == R.id.settings_privacy_header_layout) &&
					   !is_Initializing &&
					   Settings_Handler.Screen_Is_Visible) // Left to Right swipe
					{
						FMF_Service.Service_Core.File_Handler.Save_FMF_Settings();

						Settings_Handler.Hide_Screen();
					}

					break;
			}

			return true;
		}
	};

	public ServiceConnection Service_Connection = new ServiceConnection()
	{
		@Override public void onServiceConnected(ComponentName className, IBinder i_binder)
		{
			FMFService.Binder Local_Binder = (FMFService.Binder)i_binder;

			FMF_Service = Local_Binder.Get_Service();

			FMF_Service.Service_Core.Set_UI_Core(Get_Instance());
			FMF_Service.Service_Core.File_Handler.Set_Service_Core(FMF_Service.Service_Core);
			FMF_Service.Service_Core.File_Handler.Read_FMF_Settings();
			FMF_Service.Service_Core.Location_Service_Handler.Set_Service_Core(FMF_Service.Service_Core);
			FMF_Service.Service_Core.Facebook_Handler.Set_Service_Core(FMF_Service.Service_Core);
			FMF_Service.Service_Core.Server_Response_Functions.Set_Service_Core(FMF_Service.Service_Core);
			FMF_Service.Service_Core.Data_Handler.Set_Service_Core(FMF_Service.Service_Core);

			Service_Is_Bound = true;

			if(!is_Network_Available() && !is_GPS_Available())
			{
				FMF_Service.Service_Core.UI_Is_Present = false;

				Toast.makeText(Base_Context, "Please activate your Network and/or GPS, and try again.", Toast.LENGTH_LONG).show();

				((Activity)Base_Context).finish();
			}
		}

		@Override public void onServiceDisconnected(ComponentName component_name){ Service_Is_Bound = false; }
	};

	private UICore Get_Instance(){ return this; }

	private void Get_Hash_Key()
	{
		try
		{
			PackageInfo info = Base_Context.getPackageManager().getPackageInfo("com.unscarred.find_my_friend", PackageManager.GET_SIGNATURES);

			for(Signature signature : info.signatures)
			{
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("Key Hash: ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		}
		catch(PackageManager.NameNotFoundException e)
		{
			System.err.println("Error in Get_Hash_Key:");
			System.err.println("Caught PackageManager Name Not Found Exception: " + e.getMessage());
		}
		catch(NoSuchAlgorithmException e)
		{
			System.err.println("Error in Get_Hash_Key:");
			System.err.println("Caught No Such Algorithm Exception Exception: " + e.getMessage());
		}
	}
}
