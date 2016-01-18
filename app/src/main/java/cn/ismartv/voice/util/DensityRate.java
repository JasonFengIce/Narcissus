package cn.ismartv.voice.util;

import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;

/**
 * Created by huaijie on 1/18/16.
 */
public class DensityRate {

    public static float getDensityRate(FragmentActivity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        // 屏幕密度DPI（120 / 160 / 240）
        int densityDpi = metric.densityDpi;
        float rate = (float) densityDpi / (float) 160;
        return rate;
    }
}
