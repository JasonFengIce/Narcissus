package cn.ismartv.voice;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by huaijie on 1/19/16.
 */
public class UriTest extends AndroidTestCase {
    private static final String TAG = "UriTest";

    private static final String host = "http://oak.t.tvxio.com";

    public void testParseUri() {
        Uri uri = Uri.parse(host);
        String api = uri.toString();
        if (!uri.toString().startsWith("http://") && !uri.toString().startsWith("https://")) {
            api = "http://" + host;
        }

        Log.i(TAG, "url : " + api);
    }

}
