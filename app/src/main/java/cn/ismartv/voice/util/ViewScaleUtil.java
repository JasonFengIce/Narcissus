package cn.ismartv.voice.util;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.view.View;

import cn.ismartv.voice.R;

/**
 * Created by huaijie on 1/31/16.
 */
public class ViewScaleUtil {
    public static void scaleToLarge(View view, float rate) {
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{1.0F, rate});
        objectAnimatorX.setDuration(100L);
        objectAnimatorX.start();
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{1.0F, rate});
        objectAnimatorY.setDuration(100L);
        objectAnimatorY.start();
    }


    public static void scaleToNormal(View view, float rate) {
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{rate, 1.0F});
        objectAnimatorX.setDuration(100L);
        objectAnimatorX.start();
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{rate, 1.0F});
        objectAnimatorY.setDuration(100L);
        objectAnimatorY.start();
    }


    public static void scaleOut1(View view) {

        Animator animator = AnimatorInflater.loadAnimator(view.getContext(), R.animator.scaleout_poster);
        animator.setTarget(view);
        animator.start();
    }

    public static void scaleIn1(View view) {

        Animator animator = AnimatorInflater.loadAnimator(view.getContext(), R.animator.scalein_poster);
        animator.setTarget(view);
        animator.start();
    }

    public static void scaleLarge_1_3_(View view) {

        Animator animator = AnimatorInflater.loadAnimator(view.getContext(), R.animator.scaleout_hotword);
        animator.setTarget(view);
        animator.start();
    }

    public static void scaleNormal_1_3(View view) {

        Animator animator = AnimatorInflater.loadAnimator(view.getContext(), R.animator.scalein_hotword);
        animator.setTarget(view);
        animator.start();
    }
}
