package com.unscarred.find_my_friend.CreateProfile;

import com.unscarred.find_my_friend.Common.FileHandler;
import com.unscarred.find_my_friend.Common.FileServerHandler;
import com.unscarred.find_my_friend.Common.ThemeSettings;
import com.unscarred.find_my_friend.R;
import com.unscarred.find_my_friend.Common.ServerHandler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class EmailVerificationHandler
{
	private Context         Base_Context;
	private CreateProfile   Create_Profile;
	public  ServerHandler   Server_Handler;
	private FileHandler     File_Handler;
	private ThemeSettings   Theme_Settings;
	public  String          SERVER_URL = "http://find-my-friend.unscarredtechnology.co.za/v1";

	private TextView        Network_Error_Icon;

	public  View            Email_Verification_View;
	private EditText        Code_Edit_Text;
	private TextView        Resend_Button;
	private TextView        Done_Button;

	public boolean          Screen_Is_Visible = false;


	public EmailVerificationHandler(Context context, CreateProfile create_profile)
	{
		Base_Context = context;
		Server_Handler = new ServerHandler();
		File_Handler = new FileHandler(Base_Context);
		Theme_Settings = new ThemeSettings(Base_Context);

		Create_Profile = create_profile;

		File_Handler.Read_FMF_Settings();
	}

	public void Load_Screen()
	{
		Screen_Is_Visible = true;

		LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Email_Verification_View = layoutInflater.inflate(R.layout.email_verification, null);

		Network_Error_Icon = (TextView)((Activity)Base_Context).findViewById(R.id.main_network_error_text_view);
		Network_Error_Icon.setTypeface(Theme_Settings.Font_Bold);
		Network_Error_Icon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		Network_Error_Icon.setTextColor(Base_Context.getResources().getColor(R.color.color_3));
		Network_Error_Icon.setVisibility(View.INVISIBLE);

		Code_Edit_Text = (EditText)Email_Verification_View.findViewById(R.id.email_verification_code_edit_text);
		Resend_Button = (TextView)Email_Verification_View.findViewById(R.id.email_verification_resend_button);
		Done_Button = (TextView)Email_Verification_View.findViewById(R.id.email_verification_done_button);

		TextView Text_View;
		Text_View = (TextView)Email_Verification_View.findViewById(R.id.email_verification_title_text_view);
		Text_View.setTypeface(Theme_Settings.Font_Regular);

		Resend_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		Resend_Button.setTextColor(Base_Context.getResources().getColor(R.color.color_4));
		Resend_Button.setTypeface(Theme_Settings.Font_Light);
		Resend_Button.setOnClickListener(On_Click_Listener);

		Done_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		Done_Button.setTextColor(Base_Context.getResources().getColor(R.color.color_4));
		Done_Button.setTypeface(Theme_Settings.Font_Light);
		Done_Button.setOnClickListener(On_Click_Listener);

		Code_Edit_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		Code_Edit_Text.setTextColor(Base_Context.getResources().getColor(R.color.color_2));
		Code_Edit_Text.setTypeface(Theme_Settings.Font_Regular);
	}

	private View.OnClickListener On_Click_Listener = new View.OnClickListener()
	{
		@Override public void onClick(View view)
		{
			switch(view.getId())
			{
				case R.id.email_verification_resend_button:
					try
					{
						JSONObject jsonSend = new JSONObject();

						jsonSend.accumulate("action", "Get_Activation_Code");
						jsonSend.accumulate("name", File_Handler.FMF_Settings_Data.getString("Name"));
						jsonSend.accumulate("surname", File_Handler.FMF_Settings_Data.getString("Surname"));
						jsonSend.accumulate("email", File_Handler.FMF_Settings_Data.getString("Email"));

						Server_Handler.Queue_Server_Request(SERVER_URL, "init.php", jsonSend, Server_Response_Handler);
					}
					catch(JSONException e)
					{
						System.err.println("Error sending parsing JSONObject to SendServerRequest():");
						System.err.println("Caught JSON Exception: " + e.getMessage());
					}
					break;

				case R.id.email_verification_done_button:
					try
					{
						if(Integer.parseInt(Code_Edit_Text.getText().toString()) == File_Handler.FMF_Settings_Data.getInt("Code") &&
						   File_Handler.FMF_Settings_Data.getInt("Code") != 0)
						{
							try
							{
								JSONObject jsonSend = new JSONObject();

								jsonSend.accumulate("action", "Initialize");
								jsonSend.accumulate("name", File_Handler.FMF_Settings_Data.getString("Name"));
								jsonSend.accumulate("surname", File_Handler.FMF_Settings_Data.getString("Surname"));
								jsonSend.accumulate("email", File_Handler.FMF_Settings_Data.getString("Email"));

								Server_Handler.Queue_Server_Request(SERVER_URL, "init.php", jsonSend, Server_Response_Handler);
							}
							catch(JSONException e)
							{
								System.err.println("Error in EmailVerificationHandler: On_Click_Listener: email_verification_done_button:");
								System.err.println("Caught JSON Exception: " + e.getMessage());
							}
						}
						else
						{
							Toast.makeText(Base_Context, "The code you entered is not valid.", Toast.LENGTH_LONG).show();
						}
					}
					catch(JSONException e)
					{
						System.err.println("Error in EmailVerificationHandler: On_Click_Listener:");
						System.err.println("Caught JSONException: " + e.getMessage());
					}
					break;
			}
		}
	};

	public Handler Server_Response_Handler = new Handler()
	{
		@Override public void handleMessage(Message message)
		{
			try
			{
				Log.d("*** Nic Says ***", "Response From Server: " + message.getData().toString());

				if(message.getData().getBoolean("network_error"))
				{
					AlphaAnimation Alpha_Animation = new AlphaAnimation(1.0f, 0.0f);
					Alpha_Animation.setDuration(5000);
					Alpha_Animation.setFillAfter(true);

					Network_Error_Icon.startAnimation(Alpha_Animation);
				}
				else if(!message.getData().getString("json").equals(""))
				{
					JSONObject JSON_Receive = new JSONObject(message.getData().getString("json"));

					Log.d("Nic Says", "Service_Core.Server_Response_Functions.Get_Action(): " + JSON_Receive.getInt("action"));

					switch(JSON_Receive.getInt("action"))
					{
						case 100:
							if(JSON_Receive.getBoolean("success"))
							{
								File_Handler.FMF_Settings_Data.put("Step", "Run");
								File_Handler.FMF_Settings_Data.put("User_ID", JSON_Receive.getInt("user_id"));

								File_Handler.FMF_Settings_Data.remove("Code");
								File_Handler.FMF_Settings_Data.remove("Name");
								File_Handler.FMF_Settings_Data.remove("Surname");
								File_Handler.FMF_Settings_Data.remove("Email");

								File_Handler.Save_FMF_Settings();

								FileServerHandler File_Server_Handler = new FileServerHandler();

								File_Server_Handler.Queue_Server_Request("http://find-my-friend.unscarredtechnology.co.za/v1",
								                                         "profile_pic.php",
								                                         new File(Create_Profile.Base_Context.getFilesDir().getPath() + "/profile_pic.jpg"),
								                                         File_Handler.FMF_Settings_Data.getString("User_ID"),
								                                         File_Handler.FMF_Settings_Data.getString("User_ID") + ".jpg",
								                                         Create_Profile.Activity_Message_Handler);

								File_Server_Handler.Queue_Server_Request("http://find-my-friend.unscarredtechnology.co.za/v1",
								                                         "profile_pic.php",
								                                         new File(Create_Profile.Base_Context.getFilesDir().getPath() + "/profile_pic_thumb.jpg"),
								                                         File_Handler.FMF_Settings_Data.getString("User_ID"),
								                                         File_Handler.FMF_Settings_Data.getString("User_ID") + "_thumb.jpg",
								                                         Create_Profile.Activity_Message_Handler);

								Bundle bundle = new Bundle();
								Message bundle_message = new Message();

								bundle.putInt("action", 100);
								bundle_message.setData(bundle);

								Create_Profile.Activity_Message_Handler.sendMessage(bundle_message);
							}
							break;

						case 201:
							String str = "Your Code has been emailed to '" + File_Handler.FMF_Settings_Data.getString("Email") + "'.";
							Show_Toast(str);
							break;

						default: Log.d("Nic Says", "ERROR: Unknown response from server."); break;
					}
				}
			}
			catch(JSONException e)
			{
				System.err.println("Error in EmailVerificationHandler: Server_Response_Handler:");
				System.err.println("Caught JSON Exception: " + e.getMessage());
			}
			catch(NullPointerException e)
			{
				System.err.println("Error in EmailVerificationHandler: Server_Response_Handler:");
				System.err.println("Caught Null Pointer Exception: " + e.getMessage());
			}
		}
	};

	private void Show_Toast(String message)
	{
		Toast toast = Toast.makeText(Base_Context, message, Toast.LENGTH_SHORT);
		TextView text_view = (TextView) toast.getView().findViewById(android.R.id.message);
		if(text_view != null) text_view.setGravity(Gravity.CENTER);

		toast.show();
	}

	public void Kill_Screen()
	{
		Screen_Is_Visible = false;

		Email_Verification_View = null;

		Code_Edit_Text = null;
		Resend_Button = null;
		Done_Button = null;

	}
}
