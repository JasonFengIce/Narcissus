package cn.ismartv.voice.app_update;

import android.test.AndroidTestCase;
import android.util.Log;

import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.core.update.VersionInfoV2Entity;
import cn.ismartv.voice.util.DeviceUtil;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huaijie on 1/19/16.
 */
public class AppUpdate extends AndroidTestCase {
    private static final String TAG = "AppUpdate";

    public void testCheckUpdate() {

        String location = "SH";
        String sn = "go_grf7qt3h";
        String app = "voice";
        String ver = String.valueOf(1);
        String manu = "google";
        String model = DeviceUtil.getModelName();
        HttpManager.getInstance().resetAdapter_APP_UPDATE.create(HttpAPI.CheckAppUpdate.class).doRequest(sn, manu, app, model, location, ver).enqueue(new Callback<VersionInfoV2Entity>() {
            @Override
            public void onResponse(Response<VersionInfoV2Entity> response) {
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }
}
