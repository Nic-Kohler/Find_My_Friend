package com.unscarred.find_my_friend.UI.Settings;

import com.unscarred.find_my_friend.UI.UICore;
import com.unscarred.find_my_friend.R;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;


public class GeneralSettingsHandler
{
	private Context         Base_Context;
	private UICore          UI_Core;

	public  View            General_Settings_View;
	private LinearLayout    Header_Layout;
	public  CheckBox        Run_In_Background_Check_Box;

	public CheckBox         Display_Navigation_Buttons_Check_Box;

	public RadioButton      Meters_Radio_Button;
	public RadioButton      Feet_Radio_Button;

	private EditText        Max_Range_Edit_Text;
	public TextView         Range_Label_Text_View;

	private TextView        Location_Update_Interval_Minutes_Text_View;
	private TextView        Location_Update_Interval_Seconds_Text_View;
	public  SeekBar         Location_Update_Interval_Seconds_Seek_Bar;

	private TextView        User_Update_Interval_Minutes_Text_View;
	private TextView        User_Update_Interval_Seconds_Text_View;
	public  SeekBar         User_Update_Interval_Seconds_Seek_Bar;

	public boolean          Screen_Is_Visible = false;

	final int               MIN_RANGE = 3000;
	final int               MAX_RANGE = 897000;


	public GeneralSettingsHandler(Context context, UICore core)
	{
		Base_Context = context;
		UI_Core = core;
	}

	public void Load_Screen()
	{
		Screen_Is_Visible = true;

		LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		General_Settings_View = layoutInflater.inflate(R.layout.general_settings, null);

		Header_Layout = (LinearLayout)General_Settings_View.findViewById(R.id.settings_general_header_layout);
		Run_In_Background_Check_Box = (CheckBox)General_Settings_View.findViewById(R.id.settings_run_in_background_check_box);
		Meters_Radio_Button = (RadioButton)General_Settings_View.findViewById(R.id.meters_radio_button);
		Feet_Radio_Button = (RadioButton)General_Settings_View.findViewById(R.id.feet_radio_button);
		Max_Range_Edit_Text = (EditText)General_Settings_View.findViewById(R.id.settings_range_max_edit_text);
		Range_Label_Text_View = (TextView)General_Settings_View.findViewById(R.id.settings_range_max_measurement_label_text_view);

		Header_Layout.setOnTouchListener(UI_Core.On_Touch_Listener);

		try
		{
			Max_Range_Edit_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
			Max_Range_Edit_Text.setTypeface(UI_Core.Theme_Settings.Font_Regular);
			Max_Range_Edit_Text.setText(String.valueOf(UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getInt("Max_Range") + 10));
			Max_Range_Edit_Text.addTextChangedListener(Text_Watcher);

			Range_Label_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
			Range_Label_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
			Range_Label_Text_View.setText(UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System"));

			Meters_Radio_Button.setOnClickListener(UI_Core.Settings_Handler.On_Click_Listener);
			Feet_Radio_Button.setOnClickListener(UI_Core.Settings_Handler.On_Click_Listener);
			Run_In_Background_Check_Box.setOnClickListener(UI_Core.Settings_Handler.On_Click_Listener);

			Meters_Radio_Button.setChecked(false);
			Feet_Radio_Button.setChecked(false);

			if(UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System").equals("Meters")) Meters_Radio_Button.setChecked(true);
			else Feet_Radio_Button.setChecked(true);
		}
		catch(JSONException e)
		{
			System.err.println("Error in General Settings Handler: Load_Screen:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}


		TextView Text_View;
		Text_View = (TextView)General_Settings_View.findViewById(R.id.settings_general_title_text_view);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Light);

		Text_View = (TextView)General_Settings_View.findViewById(R.id.settings_run_in_background_label_text_view);
		Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Bold);
		Text_View = (TextView)General_Settings_View.findViewById(R.id.settings_range_measurement_label_text_view);
		Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Bold);
		Text_View = (TextView)General_Settings_View.findViewById(R.id.meters_radio_button_label);
		Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
		Text_View = (TextView)General_Settings_View.findViewById(R.id.feet_radio_button_label);
		Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
		Text_View = (TextView)General_Settings_View.findViewById(R.id.settings_range_max_label_text_view);
		Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Bold);

		Set_Display_Navigation_Buttons_Layout();
		Set_Location_Update_Interval_Layout();
		Set_User_Update_Interval_Layout();

		UI_Core.Settings_Handler.Settings_View_Switcher.addView(General_Settings_View);

		UI_Core.Settings_Handler.Settings_View_Switcher.setInAnimation(UI_Core.Theme_Settings.Left_In);
		UI_Core.Settings_Handler.Settings_View_Switcher.setOutAnimation(UI_Core.Theme_Settings.Left_Out);

		UI_Core.Settings_Handler.Settings_View_Switcher.showNext();
	}

	private void Set_Display_Navigation_Buttons_Layout()
	{
		TextView Text_View;
		Text_View = (TextView)General_Settings_View.findViewById(R.id.settings_display_navigation_buttons_label_text_view);
		Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Bold);

		Text_View = (TextView)General_Settings_View.findViewById(R.id.settings_display_navigation_button_help_label);
		Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE - 4);
		Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);

		Display_Navigation_Buttons_Check_Box = (CheckBox)General_Settings_View.findViewById(R.id.settings_display_navigation_buttons_check_box);
		Display_Navigation_Buttons_Check_Box.setOnClickListener(UI_Core.Settings_Handler.On_Click_Listener);


		if(UI_Core.Display_Navigation_Buttons) Display_Navigation_Buttons_Check_Box.setChecked(true);
		else Display_Navigation_Buttons_Check_Box.setChecked(false);
	}

	private void Set_Location_Update_Interval_Layout()
	{
		TextView Location_Update_Interval_Label = (TextView)General_Settings_View.findViewById(R.id.settings_location_update_interval_label_text_view);
		Location_Update_Interval_Minutes_Text_View = (TextView)General_Settings_View.findViewById(R.id.settings_location_update_interval_minute_text_view);
		Location_Update_Interval_Seconds_Text_View = (TextView)General_Settings_View.findViewById(R.id.settings_location_update_interval_second_text_view);
		Location_Update_Interval_Seconds_Seek_Bar = (SeekBar)General_Settings_View.findViewById(R.id.settings_location_update_interval_seek_bar);
		TextView Location_Battery_Usage_Low_Label = (TextView)General_Settings_View.findViewById(R.id.settings_location_battery_usage_low_label);
		TextView Location_Accuracy_High_Label = (TextView)General_Settings_View.findViewById(R.id.settings_location_accuracy_high_label);

		Location_Update_Interval_Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		Location_Update_Interval_Label.setTypeface(UI_Core.Theme_Settings.Font_Bold);

		Location_Update_Interval_Minutes_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		Location_Update_Interval_Minutes_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);

		Location_Update_Interval_Seconds_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		Location_Update_Interval_Seconds_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);

		Location_Battery_Usage_Low_Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE - 4);
		Location_Battery_Usage_Low_Label.setTypeface(UI_Core.Theme_Settings.Font_Regular);

		Location_Accuracy_High_Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE - 4);
		Location_Accuracy_High_Label.setTypeface(UI_Core.Theme_Settings.Font_Regular);

		try
		{
			Location_Update_Interval_Seconds_Seek_Bar.setMax(MAX_RANGE);
			Location_Update_Interval_Seconds_Seek_Bar.setProgress(UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getInt("Location_Update_Interval"));
			Location_Update_Interval_Seconds_Seek_Bar.setOnSeekBarChangeListener(On_Seek_Bar_Change_Listener);
		}
		catch(JSONException e)
		{
			System.err.println("Error in GeneralSettingsHandler: Set_Location_Update_Interval_Layout");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}

		Set_Location_Update_Interval();
	}

	private void Set_User_Update_Interval_Layout()
	{
		TextView User_Update_Interval_Label = (TextView)General_Settings_View.findViewById(R.id.settings_user_update_interval_label_text_view);
		User_Update_Interval_Minutes_Text_View = (TextView)General_Settings_View.findViewById(R.id.settings_user_update_interval_minute_text_view);
		User_Update_Interval_Seconds_Text_View = (TextView)General_Settings_View.findViewById(R.id.settings_user_update_interval_second_text_view);
		User_Update_Interval_Seconds_Seek_Bar = (SeekBar)General_Settings_View.findViewById(R.id.settings_user_update_interval_seek_bar);
		TextView User_Battery_Usage_Low_Label = (TextView)General_Settings_View.findViewById(R.id.settings_user_battery_low_label);
		TextView User_Accuracy_High_Label = (TextView)General_Settings_View.findViewById(R.id.settings_user_accuracy_high_label);

		User_Update_Interval_Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		User_Update_Interval_Label.setTypeface(UI_Core.Theme_Settings.Font_Bold);

		User_Update_Interval_Minutes_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		User_Update_Interval_Minutes_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);

		User_Update_Interval_Seconds_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
		User_Update_Interval_Seconds_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);

		User_Battery_Usage_Low_Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE - 4);
		User_Battery_Usage_Low_Label.setTypeface(UI_Core.Theme_Settings.Font_Regular);

		User_Accuracy_High_Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE - 4);
		User_Accuracy_High_Label.setTypeface(UI_Core.Theme_Settings.Font_Regular);

		User_Update_Interval_Seconds_Seek_Bar.setMax(MAX_RANGE);
		User_Update_Interval_Seconds_Seek_Bar.setProgress(UI_Core.FMF_Service.Service_Core.DATA_UPDATE_INTERVAL);
		User_Update_Interval_Seconds_Seek_Bar.setOnSeekBarChangeListener(On_Seek_Bar_Change_Listener);

		Set_User_Update_Interval();
	}

	private void Set_Location_Update_Interval()
	{
		try
		{
			int minutes = (int)Math.floor(UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getInt("Location_Update_Interval") / 60000);

			Log.d("Nic Says", "minutes: " + minutes);

			int seconds = Math.round((UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getInt("Location_Update_Interval") - (minutes * 60000)) / 1000);


			Log.d("Nic Says", "seconds: " + seconds);

			if(minutes == 0)
				Location_Update_Interval_Minutes_Text_View.setVisibility(View.GONE);
			else
				Location_Update_Interval_Minutes_Text_View.setVisibility(View.VISIBLE);

			if(seconds == 0)
				Location_Update_Interval_Seconds_Text_View.setVisibility(View.GONE);
			else
				Location_Update_Interval_Seconds_Text_View.setVisibility(View.VISIBLE);

			String mins = minutes + " Min";
			String secs = seconds + " Sec";

			Location_Update_Interval_Minutes_Text_View.setText(mins);
			Location_Update_Interval_Seconds_Text_View.setText(secs);
		}
		catch(JSONException e)
		{
			System.err.println("Error in GeneralSettingsHandler: Set_Location_Update_Interval");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	private void Set_User_Update_Interval()
	{
		int minutes = (int)Math.floor(UI_Core.FMF_Service.Service_Core.DATA_UPDATE_INTERVAL / 60000);

		Log.d("Nic Says", "minutes: " + minutes);

		int seconds = Math.round((UI_Core.FMF_Service.Service_Core.DATA_UPDATE_INTERVAL - (minutes * 60000)) / 1000);

		Log.d("Nic Says", "seconds: " + seconds);

		if(minutes == 0)
			User_Update_Interval_Minutes_Text_View.setVisibility(View.GONE);
		else
			User_Update_Interval_Minutes_Text_View.setVisibility(View.VISIBLE);

		if(seconds == 0)
			User_Update_Interval_Seconds_Text_View.setVisibility(View.GONE);
		else
			User_Update_Interval_Seconds_Text_View.setVisibility(View.VISIBLE);

		String mins = minutes + " Min";
		String secs = seconds + " Sec";

		User_Update_Interval_Minutes_Text_View.setText(mins);
		User_Update_Interval_Seconds_Text_View.setText(secs);
	}

	private SeekBar.OnSeekBarChangeListener On_Seek_Bar_Change_Listener = new SeekBar.OnSeekBarChangeListener()
	{
		@Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		{
			progress += MIN_RANGE; if(progress < MIN_RANGE) progress = MIN_RANGE;

			switch(seekBar.getId())
			{
				case R.id.settings_location_update_interval_seek_bar:
					try
					{
						UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.put("Location_Update_Interval", progress);
					}
					catch(JSONException e)
					{
						System.err.println("Error in GeneralSettingsHandler: On_Seek_Bar_Change_Listener: onProgressChanged");
						System.err.println("Caught JSON Exception: " + e.getMessage());
					}

					Set_Location_Update_Interval();
					break;
			}
		}

		@Override public void onStartTrackingTouch(SeekBar seekBar){}
		@Override public void onStopTrackingTouch(SeekBar seekBar){}
	};

	public void Kill_Screen()
	{
		Screen_Is_Visible = false;

		General_Settings_View = null;

		Meters_Radio_Button = null;
		Feet_Radio_Button = null;
		Max_Range_Edit_Text = null;
		Range_Label_Text_View = null;
	}

	private TextWatcher Text_Watcher = new TextWatcher()
	{
		public void afterTextChanged(Editable s)
		{
			String str_max_range = Max_Range_Edit_Text.getText().toString();

			if(StringUtils.isNumeric(str_max_range))
			{
				try
				{
					int int_max_range = Integer.parseInt(str_max_range);

					if(int_max_range > 10)
						UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.put("Max_Range", int_max_range - 10);
					else
						UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.put("Max_Range", 0);

					UI_Core.Tab_Layout_Handler.Set_Seek_Bar_Range();
				}
				catch(JSONException e)
				{
					System.err.println("Error in GeneralSettingsHandler: Text_Watcher: afterTextChanged");
					System.err.println("Caught JSON Exception: " + e.getMessage());
				}
			}
		}
		public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		public void onTextChanged(CharSequence s, int start, int before, int count){}
	};
}
