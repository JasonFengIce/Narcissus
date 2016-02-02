package cn.ismartv.voice.util;

import android.animation.ObjectAnimator;
import android.view.View;

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
}
