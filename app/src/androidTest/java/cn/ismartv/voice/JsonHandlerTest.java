package cn.ismartv.voice;

import android.test.AndroidTestCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by huaijie on 1/4/16.
 */
public class JsonHandlerTest extends AndroidTestCase {
    private static final String TAG = "JsonHandlerTest";

    public void testAppJson() {
        try {
            InputStream inputStream = getContext().getAssets().open("json_test.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer result = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
//            new JsonResultHandler(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
