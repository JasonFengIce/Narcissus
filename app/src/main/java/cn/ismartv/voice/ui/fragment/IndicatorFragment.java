package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.ui.activity.HomeActivity;
import cn.ismartv.voice.util.ViewScaleUtil;

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
        slideMenu.setOnClickListener(this);
    }


    public void initIndicator(SemanticSearchResponseEntity entity, String data) {
        videoTypeLayout.setVisibility(View.VISIBLE);
        videoContentLayout.removeAllViews();
        for (SemanticSearchResponseEntity.Facet facet : entity.getFacet()) {
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_indicator, null);
            linearLayout.setBackgroundResource(R.drawable.seletor_indicator_item);
            TextView title = (TextView) linearLayout.findViewById(R.id.title);
            title.setText(getChineseType(facet.getContent_type()) + "  ( " + facet.getTotal_count() + " )");

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("type", facet.getContent_type());
            hashMap.put("data", data);
            linearLayout.setTag(hashMap);
            linearLayout.setOnClickListener(this);
            linearLayout.setOnFocusChangeListener(this);
            videoContentLayout.addView(linearLayout);


        }

        ((HomeActivity) getActivity()).handleIndicatorClick(entity.getFacet().get(0).getContent_type(), data);
    }


    public void initAppIndicator(List<AppSearchObjectEntity> entitys, String data) {
        appTypeLayout.setVisibility(View.VISIBLE);
        appContentLayout.removeAllViews();
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_indicator, null);
        TextView title = (TextView) linearLayout.findViewById(R.id.title);
        linearLayout.setId(R.id.all_app_type);
        title.setText("全部应用" + "  ( " + entitys.size() + " )");

        String rawText = new JsonParser().parse(data).getAsJsonObject().get("raw_text").toString();
        linearLayout.setTag(rawText);
        linearLayout.setOnClickListener(this);
        linearLayout.setOnFocusChangeListener(this);
        appContentLayout.addView(linearLayout);

        if (videoTypeLayout.getVisibility() == View.GONE) {
            ((HomeActivity) getActivity()).handleAppIndicatorClick(rawText);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.indicator_slide_menu:
                ((HomeActivity) getActivity()).backToVoiceFragment();
                break;
            case R.id.all_app_type:
                String rawText = (String) v.getTag();
                ((HomeActivity) getActivity()).handleAppIndicatorClick(rawText);
                break;
            default:
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
                ((HomeActivity) getActivity()).handleIndicatorClick(type, data);
                break;
        }

    }

    private String getChineseType(String englishType) {
        switch (englishType) {
            case "sport":
                return "体育";
            case "movie":
                return "电影";
            case "comic":
                return "少儿";
            default:
                return englishType;
        }
    }

    public void clearLayout() {
        videoTypeLayout.setVisibility(View.GONE);
        appTypeLayout.setVisibility(View.GONE);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
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

        }
    }
}
