package cn.ismartv.voice.core.filter;

import java.util.List;

/**
 * Created by huaijie on 2016/1/20.
 */
public class FilterUtil {
    private static final String TAG = "FilterUtil";
    private static final String[] words1 = new String[]{"好"};

    public static List<WordFilterResult> filter(String content) {
        word_filter wf = new word_filter();
        wf.add_wrods(words1, 1);
        List<WordFilterResult> results = wf.Match(content);
        return results;
    }


    public void input(String action, String name){
        String  key  = action + name;
        switch (key){
            case "dakaiweixin":
                break;
            default:
                break;
        }
    }
}
