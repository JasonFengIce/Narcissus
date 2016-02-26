package cn.ismartv.voice.http_api;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;

import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.http.AppSearchResponseEntity;
import cn.ismartv.voice.data.http.RecommandAppEntity;
import cn.ismartv.voice.data.http.SemanticSearchRequestEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 1/20/16.
 */
public class SkyApiTest extends AndroidTestCase {
    private static final String TAG = "SkyApiTest";

    public void testVodSearch() {
        String data = "{\n" +
                "        \"raw_text\": \"在线播放非诚勿扰\",\n" +
                "        \"domain\": \"video\",\n" +
                "        \"intent\": \"online\",\n" +
                "        \"score\": 1,\n" +
                "        \"demand\": 0,\n" +
                "        \"update\": 1,\n" +
                "        \"object\": {\n" +
                "            \"name\": \"非诚勿扰\"\n" +
                "        }\n" +
                "    }";
        JsonObject j =  new JsonParser().parse(data).getAsJsonObject();
        SemanticSearchRequestEntity entity = new SemanticSearchRequestEntity();
        entity.setSemantic(j);
        entity.setContent_type("movie");
        entity.setPage_on(1);
        entity.setPage_count(30);

        Retrofit retrofit = HttpManager.getInstance().resetAdapter_QIANGUANGZHAO;

        try {
            Response<SemanticSearchResponseEntity> response = retrofit.create(HttpAPI.SemanticSearch.class).doRequest(entity).execute();
            String s = new Gson().toJson(response.body());
            Log.i(TAG, s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void testAppSearch() {
//        Retrofit retrofit = HttpManager.getInstance().resetAdapter_QIANGUANGZHAO;
//        try {
//            Response<AppSearchResponseEntity> response = retrofit.create(HttpAPI.AppSearch.class).doRequest("三国", 1, 30).execute();
//            String s = new Gson().toJson(response.body());
//            Log.i(TAG, s);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void testWeatherSearch() {
        Retrofit retrofit = HttpManager.getInstance().resetAdapter_MEDIA_LILY;
        try {
            Response<ResponseBody> response = retrofit.create(HttpAPI.WeatherSearch.class).doRequest("101010100").execute();
            String s = response.body().string();
            Log.i(TAG, s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testWords() {
        Retrofit retrofit = HttpManager.getInstance().resetAdapter_WUGUOJUN;
        try {
            Response<List<String>> response = retrofit.create(HttpAPI.Words.class).doRequest(5).execute();
            String s = new Gson().toJson(response.body());
            Log.i(TAG, s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testHotWords() {
        Retrofit retrofit = HttpManager.getInstance().resetAdapter_WUGUOJUN;
//        try {
//            Response<ResponseBody> response = retrofit.create(HttpAPI.SharpHotWords.class).doRequest(8).execute();
//            String s = new Gson().toJson(response.body());
//            Log.i(TAG, s);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void testRecommandApp() {
        Retrofit retrofit = HttpManager.getInstance().resetAdapter_WUGUOJUN;
//        try {
//            Response<List<RecommandAppEntity>> response = retrofit.create(HttpAPI.RecommandApp.class).doRequest(8).execute();
//            String s = new Gson().toJson(response.body());
//            Log.i(TAG, s);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
