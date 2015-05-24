package com.dugan.restartlogger;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by Todd on 1/4/2015.
 */
public class CustomAnimations {

    public static void expand(Context context, final View myView){
        myView.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        myView.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(myView, 0, myView.getMeasuredHeight());
        mAnimator.start();
    }

    public static void collapse(Context context, final View myView){
        int finalHeight = myView.getHeight();

        ValueAnimator mAnimator = slideAnimator(myView, finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                myView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();
    }

    public static void rotate180(final Context context, final View rotateView){
        final Animation rotate = AnimationUtils.loadAnimation(context, R.anim.rotate);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (rotateView.getRotation() == 180){
                    rotateView.setRotation(0);
                } else {
                    rotateView.setRotation(180);
                }
                ScheduleFragment.refreshScheduleCursor(context);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rotateView.setAnimation(rotate);
    }

    public static void slideRight(final Context context, final View view){
        final Animation anim = AnimationUtils.loadAnimation(context, R.anim.rotate);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ScheduleFragment.refreshScheduleCursor(context);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.setAnimation(anim);
    }

    private static ValueAnimator slideAnimator(final View myView, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = myView.getLayoutParams();
                layoutParams.height = value;
                myView.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

}
