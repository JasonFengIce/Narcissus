package cn.ismartv.voice.core.handler;

import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;

/**
 * Created by huaijie on 1/27/16.
 */
public interface HandleCallback {
    void onHandleSuccess(SemanticSearchResponseEntity entity, String rawText);
}
