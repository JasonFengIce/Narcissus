package cn.ismartv.voice.core.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.ismartv.voice.data.http.JsonRes;
import cn.ismartv.voice.data.http.VoiceResultEntity;

/**
 * Created by huaijie on 1/4/16.
 */
public class JsonResultHandler {
    private static final String TAG = "JsonResultHandler";
    private HandleCallback callback;

    public JsonResultHandler(String json, HandleCallback handleCallback) {
        this.callback = handleCallback;
        getElement(json);
    }

    private void getElement(String result) {
        VoiceResultEntity[] voiceResultEntity = new Gson().fromJson(result.toString(), VoiceResultEntity[].class);
        for (VoiceResultEntity entity : voiceResultEntity) {
            JsonElement jsonElement = new JsonParser().parse(entity.getJson_res());
            JsonRes jsonRes = new Gson().fromJson(jsonElement, JsonRes.class);
            String rawText = jsonRes.getRaw_text();
            Object resultObject = jsonRes.getResults();

            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = jsonParser.parse(resultObject.toString()).getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject o = jsonArray.get(i).getAsJsonObject();
                String domain = o.get("domain").getAsString();
                switch (domain) {
                    case "app":
                        new AppHandler(o, callback);
                        break;
                    case "video":
                        new VideoHandler(o, callback);
                        break;
                    case "weather":
                        new WeatherHandler(o);
                        break;
                    default:
                        new DefaultHandler(o);
                        break;
                }
            }
        }
    }
}
