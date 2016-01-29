package cn.ismartv.voice;

import android.test.AndroidTestCase;
import android.util.Log;

import java.util.List;

import cn.ismartv.injectdb.library.query.Select;
import cn.ismartv.voice.data.table.AppTable;

/**
 * Created by huaijie on 1/28/16.
 */
public class AppTableTest extends AndroidTestCase {
    private static final String TAG = "AppTableTest";

    public void testApp() {
        final List<AppTable> appTables = new Select().from(AppTable.class).where("app_name like ?", "%" + "豌豆荚" + "%").execute();

        Log.d(TAG, "size: " + appTables.size());
    }
}
