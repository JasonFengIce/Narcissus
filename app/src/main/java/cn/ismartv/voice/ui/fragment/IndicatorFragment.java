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

/**
 * Created by huaijie on 1/18/16.
 */
public class IndicatorFragment extends BaseFragment implements View.OnClickListener {

    private ViewGroup mainView;
    private LinearLayout videoTypeLayout;
    private LinearLayout appTypeLayout;
    private LinearLayout videoContentLayout;
    private LinearLayout appContentLayout;
    private ImageView slideMenu;


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

    public void initIndicator(SemanticSearchResponseEntity entity, String data, long tag) {
        videoTypeLayout.setVisibility(View.VISIBLE);
        videoContentLayout.removeAllViews();
        for (SemanticSearchResponseEntity.Facet facet : entity.getFacet()) {
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_indicator, null);
            TextView title = (TextView) linearLayout.findViewById(R.id.title);
            title.setText(getChineseType(facet.getContent_type()) + "  ( " + facet.getTotal_count() + " )");

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("type", facet.getContent_type());
            hashMap.put("data", data);
            linearLayout.setTag(hashMap);
            linearLayout.setOnClickListener(this);
            videoTypeLayout.addView(linearLayout);
        }
    }


    public void initAppIndicator(List<AppSearchObjectEntity> entitys, String data, long tag) {
        appTypeLayout.setVisibility(View.VISIBLE);
        appContentLayout.removeAllViews();
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_indicator, null);
        TextView title = (TextView) linearLayout.findViewById(R.id.title);
        linearLayout.setId(R.id.all_app_type);
        title.setText("全部应用" + "  ( " + entitys.size() + " )");

        String rawText = new JsonParser().parse(data).getAsJsonObject().get("raw_text").toString();
        linearLayout.setTag(rawText);
        linearLayout.setOnClickListener(this);
        appContentLayout.addView(linearLayout);
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
}
