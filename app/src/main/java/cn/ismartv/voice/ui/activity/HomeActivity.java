package cn.ismartv.voice.ui.activity;

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

import java.util.ArrayList;
import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.ScreenManager;
import cn.ismartv.voice.core.event.AnswerAvailableEvent;
import cn.ismartv.voice.core.update.AppUpdateUtilsV2;
import cn.ismartv.voice.data.table.CityTable;
import cn.ismartv.voice.ui.fragment.BaseFragment;
import cn.ismartv.voice.ui.fragment.RecognizeErrorFragment;
import cn.ismartv.voice.ui.fragment.RecommendAppFragment;
import cn.ismartv.voice.ui.fragment.RecommendVodFragment;
import cn.ismartv.voice.ui.fragment.SearchLoadingFragment;
import cn.ismartv.voice.ui.fragment.VoiceFragment;
import cn.ismartv.voice.ui.fragment.WeatherFragment;
import cn.ismartv.voice.ui.widget.AppUpdateMessagePopWindow;
import cn.ismartv.voice.ui.widget.MessagePopWindow;

/**
 * Created by huaijie on 1/18/16.
 */
public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivity";

    //    private static final String VOICE_FRAGMENT_TAG = "voice_fragment_tag";
//    private static final String CONTENT_FRAGMENT_TAG = "content_fragment_tag";
//    private static final String APP_SEARCH_FRAGMENT_TAG = "app_search_fragment_tag";
//    private static final String WEATHER_FRAGMENT_TAG = "weather_fragment_tag";
//    private static final String RECOGNIZE_ERROR_FRAGMENT_TAG = "recognize_error_fragment_tag";
    private VoiceFragment voiceFragment;
    private RecommendVodFragment recommendVodFragment;
    private RecommendAppFragment recommendAppFragment;
    private WeatherFragment weatherFragment;
    private RecognizeErrorFragment recognizeErrorFragment;
    private SearchLoadingFragment searchLoadingFragment;

    private boolean voiceBtnIsDown = false;
    private View contentView;
    private MessagePopWindow networkEorrorPopupWindow;
    private AppUpdateMessagePopWindow appUpdateMessagePopWindow;

    private List<BaseFragment> rightFragmentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenManager.getScreenManager().pushActivity(this);
        networkEorrorPopupWindow = new MessagePopWindow(this, "网络异常，请检查网络", null);
        contentView = LayoutInflater.from(this).inflate(R.layout.activity_home, null);
        setContentView(contentView);

        AppUpdateUtilsV2.getInstance(this).checkAppUpdate();

        voiceFragment = new VoiceFragment();
        recommendVodFragment = new RecommendVodFragment();
        recommendAppFragment = new RecommendAppFragment();
        weatherFragment = new WeatherFragment();
        recognizeErrorFragment = new RecognizeErrorFragment();
        searchLoadingFragment = new SearchLoadingFragment();
        rightFragmentList = new ArrayList<>();

        rightFragmentList.add(recommendVodFragment);
        rightFragmentList.add(recommendAppFragment);
        rightFragmentList.add(weatherFragment);
        rightFragmentList.add(recognizeErrorFragment);
        rightFragmentList.add(searchLoadingFragment);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.left_fragment, voiceFragment);
        transaction.add(R.id.right_fragment, recommendVodFragment);
        transaction.add(R.id.right_fragment, recommendAppFragment);
        transaction.add(R.id.right_fragment, weatherFragment);
        transaction.add(R.id.right_fragment, recognizeErrorFragment);
        transaction.add(R.id.right_fragment, searchLoadingFragment);

        transaction.hide(recommendVodFragment);
        transaction.hide(recommendAppFragment);
        transaction.hide(weatherFragment);
        transaction.hide(recognizeErrorFragment);
        transaction.hide(searchLoadingFragment);
        transaction.hide(voiceFragment);

        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        voiceFragment.reset();
        getSupportFragmentManager().beginTransaction().show(voiceFragment).commitAllowingStateLoss();

        recommendVodFragment.reset();
        showRightFragment(recommendVodFragment);

//        contentView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showNetworkErrorPop();
//            }
//        }, 3000);
    }


    private void showRightFragment(BaseFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (BaseFragment baseFragment : rightFragmentList) {
            if (fragment != baseFragment && baseFragment.isVisible())
                transaction.hide(baseFragment);
        }

        if (fragment.getClass() != SearchLoadingFragment.class) {
            transaction.setCustomAnimations(
                    R.anim.push_left_in,
                    R.anim.push_left_out);
        }

        transaction.show(fragment).commitAllowingStateLoss();
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
                voiceFragment.startSpeek();
            }
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (recommendVodFragment.isVisible()) {
                if (recommendVodFragment.isRecommend()) {
                    backToInit();
                } else {
                    System.exit(0);
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


    public void backToInit() {
        voiceFragment.reset();
        getSupportFragmentManager().beginTransaction().show(voiceFragment).commit();
        recommendVodFragment.reset();
        showRightFragment(recommendVodFragment);

//        if (contentFragment != null && contentFragment.isVisible()) {
//            if (contentFragment.isRecommend()) {
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                voiceFragment = new VoiceFragment();
//                contentFragment = new RecommendVodFragment();
//                transaction.replace(R.id.left_fragment, voiceFragment, CONTENT_FRAGMENT_TAG);
//                transaction.setCustomAnimations(
//                        R.anim.push_left_in,
//                        R.anim.push_left_out);
//                transaction.replace(R.id.right_fragment, contentFragment, VOICE_FRAGMENT_TAG);
//                transaction.commit();
//            }
//        } else {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            voiceFragment = new VoiceFragment();
//            contentFragment = new RecommendVodFragment();
//            transaction.replace(R.id.left_fragment, voiceFragment, CONTENT_FRAGMENT_TAG);
//            transaction.setCustomAnimations(
//                    R.anim.push_left_in,
//                    R.anim.push_left_out);
//            transaction.replace(R.id.right_fragment, contentFragment, VOICE_FRAGMENT_TAG);
//            transaction.commit();
//        }
    }


    /**
     * receive app update broadcast, and show update popup window
     */
    public void showAppUpdatePop(Bundle bundle) {
        final String path = bundle.getString("path");
        List<String> msgs = bundle.getStringArrayList("msgs");
        appUpdateMessagePopWindow = new AppUpdateMessagePopWindow(this, msgs, new AppUpdateMessagePopWindow.ConfirmListener() {
            @Override
            public void confirmClick(View view) {
                appUpdateMessagePopWindow.dismiss();
                installApk(HomeActivity.this, path);
            }
        }, new AppUpdateMessagePopWindow.CancelListener() {
            @Override
            public void cancelClick(View view) {
                appUpdateMessagePopWindow.dismiss();
            }
        });

        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!appUpdateMessagePopWindow.isShowing()) {
                    appUpdateMessagePopWindow.setButtonText("立即升级", "下次升级");
                    appUpdateMessagePopWindow.showAtLocation(contentView, Gravity.CENTER);
                }
            }
        }, 1000);
    }


    public void installApk(Context mContext, String path) {
        Uri uri = Uri.parse("file://" + path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }


    public void recommendVideo() {
        recommendVodFragment.changeTitle();
        showRightFragment(recommendAppFragment);


    }

    public void recommendApp() {
        showRightFragment(recommendAppFragment);
    }

    public void showWeatherNoRegion(CityTable cityTable) {
        weatherFragment.setLocation(cityTable);
        showRightFragment(weatherFragment);

    }


    public void showRecognizeError() {
        showRightFragment(recognizeErrorFragment);
    }

    private void showNetworkErrorPop() {
        networkEorrorPopupWindow = new MessagePopWindow(this, "网络异常，请检查网络", null);
        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!networkEorrorPopupWindow.isShowing()) {
                    networkEorrorPopupWindow.showAtLocation(contentView, Gravity.CENTER, new MessagePopWindow.ConfirmListener() {
                                @Override
                                public void confirmClick(View view) {
                                    networkEorrorPopupWindow.dismiss();
                                    ScreenManager.getScreenManager().popAllActivityExceptOne(null);
                                }
                            },
                            null
                    );
                }
            }
        }, 1000);
    }

    public void showSearchLoadingFragment() {
        showRightFragment(searchLoadingFragment);
    }

    @Subscribe
    public void answerAvailable(AnswerAvailableEvent event) {
        switch (event.getEventType()) {
            case NETWORK_ERROR:
                showNetworkErrorPop();
                break;
            case APP_UPDATE:
                showAppUpdatePop((Bundle) event.getMsg());
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        networkEorrorPopupWindow = null;
    }

    @Override
    protected void onDestroy() {
        ScreenManager.getScreenManager().popActivity(this);
        super.onDestroy();
    }

}
