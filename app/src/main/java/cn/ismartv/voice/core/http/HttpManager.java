package cn.ismartv.voice.core.http;

import java.util.concurrent.TimeUnit;

import cn.ismartv.log.interceptor.HttpLoggingInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 1/15/16.
 */
public class HttpManager {
    private static final int DEFAULT_TIMEOUT = 2;

    private static HttpManager ourInstance = new HttpManager();
    private static final String TSN_BAIDU_HOST = "http://tsn.baidu.com";
    private static final String OPENAPI_BAIDU_HOST = "https://openapi.baidu.com";


    public OkHttpClient client;

    public Retrofit restAdapter_TSN_BAIDU_HOST;
    public Retrofit restAdapter_OPENAPI_BAIDU;
    public Retrofit resetAdapter_APP_UPDATE;

    public static HttpManager getInstance() {
        return ourInstance;
    }

    private HttpManager() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
        restAdapter_TSN_BAIDU_HOST = new Retrofit.Builder()
                .client(client)
                .baseUrl(TSN_BAIDU_HOST)
                .build();

        restAdapter_OPENAPI_BAIDU = new Retrofit.Builder()
                .client(client)
                .baseUrl(OPENAPI_BAIDU_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        resetAdapter_APP_UPDATE = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://oak.t.tvxio.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


}
