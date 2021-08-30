package com.unscarred.find_my_friend.Common;

import android.util.Log;
import com.unscarred.find_my_friend.Service.FMFService.ServiceCore;

import android.content.Context;

import org.apache.commons.io.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class FileHandler
{
	private Context         Base_Context;
	private ServiceCore     Service_Core;

	public  String          FMF_Settings_File_Name = "settings.find_my_friend";
	public  JSONObject      FMF_Settings_Data;


	public FileHandler(Context context)
	{
		Base_Context = context;

		FMF_Settings_Data = new JSONObject();
	}

	public void Set_Service_Core(ServiceCore core){ Service_Core = core; }

	private void Write_To_File(String File_Name, JSONObject File_Data)
	{
		byte byte_file_data[] = File_Data.toString().getBytes();

		try
		{
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(Base_Context.getFilesDir().getPath() + "/" + File_Name));

			bos.write(byte_file_data);
			bos.flush();
			bos.close();
		}
		catch(FileNotFoundException e)
		{
			System.err.println("Error writing Recent Places data:");
			System.err.println("Caught FileNotFoundException: " + e.getMessage());
		}
		catch(IOException e)
		{
			System.err.println("Error writing Recent Places data:");
			System.err.println("Caught IOException: " + e.getMessage());
		}
	}

	private void Print_Contents()
	{
		try
		{
			Log.d("Nic Says", "*** ======================================");
			Log.d("Nic Says", "*** Step:                       " + FMF_Settings_Data.getString("Step"));
			Log.d("Nic Says", "*** User_ID:                    " + FMF_Settings_Data.getInt("User_ID"));
			Log.d("Nic Says", "*** Measurement_System:         " + FMF_Settings_Data.getString("Measurement_System"));
			Log.d("Nic Says", "*** Current_Range:              " + FMF_Settings_Data.getInt("Current_Range"));
			Log.d("Nic Says", "*** Max_Range:                  " + FMF_Settings_Data.getInt("Max_Range"));
			Log.d("Nic Says", "*** Location_Update_Interval:   " + FMF_Settings_Data.getInt("Location_Update_Interval"));
			Log.d("Nic Says", "*** Display_Navigation_Buttons: " + FMF_Settings_Data.getBoolean("Display_Navigation_Buttons"));
			if(FMF_Settings_Data.has("Code")){    Log.d("Nic Says", "*** Code:                       " + FMF_Settings_Data.getInt("Code"));}
			if(FMF_Settings_Data.has("Name")){    Log.d("Nic Says", "*** Name:                       " + FMF_Settings_Data.getString("Name"));}
			if(FMF_Settings_Data.has("Surname")){ Log.d("Nic Says", "*** Surname:                    " + FMF_Settings_Data.getString("Surname"));}
			if(FMF_Settings_Data.has("Email")){   Log.d("Nic Says", "*** Email:                      " + FMF_Settings_Data.getString("Email"));}
			Log.d("Nic Says", "*** ======================================");
		}
		catch(JSONException e)
		{
			System.err.println("Error in FileHandler: Save_FMF_Settings:");
			System.err.println("Caught JSONException: " + e.getMessage());
		}
	}

	public void Save_FMF_Settings()
	{
		File FMF_Settings_File = new File(Base_Context.getFilesDir().getPath() + "/" + FMF_Settings_File_Name);

		try
		{
			if(!FMF_Settings_File.exists()) FMF_Settings_File.createNewFile();

			Log.d("Nic Says", "*** Settings Saved To File:");
			Print_Contents();

			Write_To_File(FMF_Settings_File_Name, FMF_Settings_Data);
		}
		catch(IOException e)
		{
			System.err.println("Error in FileHandler: Save_FMF_Settings:");
			System.err.println("Caught IOException: " + e.getMessage());
		}
	}

	public void Read_FMF_Settings()
	{
		File FMF_Settings_File = new File(Base_Context.getFilesDir().getPath() + "/" + FMF_Settings_File_Name);

		try
		{
			if(FMF_Settings_File.exists())
			{
				FMF_Settings_Data = new JSONObject(new String(FileUtils.readFileToByteArray(FMF_Settings_File)));

				Log.d("Nic Says", "*** Settings Read From File:");
				Print_Contents();
			}
			else
			{
				FMF_Settings_Data.put("Step", "Setup");
				FMF_Settings_Data.put("User_ID", -1);
				FMF_Settings_Data.put("Measurement_System", "Meters");
				FMF_Settings_Data.put("Current_Range", 50);
				FMF_Settings_Data.put("Max_Range", 490);
				FMF_Settings_Data.put("Location_Update_Interval", 10000);
				FMF_Settings_Data.put("Display_Navigation_Buttons", true);
				FMF_Settings_Data.put("Code", 0);
				FMF_Settings_Data.put("Name", "");
				FMF_Settings_Data.put("Surname", "");
				FMF_Settings_Data.put("Email", "");

				File folder = new File(Base_Context.getFilesDir().getPath() + "/Profile_Pics");
				if(folder.mkdir()) Log.d("Nic Says", "*** Profile_Pics Folder Created.");
				else Log.d("Nic Says", "*** Profile_Pics Folder not Created.");

				Log.d("Nic Says", "*** Default Settings File Created:");
				Print_Contents();
			}
		}
		catch(JSONException e)
		{
			System.err.println("Error in FileHandler: Read_FMF_Settings:");
			System.err.println("Caught JSONException: " + e.getMessage());
		}
		catch(IOException e)
		{
			System.err.println("Error in FileHandler: Read_FMF_Settings:");
			System.err.println("Caught IOException: " + e.getMessage());
		}
	}
}
