package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ScaleXSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.filter.FilterUtil;
import cn.ismartv.voice.core.filter.WordFilterResult;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.http.SemantichObjectEntity;
import cn.ismartv.voice.data.http.SharpHotWordsEntity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 2/18/16.
 */
public class RecognizeErrorFragment extends BaseFragment {

    private LinearLayout errorTipListLayout;
    private Call wordsCall;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recognize_error, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        errorTipListLayout = (LinearLayout) view.findViewById(R.id.tip_list_layout);

        fetchWords();
    }

    @Override
    public void onStop() {
        if (wordsCall != null && !wordsCall.isCanceled()) {
            wordsCall.cancel();
        }
        super.onStop();
    }

    public void fetchWords() {
        Retrofit retrofit = HttpManager.getInstance().resetAdapter_WUGUOJUN;
        wordsCall = retrofit.create(HttpAPI.SharpHotWords.class).doRequest(7);
        wordsCall.enqueue(new Callback<SharpHotWordsEntity>() {
            @Override
            public void onResponse(Response<SharpHotWordsEntity> response) {
                if (response.errorBody() == null) {
                    SharpHotWordsEntity tipList = response.body();
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                    int marginTop = (int) (getResources().getDimension(R.dimen.recognize_error_tip_item_margin_top) / getDensityRate());
                    layoutParams.topMargin = marginTop;
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color._ff9c3c));
                    ScaleXSpan scaleXSpan = new ScaleXSpan(1.2f);

                    for (int i = 0; i < tipList.getObjects().size() && i < 7; i++) {
                        TextView textView = new TextView(getContext());
                        textView.setTextSize(getResources().getDimension(R.dimen.textSize_36sp) / getDensityRate());
                        SemantichObjectEntity entity = tipList.getObjects().get(i);
                        String text = entity.getTitle();

                        List<WordFilterResult> results = FilterUtil.filter(text);
                        SpannableStringBuilder builder = new SpannableStringBuilder(text);
                        for (WordFilterResult result : results) {
                            builder.setSpan(foregroundColorSpan, result.start, result.end + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            builder.setSpan(scaleXSpan, result.start, result.end + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }


                        textView.setText(builder);
                        errorTipListLayout.addView(textView, layoutParams);
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


}
