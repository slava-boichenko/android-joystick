package com.seeyou.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.MapFragment;
import com.seeyou.ui.activities.MainActivity;

/**
 * Created by vyacheslavboychenko on 7/21/14.
 */
public class CYMapFragment extends MapFragment {
    MainActivity mainActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity)activity;
    }


}
