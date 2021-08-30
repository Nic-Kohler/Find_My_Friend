package com.unscarred.find_my_friend.UI.Settings;

import android.app.Activity;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.unscarred.find_my_friend.Common.FileHandler;
import com.unscarred.find_my_friend.Common.ThemeSettings;
import com.unscarred.find_my_friend.CreateProfile.CreateProfile;
import com.unscarred.find_my_friend.UI.UICore;
import com.unscarred.find_my_friend.CreateProfile.EmailVerificationHandler;
import com.unscarred.find_my_friend.Common.ServerHandler;
import com.unscarred.find_my_friend.R;

import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class ProfileSettingsHandler
{
	private Context         Base_Context;
	private CreateProfile   Create_Profile;

	public  View            Profile_Settings_View;
	public  ViewSwitcher    View_Switcher;
	public  ScrollView      Scroll_View;
	private LinearLayout    Header_Layout;
	private EditText        Name_Edit_Text;
	private EditText        Surname_Edit_Text;
	private EditText        Email_Edit_Text;
	private TextView        Select_Image_Button;
	public  LinearLayout    Profile_Pic_Layout;
	public  ImageView       Profile_Pic_Image_View;
	private TextView        Done_Button;

	public boolean          Screen_Is_Visible = false;

	final  int              PROFILE_PIC_DIMENSION = 200;


	public  String           Raw_Profile_Pic_Path;
	public  int              Raw_Profile_Pic_Width;
	public  int              Raw_Profile_Pic_Height;

	public String                   SERVER_URL = "http://find-my-friend.unscarredtechnology.co.za/v1";
	public ProfilePicEditorHandler  Profile_Pic_Editor;
	public ServerHandler            Server_Handler;
	public FileHandler              File_Handler;
	public ThemeSettings            Theme_Settings;


	public ProfileSettingsHandler(Context context, CreateProfile create_profile)
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
		Profile_Settings_View = layoutInflater.inflate(R.layout.profile_settings, null);

		View_Switcher = (ViewSwitcher)Profile_Settings_View.findViewById(R.id.settings_profile_view_switcher);
		Scroll_View = (ScrollView)Profile_Settings_View.findViewById(R.id.settings_profile_scroll_view);
		Header_Layout = (LinearLayout)Profile_Settings_View.findViewById(R.id.settings_profile_header_layout);
		Name_Edit_Text = (EditText)Profile_Settings_View.findViewById(R.id.settings_profile_name_edit_text);
		Surname_Edit_Text = (EditText)Profile_Settings_View.findViewById(R.id.settings_profile_surname_edit_text);
		Email_Edit_Text = (EditText)Profile_Settings_View.findViewById(R.id.settings_profile_email_edit_text);
		Select_Image_Button = (TextView)Profile_Settings_View.findViewById(R.id.settings_profile_select_image_button);
		Profile_Pic_Layout = (LinearLayout)Profile_Settings_View.findViewById(R.id.settings_profile_profile_pic_layout);
		Profile_Pic_Image_View = (ImageView)Profile_Settings_View.findViewById(R.id.settings_profile_profile_pic_image_view);
		Done_Button = (TextView)Profile_Settings_View.findViewById(R.id.settings_profile_done_button);

		Set_Profile_Pic(null);

		TextView Text_View;
		Text_View = (TextView)Profile_Settings_View.findViewById(R.id.settings_profile_title_text_view);
		Text_View.setTypeface(Theme_Settings.Font_Regular);

		Select_Image_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		Select_Image_Button.setTextColor(Base_Context.getResources().getColor(R.color.color_4));
		Select_Image_Button.setTypeface(Theme_Settings.Font_Light);
		Select_Image_Button.setOnClickListener(On_Click_Listener);

		Done_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		Done_Button.setTextColor(Base_Context.getResources().getColor(R.color.color_4));
		Done_Button.setTypeface(Theme_Settings.Font_Light);
		Done_Button.setOnClickListener(On_Click_Listener);

		Name_Edit_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		Name_Edit_Text.setTextColor(Base_Context.getResources().getColor(R.color.color_2));
		Name_Edit_Text.setTypeface(Theme_Settings.Font_Regular);

		Surname_Edit_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		Surname_Edit_Text.setTextColor(Base_Context.getResources().getColor(R.color.color_2));
		Surname_Edit_Text.setTypeface(Theme_Settings.Font_Regular);

		Email_Edit_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		Email_Edit_Text.setTextColor(Base_Context.getResources().getColor(R.color.color_2));
		Email_Edit_Text.setTypeface(Theme_Settings.Font_Regular);

		View_Switcher.setInAnimation(Theme_Settings.Left_In);
		View_Switcher.setOutAnimation(Theme_Settings.Left_Out);

		/*
		Set_Facebook_Profile();

		Core.Settings_Handler.Settings_View_Switcher.addView(Profile_Settings_View);

		Core.Settings_Handler.Settings_View_Switcher.setInAnimation(Core.Settings_Handler.Left_In);
		Core.Settings_Handler.Settings_View_Switcher.setOutAnimation(Core.Settings_Handler.Left_Out);

		Core.Settings_Handler.Settings_View_Switcher.showNext();
		*/
	}

	public void Set_Dependencies(UICore ui_core)
	{
		Header_Layout.setOnTouchListener(ui_core.On_Touch_Listener);
	}

	/*
	private void Set_Facebook_Profile()
	{
		if(Core.FMF_Service.Service_Core.Facebook_Handler.Facebook_Profile != null)
		{
			if(Core.FMF_Service.Service_Core.Facebook_Handler.Facebook_Profile_Pic != null)
				Profile_Pic_Image_View.setImageBitmap(Core.Users_UI_Handler.Profile_Pic_Handler
						                                      .Get_Masked_Profile_Pic(Core.FMF_Service.Service_Core.Facebook_Handler.Facebook_Profile_Pic,
						                                                              PROFILE_PIC_DIMENSION));
			else
				Profile_Pic_Image_View.setImageBitmap(Core.Users_UI_Handler.Profile_Pic_Handler.Create_All_User_Profile_Pic(PROFILE_PIC_DIMENSION));
		}
		else Profile_Pic_Layout.setVisibility(View.GONE);
	}

	public Handler Facebook_Profile_Pic_Update_Handler = new Handler()
	{
		@Override public void handleMessage(Message message)
		{
			switch(message.getData().getInt("action"))
			{
				case 0:
					Set_Profile_Pic(Core.FMF_Service.Service_Core.Facebook_Handler.Facebook_Profile_Pic);

					Profile_Pic_Layout.setVisibility(View.VISIBLE);

					Core.Settings_Handler.Profile_Pic_Image_View.setImageBitmap(Core.Users_UI_Handler.Profile_Pic_Handler
							                                      .Get_Masked_Profile_Pic(Core.FMF_Service.Service_Core.Facebook_Handler.Facebook_Profile_Pic, 50));
					break;
			}
		}
	};
	*/

	private ProfileSettingsHandler Get_Instance(){ return this; }

	private View.OnClickListener On_Click_Listener = new View.OnClickListener()
	{
		@Override public void onClick(View view)
		{
			switch(view.getId())
			{
				case R.id.settings_profile_select_image_button:
					Profile_Pic_Editor = new ProfilePicEditorHandler(Base_Context, Get_Instance());

					Show_File_Chooser();
					break;

				case R.id.settings_profile_done_button:
					try
					{
						JSONObject jsonSend = new JSONObject();

						String name    = Name_Edit_Text.getText().toString();
						String surname = Surname_Edit_Text.getText().toString();
						String email   = Email_Edit_Text.getText().toString();

						name    = "Nic_JAVA";
						surname = "Kohler_JAVA";
						email   = "kohler.nic@gmail.com";

						jsonSend.accumulate("action", "Get_Activation_Code");
						jsonSend.accumulate("name", name);
						jsonSend.accumulate("surname", surname);
						jsonSend.accumulate("email", email);

						Server_Handler.Queue_Server_Request(SERVER_URL, "init.php", jsonSend, Message_Response_Handler);
					}
					catch(JSONException e)
					{
						System.err.println("Error sending parsing JSONObject to SendServerRequest():");
						System.err.println("Caught JSON Exception: " + e.getMessage());
					}
					break;
			}
		}
	};

	public void Save_Editor_Profile_Pic_to_File(Intent data)
	{
		String[] media_data = { MediaStore.Images.Media.DATA };
		Cursor cursor = Base_Context.getContentResolver().query(data.getData(), media_data, null, null, null);
		cursor.moveToFirst();

		File Profile_Pic_File = new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
		Raw_Profile_Pic_Path  = Profile_Pic_File.getAbsolutePath();

		Bitmap Profile_Pic_Bitmap =  BitmapFactory.decodeFile(Raw_Profile_Pic_Path);
		Raw_Profile_Pic_Width  = Profile_Pic_Bitmap.getWidth();
		Raw_Profile_Pic_Height = Profile_Pic_Bitmap.getHeight();

		int max_limit = 1024;
		int min_limit = Theme_Settings.Screen_Width - Theme_Settings.DP(60);
		float scale = 1f;

		if(Raw_Profile_Pic_Width < min_limit && Raw_Profile_Pic_Height < min_limit)
		{
			if(Raw_Profile_Pic_Width > Raw_Profile_Pic_Height) scale = (float)Raw_Profile_Pic_Width / (float)min_limit;
			if(Raw_Profile_Pic_Height > Raw_Profile_Pic_Width) scale = (float)Raw_Profile_Pic_Height / (float)min_limit;
		}

		if(Raw_Profile_Pic_Width > max_limit || Raw_Profile_Pic_Height > max_limit)
		{
			if(Raw_Profile_Pic_Width > Raw_Profile_Pic_Height) scale = (float)max_limit / (float)Raw_Profile_Pic_Width;
			if(Raw_Profile_Pic_Height > Raw_Profile_Pic_Width) scale = (float)max_limit / (float)Raw_Profile_Pic_Height;
		}

		Save_Profile_Pic(Bitmap.createScaledBitmap(Profile_Pic_Bitmap,
		                                           Math.round(Raw_Profile_Pic_Width * scale),
		                                           Math.round(Raw_Profile_Pic_Height * scale),
		                                           false), "profile_pic.jpg");
	}

	public void Save_Profile_Pic(Bitmap Profile_Pic_Bitmap, String Profile_Pic_File_Name)
	{
		try
		{
			File profile_pic_file = new File(Base_Context.getFilesDir().getPath(), Profile_Pic_File_Name);
			OutputStream file_output_stream = new FileOutputStream(profile_pic_file);

			Profile_Pic_Bitmap.compress(Bitmap.CompressFormat.JPEG, 85, file_output_stream);
			file_output_stream.flush();
			file_output_stream.close();
		}
		catch(FileNotFoundException e)
		{
			System.err.println("Error reading Response in ProfileSettingsHandler: Save_Profile_Pic:");
			System.err.println("Caught File Not Found Exception: " + e.getMessage());
		}
		catch(IOException e)
		{
			System.err.println("Error reading Response in ProfileSettingsHandler: Save_Profile_Pic:");
			System.err.println("Caught IO Exception: " + e.getMessage());
		}
	}

	private void Handle_Javascript_Profile_Pic_Details(String params)
	{
		View_Switcher.setInAnimation(Theme_Settings.Right_In);
		View_Switcher.setOutAnimation(Theme_Settings.Right_Out);

		View_Switcher.showPrevious();

		View_Switcher.removeViewAt(1);
		Profile_Pic_Editor.Kill_Screen();

		try
		{
			JSONObject pp_editor_parameters = new JSONObject(params);
			Bitmap Profile_Pic_Bitmap = BitmapFactory.decodeFile(Raw_Profile_Pic_Path);
			Bitmap Profile_Pic_Bitmap_Thumb;

			float scale = (float)Raw_Profile_Pic_Width / (float)pp_editor_parameters.getInt("image_width");
			int   x     = Math.round((float)pp_editor_parameters.getInt("crop_block_left") * scale);
			int   y     = Math.round((float)pp_editor_parameters.getInt("crop_block_top") * scale);
			int   dim   = Math.round((float)pp_editor_parameters.getInt("crop_block_dim") * scale);

			Profile_Pic_Bitmap = Bitmap.createBitmap(Profile_Pic_Bitmap, x, y, dim, dim);
			Profile_Pic_Bitmap = Bitmap.createScaledBitmap(Profile_Pic_Bitmap, 256, 256, true);
			Profile_Pic_Bitmap_Thumb = Bitmap.createScaledBitmap(Profile_Pic_Bitmap, 64, 64, true);

			Save_Profile_Pic(Profile_Pic_Bitmap, "profile_pic.jpg");
			Save_Profile_Pic(Profile_Pic_Bitmap_Thumb, "profile_pic_thumb.jpg");
			Set_Profile_Pic(Profile_Pic_Bitmap);
		}
		catch(JSONException e)
		{
			System.err.println("Error reading Response in ProfileSettingsHandler: Message_Handler");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	public Bitmap Create_All_User_Profile_Pic(int Dimension)
	{
		return Get_Masked_Profile_Pic(Convert_Drawable_To_Bitmap(Base_Context.getResources().getDrawable(R.drawable.no_avatar)), Dimension);
	}

	public void Set_Profile_Pic(final Bitmap Profile_Pic)
	{
		if(Profile_Pic != null)
			Profile_Pic_Image_View.setImageBitmap(Get_Masked_Profile_Pic(Profile_Pic, PROFILE_PIC_DIMENSION));
		else
			Profile_Pic_Image_View.setImageBitmap(Create_All_User_Profile_Pic(PROFILE_PIC_DIMENSION));
	}

	public Bitmap Get_Masked_Profile_Pic(Bitmap Temp_Bitmap, int Dimension)
	{
		Temp_Bitmap = Bitmap.createScaledBitmap(Temp_Bitmap, Theme_Settings.DP(Dimension), Theme_Settings.DP(Dimension), true);

		Bitmap Profile_Pic_Bitmap;
		if(Temp_Bitmap.isMutable()) Profile_Pic_Bitmap = Temp_Bitmap; else Profile_Pic_Bitmap = Temp_Bitmap.copy(Bitmap.Config.ARGB_8888, true);

		Profile_Pic_Bitmap.setHasAlpha(true);

		Canvas Profile_Pic_Canvas = new Canvas(Profile_Pic_Bitmap);

		Bitmap Profile_Pic_Mask_Bitmap = Convert_Drawable_To_Bitmap(Base_Context.getResources().getDrawable(R.drawable.profile_pic_mask));
		Profile_Pic_Mask_Bitmap = Bitmap.createScaledBitmap(Profile_Pic_Mask_Bitmap, Theme_Settings.DP(Dimension), Theme_Settings.DP(Dimension), true);

		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		Profile_Pic_Canvas.drawBitmap(Profile_Pic_Mask_Bitmap, 0, 0, paint);

		Profile_Pic_Mask_Bitmap.recycle();

		return Profile_Pic_Bitmap;
	}

	public Bitmap Convert_Drawable_To_Bitmap(Drawable drawable)
	{
		if(drawable instanceof BitmapDrawable) return ((BitmapDrawable)drawable).getBitmap();

		Bitmap Temp_Bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Bitmap bitmap;
		if(Temp_Bitmap.isMutable()) bitmap = Temp_Bitmap; else { bitmap = Temp_Bitmap.copy(Bitmap.Config.ARGB_8888, true); Temp_Bitmap.recycle(); }

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}


	public Handler Message_Response_Handler = new Handler()
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

					TextView Network_Error_Icon = (TextView)((Activity)Base_Context).findViewById(android.R.id.content).findViewById(R.id.main_network_error_text_view);
					Network_Error_Icon.setTypeface(Theme_Settings.Font_Bold);
					Network_Error_Icon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
					Network_Error_Icon.setTextColor(Base_Context.getResources().getColor(R.color.color_3));
					Network_Error_Icon.setVisibility(View.INVISIBLE);

					Network_Error_Icon.startAnimation(Alpha_Animation);
				}
				else if(!message.getData().getString("json").equals(""))
				{
					JSONObject JSON_Response = new JSONObject(message.getData().getString("json"));

					Log.d("Nic Says", "JSON_Response - Action: " + JSON_Response.getInt("action"));

					switch(JSON_Response.getInt("action"))
					{
						case 201:
							Log.d("Nic Says", "JSON_Response - Code:    " + JSON_Response.getInt("code"));
							Log.d("Nic Says", "JSON_Response - Success: " + JSON_Response.getBoolean("success"));

							String name    = Name_Edit_Text.getText().toString();
							String surname = Surname_Edit_Text.getText().toString();
							String email   = Email_Edit_Text.getText().toString();

							name    = "Nic_JAVA";
							surname = "Kohler_JAVA";
							email   = "kohler.nic@gmail.com";

							File_Handler.FMF_Settings_Data.put("Step", "Email_Verification");
							File_Handler.FMF_Settings_Data.put("Code", JSON_Response.getInt("code"));
							File_Handler.FMF_Settings_Data.put("Name", name);
							File_Handler.FMF_Settings_Data.put("Surname", surname);
							File_Handler.FMF_Settings_Data.put("Email", email);

							File_Handler.Save_FMF_Settings();

							Toast.makeText(Base_Context, "Your Code has been emailed to '" + email + "'.", Toast.LENGTH_LONG).show();

							EmailVerificationHandler Email_Verification_Handler = new EmailVerificationHandler(Base_Context, Create_Profile);

							View_Switcher.setInAnimation(AnimationUtils.loadAnimation(Base_Context, R.anim.fade_in));
							View_Switcher.setOutAnimation(AnimationUtils.loadAnimation(Base_Context, R.anim.fade_out));

							Email_Verification_Handler.Load_Screen();
							View_Switcher.addView(Email_Verification_Handler.Email_Verification_View);

							View_Switcher.showNext();
							break;

						case 299:
							Handle_Javascript_Profile_Pic_Details(JSON_Response.getString("params"));
							break;

						default: Log.d("Nic Says", "ERROR: Unknown response from server."); break;
					}
				}
			}
			catch(JSONException e)
			{
				System.err.println("Error reading Response in LogInHandler: Message_Handler:");
				System.err.println("Caught JSON Exception: " + e.getMessage());
			}
		}
	};

	public void Show_File_Chooser()
	{
		Bundle bundle = new Bundle();
		Message bundle_message = new Message();

		bundle.putInt("action", 102);
		bundle_message.setData(bundle);

		Create_Profile.Activity_Message_Handler.sendMessage(bundle_message);
	}

	public void Kill_Screen()
	{
		Screen_Is_Visible = false;

		Profile_Settings_View = null;

		View_Switcher = null;
		Scroll_View = null;
		Select_Image_Button = null;
		Profile_Pic_Image_View = null;
		Profile_Pic_Layout = null;
		Done_Button = null;
	}
}
