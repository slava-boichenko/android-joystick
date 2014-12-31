package com.seeyou.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seeyou.R;
import com.seeyou.ui.activities.MainActivity;
import com.seeyou.ui.view.blind.BlindView;

public class NavigationMenuFragment extends ListFragment {
    public static final String TAG = "navigationmenu";

    public static NavigationMenuFragment newInstance() {
        return new NavigationMenuFragment();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //((MainActivity)activity).setControlsVisibility(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.navigation_menu, container, false);
        final BlindView blindView = (BlindView)v.findViewById(R.id.blindView);
        blindView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ViewTreeObserver obs = blindView.getViewTreeObserver();
                obs.removeOnPreDrawListener(this);

                ObjectAnimator animator = ObjectAnimator.ofInt(blindView, "tick", 0, 9000);
                animator.setDuration(1000);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        blindView.setBlindMode(false);
                    }
                });
                animator.start();

                return true;
            }
        });

        //v.setBackgroundColor(getResources().getColor(android.R.color.white));
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setCacheColorHint(android.R.color.transparent);
        getListView().setDivider(null);
        setListAdapter(new NavigationAdapter(getActivity()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private static class NavigationAdapter extends ArrayAdapter<String> {
        LayoutInflater inflater;
        int[] drawables;

        public NavigationAdapter(Context context) {
            super(context, 0, context.getResources().getStringArray(R.array.navigation_menu));
            inflater = LayoutInflater.from(context);
            drawables = new int[]{
                    R.drawable.ic_nav_home, R.drawable.ic_nav_profile,
                    R.drawable.ic_nav_my_status, R.drawable.ic_nav_contacts,
                    R.drawable.ic_nav_inbox, R.drawable.ic_nav_blocked_users,
                    R.drawable.ic_nav_settings, R.drawable.ic_nav_more
            };
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.navigation_menu_item, parent, false);
            updateView(convertView, position);
            return convertView;
        }

        private void updateView(View convertView, int position) {
            TextView tv = (TextView) convertView.findViewById(R.id.text);
            ImageView iv = (ImageView) convertView.findViewById(R.id.image);
            tv.setText(getItem(position));
            iv.setImageResource(drawables[position]);
        }
    }
}
