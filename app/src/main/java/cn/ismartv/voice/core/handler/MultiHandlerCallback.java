package cn.ismartv.voice.core.handler;

import java.util.List;

import cn.ismartv.voice.data.http.IndicatorResponseEntity;

/**
 * Created by huaijie on 2016/1/30.
 */
public interface MultiHandlerCallback {
    void onHandle(List<IndicatorResponseEntity> list);
}
