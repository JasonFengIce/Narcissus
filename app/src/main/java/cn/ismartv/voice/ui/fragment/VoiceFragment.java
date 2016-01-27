package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.ui.activity.HomeActivity;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 1/18/16.
 */
public class VoiceFragment extends BaseFragment implements OnClickListener, View.OnTouchListener {

    private ImageView voiceProgressImg;
    private ImageView voiceMicImg;
    private LinearLayout tipListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voice, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        voiceProgressImg = (ImageView) view.findViewById(R.id.voice_progress);
        voiceMicImg = (ImageView) view.findViewById(R.id.voice_mic);
        tipListView = (LinearLayout) view.findViewById(R.id.tip_list);
        voiceProgressImg.setOnTouchListener(this);


        fetchWords();
    }

    @Override
    public void onClick(View v) {
//                ((HomeActivity) getActivity()).handleVoice();

    }


    private void loopAnim(View imageView, boolean start) {
        Animation operatingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.loop);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (start) {
            imageView.startAnimation(operatingAnim);
        } else {
            imageView.clearAnimation();
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.voice_progress:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        loopAnim(v, true);
                        voiceMicImg.setImageResource(R.drawable.voice_vol_1);
                        return true;
                    case MotionEvent.ACTION_UP:
                        loopAnim(v, false);
                        voiceMicImg.setImageResource(R.drawable.voice_mic);
                        ((HomeActivity) getActivity()).handleVoice();
                        return true;
                }
        }
        return false;
    }

    public void fetchWords() {
        Retrofit retrofit = HttpManager.getInstance().resetAdapter_WUGUOJUN;
        retrofit.create(HttpAPI.Words.class).doRequest(5).enqueue(new Callback<List<String>>() {
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
}
