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

    public JsonResultHandler(String json) {
        getElement(json);
    }

    private void getElement(String result) {
        VoiceResultEntity[] voiceResultEntity = new Gson().fromJson(result.toString(), VoiceResultEntity[].class);
        for (VoiceResultEntity entity : voiceResultEntity) {
            JsonElement jsonElement = new JsonParser().parse(entity.getJson_res());
            JsonRes jsonRes = new Gson().fromJson(jsonElement, JsonRes.class);
            Object resultObject = jsonRes.getResults();

            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = jsonParser.parse(resultObject.toString()).getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject o = jsonArray.get(i).getAsJsonObject();
                String domain = o.get("domain").getAsString();
                switch (domain) {
                    case "app":
                        new AppHandler(o);
                        break;
                    case "video":
                        new VideoHandler(o);
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
