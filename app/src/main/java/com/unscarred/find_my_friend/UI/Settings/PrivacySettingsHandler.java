package com.unscarred.find_my_friend.UI.Settings;

import com.unscarred.find_my_friend.R;
import com.unscarred.find_my_friend.UI.UICore;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PrivacySettingsHandler
{
	private Context         Base_Context;
	private UICore          UI_Core;

	public  View            Privacy_Settings_View;
	private LinearLayout    Header_Layout;
	private LinearLayout    Blocked_Users_Layout;

	public boolean          Screen_Is_Visible = false;


	public PrivacySettingsHandler(Context context, UICore core)
	{
		Base_Context = context;
		UI_Core = core;
	}

	public void Load_Screen()
	{
		Screen_Is_Visible = true;

		LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Privacy_Settings_View = layoutInflater.inflate(R.layout.privacy_settings, null);

		Header_Layout = (LinearLayout)Privacy_Settings_View.findViewById(R.id.settings_privacy_header_layout);
		Blocked_Users_Layout = (LinearLayout)Privacy_Settings_View.findViewById(R.id.settings_privacy_blocked_users_layout);

		Header_Layout.setOnTouchListener(UI_Core.On_Touch_Listener);

		TextView Text_View;
		Text_View = (TextView)Privacy_Settings_View.findViewById(R.id.settings_privacy_title_text_view);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Light);

		Text_View = (TextView)Privacy_Settings_View.findViewById(R.id.settings_privacy_blocked_users_label);
		Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Bold);

		Text_View = (TextView)Privacy_Settings_View.findViewById(R.id.settings_privacy_blocked_users_help);
		Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE - 4);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);

		Draw_Blocked_User_Lists();

		UI_Core.Settings_Handler.Settings_View_Switcher.addView(Privacy_Settings_View);

		UI_Core.Settings_Handler.Settings_View_Switcher.setInAnimation(UI_Core.Theme_Settings.Left_In);
		UI_Core.Settings_Handler.Settings_View_Switcher.setOutAnimation(UI_Core.Theme_Settings.Left_Out);

		UI_Core.Settings_Handler.Settings_View_Switcher.showNext();
	}

	public void Draw_Blocked_User_Lists()
	{
		try
		{
			JSONArray Blocked_Users = UI_Core.FMF_Service.Service_Core.Data_Handler.Friends_Handler.Blocked_Users;
			JSONArray Blocked_User_Params_Array = new JSONArray();
			JSONObject Blocked_User_Params;

			for(int i = 0; i < Blocked_Users.length(); i++)
			{
				Blocked_User_Params = new JSONObject();

				Blocked_User_Params.accumulate("blocked_user_id", Blocked_Users.getJSONObject(i).getInt("blocked_user_id"));
				Blocked_User_Params.accumulate("blocked_user_name", "User-" + Blocked_Users.getJSONObject(i).getString("blocked_user_id"));
				Blocked_User_Params.accumulate("blocked_user_url", "");

				Blocked_User_Params_Array.put(Blocked_User_Params);
			}

			Blocked_User_Params_Array = Sort_In_Blocked_User_List(Blocked_User_Params_Array);

			for(int i = 0; i < Blocked_User_Params_Array.length(); i++)
			{
				Blocked_Users_Layout.addView(UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Blocked_User_UI_Handler
						                             .Create_Blocked_User_Item(Blocked_User_Params_Array.getJSONObject(i).getInt("blocked_user_id"),
						                                                       Blocked_User_Params_Array.getJSONObject(i).getString("blocked_user_name")));
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in PrivacySettingsHandler: Draw_Blocked_User_Lists:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	private JSONArray Sort_In_Blocked_User_List(JSONArray Blocked_User_Params_Array)
	{
		try
		{
			for(int i = 0; i < Blocked_User_Params_Array.length(); i++)
			{
				for(int j = 0; j < Blocked_User_Params_Array.length(); j++)
				{
					if(Blocked_User_Params_Array.getJSONObject(i).getString("blocked_user_name")
					                            .compareToIgnoreCase(Blocked_User_Params_Array.getJSONObject(j).getString("blocked_user_name")) < 0)
					{
						JSONObject Temp_Object = Blocked_User_Params_Array.getJSONObject(j);
						Blocked_User_Params_Array.put(j, Blocked_User_Params_Array.getJSONObject(i));
						Blocked_User_Params_Array.put(i, Temp_Object);
					}
				}
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in PrivacySettingsHandler: Sort_In_Blocked_User_List():");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}

		return Blocked_User_Params_Array;
	}

	public void Kill_Screen()
	{
		Screen_Is_Visible = false;

		Privacy_Settings_View = null;

		Blocked_Users_Layout = null;
	}
}
