package com.unscarred.find_my_friend.UI;

import com.unscarred.find_my_friend.R;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PokeUIHandler
{
	private UICore          UI_Core;
	private Context         Base_Context;

	private View            Poke_UI_On_Touch_View;

	public boolean          Poke_Alert_Popup_Is_Visible;
	private ArrayList<PokeAlertPopupWindow> Poke_Alert_Popup_Window_Array = new ArrayList<>();


	public PokeUIHandler(Context context, UICore core)
	{
		Base_Context = context;
		UI_Core = core;
	}

	public void Add_Poke_Alert_Popup_Window(int User_ID)
	{
		Poke_Alert_Popup_Window_Array.add(new PokeAlertPopupWindow(Base_Context, UI_Core, User_ID));
	}

	public void Remove_Poke_Alert_Popup_Window(int User_ID)
	{
		int index = -1;
		boolean not_found = true;

		for(int i = 0; i < Poke_Alert_Popup_Window_Array.size() && not_found; i++)
		{
			if(Poke_Alert_Popup_Window_Array.get(i).Get_User_ID() == User_ID)
			{
				not_found = false;
				index = i;
			}
		}

		Poke_Alert_Popup_Window_Array.remove(index);

		if(Poke_Alert_Popup_Window_Array.size() == 0) Poke_Alert_Popup_Is_Visible = false;
	}

	public ImageView Get_Poke_Alert_PP_Image_View(int User_ID)
	{
		boolean not_found = true;
		ImageView Image_View = null;

		for(int i = 0; i < Poke_Alert_Popup_Window_Array.size() && not_found; i++)
		{
			if(Poke_Alert_Popup_Window_Array.get(i).Get_User_ID() == User_ID)
			{
				not_found = false;

				Image_View = (ImageView)Poke_Alert_Popup_Window_Array.get(i).Get_Popup_Window_View().findViewWithTag("Poke_Alert_Pic_" + User_ID);
			}
		}

		return Image_View;
	}

	public void Draw_Poke_List()
	{
		Log.d("Nic Says", "Draw_Poke_List HAS RUN, BUT it's all commented out.");

		/*
		try
		{
			UI_Core.Friends_UI_Handler.Pokes_Layout_View.removeAllViews();

			JSONArray Temp_Poke_List = UI_Core.FMF_Service.Service_Core.Friends_Handler.Poke_List;
			int index_j;
			boolean friend_not_found;

			for(int i = 0; i < Temp_Poke_List.length(); i++)
			{
				index_j = -1;
				friend_not_found = true;

				for(int j = 0; j < Temp_Facebook_Friends.length() && friend_not_found; j++)
				{
					if(Temp_Facebook_Friends.getJSONObject(j).getString("id").equals(Temp_Poke_List.getJSONObject(i).getString("from_facebook_id")))
					{
						index_j = j;
						friend_not_found = false;
					}
				}

				if(!friend_not_found)
				{
					SimpleDateFormat Date_Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					long date_of_poke_in_milli = Date_Format.parse(Temp_Poke_List.getJSONObject(i).getString("date_and_time")).getTime();
					long current_date_in_milli = Calendar.getInstance().getTimeInMillis();

					long diff_in_milli = current_date_in_milli - date_of_poke_in_milli;

					long Seconds = diff_in_milli / 1000 % 60;
					long Minutes = diff_in_milli / (60 * 1000) % 60;
					long Hours = diff_in_milli / (60 * 60 * 1000) % 24;
					long Days = diff_in_milli / (24 * 60 * 60 * 1000);

					String Time_Difference = "";

					if(Days != 0)       Time_Difference += Days + "D ";
					if(Hours != 0)      Time_Difference += Hours + "H ";
					if(Minutes != 0)    Time_Difference += Minutes + "M ";
					if(Seconds != 0)    Time_Difference += Seconds + "S";

					Time_Difference += " ago";

					UI_Core.Friends_UI_Handler.Pokes_Layout_View.addView(Create_Poke_Item(Temp_Poke_List.getJSONObject(i).getInt("from_user_id"),
					                                                                    Time_Difference,
					                                                                    Temp_Facebook_Friends.getJSONObject(index_j).getString("name"),
					                                                                    new URL(Temp_Facebook_Friends.getJSONObject(index_j)
					                                                                                                 .getJSONObject("picture")
					                                                                                                 .getJSONObject("data")
					                                                                                                 .getString("url"))));
				}
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in PokeUIHandler: Draw_Poke_List():");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
		catch(MalformedURLException e)
		{
			System.err.println("Error PokeUIHandler: Draw_Poke_List():");
			System.err.println("Caught Malformed URL Exception: " + e.getMessage());
		}
		catch(ParseException e)
		{
			System.err.println("Error PokeUIHandler: Draw_Poke_List():");
			System.err.println("Caught Parse Exception: " + e.getMessage());
		}
		*/
	}

	public void Sort_Poke_List()
	{
		try
		{
			JSONArray Temp_Poke_List = UI_Core.FMF_Service.Service_Core.Data_Handler.Friends_Handler.Poke_List;
			SimpleDateFormat Date_Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			for(int i = 0; i < Temp_Poke_List.length(); i++)
			{
				for(int j = 0; j < Temp_Poke_List.length(); j++)
				{
					Date Date_1 = Date_Format.parse(Temp_Poke_List.getJSONObject(i).getString("date_and_time"));
					Date Date_2 = Date_Format.parse(Temp_Poke_List.getJSONObject(j).getString("date_and_time"));

					if(Date_1.compareTo(Date_2) < 0)
					{
						JSONObject Temp_Object;

						Temp_Object = Temp_Poke_List.getJSONObject(j);
						Temp_Poke_List.put(j, Temp_Poke_List.getJSONObject(i));
						Temp_Poke_List.put(i, Temp_Object);
					}
				}
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in TabLayoutHandler: Sort_Poke_List():");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
		catch (ParseException e)
		{
			System.err.println("Error in TabLayoutHandler: Sort_Poke_List():");
			System.err.println("Caught Parse Exception: " + e.getMessage());
		}
	}

	public void Update_Poke_Times()
	{
		try
		{
			JSONArray Temp_Poke_List = UI_Core.FMF_Service.Service_Core.Data_Handler.Friends_Handler.Poke_List;

			for(int i = 0; i < Temp_Poke_List.length(); i++)
			{
				String tag = "Poke_Date_Time_" + Temp_Poke_List.getJSONObject(i).getInt("from_user_id");
				SimpleDateFormat Date_Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				long date_of_poke_in_milli = Date_Format.parse(Temp_Poke_List.getJSONObject(i).getString("date_and_time")).getTime();
				long current_date_in_milli = Calendar.getInstance().getTimeInMillis();

				long diff_in_milli = current_date_in_milli - date_of_poke_in_milli;

				long Seconds = diff_in_milli / 1000 % 60;
				long Minutes = diff_in_milli / (60 * 1000) % 60;
				long Hours = diff_in_milli / (60 * 60 * 1000) % 24;
				long Days = diff_in_milli / (24 * 60 * 60 * 1000);

				String Time_Difference = "";

				if(Days != 0)       Time_Difference += Days + "D ";
				if(Hours != 0)      Time_Difference += Hours + "H ";
				if(Minutes != 0)    Time_Difference += Minutes + "M ";
				if(Seconds != 0)    Time_Difference += Seconds + "S";

				Time_Difference += " ago";

				TextView Text_View = (TextView)UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Friend_Items_Layout.findViewWithTag(tag);
				if(Text_View != null) Text_View.setText(Time_Difference);
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in TabLayoutHandler: Update_Poke_Times():");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
		catch(ParseException e)
		{
			System.err.println("Error in TabLayoutHandler: Update_Poke_Times():");
			System.err.println("Caught Parse Exception: " + e.getMessage());
		}
	}

	float Swipe_Initial_X;
	float Swipe_Initial_Y;
	long  Swipe_Initial_Time;
	float Poke_Item_Initial_X;
	float Poke_Item_Initial_Y;

	private void Remove_Poke(final int User_ID, int Duration)
	{
		UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.UI_On_Touch_Handler.postDelayed(new Runnable()
		{
			@Override public void run()
			{
				try
				{
					JSONObject jsonSend = new JSONObject();

					jsonSend.accumulate("action", "Delete_Poke");
					jsonSend.accumulate("to_user_id", UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getInt("User_ID"));
					jsonSend.accumulate("from_user_id", User_ID);

					UI_Core.FMF_Service.Service_Core.Server_Handler.Queue_Server_Request(UI_Core.FMF_Service.Service_Core.SERVER_URL,
					                                                                     "find_my_friend.php",
					                                                                     jsonSend,
					                                                                     UI_Core.FMF_Service.Service_Core.Server_Response_Handler);

					((ViewGroup)Poke_UI_On_Touch_View.getParent()).removeView(Poke_UI_On_Touch_View);

					boolean poker_not_found = true;

					for(int i = 0; i < UI_Core.FMF_Service.Service_Core.Data_Handler.Friends_Handler.Poke_List.length() && poker_not_found; i++)
					{
						if(UI_Core.FMF_Service.Service_Core.Data_Handler.Friends_Handler.Poke_List.getJSONObject(i).getInt("from_fmf_profile_id") == User_ID)
						{
							UI_Core.FMF_Service.Service_Core.Data_Handler.Friends_Handler.Poke_List.remove(i);

							poker_not_found = false;
						}
					}
				}
				catch(JSONException e)
				{
					System.err.println("Error in PokeUIHandler: Remove_Poke:");
					System.err.println("Caught JSON Exception: " + e.getMessage());
				}
			}
		}, Duration);
	}

	public View.OnTouchListener Poke_UI_On_Touch_Listener = new View.OnTouchListener()
	{
		@Override public boolean onTouch(View view, MotionEvent event)
		{
			Poke_UI_On_Touch_View = view;

			String[] User_Details = view.getTag().toString().split("_");
			final int user_id = Integer.parseInt(User_Details[1]);
			String user_type = User_Details[0];

			float Final_X = event.getRawX();
			float Final_Y = event.getRawY();
			long  Final_Time = System.currentTimeMillis();

			float Delta_X = Final_X - Swipe_Initial_X;
			float Delta_Y = Final_Y - Swipe_Initial_Y;
			float Delta_Time = Final_Time - Swipe_Initial_Time;
			float Delta_Distance = (float)Math.sqrt((Final_X - Swipe_Initial_X) * (Final_X - Swipe_Initial_X) +
			                                        (Final_Y - Swipe_Initial_Y) * (Final_Y - Swipe_Initial_Y));
			float Velocity = Delta_Distance / Delta_Time;

			float view_width_on_screen;
			int duration;
			ObjectAnimator Object_Animator_Translation;
			ObjectAnimator Object_Animator_Alpha;
			AnimatorSet Animator_Set;
			boolean view_not_swiped = true;

			switch(event.getActionMasked())
			{
				case MotionEvent.ACTION_DOWN:
					//UI_Core.Tab_Layout_Handler.Notifications_Scroll_View.requestDisallowInterceptTouchEvent(true);

					Swipe_Initial_X = event.getRawX();
					Swipe_Initial_Y = event.getRawY();
					Poke_Item_Initial_X = view.getX();
					Poke_Item_Initial_Y = view.getY();
					Swipe_Initial_Time = System.currentTimeMillis();
					break;

				case MotionEvent.ACTION_MOVE:
					if(Math.abs(Delta_Y) > 50)
					{
						//UI_Core.Tab_Layout_Handler.Notifications_Scroll_View.requestDisallowInterceptTouchEvent(false);

						view.setX(Delta_X + Poke_Item_Initial_X);

						if(view.getX() < 0) view_width_on_screen = view.getX() + view.getWidth();
						else view_width_on_screen = UI_Core.Theme_Settings.Screen_Width - view.getX();

						view.setAlpha(view_width_on_screen / UI_Core.Theme_Settings.Screen_Width);
					}
					break;

				case MotionEvent.ACTION_UP:
					if(Swipe_Initial_X < Final_X && Math.abs(Delta_Y) < UI_Core.Tab_Layout_Handler.SWIPE_THRESHOLD) // Swipe Left to Right
					{
						view_not_swiped = false;
						duration = Math.round((UI_Core.Theme_Settings.Screen_Width - view.getX()) / Velocity);
						view_width_on_screen = UI_Core.Theme_Settings.Screen_Width - view.getX();

						if(view.getX() > (UI_Core.Theme_Settings.Screen_Width / 2)) // Move Out
						{
							Object_Animator_Translation = ObjectAnimator.ofFloat(view, "translationX", view.getX(), UI_Core.Theme_Settings.Screen_Width);
							Object_Animator_Alpha = ObjectAnimator.ofFloat(view, "alpha", view_width_on_screen / UI_Core.Theme_Settings.Screen_Width, 0f);

							Remove_Poke(user_id, duration + 20);
						}
						else // Move Back
						{
							Object_Animator_Translation = ObjectAnimator.ofFloat(view, "translationX", view.getX(), 0f);
							Object_Animator_Alpha = ObjectAnimator.ofFloat(view, "alpha", view_width_on_screen / UI_Core.Theme_Settings.Screen_Width, 1f);

							if(duration > 200) duration = 200;
						}

						Animator_Set = new AnimatorSet();
						Animator_Set.setDuration(duration);
						Animator_Set.playTogether(Object_Animator_Translation, Object_Animator_Alpha);
						Animator_Set.start();
					}

					if(Swipe_Initial_X > Final_X && Math.abs(Delta_Y) < UI_Core.Tab_Layout_Handler.SWIPE_THRESHOLD) // Swipe Right to Left
					{
						view_not_swiped = false;
						duration = Math.round((view.getWidth() + view.getX()) / Velocity);
						view_width_on_screen = view.getX() + view.getWidth();

						if(view_width_on_screen < (UI_Core.Theme_Settings.Screen_Width / 2)) // Move Out
						{
							Object_Animator_Translation = ObjectAnimator.ofFloat(view, "translationX", view.getX(), UI_Core.Theme_Settings.Screen_Width * -1);
							Object_Animator_Alpha = ObjectAnimator.ofFloat(view, "alpha", view_width_on_screen / UI_Core.Theme_Settings.Screen_Width, 0);

							Remove_Poke(user_id, duration + 20);
						}
						else
						{
							Object_Animator_Translation = ObjectAnimator.ofFloat(view, "translationX", view.getX(), 0);
							Object_Animator_Alpha = ObjectAnimator.ofFloat(view, "alpha", view_width_on_screen / UI_Core.Theme_Settings.Screen_Width, 1);

							if(duration > 200) duration = 200;
						}

						Animator_Set = new AnimatorSet();
						Animator_Set.setDuration(duration);
						Animator_Set.playTogether(Object_Animator_Translation, Object_Animator_Alpha);
						Animator_Set.start();
					}

					if(view_not_swiped)
					{
						UI_Core.Tab_Layout_Handler.Value_Animator_Up.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
						{
							@Override public void onAnimationUpdate(ValueAnimator animator){ Poke_UI_On_Touch_View.setBackgroundColor((Integer)animator.getAnimatedValue()); }
						});

						UI_Core.Tab_Layout_Handler.Value_Animator_Up.start();

						try
						{
							UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.UI_On_Touch_Handler.postDelayed(new Runnable()
							{
								@Override public void run(){ ((ViewGroup)Poke_UI_On_Touch_View.getParent()).removeView(Poke_UI_On_Touch_View); }
							}, UI_Core.Tab_Layout_Handler.ACTION_UP_ANIMATION_DURATION);

							JSONObject jsonSend = new JSONObject();

							jsonSend.accumulate("action", "Delete_Poke");
							jsonSend.accumulate("to_user_id", UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getInt("User_ID"));
							jsonSend.accumulate("from_user_id", user_id);

							UI_Core.FMF_Service.Service_Core.Server_Handler.Queue_Server_Request(UI_Core.FMF_Service.Service_Core.SERVER_URL,
							                                                                     "find_my_friend.php",
							                                                                     jsonSend,
							                                                                     UI_Core.FMF_Service.Service_Core.Server_Response_Handler);

							//UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Friend_Handler.Load_Screen(user_id, user_type);

							//UI_Core.Main_View_Switcher.addView(UI_Core.FMF_Alert.FMF_View);
							//UI_Core.Main_View_Switcher.showNext();
						}
						catch(JSONException e)
						{
							System.err.println("Error in PokeUIHandler: Poke_UI_On_Touch_Listener:");
							System.err.println("Caught JSON Exception: " + e.getMessage());
						}
					}
					break;
			}

			return true;
		}
	};

	public LinearLayout Create_Poke_Item(final int User_ID, String Date_And_Time, String Friend_Name)
	{
		LinearLayout Main_Linear_Layout = new LinearLayout(Base_Context);
		Main_Linear_Layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		Main_Linear_Layout.setOrientation(LinearLayout.HORIZONTAL);
		Main_Linear_Layout.setPadding(UI_Core.Theme_Settings.DP(5), UI_Core.Theme_Settings.DP(5), UI_Core.Theme_Settings.DP(5), UI_Core.Theme_Settings.DP(5));
		Main_Linear_Layout.setTag("Poke_" + User_ID);
		Main_Linear_Layout.setOnTouchListener(Poke_UI_On_Touch_Listener);

		ImageView Profile_Image_View = new ImageView(Base_Context);
		Profile_Image_View.setLayoutParams(new LinearLayout.LayoutParams(UI_Core.Theme_Settings.DP(50), UI_Core.Theme_Settings.DP(50)));
		Profile_Image_View.setBackgroundResource(R.drawable.profile_pic_background);
		Profile_Image_View.setScaleType(ImageView.ScaleType.FIT_CENTER);
		Profile_Image_View.setTag("Poke_Profile_Pic_" + String.valueOf(User_ID));

		LinearLayout Profile_Pic_Background = new LinearLayout(Base_Context);
		Profile_Pic_Background.setLayoutParams(new LinearLayout.LayoutParams(UI_Core.Theme_Settings.DP(54), UI_Core.Theme_Settings.DP(54)));
		Profile_Pic_Background.setBackgroundResource(R.drawable.profile_pic_background);
		Profile_Pic_Background.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);

		Profile_Pic_Background.addView(Profile_Image_View);

		Profile_Image_View.setImageBitmap(UI_Core.FMF_Service.Service_Core.Data_Handler.Pic_Handler
				                                  .Get_Masked_Profile_Pic(UI_Core.FMF_Service.Service_Core.Data_Handler.Pic_Handler.Get_Friend_Profile_Pic_Bitmap(User_ID),
				                                                          50));

		TextView Profile_Text_View = new TextView(Base_Context);
		Profile_Text_View.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, UI_Core.Theme_Settings.DP(50)));
		Profile_Text_View.setGravity(Gravity.CENTER_VERTICAL);
		Profile_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		Profile_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_2));
		Profile_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
		Profile_Text_View.setText(Friend_Name);
		Profile_Text_View.setTag("Poke_Name_" +  String.valueOf(User_ID));

		LinearLayout.LayoutParams Profile_Text_Layout_Params = (LinearLayout.LayoutParams)Profile_Text_View.getLayoutParams();
		Profile_Text_Layout_Params.setMargins(UI_Core.Theme_Settings.DP(20), 0, 0, 0);
		Profile_Text_View.setLayoutParams(Profile_Text_Layout_Params);

		TextView Distance_Text_View = new TextView(Base_Context);
		Distance_Text_View.setLayoutParams(new LinearLayout.LayoutParams(0, UI_Core.Theme_Settings.DP(50), 1));
		Distance_Text_View.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
		Distance_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE - 4);
		Distance_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_2));
		Distance_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
		Distance_Text_View.setText(Date_And_Time);
		Distance_Text_View.setTag("Poke_Date_Time_" + String.valueOf(User_ID));

		Main_Linear_Layout.addView(Profile_Pic_Background);
		Main_Linear_Layout.addView(Profile_Text_View);
		Main_Linear_Layout.addView(Distance_Text_View);

		return Main_Linear_Layout;
	}

	public void Clear_All_Pokes()
	{
		UI_Core.Tab_Layout_Handler.Notifications_Layout_View.removeAllViews();
		UI_Core.FMF_Service.Service_Core.Data_Handler.Friends_Handler.Poke_List = new JSONArray();

		try
		{
			JSONObject jsonSend = new JSONObject();

			jsonSend.accumulate("action", "Clear_Pokes");
			jsonSend.accumulate("to_user_id", UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getInt("User_ID"));

			UI_Core.FMF_Service.Service_Core.Server_Handler.Queue_Server_Request(UI_Core.FMF_Service.Service_Core.SERVER_URL,
			                                                                     "find_my_friend.php",
			                                                                     jsonSend,
			                                                                     UI_Core.FMF_Service.Service_Core.Server_Response_Handler);
		}
		catch(JSONException e)
		{
			System.err.println("Error in PokeUIHandler: Clear_All_Pokes");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}
}
