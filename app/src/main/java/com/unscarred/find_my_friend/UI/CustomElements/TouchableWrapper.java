package com.unscarred.find_my_friend.UI.CustomElements;

import com.unscarred.find_my_friend.CoreActivity;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout
{
	public TouchableWrapper(Context context){ super(context); }

	@Override public boolean dispatchTouchEvent(MotionEvent event)
	{
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				//if(CoreActivity.UI_Core.Current_Screen.equals("Get_Location"))
					//CoreActivity.UI_Core.Tab_Layout_Handler.Locations_Tab_Handler.Get_Location.Get_Location_Scroll_View.requestDisallowInterceptTouchEvent(true);
				break;
			case MotionEvent.ACTION_UP:
				//if(CoreActivity.UI_Core.Current_Screen.equals("Get_Location"))
					//CoreActivity.UI_Core.Tab_Layout_Handler.Locations_Tab_Handler.Get_Location.Get_Location_Scroll_View.requestDisallowInterceptTouchEvent(false);
				break;
		}

		return super.dispatchTouchEvent(event);
	}
}