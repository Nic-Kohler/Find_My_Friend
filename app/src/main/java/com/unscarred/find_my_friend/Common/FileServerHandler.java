package com.unscarred.find_my_friend.Common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileServerHandler
{
	private Thread  Server_Request_Thread = null;
	private ArrayList<Server_Call_Info> Server_Request_Queue = new ArrayList<Server_Call_Info>();
	private int Timeout_Interval = 5000;


	public void Queue_Server_Request(String uri, String server_file, File upload_file, String user_id, String upload_file_name, Handler mh)
	{
		Server_Request_Queue.add(new Server_Call_Info(uri, server_file, upload_file, user_id, upload_file_name, mh));

		if(Server_Request_Queue.size() == 1) Start_Server_Request_Thread();
	}

	private void Start_Server_Request_Thread()
	{
		Server_Request_Thread = new Thread(){ public void run(){ Send_Server_Request(); } };

		Server_Request_Thread.start();
	}

	private void Send_Server_Request()
	{
		try
		{
			HttpClient HTTP_Client = new DefaultHttpClient();
			HttpParams HTTP_Params = HTTP_Client.getParams();
			HttpConnectionParams.setConnectionTimeout(HTTP_Params, Timeout_Interval);
			HttpConnectionParams.setSoTimeout(HTTP_Params, Timeout_Interval);
			HttpPost HTTP_Post;

			Log.d("*** Nic Says ***", "URI: " + Server_Request_Queue.get(0).URI);
			Log.d("*** Nic Says ***", "File: " + Server_Request_Queue.get(0).Server_File);

			if(Server_Request_Queue.get(0).Server_File != null){ HTTP_Post = new HttpPost(Server_Request_Queue.get(0).URI + "/" + Server_Request_Queue.get(0).Server_File); }
			else{ HTTP_Post = new HttpPost(Server_Request_Queue.get(0).URI); }

			HttpResponse HTTP_Response = null;

			if(Server_Request_Queue.get(0).Upload_File != null)
			{
				Log.d("*** Nic Says ***", "Upload File: " + (Server_Request_Queue.get(0).Upload_File.toString()));

				MultipartEntityBuilder Multipart_Entity_Builder = MultipartEntityBuilder.create();
				Multipart_Entity_Builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				Multipart_Entity_Builder.addPart("upload_file", new FileBody(Server_Request_Queue.get(0).Upload_File));
				Multipart_Entity_Builder.addPart("user_id", new StringBody(Server_Request_Queue.get(0).User_ID, ContentType.TEXT_PLAIN));
				Multipart_Entity_Builder.addPart("upload_file_name", new StringBody(Server_Request_Queue.get(0).Upload_File_Name, ContentType.TEXT_PLAIN));

				HTTP_Post.setEntity(Multipart_Entity_Builder.build());
				HTTP_Response = HTTP_Client.execute(HTTP_Post);
			}

			if(HTTP_Response != null)
			{
				if(HTTP_Response.getStatusLine().getStatusCode() == 500)
					Log.w("Read from server", "Error on Server - PHP Code");
				else
					Server_Response_Handler(EntityUtils.toString(HTTP_Response.getEntity()));
			}
		}
		catch(IOException e)
		{
			Log.d("*** Nic Says ***", "Server_Handler - Error sending data to server:");
			Log.d("*** Nic Says ***", "==============================================");
			Log.d("*** Nic Says ***", "*** Caught IO Exception: " + e.getMessage());
			Log.d("*** Nic Says ***", "*** URI:                 " + Server_Request_Queue.get(0).URI);
			Log.d("*** Nic Says ***", "*** Server File:         " + Server_Request_Queue.get(0).Server_File);
			Log.d("*** Nic Says ***", "*** User ID:             " + Server_Request_Queue.get(0).User_ID);
			Log.d("*** Nic Says ***", "*** Upload_File_Name:    " + Server_Request_Queue.get(0).Upload_File_Name);
			Log.d("*** Nic Says ***", "*** Upload File:         " + Server_Request_Queue.get(0).Upload_File);
			Log.d("*** Nic Says ***", "==============================================");

			Bundle callback_bundle = new Bundle();
			Message callback_message = new Message();

			callback_bundle.putBoolean("network_error", true);
			callback_message.setData(callback_bundle);

			Server_Request_Queue.get(0).Callback_Message_Handler.sendMessage(callback_message);

			Server_Request_Thread = null;

			Server_Response_Handler(null);
		}
		catch(IllegalArgumentException e)
		{
			Log.d("*** Nic Says ***", "Server_Handler - Error sending data to server:");
			Log.d("*** Nic Says ***", "==============================================");
			Log.d("*** Nic Says ***", "*** Caught IO Exception: " + e.getMessage());
			Log.d("*** Nic Says ***", "*** URI:                 " + Server_Request_Queue.get(0).URI);
			Log.d("*** Nic Says ***", "*** Server File:         " + Server_Request_Queue.get(0).Server_File);
			Log.d("*** Nic Says ***", "*** User ID:             " + Server_Request_Queue.get(0).User_ID);
			Log.d("*** Nic Says ***", "*** Upload_File_Name:    " + Server_Request_Queue.get(0).Upload_File_Name);
			Log.d("*** Nic Says ***", "*** Upload File:         " + Server_Request_Queue.get(0).Upload_File);
			Log.d("*** Nic Says ***", "==============================================");
		}
	}

	private Handler Timeout_Dialog_Handler = new Handler();

	private void Server_Response_Handler(String strReceive)
	{
		Server_Request_Thread = null;

		Bundle callback_bundle = new Bundle();
		Message callback_message = new Message();

		if(strReceive == null)
		{
			Timeout_Dialog_Handler.postDelayed(new Runnable(){ public void run(){ Start_Server_Request_Thread(); } }, Timeout_Interval);
		}
		else
		{
			callback_bundle.putString("json", strReceive);
			callback_bundle.putBoolean("network_error", false);
			callback_message.setData(callback_bundle);

			Server_Request_Queue.get(0).Callback_Message_Handler.sendMessage(callback_message);

			Server_Request_Queue.remove(0);
			if(Server_Request_Queue.size() != 0) Start_Server_Request_Thread();
		}
	}

	private class Server_Call_Info
	{
		public  String  URI;
		public  String  Server_File;
		private File    Upload_File;
		private String     User_ID;
		private String  Upload_File_Name;
		private Handler Callback_Message_Handler;

		public Server_Call_Info(String uri, String server_file, File upload_file, String user_id, String upload_file_name, Handler mh)
		{
			URI = uri;
			Server_File = server_file;
			Upload_File = upload_file;
			User_ID = user_id;
			Upload_File_Name = upload_file_name;
			Callback_Message_Handler = mh;
		}
	}
}
