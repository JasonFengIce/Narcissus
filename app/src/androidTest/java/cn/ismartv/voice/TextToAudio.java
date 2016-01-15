package cn.ismartv.voice;

import android.test.AndroidTestCase;
import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.OauthResultEntity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by huaijie on 1/15/16.
 */
public class TextToAudio extends AndroidTestCase {
    private static final String TAG = "TextToAudio";
    private static final String GRANT_TYPE = "client_credentials";
    private static final String CLIENT_ID = "YuKSME6OUvZwv016LktWKkjY";
    private static final String CLIENT_SECRET = "5fead3154852939e74bcaa1248cf33c6";

    /**
     * =============================================
     */
    private static final String LAN = "zh";
    private static final String CUID = "sdafadsdfasdfadhtuytytdgfvwe";
    private static final String CTP = "1";

    public void testApp() {
        getAudio("test");
    }

    private void getAudio(String text) {
        try {
            Response<OauthResultEntity> oauthResp = HttpManager.getInstance().restAdapter_OPENAPI_BAIDU.create(OauthRequest.class).doRequest(GRANT_TYPE, CLIENT_ID, CLIENT_SECRET).execute();
            OauthResultEntity oauthResultEntity = oauthResp.body();
            Response<ResponseBody> audioResp = HttpManager.getInstance().restAdapter_TSN_BAIDU_HOST.create(AudioRequest.class).doRequest("你好", LAN, CUID, CTP, oauthResultEntity.getAccess_token()).execute();

            File file = new File("/sdcard/jjkk.mp3");
            FileReader fileReader = new FileReader(file);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    interface AudioRequest {
        @GET("/text2audio")
        Call<ResponseBody> doRequest(
                @Query("tex") String tex,
                @Query("lan") String lan,
                @Query("cuid") String cuid,
                @Query("ctp") String ctp,
                @Query("tok") String tok
        );
    }


    interface OauthRequest {
        @FormUrlEncoded
        @POST("/oauth/2.0/token")
        Call<OauthResultEntity> doRequest(
                @Field("grant_type") String grantType,
                @Field("client_id") String clientId,
                @Field("client_secret") String clientSecret
        );

    }
}
