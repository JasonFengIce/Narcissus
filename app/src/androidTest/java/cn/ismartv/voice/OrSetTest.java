package cn.ismartv.voice;

import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by huaijie on 2016/1/20.
 */
public class OrSetTest extends AndroidTestCase {
    private static final String TAG = "OrSetTest";

    public void testORSet() {
        int set1 = 1;
        int set2 = 2;

        int a = 4 | 5;

        Log.i(TAG, String.valueOf(a));

    }

}
