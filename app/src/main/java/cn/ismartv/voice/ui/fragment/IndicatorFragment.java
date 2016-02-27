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
import cn.ismartv.voice.ui.activity.HomeActivity;
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
        slideMenu.bringToFront();
        slideMenu.setOnClickListener(this);
    }


    public void initVodIndicator(SemanticSearchResponseEntity entity, String data, boolean isFirstTime) {
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
                    ((HomeActivity) getActivity()).showSearchLoading(true);
                    if (selectedView != null) {
                        TextView textView = (TextView) selectedView.findViewById(R.id.title);
                        textView.setTextColor(getResources().getColor(R.color._a6a6a6));
                    } else {
                        TextView textView = (TextView) v.findViewById(R.id.title);
                        textView.setTextColor(getResources().getColor(R.color._ffffff));
                    }
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

            videoContentLayout.addView(linearLayout);
            i = i + 1;
        }

        if (videoContentLayout.getChildCount() != 0) {
            selectedView = videoContentLayout.getChildAt(0);
            TextView textView = (TextView) selectedView.findViewById(R.id.title);
            textView.setTextColor(getResources().getColor(R.color._ffffff));
        }


        searchVod(entity.getFacet().get(0).getContent_type(), data, true);
    }


    public void initAppIndicator(List<AppSearchObjectEntity> entitys, String data, boolean isFirstTime) {
        appTypeLayout.setVisibility(View.VISIBLE);
        appContentLayout.removeAllViews();
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_indicator, null);
        TextView title = (TextView) linearLayout.findViewById(R.id.title);
        linearLayout.setId(R.id.all_app_type);
        title.setText("全部应用" + "  ( " + entitys.size() + " )");

        String rawText = new JsonParser().parse(data).getAsJsonObject().get("raw_text").toString();
        linearLayout.setTag(rawText);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).showSearchLoading(true);
                String rawText = (String) v.getTag();
                searchApp(rawText, false);
            }
        });
        linearLayout.setOnFocusChangeListener(this);
        appContentLayout.addView(linearLayout);

        if (videoTypeLayout.getVisibility() == View.GONE) {
            if (appContentLayout.getChildCount() != 0) {
                selectedView = appContentLayout.getChildAt(0);
                TextView textView = (TextView) selectedView.findViewById(R.id.title);
                textView.setTextColor(getResources().getColor(R.color._ffffff));
            }
            searchApp(rawText, true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.indicator_slide_menu:
                ((HomeActivity) getActivity()).backToVoiceFragment();
                break;
            default:
                break;
        }

    }


    public void clearLayout() {
        videoTypeLayout.setVisibility(View.GONE);
        appTypeLayout.setVisibility(View.GONE);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            default:
                TextView textView = (TextView) v.findViewById(R.id.title);
                if (hasFocus) {
                    if (selectedView == v) {
                        textView.setTextColor(getResources().getColor(R.color._ffffff));
                        ViewScaleUtil.scaleToLarge(v, 1.3f);
                    } else {
                        textView.setTextColor(getResources().getColor(R.color._ff9c3c));
                        ViewScaleUtil.scaleToLarge(v, 1.3f);
                    }

                } else {
                    if (selectedView == v) {
                        textView.setTextColor(getResources().getColor(R.color._ffffff));
                        ViewScaleUtil.scaleToNormal(v, 1.3f);
                    } else {
                        textView.setTextColor(getResources().getColor(R.color._a6a6a6));
                        ViewScaleUtil.scaleToNormal(v, 1.3f);
                    }
                    slideMenu.setNextFocusRightId(v.getId());
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
                        ((HomeActivity) getActivity()).showIndicatorFragment();
                    }
                    ((HomeActivity) getActivity()).refreshContentFragment(response.body(), rawText);
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
                        ((HomeActivity) getActivity()).showIndicatorFragment();
                    }
                    ((HomeActivity) getActivity()).refreshAppSearchFragment(appList, appName);
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
