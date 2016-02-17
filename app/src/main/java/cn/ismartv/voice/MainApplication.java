package cn.ismartv.voice;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import cn.ismartv.injectdb.library.app.Application;
import cn.ismartv.voice.core.initialize.InitializeProcess;

/**
 * Created by huaijie on 1/4/16.
 */
public class MainApplication extends Application {
    private static final String TAG = "MainApplication";

    private static Context mContext;

    private static String locationPY;
    private static String snToken;
    private static String appUpdateDomain;
    private static String apiDomain;
    private static long geoId;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        initializeInfo();
        new Thread(new InitializeProcess(this)).start();
    }

    public static Context getContext() {
        return mContext;
    }

    private void initializeInfo() {
        try {
            Context daisyContext = createPackageContext("tv.ismar.daisy", Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences sharedPreferences = daisyContext.getSharedPreferences("account", Context.MODE_WORLD_READABLE);
            appUpdateDomain = sharedPreferences.getString("app_update_domain", "");
            locationPY = sharedPreferences.getString("province_py", "");
            snToken = sharedPreferences.getString("sn_token", "");
            apiDomain = sharedPreferences.getString("api_domain", "");
            geoId = sharedPreferences.getLong("geoid", 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static String getLocationPY() {
        return locationPY;
    }

    public static Long getGeoId() {
        return geoId;
    }


    public static String getSnToken() {
        return snToken;
    }


    public static String getAppUpdateDomain() {
        return appUpdateDomain;
    }

    public static String getApiDomain() {
        return apiDomain;
    }
}
