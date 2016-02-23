package cn.ismartv.voice.core.http;

import java.util.List;

import cn.ismartv.voice.core.update.VersionInfoV2Entity;
import cn.ismartv.voice.data.http.ActorRelateRequestParams;
import cn.ismartv.voice.data.http.AppSearchRequestParams;
import cn.ismartv.voice.data.http.AppSearchResponseEntity;
import cn.ismartv.voice.data.http.RecommandAppEntity;
import cn.ismartv.voice.data.http.SemanticSearchRequestEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.data.http.SharpHotWordsEntity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by huaijie on 1/19/16.
 */
public class HttpAPI {

    public interface CheckAppUpdate {
        @GET("api/v2/upgrade/")
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
        @POST("api/tv/semanticsearch/")
        Call<SemanticSearchResponseEntity> doRequest(
                @Body SemanticSearchRequestEntity entity
        );
    }

    public interface AppSearch {
        @POST("api/tv/qjhappsearch/")
        Call<AppSearchResponseEntity> doRequest(
                @Body AppSearchRequestParams entity
        );
    }

    public interface WeatherSearch {
        @GET("/{geoId}.xml")
        Call<ResponseBody> doRequest(
                @Path("geoId") String geoId
        );
    }

    public interface Words {
        @GET("api/tv/words/{count}/")
        Call<List<String>> doRequest(
                @Path("count") int count
        );
    }


    public interface SharpHotWords {
        @GET("api/tv/homepage/sharphotwords/{count}/")
        Call<SharpHotWordsEntity> doRequest(
                @Path("count") int count
        );
    }

    public interface RecommandApp {
        @GET("api/recommand_app/{count}/")
        Call<List<RecommandAppEntity>> doRequest(
                @Path("count") int count
        );
    }

    public interface ActorRelate {
        @POST("api/tv/actorrelate/")
        Call<SemanticSearchResponseEntity> doRequest(
                @Body ActorRelateRequestParams params
        );

    }
}
