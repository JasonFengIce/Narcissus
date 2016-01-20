package cn.ismartv.voice.filter;

import android.test.AndroidTestCase;
import android.util.Log;

import java.util.List;

import cn.ismartv.voice.core.filter.FilterUtil;
import cn.ismartv.voice.core.filter.word_filter;
import cn.ismartv.voice.core.filter.word_filter_result;

/**
 * Created by huaijie on 2016/1/20.
 */
public class FilterTest extends AndroidTestCase {
    private static final String TAG = "FilterTest";

    public void testFilter() {
        word_filter wf = new word_filter();

        String[] words1 = new String[]{"中华", "中华人民共和国", "中华人民", "中华人民共和",
                "中华人民共和国", "中华民族"};
        String[] words2 = new String[]{"应用", "应用程序", "应用电子学院", "应用电子"};

        String[] words3 = new String[]{"刘德华", "张学友", "黄晓明"};

        String[] words4 = new String[]{"少林寺", "新少林寺", "变形金刚", "魔界"};

        wf.add_wrods(words1, 1);
        wf.add_wrods(words2, 2);
        wf.add_wrods(words3, 3);
        wf.add_wrods(words4, 4);

        String[] tests = new String[]{"刘德华的电视剧新少林寺", "刘德华黄晓明新少林寺变形金刚魔界电影",
                "刘德华和黄晓明的电视剧新少林寺以及变形金刚包括魔界电影", "你好，中华人民共和国的应用电子学院故事会应用电子",
                "中华人民共和国", "你好，中华人民共和国", "我爱你中华民族", "我的中华我的中华民族少林寺",
                "刘德华+少林寺中华人民共和国中华中华人民共和"};

        for (String content : tests) {
            List<word_filter_result> results = wf.Match(content);
            Log.i(TAG, "content---> " + content);

            int i = 0;
            for (word_filter_result rslt : results) {
                i++;
                Log.i(TAG, i + " : " +
                        rslt.start + " - " + rslt.end +
                        content.substring(rslt.start, rslt.end + 1) + " tag" + rslt.tag);
            }
            System.out.println();
        }

    }

    public void testCustom() {
        int tag = FilterUtil.filter("请打开我的微信");
        Log.i("custom", "tag: " + tag);
    }

}
