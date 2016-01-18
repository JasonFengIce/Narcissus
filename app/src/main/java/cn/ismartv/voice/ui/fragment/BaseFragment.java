package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.ismartv.voice.util.DensityRate;

/**
 * Created by huaijie on 1/18/16.
 */
public class BaseFragment extends Fragment {

    private float densityRate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        densityRate = DensityRate.getDensityRate(getActivity());
    }

    public float getDensityRate() {
        return densityRate;
    }
}
