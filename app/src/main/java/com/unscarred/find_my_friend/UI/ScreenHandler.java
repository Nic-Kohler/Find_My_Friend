package com.unscarred.find_my_friend.UI;

import com.unscarred.find_my_friend.R;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;


import java.util.ArrayList;


public class ScreenHandler
{
	private Context                         Base_Context;
	private UICore                          UI_Core;
	private ArrayList<ScreenParameters>     Previous_Screens;
	private ArrayList<PreloadedView>        Preloaded_Views;
	private ArrayList<PreloadedLoadScreenFunction> Preloaded_Load_Screen_Functions = null;
	private View                            Current_View;

	private Animation                       Fade_In;
	private Animation                       Fade_Out;
	private Animation                       Left_In;
	private Animation                       Left_Out;
	private Animation                       Right_In;
	private Animation                       Right_Out;
	private Animation                       Up_In;
	private Animation                       Up_Out;
	private Animation                       Down_In;
	private Animation                       Down_Out;

	private final int                       FINALIZATION_DELAY = 800;


	public ScreenHandler(Context context, UICore ui_core)
	{
		Base_Context = context;
		UI_Core = ui_core;
		Previous_Screens = new ArrayList<ScreenParameters>();
		Preloaded_Views  = new ArrayList<PreloadedView>();

		Fade_In = AnimationUtils.loadAnimation(Base_Context, R.anim.fade_in);
		Fade_Out = AnimationUtils.loadAnimation(Base_Context, R.anim.fade_out);
		Left_In = AnimationUtils.loadAnimation(Base_Context, R.anim.left_in);
		Left_Out = AnimationUtils.loadAnimation(Base_Context, R.anim.left_out);
		Right_In = AnimationUtils.loadAnimation(Base_Context, R.anim.right_in);
		Right_Out = AnimationUtils.loadAnimation(Base_Context, R.anim.right_out);
		Up_In = AnimationUtils.loadAnimation(Base_Context, R.anim.up_in);
		Up_Out = AnimationUtils.loadAnimation(Base_Context, R.anim.up_out);
		Down_In = AnimationUtils.loadAnimation(Base_Context, R.anim.down_in);
		Down_Out = AnimationUtils.loadAnimation(Base_Context, R.anim.down_out);
	}

	public interface Get_Popup_Window_Function{ PopupWindow Execute(); }
	public interface Load_Screen_Function{ View Execute(); }
	public interface Screen_Function{ void Execute(); }

	private View Get_View(String Target_Screen, Load_Screen_Function Load_Function)
	{
		boolean View_Is_Not_Loaded = true;
		View current_view = null;

		for(int i = 0; i < Preloaded_Views.size() && View_Is_Not_Loaded; i++)
		{
			if(Preloaded_Views.get(i).Get_Screen_Name().equals(Target_Screen))
			{
				current_view = Preloaded_Views.get(i).Get_Target_View();
				View_Is_Not_Loaded = false;

				Preloaded_Load_Screen_Functions = null;
				Preloaded_Views = new ArrayList<PreloadedView>();
			}
		}

		if(View_Is_Not_Loaded) current_view = Load_Function.Execute();

		return current_view;
	}

	private void Save_Screen_Parameters(String target_screen, final FrameLayout Target_Frame_Layout, String In_Animation, String Out_Animation,
	                                    Load_Screen_Function Load_Function, Screen_Function Post_Load_Function, Screen_Function Kill_Function)
	{
		if(!target_screen.equals("Splash"))
		{
			ScreenParameters screen_parameters = new ScreenParameters();

			screen_parameters.Set_Screen_Name(target_screen);
			screen_parameters.Set_Target_Frame_Layout(Target_Frame_Layout);
			screen_parameters.Set_In_Animations(In_Animation);
			screen_parameters.Set_Out_Animations(Out_Animation);
			screen_parameters.Set_Load_Function(Load_Function);
			screen_parameters.Set_Post_Load_Function(Post_Load_Function);
			screen_parameters.Set_Kill_Function(Kill_Function);
			screen_parameters.Set_Preloaded_Load_Screen_Functions(Preloaded_Load_Screen_Functions);

			Previous_Screens.add(screen_parameters);
		}
	}

	private void Preload_Views()
	{
		if(Preloaded_Load_Screen_Functions != null)
		{
			for(int i = 0; i < Preloaded_Load_Screen_Functions.size(); i++)
			{
				Log.d("Nic Says", "*** Loading Preloaded Screens: " + Preloaded_Load_Screen_Functions.get(i).Get_Screen_Name());

				Preloaded_Views.add(new PreloadedView(Preloaded_Load_Screen_Functions.get(i).Get_Screen_Name(),
				                                      Preloaded_Load_Screen_Functions.get(i).Get_Load_Screen_Function().Execute()));
			}
		}
	}

	public void Set_Preloaded_Load_Screen_Functions(ArrayList<PreloadedLoadScreenFunction> preloaded_load_screen_functions)
	{
		Preloaded_Load_Screen_Functions = preloaded_load_screen_functions;
	}

	private void Finalize_Show_Screen(final FrameLayout Target_Frame_Layout)
	{
		new Handler().postDelayed(new Runnable()
		{
			public void run()
			{
				if(Target_Frame_Layout.getChildCount() > 1) Target_Frame_Layout.removeViewAt(0);

				if(Previous_Screens.size() > 1 && Previous_Screens.get(Previous_Screens.size() - 2).Get_Kill_Function() != null)
					Previous_Screens.get(Previous_Screens.size() - 2).Get_Kill_Function().Execute();

				Preload_Views();
			}
		}, FINALIZATION_DELAY);
	}

	public Animation Load_In_Animation_Parameters(String in_anim, final Screen_Function Post_Load_Function)
	{
		final Animation in_animation = Get_Animation(in_anim);

		in_animation.setAnimationListener(new Animation.AnimationListener()
		{
			@Override public void onAnimationStart(Animation animation){}
			@Override public void onAnimationRepeat(Animation animation){}

			@Override public void onAnimationEnd(Animation animation)
			{
				if(Post_Load_Function != null) Post_Load_Function.Execute();

				in_animation.setAnimationListener(null);
			}
		});

		return in_animation;
	}

	public Animation Load_Out_Animation_Parameters()
	{
		final int last_screen_index = Previous_Screens.size() - 1;
		final Animation out_animation = Get_Animation(Previous_Screens.get(last_screen_index).Get_Out_Animation());

		out_animation.setAnimationListener(new Animation.AnimationListener()
		{
			@Override public void onAnimationStart(Animation animation){}
			@Override public void onAnimationRepeat(Animation animation){}

			@Override public void onAnimationEnd(Animation animation)
			{
				Previous_Screens.get(last_screen_index).Get_Target_Frame_Layout().removeViewAt(1);

				if(Previous_Screens.size() > 1)
				{
					if(Previous_Screens.get(last_screen_index).Get_Kill_Function() != null)
						Previous_Screens.get(last_screen_index).Get_Kill_Function().Execute();

					Previous_Screens.remove(last_screen_index);
				}

				out_animation.setAnimationListener(null);
			}
		});

		return out_animation;
	}

	public void Show_Screen(String target_screen, FrameLayout Target_Frame_Layout, String In_Animation, String Out_Animation,
	                        Load_Screen_Function Load_Function, Screen_Function Post_Load_Function, Screen_Function Kill_Function)
	{
		Current_View = Get_View(target_screen, Load_Function);

		Save_Screen_Parameters(target_screen, Target_Frame_Layout, In_Animation, Out_Animation, Load_Function, Post_Load_Function, Kill_Function);
		Animation animation = Load_In_Animation_Parameters(In_Animation, Post_Load_Function);

		Current_View.setAnimation(animation);
		Current_View.setVisibility(View.INVISIBLE);

		Target_Frame_Layout.addView(Current_View);

		Current_View.setVisibility(View.VISIBLE);
		Current_View.animate();

		Finalize_Show_Screen(Target_Frame_Layout);
	}

	public void Show_Previous_Screen()
	{
		if(Previous_Screens.size() > 1 && Previous_Screens.get(Previous_Screens.size() - 1).Is_Popup())
		{
			Previous_Screens.get(Previous_Screens.size() - 1).Get_Target_Popup_Window().dismiss();
		}
		else
		{
			if(Previous_Screens.size() > 1)
			{
				View previous_view = Previous_Screens.get(Previous_Screens.size() - 2).Get_Load_Function().Execute();
				Current_View.setAnimation(Load_Out_Animation_Parameters());

				previous_view.setVisibility(View.INVISIBLE);
				Current_View.setVisibility(View.INVISIBLE);

				Previous_Screens.get(Previous_Screens.size() - 1).Get_Target_Frame_Layout().addView(previous_view, 0);

				previous_view.setVisibility(View.VISIBLE);
				Current_View.setVisibility(View.VISIBLE);

				Current_View.animate();
			}
		}

		if(Previous_Screens.size() < 2)
		{
			if(Previous_Screens.get(0).Get_Kill_Function() != null)
				Previous_Screens.get(0).Get_Kill_Function().Execute();
			Previous_Screens.remove(0);

			((Activity)Base_Context).finish();
		}
	}

	public void Show_Popup(String target_screen, Load_Screen_Function Load_Function, Screen_Function Kill_Function, Get_Popup_Window_Function Get_Target_Popup_Window)
	{
		View target_view = Load_Function.Execute();
		PopupWindow target_popup_window = Get_Target_Popup_Window.Execute();

		ScreenParameters screen_parameters = new ScreenParameters();

		screen_parameters.Set_Is_Popup_Window(true);
		screen_parameters.Set_Screen_Name(target_screen);
		screen_parameters.Set_Target_Popup_Window(target_popup_window);
		screen_parameters.Set_Load_Function(Load_Function);
		screen_parameters.Set_Kill_Function(Kill_Function);

		Previous_Screens.add(screen_parameters);

		target_popup_window.showAtLocation(target_view, Gravity.CENTER, 0, 0);
	}

	private Animation Get_Animation(String anim)
	{
		Animation selected_animation = null;

		if(anim.equals("Left_In"))      selected_animation = Left_In;
		if(anim.equals("Left_Out"))     selected_animation = Left_Out;
		if(anim.equals("Right_In"))     selected_animation = Right_In;
		if(anim.equals("Right_Out"))    selected_animation = Right_Out;
		if(anim.equals("Up_In"))        selected_animation = Up_In;
		if(anim.equals("Up_Out"))       selected_animation = Up_Out;
		if(anim.equals("Down_In"))      selected_animation = Down_In;
		if(anim.equals("Down_Out"))     selected_animation = Down_Out;
		if(anim.equals("Fade_In"))      selected_animation = Fade_In;
		if(anim.equals("Fade_Out"))     selected_animation = Fade_Out;

		return selected_animation;
	}

	public String Get_Current_Screen_Name()
	{
		String screen_name = null;

		if(Previous_Screens.size() != 0) screen_name = Previous_Screens.get(Previous_Screens.size() - 1).Get_Screen_name();

		return screen_name;
	}

	public class PreloadedLoadScreenFunction
	{
		private String                  Screen_Name;
		private Load_Screen_Function    Load_Screen;

		public PreloadedLoadScreenFunction(String screen_name, Load_Screen_Function load_screen)
		{
			Screen_Name = screen_name;
			Load_Screen = load_screen;
		}

		public String Get_Screen_Name(){ return Screen_Name; }
		public Load_Screen_Function Get_Load_Screen_Function(){ return Load_Screen; }
	}

	public class PreloadedView
	{
		private String   Screen_Name;
		private View     Target_View;

		public PreloadedView(String screen_name, View target_view)
		{
			Screen_Name = screen_name;
			Target_View = target_view;
		}

		public String Get_Screen_Name(){ return Screen_Name; }
		public View Get_Target_View(){ return Target_View; }
	}

	private class ScreenParameters
	{
		private boolean                 Is_Popup_Window = false;
		private Load_Screen_Function    Load_Function;
		private Screen_Function         Post_Load_Function = null;
		private Screen_Function         Kill_Function = null;
		private ArrayList<PreloadedLoadScreenFunction> Preloaded_Load_Screen_Functions;
		private FrameLayout             Target_Frame_Layout = null;
		private PopupWindow             Target_Popup_Window = null;
		private String                  Screen_Name;
		private String                  In_Animation;
		private String                  Out_Animation;

		public void Set_Is_Popup_Window(boolean is_popup_window){ Is_Popup_Window = is_popup_window; }
		public boolean Is_Popup(){ return Is_Popup_Window; }

		public void Set_Load_Function(Load_Screen_Function load_screen_function){ Load_Function = load_screen_function; }
		public Load_Screen_Function Get_Load_Function(){ return Load_Function; }

		public void Set_Post_Load_Function(Screen_Function post_load_screen_function){ Post_Load_Function = post_load_screen_function; }
		public Screen_Function Get_Post_Load_Function(){ return Post_Load_Function; }

		public void Set_Kill_Function(Screen_Function kill_screen_function){ Kill_Function = kill_screen_function; }
		public Screen_Function Get_Kill_Function(){ return Kill_Function; }

		public void Set_Preloaded_Load_Screen_Functions(ArrayList<PreloadedLoadScreenFunction> preloaded_load_screen_functions)
		{ Preloaded_Load_Screen_Functions = preloaded_load_screen_functions; }
		public ArrayList<PreloadedLoadScreenFunction> Get_Preloaded_Load_Screen_Functions(){ return Preloaded_Load_Screen_Functions; }

		public void Set_Target_Frame_Layout(FrameLayout target_view_switcher){ Target_Frame_Layout = target_view_switcher; }
		public FrameLayout Get_Target_Frame_Layout(){ return Target_Frame_Layout; }

		public void Set_Target_Popup_Window(PopupWindow target_popup_window){ Target_Popup_Window = target_popup_window; }
		public PopupWindow Get_Target_Popup_Window(){ return Target_Popup_Window; }

		public void Set_Screen_Name(String screen_name){ Screen_Name = screen_name; }
		public String Get_Screen_name(){ return Screen_Name; }

		public void Set_In_Animations(String in_animation){ In_Animation = in_animation; }
		public String Get_In_Animation(){ return In_Animation; }

		public void Set_Out_Animations(String out_animation){ Out_Animation = out_animation; }
		public String Get_Out_Animation(){ return Out_Animation; }
	}
}
