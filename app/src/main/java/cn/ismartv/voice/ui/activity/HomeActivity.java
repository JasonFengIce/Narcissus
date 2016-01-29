package cn.ismartv.voice.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.ismartv.injectdb.library.query.Select;
import cn.ismartv.voice.R;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.core.update.AppUpdateUtilsV2;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.data.http.AppSearchResponseEntity;
import cn.ismartv.voice.data.http.SemanticSearchRequestEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.data.table.AppTable;
import cn.ismartv.voice.ui.fragment.AppSearchFragment;
import cn.ismartv.voice.ui.fragment.ContentFragment;
import cn.ismartv.voice.ui.fragment.IndicatorFragment;
import cn.ismartv.voice.ui.fragment.VoiceFragment;
import cn.ismartv.voice.ui.widget.MessagePopWindow;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 1/18/16.
 */
public class HomeActivity extends BaseActivity {
    private static final String VOICE_FRAGMENT_TAG = "voice_fragment_tag";
    private static final String CONTENT_FRAGMENT_TAG = "content_fragment_tag";
    private static final String INDICATOR_FRAGMENT_TAG = "indicator_fragment_tag";
    private static final String APP_SEARCH_FRAGMENT_TAG = "app_search_fragment_tag";


    private VoiceFragment voiceFragment;
    private ContentFragment contentFragment;
    private IndicatorFragment indicatorFragment;
    private AppSearchFragment appSearchFragment;

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
        AppUpdateUtilsV2.getInstance(this).checkAppUpdate();

        if (savedInstanceState != null) {

        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.left_fragment, voiceFragment, VOICE_FRAGMENT_TAG);
            transaction.add(R.id.right_fragment, contentFragment, CONTENT_FRAGMENT_TAG);
            transaction.add(R.id.left_fragment, indicatorFragment, INDICATOR_FRAGMENT_TAG);
            transaction.add(R.id.right_fragment, appSearchFragment, APP_SEARCH_FRAGMENT_TAG);
            transaction.hide(indicatorFragment);
            transaction.hide(appSearchFragment);
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


    public void handleAppIndicatorClick(String rawText) {
        searchApp(rawText);
    }

    private void searchApp(final String appName) {
        final List<AppTable> appTables = new Select().from(AppTable.class).where("app_name like ?", "%" + appName.replace("\"", "") + "%").execute();
        Retrofit retrofit = HttpManager.getInstance().resetAdapter_QIANGUANGZHAO;
        retrofit.create(HttpAPI.AppSearch.class).doRequest(appName, 1, 30).enqueue(new Callback<AppSearchResponseEntity>() {
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
                    appList.addAll(appSearchResponseEntity.getObjects());

                    appSearchResponseEntity.setObjects(appList);
                    appSearchResponseEntity.setTotal_count(appList.size());
                    refreshAppSearchFragment(appSearchResponseEntity.getObjects(), appName);
                } else {
                    //error
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    public void showIndicatorFragment(SemanticSearchResponseEntity entity, String rawText, long tag) {
        hideFragment(voiceFragment);
        showFragment(indicatorFragment);
        indicatorFragment.clearLayout();
        indicatorFragment.initIndicator(entity, rawText, tag);
    }

    public void showAppIndicatorFragment(List<AppSearchObjectEntity> entity, String data, long tag) {
        hideFragment(voiceFragment);
        showFragment(indicatorFragment);
        indicatorFragment.clearLayout();
        indicatorFragment.initAppIndicator(entity, data, tag);
    }

    private void refreshContentFragment(SemanticSearchResponseEntity entity, String rawText) {
        hideFragment(appSearchFragment);
        showFragment(contentFragment);
        contentFragment.notifyDataChanged(entity, rawText);
    }

    private void refreshAppSearchFragment(final List<AppSearchObjectEntity> list, String rawText) {
        hideFragment(contentFragment);
        showFragment(appSearchFragment);
        appSearchFragment.notifyDataChanged(list, rawText);

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


    private void searchVod(final String contentType, final String rawText) {
        SemanticSearchRequestEntity entity = new SemanticSearchRequestEntity();
        entity.setData(rawText);
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
        hideFragment(indicatorFragment);
        showFragment(voiceFragment);
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
}
