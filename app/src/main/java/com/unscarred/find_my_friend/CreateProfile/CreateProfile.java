package com.unscarred.find_my_friend.CreateProfile;

import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.FacebookSdk;
import com.unscarred.find_my_friend.Common.FileHandler;
import com.unscarred.find_my_friend.Common.ThemeSettings;
import com.unscarred.find_my_friend.R;

import android.app.Activity;
import android.content.Context;
import android.view.animation.AnimationUtils;
import android.widget.ViewSwitcher;

public class CreateProfile
{
	public  Context Base_Context;

	public  FileHandler              File_Handler;
	public  LogInHandler             Log_In_Handler;
	private EmailVerificationHandler Email_Verification_Handler;
	public  ThemeSettings            Theme_Settings;

	public  Handler                  Activity_Message_Handler;

	public  FrameLayout              Frame_Layout;


	public CreateProfile(Context context, Handler handler)
	{
		Base_Context = context;
		File_Handler = new FileHandler(Base_Context);
		Theme_Settings = new ThemeSettings(Base_Context);

		Activity_Message_Handler = handler;

		Frame_Layout = (FrameLayout)((Activity)Base_Context).findViewById(R.id.main_frame_layout);

		RelativeLayout Button_Layout = (RelativeLayout)((Activity)Base_Context).findViewById(R.id.main_button_layout);
		TextView Network_Error_Icon = (TextView)((Activity)Base_Context).findViewById(R.id.main_network_error_text_view);
		TextView Title_Text_View = (TextView)((Activity)Base_Context).findViewById(R.id.main_title_text_view);
		Title_Text_View.setTypeface(Theme_Settings.Font_Light);

		Network_Error_Icon.setTypeface(Theme_Settings.Font_Bold);
		Network_Error_Icon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		Network_Error_Icon.setTextColor(Base_Context.getResources().getColor(R.color.color_3));
		Network_Error_Icon.setVisibility(View.INVISIBLE);
		Button_Layout.setVisibility(View.GONE);

		FacebookSdk.sdkInitialize(Base_Context);
	}

	public void Show_Log_In_Screen()
	{
		Log_In_Handler = new LogInHandler(Base_Context, this);

		//View_Switcher.setInAnimation(AnimationUtils.loadAnimation(Base_Context, R.anim.fade_in));
		//View_Switcher.setOutAnimation(AnimationUtils.loadAnimation(Base_Context, R.anim.fade_out));

		Log_In_Handler.Load_Screen();
		//View_Switcher.addView(Log_In_Handler.Log_In_View);
	}

	public void Show_Email_Verification_Screen()
	{
		Email_Verification_Handler = new EmailVerificationHandler(Base_Context, this);

		//View_Switcher.setInAnimation(AnimationUtils.loadAnimation(Base_Context, R.anim.fade_in));
		//View_Switcher.setOutAnimation(AnimationUtils.loadAnimation(Base_Context, R.anim.fade_out));

		Email_Verification_Handler.Load_Screen();
		//View_Switcher.addView(Email_Verification_Handler.Email_Verification_View);
	}
}
