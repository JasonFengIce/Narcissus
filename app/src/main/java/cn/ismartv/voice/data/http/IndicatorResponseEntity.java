package cn.ismartv.voice.data.http;

import java.util.List;

/**
 * Created by huaijie on 2016/1/30.
 */
public class IndicatorResponseEntity {

    private String title;
    private String searchData;
    private int count;
    private String type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSearchData() {
        return searchData;
    }

    public void setSearchData(String searchData) {
        this.searchData = searchData;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
