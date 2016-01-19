package cn.ismartv.voice.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.update.AppUpdateUtilsV2;
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
        AppUpdateUtilsV2.getInstance(this).checkAppUpdate();

        if (savedInstanceState != null) {

        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.left_fragment, voiceFragment, VOICE_FRAGMENT_TAG);
            transaction.add(R.id.right_fragment, contentFragment, CONTENT_FRAGMENT_TAG);
            transaction.commit();
        }
    }

    /**
     * receive app update broadcast, and show update popup window
     */
    class AppUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle bundle = intent.getBundleExtra("data");
            final String path = bundle.getString("path");
            installApk(HomeActivity.this, path);
//            contentView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    showUpdatePopup(contentView, bundle);
//                }
//            }, 2000);

        }
    }

    public void installApk(Context mContext, String path) {
        Uri uri = Uri.parse("file://" + path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

}
