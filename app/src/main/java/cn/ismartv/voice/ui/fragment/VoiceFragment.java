package cn.ismartv.voice.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
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

import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionClient.VoiceClientStatusChangeListener;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;

import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.handler.AppHandleCallback;
import cn.ismartv.voice.core.handler.HandleCallback;
import cn.ismartv.voice.core.handler.JsonResultHandler;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.core.initialization.AppTableInit;
import cn.ismartv.voice.data.http.AppSearchResponseEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.ui.activity.HomeActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.baidu.voicerecognition.android.VoiceRecognitionClient.getInstance;

/**
 * Created by huaijie on 1/18/16.
 */
public class VoiceFragment extends BaseFragment implements OnClickListener, View.OnTouchListener, VoiceClientStatusChangeListener, HandleCallback, AppHandleCallback {
    private static final String TAG = "VoiceFragment";

    private static final String API_KEY = "YuKSME6OUvZwv016LktWKkjY";
    private static final String SECRET_KEY = "5fead3154852939e74bcaa1248cf33c6";

    private ImageView voiceProgressImg;
    private ImageView voiceMicImg;
    private LinearLayout tipListView;
    private VoiceRecognitionClient voiceRecognitionClient;
    private Call wordsCall;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        voiceRecognitionClient = getInstance(getContext());
        voiceRecognitionClient.setTokenApis(API_KEY, SECRET_KEY);
        AppTableInit.getInstance().getLocalAppList(getContext());
    }

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
                        startRecord();
                        return true;
                    case MotionEvent.ACTION_UP:
                        loopAnim(v, false);
                        voiceMicImg.setImageResource(R.drawable.voice_mic);
//                        ((HomeActivity) getActivity()).handleVoice();
                        voiceRecognitionClient.speakFinish();
                        return true;
                }
        }
        return false;
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

    private void bindParams(VoiceRecognitionConfig config) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        //语义解析
        if (preferences.getBoolean(getString(R.string.nlu), true)) {
            config.enableNLU();
        }

        //采样率
        config.setSampleRate(Integer.parseInt(preferences.getString(getString(R.string.audio_sample), "16000")));

        //语言
        config.setLanguage(preferences.getString(getString(R.string.language), ""));

        //是否开启音效
        if (preferences.getBoolean(getString(R.string.sound_effect), true)) {
            config.enableBeginSoundEffect(R.raw.bdspeech_recognition_start);
            config.enableEndSoundEffect(R.raw.bdspeech_speech_end);
        } else {
            config.disableBeginSoundEffect();
            config.disableEndSoundEffect();
        }
        //是否开启蓝牙
        config.setUseBlueTooth(preferences.getBoolean(getString(R.string.bluetooth), false));
    }

    private void startRecord() {
        VoiceRecognitionConfig voiceRecognitionConfig = new VoiceRecognitionConfig();
        bindParams(voiceRecognitionConfig);
//        voiceRecognitionConfig.setUseDefaultAudioSource(false);
        voiceRecognitionClient.startVoiceRecognition(this, voiceRecognitionConfig);
    }

    @Override
    public void onClientStatusChange(int status, Object o) {
        switch (status) {
            // 语音识别实际开始，这是真正开始识别的时间点，需在界面提示用户说话。
            case VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING:
                break;
            // 检测到语音起点
            case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START:
                break;
            // 已经检测到语音终点，等待网络返回
            case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END:
                break;
            // 语音识别完成，显示obj中的结果
            case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
//                resultText.setText(o.toString());
                new JsonResultHandler(o.toString(), this, this);
                Log.i(TAG, o.toString());
                break;
            // 处理连续上屏
            case VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS:
                break;
            // 用户取消
            case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetworkStatusChange(int i, Object o) {

    }

    @Override
    public void onError(int i, int i1) {

    }

    @Override
    public void onHandleSuccess(SemanticSearchResponseEntity entity, String jsonData, long tag) {
        ((HomeActivity) getActivity()).showIndicatorFragment(entity, jsonData, tag);
    }

    @Override
    public void onAppHandleSuccess(AppSearchResponseEntity entity, String data, long tag) {
        ((HomeActivity) getActivity()).showAppIndicatorFragment(entity.getObjects(), data, tag);
    }
}
