package cn.ismartv.voice.core.handler;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.ismartv.voice.data.http.JsonRes;
import cn.ismartv.voice.data.http.VoiceResultEntity;

/**
 * Created by huaijie on 2016/1/30.
 */
public class JsonDomainHandler {
    private static final String TAG = "JsonDomainHandler";

    private HandleCallback callback;
    private AppHandleCallback appHandleCallback;
    private MultiHandlerCallback multiHandlerCallback;
    private WeatherHandlerCallback weatherHandlerCallback;

    public JsonDomainHandler(String json, HandleCallback handleCallback, AppHandleCallback appHandleCallback, MultiHandlerCallback multiHandlerCallback,
                             WeatherHandlerCallback weatherHandlerCallback) {
        this.multiHandlerCallback = multiHandlerCallback;
        this.appHandleCallback = appHandleCallback;
        this.callback = handleCallback;
        this.weatherHandlerCallback = weatherHandlerCallback;
        getElement(json);
    }

    private void getElement(String result) {
        VoiceResultEntity[] voiceResultEntity = new Gson().fromJson(result.toString(), VoiceResultEntity[].class);
        for (VoiceResultEntity entity : voiceResultEntity) {
            JsonElement jsonElement = new JsonParser().parse(entity.getJson_res());
            JsonRes jsonRes = new Gson().fromJson(jsonElement, JsonRes.class);
            String rawText = jsonRes.getRaw_text();
//            List<WordFilterResult> filterResults = FilterUtil.filter(rawText);
//            if (!filterResults.isEmpty()) {
//            } else {
            Object resultObject = jsonRes.getResults();
            String json = new Gson().toJson(resultObject);
            Log.i(TAG, json);
            if (resultObject == null || new JsonParser().parse(json).getAsJsonArray().size() == 0) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("raw_text", rawText);
                new DefaultHandler(jsonObject, callback);
            } else if (new JsonParser().parse(json).getAsJsonArray().size() == 1) {
                JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
                JsonObject o = jsonArray.get(0).getAsJsonObject();
                o.addProperty("raw_text", rawText);
                String domain = o.get("domain").getAsString();
                switch (domain) {
                    case "app":
                        new AppHandler(o, appHandleCallback);
                        break;
                    case "video":
                        new VideoHandler(o, callback);
                        break;
                    case "weather":
                        new WeatherHandler(o, weatherHandlerCallback);
                        break;
                    default:
                        new VideoHandler(o, callback);
                        break;
                }

            } else {
                JsonArray array = new JsonParser().parse(json).getAsJsonArray();
                new MultiHandler(array, rawText, multiHandlerCallback).start();
            }
        }
    }
}
