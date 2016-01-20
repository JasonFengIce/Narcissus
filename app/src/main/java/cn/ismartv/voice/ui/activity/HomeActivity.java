package cn.ismartv.voice.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.core.update.AppUpdateUtilsV2;
import cn.ismartv.voice.data.http.SemanticSearchRequestEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.ui.fragment.ContentFragment;
import cn.ismartv.voice.ui.fragment.IndicatorFragment;
import cn.ismartv.voice.ui.fragment.VoiceFragment;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 1/18/16.
 */
public class HomeActivity extends FragmentActivity {
    private static final String VOICE_FRAGMENT_TAG = "voice_fragment_tag";
    private static final String CONTENT_FRAGMENT_TAG = "content_fragment_tag";
    private static final String INDICATOR_FRAGMENT_TAG = "indicator_fragment_tag";


    private VoiceFragment voiceFragment;
    private ContentFragment contentFragment;
    private IndicatorFragment indicatorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        voiceFragment = new VoiceFragment();
        contentFragment = new ContentFragment();
        indicatorFragment = new IndicatorFragment();
        AppUpdateUtilsV2.getInstance(this).checkAppUpdate();

        if (savedInstanceState != null) {

        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.left_fragment, voiceFragment, VOICE_FRAGMENT_TAG);
            transaction.add(R.id.right_fragment, contentFragment, CONTENT_FRAGMENT_TAG);
            transaction.add(R.id.left_fragment, indicatorFragment, INDICATOR_FRAGMENT_TAG);
            transaction.hide(indicatorFragment);
            transaction.commit();
        }
    }


    public void handleVoice() {
        searchVod(null);
    }

    public void handleIndicatorClick(String contentType) {
        searchVod(contentType);
    }

    private void showIndicatorFragment(SemanticSearchResponseEntity entity) {
        hideFragment(voiceFragment);
        showFragment(indicatorFragment);
        indicatorFragment.initIndicator(entity);
    }

    private void refreshContentFragment(SemanticSearchResponseEntity entity) {
        showFragment(contentFragment);
        contentFragment.notifyDataChanged(entity);
    }

    private void showFragment(Fragment fragment) {
        if (fragment.isHidden()) {
            getSupportFragmentManager().beginTransaction().show(fragment).commit();
        }

    }

    private void hideFragment(Fragment fragment) {
        if (fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().hide(fragment).commit();
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


    private void searchVod(final String contentType) {
        String data = "{\n" +
                "        \"raw_text\": \"在线播放非诚勿扰\",\n" +
                "        \"domain\": \"video\",\n" +
                "        \"intent\": \"online\",\n" +
                "        \"score\": 1,\n" +
                "        \"demand\": 0,\n" +
                "        \"update\": 1,\n" +
                "        \"object\": {\n" +
                "            \"name\": \"非诚勿扰\"\n" +
                "        }\n" +
                "    }";
        SemanticSearchRequestEntity entity = new SemanticSearchRequestEntity();
        entity.setData(data);
        if (!TextUtils.isEmpty(contentType)) {
            entity.setContent_type(contentType);
        }

        entity.setPage_on(1);
        entity.setPage_count(30);

        Retrofit retrofit = HttpManager.getInstance().resetAdapter_SKY;
        retrofit.create(HttpAPI.SemanticSearch.class).doRequest(entity).enqueue(new Callback<SemanticSearchResponseEntity>() {
            @Override
            public void onResponse(Response<SemanticSearchResponseEntity> response) {
                if (TextUtils.isEmpty(contentType)) {
                    showIndicatorFragment(response.body());
                } else {
                    refreshContentFragment(response.body());
                }

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
