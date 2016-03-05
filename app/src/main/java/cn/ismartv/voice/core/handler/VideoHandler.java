package cn.ismartv.voice.core.handler;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import cn.ismartv.voice.core.event.AnswerAvailableEvent;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.http.SemanticSearchRequestEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 1/15/16.
 */
public class VideoHandler {
    private static final String TAG = "VideoHandler";
    private HandleCallback callback;

    public VideoHandler(final JsonObject jsonObject, final HandleCallback call) {
        this.callback = call;
        SemanticSearchRequestEntity entity = new SemanticSearchRequestEntity();
        entity.setSemantic(jsonObject);
        entity.setPage_on(1);
        entity.setPage_count(30);

        Retrofit retrofit = HttpManager.getInstance().resetAdapter_QIANGUANGZHAO;

        retrofit.create(HttpAPI.SemanticSearch.class).doRequest(entity).enqueue(new Callback<SemanticSearchResponseEntity>() {
            @Override
            public void onResponse(Response<SemanticSearchResponseEntity> response) {
                if (response.errorBody() == null) {
                    callback.onHandleSuccess(response.body(), new Gson().toJson(jsonObject));
                } else {
                    EventBus.getDefault().post(new AnswerAvailableEvent(AnswerAvailableEvent.EventType.NETWORK_ERROR, AnswerAvailableEvent.NETWORK_ERROR));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                EventBus.getDefault().post(new AnswerAvailableEvent(AnswerAvailableEvent.EventType.NETWORK_ERROR, AnswerAvailableEvent.NETWORK_ERROR));
            }
        });
    }

}
