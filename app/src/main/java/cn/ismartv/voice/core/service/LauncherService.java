package cn.ismartv.voice.core.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by huaijie on 2/23/16.
 */
public class LauncherService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent i = new Intent();
        i.setAction("cn.ismartv.voice.home");
        startActivity(i);
        return super.onStartCommand(intent, flags, startId);

    }


}
