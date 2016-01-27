package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import cn.ismartv.voice.R;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.ui.activity.HomeActivity;

/**
 * Created by huaijie on 1/18/16.
 */
public class IndicatorFragment extends BaseFragment implements View.OnClickListener {

    private ViewGroup mainView;
    private LinearLayout videoTypeLayout;
    private LinearLayout appTypeLayout;
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
        slideMenu = (ImageView) view.findViewById(R.id.indicator_slide_menu);
        slideMenu.setOnClickListener(this);
    }

    public void initIndicator(SemanticSearchResponseEntity entity, String rawText) {
        for (SemanticSearchResponseEntity.Facet facet : entity.getFacet()) {
//            TextView textView = new TextView(getContext());
//            textView.setText(facet.getContent_type());
//            textView.setClickable(true);
//            textView.setPadding(0, 100, 0, 0);
//            textView.setOnClickListener(this);
//            textView.setTag(facet.getContent_type());

            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_indicator, null);
            TextView title = (TextView) linearLayout.findViewById(R.id.title);
            title.setText(getChineseType(facet.getContent_type()) + "  ( " + facet.getTotal_count() + " )");

            HashMap<String, String> tag = new HashMap<>();
            tag.put("type", facet.getContent_type());
            tag.put("rawText", rawText);
            linearLayout.setTag(tag);
            linearLayout.setOnClickListener(this);
            videoTypeLayout.addView(linearLayout);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.indicator_slide_menu:
                ((HomeActivity) getActivity()).backToVoiceFragment();
                break;
            default:
                HashMap<String, String> tag = (HashMap<String, String>) v.getTag();
                String type = tag.get("type");
                String rawText = tag.get("rawText");
                ((HomeActivity) getActivity()).handleIndicatorClick(type, rawText);
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
