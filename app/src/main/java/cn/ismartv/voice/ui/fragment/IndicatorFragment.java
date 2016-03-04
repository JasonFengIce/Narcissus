package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.ismartv.injectdb.library.query.Select;
import cn.ismartv.voice.AppConstant;
import cn.ismartv.voice.R;
import cn.ismartv.voice.core.event.AnswerAvailableEvent;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.data.http.AppSearchRequestParams;
import cn.ismartv.voice.data.http.AppSearchResponseEntity;
import cn.ismartv.voice.data.http.SemanticSearchRequestEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.data.table.AppTable;
import cn.ismartv.voice.ui.activity.SearchResultActivity;
import cn.ismartv.voice.util.ViewScaleUtil;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 1/18/16.
 */
public class IndicatorFragment extends BaseFragment implements View.OnClickListener, View.OnFocusChangeListener {

    private ViewGroup mainView;
    private LinearLayout videoTypeLayout;
    private LinearLayout appTypeLayout;
    private LinearLayout videoContentLayout;
    private LinearLayout appContentLayout;
    private ImageView slideMenu;

    private View selectedView;
    private ImageView transferLine;
    private View lostFocusView;
    private String type;
    private String data;
    private String raw;

    public void setType(String type) {
        this.type = type;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    private String appData;
    private String videoData;
    private String appRaw;
    private String videoRaw;

    public void setAppData(String appData) {
        this.appData = appData;
    }

    public void setVideoData(String videoData) {
        this.videoData = videoData;
    }

    public void setAppRaw(String appRaw) {
        this.appRaw = appRaw;
    }

    public void setVideoRaw(String videoRaw) {
        this.videoRaw = videoRaw;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = (ViewGroup) inflater.inflate(R.layout.fragment_indicator, null);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        videoTypeLayout = (LinearLayout) view.findViewById(R.id.video_type_layout);
        appTypeLayout = (LinearLayout) view.findViewById(R.id.app_type_layout);
        videoContentLayout = (LinearLayout) view.findViewById(R.id.video_content);
        appContentLayout = (LinearLayout) view.findViewById(R.id.app_content);
        slideMenu = (ImageView) view.findViewById(R.id.indicator_slide_menu);
        transferLine = (ImageView) view.findViewById(R.id.transfer_line);
        transferLine.setOnFocusChangeListener(this);
        slideMenu.bringToFront();
        slideMenu.setOnClickListener(this);

        switch (type) {
            case "video":
                SemanticSearchResponseEntity entity = new Gson().fromJson(data, SemanticSearchResponseEntity.class);
                initVodIndicator(entity, raw);
                break;
            case "app":
                AppSearchResponseEntity appSearchResponseEntity = new Gson().fromJson(data, AppSearchResponseEntity.class);
                initAppIndicator(appSearchResponseEntity.getFacet()[0].getObjects(), raw);
                break;
            case "multi":
                SemanticSearchResponseEntity videoEntity = new Gson().fromJson(videoData, SemanticSearchResponseEntity.class);
                initVodIndicator(videoEntity, videoRaw);

                List<AppSearchObjectEntity> appEntity = new Gson().fromJson(appData, List.class);
                initAppIndicator(appEntity, appRaw);
                break;
        }
    }


    private void initVodIndicator(SemanticSearchResponseEntity entity, String data) {
        lostFocusView = null;
        selectedView = null;
        videoTypeLayout.setVisibility(View.VISIBLE);
        videoContentLayout.removeAllViews();
        int i = 0;
        for (SemanticSearchResponseEntity.Facet facet : entity.getFacet()) {
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_indicator, null);
            linearLayout.setBackgroundResource(R.drawable.seletor_indicator_item);
            TextView title = (TextView) linearLayout.findViewById(R.id.title);
            title.setText(facet.getName() + "  ( " + facet.getTotal_count() + " )");

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("type", facet.getContent_type());
            hashMap.put("data", data);
            linearLayout.setTag(hashMap);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SearchResultActivity) getActivity()).showSearchLoadingFragment();
                    if (selectedView != null) {
                        TextView textView = (TextView) selectedView.findViewById(R.id.title);
                        textView.setTextColor(getResources().getColor(R.color._a6a6a6));
                    }
                    TextView textView = (TextView) v.findViewById(R.id.title);
                    textView.setTextColor(getResources().getColor(R.color._ffffff));
                    selectedView = v;
                    HashMap<String, String> tag = (HashMap<String, String>) v.getTag();
                    String type = tag.get("type");
                    String data = tag.get("data");
                    searchVod(type, data, false);
                }
            });
            linearLayout.setId(R.id.indicator_list_item + i);
            linearLayout.setOnFocusChangeListener(this);
            linearLayout.setNextFocusLeftId(R.id.indicator_slide_menu);

            if (i == 0) {
                selectedView = linearLayout;
                lostFocusView = linearLayout;
                TextView textView = (TextView) linearLayout.findViewById(R.id.title);
                textView.setTextColor(getResources().getColor(R.color._ffffff));
            }

            videoContentLayout.addView(linearLayout);
            i = i + 1;
        }

        searchVod(entity.getFacet().get(0).getContent_type(), data, true);
    }


    private void initAppIndicator(List<AppSearchObjectEntity> entitys, String raw) {
        appTypeLayout.setVisibility(View.VISIBLE);
        appContentLayout.removeAllViews();
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_indicator, null);
        linearLayout.setBackgroundResource(R.drawable.seletor_indicator_item);
        TextView title = (TextView) linearLayout.findViewById(R.id.title);
        linearLayout.setId(R.id.all_app_type);
        title.setText("全部应用" + "  ( " + entitys.size() + " )");

        String rawText = new JsonParser().parse(raw).getAsJsonObject().get("raw_text").toString();
        linearLayout.setTag(rawText);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchResultActivity) getActivity()).showSearchLoadingFragment();
                if (selectedView != null) {
                    TextView textView = (TextView) selectedView.findViewById(R.id.title);
                    textView.setTextColor(getResources().getColor(R.color._a6a6a6));
                }
                TextView textView = (TextView) v.findViewById(R.id.title);
                textView.setTextColor(getResources().getColor(R.color._ffffff));
                selectedView = v;
                String rawText = (String) v.getTag();
                searchApp(rawText, false);
            }
        });
        linearLayout.setNextFocusLeftId(R.id.indicator_slide_menu);
        linearLayout.setOnFocusChangeListener(this);
        appContentLayout.addView(linearLayout);

        if (videoTypeLayout.getVisibility() == View.GONE) {
            lostFocusView = null;
            selectedView = null;
            selectedView = linearLayout;
            lostFocusView = linearLayout;
            TextView textView = (TextView) linearLayout.findViewById(R.id.title);
            textView.setTextColor(getResources().getColor(R.color._ffffff));
            searchApp(rawText, true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.indicator_slide_menu:
//                ((HomeActivity) getActivity()).backToVoiceFragment();
                break;
            default:
                break;
        }

    }


//    public void clearLayout() {
//        videoTypeLayout.setVisibility(View.GONE);
//        appTypeLayout.setVisibility(View.GONE);
//    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.transfer_line:
                if (lostFocusView != null) {
                    lostFocusView.requestFocus();
                    lostFocusView.requestFocusFromTouch();
                }
                break;
            default:
                TextView textView = (TextView) v.findViewById(R.id.title);
                if (hasFocus) {
                    transferLine.setFocusable(false);
                    transferLine.setFocusableInTouchMode(false);
                    if (selectedView == v) {
                        textView.setTextColor(getResources().getColor(R.color._ffffff));
                        ViewScaleUtil.scaleLarge_1_3_(v);
                    } else {
                        textView.setTextColor(getResources().getColor(R.color._ff9c3c));
                        ViewScaleUtil.scaleLarge_1_3_(v);
                    }

                } else {
                    transferLine.setFocusable(true);
                    transferLine.setFocusableInTouchMode(true);
                    if (selectedView == v) {
                        textView.setTextColor(getResources().getColor(R.color._ffffff));
                        ViewScaleUtil.scaleNormal_1_3(v);
                    } else {
                        textView.setTextColor(getResources().getColor(R.color._a6a6a6));
                        ViewScaleUtil.scaleNormal_1_3(v);
                    }
                    slideMenu.setNextFocusRightId(v.getId());
                    lostFocusView = v;

                }
                break;
        }
    }

    private void searchVod(final String contentType, final String rawText, final boolean isFirstTime) {
        SemanticSearchRequestEntity entity = new SemanticSearchRequestEntity();
        entity.setSemantic(new JsonParser().parse(rawText).getAsJsonObject());
        if (!TextUtils.isEmpty(contentType)) {
            entity.setContent_type(contentType);
        }
        entity.setPage_on(1);
        entity.setPage_count(300);

        Retrofit retrofit = HttpManager.getInstance().resetAdapter_QIANGUANGZHAO;
        retrofit.create(HttpAPI.SemanticSearch.class).doRequest(entity).enqueue(new Callback<SemanticSearchResponseEntity>() {
            @Override
            public void onResponse(Response<SemanticSearchResponseEntity> response) {
                if (response.errorBody() == null) {
                    if (isFirstTime) {
                        ((SearchResultActivity) getActivity()).firstTimeVod(response.body(), rawText);
                    } else {
                        ((SearchResultActivity) getActivity()).refreshContent(response.body(), rawText);
                    }
                } else {
                    //error
                    EventBus.getDefault().post(new AnswerAvailableEvent());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                EventBus.getDefault().post(new AnswerAvailableEvent());
            }
        });
    }

    private void searchApp(final String appName, final boolean isFirstTime) {
        final List<AppTable> appTables = new Select().from(AppTable.class).where("app_name like ?", "%" + appName.replace("\"", "") + "%").execute();
        Retrofit retrofit = HttpManager.getInstance().resetAdapter_QIANGUANGZHAO;
        AppSearchRequestParams params = new AppSearchRequestParams();
        params.setKeyword(appName);
        params.setPage_count(AppConstant.DEFAULT_PAGE_COUNT);
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
                            List<AppTable> tables = new Select().from(AppTable.class).where("app_package = ?", entity.getCaption()).execute();
                            if (appTables.size() == 0) {
                                appList.add(entity);
                            } else {
                                for (AppTable table : tables) {
                                    AppSearchObjectEntity appSearchObjectEntity = new AppSearchObjectEntity();
                                    appSearchObjectEntity.setTitle(table.app_name);
                                    appSearchObjectEntity.setCaption(table.app_package);
                                    appSearchObjectEntity.setIsLocal(true);
                                    appList.add(appSearchObjectEntity);
                                }
                            }
                        }
                        appSearchResponseEntity.getFacet()[0].setObjects(appList);
                        appSearchResponseEntity.getFacet()[0].setTotal_count(appList.size());
                    }
                    if (isFirstTime) {
                        ((SearchResultActivity) getActivity()).firstTimeApp(appList, appName);
                    } else {
                        ((SearchResultActivity) getActivity()).refreshApp(appList, appName);
                    }
                } else {
                    //error
                    EventBus.getDefault().post(new AnswerAvailableEvent());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                EventBus.getDefault().post(new AnswerAvailableEvent());
            }
        });
    }
}
