package com.unscarred.find_my_friend.UI.Settings;

import com.unscarred.find_my_friend.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfilePicEditorHandler
{
	private Context                 Base_Context;
	private ProfileSettingsHandler  Profile_Settings_Handler;

	public  View                    Profile_Pic_Editor_View;
	private RelativeLayout          Main_Layout;
	private LinearLayout            Border_Layout;
	private WebView                 PP_Editor_Web_View;
	private TextView                Done_Button;

	public  boolean Screen_Is_Visible = false;
	private boolean Image_Details_Received = false;

	public ProfilePicEditorHandler(Context context, ProfileSettingsHandler psh)
	{
		Base_Context = context;
		Profile_Settings_Handler = psh;
	}

	public class JavaScriptInterface
	{
		@JavascriptInterface public void Show_Message(String message)
		{
			Log.d("Nic Says", "Javascript Says: " + message);
		}

		@JavascriptInterface public void Set_PP_Editor_Parameters(String pp_editor_params)
		{
			if(!Image_Details_Received)
			{
				try
				{
					Image_Details_Received = true;
					Bundle bundle = new Bundle();
					Message message = new Message();
					JSONObject JSON_Send = new JSONObject();

					JSON_Send.put("action", 299);
					JSON_Send.put("params", pp_editor_params);

					bundle.putString("json", JSON_Send.toString());
					message.setData(bundle);

					Profile_Settings_Handler.Message_Response_Handler.sendMessage(message);
				}
				catch(JSONException e)
				{
					System.err.println("Error reading Response in ProfilePicEditorHandler: JavaScriptInterface: Set_PP_Editor_Parameters:");
					System.err.println("Caught JSON Exception: " + e.getMessage());
				}
			}
		}
	}

	public void Load_Screen(final String Profile_Pic_Path, final int Width, final int Height)
	{
		Screen_Is_Visible = true;

		LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Profile_Pic_Editor_View = layoutInflater.inflate(R.layout.profile_pic_editor, null);

		Main_Layout = (RelativeLayout)Profile_Pic_Editor_View.findViewById(R.id.pp_editor_main_layout);
		Border_Layout = (LinearLayout)Profile_Pic_Editor_View.findViewById(R.id.pp_editor_border_layout);
		PP_Editor_Web_View = (WebView)Profile_Pic_Editor_View.findViewById(R.id.pp_editor_web_view);
		Done_Button = (TextView)Profile_Pic_Editor_View.findViewById(R.id.pp_editor_done_button);

		Done_Button.setOnTouchListener(On_Touch_Listener);

		PP_Editor_Web_View.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		PP_Editor_Web_View.getSettings().setJavaScriptEnabled(true);
		PP_Editor_Web_View.getSettings().setDomStorageEnabled(true);
		PP_Editor_Web_View.loadUrl("file:///android_asset/pp_editor/pp_editor.html");
		PP_Editor_Web_View.setBackgroundColor(Color.parseColor("#00000000"));
		PP_Editor_Web_View.setOnTouchListener(On_Touch_Listener);
		PP_Editor_Web_View.addJavascriptInterface(new JavaScriptInterface(), "PP_Editor_Android");

		PP_Editor_Web_View.setWebViewClient(new WebViewClient()
		{
			@Override public void onPageFinished(WebView web_view, String url)
			{
				super.onPageFinished(web_view, url);

				PP_Editor_Web_View.loadUrl("javascript:Set_Profile_Pic('" + Profile_Pic_Path + "', " + Width + ", " + Height + ")");
			}
		});

		int dim = Profile_Settings_Handler.Theme_Settings.Screen_Width - Profile_Settings_Handler.Theme_Settings.DP(60);
		ViewGroup.LayoutParams layout_params = Border_Layout.getLayoutParams();

		layout_params.width = dim;
		layout_params.height = dim;

		Border_Layout.setLayoutParams(layout_params);

		TextView Text_View;
		Text_View = (TextView)Profile_Pic_Editor_View.findViewById(R.id.pp_editor_title_text_view);
		Text_View.setTypeface(Profile_Settings_Handler.Theme_Settings.Font_Regular);



		ViewTreeObserver View_Tree_Observer = Profile_Pic_Editor_View.getViewTreeObserver();
		View_Tree_Observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
		{
			@Override public void onGlobalLayout()
			{
				int max_dim = Profile_Settings_Handler.Theme_Settings.Screen_Width - Profile_Settings_Handler.Theme_Settings.DP(60);
				int top_border_height = Math.round((Main_Layout.getHeight() - max_dim) / 2);

				Border_Layout.setY(top_border_height);

				Profile_Pic_Editor_View.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});

	}

	private int Initial_Primary_X;
	private int Initial_Primary_Y;
	private int Initial_Secondary_X = 0;
	private int Initial_Secondary_Y = 0;
	private boolean Action_Move_Allowed = false;

	private View.OnTouchListener On_Touch_Listener = new View.OnTouchListener()
	{
		@Override public boolean onTouch(View view, MotionEvent event)
		{
			switch(view.getId())
			{
				case R.id.pp_editor_web_view:
					switch(event.getAction() & MotionEvent.ACTION_MASK)
					{
						case MotionEvent.ACTION_DOWN:
							Action_Move_Allowed = true;

							Initial_Primary_X = Math.round(event.getX(0));
							Initial_Primary_Y = Math.round(event.getY(0));

							PP_Editor_Web_View.loadUrl("javascript:Set_Initial_Primary_XY()");
							break;

						case MotionEvent.ACTION_POINTER_DOWN:
							if(event.getPointerCount() < 3)
							{
								Initial_Secondary_X = Math.round(event.getX(1));
								Initial_Secondary_Y = Math.round(event.getY(1));

								PP_Editor_Web_View.loadUrl("javascript:Set_Initial_Dimensions()");
							}
							break;

						case MotionEvent.ACTION_POINTER_UP:
							Action_Move_Allowed = false;

							Initial_Secondary_X = 0;
							Initial_Secondary_Y = 0;
							break;

						case MotionEvent.ACTION_MOVE:
							if(event.getPointerCount() == 1 && Action_Move_Allowed) Handle_Single_Touch_Event(Math.round(event.getX(0)), Math.round(event.getY(0)));

							if(event.getPointerCount() == 2) Handle_Multi_Touch_Event(Math.round(event.getX(0)), Math.round(event.getY(0)),
							                                                          Math.round(event.getX(1)), Math.round(event.getY(1)));
							break;
					}
					break;

				case R.id.pp_editor_done_button:
					PP_Editor_Web_View.loadUrl("javascript:Get_Profile_Pic_Parameters()");
					break;
			}

			return true;
		}
	};

	private void Handle_Single_Touch_Event(int X, int Y)
	{
		int diff_x = Profile_Settings_Handler.Theme_Settings.PX(Initial_Primary_X - X);
		int diff_y = Profile_Settings_Handler.Theme_Settings.PX(Initial_Primary_Y - Y);

		PP_Editor_Web_View.loadUrl("javascript:Action_Move(" + diff_x + ", " + diff_y + ")");
	}

	private void Handle_Multi_Touch_Event(int Primary_X, int Primary_Y, int Secondary_X, int Secondary_Y)
	{
		int Initial_Delta_X = Initial_Primary_X - Initial_Secondary_X;
		int Initial_Delta_Y = Initial_Primary_Y - Initial_Secondary_Y;
		int Delta_X = Primary_X - Secondary_X;
		int Delta_Y = Primary_Y - Secondary_Y;

		double Initial_Hypotenuse = Math.abs(Math.sqrt((Initial_Delta_X * Initial_Delta_X) + (Initial_Delta_Y * Initial_Delta_Y)));
		double Hypotenuse = Math.abs(Math.sqrt((Delta_X * Delta_X) + (Delta_Y * Delta_Y)));

		double scale = Hypotenuse / Initial_Hypotenuse;

		PP_Editor_Web_View.loadUrl("javascript:Action_Scale(" + scale + ")");
	}

	public void Kill_Screen()
	{
		Screen_Is_Visible = false;
		Profile_Pic_Editor_View = null;

		Main_Layout = null;
		Border_Layout = null;
		PP_Editor_Web_View = null;
	}

}
