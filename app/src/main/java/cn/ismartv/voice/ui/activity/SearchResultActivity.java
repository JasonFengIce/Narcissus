package cn.ismartv.voice.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.ScreenManager;
import cn.ismartv.voice.core.event.AnswerAvailableEvent;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.ui.fragment.IndicatorFragment;
import cn.ismartv.voice.ui.fragment.ResultAppFragment;
import cn.ismartv.voice.ui.fragment.ResultVodFragment;
import cn.ismartv.voice.ui.fragment.SearchLoadingFragment;
import cn.ismartv.voice.ui.widget.MessagePopWindow;

/**
 * Created by huaijie on 3/3/16.
 */
public class SearchResultActivity extends BaseActivity {

    private static final String CONTENT_FRAGMENT_TAG = "content_fragment_tag";
    private static final String INDICATOR_FRAGMENT_TAG = "indicator_fragment_tag";
    private static final String APP_SEARCH_FRAGMENT_TAG = "app_search_fragment_tag";
    private MessagePopWindow networkEorrorPopupWindow;
    private View contentView;

    private ResultVodFragment contentFragment;
    private IndicatorFragment indicatorFragment;
    private ResultAppFragment appSearchFragment;
    private SearchLoadingFragment searchLoadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenManager.getScreenManager().pushActivity(this);
        networkEorrorPopupWindow = new MessagePopWindow(this, "网络异常，请检查网络", null);
        contentView = LayoutInflater.from(this).inflate(R.layout.activity_home, null);
        setContentView(contentView);

        indicatorFragment = new IndicatorFragment();

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        String data = intent.getStringExtra("data");
        String raw = intent.getStringExtra("raw");
        String appData = intent.getStringExtra("app_data");
        String videoData = intent.getStringExtra("video_data");
        String appRaw = intent.getStringExtra("app_raw");
        String videoRaw = intent.getStringExtra("video_raw");

        indicatorFragment.setType(type);
        indicatorFragment.setData(data);
        indicatorFragment.setRaw(raw);

        indicatorFragment.setAppData(appData);
        indicatorFragment.setVideoData(videoData);
        indicatorFragment.setAppRaw(appRaw);
        indicatorFragment.setVideoRaw(videoRaw);

//
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.left_fragment, indicatorFragment, INDICATOR_FRAGMENT_TAG);
//        transaction.add(R.id.right_fragment, contentFragment, CONTENT_FRAGMENT_TAG);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void firstTimeVod(SemanticSearchResponseEntity entity, String raw) {
        contentFragment = new ResultVodFragment();
        contentFragment.setRaw(raw);
        contentFragment.setObjectEntities(entity.getFacet().get(0).getObjects());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.right_fragment, contentFragment, CONTENT_FRAGMENT_TAG);
        transaction.commit();
    }

    public void firstTimeApp(List<AppSearchObjectEntity> list, String appname) {
        appSearchFragment = new ResultAppFragment();

        appSearchFragment.setRaw(appname);
        appSearchFragment.setAppSearchObjectEntities(list);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.right_fragment, appSearchFragment, APP_SEARCH_FRAGMENT_TAG);
        transaction.commit();

    }

    public void refreshContent(SemanticSearchResponseEntity entity, String raw) {
        contentFragment = new ResultVodFragment();
        contentFragment.setRaw(raw);
        contentFragment.setObjectEntities(entity.getFacet().get(0).getObjects());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.right_fragment, contentFragment, CONTENT_FRAGMENT_TAG);
        transaction.commit();
    }

    public void refreshApp(List<AppSearchObjectEntity> list, String appname) {
        appSearchFragment = new ResultAppFragment();

        appSearchFragment.setRaw(appname);
        appSearchFragment.setAppSearchObjectEntities(list);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.right_fragment, appSearchFragment, APP_SEARCH_FRAGMENT_TAG);
        transaction.commit();
    }

    public void showSearchLoadingFragment() {
        searchLoadingFragment = new SearchLoadingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.right_fragment, searchLoadingFragment).commit();
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


    @Subscribe
    public void answerAvailable(AnswerAvailableEvent event) {
        switch (event.getEventType()) {
            case NETWORK_ERROR:
                showNetworkErrorPop();
                break;
        }
    }


    @Override
    protected void onStop() {
        networkEorrorPopupWindow = null;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ScreenManager.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}
