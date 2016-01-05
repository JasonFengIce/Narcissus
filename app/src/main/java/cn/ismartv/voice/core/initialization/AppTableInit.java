package cn.ismartv.voice.core.initialization;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

import cn.ismartv.injectdb.library.ActiveAndroid;
import cn.ismartv.injectdb.library.query.Delete;
import cn.ismartv.voice.data.table.AppTable;

/**
 * Created by huaijie on 1/4/16.
 */
public class AppTableInit {
    private static AppTableInit instance;

    private AppTableInit() {

    }

    public static AppTableInit getInstance() {
        if (instance == null) {
            instance = new AppTableInit();
        }
        return instance;
    }

    public void getLocalAppList(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> apps = packageManager.getInstalledPackages(0);
        new Delete().from(AppTable.class).execute();
        ActiveAndroid.beginTransaction();
        try {
            for (PackageInfo packageInfo : apps) {
                // 非系统应用
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    AppTable appTable = new AppTable();
                    appTable.app_name = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
                    appTable.app_package = packageInfo.packageName;
                    appTable.version_code = packageInfo.versionCode;
                    appTable.version_name = packageInfo.versionName;
                    appTable.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
