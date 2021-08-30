package com.unscarred.find_my_friend.UI;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class Animations
{
	/*
	private Context    Base_Context;
	private UICore     UI_Core;

	public  final int   ANIMATION_DURATION_FULL = 400;
	public  final int   ANIMATION_DURATION_HALF = 275;

	public  boolean     Flip_Screen_Is_In_Progress;


	public Animations(Context context, UICore ui_core)
	{
		Base_Context = context;
		UI_Core = ui_core;
		Flip_Screen_Is_In_Progress = false;
	}

	private Animator.AnimatorListener Animation_Listener = new Animator.AnimatorListener()
	{
		@Override public void onAnimationStart(Animator anim){}

		@Override public void onAnimationCancel(Animator anim){}

		@Override public void onAnimationRepeat(Animator anim){}

		@Override public void onAnimationEnd(Animator anim)
		{
			int action = -1;
			ObjectAnimator Rotate;
			ObjectAnimator Scale_X;
			ObjectAnimator Scale_Y;
			AnimatorSet Animator_Set;

			if(UI_Core.Current_Screen.equals("Main")) action = 0;
			if(UI_Core.Current_Screen.equals("Get_From_Location")) action = 1;
			if(UI_Core.Current_Screen.equals("Get_To_Location")) action = 1;

			switch(action)
			{
				case 0:
					UI_Core.Main_Scroll_View.removeAllViews();
					UI_Core.Main_Scroll_View.addView(UI_Core.Get_Location_Screen.Get_Location_View);

					Rotate = ObjectAnimator.ofFloat(UI_Core.Get_Location_Screen.Get_Location_View, "rotationY", 90f, 0f);
					Rotate.setDuration(ANIMATION_DURATION_HALF);
					Rotate.setInterpolator(new AccelerateDecelerateInterpolator());

					Scale_X = ObjectAnimator.ofFloat(UI_Core.Get_Location_Screen.Get_Location_View, "scaleX", 1f);
					Scale_X.setDuration(ANIMATION_DURATION_HALF);
					Scale_Y = ObjectAnimator.ofFloat(UI_Core.Get_Location_Screen.Get_Location_View, "scaleY", 1f);
					Scale_Y.setDuration(ANIMATION_DURATION_HALF);

					Animator_Set = new AnimatorSet();
					Animator_Set.playTogether(Rotate, Scale_X, Scale_Y);
					Animator_Set.start();

					UI_Core.Current_Screen = "Get_From_Location";
					break;

				case 1:
					UI_Core.Get_Location_Screen.Kill_Screen();
					UI_Core.Main_Scroll_View.removeAllViews();
					UI_Core.Main_Scroll_View.addView(UI_Core.Info_Collection_Screen.Info_Collection_View);

					Rotate = ObjectAnimator.ofFloat(UI_Core.Info_Collection_Screen.Info_Collection_View, "rotationY", 90f, 0f);
					Rotate.setDuration(ANIMATION_DURATION_HALF);
					Rotate.setInterpolator(new AccelerateDecelerateInterpolator());

					Scale_X = ObjectAnimator.ofFloat(UI_Core.Info_Collection_Screen.Info_Collection_View, "scaleX", 1f);
					Scale_X.setDuration(ANIMATION_DURATION_HALF);
					Scale_Y = ObjectAnimator.ofFloat(UI_Core.Info_Collection_Screen.Info_Collection_View, "scaleY", 1f);
					Scale_Y.setDuration(ANIMATION_DURATION_HALF);

					Animator_Set = new AnimatorSet();
					Animator_Set.playTogether(Rotate, Scale_X, Scale_Y);
					Animator_Set.start();

					UI_Core.Current_Screen = "Main";
					break;
			}

			Flip_Screen_Is_In_Progress = false;
		}
	};

	private Animator.AnimatorListener Animation_Listener_2 = new Animator.AnimatorListener()
	{
		@Override public void onAnimationStart(Animator anim){}

		@Override public void onAnimationCancel(Animator anim){}

		@Override public void onAnimationRepeat(Animator anim){}

		@Override public void onAnimationEnd(Animator anim)
		{
			int action = -1;
			ObjectAnimator Rotate;
			ObjectAnimator Scale_X;
			ObjectAnimator Scale_Y;
			AnimatorSet Animator_Set;

			if(Core.Current_Screen.equals("Main")) action = 0;
			if(Core.Current_Screen.equals("Get_Date_And_Time")) action = 1;

			switch(action)
			{
				case 0:
					Core.Main_Scroll_View.removeAllViews();
					Core.Main_Scroll_View.addView(Core.Get_Date_And_Time_Screen.Get_Date_And_Time_View);
					Core.Get_Date_And_Time_Screen.Get_Current_Time = true;

					Rotate = ObjectAnimator.ofFloat(Core.Get_Date_And_Time_Screen.Get_Date_And_Time_View, "rotationY", 90f, 0f);
					Rotate.setDuration(ANIMATION_DURATION_HALF);
					Rotate.setInterpolator(new AccelerateDecelerateInterpolator());

					Scale_X = ObjectAnimator.ofFloat(Core.Get_Date_And_Time_Screen.Get_Date_And_Time_View, "scaleX", 1f);
					Scale_X.setDuration(ANIMATION_DURATION_HALF);
					Scale_Y = ObjectAnimator.ofFloat(Core.Get_Date_And_Time_Screen.Get_Date_And_Time_View, "scaleY", 1f);
					Scale_Y.setDuration(ANIMATION_DURATION_HALF);

					Animator_Set = new AnimatorSet();
					Animator_Set.playTogether(Rotate, Scale_X, Scale_Y);
					Animator_Set.start();

					Core.Current_Screen = "Get_Date_And_Time";
					break;

				case 1:
					Core.Get_Date_And_Time_Screen.Kill_Screen();

					Core.Get_Date_And_Time_Screen.Get_Current_Time = false;
					Core.Main_Scroll_View.removeAllViews();
					Core.Main_Scroll_View.addView(Core.Info_Collection_Screen.Info_Collection_View);

					Rotate = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.Info_Collection_View, "rotationY", 90f, 0f);
					Rotate.setDuration(ANIMATION_DURATION_HALF);
					Rotate.setInterpolator(new AccelerateDecelerateInterpolator());

					Scale_X = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.Info_Collection_View, "scaleX", 1f);
					Scale_X.setDuration(ANIMATION_DURATION_HALF);
					Scale_Y = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.Info_Collection_View, "scaleY", 1f);
					Scale_Y.setDuration(ANIMATION_DURATION_HALF);

					Animator_Set = new AnimatorSet();
					Animator_Set.playTogether(Rotate, Scale_X, Scale_Y);
					Animator_Set.start();

					Core.Current_Screen = "Main";
					break;
			}

			Flip_Screen_Is_In_Progress = false;
		}
	};

	public void Flip_Main_And_Get_Date_and_Time_Screens()
	{
		Flip_Screen_Is_In_Progress = true;

		int action = -1;
		ObjectAnimator Rotate;
		ObjectAnimator Scale_X;
		ObjectAnimator Scale_Y;
		AnimatorSet Animator_Set;

		if(Core.Current_Screen.equals("Main")) action = 0;
		if(Core.Current_Screen.equals("Get_Date_And_Time")) action = 1;

		switch(action)
		{
			case 0:
				Core.Get_Date_And_Time_Screen.Load_Screen();

				Rotate = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.Info_Collection_View, "rotationY", 0f, -90f);
				Rotate.setDuration(ANIMATION_DURATION_HALF);
				Rotate.setInterpolator(new AccelerateDecelerateInterpolator());

				Scale_X = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.Info_Collection_View, "scaleX", 0.35f);
				Scale_X.setDuration(ANIMATION_DURATION_HALF);
				Scale_Y = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.Info_Collection_View, "scaleY", 0.35f);
				Scale_Y.setDuration(ANIMATION_DURATION_HALF);

				Animator_Set = new AnimatorSet();
				Animator_Set.playTogether(Rotate, Scale_X, Scale_Y);
				Animator_Set.addListener(Animation_Listener_2);
				Animator_Set.start();
				break;

			case 1:
				Rotate = ObjectAnimator.ofFloat(Core.Get_Date_And_Time_Screen.Get_Date_And_Time_View, "rotationY", 0f, -90f);
				Rotate.setDuration(ANIMATION_DURATION_HALF);
				Rotate.setInterpolator(new AccelerateDecelerateInterpolator());

				Scale_X = ObjectAnimator.ofFloat(Core.Get_Date_And_Time_Screen.Get_Date_And_Time_View, "scaleX", 0.35f);
				Scale_X.setDuration(ANIMATION_DURATION_HALF);
				Scale_Y = ObjectAnimator.ofFloat(Core.Get_Date_And_Time_Screen.Get_Date_And_Time_View, "scaleY", 0.35f);
				Scale_Y.setDuration(ANIMATION_DURATION_HALF);

				Animator_Set = new AnimatorSet();
				Animator_Set.playTogether(Rotate, Scale_X, Scale_Y);
				Animator_Set.addListener(Animation_Listener_2);
				Animator_Set.start();
				break;
		}
	}

	public void Flip_Main_And_Get_Location_Screens()
	{
		Flip_Screen_Is_In_Progress = true;

		int action = -1;
		ObjectAnimator Rotate;
		ObjectAnimator Scale_X;
		ObjectAnimator Scale_Y;
		AnimatorSet Animator_Set;

		if(Core.Current_Screen.equals("Main")) action = 0;
		if(Core.Current_Screen.equals("Get_From_Location")) action = 1;
		if(Core.Current_Screen.equals("Get_To_Location")) action = 1;

		switch(action)
		{
			case 0:
				Core.Get_Location_Screen.Load_Screen();
				Rotate = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.Info_Collection_View, "rotationY", 0f, -90f);
				Rotate.setDuration(ANIMATION_DURATION_HALF);
				Rotate.setInterpolator(new AccelerateDecelerateInterpolator());

				Scale_X = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.Info_Collection_View, "scaleX", 0.35f);
				Scale_X.setDuration(ANIMATION_DURATION_HALF);
				Scale_Y = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.Info_Collection_View, "scaleY", 0.35f);
				Scale_Y.setDuration(ANIMATION_DURATION_HALF);

				Animator_Set = new AnimatorSet();
				Animator_Set.playTogether(Rotate, Scale_X, Scale_Y);
				Animator_Set.addListener(Animation_Listener);
				Animator_Set.start();
				break;

			case 1:
				Rotate = ObjectAnimator.ofFloat(Core.Get_Location_Screen.Get_Location_View, "rotationY", 0f, -90f);
				Rotate.setDuration(ANIMATION_DURATION_HALF);
				Rotate.setInterpolator(new AccelerateDecelerateInterpolator());

				Scale_X = ObjectAnimator.ofFloat(Core.Get_Location_Screen.Get_Location_View, "scaleX", 0.35f);
				Scale_X.setDuration(ANIMATION_DURATION_HALF);
				Scale_Y = ObjectAnimator.ofFloat(Core.Get_Location_Screen.Get_Location_View, "scaleY", 0.35f);
				Scale_Y.setDuration(ANIMATION_DURATION_HALF);

				Animator_Set = new AnimatorSet();
				Animator_Set.playTogether(Rotate, Scale_X, Scale_Y);
				Animator_Set.addListener(Animation_Listener);
				Animator_Set.start();
				break;
		}
	}

	public void Fade_Out_And_Scroll()
	{
		ObjectAnimator Scale_Y = ObjectAnimator.ofFloat(Core.Get_Location_Screen.Confirm_Location_Layout, "scaleY", 0f);
		ObjectAnimator Fade_Out = ObjectAnimator.ofFloat(Core.Get_Location_Screen.Confirm_Location_Layout, "alpha", 0f);

		AnimatorSet Animator_Set = new AnimatorSet();
		Animator_Set.playTogether(Scale_Y, Fade_Out);
		Animator_Set.setDuration(ANIMATION_DURATION_FULL);
		Animator_Set.addListener(new Animator.AnimatorListener()
		{
			@Override public void onAnimationStart(Animator animator){}
			@Override public void onAnimationCancel(Animator animator){}
			@Override public void onAnimationRepeat(Animator animator){}
			@Override public void onAnimationEnd(Animator animator)
			{
				Core.Get_Location_Screen.Confirm_Location_Layout.setVisibility(View.GONE);
				ObjectAnimator Scroll_Y = ObjectAnimator.ofInt(Core.Main_Scroll_View, "scrollY", 0);
				Scroll_Y.setDuration(ANIMATION_DURATION_FULL);
				Scroll_Y.start();
			}
		});
		Animator_Set.start();
	}

	public void Fade_In_And_Scroll()
	{
		Core.Get_Location_Screen.Confirm_Location_Button_Layout.setVisibility(View.VISIBLE);
		Core.Get_Location_Screen.Confirm_Location_Layout.setVisibility(View.VISIBLE);

		ObjectAnimator Scale_Y = ObjectAnimator.ofFloat(Core.Get_Location_Screen.Confirm_Location_Layout, "scaleY", 0f, 1f);
		ObjectAnimator Fade_In = ObjectAnimator.ofFloat(Core.Get_Location_Screen.Confirm_Location_Layout, "alpha", 0f, 1f);
		ObjectAnimator Scroll_Y = ObjectAnimator.ofInt(Core.Main_Scroll_View, "scrollY", Core.Get_Location_Screen.Main_Top_Layout.getBottom());

		AnimatorSet Animator_Set = new AnimatorSet();
		Animator_Set.playTogether(Scale_Y, Fade_In, Scroll_Y);
		Animator_Set.setDuration(ANIMATION_DURATION_FULL);
		Animator_Set.start();
	}

	public void Move_From_Down()
	{
		Core.Info_Collection_Screen.Swap_To_From_Button.setClickable(false);

		ObjectAnimator Reset_From_Ghost_Y = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.From_Ghost_Text_View, "translationY", 0);
		Reset_From_Ghost_Y.setDuration(0);
		Reset_From_Ghost_Y.start();

		int[] To_Location = new int[2];
		Core.Info_Collection_Screen.To_Text_View.getLocationInWindow(To_Location);

		int[] From_Ghost_Location = new int[2];
		Core.Info_Collection_Screen.From_Ghost_Text_View.getLocationInWindow(From_Ghost_Location);

		int Y_Difference = To_Location[1] - From_Ghost_Location[1];

		ObjectAnimator Move_Y = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.From_Ghost_Text_View, "translationY", Y_Difference);
		Move_Y.setDuration(ANIMATION_DURATION_FULL);
		Move_Y.addListener(new Animator.AnimatorListener()
		{
			@Override public void onAnimationStart(Animator animator){}
			@Override public void onAnimationCancel(Animator animator){}
			@Override public void onAnimationRepeat(Animator animator){}

			@Override public void onAnimationEnd(Animator animator)
			{
				Core.Info_Collection_Screen.From_Text_View.setText("From");
				Core.Info_Collection_Screen.From_Text_View.setTextColor(Color.parseColor("#7C7C7C"));
				Core.Info_Collection_Screen.From_Text_View.setVisibility(View.VISIBLE);

				Core.Info_Collection_Screen.To_Text_View.setText(Core.Info_Collection_Screen.From_Ghost_Text_View.getText());
				Core.Info_Collection_Screen.To_Text_View.setTextColor(Color.parseColor("#DDDDDD"));
				Core.Info_Collection_Screen.To_Text_View.setVisibility(View.VISIBLE);

				Core.Info_Collection_Screen.From_Ghost_Text_View.setVisibility(View.GONE);
				Core.Info_Collection_Screen.From_Ghost_Text_View.setText("");

				Core.Info_Collection_Screen.Swap_To_From_Button.setClickable(true);
			}
		});
		Move_Y.start();
	}

	public void Move_To_Up()
	{
		Core.Info_Collection_Screen.Swap_To_From_Button.setClickable(false);

		int[] From_Location = new int[2];
		Core.Info_Collection_Screen.From_Text_View.getLocationInWindow(From_Location);
		int[] To_Location = new int[2];
		Core.Info_Collection_Screen.To_Text_View.getLocationInWindow(To_Location);

		int Y_Difference = To_Location[1] - From_Location[1];

		ObjectAnimator Reset_To_Ghost_Y = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.To_Ghost_Text_View, "translationY", Y_Difference);
		Reset_To_Ghost_Y.setDuration(0);
		Reset_To_Ghost_Y.start();

		ObjectAnimator Move_Y = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.To_Ghost_Text_View, "translationY", 0);
		Move_Y.setDuration(ANIMATION_DURATION_FULL);
		Move_Y.addListener(new Animator.AnimatorListener()
		{
			@Override public void onAnimationStart(Animator animator){}
			@Override public void onAnimationCancel(Animator animator){}
			@Override public void onAnimationRepeat(Animator animator){}

			@Override public void onAnimationEnd(Animator animator)
			{
				Core.Info_Collection_Screen.To_Text_View.setText("To");
				Core.Info_Collection_Screen.To_Text_View.setTextColor(Color.parseColor("#7C7C7C"));
				Core.Info_Collection_Screen.To_Text_View.setVisibility(View.VISIBLE);

				Core.Info_Collection_Screen.From_Text_View.setText(Core.Info_Collection_Screen.To_Ghost_Text_View.getText());
				Core.Info_Collection_Screen.From_Text_View.setTextColor(Color.parseColor("#DDDDDD"));
				Core.Info_Collection_Screen.From_Text_View.setVisibility(View.VISIBLE);

				Core.Info_Collection_Screen.To_Ghost_Text_View.setVisibility(View.GONE);
				Core.Info_Collection_Screen.To_Ghost_Text_View.setText("");

				Core.Info_Collection_Screen.Swap_To_From_Button.setClickable(true);
			}
		});
		Move_Y.start();
	}

	public void Move_From_and_To()
	{
		Core.Info_Collection_Screen.Swap_To_From_Button.setClickable(false);

		int[] From_Location = new int[2];
		Core.Info_Collection_Screen.From_Text_View.getLocationInWindow(From_Location);
		int[] To_Location = new int[2];
		Core.Info_Collection_Screen.To_Text_View.getLocationInWindow(To_Location);

		int Y_Difference = To_Location[1] - From_Location[1];

		ObjectAnimator Reset_From_Ghost_Y = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.From_Ghost_Text_View, "translationY", 0);
		Reset_From_Ghost_Y.setDuration(0);
		Reset_From_Ghost_Y.start();

		ObjectAnimator Reset_To_Ghost_Y = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.To_Ghost_Text_View, "translationY", Y_Difference);
		Reset_To_Ghost_Y.setDuration(0);
		Reset_To_Ghost_Y.start();

		ObjectAnimator Move_From = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.From_Ghost_Text_View, "translationY", Y_Difference);
		ObjectAnimator Move_To = ObjectAnimator.ofFloat(Core.Info_Collection_Screen.To_Ghost_Text_View, "translationY", 0);

		AnimatorSet Animator_Set = new AnimatorSet();
		Animator_Set.playTogether(Move_From, Move_To);
		Animator_Set.addListener(new Animator.AnimatorListener()
		{
			@Override public void onAnimationStart(Animator animator){}
			@Override public void onAnimationCancel(Animator animator){}
			@Override public void onAnimationRepeat(Animator animator){}
			@Override public void onAnimationEnd(Animator animator)
			{
				Core.Info_Collection_Screen.To_Text_View.setText(Core.Info_Collection_Screen.From_Ghost_Text_View.getText());
				Core.Info_Collection_Screen.To_Text_View.setVisibility(View.VISIBLE);

				Core.Info_Collection_Screen.From_Ghost_Text_View.setVisibility(View.GONE);
				Core.Info_Collection_Screen.From_Ghost_Text_View.setText("");
				Core.Info_Collection_Screen.From_Ghost_Text_View.setTop(Core.Info_Collection_Screen.From_Text_View.getTop());

				Core.Info_Collection_Screen.From_Text_View.setText(Core.Info_Collection_Screen.To_Ghost_Text_View.getText());
				Core.Info_Collection_Screen.From_Text_View.setVisibility(View.VISIBLE);

				Core.Info_Collection_Screen.To_Ghost_Text_View.setVisibility(View.GONE);
				Core.Info_Collection_Screen.To_Ghost_Text_View.setText("");
				Core.Info_Collection_Screen.To_Ghost_Text_View.setTop(Core.Info_Collection_Screen.To_Text_View.getTop());

				Core.Info_Collection_Screen.Swap_To_From_Button.setClickable(true);
			}
		});
		Animator_Set.setDuration(ANIMATION_DURATION_FULL);
		Animator_Set.start();
	}

	public void Resize_Recent_Places_Layout()
	{
		int height;

		if(Core.Get_Location_Screen.recent_places_layout_is_expanded)
			height = Core.Info_Collection_Screen.From_Text_View.getHeight() * 3;
		else
			height = Core.Info_Collection_Screen.From_Text_View.getHeight() * Core.Get_Location_Screen.Recent_Places_Data.length();

		ValueAnimator Increase_Height = ValueAnimator.ofInt(Core.Get_Location_Screen.Saved_Places_Layout.getMeasuredHeight(), height);
		ObjectAnimator Scroll_Y = ObjectAnimator.ofInt(Core.Main_Scroll_View, "scrollY", Core.Get_Location_Screen.Get_Location_View.getBottom());

		Increase_Height.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override public void onAnimationUpdate(ValueAnimator valueAnimator)
			{
				int val = (Integer)valueAnimator.getAnimatedValue();

				ViewGroup.LayoutParams layoutParams = Core.Get_Location_Screen.Saved_Places_Layout.getLayoutParams();
				layoutParams.height = val;

				Core.Get_Location_Screen.Saved_Places_Layout.setLayoutParams(layoutParams);
			}
		});

		AnimatorSet Animator_Set = new AnimatorSet();
		Animator_Set.playTogether(Increase_Height, Scroll_Y);
		Animator_Set.setDuration(ANIMATION_DURATION_FULL);
		Animator_Set.start();
	}

	public void Slide_Main_and_Route_Permutation_Progress_Screens()
	{
		Core.Main_Container_View_Switcher.addView(Core.Route_Permutation_Progress_Screen.Route_Permutation_Progress_View);

		Animation inAnimation = AnimationUtils.loadAnimation(Base_Context, R.anim.left_in); inAnimation.setDuration(ANIMATION_DURATION_HALF);
		Animation outAnimation = AnimationUtils.loadAnimation(Base_Context, R.anim.left_out); outAnimation.setDuration(ANIMATION_DURATION_HALF);
		Core.Main_Container_View_Switcher.setInAnimation(inAnimation);
		Core.Main_Container_View_Switcher.setOutAnimation(outAnimation);

		Core.Main_Container_View_Switcher.showNext();

		Core.Current_Screen = "Route_Permutation_Progress";
	}

	public void Slide_Route_Permutation_Progress_and_Found_Routes_Screens()
	{
		Core.Main_Container_View_Switcher.removeViewAt(0);
		Core.Main_Container_View_Switcher.addView(Core.Found_Routes_Screen.Found_Routes_Scroll_View);

		Animation inAnimation = AnimationUtils.loadAnimation(Base_Context, R.anim.left_in); inAnimation.setDuration(ANIMATION_DURATION_HALF);
		Animation outAnimation = AnimationUtils.loadAnimation(Base_Context, R.anim.left_out); outAnimation.setDuration(ANIMATION_DURATION_HALF);
		Core.Main_Container_View_Switcher.setInAnimation(inAnimation);
		Core.Main_Container_View_Switcher.setOutAnimation(outAnimation);

		Core.Main_Container_View_Switcher.showNext();

		Core.Current_Screen = "Found_Routes";
	}

	public void Slide_Found_Routes_and_Route_Details_Screens()
	{
		Core.Main_Container_View_Switcher.removeViewAt(0);
		Core.Main_Container_View_Switcher.addView(Core.Route_Details_Screen.Route_Details_View);

		Animation inAnimation = AnimationUtils.loadAnimation(Base_Context, R.anim.left_in); inAnimation.setDuration(ANIMATION_DURATION_HALF);
		Animation outAnimation = AnimationUtils.loadAnimation(Base_Context, R.anim.left_out); outAnimation.setDuration(ANIMATION_DURATION_HALF);
		Core.Main_Container_View_Switcher.setInAnimation(inAnimation);
		Core.Main_Container_View_Switcher.setOutAnimation(outAnimation);

		Core.Main_Container_View_Switcher.showNext();

		Core.Current_Screen = "Route_Details";
	}
	*/
}