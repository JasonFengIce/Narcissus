package cn.ismartv.voice.core.http;

import cn.ismartv.voice.core.update.VersionInfoV2Entity;
import cn.ismartv.voice.data.http.AppSearchResponseEntity;
import cn.ismartv.voice.data.http.SemanticSearchRequestEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by huaijie on 1/19/16.
 */
public class HttpAPI {

    public interface CheckAppUpdate {
        @GET("/api/v2/upgrade/")
        Call<VersionInfoV2Entity> doRequest(
                @Query("sn") String sn,
                @Query("manu") String manu,
                @Query("app") String app,
                @Query("modelname") String modelname,
                @Query("loc") String loc,
                @Query("ver") String ver
        );
    }


    public interface SemanticSearch {
        @POST("/api/tv/semanticsearch/")
        Call<SemanticSearchResponseEntity> doRequest(
                @Body SemanticSearchRequestEntity entity
        );
    }

    public interface AppSearch {
        @FormUrlEncoded
        @POST("/api/tv/qjhappsearch/")
        Call<AppSearchResponseEntity> doRequest(
                @Field("keyword") String keyword,
                @Field("page_no") int pageNo,
                @Field("page_count") int pageCount
        );
    }

    public interface WeatherSearch {
        @GET("/{geoId}.xml")
        Call<ResponseBody> doRequest(
                @Path("geoId") String geoId
        );
    }
}