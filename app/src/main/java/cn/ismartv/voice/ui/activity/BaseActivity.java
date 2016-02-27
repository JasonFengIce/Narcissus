package cn.ismartv.voice.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import cn.ismartv.voice.util.DensityRate;

/**
 * Created by huaijie on 1/18/16.
 */
public class BaseActivity extends FragmentActivity {
    private static final String TAG = "BaseActivity";
    public final static int NETWORK_ERROR = 0x0001;
    private float densityRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        densityRate = DensityRate.getDensityRate(this);
    }


    public float getDensityRate() {
        return densityRate;
    }


//    @Produce
//    public AnswerAvailableEvent produceAnswer() {
//        // Assuming 'lastAnswer' exists.
//        return new AnswerAvailableEvent();
//    }

}
