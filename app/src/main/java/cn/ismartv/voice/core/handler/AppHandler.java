package cn.ismartv.voice.core.handler;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.google.gson.JsonObject;

import java.util.List;

import cn.ismartv.injectdb.library.query.Select;
import cn.ismartv.voice.MainApplication;
import cn.ismartv.voice.data.table.AppTable;

/**
 * Created by huaijie on 1/4/16.
 */
public class AppHandler {
    private static final String TAG = "AppHandler";

    public AppHandler(JsonObject jsonObject) {
        String appName = jsonObject.get("object").getAsJsonObject().get("appname").getAsString();
        String intent = jsonObject.get("intent").getAsString();
        switch (intent) {
            case "open":
                appOpen(appName);
                break;
            default:
                break;
        }
    }

    private void appOpen(String name) {
        List<AppTable> appTables = new Select().from(AppTable.class).where("app_name like ?", "%" + name + "%").execute();
        if (appTables.size() == 1) {

            launchApp(appTables.get(0).app_package);
        } else {

        }
    }

    private void launchApp(String appPackage) {
        Context context = MainApplication.getContext();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(appPackage);
        context.startActivity(intent);
    }
}
