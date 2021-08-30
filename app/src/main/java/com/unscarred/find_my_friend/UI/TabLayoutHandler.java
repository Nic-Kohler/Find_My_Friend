package com.unscarred.find_my_friend.UI;

import com.unscarred.find_my_friend.R;
import com.unscarred.find_my_friend.UI.ScreenHandler.Load_Screen_Function;
import com.unscarred.find_my_friend.UI.ScreenHandler.Screen_Function;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.content.Context;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;

import android.util.TypedValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class TabLayoutHandler
{
	private Context                 Base_Context;
	public  UICore                  UI_Core;
	public  FriendsTabHandler       Friends_Tab_Handler;
	public  PokeUIHandler           Poke_UI_Handler;

	public  View                    Tab_Layout_View;
	public  LinearLayout            Notifications_Layout_View;
	public  SeekBar                 Range_Seek_Bar;
	private TextView                Range_Text_View;
	public  TabHost                 Tab_Host;
	private LinearLayout            Range_Layout;

	public  ValueAnimator           Value_Animator_Down;
	public  ValueAnimator           Value_Animator_Up;

	private int                     Current_Tab_Index = 0;
	public  final int               MIN_RANGE = 10;

	public final int                SWIPE_THRESHOLD = 400;
	final  int                      ACTION_DOWN_ANIMATION_DURATION = 1000;
	final  int                      ACTION_UP_ANIMATION_DURATION = 200;


	public TabLayoutHandler(Context context, UICore core)
	{
		Base_Context = context;
		UI_Core = core;
		Friends_Tab_Handler = new FriendsTabHandler(Base_Context, UI_Core);
		Poke_UI_Handler = new PokeUIHandler(Base_Context, UI_Core);
	}

	public Load_Screen_Function Load_Screen = new Load_Screen_Function()
	{
		@Override public View Execute()
		{
			LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			Tab_Layout_View = layoutInflater.inflate(R.layout.tab_layout, null);

			Notifications_Layout_View = (LinearLayout)Tab_Layout_View.findViewById(R.id.notifications_tab_items_layout);
			Range_Seek_Bar = (SeekBar)Tab_Layout_View.findViewById(R.id.range_seek_bar);
			Range_Text_View = (TextView)Tab_Layout_View.findViewById(R.id.range_text_view);
			Range_Layout = (LinearLayout)Tab_Layout_View.findViewById(R.id.range_layout);

			Range_Layout.setOnTouchListener(Menu_Swipe_Up_On_Touch_Listener);

			Range_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
			Range_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);

			TextView Text_View = (TextView)Tab_Layout_View.findViewById(R.id.range_label_text_view);
			Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE);
			Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);

			Value_Animator_Down = ValueAnimator.ofObject(new ArgbEvaluator(),
			                                             ContextCompat.getColor(Base_Context, R.color.color_1),
			                                             ContextCompat.getColor(Base_Context, R.color.color_5))
			                                   .setDuration(ACTION_DOWN_ANIMATION_DURATION);

			Value_Animator_Up = ValueAnimator.ofObject(new ArgbEvaluator(),
			                                           ContextCompat.getColor(Base_Context, R.color.color_4),
			                                           ContextCompat.getColor(Base_Context, R.color.color_1))
			                                 .setDuration(ACTION_UP_ANIMATION_DURATION);


			Set_Seek_Bar_Range();

			try
			{
				String text = UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getInt("Current_Range") + " " +
				              UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System");
				Range_Text_View.setText(text);
			}
			catch(JSONException e)
			{
				System.err.println("Error in TabLayoutHandler: Load_Screen:");
				System.err.println("Caught JSON Exception: " + e.getMessage());
			}

			Range_Seek_Bar.setOnSeekBarChangeListener(On_Seek_Bar_Change_Listener);

			Set_Tab_Host();

			return Tab_Layout_View;
		}
	};

	public void Set_Seek_Bar_Range()
	{
		try
		{
			JSONObject FMF_Settings_Data = UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data;

			if(FMF_Settings_Data.getInt("Current_Range") > FMF_Settings_Data.getInt("Max_Range"))
				FMF_Settings_Data.put("Current_Range", FMF_Settings_Data.getInt("Max_Range"));

			Range_Seek_Bar.setMax(FMF_Settings_Data.getInt("Max_Range"));
			Range_Seek_Bar.setProgress(FMF_Settings_Data.getInt("Current_Range") - MIN_RANGE);
		}
		catch(JSONException e)
		{
			System.err.println("Error in TabLayoutHandler: Set_Seek_Bar_Range:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
}

	public int Get_Seek_Bar_Range(){ return Range_Seek_Bar.getProgress() + MIN_RANGE; }

	private SeekBar.OnSeekBarChangeListener On_Seek_Bar_Change_Listener = new SeekBar.OnSeekBarChangeListener()
	{
		@Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		{
			int range = progress + MIN_RANGE;

			try
			{
				String text = range + " " + UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.getString("Measurement_System");
				Range_Text_View.setText(text);
				UI_Core.FMF_Service.Service_Core.File_Handler.FMF_Settings_Data.put("Current_Range", range);
			}
			catch(JSONException e)
			{
				System.err.println("Error in TabLayoutHandler: Get_Seek_Bar_Range:");
				System.err.println("Caught JSON Exception: " + e.getMessage());
			}

			Friends_Tab_Handler.Update_Friends();
		}

		@Override public void onStartTrackingTouch(SeekBar seekBar){}
		@Override public void onStopTrackingTouch(SeekBar seekBar){}
	};

	private void Set_Tab_Host()
	{
		Tab_Host = (TabHost)Tab_Layout_View.findViewById(R.id.tab_host);
		Tab_Host.setup();
		Tab_Host.setOnTabChangedListener(On_Tab_Change_Listener);

		Friends_Tab_Handler.Load_Screen();

		TabHost.TabContentFactory Tab_Content = new TabHost.TabContentFactory(){ public View createTabContent(String tag){ return Friends_Tab_Handler.Friends_Tab_View; }};
		TabHost.TabSpec Tab_Spec_Content = Tab_Host.newTabSpec("Friends_Tab")
		                                           .setIndicator("", ContextCompat.getDrawable(Base_Context, R.drawable.no_avatar))
		                                           .setContent(Tab_Content);
		Tab_Host.addTab(Tab_Spec_Content);

		Tab_Spec_Content = Tab_Host.newTabSpec("Notifications_Tab")
		                           .setIndicator("", ContextCompat.getDrawable(Base_Context, R.drawable.no_avatar))
		                           .setContent(new TabHost.TabContentFactory()
		                           {
			                           public View createTabContent(String tag)
			                           {
				                           return LayoutInflater.from(Base_Context).inflate(R.layout.notifications_tab, null);
			                           }
		                           });

		Tab_Host.addTab(Tab_Spec_Content);


		LinearLayout Text_View = (LinearLayout)Tab_Host.getTabWidget().getChildAt(0);
		Text_View.setBackgroundResource(R.drawable.tab_widget_selected);
		Text_View.setPadding(UI_Core.Theme_Settings.DP(10), UI_Core.Theme_Settings.DP(10), UI_Core.Theme_Settings.DP(10), UI_Core.Theme_Settings.DP(10));
		Text_View.setMinimumWidth(UI_Core.Theme_Settings.DP(30));
		Text_View.getLayoutParams().width = UI_Core.Theme_Settings.DP(30);

		Tab_Host.getTabWidget().getChildAt(0).setVisibility(View.VISIBLE);
		Tab_Host.setCurrentTab(Current_Tab_Index);

		Set_PreLoaded_Screens();
	}

	private TabHost.OnTabChangeListener On_Tab_Change_Listener = new TabHost.OnTabChangeListener()
	{
		@Override public void onTabChanged(String Tab_Tag)
		{
			Log.d("Nic Says", "Tab has changed to " + Tab_Tag);

			LinearLayout Text_View;

			for(int i = 0; i < Tab_Host.getTabWidget().getChildCount(); i++)
			{
				Text_View = (LinearLayout)Tab_Host.getTabWidget().getChildAt(i);

				Text_View.setBackgroundResource(R.drawable.tab_widget_unselected);
				Text_View.setPadding(UI_Core.Theme_Settings.DP(10), UI_Core.Theme_Settings.DP(10), UI_Core.Theme_Settings.DP(10), UI_Core.Theme_Settings.DP(10));
				Text_View.setMinimumWidth(UI_Core.Theme_Settings.DP(80));
			}

			Current_Tab_Index = Tab_Host.getCurrentTab();
			Text_View = (LinearLayout)Tab_Host.getTabWidget().getChildAt(Current_Tab_Index);
			Text_View.setBackgroundResource(R.drawable.tab_widget_selected);
			Text_View.setPadding(UI_Core.Theme_Settings.DP(10), UI_Core.Theme_Settings.DP(10), UI_Core.Theme_Settings.DP(10), UI_Core.Theme_Settings.DP(10));
			Text_View.setMinimumWidth(UI_Core.Theme_Settings.DP(80));

			Set_PreLoaded_Screens();
		}
	};

	private void Set_PreLoaded_Screens()
	{
		ArrayList<ScreenHandler.PreloadedLoadScreenFunction> Preloaded_Load_Screen_Functions = new ArrayList<ScreenHandler.PreloadedLoadScreenFunction>();

		switch(Current_Tab_Index)
		{
			case 0: //Friends
				Preloaded_Load_Screen_Functions.add(UI_Core.Screen_Handler.new PreloadedLoadScreenFunction("Friend",
				                                                                                           Friends_Tab_Handler.Friend_Handler.Load_Screen));
				break;

			case 2: //Notifications
				break;
		}

		if(Preloaded_Load_Screen_Functions.size() == 0)
			UI_Core.Screen_Handler.Set_Preloaded_Load_Screen_Functions(null);
		else
			UI_Core.Screen_Handler.Set_Preloaded_Load_Screen_Functions(Preloaded_Load_Screen_Functions);
	}

	float Menu_Swipe_Initial_X;
	float Menu_Swipe_Initial_Y;

	public View.OnTouchListener Menu_Swipe_Up_On_Touch_Listener = new View.OnTouchListener()
	{
		@Override public boolean onTouch(View view, MotionEvent event)
		{
			float Final_X = event.getRawX();
			float Final_Y = event.getRawY();
			float Delta_X = Final_X - Menu_Swipe_Initial_X;

			switch(event.getActionMasked())
			{
				case MotionEvent.ACTION_DOWN:
					Menu_Swipe_Initial_X = event.getRawX();
					Menu_Swipe_Initial_Y = event.getRawY();
					break;

				case MotionEvent.ACTION_UP:
					if(Menu_Swipe_Initial_Y > Final_Y &&
					   Math.abs(Delta_X) < SWIPE_THRESHOLD &&
					   view.getId() == R.id.range_layout &&
					   !UI_Core.is_Initializing) // Swipe Down to Up
						UI_Core.Settings_Handler.Show_Screen();
					break;
			}

			return true;
		}
	};
}
