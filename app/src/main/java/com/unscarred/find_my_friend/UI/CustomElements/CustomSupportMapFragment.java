package com.unscarred.find_my_friend.UI.CustomElements;

import com.google.android.gms.maps.SupportMapFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CustomSupportMapFragment extends SupportMapFragment
{
	public View mOriginalContentView;
	public TouchableWrapper mTouchView;

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
		mTouchView = new TouchableWrapper(getActivity());
		mTouchView.addView(mOriginalContentView);

		return mTouchView;
	}

	@Override public View getView() { return mOriginalContentView; }
}