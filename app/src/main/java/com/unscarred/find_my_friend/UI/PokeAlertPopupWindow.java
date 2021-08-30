package com.unscarred.find_my_friend.UI;

import com.unscarred.find_my_friend.R;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


public class PokeAlertPopupWindow
{
	private Context     Base_Context;
	private UICore      UI_Core;

	private int         User_ID;

	private PopupWindow Popup_Window;
	private View        Popup_Window_View;


	public PokeAlertPopupWindow(Context context, UICore core, int user_id)
	{
		Base_Context = context;
		UI_Core = core;
		User_ID = user_id;

		Show_Poke_Alert_Popup();
	}

	public int Get_User_ID(){ return User_ID; }
	public View Get_Popup_Window_View(){ return Popup_Window_View; }

	public void Show_Poke_Alert_Popup()
	{
		LayoutInflater layoutInflater = (LayoutInflater)Base_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Popup_Window_View = layoutInflater.inflate(R.layout.poke_alert, null);
		Popup_Window_View.setOnTouchListener(Poke_Alert_Popup_On_Touch_Listener);
		Popup_Window = new PopupWindow(Popup_Window_View, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
		Popup_Window.setAnimationStyle(android.R.style.Animation_Dialog);

		ImageView Profile_Pic_Image_View = (ImageView)Popup_Window_View.findViewById(R.id.poke_alert_profile_pic_image_view);
		TextView Profile_Name_Text_View = (TextView)Popup_Window_View.findViewById(R.id.poke_alert_prompt_text_view);
		TextView  Dismiss_Text_View = (TextView)Popup_Window_View.findViewById(R.id.poke_alert_dismiss_label);
		TextView  Swipe_Text_View = (TextView)Popup_Window_View.findViewById(R.id.poke_alert_swipe_label);

		Profile_Pic_Image_View.setTag("Poke_Alert_Pic_" + User_ID);

		Profile_Name_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Bold);
		Profile_Name_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE + 4);
		Profile_Name_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_2));

		Dismiss_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Bold);
		Dismiss_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE + 4);
		Dismiss_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_3));

		Swipe_Text_View.setTypeface(UI_Core.Theme_Settings.Font_Regular);
		Swipe_Text_View.setTextSize(TypedValue.COMPLEX_UNIT_SP, UI_Core.Theme_Settings.FONT_SIZE - 4);
		Swipe_Text_View.setTextColor(ContextCompat.getColor(Base_Context, R.color.color_3));

		String text = "User-" + User_ID + " has Poked you.";
		Profile_Name_Text_View.setText(text);

		Profile_Pic_Image_View
				.setImageBitmap(UI_Core.FMF_Service.Service_Core.Data_Handler.Pic_Handler
						                .Get_Masked_Profile_Pic(UI_Core.FMF_Service.Service_Core.Data_Handler.Pic_Handler
								                                        .Convert_Drawable_To_Bitmap(ResourcesCompat.getDrawable(Base_Context.getResources(),
								                                                                                                R.drawable.no_avatar,
								                                                                                                null)),
						                                        UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.Friend_Handler.PROFILE_PIC_DIMENSION));

		Popup_Window.showAtLocation(Popup_Window_View, Gravity.CENTER, 0, 0);
		UI_Core.Theme_Settings.Vibe.vibrate(50);
	}

	float Popup_Swipe_Initial_X;
	float Popup_Swipe_Initial_Y;
	float Popup_Window_Initial_X;
	float Popup_Window_Initial_Y;
	long  Popup_Window_Initial_Time;

	public View.OnTouchListener Poke_Alert_Popup_On_Touch_Listener = new View.OnTouchListener()
	{
		@Override public boolean onTouch(View view, MotionEvent event)
		{
			float Final_X = event.getX();
			float Final_Y = event.getY();
			long  Final_Time = System.currentTimeMillis();

			float Delta_X = Final_X - Popup_Swipe_Initial_X;
			float Delta_Y = Final_Y - Popup_Swipe_Initial_Y;
			float Delta_Time = Final_Time - Popup_Window_Initial_Time;
			float Delta_Distance = (float)Math.sqrt((Final_X - Popup_Swipe_Initial_X) * (Final_X - Popup_Swipe_Initial_X) +
			                                        (Final_Y - Popup_Swipe_Initial_Y) * (Final_Y - Popup_Swipe_Initial_Y));
			float Velocity = Delta_Distance / Delta_Time;

			float   space_left;
			int     duration = 0;
			boolean view_was_swiped = false;

			ObjectAnimator Object_Animator_Translation = null;
			ObjectAnimator Object_Animator_Alpha = null;
			AnimatorSet Animator_Set;

			switch(event.getActionMasked())
			{
				case MotionEvent.ACTION_DOWN:
					Popup_Swipe_Initial_X = event.getX();
					Popup_Swipe_Initial_Y = event.getY();
					Popup_Window_Initial_X = Popup_Window_View.getX();
					Popup_Window_Initial_Y = Popup_Window_View.getY();
					Popup_Window_Initial_Time = System.currentTimeMillis();
					break;

				case MotionEvent.ACTION_MOVE:
					Popup_Window_View.setX(Delta_X + Popup_Window_Initial_X);

					if(Popup_Window_View.getX() < 0)
						space_left = Popup_Window_View.getX() + Popup_Window_View.getWidth();
					else
						space_left = UI_Core.Theme_Settings.Screen_Width - Popup_Window_View.getX();

					Popup_Window_View.setAlpha(space_left / UI_Core.Theme_Settings.Screen_Width);
					break;

				case MotionEvent.ACTION_UP:
					if(Popup_Swipe_Initial_X < Final_X && Math.abs(Delta_Y) < UI_Core.Tab_Layout_Handler.SWIPE_THRESHOLD) // Swipe Left to Right
					{
						view_was_swiped = true;
						duration = Math.round((UI_Core.Theme_Settings.Screen_Width - Popup_Window_View.getX()) / Velocity);
						space_left = UI_Core.Theme_Settings.Screen_Width - Popup_Window_View.getX();

						Object_Animator_Translation = ObjectAnimator.ofFloat(Popup_Window_View,
						                                                     "translationX",
						                                                     Popup_Window_View.getX(),
						                                                     UI_Core.Theme_Settings.Screen_Width);
						Object_Animator_Alpha = ObjectAnimator.ofFloat(Popup_Window_View,
						                                               "alpha",
						                                               space_left / UI_Core.Theme_Settings.Screen_Width,
						                                               0);
					}

					if(Popup_Swipe_Initial_X > Final_X && Math.abs(Delta_Y) < UI_Core.Tab_Layout_Handler.SWIPE_THRESHOLD) // Swipe Right to Left
					{
						view_was_swiped = true;
						duration = Math.round((Popup_Window_View.getWidth() + Popup_Window_View.getX()) / Velocity);
						space_left = Popup_Window_View.getX() + Popup_Window_View.getWidth();

						Object_Animator_Translation = ObjectAnimator.ofFloat(Popup_Window_View,
						                                                     "translationX",
						                                                     Popup_Window_View.getX(),
						                                                     UI_Core.Theme_Settings.Screen_Width * -1);
						Object_Animator_Alpha = ObjectAnimator.ofFloat(Popup_Window_View,
						                                               "alpha",
						                                               space_left / UI_Core.Theme_Settings.Screen_Width,
						                                               0);
					}

					if(view_was_swiped)
					{
						Animator_Set = new AnimatorSet();
						Animator_Set.setDuration(duration);
						Animator_Set.playTogether(Object_Animator_Translation, Object_Animator_Alpha);
						Animator_Set.start();

						UI_Core.Tab_Layout_Handler.Friends_Tab_Handler.UI_On_Touch_Handler.postDelayed(new Runnable()
						{
							@Override public void run()
							{
								Popup_Window.dismiss();

								UI_Core.Tab_Layout_Handler.Poke_UI_Handler.Remove_Poke_Alert_Popup_Window(User_ID);
							}
						}, duration);
					}
					break;
			}

			return true;
		}
	};
}

