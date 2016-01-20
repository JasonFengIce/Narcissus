package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.ismartv.voice.R;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.ui.activity.HomeActivity;

/**
 * Created by huaijie on 1/18/16.
 */
public class IndicatorFragment extends BaseFragment implements View.OnClickListener {

    private ViewGroup mainView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = (ViewGroup) inflater.inflate(R.layout.fragment_indicator, null);
        return mainView;
    }


    public void initIndicator(SemanticSearchResponseEntity entity) {
        for (SemanticSearchResponseEntity.Facet facet : entity.getFacet()) {
            TextView textView = new TextView(getContext());
            textView.setText(facet.getContent_type());
            textView.setClickable(true);
            textView.setPadding(0, 100, 0, 0);
            textView.setOnClickListener(this);
            textView.setTag(facet.getContent_type());
            mainView.addView(textView);
        }
    }

    @Override
    public void onClick(View v) {
        ((HomeActivity) getActivity()).handleIndicatorClick(v.getTag().toString());
    }
}
