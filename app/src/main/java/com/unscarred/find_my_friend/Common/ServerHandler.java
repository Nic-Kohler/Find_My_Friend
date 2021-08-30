package com.unscarred.find_my_friend.Common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ServerHandler
{
	private Thread  Server_Request_Thread = null;
	private ArrayList<Server_Call_Info> Server_Request_Queue = new ArrayList<Server_Call_Info>();
	private int Timeout_Interval = 5000;


	public void Queue_Server_Request(String uri, String file, JSONObject json, Handler mh)
	{
		Server_Request_Queue.add(new Server_Call_Info(uri, file, json, mh));

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
	        Log.d("*** Nic Says ***", "File: " + Server_Request_Queue.get(0).File);

	        if(Server_Request_Queue.get(0).File != null){ HTTP_Post = new HttpPost(Server_Request_Queue.get(0).URI + "/" + Server_Request_Queue.get(0).File); }
            else{ HTTP_Post = new HttpPost(Server_Request_Queue.get(0).URI); }

	        HttpResponse HTTP_Response = null;
	        HttpEntity Http_Entity = null;

            if(Server_Request_Queue.get(0).Package != null)
            {
	            Log.d("*** Nic Says ***", "JSONSend: " + (Server_Request_Queue.get(0).Package.toString()));

	            HTTP_Post.setHeader("Accept", "application/json");
	            HTTP_Post.setHeader("Content-type", "application/json");
	            HTTP_Post.setEntity(new StringEntity(Server_Request_Queue.get(0).Package.toString()));

	            HTTP_Response = HTTP_Client.execute(HTTP_Post);
	            Http_Entity = HTTP_Response.getEntity();
            }

            if(Http_Entity != null)
            {
	            if(HTTP_Response.getStatusLine().getStatusCode() == 500)
                    Log.w("Read from server", "Error on Server - PHP Code");
                else
		            Server_Response_Handler(EntityUtils.toString(Http_Entity));
            }
        }
        catch(IOException e)
        {
	        Log.d("*** Nic Says ***", "Server_Handler - Error sending data to server:");
	        Log.d("*** Nic Says ***", "==============================================");
	        Log.d("*** Nic Says ***", "*** Caught IO Exception: " + e.getMessage());
	        Log.d("*** Nic Says ***", "*** URI:                 " + Server_Request_Queue.get(0).URI);
	        Log.d("*** Nic Says ***", "*** File:                " + Server_Request_Queue.get(0).File);
	        Log.d("*** Nic Says ***", "*** jsonSend:            " + Server_Request_Queue.get(0).Package);
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
	        Log.d("*** Nic Says ***", "*** File:                " + Server_Request_Queue.get(0).File);
	        Log.d("*** Nic Says ***", "*** jsonSend:            " + Server_Request_Queue.get(0).Package);
	        Log.d("*** Nic Says ***", "==============================================");
        }
    }

	private Handler Timeout_Dialog_Handler = new Handler();

	public void Server_Response_Handler(String strReceive)
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
		public String URI;
		public String File;
		private JSONObject Package;
		private Handler Callback_Message_Handler;

		public Server_Call_Info(String uri, String file, JSONObject object, Handler mh)
		{
			URI = uri;
			File = file;
			Package = object;
			Callback_Message_Handler = mh;
		}
	}
}
