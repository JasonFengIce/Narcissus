package cn.ismartv.voice.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import cn.ismartv.injectdb.library.query.Select;
import cn.ismartv.voice.R;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.core.update.AppUpdateUtilsV2;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.data.http.AppSearchRequestParams;
import cn.ismartv.voice.data.http.AppSearchResponseEntity;
import cn.ismartv.voice.data.http.SemanticSearchRequestEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.data.table.AppTable;
import cn.ismartv.voice.data.table.CityTable;
import cn.ismartv.voice.ui.fragment.AppSearchFragment;
import cn.ismartv.voice.ui.fragment.BaseFragment;
import cn.ismartv.voice.ui.fragment.ContentFragment;
import cn.ismartv.voice.ui.fragment.IndicatorFragment;
import cn.ismartv.voice.ui.fragment.RecognizeErrorFragment;
import cn.ismartv.voice.ui.fragment.SearchLoadingFragment;
import cn.ismartv.voice.ui.fragment.VoiceFragment;
import cn.ismartv.voice.ui.fragment.WeatherFragment;
import cn.ismartv.voice.ui.widget.MessagePopWindow;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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


    private VoiceFragment voiceFragment;
    private ContentFragment contentFragment;
    private IndicatorFragment indicatorFragment;
    private AppSearchFragment appSearchFragment;
    private SearchLoadingFragment searchLoadingFragment;
    private WeatherFragment weatherFragment;
    private RecognizeErrorFragment recognizeErrorFragment;

    private List<BaseFragment> fragmentList;
    private List<BaseFragment> leftFragmentList;

    private boolean voiceBtnIsDown = false;
    private View contentView;

    public void hideNavigationBar() {
        int uiFlags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = LayoutInflater.from(this).inflate(R.layout.activity_home, null);
        setContentView(contentView);
        voiceFragment = new VoiceFragment();
        contentFragment = new ContentFragment();
        indicatorFragment = new IndicatorFragment();
        appSearchFragment = new AppSearchFragment();
        searchLoadingFragment = new SearchLoadingFragment();
        weatherFragment = new WeatherFragment();
        recognizeErrorFragment = new RecognizeErrorFragment();
        fragmentList = new ArrayList<>();
        fragmentList.add(contentFragment);
        fragmentList.add(appSearchFragment);
        fragmentList.add(searchLoadingFragment);
        fragmentList.add(weatherFragment);
        fragmentList.add(recognizeErrorFragment);

        leftFragmentList = new ArrayList<>();
        leftFragmentList.add(voiceFragment);
        leftFragmentList.add(indicatorFragment);

        AppUpdateUtilsV2.getInstance(this).checkAppUpdate();

        if (savedInstanceState != null) {

        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.left_fragment, voiceFragment, VOICE_FRAGMENT_TAG);
            transaction.add(R.id.right_fragment, contentFragment, CONTENT_FRAGMENT_TAG);
            transaction.add(R.id.left_fragment, indicatorFragment, INDICATOR_FRAGMENT_TAG);
            transaction.add(R.id.right_fragment, appSearchFragment, APP_SEARCH_FRAGMENT_TAG);
            transaction.add(R.id.right_fragment, searchLoadingFragment, SEARCH_LOADING_FRAGMENT);
            transaction.add(R.id.right_fragment, weatherFragment, WEATHER_FRAGMENT_TAG);
            transaction.add(R.id.right_fragment, recognizeErrorFragment, RECOGNIZE_ERROR_FRAGMENT_TAG);
            transaction.hide(searchLoadingFragment);
            transaction.hide(indicatorFragment);
            transaction.hide(appSearchFragment);
            transaction.hide(weatherFragment);
            transaction.hide(recognizeErrorFragment);
//            transaction.hide(contentFragment);
            transaction.commit();
        }

//        contentView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showAppUpdatePop();
//            }
//        }, 3000);
    }

//
//    public void handleVoice() {
//        searchVod(null);
//    }

    public void handleIndicatorClick(String contentType, String rawText) {
        searchVod(contentType, rawText);
    }

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
            onBackPressed();
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

    public void handleAppIndicatorClick(String rawText) {
        searchApp(rawText);
    }

    private void searchApp(final String appName) {
        final List<AppTable> appTables = new Select().from(AppTable.class).where("app_name like ?", "%" + appName.replace("\"", "") + "%").execute();
        Retrofit retrofit = HttpManager.getInstance().resetAdapter_QIANGUANGZHAO;
        AppSearchRequestParams params = new AppSearchRequestParams();
        params.setKeyword(appName);
        params.setPage_count(30);
        params.setPage_no(1);
        params.setContent_type("app");
        retrofit.create(HttpAPI.AppSearch.class).doRequest(params).enqueue(new Callback<AppSearchResponseEntity>() {
            @Override
            public void onResponse(Response<AppSearchResponseEntity> response) {
                if (response.errorBody() == null) {

                    AppSearchResponseEntity appSearchResponseEntity = response.body();
                    List<AppSearchObjectEntity> appList = new ArrayList<>();
                    for (AppTable appTable : appTables) {

                        AppSearchObjectEntity appSearchObjectEntity = new AppSearchObjectEntity();
                        appSearchObjectEntity.setTitle(appTable.app_name);
                        appSearchObjectEntity.setCaption(appTable.app_package);
                        appSearchObjectEntity.setIsLocal(true);
                        appList.add(appSearchObjectEntity);
                    }
                    AppSearchResponseEntity.Facet facet[] = appSearchResponseEntity.getFacet();
                    if (facet != null) {
                        List<AppSearchObjectEntity> serverAppList = facet[0].getObjects();
                        for (AppSearchObjectEntity entity : serverAppList) {
                            List<AppTable> appTables = new Select().from(AppTable.class).where("app_package = ?", entity.getCaption()).execute();
                            if (appTables.size() == 0) {
                                appList.add(entity);
                            }
                        }
                        appSearchResponseEntity.getFacet()[0].setObjects(appList);
                        appSearchResponseEntity.getFacet()[0].setTotal_count(appList.size());
                    }

                    refreshAppSearchFragment(appList, appName);
                } else {
                    //error
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    public void showIndicatorFragment(SemanticSearchResponseEntity entity, String rawText) {
        showLeftFragment(indicatorFragment);
        indicatorFragment.clearLayout();
        indicatorFragment.initIndicator(entity, rawText);
    }


    public void showAppIndicatorFragment(List<AppSearchObjectEntity> entity, String data) {
        showLeftFragment(indicatorFragment);
        indicatorFragment.clearLayout();
        indicatorFragment.initAppIndicator(entity, data);
    }

    public void showAppIndicatorFragmentNoClear(List<AppSearchObjectEntity> entity, String data) {
        showLeftFragment(indicatorFragment);
        indicatorFragment.initAppIndicator(entity, data);
    }


    private void refreshContentFragment(SemanticSearchResponseEntity entity, String rawText) {
        showMyFragment(contentFragment);
        contentFragment.notifyDataChanged(entity, rawText);
    }

    private void refreshAppSearchFragment(final List<AppSearchObjectEntity> list, String rawText) {
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


    private void searchVod(final String contentType, final String rawText) {
        SemanticSearchRequestEntity entity = new SemanticSearchRequestEntity();
        entity.setSemantic(new JsonParser().parse(rawText).getAsJsonObject());
        if (!TextUtils.isEmpty(contentType)) {
            entity.setContent_type(contentType);
        }
        entity.setPage_on(1);
        entity.setPage_count(30);

        Retrofit retrofit = HttpManager.getInstance().resetAdapter_QIANGUANGZHAO;
        retrofit.create(HttpAPI.SemanticSearch.class).doRequest(entity).enqueue(new Callback<SemanticSearchResponseEntity>() {
            @Override
            public void onResponse(Response<SemanticSearchResponseEntity> response) {
                if (response.errorBody() == null) {
                    refreshContentFragment(response.body(), rawText);
                } else {
                    //error
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
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
        showMyFragment(contentFragment);
        contentFragment.setSearchTitle();
        contentFragment.fetchSharpHotWords();
    }

    public void recommendApp() {
        showMyFragment(appSearchFragment);
        appSearchFragment.setSearchTitle();
        appSearchFragment.fetchRecommendApp();
    }

    public void showSearchLoading() {
        showMyFragment(searchLoadingFragment);
    }

    public void showWeatherNoRegion(CityTable cityTable) {
        weatherFragment.setLocation(cityTable);
        showMyFragment(weatherFragment);
    }


    public void showRecognizeError() {
        showMyFragment(recognizeErrorFragment);
    }

    private void showMyFragment(BaseFragment fragment) {
        for (BaseFragment f : fragmentList) {
            if (fragment != f && f.isVisible())
                getSupportFragmentManager().beginTransaction().hide(f).commit();
        }
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
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
}
