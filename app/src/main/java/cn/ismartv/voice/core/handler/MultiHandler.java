package cn.ismartv.voice.core.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.ismartv.injectdb.library.query.Select;
import cn.ismartv.voice.AppConstant;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
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
                        Response<AppSearchResponseEntity> response = HttpManager.getInstance().resetAdapter_QIANGUANGZHAO.create(HttpAPI.AppSearch.class)
                                .doRequest(appName, AppConstant.DEFAULT_PAGE_NO, AppConstant.DEFAULT_PAGE_COUNT)
                                .execute();
                        AppSearchResponseEntity responseEntity = response.body();

                        IndicatorResponseEntity entity = new IndicatorResponseEntity();
                        entity.setTitle("全部应用");
                        entity.setType("app");
                        entity.setCount(appTables.size() + responseEntity.getTotal_count());
                        entity.setSearchData(rawText);
                        indicatorList.add(entity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "video":
                    SemanticSearchRequestEntity requestEntity = new SemanticSearchRequestEntity();
                    requestEntity.setData(jsonObject.toString());
                    requestEntity.setContent_type("movie");
                    requestEntity.setPage_on(AppConstant.DEFAULT_PAGE_NO);
                    requestEntity.setPage_count(AppConstant.DEFAULT_PAGE_COUNT);
                    try {
                        Response<SemanticSearchResponseEntity> response = HttpManager.getInstance().resetAdapter_QIANGUANGZHAO.create(HttpAPI.SemanticSearch.class)
                                .doRequest(requestEntity).execute();
                        for (SemanticSearchResponseEntity.Facet facet : response.body().getFacet()) {
                            IndicatorResponseEntity entity = new IndicatorResponseEntity();
                            entity.setTitle(getChineseType(facet.getContent_type()));
                            entity.setType(facet.getContent_type());
                            entity.setCount(facet.getCount());
                            entity.setSearchData(rawText);
                            indicatorList.add(entity);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        //send message
        Message message = messageHandler.obtainMessage(HANDLE_SUCCESS, indicatorList);
        messageHandler.sendMessage(message);
    }

    private String getChineseType(String englishType) {
        switch (englishType) {
            case "sport":
                return "体育";
            case "movie":
                return "电影";
            case "comic":
                return "少儿";
            default:
                return englishType;
        }
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
                        handler.callback.onMultiHandle((List<IndicatorResponseEntity>) msg.obj);
                        break;
                }
            }
        }
    }
}
