package cn.ismartv.voice.core.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.ismartv.injectdb.library.query.Select;
import cn.ismartv.voice.AppConstant;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.data.http.AppSearchRequestParams;
import cn.ismartv.voice.data.http.AppSearchResponseEntity;
import cn.ismartv.voice.data.http.IndicatorResponseEntity;
import cn.ismartv.voice.data.http.SemanticSearchRequestEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.data.table.AppTable;
import retrofit2.Response;

/**
 * Created by huaijie on 2016/1/30.
 */
public class MultiHandler extends Thread {
    private static final int HANDLE_SUCCESS = 0x0001;
    private JsonArray jsonArray;
    private String rawText;
    private MessageHandler messageHandler;
    private MultiHandlerCallback callback;

    public MultiHandler(JsonArray jsonArray, String rawText, MultiHandlerCallback callback) {
        messageHandler = new MessageHandler(this);
        this.jsonArray = jsonArray;
        this.rawText = rawText;
        this.callback = callback;
    }

    @Override
    public void run() {
        List<IndicatorResponseEntity> indicatorList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            jsonObject.addProperty("raw_text", rawText);
            String domain = jsonObject.get("domain").getAsString();
            switch (domain) {
                case "app":
                    String appName = jsonObject.get("object").getAsJsonObject().get("appname").toString().replace("\"", "");
                    final List<AppTable> appTables = new Select().from(AppTable.class).where("app_name like ?", "%" + appName + "%").execute();
                    try {
                        AppSearchRequestParams params = new AppSearchRequestParams();
                        params.setKeyword(appName);
                        params.setContent_type("app");
                        params.setPage_count(30);
                        params.setPage_no(1);
                        Response<AppSearchResponseEntity> response = HttpManager.getInstance().resetAdapter_QIANGUANGZHAO.create(HttpAPI.AppSearch.class)
                                .doRequest(params)
                                .execute();
                        AppSearchResponseEntity responseEntity = response.body();

                        List<AppSearchObjectEntity> appList = new ArrayList<>();
                        for (AppTable appTable : appTables) {

                            AppSearchObjectEntity appSearchObjectEntity = new AppSearchObjectEntity();
                            appSearchObjectEntity.setTitle(appTable.app_name);
                            appSearchObjectEntity.setCaption(appTable.app_package);
                            appSearchObjectEntity.setIsLocal(true);
                            appList.add(appSearchObjectEntity);
                        }


                        AppSearchResponseEntity.Facet[] facet = responseEntity.getFacet();

                        if (facet != null) {
                            List<AppSearchObjectEntity> serverAppList = facet[0].getObjects();
                            for (AppSearchObjectEntity entity : serverAppList) {
                                List<AppTable> tables = new Select().from(AppTable.class).where("app_package = ?", entity.getCaption()).execute();
                                if (tables.size() == 0) {
                                    appList.add(entity);
                                }
                            }
                        }

                        IndicatorResponseEntity entity = new IndicatorResponseEntity();
                        entity.setType("app");
                        entity.setSearchData(appList);
                        entity.setSemantic(new Gson().toJson(jsonObject));

                        indicatorList.add(entity);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "video":
                case "tv_show":
                    SemanticSearchRequestEntity requestEntity = new SemanticSearchRequestEntity();
                    requestEntity.setSemantic(jsonObject);
                    requestEntity.setPage_on(AppConstant.DEFAULT_PAGE_NO);
                    requestEntity.setPage_count(AppConstant.DEFAULT_PAGE_COUNT);
                    try {
                        Response<SemanticSearchResponseEntity> response = HttpManager.getInstance().resetAdapter_QIANGUANGZHAO.create(HttpAPI.SemanticSearch.class)
                                .doRequest(requestEntity).execute();
                        IndicatorResponseEntity entity = new IndicatorResponseEntity();
                        entity.setType("video");
                        entity.setSearchData(response.body());
                        entity.setSemantic(new Gson().toJson(jsonObject));
                        indicatorList.add(0, entity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        //send message
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("list", indicatorList);
        hashMap.put("rawText", rawText);
        Message message = messageHandler.obtainMessage(HANDLE_SUCCESS, hashMap);
        messageHandler.sendMessage(message);
    }


    private class MessageHandler extends Handler {
        public WeakReference<MultiHandler> weakReference;

        public MessageHandler(MultiHandler handler) {
            super(Looper.getMainLooper());
            this.weakReference = new WeakReference<>(handler);
        }

        @Override
        public void handleMessage(Message msg) {
            MultiHandler handler = weakReference.get();
            if (handler != null) {
                switch (msg.what) {
                    case HANDLE_SUCCESS:
                        HashMap<String, Object> hashMap = (HashMap) msg.obj;
                        List<IndicatorResponseEntity> list = (List<IndicatorResponseEntity>) hashMap.get("list");
                        String rawText = (String) hashMap.get("rawText");
                        handler.callback.onMultiHandle(list, rawText);
                        break;
                }
            }
        }
    }
}
