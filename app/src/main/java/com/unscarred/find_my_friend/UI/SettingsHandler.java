package com.unscarred.find_my_friend.UI;

import com.unscarred.find_my_friend.R;
import com.unscarred.find_my_friend.UI.Settings.*;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.content.Context;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;
import org.json.JSONException;

public class SettingsHandler
{
	private Context         Base_Context;
	private UICore          UI_Core;

	public  GeneralSettingsHandler          General_Settings_Handler;
	public  ProfileSettingsHandler          Profile_Settings_Handler;
	public  PrivacySettingsHandler          Privacy_Settings_Handler;

	public  View            Settings_View;
	private LinearLayout    Header_Layout;
	private LinearLayout    Top_Divider_Layout;
	public  ViewSwitcher    Settings_View_Switcher;
	private LinearLayout    General_Button;
	private LinearLayout    Profile_Button;
	public  ImageView       Profile_Pic_Image_View;
	private LinearLayout    Privacy_Button;
	public  LinearLayout    Invite_Friends_Button;

	public boolean          Screen_Is_Visible = false;


	public SettingsHandler(Context context, UICore core)
	{
		Base_Context = context;
		UI_Core = core;

		General_Settings_Handler = new GeneralSettingsHandler(Base_Context, UI_Core);
		Profile_Settings_Handler = new ProfileSettingsHandler(Base_Context, null);
		Privacy_Settings_Handler = new PrivacySettingsHandler(Base_Context, UI_Core);
	}

	private void Load_Screen()
	{
		Screen_Is_Visible = true;

		LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Settings_View = layoutInflater.inflate(R.layout.settings, null);

		Header_Layout = (LinearLayout)Settings_View.findViewById(R.id.settings_header_layout);
		Top_Divider_Layout = (LinearLayout)Settings_View.findViewById(R.id.settings_top_divider_layout);
		Settings_View_Switcher = (ViewSwitcher)Settings_View.findViewById(R.id.settings_view_switcher);
		General_Button = (LinearLayout)Settings_View.findViewById(R.id.settings_general_button);
		Profile_Button = (LinearLayout)Settings_View.findViewById(R.id.settings_profile_button);
		Profile_Pic_Image_View = (ImageView)Settings_View.findViewById(R.id.settings_profile_pic_image_view);
		Privacy_Button = (LinearLayout)Settings_View.findViewById(R.id.settings_privacy_button);
		Invite_Friends_Button = (LinearLayout)Settings_View.findViewById(R.id.settings_invite_friends_button);

		Header_Layout.setOnTouchListener(UI_Core.On_Touch_Listener);

		General_Button.setOnTouchListener(Settings_On_Touch_Listener);
		Profile_Button.setOnTouchListener(Settings_On_Touch_Listener);
		Privacy_Button.setOnTouchListener(Settings_On_Touch_Listener);
		Invite_Friends_Button.setOnTouchListener(Settings_On_Touch_Listener);

		if(UI_Core.FMF_Service.Service_Core.Facebook_Handler.Is_Logged_In)
			Invite_Friends_Button.setVisibility(View.VISIBLE);
		else
			Invite_Friends_Button.setVisibility(View.GONE);

		Settings_View_Switcher.setInAnimation(AnimationUtils.loadAnimation(Base_Context, R.anim.left_in));
		Settings_View_Switcher.setOutAnimation(AnimationUtils.loadAnimation(Base_Context, R.anim.left_out));

		TextView Text_View = (TextView)Settings_View.findViewById(R.id.settings_title_text_view);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Light);
		Text_View = (TextView)Settings_View.findViewById(R.id.settings_general_button_text_view);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
		Text_View = (TextView)Settings_View.findViewById(R.id.settings_profile_button_text_view);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
		Text_View = (TextView)Settings_View.findViewById(R.id.settings_privacy_button_text_view);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
		Text_View = (TextView)Settings_View.findViewById(R.id.settings_invite_friends_button_text_view);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);

		UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Set_Friend_Item_Clickable_State(false);

		if(UI_Core.FMF_Service.Service_Core.Facebook_Handler.Is_Logged_In && UI_Core.FMF_Service.Service_Core.Facebook_Handler.Facebook_Profile_Pic != null)
			Profile_Pic_Image_View.setImageBitmap(UI_Core.FMF_Service.Service_Core.Data_Handler.Pic_Handler
					                                      .Get_Masked_Profile_Pic(UI_Core.FMF_Service.Service_Core.Facebook_Handler.Facebook_Profile_Pic, 50));
		else
			Profile_Pic_Image_View.setImageBitmap(UI_Core.FMF_Service.Service_Core.Data_Handler.Pic_Handler.Create_Default_Profile_Pic(50));

		UI_Core.Main_Content_Layout.addView(Settings_View, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
	}

	private void Kill_Screen()
	{
		Screen_Is_Visible = false;

		Settings_View = null;

		Top_Divider_Layout = null;
		Settings_View_Switcher = null;
		General_Button = null;
		Profile_Button = null;
		Profile_Pic_Image_View = null;
		Privacy_Button = null;
	}

	public void Show_Screen()
	{
		Load_Screen();

		Settings_View.setAnimation(UI_Core.Theme_Settings.Up_In);
		Settings_View.animate();

		new Handler().postDelayed(new Runnable()
		{
			@Override public void run()
			{
				Top_Divider_Layout.setAnimation(AnimationUtils.loadAnimation(Base_Context, R.anim.fade_out));
				Top_Divider_Layout.animate();

				new Handler().postDelayed(new Runnable()
				{
					@Override public void run(){ Top_Divider_Layout.setVisibility(View.INVISIBLE); }
				}, 500);
			}
		}, 500);
	}

	public void Hide_Screen()
	{
		if(UI_Core.Settings_Handler.General_Settings_Handler.Screen_Is_Visible)
		{
			UI_Core.Settings_Handler.Settings_View_Switcher.setInAnimation(UI_Core.Theme_Settings.Right_In);
			UI_Core.Settings_Handler.Settings_View_Switcher.setOutAnimation(UI_Core.Theme_Settings.Right_Out);

			UI_Core.Settings_Handler.Settings_View_Switcher.showPrevious();
			UI_Core.Settings_Handler.Settings_View_Switcher.removeViewAt(1);

			UI_Core.Settings_Handler.General_Settings_Handler.Kill_Screen();

			UI_Core.Swap_Nav_Buttons();
		}
		else if(UI_Core.Settings_Handler.Profile_Settings_Handler.Screen_Is_Visible)
		{
			UI_Core.Settings_Handler.Settings_View_Switcher.setInAnimation(UI_Core.Theme_Settings.Right_In);
			UI_Core.Settings_Handler.Settings_View_Switcher.setOutAnimation(UI_Core.Theme_Settings.Right_Out);

			UI_Core.Settings_Handler.Settings_View_Switcher.showPrevious();
			UI_Core.Settings_Handler.Settings_View_Switcher.removeViewAt(1);

			UI_Core.Settings_Handler.Profile_Settings_Handler.Kill_Screen();

			UI_Core.Swap_Nav_Buttons();
		}
		else if(UI_Core.Settings_Handler.Privacy_Settings_Handler.Screen_Is_Visible)
		{
			UI_Core.Settings_Handler.Settings_View_Switcher.setInAnimation(UI_Core.Theme_Settings.Right_In);
			UI_Core.Settings_Handler.Settings_View_Switcher.setOutAnimation(UI_Core.Theme_Settings.Right_Out);

			UI_Core.Settings_Handler.Settings_View_Switcher.showPrevious();
			UI_Core.Settings_Handler.Settings_View_Switcher.removeViewAt(1);

			UI_Core.Settings_Handler.Privacy_Settings_Handler.Kill_Screen();

			UI_Core.Swap_Nav_Buttons();
		}
		else
		{
			Top_Divider_Layout.setVisibility(View.VISIBLE);

			Settings_View.setAnimation(UI_Core.Theme_Settings.Down_Out);
			Settings_View.animate();

			new Handler().postDelayed(new Runnable()
			{
				@Override public void run()
				{
					UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Set_Friend_Item_Clickable_State(true);

					UI_Core.Main_Content_Layout.removeView(Settings_View);

					Kill_Screen();
				}
			}, 500);
		}
	}

	public View.OnClickListener On_Click_Listener = new View.OnClickListener()
	{
		@Override public void onClick(View view)
		{
			switch(view.getId())
			{
				case R.id.meters_radio_button:
					try
					{
						UI_Core.Settings_Handler.General_Settings_Handler.Meters_Radio_Button.setChecked(true);
						UI_Core.Settings_Handler.General_Settings_Handler.Feet_Radio_Button.setChecked(false);

						UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.put("Measurement_System", "Meters");
						UI_Core.Settings_Handler.General_Settings_Handler.Range_Label_Text_View
								.setText(UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System"));
					}
					catch(JSONException e)
					{
						System.err.println("Error in SettingsHandler: On_Click_Listener: R.id.meters_radio_button:");
						System.err.println("Caught JSON Exception: " + e.getMessage());
					}
					break;

				case R.id.feet_radio_button:
					try
					{
						UI_Core.Settings_Handler.General_Settings_Handler.Meters_Radio_Button.setChecked(false);
						UI_Core.Settings_Handler.General_Settings_Handler.Feet_Radio_Button.setChecked(true);

						UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.put("Measurement_System", "Feet");
						UI_Core.Settings_Handler.General_Settings_Handler.Range_Label_Text_View
								.setText(UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System"));
					}
					catch(JSONException e)
					{
						System.err.println("Error in SettingsHandler: On_Click_Listener: R.id.feet_radio_button:");
						System.err.println("Caught JSON Exception: " + e.getMessage());
					}
					break;

				case R.id.settings_display_navigation_buttons_check_box:
					UI_Core.Display_Navigation_Buttons = UI_Core.Settings_Handler.General_Settings_Handler.Display_Navigation_Buttons_Check_Box.isChecked();

					if(UI_Core.Display_Navigation_Buttons) UI_Core.Button_Layout.setVisibility(View.VISIBLE);
					else UI_Core.Button_Layout.setVisibility(View.GONE);
					break;
			}
		}
	};

	private View Settings_On_Touch_View;

	public View.OnTouchListener Settings_On_Touch_Listener = new View.OnTouchListener()
	{
		@Override public boolean onTouch(View view, MotionEvent event)
		{
			Settings_On_Touch_View = view;

			switch(event.getActionMasked())
			{
				case MotionEvent.ACTION_UP:
					UI_Core.Tab_Layout_Handler.Value_Animator_Up.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
					{
						@Override public void onAnimationUpdate(ValueAnimator animator){ Settings_On_Touch_View.setBackgroundColor((Integer)animator.getAnimatedValue()); }
					});
					UI_Core.Tab_Layout_Handler.Value_Animator_Up.start();

					switch(view.getId())
					{
						case R.id.settings_general_button:
							General_Settings_Handler.Load_Screen();
							UI_Core.Swap_Nav_Buttons();
							break;
						case R.id.settings_profile_button:
							Profile_Settings_Handler.Load_Screen();
							UI_Core.Swap_Nav_Buttons();
							break;
						case R.id.settings_privacy_button:
							Privacy_Settings_Handler.Load_Screen();
							UI_Core.Swap_Nav_Buttons();
							break;
						case R.id.settings_invite_friends_button:
							MessageDialog Message_Dialog = new MessageDialog((Activity)Base_Context);

							if(MessageDialog.canShow(ShareLinkContent.class))
							{
								String Facebook_Name = UI_Core.FMF_Service.Service_Core.Facebook_Handler.Facebook_Profile.getName();
								String content_description = Facebook_Name + " would like you to invite you to FMF. Know when your friends are in your vicinity.";


								ShareLinkContent linkContent = new ShareLinkContent.Builder().setContentTitle("FMF Invite")
								                                                             .setContentDescription(content_description)
								                                                             .setContentUrl(Uri.parse("http://developers.facebook.com/docs/android"))
								                                                             .build();

								Message_Dialog.registerCallback(UI_Core.FMF_Service.Service_Core.Facebook_Handler.Callback_Manager,
								                                UI_Core.FMF_Service.Service_Core.Facebook_Handler.Facebook_Send_Callback);

								Message_Dialog.show(linkContent);
							}
							break;
					}
					break;
			}

			return true;
		}
	};

}
