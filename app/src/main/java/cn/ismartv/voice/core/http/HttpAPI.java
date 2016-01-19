package cn.ismartv.voice.core.http;

import cn.ismartv.voice.core.update.VersionInfoV2Entity;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by huaijie on 1/19/16.
 */
public class HttpAPI {


    public interface CheckAppUpdate {
        @GET("/api/v2/upgrade/")
        Call<VersionInfoV2Entity> doRequest(

        );
    }
}
