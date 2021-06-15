package com.morlag.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import javax.xml.datatype.Duration;

public class SunsetFragment extends Fragment {

    private View mSceneView;
    private View mSunView;
    private View mSkyView;

    private float mSunTop;
    private float mSunBottom;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;

    private boolean mIsNight = false;

    public static SunsetFragment newInstance() {
        SunsetFragment fragment = new SunsetFragment();
        return fragment;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sunset,container,false);

        mSceneView = v;
        mSkyView = v.findViewById(R.id.sky);
        mSunView = v.findViewById(R.id.sun);

        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mIsNight)
                    startStraightAnimation();
                else
                    startBackwardAnimation();
            }
        });

        mSunView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float sunYStart = mSunView.getTop();
                float sunYEnd = mSkyView.getBottom();
                Toast.makeText(getActivity(),"start="+sunYStart+";end"+sunYEnd,Toast.LENGTH_SHORT).show();
            }
        });

        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSunTop = mSunView.getTop();
        mSunBottom = mSkyView.getBottom();
    }

    private void startStraightAnimation(){
        mIsNight = true;
        float sunYStart = mSunView.getTop();
        float sunYEnd = mSkyView.getBottom();

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "Y", sunYEnd)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        int backgroundColor = mSkyView.getSolidColor();
        ObjectAnimator sunsetSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor",mBlueSkyColor, mSunsetSkyColor)
                .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor",mSunsetSkyColor, mNightSkyColor)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

        //heightAnimator.start();
        //sunsetSkyAnimator.start();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(heightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);
        animatorSet.start();
    }
    private void startBackwardAnimation(){
        mIsNight = false;
        float sunYStart = mSunView.getTop();
        float sunYEnd = mSkyView.getBottom();

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView,"Y",sunYStart)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        int backgroundColor = mSkyView.getSolidColor();
        ObjectAnimator sunsetSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor",mNightSkyColor, mBlueSkyColor)
                .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

        AnimatorSet animatorSet = new AnimatorSet();
        if(mSkyView.getSolidColor() == mNightSkyColor){
            animatorSet
                    .play(heightAnimator)
                    .with(sunsetSkyAnimator)
                    .after(nightSkyAnimator);
        }
        else{
            animatorSet
                    .play(heightAnimator)
                    .with(sunsetSkyAnimator);
        }
        animatorSet.start();
    }
}
