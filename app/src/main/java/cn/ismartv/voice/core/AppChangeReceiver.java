package cn.ismartv.voice.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.ismartv.voice.core.initialization.AppTableInit;

/**
 * Created by huaijie on 2/24/16.
 */
public class AppChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "AppChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "app changed!!!");
        AppTableInit.getInstance().getLocalAppList(context);
    }
}
