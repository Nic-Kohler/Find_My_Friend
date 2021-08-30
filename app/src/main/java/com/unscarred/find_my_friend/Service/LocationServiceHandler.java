package com.unscarred.find_my_friend.Service;

import com.unscarred.find_my_friend.Service.FMFService.ServiceCore;

import android.app.Activity;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LocationServiceHandler
{
    private Context             Base_Context;
	private ServiceCore         Service_Core;

	private LocationManager     Location_Manager;
	private String              Provider;

	private final int           PASSIVE_TIME_INTERVAL        = 30000;
	private final int           AGGRESSIVE_TIME_INTERVAL     = 3000;
	private final int           AGGRESSIVE_DISTANCE_INTERVAL = 1;

	private String              Location_Update_Mode;
	public  boolean             First_Run_Complete = false;


    public LocationServiceHandler(Context context)
    {
        Base_Context = context;
	    Location_Manager = (LocationManager)Base_Context.getSystemService(Context.LOCATION_SERVICE);
	    Location_Update_Mode = "PASSIVE";

	    if(!is_Network_Enabled())
	    {
		    Toast.makeText(Base_Context, "Make sure 'High Accuracy' or 'Power Saving' is selected in Settings > More > Locations > Mode", Toast.LENGTH_LONG).show();

		    ((Activity)Base_Context).finish();
	    }
	    else
	    {
		    Get_Provider();
		    Initialize_Location_Updates();
	    }
    }

	public  void    Set_Service_Core(ServiceCore core){ Service_Core = core; }
	private boolean is_GPS_Enabled()    { return Location_Manager.isProviderEnabled(LocationManager.GPS_PROVIDER); }
	private boolean is_Network_Enabled(){ return Location_Manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); }

	private void Get_Provider()
	{
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(true);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		Provider = Location_Manager.getBestProvider(criteria, true);

		if(Provider == null)
		{
			if(is_GPS_Enabled())     Provider = LocationManager.GPS_PROVIDER;
			if(is_Network_Enabled()) Provider = LocationManager.NETWORK_PROVIDER;
		}

		Log.d("Nic Says", "Provider: " + Provider);
	}

	private void Initialize_Location_Updates()
	{
		try
		{
			if(is_Network_Enabled()) Location_Manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Location_Listener);
			if(is_GPS_Enabled())     Location_Manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,     0, 0, Location_Listener);
		}
		catch(SecurityException e)
		{
			System.err.println("Error in LocationServiceHandler: Initialize_Location_Updates:");
			System.err.println("Caught Security Exception: " + e.getMessage());
		}
	}

	private void Stop_Location_Updates()
	{
		try
		{
			Location_Manager.removeUpdates(Location_Listener);
		}
		catch(SecurityException e)
		{
			System.err.println("Error in LocationServiceHandler: Stop_Location_Updates:");
			System.err.println("Caught Security Exception: " + e.getMessage());
		}
	}

	private void Schedule_Next_Location_Update()
	{
		new Handler().postDelayed(new Runnable()
		{
			@Override public void run()
			{
				Initialize_Location_Updates();
			}
		}, PASSIVE_TIME_INTERVAL);
	}

	private LocationListener Location_Listener = new LocationListener()
	{
		@Override public void onLocationChanged(Location Current_Location)
		{
			Service_Core.User_Latitude  = Current_Location.getLatitude();
			Service_Core.User_Longitude = Current_Location.getLongitude();
			Service_Core.User_Altitude  = Current_Location.getAltitude();
			Service_Core.User_Address   = Get_Address(Service_Core.User_Latitude, Service_Core.User_Longitude);

			if(!First_Run_Complete)
			{
				First_Run_Complete = true;
				Service_Core.Data_Handler.Get_Data();
			}

			Stop_Location_Updates();
			Schedule_Next_Location_Update();

			Log.d("Nic Says", "Location_Listener: onLocationChanged:");
			Log.d("Nic Says", "=====================================");
			Log.d("Nic Says", "Latitude:  " + Service_Core.User_Latitude);
			Log.d("Nic Says", "Longitude: " + Service_Core.User_Longitude);
			Log.d("Nic Says", "Altitude:  " + Service_Core.User_Altitude);

			try
			{
				JSONObject jsonSend = new JSONObject();

				jsonSend.accumulate("action",    "Update_Location");
				jsonSend.accumulate("user_id",   Service_Core.File_Handler.FMF_Settings_Data.getInt("User_ID"));
				jsonSend.accumulate("latitude",  Service_Core.User_Latitude);
				jsonSend.accumulate("longitude", Service_Core.User_Longitude);
				jsonSend.accumulate("altitude",  Service_Core.User_Altitude);

				Service_Core.Server_Handler.Queue_Server_Request(Service_Core.SERVER_URL, "find_my_friend.php", jsonSend, Service_Core.Server_Response_Handler);
			}
			catch(JSONException e)
			{
				System.err.println("Error in LocationServiceHandler: Location_Listener: onLocationChanged:");
				System.err.println("Caught JSON Exception: " + e.getMessage());
			}
		}

		@Override public void onStatusChanged(String s, int i, Bundle bundle)
		{
			Log.d("Nic Says", "Location_Listener: onStatusChanged: HAS RUN.");
		}
		@Override public void onProviderEnabled(String s)
		{
			Log.d("Nic Says", "Location_Listener: onProviderEnabled: HAS RUN.");
		}
		@Override public void onProviderDisabled(String s)
		{
			Log.d("Nic Says", "Location_Listener: onProviderDisabled: HAS RUN.");
		}
	};

	public String Get_Address(double latitude, double longitude)
	{
		String   strAdd   = "";
		Geocoder geocoder = new Geocoder(Base_Context, Locale.getDefault());
		try
		{
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

			if(addresses != null)
			{
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("");

				for(int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++)
				{
					strReturnedAddress.append(returnedAddress.getAddressLine(i));

					if(i < (returnedAddress.getMaxAddressLineIndex() - 1)) strReturnedAddress.append("\n");
				}

				strAdd = strReturnedAddress.toString();
			}
			else
			{
				strAdd = "No Address Available.";
			}
		}
		catch(IOException e)
		{
			System.err.println("Error in LocationServiceHandler: Get_Address:");
			System.err.println("Caught IO Exception: " + e.getMessage());
		}

		return strAdd;
	}
}

