package cn.ismartv.voice.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.event.AnswerAvailableEvent;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.data.table.CityTable;
import cn.ismartv.voice.ui.fragment.AppSearchFragment;
import cn.ismartv.voice.ui.fragment.BaseFragment;
import cn.ismartv.voice.ui.fragment.ContentFragment2;
import cn.ismartv.voice.ui.fragment.IndicatorFragment;
import cn.ismartv.voice.ui.fragment.RecognizeErrorFragment;
import cn.ismartv.voice.ui.fragment.SearchLoadingFragment;
import cn.ismartv.voice.ui.fragment.SearchLoadingWithBGFragment;
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
    private static final String INDICATOR_FRAGMENT_TAG = "indicator_fragment_tag";
    private static final String APP_SEARCH_FRAGMENT_TAG = "app_search_fragment_tag";
    private static final String SEARCH_LOADING_FRAGMENT = "search_loading_fragment_tag";
    private static final String WEATHER_FRAGMENT_TAG = "weather_fragment_tag";
    private static final String RECOGNIZE_ERROR_FRAGMENT_TAG = "recognize_error_fragment_tag";
    private static final String SEARCH_LOADING_BG_FRAGMENT = "search_loading_bg_fragment_tag";


    private VoiceFragment voiceFragment;
    private ContentFragment2 contentFragment;
    private IndicatorFragment indicatorFragment;
    private AppSearchFragment appSearchFragment;
    private SearchLoadingFragment searchLoadingFragment;
    private WeatherFragment weatherFragment;
    private RecognizeErrorFragment recognizeErrorFragment;
    private SearchLoadingWithBGFragment searchLoadingWithBGFragment;

    private List<BaseFragment> fragmentList;
    private List<BaseFragment> leftFragmentList;

    private boolean voiceBtnIsDown = false;
    private View contentView;
    private MessagePopWindow networkEorrorPopupWindow;

    public void hideNavigationBar() {
        int uiFlags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkEorrorPopupWindow = new MessagePopWindow(this, "网络异常，请检查网络", null);
        contentView = LayoutInflater.from(this).inflate(R.layout.activity_home, null);
        setContentView(contentView);
        voiceFragment = new VoiceFragment();
        contentFragment = new ContentFragment2();
        indicatorFragment = new IndicatorFragment();
        appSearchFragment = new AppSearchFragment();
        searchLoadingFragment = new SearchLoadingFragment();
        weatherFragment = new WeatherFragment();
        recognizeErrorFragment = new RecognizeErrorFragment();
        searchLoadingWithBGFragment = new SearchLoadingWithBGFragment();
        fragmentList = new ArrayList<>();
        fragmentList.add(contentFragment);
        fragmentList.add(appSearchFragment);
        fragmentList.add(searchLoadingFragment);
        fragmentList.add(weatherFragment);
        fragmentList.add(recognizeErrorFragment);
        fragmentList.add(searchLoadingWithBGFragment);

        leftFragmentList = new ArrayList<>();
        leftFragmentList.add(voiceFragment);
        leftFragmentList.add(indicatorFragment);

//        AppUpdateUtilsV2.getInstance(this).checkAppUpdate();

        if (savedInstanceState != null) {

        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.left_fragment, voiceFragment, VOICE_FRAGMENT_TAG);
            transaction.add(R.id.left_fragment, indicatorFragment, INDICATOR_FRAGMENT_TAG);
            transaction.add(R.id.right_fragment, appSearchFragment, APP_SEARCH_FRAGMENT_TAG);
            transaction.add(R.id.right_fragment, searchLoadingFragment, SEARCH_LOADING_FRAGMENT);
            transaction.add(R.id.right_fragment, weatherFragment, WEATHER_FRAGMENT_TAG);
            transaction.add(R.id.right_fragment, recognizeErrorFragment, RECOGNIZE_ERROR_FRAGMENT_TAG);
            transaction.add(R.id.right_fragment, searchLoadingWithBGFragment, SEARCH_LOADING_BG_FRAGMENT);
            transaction.hide(searchLoadingFragment);
            transaction.hide(indicatorFragment);
            transaction.hide(appSearchFragment);
            transaction.hide(weatherFragment);
            transaction.hide(recognizeErrorFragment);
            transaction.hide(searchLoadingWithBGFragment);
            transaction.add(R.id.right_fragment, contentFragment, CONTENT_FRAGMENT_TAG);
//            transaction.hide(contentFragment);
            transaction.commit();
        }

        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                showIndicatorFragment(null, null);
            }
        }, 1000);

//        contentView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showAppUpdatePop();
//            }
//        }, 1000);
    }

//
//    public void handleVoice() {
//        searchVod(null);
//    }
//
//    public void handleIndicatorClick(String contentType, String rawText) {
//        searchVod(contentType, rawText);
//    }

//    @Override
//    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_F12) {
//            Log.e(TAG, "long down: " + keyCode);
//            voiceFragment.startSpeek();
//            return true;
//        }
//        return super.onKeyLongPress(keyCode, event);
//    }


    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F12) {

            if (!voiceBtnIsDown) {
                voiceBtnIsDown = true;
                showLeftFragment(voiceFragment);
                voiceFragment.startSpeek();
            }
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (contentFragment.isRecommend()) {
                onBackPressed();
            } else {
                voiceFragment.showTipFragment();
                showLeftFragment(voiceFragment);
                contentFragment.fetchSharpHotWords();
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
//
//    public void handleAppIndicatorClick(String rawText) {
//        searchApp(rawText);
//    }


    public void showIndicatorFragment(SemanticSearchResponseEntity entity, String rawText) {
        String testJson = "{\"facet\":[{\"content_type\":\"variety\",\"count\":0,\"name\":\"综艺\",\"objects\":[],\"total_count\":2},{\"content_type\":\"entertainment\",\"count\":0,\"name\":\"娱乐\",\"objects\":[],\"total_count\":6},{\"content_type\":\"documentary\",\"count\":0,\"name\":\"纪录片\",\"objects\":[],\"total_count\":1}]}";
        String testText = "{\"raw_text\":\"一\"}";
        indicatorFragment.clearLayout();
        indicatorFragment.initVodIndicator(new Gson().fromJson(testJson, SemanticSearchResponseEntity.class), testText, true);
    }


    public void showAppIndicatorFragment(List<AppSearchObjectEntity> entity, String data) {
        indicatorFragment.clearLayout();
        indicatorFragment.initAppIndicator(entity, data, true);
    }

    public void showAppIndicatorFragmentNoClear(List<AppSearchObjectEntity> entity, String data) {
        indicatorFragment.initAppIndicator(entity, data, true);
    }


    public void refreshContentFragment(SemanticSearchResponseEntity entity, String rawText) {
        showMyFragment(contentFragment);
        contentFragment.notifyDataChanged(entity, rawText);
    }

    public void refreshAppSearchFragment(final List<AppSearchObjectEntity> list, String rawText) {
        showMyFragment(appSearchFragment);
        appSearchFragment.notifyDataChanged(list, rawText);

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


    public void backToVoiceFragment() {
        voiceFragment.backToVoice();
        showLeftFragment(voiceFragment);
    }


    private void showAppUpdatePop() {
        final MessagePopWindow popupWindow = new MessagePopWindow(this, "是否提交反馈信息?", null);
        popupWindow.showAtLocation(contentView, Gravity.CENTER, new MessagePopWindow.ConfirmListener() {
                    @Override
                    public void confirmClick(View view) {
                        popupWindow.dismiss();
                    }
                },
                new MessagePopWindow.CancelListener() {
                    @Override
                    public void cancelClick(View view) {
                        popupWindow.dismiss();
                    }
                }
        );
    }


    public void recommendVideo() {
        contentFragment.setSearchTitle();
        contentFragment.fetchSharpHotWords();
        showMyFragment(contentFragment);
    }

    public void recommendApp() {
        showMyFragment(appSearchFragment);
        appSearchFragment.setSearchTitle();
        appSearchFragment.fetchRecommendApp();
    }


    public void showSearchLoading() {

        showMyFragment(searchLoadingFragment);
    }

    public void showSearchLoadingWithBG() {

        showMyFragment(searchLoadingWithBGFragment);
    }

    public void showWeatherNoRegion(CityTable cityTable) {
        weatherFragment.setLocation(cityTable);
        showMyFragment(weatherFragment);
    }


    public void showRecognizeError() {
        showMyFragment(recognizeErrorFragment);
    }

    private void showMyFragment(BaseFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(
//                R.anim.push_left_in,
//                R.anim.push_left_out);

        for (BaseFragment f : fragmentList) {
            if (fragment != f && f.isVisible())
                transaction.hide(f);
        }
        transaction.show(fragment).commit();
    }

    private void showLeftFragment(BaseFragment fragment) {
        for (BaseFragment f : leftFragmentList) {
            if (fragment != f && f.isVisible())
                getSupportFragmentManager().beginTransaction().hide(f).commit();
        }
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
    }

    public void showIndicatorFragment() {
        showLeftFragment(indicatorFragment);
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
