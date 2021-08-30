package com.unscarred.find_my_friend.UI;

import com.unscarred.find_my_friend.R;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;


public class BlockedFriendsUIHandler
{
	private UICore          UI_Core;
	private Context         Base_Context;

	private PopupWindow     Confirm_Block_User_Popup_Window;
	private View            Confirm_Block_User_Popup_Window_View;


	public BlockedFriendsUIHandler(Context context, UICore core)
	{
		Base_Context = context;
		UI_Core = core;
	}

	public void Show_Confirm_Block_User_Popup(int User_ID)
	{
		LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Confirm_Block_User_Popup_Window_View = layoutInflater.inflate(R.layout.confirm_block, null);
		Confirm_Block_User_Popup_Window_View.setOnTouchListener(Blocked_User_Popup_On_Touch_Listener);
		Confirm_Block_User_Popup_Window = new PopupWindow(Confirm_Block_User_Popup_Window_View, LinearLayout.LayoutParams.MATCH_PARENT,
		                                                  LinearLayout.LayoutParams.MATCH_PARENT, true);
		Confirm_Block_User_Popup_Window.setAnimationStyle(android.R.style.Animation_Dialog);

		ImageView Profile_Pic_Image_View = (ImageView)Confirm_Block_User_Popup_Window_View.findViewById(R.id.confirm_block_profile_pic_image_view);
		TextView Profile_Name_Text_View = (TextView)Confirm_Block_User_Popup_Window_View.findViewById(R.id.confirm_block_prompt_text_view);
		TextView Yes_Text_View = (TextView)Confirm_Block_User_Popup_Window_View.findViewById(R.id.confirm_block_yes_label);
		TextView No_Text_View = (TextView)Confirm_Block_User_Popup_Window_View.findViewById(R.id.confirm_block_no_label);
		TextView Swipe_Text_View = (TextView)Confirm_Block_User_Popup_Window_View.findViewById(R.id.confirm_block_swipe_label);

		Profile_Name_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Bold);
		Profile_Name_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE + 4);
		Profile_Name_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_2));

		Yes_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Bold);
		Yes_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE + 4);
		Yes_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_3));

		No_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Bold);
		No_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE + 4);
		No_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_3));

		Swipe_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
		Swipe_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE - 4);
		Swipe_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_3));

		String Friend_Name = UI_Core.FMF_Service.Service_Core.Data_Handler.Friends_Handler.Get_Friend_Name(User_ID);
		String text = "Block " + Friend_Name + "?";
		Profile_Name_Text_View.setText(text);

		Profile_Pic_Image_View.setImageBitmap(UI_Core.FMF_Service.Service_Core.Data_Handler.Pic_Handler
						                .Get_Masked_Profile_Pic(UI_Core.FMF_Service.Service_Core.Data_Handler.Pic_Handler.Get_Friend_Profile_Pic_Bitmap(User_ID),
						                                        UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Friend_Handler.PROFILE_PIC_DIMENSION));

		Confirm_Block_User_Popup_Window.showAtLocation(Confirm_Block_User_Popup_Window_View, Gravity.CENTER, 0, 0);
	}

	float Blocked_User_Popup_Swipe_Initial_X;
	float Blocked_User_Popup_Swipe_Initial_Y;
	float Blocked_User_Popup_Window_Initial_X;
	float Blocked_User_Popup_Window_Initial_Y;
	long  Blocked_User_Popup_Window_Initial_Time;

	public View.OnTouchListener Blocked_User_Popup_On_Touch_Listener = new View.OnTouchListener()
	{
		@Override public boolean onTouch(View view, MotionEvent event)
		{
			int action = event.getActionMasked();
			float Final_X = event.getX();
			float Final_Y = event.getY();
			long  Final_Time = System.currentTimeMillis();

			float Delta_X = Final_X - Blocked_User_Popup_Swipe_Initial_X;
			float Delta_Y = Final_Y - Blocked_User_Popup_Swipe_Initial_Y;
			float Delta_Time = Final_Time - Blocked_User_Popup_Window_Initial_Time;
			float Delta_Distance = (float)Math.sqrt((Final_X - Blocked_User_Popup_Swipe_Initial_X) * (Final_X - Blocked_User_Popup_Swipe_Initial_X) +
			                                        (Final_Y - Blocked_User_Popup_Swipe_Initial_Y) * (Final_Y - Blocked_User_Popup_Swipe_Initial_Y));

			float Velocity = Delta_Distance / Delta_Time;
			float space_left;
			int duration;

			switch(action)
			{
				case MotionEvent.ACTION_DOWN:
					Blocked_User_Popup_Swipe_Initial_X = event.getX();
					Blocked_User_Popup_Swipe_Initial_Y = event.getY();
					Blocked_User_Popup_Window_Initial_X = Confirm_Block_User_Popup_Window_View.getX();
					Blocked_User_Popup_Window_Initial_Y = Confirm_Block_User_Popup_Window_View.getY();
					Blocked_User_Popup_Window_Initial_Time = System.currentTimeMillis();
					break;

				case MotionEvent.ACTION_MOVE:
					Confirm_Block_User_Popup_Window_View.setX(Delta_X + Blocked_User_Popup_Window_Initial_X);

					if(Confirm_Block_User_Popup_Window_View.getX() < 0)
						space_left = Confirm_Block_User_Popup_Window_View.getX() + Confirm_Block_User_Popup_Window_View.getWidth();
					else
						space_left = UI_Core.Theme_Settings.Screen_Width - Confirm_Block_User_Popup_Window_View.getX();

					Confirm_Block_User_Popup_Window_View.setAlpha(space_left / UI_Core.Theme_Settings.Screen_Width);
					break;

				case MotionEvent.ACTION_UP:
					if(Blocked_User_Popup_Swipe_Initial_X < Final_X && Math.abs(Delta_Y) < UI_Core.Tab_Layout_Handler.SWIPE_THRESHOLD) // Left to Right swipe
					{
						duration = Math.round((UI_Core.Theme_Settings.Screen_Width - Confirm_Block_User_Popup_Window_View.getX()) / Velocity);
						space_left = UI_Core.Theme_Settings.Screen_Width - Confirm_Block_User_Popup_Window_View.getX();

						ObjectAnimator Object_Animator_Translation = ObjectAnimator.ofFloat(Confirm_Block_User_Popup_Window_View,
						                                                                    "translationX",
						                                                                    Confirm_Block_User_Popup_Window_View.getX(),
						                                                                    UI_Core.Theme_Settings.Screen_Width);
						ObjectAnimator Object_Animator_Alpha = ObjectAnimator.ofFloat(Confirm_Block_User_Popup_Window_View,
						                                                              "alpha",
						                                                              space_left / UI_Core.Theme_Settings.Screen_Width, 0);

						AnimatorSet Animator_Set = new AnimatorSet();
						Animator_Set.setDuration(duration);
						Animator_Set.playTogether(Object_Animator_Translation, Object_Animator_Alpha);
						Animator_Set.start();

						UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.UI_On_Touch_Handler.postDelayed(new Runnable(){ @Override public void run(){ Confirm_Block_User_Popup_Window.dismiss(); } },
						                                                         duration);
					}

					if(Blocked_User_Popup_Swipe_Initial_X > Final_X && Math.abs(Delta_Y) < UI_Core.Tab_Layout_Handler.SWIPE_THRESHOLD) // Right to Left swipe
					{
						duration = Math.round((Confirm_Block_User_Popup_Window_View.getWidth() + Confirm_Block_User_Popup_Window_View.getX()) / Velocity);
						space_left = Confirm_Block_User_Popup_Window_View.getX() + Confirm_Block_User_Popup_Window_View.getWidth();

						ObjectAnimator Object_Animator_Translation = ObjectAnimator.ofFloat(Confirm_Block_User_Popup_Window_View,
						                                                                    "translationX",
						                                                                    Confirm_Block_User_Popup_Window_View.getX(),
						                                                                    UI_Core.Theme_Settings.Screen_Width * -1);
						ObjectAnimator Object_Animator_Alpha = ObjectAnimator.ofFloat(Confirm_Block_User_Popup_Window_View,
						                                                              "alpha",
						                                                              space_left / UI_Core.Theme_Settings.Screen_Width, 0);

						AnimatorSet Animator_Set = new AnimatorSet();
						Animator_Set.setDuration(duration);
						Animator_Set.playTogether(Object_Animator_Translation, Object_Animator_Alpha);
						Animator_Set.start();

						UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.UI_On_Touch_Handler.postDelayed(new Runnable()
						{
							@Override public void run()
							{
								String[] User_Details = UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.UI_On_Touch_View.getTag().toString().split("_");
								int user_id = Integer.parseInt(User_Details[1]);

								Block_User(user_id);
								Confirm_Block_User_Popup_Window.dismiss();
							}
						}, duration);
					}

					if(Blocked_User_Popup_Swipe_Initial_Y < Final_Y && Math.abs(Delta_X) > UI_Core.Tab_Layout_Handler.SWIPE_THRESHOLD)
					{
						Log.d("Nic Says", "Up to Down swipe performed");
					}

					if(Blocked_User_Popup_Swipe_Initial_Y > Final_Y && Math.abs(Delta_X) > UI_Core.Tab_Layout_Handler.SWIPE_THRESHOLD)
					{
						Log.d("Nic Says", "Down to Up swipe performed");
					}

					break;

				case MotionEvent.ACTION_CANCEL:
					Log.d("Nic Says","Action was CANCEL");
					break;

				case MotionEvent.ACTION_OUTSIDE:
					Log.d("Nic Says", "Movement occurred outside bounds of current screen element");
					break;
			}

			return true;
		}
	};

	private void Block_User(int blocked_user_id)
	{
		try
		{
			JSONObject jsonSend = new JSONObject();

			jsonSend.accumulate("action", "Insert_Blocked_User");
			jsonSend.accumulate("user_id", UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getInt("User_ID"));
			jsonSend.accumulate("blocked_user_id", blocked_user_id);

			UI_Core.FMF_Service.Service_Core.Server_Handler.Queue_Server_Request(UI_Core.FMF_Service.Service_Core.SERVER_URL,
			                                                                     "find_my_friend.php",
			                                                                     jsonSend,
			                                                                     UI_Core.FMF_Service.Service_Core.Server_Response_Handler);

			JSONObject Blocked_User = new JSONObject();

			Blocked_User.accumulate("blocked_user_id", blocked_user_id);

			//UI_Core.Friends_UI_Handler.Blocked_Users.put(Blocked_User);

			LinearLayout Friend_Item = (LinearLayout)UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Friend_Items_Layout.findViewWithTag("Friend_" + blocked_user_id);

			if(Friend_Item != null) ((ViewGroup)Friend_Item.getParent()).removeView(Friend_Item);
		}
		catch(JSONException e)
		{
			System.err.println("Error in BlockedFriendsUIHandler: Block_User():");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	public void Unblock_User(int Blocked_User_ID)
	{
		try
		{
			JSONObject jsonSend = new JSONObject();

			jsonSend.accumulate("action", "Delete_Blocked_User");
			jsonSend.accumulate("user_id", UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getInt("User_ID"));
			jsonSend.accumulate("blocked_user_id", Blocked_User_ID);

			UI_Core.FMF_Service.Service_Core.Server_Handler.Queue_Server_Request(UI_Core.FMF_Service.Service_Core.SERVER_URL,
			                                                                     "find_my_friend.php",
			                                                                     jsonSend,
			                                                                     UI_Core.FMF_Service.Service_Core.Server_Response_Handler);

			LinearLayout Linear_Layout = (LinearLayout)UI_Core.Settings_Handler.Privacy_Settings_Handler.Privacy_Settings_View
					.findViewWithTag("Blocked_" + String.valueOf(Blocked_User_ID));
			if(Linear_Layout != null) ((ViewGroup)Linear_Layout.getParent()).removeView(Linear_Layout);

			//boolean blocked_user_not_found = true;
			/*
			for(int i = 0; i < UI_Core.Users_UI_Handler.Blocked_Users.length() && blocked_user_not_found; i++)
			{
				if(UI_Core.Users_UI_Handler.Blocked_Users.getJSONObject(i).getInt("blocked_user_id") == Blocked_User_ID)
				{
					UI_Core.Users_UI_Handler.Blocked_Users.remove(i);
					blocked_user_not_found = false;
				}
			}

			UI_Core.FMF_Service.Service_Core.Users_Handler.Get_Friends();
			*/
		}
		catch(JSONException e)
		{
			System.err.println("Error in BlockedFriendsUIHandler: Unlock_User");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	public LinearLayout Create_Blocked_User_Item(final int User_ID, String Friend_Name)
	{
		LinearLayout Main_Linear_Layout = new LinearLayout(Base_Context);
		Main_Linear_Layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		Main_Linear_Layout.setOrientation(LinearLayout.HORIZONTAL);
		Main_Linear_Layout.setGravity(Gravity.CENTER_VERTICAL);
		Main_Linear_Layout.setPadding(UI_Core.Theme_Settings.DP(5), UI_Core.Theme_Settings.DP(5), UI_Core.Theme_Settings.DP(5), UI_Core.Theme_Settings.DP(5));
		Main_Linear_Layout.setTag("Blocked_" + User_ID);

		ImageView Profile_Image_View = new ImageView(Base_Context);
		Profile_Image_View.setLayoutParams(new LinearLayout.LayoutParams(UI_Core.Theme_Settings.DP(50), UI_Core.Theme_Settings.DP(50)));
		Profile_Image_View.setBackgroundResource(R.drawable.profile_pic_background);
		Profile_Image_View.setScaleType(ImageView.ScaleType.FIT_CENTER);
		Profile_Image_View.setTag("Blocked_Profile_Pic_" + String.valueOf(User_ID));

		LinearLayout Profile_Pic_Background = new LinearLayout(Base_Context);
		Profile_Pic_Background.setLayoutParams(new LinearLayout.LayoutParams(UI_Core.Theme_Settings.DP(54), UI_Core.Theme_Settings.DP(54)));
		Profile_Pic_Background.setBackgroundResource(R.drawable.profile_pic_background);
		Profile_Pic_Background.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);

		Profile_Pic_Background.addView(Profile_Image_View);

		Profile_Image_View.setImageBitmap(UI_Core.FMF_Service.Service_Core.Data_Handler.Pic_Handler
				                                  .Get_Masked_Profile_Pic(UI_Core.FMF_Service.Service_Core.Data_Handler.Pic_Handler.Get_Friend_Profile_Pic_Bitmap(User_ID), 50));

		TextView Profile_Text_View = new TextView(Base_Context);
		Profile_Text_View.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
		Profile_Text_View.setGravity(Gravity.CENTER_VERTICAL);
		Profile_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		Profile_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_2));
		Profile_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
		Profile_Text_View.setText(Friend_Name);
		Profile_Text_View.setTag("Blocked_Name_" +  String.valueOf(User_ID));

		LinearLayout.LayoutParams Profile_Text_Layout_Params = (LinearLayout.LayoutParams)Profile_Text_View.getLayoutParams();
		Profile_Text_Layout_Params.setMargins(UI_Core.Theme_Settings.DP(20), 0, 0, 0);
		Profile_Text_View.setLayoutParams(Profile_Text_Layout_Params);

		TextView Button_Text_View = new TextView(Base_Context);
		Button_Text_View.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		Button_Text_View.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		Button_Text_View.setPadding(UI_Core.Theme_Settings.DP(10), UI_Core.Theme_Settings.DP(10), UI_Core.Theme_Settings.DP(10), UI_Core.Theme_Settings.DP(10));
		Button_Text_View.setBackgroundColor(ContextCompat.getColor(Base_Context, R.color.color_3));
		Button_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_4));
		Button_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE + 2);
		Button_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Light);
		String text =  "Unblock";
		Button_Text_View.setText(text);
		Button_Text_View.setTag("Unblock_" + String.valueOf(User_ID));
		Button_Text_View.setOnClickListener(UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.On_Click_Listener);


		Main_Linear_Layout.addView(Profile_Pic_Background);
		Main_Linear_Layout.addView(Profile_Text_View);
		Main_Linear_Layout.addView(Button_Text_View);

		return Main_Linear_Layout;
	}
}
