package cn.ismartv.voice;

import android.content.Context;

import cn.ismartv.injectdb.library.app.Application;

/**
 * Created by huaijie on 1/4/16.
 */
public class MainApplication extends Application {
    private static final String TAG = "MainApplication";

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

}
