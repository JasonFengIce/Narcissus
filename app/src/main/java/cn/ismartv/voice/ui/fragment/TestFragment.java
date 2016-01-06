package cn.ismartv.voice.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cn.ismartv.voice.core.handler.JsonResultHandler;
import cn.ismartv.voice.data.table.AppTable;

import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

import java.io.IOException;
import java.io.InputStream;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.Config;
import cn.ismartv.voice.core.initialization.AppTableInit;
import cn.ismartv.voice.ui.activity.SettingActivity;

import static com.baidu.voicerecognition.android.VoiceRecognitionClient.*;

/**
 * Created by huaijie on 12/31/15.
 */
public class TestFragment extends Fragment implements OnTouchListener, VoiceClientStatusChangeListener, OnClickListener {
    private static final String TAG = "TestFragment";
    private static final String API_KEY = "YuKSME6OUvZwv016LktWKkjY";
    private static final String SECRET_KEY = "5fead3154852939e74bcaa1248cf33c6";

    private Button button;
    private Button settingBtn;
    private TextView resultText;

    private BaiduASRDigitalDialog mDialog;
    private VoiceRecognitionClient voiceRecognitionClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        voiceRecognitionClient = getInstance(getContext());
        voiceRecognitionClient.setTokenApis(API_KEY, SECRET_KEY);
        AppTableInit.getInstance().getLocalAppList(getContext());


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        button = (Button) view.findViewById(R.id.btn_test);
        settingBtn = (Button) view.findViewById(R.id.setting);
        resultText = (TextView) view.findViewById(R.id.result);

        button.setOnTouchListener(this);
        settingBtn.setOnClickListener(this);

    }


    private void bindParams(VoiceRecognitionConfig config) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        //语义解析
        if (preferences.getBoolean(getString(R.string.nlu), true)) {
            config.enableNLU();
        }

        //采样率
        config.setSampleRate(Integer.parseInt(preferences.getString(getString(R.string.audio_sample), "")));

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

    private void startTest2() {
        VoiceRecognitionConfig voiceRecognitionConfig = new VoiceRecognitionConfig();
        voiceRecognitionConfig.enableBeginSoundEffect(R.raw.bdspeech_recognition_start);
        voiceRecognitionConfig.enableEndSoundEffect(R.raw.bdspeech_speech_end);
        voiceRecognitionConfig.enableNLU();
        voiceRecognitionConfig.setSampleRate(VoiceRecognitionConfig.SAMPLE_RATE_8K);
//        voiceRecognitionConfig.setUseDefaultAudioSource(false);
        voiceRecognitionClient.startVoiceRecognition(this, voiceRecognitionConfig);

    }

    private void startTest() {
        Bundle params = new Bundle();
        params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, API_KEY);
        params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, SECRET_KEY);
        params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, Config.DIALOG_THEME);

        mDialog = new BaiduASRDigitalDialog(getContext(), params);
        mDialog.setDialogRecognitionListener(new DialogRecognitionListener() {
            @Override
            public void onResults(Bundle bundle) {

            }
        });

        mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP, Config.CURRENT_PROP);
        mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE, Config.getCurrentLanguage());
        mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, Config.PLAY_START_SOUND);
        mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, Config.PLAY_END_SOUND);
        mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, Config.DIALOG_TIPS_SOUND);
        mDialog.show();

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
                resultText.setText(o.toString());
                new JsonResultHandler(o.toString());
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
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.btn_test:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startTest2();
                        return true;
                    case MotionEvent.ACTION_UP:
                        voiceRecognitionClient.speakFinish();
                        return true;
                }
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting:
                Intent intent = new Intent();
                intent.setClass(getContext(), SettingActivity.class);
                startActivity(intent);
                break;
        }
    }
}
