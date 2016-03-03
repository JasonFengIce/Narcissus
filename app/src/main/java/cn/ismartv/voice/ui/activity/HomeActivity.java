package cn.ismartv.voice.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.event.AnswerAvailableEvent;
import cn.ismartv.voice.data.table.CityTable;
import cn.ismartv.voice.ui.fragment.RecognizeErrorFragment;
import cn.ismartv.voice.ui.fragment.RecommendAppFragment;
import cn.ismartv.voice.ui.fragment.RecommendFragment;
import cn.ismartv.voice.ui.fragment.VoiceFragment;
import cn.ismartv.voice.ui.fragment.WeatherFragment;
import cn.ismartv.voice.ui.widget.MessagePopWindow;

/**
 * Created by huaijie on 1/18/16.
 */
public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivity";

    private static final String VOICE_FRAGMENT_TAG = "voice_fragment_tag";
    private static final String CONTENT_FRAGMENT_TAG = "content_fragment_tag";
    private static final String APP_SEARCH_FRAGMENT_TAG = "app_search_fragment_tag";
    private static final String WEATHER_FRAGMENT_TAG = "weather_fragment_tag";
    private static final String RECOGNIZE_ERROR_FRAGMENT_TAG = "recognize_error_fragment_tag";
    private VoiceFragment voiceFragment;
    private RecommendFragment contentFragment;
    private RecommendAppFragment appSearchFragment;
    private WeatherFragment weatherFragment;
    private RecognizeErrorFragment recognizeErrorFragment;

    private boolean voiceBtnIsDown = false;
    private View contentView;
    private MessagePopWindow networkEorrorPopupWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkEorrorPopupWindow = new MessagePopWindow(this, "网络异常，请检查网络", null);
        contentView = LayoutInflater.from(this).inflate(R.layout.activity_home, null);
        setContentView(contentView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        voiceFragment = new VoiceFragment();
        contentFragment = new RecommendFragment();
        transaction.replace(R.id.right_fragment, contentFragment, VOICE_FRAGMENT_TAG);
        transaction.replace(R.id.left_fragment, voiceFragment, CONTENT_FRAGMENT_TAG);
        transaction.commit();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F12) {
            Log.i(TAG, "key code: " + keyCode);
            if (!voiceBtnIsDown) {
                voiceBtnIsDown = true;
//                showLeftFragment(voiceFragment);
                voiceFragment.startSpeek();
            }
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (contentFragment.isVisible()) {
                if (contentFragment.isRecommend()) {
                    backToInit();
                } else {
                    onBackPressed();
                }
            } else {
                backToInit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F12) {
            voiceBtnIsDown = false;
            Log.e(TAG, "up: " + keyCode);
            voiceFragment.stopSpeek();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void backToInit() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        voiceFragment = new VoiceFragment();
        contentFragment = new RecommendFragment();
        transaction.replace(R.id.left_fragment, voiceFragment, CONTENT_FRAGMENT_TAG);
        transaction.setCustomAnimations(
                R.anim.push_left_in,
                R.anim.push_left_out);
        transaction.replace(R.id.right_fragment, contentFragment, VOICE_FRAGMENT_TAG);
        transaction.commit();
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


    public void recommendVideo() {
        contentFragment = new RecommendFragment();
        contentFragment.setIsRecommend(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.right_fragment, contentFragment).commit();


    }

    public void recommendApp() {
        appSearchFragment = new RecommendAppFragment();
        appSearchFragment.setIsRecommend(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.right_fragment, appSearchFragment).commit();
    }

    public void showWeatherNoRegion(CityTable cityTable) {
        weatherFragment = new WeatherFragment();
        weatherFragment.setLocation(cityTable);
        getSupportFragmentManager().beginTransaction().replace(R.id.right_fragment, weatherFragment).commit();
    }


    public void showRecognizeError() {
        recognizeErrorFragment = new RecognizeErrorFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.right_fragment, recognizeErrorFragment).commit();
    }

    public void showNetworkErrorPop() {
        networkEorrorPopupWindow = new MessagePopWindow(this, "网络异常，请检查网络", null);
        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!networkEorrorPopupWindow.isShowing()) {
                    networkEorrorPopupWindow.showAtLocation(contentView, Gravity.CENTER, new MessagePopWindow.ConfirmListener() {
                                @Override
                                public void confirmClick(View view) {
                                    networkEorrorPopupWindow.dismiss();
                                    System.exit(0);
                                }
                            },
                            null
                    );
                }
            }
        }, 2000);
    }


    @Subscribe
    public void answerAvailable(AnswerAvailableEvent event) {
        showNetworkErrorPop();
    }

    @Override
    protected void onDestroy() {
        networkEorrorPopupWindow = null;
        super.onDestroy();
    }
}
