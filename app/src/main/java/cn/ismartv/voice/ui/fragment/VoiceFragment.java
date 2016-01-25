package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import cn.ismartv.voice.R;
import cn.ismartv.voice.ui.activity.HomeActivity;

/**
 * Created by huaijie on 1/18/16.
 */
public class VoiceFragment extends BaseFragment implements OnClickListener, View.OnTouchListener {

    private ImageButton voiceBtn;
    private ImageView voiceProgressImg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voice, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        voiceBtn = (ImageButton) view.findViewById(R.id.voice_btn);
        voiceProgressImg = (ImageView) view.findViewById(R.id.voice_progress);
        voiceBtn.setOnClickListener(this);
        voiceProgressImg.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.voice_btn:
                ((HomeActivity) getActivity()).handleVoice();
                break;
        }
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
                        return true;
                    case MotionEvent.ACTION_UP:
                        loopAnim(v, false);
                        return true;
                }
        }
        return false;
    }
}
