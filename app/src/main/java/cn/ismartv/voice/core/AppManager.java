package cn.ismartv.voice.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;

import cn.ismartv.voice.core.exception.AppNoInstallException;

/**
 * Created by huaijie on 12/23/15.
 */
public class AppManager {

    private static AppManager instance;
    private Context context;
    private HashMap<String, String> appHashMap;


    public static AppManager getInstance(Context context) {
        if (instance == null) {
            instance = new AppManager(context);
        }
        return instance;
    }

    private AppManager(Context context) {
        this.context = context;
        appHashMap = new HashMap<>();
    }

    private void getAllApps(Context context) {
        appHashMap.clear();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> apps = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : apps) {
            HashMap<String, String> hashMap = new HashMap<>();
            String appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
            hashMap.put(appName, packageInfo.packageName);
        }
    }

    public void launchApp(String appName) throws AppNoInstallException {
        String appPackage = appHashMap.get(appName);
        if (TextUtils.isEmpty(appPackage)) {
            throw new AppNoInstallException(appName);
        } else {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(appPackage);
            context.startActivity(intent);
        }

    }
}
