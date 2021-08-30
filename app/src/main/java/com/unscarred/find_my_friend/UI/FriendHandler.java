package com.unscarred.find_my_friend.UI;

import com.unscarred.find_my_friend.R;
import com.unscarred.find_my_friend.Service.DataHandler;
import com.unscarred.find_my_friend.UI.ScreenHandler.Load_Screen_Function;
import com.unscarred.find_my_friend.UI.ScreenHandler.Screen_Function;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.content.Context;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FriendHandler
{
	private Context         Base_Context;
	private UICore          UI_Core;

	public  int             Distance_To_Friend;

	private SupportMapFragment Support_Map_Fragment;
	private LinearLayout    Google_Map_Layout;
	public  GoogleMap       Google_Map;
	public  View            Friend_View;
	public  TextView        Distance_Text_View;
	public  TextView        Altitude_Text_View;
	private TextView        User_No_Longer_In_Range_Text_View;
	private LinearLayout    User_Is_In_Range_Layout;
	public  ImageView       Profile_Pic_Image_View;
	private TextView        User_Name_Text_View;
	private TextView        Poke_Button;

	public  boolean         Screen_Is_Visible = false;
	public  int             Friend_ID;
	public  double          Friend_Latitude = 0f;
	public  double          Friend_Longitude = 0f;
	public  double          Friend_Altitude = 0f;
	        final int       FONT_SIZE = 20;
	public  final int       PROFILE_PIC_DIMENSION = 150;

	public  SensorManager   Sensor_Manager;
	public  Sensor          Accelerometer;
	public  Sensor          Magnetometer;
	private float[]         Last_Accelerometer = new float[3];
	private float[]         Last_Magnetometer = new float[3];
	private boolean         Last_Accelerometer_Set = false;
	private boolean         Last_Magnetometer_Set = false;
	private float[]         Rotation_Matrix = new float[9];
	private float[]         Identity_Matrix = new float[9];
	private float[]         Orientation = new float[3];

	private Marker          User_Marker;
	private Marker          Friend_Marker;
	private int             Map_Dimensions;

	public FriendHandler(Context context, UICore core)
	{
		Base_Context = context;
		UI_Core = core;

		Sensor_Manager = (SensorManager)Base_Context.getSystemService(Context.SENSOR_SERVICE);

		Accelerometer = Sensor_Manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Magnetometer = Sensor_Manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}

	public Load_Screen_Function Load_Screen = new Load_Screen_Function()
	{
		@Override public View Execute()
		{
			Screen_Is_Visible = true;

			//UI_Core.FMF_Service.Service_Core.Location_Service_Handler.Set_Location_Request_Aggressive();
			Map_Dimensions = UI_Core.Theme_Settings.Screen_Width - UI_Core.Theme_Settings.DP(40);

			///UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Set_Friend_Item_Clickable_State(false);

			LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			Friend_View = layoutInflater.inflate(R.layout.friend, null);

			Google_Map_Layout = (LinearLayout)Friend_View.findViewById(R.id.friend_google_map_layout);
			User_No_Longer_In_Range_Text_View = (TextView)Friend_View.findViewById(R.id.friend_not_in_range_text_view);
			User_Is_In_Range_Layout = (LinearLayout)Friend_View.findViewById(R.id.friend_in_range_layout);
			Profile_Pic_Image_View = (ImageView)Friend_View.findViewById(R.id.friend_profile_pic_image_view);
			User_Name_Text_View = (TextView)Friend_View.findViewById(R.id.friend_name_text_view);

			User_No_Longer_In_Range_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
			User_No_Longer_In_Range_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
			User_No_Longer_In_Range_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_2));

			User_Name_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Bold);
			User_Name_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE + 4);
			User_Name_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_2));

			FragmentActivity Fragment_Activity = (FragmentActivity)Base_Context;
			Support_Map_Fragment = (SupportMapFragment)Fragment_Activity.getSupportFragmentManager().findFragmentById(R.id.friend_google_map);
			Support_Map_Fragment.getMapAsync(On_Map_Ready_Callback);

			User_No_Longer_In_Range_Text_View.setVisibility(View.GONE);
			User_Is_In_Range_Layout.setVisibility(View.VISIBLE);

			Distance_Text_View = (TextView)Friend_View.findViewById(R.id.friend_distance_text_view);
			Altitude_Text_View = (TextView)Friend_View.findViewById(R.id.friend_altitude_text_view);
			Poke_Button = (TextView)Friend_View.findViewById(R.id.friend_poke_button);

			Poke_Button.setVisibility(View.VISIBLE);
			Poke_Button.setBackgroundResource(R.drawable.poke_unselected);
			Poke_Button.setOnTouchListener(On_Touch_Listener);

			Distance_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
			Distance_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
			Distance_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_2));
			Altitude_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
			Altitude_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
			Altitude_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_2));

			Altitude_Text_View.setVisibility(View.GONE);

			Poke_Button.setTypeface(UI_Core.Theme_Settings.Font_Bold);
			Poke_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
			Poke_Button.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_3));

			return Friend_View;
		}
	};

	public void Set_Friend_ID(int friend_id){ Friend_ID = friend_id; }

	public Screen_Function Load_Screen_Data = new Screen_Function()
	{
		@Override public void Execute()
		{
			DataHandler Data_Handler = UI_Core.FMF_Service.Service_Core.Data_Handler;
			Screen_Is_Visible = true;

			//UI_Core.FMF_Service.Service_Core.Location_Service_Handler.Set_Location_Request_Aggressive();

			Start_Sensor_Manager();
			Get_Friend_Data();

			User_Name_Text_View.setText(Data_Handler.Friends_Handler.Get_Friend_Name(Friend_ID));
			Profile_Pic_Image_View.setImageBitmap(Data_Handler.Pic_Handler.Get_Masked_Profile_Pic(Data_Handler.Pic_Handler.Get_Friend_Profile_Pic_Bitmap(Friend_ID),
			                                                                                      PROFILE_PIC_DIMENSION));

			User_No_Longer_In_Range_Text_View.setVisibility(View.GONE);
			User_Is_In_Range_Layout.setVisibility(View.VISIBLE);

			String text = "is " + String.valueOf(Distance_To_Friend) + " away";
			Distance_Text_View.setText(text);

			Set_Map_Markers();

			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			builder.include(User_Marker.getPosition());
			builder.include(Friend_Marker.getPosition());

			Google_Map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), UI_Core.Theme_Settings.DP(10)));
		}
	};

	private OnMapReadyCallback On_Map_Ready_Callback = new OnMapReadyCallback()
	{
		@Override public void onMapReady(GoogleMap map)
		{
			try
			{
				ViewGroup.LayoutParams params = Google_Map_Layout.getLayoutParams();
				params.width = Map_Dimensions;
				params.height = Map_Dimensions;
				Google_Map_Layout.setLayoutParams(params);

				Google_Map = map;

				Google_Map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				Google_Map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(-33.9237292, 18.4282663), 11, 0, 0)));
				Google_Map.setTrafficEnabled(false);
				Google_Map.setMyLocationEnabled(false);

				UiSettings settings = Google_Map.getUiSettings();
				settings.setAllGesturesEnabled(false);
				settings.setMapToolbarEnabled(false);
				settings.setCompassEnabled(true);
				settings.setMyLocationButtonEnabled(false);
				settings.setRotateGesturesEnabled(false);
				settings.setScrollGesturesEnabled(true);
				settings.setTiltGesturesEnabled(false);
				settings.setZoomControlsEnabled(false);
				settings.setZoomGesturesEnabled(true);
			}
			catch(SecurityException e)
			{
				System.err.println("Error in FriendHandler: On_Map_Ready_Callback: onMapReady:");
				System.err.println("Caught Security Exception: " + e.getMessage());
			}

			Log.d("Nic Says", "Friend_Google_Map initialized.");
		}
	};

	private void Set_Map_Markers()
	{
		int        marker_size         = 36;
		int        marker_border_size  = 4;
		PicHandler Pic_Handler = UI_Core.FMF_Service.Service_Core.Data_Handler.Pic_Handler;

		Bitmap Profile_Pic = Pic_Handler.Get_Masked_Profile_Pic(Pic_Handler.Get_User_Profile_Pic_Bitmap(), marker_size);

		User_Marker = Google_Map.addMarker(new MarkerOptions().position(new LatLng(UI_Core.FMF_Service.Service_Core.User_Latitude, UI_Core.FMF_Service.Service_Core.User_Longitude))
		                                                      .title("You")
		                                                      .icon(Get_Marker(Profile_Pic, marker_size, marker_border_size))
		                                                      .anchor(0.5f, 0.5f));

		Log.d("Nic Says", "Friend_ID to get Profile Pic: " + Friend_ID);

		Profile_Pic = Pic_Handler.Get_Masked_Profile_Pic(Pic_Handler.Get_Friend_Profile_Pic_Bitmap(Friend_ID), marker_size);

		Friend_Marker = Google_Map.addMarker(new MarkerOptions().position(new LatLng(Friend_Latitude, Friend_Longitude))
		                                                        .title(UI_Core.FMF_Service.Service_Core.Data_Handler.Friends_Handler.Get_Friend_Name(Friend_ID))
		                                                        .icon(Get_Marker(Profile_Pic, marker_size, marker_border_size))
		                                                        .anchor(0.5f, 0.5f));
	}

	private BitmapDescriptor Get_Marker(Bitmap Profile_Pic, int marker_size, int marker_border_size)
	{
		Bitmap Marker = null;

		try
		{
			Drawable drawable = ContextCompat.getDrawable(Base_Context, R.drawable.profile_pic_background);

			Marker = Bitmap.createBitmap(UI_Core.Theme_Settings.DP(marker_border_size + marker_size + marker_border_size),
			                             UI_Core.Theme_Settings.DP(marker_border_size + marker_size + marker_border_size), Bitmap.Config.ARGB_8888);

			Canvas canvas = new Canvas();
			canvas.setBitmap(Marker);
			drawable.setBounds(0, 0,
			                   UI_Core.Theme_Settings.DP(marker_border_size + marker_size + marker_border_size),
			                   UI_Core.Theme_Settings.DP(marker_border_size + marker_size + marker_border_size));
			drawable.draw(canvas);
			canvas.drawBitmap(Profile_Pic, UI_Core.Theme_Settings.DP(marker_border_size), UI_Core.Theme_Settings.DP(marker_border_size), null);
		}
		catch(NullPointerException e)
		{
			System.err.println("Error in FriendHandler: Get_Marker:");
			System.err.println("Caught Null Pointer Exception: " + e.getMessage());
		}

		return BitmapDescriptorFactory.fromBitmap(Marker);
	}

	private View.OnTouchListener On_Touch_Listener = new View.OnTouchListener()
	{
		@Override public boolean onTouch(View view, MotionEvent event)
		{
			switch(event.getActionMasked())
			{
				case MotionEvent.ACTION_DOWN:
					Poke_Button.setBackgroundResource(R.drawable.poke_selected);
					break;

				case MotionEvent.ACTION_UP:
					Poke_Button.setBackgroundResource(R.drawable.poke_unselected);

					try
					{
						JSONObject jsonSend = new JSONObject();

						jsonSend.accumulate("action", "Insert_Poke");
						jsonSend.accumulate("to_user_id", Friend_ID);
						jsonSend.accumulate("from_user_id", UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getInt("User_ID"));

						UI_Core.FMF_Service.Service_Core.Server_Handler.Queue_Server_Request(UI_Core.FMF_Service.Service_Core.SERVER_URL,
						                                                                     "gravity.php",
						                                                                     jsonSend,
						                                                                     UI_Core.FMF_Service.Service_Core.Server_Response_Handler);
					}
					catch(JSONException e)
					{
						System.err.println("Error in FriendHandler: On_Touch_Listener:");
						System.err.println("Caught JSON Exception: " + e.getMessage());
					}
					break;
			}

			return true;
		}
	};

	public Screen_Function Kill_Screen = new Screen_Function()
	{
		@Override public void Execute()
		{
			Screen_Is_Visible = false;

			//UI_Core.FMF_Service.Service_Core.Location_Service_Handler.Set_Location_Request_Passive();
			Stop_Sensor_Manager();

			UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Set_Friend_Item_Clickable_State(true);

			Google_Map.clear();
			Google_Map = null;

			FragmentActivity    Fragment_Activity    = (FragmentActivity) Base_Context;
			FragmentManager     Fragment_Manager     = Fragment_Activity.getSupportFragmentManager();
			FragmentTransaction Fragment_Transaction = Fragment_Manager.beginTransaction();
			Fragment_Transaction.remove(Support_Map_Fragment).commit();

			Support_Map_Fragment = null;

			Friend_ID = -1;
			Friend_View = null;

			User_No_Longer_In_Range_Text_View = null;
			User_Is_In_Range_Layout = null;

			Distance_Text_View = null;
			Profile_Pic_Image_View = null;
			User_Name_Text_View = null;
			Poke_Button = null;
		}
	};

	public SensorEventListener Sensor_Event_Listener = new SensorEventListener()
	{
		@Override public void onSensorChanged(SensorEvent Sensor_Event)
		{
			if(Sensor_Event.sensor == Accelerometer)
			{
				System.arraycopy(Sensor_Event.values, 0, Last_Accelerometer, 0, Sensor_Event.values.length);
				Last_Accelerometer_Set = true;
			}
			else
			{
				if(Sensor_Event.sensor == Magnetometer)
				{
					System.arraycopy(Sensor_Event.values, 0, Last_Magnetometer, 0, Sensor_Event.values.length);
					Last_Magnetometer_Set = true;
				}
			}

			if(Last_Accelerometer_Set && Last_Magnetometer_Set)
			{
				boolean Rotation_Matrix_Result = SensorManager.getRotationMatrix(Rotation_Matrix, Identity_Matrix, Last_Accelerometer, Last_Magnetometer);

				if(Rotation_Matrix_Result)
				{
					SensorManager.getOrientation(Rotation_Matrix, Orientation);

					if(Google_Map != null)
					{
						//Set Map Bearing.
						double camera_target_lat = (UI_Core.FMF_Service.Service_Core.User_Latitude + Friend_Latitude) / 2;
						double camera_target_lng = (UI_Core.FMF_Service.Service_Core.User_Longitude + Friend_Longitude) / 2;

						CameraPosition camera_position = new CameraPosition.Builder().bearing((float)Math.toDegrees(Orientation[0]))
						                                                             .target(new LatLng(camera_target_lat, camera_target_lng))
						                                                             .zoom(Google_Map.getCameraPosition().zoom)
						                                                             .build();

						Google_Map.animateCamera(CameraUpdateFactory.newCameraPosition(camera_position), 100, null);
					}
				}
			}
		}

		@Override public void onAccuracyChanged(Sensor sensor, int accuracy){}
	};

	public void Start_Sensor_Manager()
	{
		if(Sensor_Manager != null)
		{
			Sensor_Manager.registerListener(Sensor_Event_Listener, Accelerometer, SensorManager.SENSOR_DELAY_UI);
			Sensor_Manager.registerListener(Sensor_Event_Listener, Magnetometer, SensorManager.SENSOR_DELAY_UI);
		}
	}

	public void Stop_Sensor_Manager()
	{
		if(Sensor_Manager != null) Sensor_Manager.unregisterListener(Sensor_Event_Listener);
	}

	public void Update_Friend_UI()
	{
		try
		{
			int altitude_difference = (int)Math.round(Friend_Altitude - UI_Core.FMF_Service.Service_Core.User_Altitude);

			if(Math.abs(altitude_difference) > 2)
			{
				String altitude_notification;

				if(UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System").equals("Feet"))
					altitude_difference = (int)Math.round(altitude_difference *  3.28084);

				if(altitude_difference > 0) altitude_notification = " above you"; else altitude_notification = " below you";

				String text = "and " +
				              String.valueOf(Math.abs(altitude_difference)) +
				              " " + UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System") +
				              altitude_notification;

				Altitude_Text_View.setText(text);

				if(Altitude_Text_View.getVisibility() == View.GONE) Altitude_Text_View.setVisibility(View.VISIBLE);
			}
			else if(Altitude_Text_View.getVisibility() == View.VISIBLE) Altitude_Text_View.setVisibility(View.GONE);

			String text = "is " + String.valueOf(Distance_To_Friend) + " " +
			              UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System") + " away";

			Distance_Text_View.setText(text);

			JSONObject jsonSend = new JSONObject();

			jsonSend.accumulate("action", "Get_Friend_Data");
			jsonSend.accumulate("friend_id", UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Friend_Handler.Friend_ID);
			jsonSend.accumulate("user_latitude", UI_Core.FMF_Service.Service_Core.User_Latitude);
			jsonSend.accumulate("user_longitude", UI_Core.FMF_Service.Service_Core.User_Longitude);
			jsonSend.accumulate("measurement_system", UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System"));

			UI_Core.FMF_Service.Service_Core.Server_Handler.Queue_Server_Request(UI_Core.FMF_Service.Service_Core.SERVER_URL,
			                                                                     "find_my_friend.php",
			                                                                     jsonSend,
			                                                                     UI_Core.FMF_Service.Service_Core.Server_Response_Handler);
		}
		catch(JSONException e)
		{
			System.err.println("Error in Users: Update_Friend_UI:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	private void Get_Friend_Data()
	{
		try
		{
			JSONArray Friends_In_Range = UI_Core.FMF_Service.Service_Core.Data_Handler.Friends_Handler.Friends_In_Range;

			Log.d("Nic Says", "Get_Friend_Data: Friend_ID: " + Friend_ID);

			for(int i = 0; i < Friends_In_Range.length(); i++)
			{
				if(Screen_Is_Visible)
				{
					if(Friend_ID == Friends_In_Range.getJSONObject(i).getInt("fmf_profile_id"))
					{
						Distance_To_Friend = Friends_In_Range.getJSONObject(i).getInt("distance");
						Friend_Latitude    = Friends_In_Range.getJSONObject(i).getDouble("latitude");
						Friend_Longitude   = Friends_In_Range.getJSONObject(i).getDouble("longitude");
						Friend_Altitude    = Friends_In_Range.getJSONObject(i).getDouble("altitude");

						Log.d("Nic Says", "Get_Friend_Data: Distance_To_Friend: " + Distance_To_Friend);
						Log.d("Nic Says", "Get_Friend_Data: Friend_Latitude:    " + Friend_Latitude);
						Log.d("Nic Says", "Get_Friend_Data: Friend_Longitude:   " + Friend_Longitude);
						Log.d("Nic Says", "Get_Friend_Data: Friend_Altitude:    " + Friend_Altitude);
					}
				}
			}

			Update_Friend_UI();
		}
		catch(JSONException e)
		{
			System.err.println("Error in FriendHandler: Get_Friend_Data:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}
}
