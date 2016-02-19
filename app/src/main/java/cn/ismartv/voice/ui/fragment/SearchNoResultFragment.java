package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.ismartv.voice.R;

/**
 * Created by huaijie on 1/29/16.
 */
public class SearchNoResultFragment extends BaseFragment {

    private TextView title;
    private TextView tip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_no_result, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = (TextView) view.findViewById(R.id.raw_text);
        tip = (TextView) view.findViewById(R.id.tip);
    }

    public void recognizeNoResult(String rawText) {
        title.setVisibility(View.VISIBLE);
        title.setText(rawText.replace("\"", ""));
        tip.setText(R.string.search_no_result);
    }

    public void recognizeError() {
        title.setVisibility(View.GONE);
        tip.setText(R.string.recognize_error_tip);

    }
}
