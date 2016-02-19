package cn.ismartv.voice.core.handler;

import cn.ismartv.voice.data.http.AppSearchResponseEntity;

/**
 * Created by huaijie on 1/28/16.
 */
public interface AppHandleCallback {
    void onAppHandleSuccess(AppSearchResponseEntity entity, String jsonData);
}
