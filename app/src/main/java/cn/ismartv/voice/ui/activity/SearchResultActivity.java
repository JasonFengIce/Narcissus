package cn.ismartv.voice.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.event.AnswerAvailableEvent;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.ui.fragment.AppSearchFragment;
import cn.ismartv.voice.ui.fragment.ContentFragment;
import cn.ismartv.voice.ui.fragment.IndicatorFragment;
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

    private ContentFragment contentFragment;
    private IndicatorFragment indicatorFragment;
    private AppSearchFragment appSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void firstTimeVod(SemanticSearchResponseEntity entity, String raw) {
        contentFragment = new ContentFragment();
        contentFragment.setRaw(raw);
        contentFragment.setObjectEntities(entity.getFacet().get(0).getObjects());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.right_fragment, contentFragment, CONTENT_FRAGMENT_TAG);
        transaction.commit();
    }

    public void firstTimeApp(List<AppSearchObjectEntity> list, String appname) {
        appSearchFragment = new AppSearchFragment();

        appSearchFragment.setRaw(appname);
        appSearchFragment.setAppSearchObjectEntities(list);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.right_fragment, appSearchFragment, APP_SEARCH_FRAGMENT_TAG);
        transaction.commit();

    }

    public void refreshContent(SemanticSearchResponseEntity entity, String raw) {
        contentFragment = new ContentFragment();
        contentFragment.setRaw(raw);
        contentFragment.setObjectEntities(entity.getFacet().get(0).getObjects());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.right_fragment, contentFragment, CONTENT_FRAGMENT_TAG);
        transaction.commit();
    }

    public void refreshApp(List<AppSearchObjectEntity> list, String appname) {
        appSearchFragment = new AppSearchFragment();

        appSearchFragment.setRaw(appname);
        appSearchFragment.setAppSearchObjectEntities(list);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.right_fragment, appSearchFragment, APP_SEARCH_FRAGMENT_TAG);
        transaction.commit();
    }

    @Subscribe
    public void answerAvailable(AnswerAvailableEvent event) {
        switch (event.getEventCode()) {

        }
    }


//    public void showIndicatorFragment(SemanticSearchResponseEntity entity, String rawText) {
////        String testJson = "{\"facet\":[{\"content_type\":\"variety\",\"count\":0,\"name\":\"综艺\",\"objects\":[],\"total_count\":2},{\"content_type\":\"entertainment\",\"count\":0,\"name\":\"娱乐\",\"objects\":[],\"total_count\":6},{\"content_type\":\"documentary\",\"count\":0,\"name\":\"纪录片\",\"objects\":[],\"total_count\":1}]}";
////        String testText = "{\"raw_text\":\"一\"}";
////        indicatorFragment.initVodIndicator(new Gson().fromJson(testJson, SemanticSearchResponseEntity.class), testText, true);
//        indicatorFragment.initVodIndicator(entity, rawText, true);
//    }
//
//
//    public void refreshContentFragment(SemanticSearchResponseEntity entity, String rawText) {
//        contentFragment.notifyDataChanged(entity, rawText);
//    }
//
//
//    public void showAppIndicatorFragment(List<AppSearchObjectEntity> entity, String data) {
//        indicatorFragment.clearLayout();
//        indicatorFragment.initAppIndicator(entity, data, true);
//    }
//
//    public void showAppIndicatorFragmentNoClear(List<AppSearchObjectEntity> entity, String data) {
//        indicatorFragment.initAppIndicator(entity, data, true);
//    }
}
