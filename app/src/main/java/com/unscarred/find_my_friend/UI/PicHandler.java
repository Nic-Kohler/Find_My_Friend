package com.unscarred.find_my_friend.UI;

import com.unscarred.find_my_friend.R;
import com.unscarred.find_my_friend.Service.DataHandler;

import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;

public class PicHandler
{
	public DataHandler Data_Handler;

	private ArrayList<Pic>              Pic_Array             = new ArrayList<>();
	private ArrayList<DownloadThread>   Download_Thread_Array = new ArrayList<>();


	public PicHandler(DataHandler data_handler){ Data_Handler = data_handler; }

	public void  Load_Pic_Array()
	{
		//Profile Pics
		String Pic_Path = Data_Handler.Base_Context.getFilesDir().getPath() + "/Profile_Pics";

		File file = new File(Pic_Path);
		File profile_files[] = file.listFiles();

		Log.d("Nic Says", "Profile Pics Array");
		Log.d("Nic Says", "==================");

		for(int i = 0; i < profile_files.length; i++)
		{
			Log.d("Nic Says", "FileName (" + i + "): " + profile_files[i].getName());
			Log.d("Nic Says", "Pic_ID:        " + Integer.parseInt(profile_files[i].getName().replace("_thumb.jpg", "")));
			Log.d("Nic Says", "Pic_Type:      Profile");
			Log.d("Nic Says", "Absolute Path: " + profile_files[i].getAbsolutePath());

			Pic_Array.add(new Pic(Integer.parseInt(profile_files[i].getName().replace("_thumb.jpg", "")), 0, BitmapFactory.decodeFile(profile_files[i].getAbsolutePath())));
		}

		Check_Profile_Pics();

		Finalize_Initialization();
	}

	public void Check_Profile_Pics()
	{
		try
		{
			JSONArray Friends = Data_Handler.Friends;
			boolean no_downloads_started = true;

			for(int i = 0; i < Friends.length(); i++)
			{
				if(Friend_Profile_Pic_Does_Not_Exist(Friends.getJSONObject(i).getInt("profile_id")))
				{
					no_downloads_started = false;
					Download_Pic(Friends.getJSONObject(i).getInt("profile_id"), 0);
				}
			}

			if(no_downloads_started) Remove_Old_Friends();
		}
		catch(JSONException e)
		{
			System.err.println("Error in PicHandler: Check_Profile_Pics:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	private void Remove_Old_Friends()
	{
		try
		{
			for(int i = Pic_Array.size() - 1; i > -1; i--)
			{
				boolean friend_not_found = true;

				for(int j = 0; j < Data_Handler.Friends.length() && friend_not_found; j++)
					if(Pic_Array.get(i).Get_Pic_ID() == Data_Handler.Friends.getJSONObject(j).getInt("profile_id") && Pic_Array.get(i).Get_Pic_Type() == 0)
						friend_not_found = false;

				if(friend_not_found)
				{
					File Friend_Profile_Pic = new File(Data_Handler.Base_Context.getFilesDir().getPath() + "/Profile_Pics", Pic_Array.get(i).Get_Pic_ID() + "_thumb.jpg");
					Friend_Profile_Pic.delete();

					Pic_Array.remove(i);
				}
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in PicHandler: Remove_Old_Friends:");
			System.err.println("Caught JSON Exception: " + e.getMessage());
		}
	}

	private void Finalize_Initialization()
	{
		if(!Data_Handler.First_Run_Complete)
		{
			Data_Handler.First_Run_Complete = true;
			Data_Handler.Service_Core.Finalize_Initialization();
		}
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

	public Bitmap Create_Default_Profile_Pic(int Dimension)
	{
		;
		return Get_Masked_Profile_Pic(Convert_Drawable_To_Bitmap(ContextCompat.getDrawable(Data_Handler.Base_Context, R.drawable.no_avatar)), Dimension);
	}

	public Bitmap Create_Default_Location_Marker(int Dimension)
	{
		return Get_Masked_Location_Marker(Convert_Drawable_To_Bitmap(ContextCompat.getDrawable(Data_Handler.Base_Context, R.drawable.location_marker)), Dimension);
	}

	public Bitmap Get_Masked_Profile_Pic(Bitmap Temp_Bitmap, int Dimension)
	{
		Temp_Bitmap = Bitmap.createScaledBitmap(Temp_Bitmap,
		                                        Data_Handler.Service_Core.UI_Core.Theme_Settings.DP(Dimension),
		                                        Data_Handler.Service_Core.UI_Core.Theme_Settings.DP(Dimension), true);

		Bitmap Profile_Pic_Bitmap;
		if(Temp_Bitmap.isMutable()) Profile_Pic_Bitmap = Temp_Bitmap; else Profile_Pic_Bitmap = Temp_Bitmap.copy(Bitmap.Config.ARGB_8888, true);

		Profile_Pic_Bitmap.setHasAlpha(true);

		Canvas Profile_Pic_Canvas = new Canvas(Profile_Pic_Bitmap);

		Bitmap Profile_Pic_Mask_Bitmap = Convert_Drawable_To_Bitmap(ContextCompat.getDrawable(Data_Handler.Base_Context, R.drawable.profile_pic_mask));
		Profile_Pic_Mask_Bitmap = Bitmap.createScaledBitmap(Profile_Pic_Mask_Bitmap,
		                                                    Data_Handler.Service_Core.UI_Core.Theme_Settings.DP(Dimension),
		                                                    Data_Handler.Service_Core.UI_Core.Theme_Settings.DP(Dimension), true);

		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		Profile_Pic_Canvas.drawBitmap(Profile_Pic_Mask_Bitmap, 0, 0, paint);

		Profile_Pic_Mask_Bitmap.recycle();

		return Profile_Pic_Bitmap;
	}

	public Bitmap Get_Masked_Location_Marker(Bitmap Temp_Bitmap, int Dimension)
	{
		Temp_Bitmap = Bitmap.createScaledBitmap(Temp_Bitmap,
		                                        Data_Handler.Service_Core.UI_Core.Theme_Settings.DP(Dimension),
		                                        Data_Handler.Service_Core.UI_Core.Theme_Settings.DP(Dimension), true);

		Bitmap Location_Marker_Bitmap;
		if(Temp_Bitmap.isMutable()) Location_Marker_Bitmap = Temp_Bitmap; else Location_Marker_Bitmap = Temp_Bitmap.copy(Bitmap.Config.ARGB_8888, true);

		Location_Marker_Bitmap.setHasAlpha(true);

		Canvas Location_Marker_Canvas = new Canvas(Location_Marker_Bitmap);

		Bitmap Profile_Pic_Mask_Bitmap = Convert_Drawable_To_Bitmap(ContextCompat.getDrawable(Data_Handler.Base_Context, R.drawable.location_marker_mask));
		Profile_Pic_Mask_Bitmap = Bitmap.createScaledBitmap(Profile_Pic_Mask_Bitmap,
		                                                    Data_Handler.Service_Core.UI_Core.Theme_Settings.DP(Dimension),
		                                                    Data_Handler.Service_Core.UI_Core.Theme_Settings.DP(Dimension), true);

		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		Location_Marker_Canvas.drawBitmap(Profile_Pic_Mask_Bitmap, 0, 0, paint);

		Profile_Pic_Mask_Bitmap.recycle();

		return Location_Marker_Bitmap;
	}

	public void Download_Pic(final int Pic_ID, final int Pic_Type)
	{
		try
		{
			final String Pic_File_Name = String.valueOf(Pic_ID) + "_thumb.jpg";
			final String Pic_Folder;
			if(Pic_Type == 0) Pic_Folder = "Profile_Pictures"; else Pic_Folder = "Location_Pics";
			String url_string = "http://unscarredtechnology.co.za/Find_My_Friend/" + Pic_Folder + "/" + Pic_File_Name;

			if(URLUtil.isValidUrl(url_string))
			{
				final URL url = new URL(url_string);

				Thread Download_Thread = new Thread(new Runnable()
				{
					@Override public void run()
					{
						try
						{
							Log.d("Nic Says", "Downloading File:");
							Log.d("Nic Says", "=================");
							Log.d("Nic Says", "Pic File Name: " + Pic_File_Name);
							Log.d("Nic Says", "Pic Type:      " + Pic_Type);
							Log.d("Nic Says", "Pic URL:       " + url.getContent());

							Bitmap Pic_Bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
							Pic_Array.add(new Pic(Pic_ID, Pic_Type, Pic_Bitmap));
							Save_Pic(Pic_Bitmap, Pic_Folder, Pic_File_Name);

							Bundle  bundle  = new Bundle();
							Message message = new Message();

							bundle.putInt("action", 0);
							bundle.putInt("pic_id", Pic_ID);
							message.setData(bundle);

							Pic_Message_Handler.sendMessage(message);
						}
						catch(MalformedURLException e)
						{
							System.err.println("Error PicHandler: Download_Pic:");
							System.err.println("Caught Malformed URL Exception: " + e.getMessage());
						}
						catch(IOException e)
						{
							System.err.println("Error PicHandler: Download_Pic:");
							System.err.println("Caught IO Exception: " + e.getMessage());
						}
					}
				});

				Download_Thread_Array.add(new DownloadThread(Pic_ID, Download_Thread));
				Download_Thread.start();
			}
		}
		catch(IOException e)
		{
			System.err.println("Error PicHandler: Download_Pic:");
			System.err.println("Caught IO Exception: " + e.getMessage());
		}
	}

	public void Save_Pic(Bitmap Pic_Bitmap, String Pic_Folder, String Pic_File_Name)
	{
		try
		{
			File Folder = new File(Data_Handler.Base_Context.getFilesDir().getPath() + "/" + Pic_Folder);
			if(!Folder.exists()) Folder.mkdirs();

			File         Pic_File   = new File(Folder, Pic_File_Name);
			OutputStream File_Output_Stream = new FileOutputStream(Pic_File);

			Pic_Bitmap.compress(Bitmap.CompressFormat.JPEG, 85, File_Output_Stream);
			File_Output_Stream.flush();
			File_Output_Stream.close();
		}
		catch(FileNotFoundException e)
		{
			System.err.println("Error in PicHandler: Save_Pic:");
			System.err.println("Caught File Not Found Exception: " + e.getMessage());
		}
		catch(IOException e)
		{
			System.err.println("Error in PicHandler: Save_Pic:");
			System.err.println("Caught IO Exception: " + e.getMessage());
		}
	}

	public Bitmap Get_Friend_Profile_Pic_Bitmap(int User_ID)
	{
		int index = -1;
		boolean profile_pic_not_found = true;

		for(int i = 0; i < Pic_Array.size() && profile_pic_not_found; i++)
		{
			if(Pic_Array.get(i).Get_Pic_ID() == User_ID && Pic_Array.get(i).Get_Pic_Type() == 0)
			{
				profile_pic_not_found = false;
				index = i;
			}
		}

		Log.d("Nic Says", "!!! User_ID: " + User_ID);
		Log.d("Nic Says", "!!! profile_pic_not_found: " + profile_pic_not_found);

		if(profile_pic_not_found) return null; else  return Pic_Array.get(index).Get_Bitmap();
	}

	public Bitmap Get_Location_Pic_Bitmap(int Location_ID)
	{
		int index = -1;
		boolean location_pic_not_found = true;

		for(int i = 0; i < Pic_Array.size() && location_pic_not_found; i++)
		{
			if(Pic_Array.get(i).Get_Pic_ID() == Location_ID && Pic_Array.get(i).Get_Pic_Type() == 1)
			{
				location_pic_not_found = false;
				index = i;
			}
		}

		if(location_pic_not_found)
			return Convert_Drawable_To_Bitmap(ContextCompat.getDrawable(Data_Handler.Base_Context, R.drawable.location_marker));
		else
			return Pic_Array.get(index).Get_Bitmap();
	}

	public Bitmap Get_User_Profile_Pic_Bitmap()
	{
		File profile_pic = new File(Data_Handler.Base_Context.getFilesDir().getPath() + "/profile_pic.jpg");

		return  BitmapFactory.decodeFile(profile_pic.getAbsolutePath(), new BitmapFactory.Options());
	}

	public boolean Friend_Profile_Pic_Does_Not_Exist(int Pic_ID)
	{
		boolean profile_pic_not_found = true;

		for(int i = 0; i < Pic_Array.size() && profile_pic_not_found; i++)
			if(Pic_Array.get(i).Get_Pic_ID() == Pic_ID && Pic_Array.get(i).Get_Pic_Type() == 0)
				profile_pic_not_found = false;

		return profile_pic_not_found;
	}

	public boolean Location_Pic_Does_Not_Exist(int Pic_ID)
	{
		boolean location_pic_not_found = true;

		for(int i = 0; i < Pic_Array.size() && location_pic_not_found; i++)
			if(Pic_Array.get(i).Get_Pic_ID() == Pic_ID && Pic_Array.get(i).Get_Pic_Type() == 1)
				location_pic_not_found = false;

		return location_pic_not_found;
	}

	private Handler Pic_Message_Handler = new Handler()
	{
		@Override public void handleMessage(Message message)
		{
			int Pic_ID = message.getData().getInt("pic_id");

			switch(message.getData().getInt("action"))
			{
				case 0: //Handle Old Friends
					for(int i = Download_Thread_Array.size() - 1; i > -1; i--)
						if(Download_Thread_Array.get(i).Get_Pic_ID() == Pic_ID) Download_Thread_Array.remove(i);

					Log.d("Nic Says", "Download_Thread_Array.size() :" + Download_Thread_Array.size());

					if(Download_Thread_Array.size() == 0) Remove_Old_Friends();

					break;

				case 1: //Load Friend Profile Pic into UI
					Bitmap Friend_Profile_Pic = Get_Friend_Profile_Pic_Bitmap(Pic_ID);

					TabLayoutHandler Tab_Layout_Handler = Data_Handler.Service_Core.UI_Core.Tab_Layout_Handler;

					if(Friend_Profile_Pic != null)
					{
						ImageView Image_View = (ImageView) Tab_Layout_Handler.Friends_Tab_Handler.Friend_Items_Layout.findViewWithTag("Friend_Profile_Pic_" + Pic_ID);
						if(Image_View != null) Image_View.setImageBitmap(Get_Masked_Profile_Pic(Friend_Profile_Pic, 50));

						Image_View = (ImageView) Tab_Layout_Handler.Friends_Tab_Handler.Friend_Items_Layout.findViewWithTag("Poke_Profile_Pic_" + Pic_ID);
						if(Image_View != null) Image_View.setImageBitmap(Get_Masked_Profile_Pic(Friend_Profile_Pic, 50));

						if(Data_Handler.Service_Core.UI_Core.Settings_Handler.Privacy_Settings_Handler.Screen_Is_Visible)
						{
							Image_View = (ImageView) Data_Handler.Service_Core.UI_Core.Settings_Handler.Privacy_Settings_Handler.Privacy_Settings_View
									                         .findViewWithTag("Blocked_Profile_Pic_" + Pic_ID);
							if(Image_View != null) Image_View.setImageBitmap(Get_Masked_Profile_Pic(Friend_Profile_Pic, 50));
						}

						FriendHandler Friend_Handler = Data_Handler.Service_Core.UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Friend_Handler;

						if(Friend_Handler.Screen_Is_Visible)
							Friend_Handler.Profile_Pic_Image_View.setImageBitmap(Get_Masked_Profile_Pic(Friend_Profile_Pic,
							                                                                            Friend_Handler.PROFILE_PIC_DIMENSION));

						if(Tab_Layout_Handler.Poke_UI_Handler.Poke_Alert_Popup_Is_Visible)
						{
							Image_View = Tab_Layout_Handler.Poke_UI_Handler.Get_Poke_Alert_PP_Image_View(Pic_ID);

							if(Image_View != null)
								Image_View.setImageBitmap(Get_Masked_Profile_Pic(Friend_Profile_Pic, Friend_Handler.PROFILE_PIC_DIMENSION));
						}
					}

					break;
			}
		}
	};

	private class Pic
	{
		private int         PP_ID;
		private int         PP_Type;
		private Bitmap      PP_Bitmap;

		public Pic(int pic_id, int pic_type, Bitmap bmp)
		{
			PP_ID     = pic_id;
			PP_Type   = pic_type;
			PP_Bitmap = bmp;

			// Pic Types:
			// ----------
			// 0 - Profile
			// 1 - Location
		}

		public int    Get_Pic_ID()  { return PP_ID; }
		public int    Get_Pic_Type(){ return PP_Type; }
		public Bitmap Get_Bitmap()  { return PP_Bitmap; }
	}

	private class DownloadThread
	{
		private int         Pic_ID;
		private Thread      Download_Thread;

		public DownloadThread(int pic_id, Thread thread)
		{
			Pic_ID = pic_id;
			Download_Thread = thread;
		}

		public int Get_Pic_ID(){ return Pic_ID; }
	}
}
