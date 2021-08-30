package com.unscarred.find_my_friend.UI;

import com.unscarred.find_my_friend.R;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FriendsTabHandler
{
	private Context         Base_Context;
	public  UICore          UI_Core;
	public  FriendHandler   Friend_Handler;
	public  BlockedFriendsUIHandler Blocked_User_UI_Handler;

	public  View            Friends_Tab_View;
	public  TextView        Friends_Layout_Header;
	public  ImageView       Add_Friends_Button;
	public  ScrollView      Friends_Scroll_View;
	public  LinearLayout    Friend_Items_Layout;

	private boolean         Friend_Items_Update_Locked = false;
	private final int       ACTION_DOWN_ANIMATION_DURATION = 1000;
	private final int       ACTION_UP_ANIMATION_DURATION = 200;


	public FriendsTabHandler(Context context, UICore core)
	{
		Base_Context = context;
		UI_Core = core;
		Friend_Handler = new FriendHandler(Base_Context, UI_Core);
		Blocked_User_UI_Handler = new BlockedFriendsUIHandler(Base_Context, UI_Core);
	}

	public void Load_Screen()
	{
		LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Friends_Tab_View = layoutInflater.inflate(R.layout.friends_tab, null);

		Friends_Layout_Header = (TextView)Friends_Tab_View.findViewById(R.id.friends_tab_header_text_view);
		Add_Friends_Button = (ImageView)Friends_Tab_View.findViewById(R.id.friends_tab_add_image_view);
		Friends_Scroll_View = (ScrollView)Friends_Tab_View.findViewById(R.id.friends_tab_scroll_view);
		Friend_Items_Layout = (LinearLayout)Friends_Tab_View.findViewById(R.id.friends_tab_items_layout);

		Update_Friends();
	}

	public void Update_Friends()
	{
		if(!Friend_Items_Update_Locked)
		{
			int range = UI_Core.Tab_Layout_Handler.Get_Seek_Bar_Range();

			Draw_Friends_List();
			Update_Friend_Distances();

			Friend_Items_Update_Locked = false;
			if(UI_Core.Tab_Layout_Handler.Get_Seek_Bar_Range() != range) Update_Friends();
		}
	}

	public void Draw_Friends_List()
	{
		try
		{
			JSONArray Friends_In_Range = UI_Core.FMF_Service.Service_Core.Data_Handler.Friends_Handler.Friends_In_Range;

			Friend_Items_Layout.removeAllViews();

			for(int i = 0; i < Friends_In_Range.length(); i++)
				Friend_Items_Layout.addView(Create_Friend_Item(Friends_In_Range.getJSONObject(i)));
		}
		catch(JSONException e)
		{
			System.err.println("Error in FriendsTabHandler: Draw_Friends_List:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	public void Update_Friend_Distances()
	{
		try
		{
			JSONArray Friends_In_Range = UI_Core.FMF_Service.Service_Core.Data_Handler.Friends_Handler.Friends_In_Range;
			FriendHandler Friend_Handler = UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Friend_Handler;


			for(int i = 0; i < Friends_In_Range.length(); i++)
			{
				if(Friend_Handler.Screen_Is_Visible)
				{
					if(Friend_Handler.Friend_ID == Friends_In_Range.getJSONObject(i).getInt("profile_id"))
					{
						String distance = String.valueOf(Friends_In_Range.getJSONObject(i).getInt("distance"));

						String text = "is " + distance + " " + UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System") + " away";
						Friend_Handler.Distance_Text_View.setText(text);

						Friend_Handler.Distance_To_Friend  = Friends_In_Range.getJSONObject(i).getInt("distance");
						Friend_Handler.Friend_Latitude     = Friends_In_Range.getJSONObject(i).getDouble("latitude");
						Friend_Handler.Friend_Longitude    = Friends_In_Range.getJSONObject(i).getDouble("longitude");
						Friend_Handler.Friend_Altitude     = Friends_In_Range.getJSONObject(i).getDouble("altitude");
					}
				}

				String tag = "Friend_Distance_" + String.valueOf(Friends_In_Range.getJSONObject(i).getInt("profile_id"));

				String distance = String.valueOf(Friends_In_Range.getJSONObject(i).getInt("distance"));
				TextView Text_View = (TextView)Friend_Items_Layout.findViewWithTag(tag);

				String text = distance + " " + UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System");
				if(Text_View != null) Text_View.setText(text);
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in FriendsTabHandler: Update_Friend_Distances:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	public void Clear_Friend_Items_Layout(){ Friend_Items_Layout.removeAllViews(); }

	public LinearLayout Create_Friend_Item(JSONObject Friend)
	{
		LinearLayout Main_Linear_Layout = new LinearLayout(Base_Context);

		try
		{
			Main_Linear_Layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			Main_Linear_Layout.setOrientation(LinearLayout.HORIZONTAL);
			Main_Linear_Layout.setPadding(UI_Core.Theme_Settings.DP(5), UI_Core.Theme_Settings.DP(5), UI_Core.Theme_Settings.DP(5), UI_Core.Theme_Settings.DP(5));
			Main_Linear_Layout.setTag("Friend_" + Friend.getString("profile_id"));
			Main_Linear_Layout.setOnTouchListener(UI_On_Touch_Listener);


			ImageView Profile_Image_View = new ImageView(Base_Context);
			Profile_Image_View.setLayoutParams(new LinearLayout.LayoutParams(UI_Core.Theme_Settings.DP(50), UI_Core.Theme_Settings.DP(50)));
			Profile_Image_View.setBackgroundResource(R.drawable.profile_pic_background);
			Profile_Image_View.setScaleType(ImageView.ScaleType.FIT_CENTER);
			Profile_Image_View.setTag("Friend_Profile_Pic_" + Friend.getString("profile_id"));

			LinearLayout Profile_Pic_Background = new LinearLayout(Base_Context);
			Profile_Pic_Background.setLayoutParams(new LinearLayout.LayoutParams(UI_Core.Theme_Settings.DP(54), UI_Core.Theme_Settings.DP(54)));
			Profile_Pic_Background.setBackgroundResource(R.drawable.profile_pic_background);
			Profile_Pic_Background.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);

			Profile_Pic_Background.addView(Profile_Image_View);

			Profile_Image_View.setImageBitmap(UI_Core.FMF_Service.Service_Core.Data_Handler.Pic_Handler
					                                  .Get_Masked_Profile_Pic(UI_Core.FMF_Service.Service_Core.Data_Handler.Pic_Handler
							                                                          .Get_Friend_Profile_Pic_Bitmap(Friend.getInt("profile_id")), 50));

			TextView Profile_Text_View = new TextView(Base_Context);
			Profile_Text_View.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, UI_Core.Theme_Settings.DP(50)));
			Profile_Text_View.setGravity(Gravity.CENTER_VERTICAL);
			Profile_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
			Profile_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_2));
			Profile_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
			String Friend_Name = Friend.getString("name") + " " + Friend.getString("surname");
			Profile_Text_View.setText(Friend_Name);
			Profile_Text_View.setTag("Friend_Name_" +  Friend.getString("profile_id"));

			LinearLayout.LayoutParams Profile_Text_Layout_Params = (LinearLayout.LayoutParams)Profile_Text_View.getLayoutParams();
			Profile_Text_Layout_Params.setMargins(UI_Core.Theme_Settings.DP(20), 0, 0, 0);
			Profile_Text_View.setLayoutParams(Profile_Text_Layout_Params);

			TextView Distance_Text_View = new TextView(Base_Context);
			Distance_Text_View.setLayoutParams(new LinearLayout.LayoutParams(0, UI_Core.Theme_Settings.DP(50), 1));
			Distance_Text_View.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
			Distance_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
			Distance_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_2));
			Distance_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);

			String text = Friend.getString("distance") + " " + UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System");
			Distance_Text_View.setText(text);

			Distance_Text_View.setTag("Friend_Distance_" + Friend.getString("profile_id"));

			Main_Linear_Layout.addView(Profile_Pic_Background);
			Main_Linear_Layout.addView(Profile_Text_View);
			Main_Linear_Layout.addView(Distance_Text_View);
		}
		catch(JSONException e)
		{
			System.err.println("Error in FriendsTabHandler: Create_Friend_Item:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}

		return Main_Linear_Layout;
	}

	private boolean run_action_up_event = true;
	public  Handler UI_On_Touch_Handler = new Handler();

	private Runnable UI_On_Touch_Runnable = new Runnable()
	{
		@Override public void run()
		{
			run_action_up_event = false;
			UI_Core.Tab_Layout_Handler.Value_Animator_Down.cancel();
			UI_Core.Tab_Layout_Handler.Value_Animator_Up.cancel();
			UI_On_Touch_View.clearAnimation();
			UI_On_Touch_View.setBackgroundColor(Color.parseColor("#00DDDDDD"));

			UI_Core.Theme_Settings.Vibe.vibrate(50);

			String[] Action_Details = UI_On_Touch_View.getTag().toString().split("_");
			int user_id = Integer.parseInt(Action_Details[1]);

			Blocked_User_UI_Handler.Show_Confirm_Block_User_Popup(user_id);
		}
	};

	public View UI_On_Touch_View;
	float  UI_Initial_X;
	float  UI_Initial_Y;

	public View.OnTouchListener UI_On_Touch_Listener = new View.OnTouchListener()
	{
		@Override public boolean onTouch(View view, MotionEvent event)
		{
			UI_On_Touch_View = view;
			float Final_X = event.getRawX();
			float Final_Y = event.getRawY();
			float Delta_Y = Final_Y - UI_Initial_Y;

			switch(event.getActionMasked())
			{
				case MotionEvent.ACTION_DOWN:
					UI_Initial_X = event.getRawX();
					UI_Initial_Y = event.getRawY();

					if(view.getId() != R.id.main_button_layout)
					{
						Friends_Scroll_View.requestDisallowInterceptTouchEvent(true);

						UI_Core.Tab_Layout_Handler.Value_Animator_Down.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
						{
							@Override public void onAnimationUpdate(ValueAnimator animator){ UI_On_Touch_View.setBackgroundColor((Integer)animator.getAnimatedValue()); }
						});
						UI_Core.Tab_Layout_Handler.Value_Animator_Down.start();

						UI_On_Touch_Handler.postDelayed(UI_On_Touch_Runnable, ACTION_DOWN_ANIMATION_DURATION);
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if(Math.abs(Delta_Y) > 50)
					{
						UI_On_Touch_Handler.removeCallbacks(UI_On_Touch_Runnable);
						UI_Core.Tab_Layout_Handler.Value_Animator_Down.cancel();

						Friends_Scroll_View.requestDisallowInterceptTouchEvent(false);
					}
					break;

				case MotionEvent.ACTION_UP:
					if(run_action_up_event)
					{
						UI_On_Touch_Handler.removeCallbacks(UI_On_Touch_Runnable);
						UI_Core.Tab_Layout_Handler.Value_Animator_Down.cancel();

						UI_Core.Tab_Layout_Handler.Value_Animator_Up.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
						{
							@Override public void onAnimationUpdate(ValueAnimator animator){ UI_On_Touch_View.setBackgroundColor((Integer)animator.getAnimatedValue()); }
						});
						UI_Core.Tab_Layout_Handler.Value_Animator_Up.start();

						UI_On_Touch_Handler.postDelayed(new Runnable()
						{
							@Override public void run()
							{
								UI_Core.Tab_Layout_Handler.Value_Animator_Up.cancel();
								UI_On_Touch_View.setBackgroundColor(Color.parseColor("#00DDDDDD"));

								String[] Action_Details = UI_On_Touch_View.getTag().toString().split("_");
								int friend_id = Integer.parseInt(Action_Details[1]);

								Friend_Handler.Set_Friend_ID(friend_id);
								UI_Core.Screen_Handler.Show_Screen("Friend", UI_Core.Main_Frame_Layout, "Left_In", "Right_Out",
								                                   Friend_Handler.Load_Screen, Friend_Handler.Load_Screen_Data, Friend_Handler.Kill_Screen);
							}
						}, ACTION_UP_ANIMATION_DURATION);
					}
					else run_action_up_event = true;
					break;
			}

			return true;
		}
	};

	public View.OnClickListener On_Click_Listener = new View.OnClickListener()
	{
		@Override public void onClick(View view)
		{
			String[] Action_Details;
			int      friend_id = -1;
			String   action  = "null";

			if(view.getTag() != null)
			{
				Action_Details = view.getTag().toString().split("_");
				friend_id = Integer.parseInt(Action_Details[1]);
				action = Action_Details[0];
			}

			Friend_Handler.Set_Friend_ID(friend_id);

			UI_Core.Screen_Handler.Show_Screen("Friend", UI_Core.Main_Frame_Layout, "Left_In", "Right_Out",
			                                   Friend_Handler.Load_Screen, Friend_Handler.Load_Screen_Data, Friend_Handler.Kill_Screen);

			if(action.equals("Unblock")) Blocked_User_UI_Handler.Unblock_User(friend_id);
		}
	};

	public void Set_Friend_Item_Clickable_State(boolean state)
	{
		try
		{
			JSONArray Friends_In_Range = UI_Core.FMF_Service.Service_Core.Data_Handler.Friends_Handler.Friends_In_Range;
			LinearLayout Friend_Item;

			for(int i = 0; i < Friends_In_Range.length(); i++)
			{
				Friend_Item = (LinearLayout)Friend_Items_Layout.findViewWithTag("Friend_" + String.valueOf(Friends_In_Range.getJSONObject(i).getInt("profile_id")));
				if(Friend_Item != null) Friend_Item.setClickable(state);
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in FriendsTabHandler: Set_Friend_Item_Clickable_State:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}
}
