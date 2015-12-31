package cn.ismartv.voice;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import cn.ismartv.voice.ui.fragment.TestFragment;


public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    private static final String MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {

        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.main_content, new TestFragment(), MAIN_FRAGMENT_TAG).commit();
        }
    }



}