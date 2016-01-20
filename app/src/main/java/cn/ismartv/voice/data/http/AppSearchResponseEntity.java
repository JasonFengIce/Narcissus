package cn.ismartv.voice.data.http;

import java.util.List;

/**
 * Created by huaijie on 1/20/16.
 */
public class AppSearchResponseEntity {
    private int count;
    private int total_count;

    private List<AppSearchObjectEntity> objects;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public List<AppSearchObjectEntity> getObjects() {
        return objects;
    }

    public void setObjects(List<AppSearchObjectEntity> objects) {
        this.objects = objects;
    }
}
