package cn.ismartv.voice.core.handler;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import cn.ismartv.injectdb.library.query.Select;
import cn.ismartv.voice.MainApplication;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.data.http.AppSearchRequestParams;
import cn.ismartv.voice.data.http.AppSearchResponseEntity;
import cn.ismartv.voice.data.table.AppTable;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 1/4/16.
 */
public class AppHandler {
    private static final String TAG = "AppHandler";

    public AppHandler(final JsonObject jsonObject, final AppHandleCallback callback) {
        String appName = jsonObject.get("object").getAsJsonObject().get("appname").toString().replace("\"", "");
        String intent = jsonObject.get("intent").toString();
        final List<AppTable> appTables = new Select().from(AppTable.class).where("app_name like ?", "%" + appName + "%").execute();


        Retrofit retrofit = HttpManager.getInstance().resetAdapter_QIANGUANGZHAO;
        AppSearchRequestParams params = new AppSearchRequestParams();
        params.setKeyword(appName);
        params.setContent_type("app");
        params.setPage_count(300);
        params.setPage_no(1);
        retrofit.create(HttpAPI.AppSearch.class).doRequest(params).enqueue(new Callback<AppSearchResponseEntity>() {
            @Override
            public void onResponse(Response<AppSearchResponseEntity> response) {
                Log.i(TAG, new Gson().toJson(response.body()));
                if (response.errorBody() == null) {

                    AppSearchResponseEntity appSearchResponseEntity = response.body();
                    List<AppSearchObjectEntity> appList = new ArrayList<>();
                    for (AppTable appTable : appTables) {

                        AppSearchObjectEntity appSearchObjectEntity = new AppSearchObjectEntity();
                        appSearchObjectEntity.setTitle(appTable.app_name);
                        appSearchObjectEntity.setCaption(appTable.app_package);
                        appSearchObjectEntity.setIsLocal(true);
                        appList.add(appSearchObjectEntity);
                    }
                    AppSearchResponseEntity.Facet[] facet = appSearchResponseEntity.getFacet();

                    if (facet != null) {
                        List<AppSearchObjectEntity> serverAppList = facet[0].getObjects();
                        for (AppSearchObjectEntity entity : serverAppList) {
                            List<AppTable> tables = new Select().from(AppTable.class).where("app_package = ?", entity.getCaption()).execute();
                            if (tables.size() == 0) {
                                appList.add(entity);
                            }else {
                                for (AppTable table : tables) {
                                    AppSearchObjectEntity appSearchObjectEntity = new AppSearchObjectEntity();
                                    appSearchObjectEntity.setTitle(table.app_name);
                                    appSearchObjectEntity.setCaption(table.app_package);
                                    appSearchObjectEntity.setIsLocal(true);
                                    appList.add(appSearchObjectEntity);
                                }
                            }
                        }
                        appSearchResponseEntity.getFacet()[0].setObjects(appList);
                        appSearchResponseEntity.getFacet()[0].setTotal_count(appList.size());
                    }
                    callback.onAppHandleSuccess(appSearchResponseEntity, new Gson().toJson(jsonObject));

                } else {
                    //error
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    private void launchApp(String appPackage) {
        Context context = MainApplication.getContext();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(appPackage);
        context.startActivity(intent);
    }
}
