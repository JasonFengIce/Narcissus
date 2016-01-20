package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import cn.ismartv.voice.R;
import cn.ismartv.voice.ui.activity.HomeActivity;

/**
 * Created by huaijie on 1/18/16.
 */
public class VoiceFragment extends BaseFragment implements OnClickListener {

    private ImageButton voiceBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voice, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        voiceBtn = (ImageButton) view.findViewById(R.id.voice_btn);
        voiceBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.voice_btn:
                ((HomeActivity) getActivity()).handleVoice();
                break;
        }
    }

}
