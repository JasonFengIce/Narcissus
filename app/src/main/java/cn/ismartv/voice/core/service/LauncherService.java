package cn.ismartv.voice.core.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by huaijie on 2/23/16.
 */
public class LauncherService extends Service {
    private static final String TAG = "LauncherService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "start service");
        Intent i = new Intent();
        i.setAction("cn.ismartv.voice.home");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        return super.onStartCommand(intent, flags, startId);
    }
}
