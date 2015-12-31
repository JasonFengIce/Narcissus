package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;

import cn.ismartv.voice.R;

/**
 * Created by huaijie on 12/31/15.
 */
public class TestFragment extends Fragment implements OnClickListener {

    private Button button;

    private BaiduASRDigitalDialog mDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        button = (Button) view.findViewById(R.id.btn_test);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test:
                break;
        }
    }

    private void startTest() {


    }
}
