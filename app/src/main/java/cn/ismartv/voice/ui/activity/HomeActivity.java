package cn.ismartv.voice.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import cn.ismartv.voice.R;
import cn.ismartv.voice.ui.fragment.ContentFragment;
import cn.ismartv.voice.ui.fragment.VoiceFragment;

/**
 * Created by huaijie on 1/18/16.
 */
public class HomeActivity extends FragmentActivity {
    private static final String VOICE_FRAGMENT_TAG = "voice_fragment_tag";
    private static final String CONTENT_FRAGMENT_TAG = "voice_fragment_tag";


    private VoiceFragment voiceFragment;
    private ContentFragment contentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        voiceFragment = new VoiceFragment();
        contentFragment = new ContentFragment();


        if (savedInstanceState != null) {

        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.left_fragment, voiceFragment, VOICE_FRAGMENT_TAG);
            transaction.add(R.id.right_fragment, contentFragment, CONTENT_FRAGMENT_TAG);
            transaction.commit();
        }
    }
}
