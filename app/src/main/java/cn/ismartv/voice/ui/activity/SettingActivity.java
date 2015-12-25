package cn.ismartv.voice.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import cn.ismartv.voice.R;
import cn.ismartv.voice.ui.fragment.SettingFragment;

/**
 * Created by huaijie on 12/22/15.
 */
public class SettingActivity extends Activity {
    private static final String SETTING_FRAGMENT_TAG = "SETTING_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if (savedInstanceState != null) {

        } else {
            getFragmentManager().beginTransaction().add(R.id.content, new SettingFragment()).commit();
        }
    }
}
