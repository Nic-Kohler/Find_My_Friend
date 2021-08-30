package com.unscarred.find_my_friend.Common;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.view.Display;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.unscarred.find_my_friend.R;


public class ThemeSettings
{
	private Context     Base_Context;

	public  Vibrator    Vibe;

	public  Typeface    Font_Regular;
	public  Typeface    Font_Light;
	public  Typeface    Font_Bold;

	public  int         Screen_Width;
	public  int         Screen_Height;

	private float       DP_Scale;

	public  final int   FONT_SIZE = 16;

	public  Animation   Up_In;
	public  Animation   Down_Out;
	public  Animation   Left_In;
	public  Animation   Left_Out;
	public  Animation   Right_In;
	public  Animation   Right_Out;

	public int DP(int size){ return Math.round(size * DP_Scale); }
	public int PX(int size){ return Math.round(size / DP_Scale); }


	public ThemeSettings(Context context)
	{
		Base_Context = context;

		Vibe = (Vibrator)Base_Context.getSystemService(Context.VIBRATOR_SERVICE);

		DP_Scale = Base_Context.getResources().getDisplayMetrics().density;

		Font_Regular = Typeface.createFromAsset(Base_Context.getAssets(), "fonts/Quicksand-Regular.otf");
		Font_Light = Typeface.createFromAsset(Base_Context.getAssets(), "fonts/Quicksand-Light.otf");
		Font_Bold = Typeface.createFromAsset(Base_Context.getAssets(), "fonts/Quicksand-Bold.otf");

		Display display = ((WindowManager)Base_Context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		Point size = new Point();
		display.getSize(size);

		Screen_Width = size.x;
		Screen_Height = size.y;

		Up_In = AnimationUtils.loadAnimation(Base_Context, R.anim.up_in);
		Down_Out = AnimationUtils.loadAnimation(Base_Context, R.anim.down_out);
		Left_In = AnimationUtils.loadAnimation(Base_Context, R.anim.left_in);
		Left_Out = AnimationUtils.loadAnimation(Base_Context, R.anim.left_out);
		Right_In = AnimationUtils.loadAnimation(Base_Context, R.anim.right_in);
		Right_Out = AnimationUtils.loadAnimation(Base_Context, R.anim.right_out);
	}
}