package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 1/29/16.
 */
public class SearchTipFragment extends BaseFragment {
    private LinearLayout tipListView;
    private Call wordsCall;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_tip, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tipListView = (LinearLayout) view.findViewById(R.id.tip_list);
        fetchWords();
    }


    public void fetchWords() {
        Retrofit retrofit = HttpManager.getInstance().resetAdapter_WUGUOJUN;
        wordsCall = retrofit.create(HttpAPI.Words.class).doRequest(5);
        wordsCall.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Response<List<String>> response) {
                if (response.errorBody() == null) {
                    List<String> tipList = response.body();
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                    int marginTop = (int) (getResources().getDimension(R.dimen.voice_tip_item_margin_top) / getDensityRate());
                    layoutParams.topMargin = marginTop;
                    for (int i = 0; i < tipList.size() && i < 5; i++) {
                        TextView textView = new TextView(getContext());
                        textView.setTextSize(getResources().getDimension(R.dimen.textSize_36sp) / getDensityRate());
                        textView.setText(tipList.get(i));
                        tipListView.addView(textView, layoutParams);
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }

    @Override
    public void onStop() {
        if (wordsCall != null && !wordsCall.isCanceled()) {
            wordsCall.cancel();
        }
        super.onStop();
    }
}
