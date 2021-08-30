package com.unscarred.find_my_friend.CreateProfile;

import com.unscarred.find_my_friend.UI.Settings.ProfileSettingsHandler;
import com.unscarred.find_my_friend.R;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewSwitcher;


public class LogInHandler
{
	private Context             Base_Context;
	private CreateProfile       Create_Profile;

	public  View                Log_In_View;
	public  ViewSwitcher        View_Switcher;
	private TextView            Create_Profile_Button;

	public  boolean             Screen_Is_Visible = false;

	public ProfileSettingsHandler Profile_Settings_Handler;

	public LogInHandler(Context context, CreateProfile create_profile)
	{
		Base_Context = context;
		Create_Profile = create_profile;
	}

	public void Load_Screen()
	{
		Screen_Is_Visible = true;

		LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Log_In_View = layoutInflater.inflate(R.layout.log_in, null);

		View_Switcher = (ViewSwitcher)Log_In_View.findViewById(R.id.log_in_view_switcher);
		Create_Profile_Button = (TextView)Log_In_View.findViewById(R.id.log_in_create_profile_button);

		TextView Text_View;
		Text_View = (TextView)Log_In_View.findViewById(R.id.log_in_title_text_view);
		Text_View.setTypeface(Create_Profile.Theme_Settings.Font_Regular);

		Create_Profile_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		Create_Profile_Button.setTextColor(Base_Context.getResources().getColor(R.color.color_4));
		Create_Profile_Button.setTypeface(Create_Profile.Theme_Settings.Font_Light);
		Create_Profile_Button.setOnTouchListener(On_Touch_Listener);

		View_Switcher.setInAnimation(Create_Profile.Theme_Settings.Left_In);
		View_Switcher.setOutAnimation(Create_Profile.Theme_Settings.Left_Out);
	}

	private View.OnTouchListener On_Touch_Listener = new View.OnTouchListener()
	{
		@Override public boolean onTouch(View view, MotionEvent event)
		{
			switch(view.getId())
			{
				case R.id.log_in_create_profile_button:
					switch(event.getAction() & MotionEvent.ACTION_MASK)
					{
						case MotionEvent.ACTION_DOWN:
							break;

						case MotionEvent.ACTION_UP:
							Profile_Settings_Handler = new ProfileSettingsHandler(Base_Context, Create_Profile);

							Profile_Settings_Handler.Load_Screen();

							//Create_Profile.View_Switcher.addView(Profile_Settings_Handler.Profile_Settings_View);
							//Create_Profile.View_Switcher.showNext();
							break;
					}
					break;
			}

			return true;
		}
	};

	public void Kill_Screen()
	{
		Screen_Is_Visible = false;

		Log_In_View = null;

		View_Switcher = null;
	}
}
