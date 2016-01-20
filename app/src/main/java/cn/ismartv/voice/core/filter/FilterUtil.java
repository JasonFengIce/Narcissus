package cn.ismartv.voice.core.filter;

import java.util.List;

/**
 * Created by huaijie on 2016/1/20.
 */
public class FilterUtil {
    private static final String TAG = "FilterUtil";
    private static final String[] words1 = new String[]{"打开", "微信",};
    private static final String[] words2 = new String[]{"应用程序", "应用电子学院", "应用电子"};


    public static int filter(String content) {
        word_filter wf = new word_filter();
        wf.add_wrods(words1, 1);
        wf.add_wrods(words2, 2);
        List<word_filter_result> results = wf.Match(content);

        int tag = 0;
        for (word_filter_result rslt : results) {
            tag = rslt.tag;
        }
        return tag;
    }
}
