package cn.ismartv.voice.core.handler;

import cn.ismartv.voice.core.filter.FilterUtil;
import cn.ismartv.voice.core.filter.WordFilterResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.ismartv.voice.data.http.JsonRes;
import cn.ismartv.voice.data.http.VoiceResultEntity;

import java.util.List;

/**
 * Created by huaijie on 2016/1/30.
 */
public class JsonDomainHandler {
    private static final String TAG = "JsonDomainHandler";

    private HandleCallback callback;
    private AppHandleCallback appHandleCallback;
    private MultiHandlerCallback multiHandlerCallback;

    public JsonDomainHandler(String json, HandleCallback handleCallback, AppHandleCallback appHandleCallback, MultiHandlerCallback multiHandlerCallback) {
        this.multiHandlerCallback = multiHandlerCallback;
        this.appHandleCallback = appHandleCallback;
        this.callback = handleCallback;
        getElement(json);
    }

    private void getElement(String result) {
        long tag = System.currentTimeMillis();
        VoiceResultEntity[] voiceResultEntity = new Gson().fromJson(result.toString(), VoiceResultEntity[].class);
        for (VoiceResultEntity entity : voiceResultEntity) {
            JsonElement jsonElement = new JsonParser().parse(entity.getJson_res());
            JsonRes jsonRes = new Gson().fromJson(jsonElement, JsonRes.class);
            String rawText = jsonRes.getRaw_text();
            List<WordFilterResult> filterResults = FilterUtil.filter(rawText);
            if (!filterResults.isEmpty()) {

            } else {
                Object resultObject = jsonRes.getResults();
                if (resultObject == null || new JsonParser().parse(resultObject.toString()).getAsJsonArray().size() == 0) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("raw_text", rawText);
                    new DefaultHandler(jsonObject, callback, tag, 1);
                } else if (new JsonParser().parse(resultObject.toString()).getAsJsonArray().size() == 1) {
                    JsonArray jsonArray = new JsonParser().parse(resultObject.toString()).getAsJsonArray();
                    if (jsonArray.size() == 1) {
                        JsonObject o = jsonArray.get(0).getAsJsonObject();
                        o.addProperty("raw_text", rawText);
                        String domain = o.get("domain").getAsString();
                        switch (domain) {
                            case "app":
                                new AppHandler(o, appHandleCallback, tag, jsonArray.size());
                                break;
                            case "video":
                                new VideoHandler(o, callback, tag, jsonArray.size());
                                break;
                            case "weather":
                                new WeatherHandler(o);
                                break;
                        }

                    } else {
                        JsonArray array = new JsonParser().parse(resultObject.toString()).getAsJsonArray();
                        new MultiHandler(array, rawText, multiHandlerCallback);
                    }
                }
            }
        }
    }
}
