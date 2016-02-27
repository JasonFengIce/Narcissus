package cn.ismartv.voice.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionClient.VoiceClientStatusChangeListener;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.handler.AppHandleCallback;
import cn.ismartv.voice.core.handler.HandleCallback;
import cn.ismartv.voice.core.handler.JsonDomainHandler;
import cn.ismartv.voice.core.handler.MultiHandlerCallback;
import cn.ismartv.voice.core.handler.WeatherHandlerCallback;
import cn.ismartv.voice.core.initialization.AppTableInit;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.data.http.AppSearchResponseEntity;
import cn.ismartv.voice.data.http.IndicatorResponseEntity;
import cn.ismartv.voice.data.http.JsonRes;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.data.http.VoiceResultEntity;
import cn.ismartv.voice.data.table.CityTable;
import cn.ismartv.voice.ui.activity.HomeActivity;

import static com.baidu.voicerecognition.android.VoiceRecognitionClient.getInstance;

/**
 * Created by huaijie on 1/18/16.
 */
public class VoiceFragment extends BaseFragment implements OnClickListener, View.OnTouchListener,
        VoiceClientStatusChangeListener, HandleCallback, AppHandleCallback, MultiHandlerCallback, WeatherHandlerCallback {
    private static final String TAG = "VoiceFragment";
    /**
     * 音量更新间隔
     */
    private static final int POWER_UPDATE_INTERVAL = 100;

    private static final String API_KEY = "YuKSME6OUvZwv016LktWKkjY";
    private static final String SECRET_KEY = "5fead3154852939e74bcaa1248cf33c6";

    private static final String SEARCH_TIP_FRAGMENT_TAG = "search_tip_fragment_tag";
    private static final String SEARCH_NO_RESULT_FRAGMENT_TAG = "search_no_result_fragment_tag";
    private static final String SEARCH_KEYWORD_FRAGMENT_TAG = "search_keyword_fragment_tag";
    private static final String LEFT_SEARCHING_LOADING_FRAGMENT = "left_searching_loading_fragment";

    private ImageView voiceProgressImg;
    private ImageView voiceMicImg;

    private VoiceRecognitionClient voiceRecognitionClient;

    private SearchTipFragment searchTipFragment;
    private SearchNoResultFragment searchNoResultFragment;
    private SearchKeyWordFragment searchKeyWordFragment;
    private LeftSearchLoadingFragment leftSearchLoadingFragment;

    private boolean isRecognition = false;
    private Handler mHandler;
    private List<BaseFragment> childFragmentList;

    private boolean voiceIsEnable = true;

    private ImageView slideMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        voiceRecognitionClient = getInstance(getContext());
        voiceRecognitionClient.setTokenApis(API_KEY, SECRET_KEY);
        AppTableInit.getInstance().getLocalAppList(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        searchTipFragment = new SearchTipFragment();
        searchNoResultFragment = new SearchNoResultFragment();
        searchKeyWordFragment = new SearchKeyWordFragment();
        leftSearchLoadingFragment = new LeftSearchLoadingFragment();
        childFragmentList = new ArrayList<>();
        childFragmentList.add(searchTipFragment);
        childFragmentList.add(searchNoResultFragment);
        childFragmentList.add(searchKeyWordFragment);
        childFragmentList.add(leftSearchLoadingFragment);

        return inflater.inflate(R.layout.fragment_voice, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.search_tip_layout, searchTipFragment, SEARCH_TIP_FRAGMENT_TAG);
        fragmentTransaction.add(R.id.search_tip_layout, searchNoResultFragment, SEARCH_NO_RESULT_FRAGMENT_TAG);
        fragmentTransaction.add(R.id.search_tip_layout, searchKeyWordFragment, SEARCH_KEYWORD_FRAGMENT_TAG);
        fragmentTransaction.add(R.id.search_tip_layout, leftSearchLoadingFragment, LEFT_SEARCHING_LOADING_FRAGMENT);
        fragmentTransaction.hide(leftSearchLoadingFragment);
        fragmentTransaction.hide(searchKeyWordFragment);
        fragmentTransaction.hide(searchNoResultFragment);
        fragmentTransaction.commit();

        voiceProgressImg = (ImageView) view.findViewById(R.id.voice_progress);
        voiceMicImg = (ImageView) view.findViewById(R.id.voice_mic);

        slideMenu = (ImageView) view.findViewById(R.id.indicator_right_slide_menu);
        slideMenu.bringToFront();
        slideMenu.setOnClickListener(this);
        voiceProgressImg.setOnTouchListener(this);


    }

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if (!hidden) {
//            if (searchKeyWordFragment.isVisible()) {
//                slideMenu.setVisibility(View.VISIBLE);
//            } else {
//                slideMenu.setVisibility(View.GONE);
//            }
//        }
//    }

    public void backToVoice() {
        slideMenu.setVisibility(View.VISIBLE);
        showFragment(searchTipFragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.indicator_right_slide_menu:
                ((HomeActivity) getActivity()).showIndicatorFragment();
                break;
        }
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
                        startSpeek();
                        return true;
                    case MotionEvent.ACTION_UP:
                        stopSpeek();
                        return true;
                }
        }
        return false;
    }

    public void startSpeek() {
        if (voiceIsEnable) {
            slideMenu.setVisibility(View.GONE);
            voiceIsEnable = false;
            loopAnim(voiceProgressImg, true);
            voiceMicImg.setImageResource(R.drawable.voice_vol_1);
            startRecord();
            voiceMicImg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    voiceIsEnable = true;
                }
            }, 1000);

        } else {
            Toast.makeText(getContext(), "您操作太过频繁!!!", Toast.LENGTH_LONG).show();
        }
    }

    public void stopSpeek() {
        loopAnim(voiceProgressImg, false);

//                        ((HomeActivity) getActivity()).handleVoice();
        voiceRecognitionClient.speakFinish();
    }


    @Override
    public void onStop() {

        super.onStop();
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
                isRecognition = true;
                mHandler.removeCallbacks(mUpdateVolume);
                mHandler.postDelayed(mUpdateVolume, POWER_UPDATE_INTERVAL);
                break;
            // 检测到语音起点
            case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START:
                break;
            // 已经检测到语音终点，等待网络返回
            case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END:
                showFragment(leftSearchLoadingFragment);
                ((HomeActivity) getActivity()).showSearchLoading();
                break;
            // 语音识别完成，显示obj中的结果
            case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
                isRecognition = false;
                voiceMicImg.setImageResource(R.drawable.voice_mic);
                VoiceResultEntity[] voiceResultEntity = new Gson().fromJson(o.toString(), VoiceResultEntity[].class);
                if (voiceResultEntity.length == 0) {
                    showRecognizeErrorFragment();
                } else {
                    JsonElement jsonElement = new JsonParser().parse(voiceResultEntity[0].getJson_res());
                    JsonRes jsonRes = new Gson().fromJson(jsonElement, JsonRes.class);
                    String rawText = jsonRes.getRaw_text();
                    showSearchKeyWordFragment(rawText);
                    new JsonDomainHandler(o.toString(), this, this, this, this);
                }
//                Log.i(TAG, o.toString());
                break;
            // 处理连续上屏
            case VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS:
                break;
            // 用户取消
            case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
                isRecognition = false;
                voiceMicImg.setImageResource(R.drawable.voice_mic);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetworkStatusChange(int i, Object o) {

    }

    @Override
    public void onError(int errorType, int errorCode) {
        voiceMicImg.setImageResource(R.drawable.voice_mic);
        isRecognition = false;
        switch (errorType) {
            case VoiceRecognitionClient.ERROR_CLIENT:
            case VoiceRecognitionClient.ERROR_RECORDER:
            case VoiceRecognitionClient.ERROR_SERVER:
                showRecognizeErrorFragment();
                break;
            case VoiceRecognitionClient.ERROR_NETWORK:
                break;
        }

    }


    @Override
    public void onHandleSuccess(SemanticSearchResponseEntity entity, String jsonData) {

        if (entity.getFacet().size() == 0) {

//        if (true) {
            String rawText = new JsonParser().parse(jsonData).getAsJsonObject().get("raw_text").toString();
            showNoVideoResultFragment(rawText);


        } else {
            ((HomeActivity) getActivity()).showIndicatorFragment(entity, jsonData);
        }

    }

    @Override
    public void onAppHandleSuccess(AppSearchResponseEntity entity, String data) {
        if (entity.getFacet() == null || entity.getFacet()[0].getObjects().size() == 0) {
//        if (true) {
            String rawText = new JsonParser().parse(data).getAsJsonObject().get("raw_text").toString();
            showNoAppResultFragment(rawText);
        } else {
            ((HomeActivity) getActivity()).showAppIndicatorFragment(entity.getFacet()[0].getObjects(), data);
        }
    }

    @Override
    public void onMultiHandle(List<IndicatorResponseEntity> list, String rawText) {
        int totalSize = 0;
        int videoListSize = 0;
        int appListSize = 0;
        for (IndicatorResponseEntity entity : list) {
            switch (entity.getType()) {
                case "video":
                    videoListSize = ((SemanticSearchResponseEntity) entity.getSearchData()).getFacet().size();
                    totalSize = totalSize + videoListSize;
                    break;
                case "app":
                    appListSize = ((List<AppSearchObjectEntity>) entity.getSearchData()).size();
                    totalSize = totalSize + appListSize;
                    break;
            }
        }

        if (totalSize == 0) {
            showNoVideoResultFragment(rawText);
        } else {
            for (IndicatorResponseEntity entity : list) {
                switch (entity.getType()) {
                    case "video":
                        if (videoListSize != 0) {
                            ((HomeActivity) getActivity()).showIndicatorFragment((SemanticSearchResponseEntity) entity.getSearchData(), entity.getSemantic());
                        }
                        break;
                    case "app":
                        if (appListSize != 0) {
                            if (videoListSize == 0) {
                                ((HomeActivity) getActivity()).showAppIndicatorFragment((List<AppSearchObjectEntity>) entity.getSearchData(), entity.getSemantic());
                            } else {
                                ((HomeActivity) getActivity()).showAppIndicatorFragmentNoClear((List<AppSearchObjectEntity>) entity.getSearchData(), entity.getSemantic());
                            }
                        }
                        break;
                }
            }
        }
    }


    private void showNoVideoResultFragment(String rawText) {
        searchNoResultFragment.recognizeNoResult(rawText);
        showFragment(searchNoResultFragment);
        ((HomeActivity) getActivity()).recommendVideo();
    }

    private void showNoAppResultFragment(String rawText) {
        showFragment(searchNoResultFragment);
        searchNoResultFragment.recognizeNoResult(rawText);
        ((HomeActivity) getActivity()).recommendApp();
    }

    private void showRecognizeErrorFragment() {
        showFragment(searchNoResultFragment);
        searchNoResultFragment.recognizeError();
        ((HomeActivity) getActivity()).showRecognizeError();
    }

    private void showSearchKeyWordFragment(String rawText) {
        searchKeyWordFragment.setSearchKeyWord(rawText);
        showFragment(searchKeyWordFragment);
    }

    private void volumeChange(int vol) {
        if (vol <= 30) {
            voiceMicImg.setImageResource(R.drawable.voice_vol_1);
        } else if (vol > 30 && vol <= 60) {
            voiceMicImg.setImageResource(R.drawable.voice_vol_2);
        } else if (vol > 60) {
            voiceMicImg.setImageResource(R.drawable.voice_vol_3);
        }
    }

    private Runnable mUpdateVolume = new Runnable() {
        public void run() {
            if (isRecognition) {
                long vol = voiceRecognitionClient.getCurrentDBLevelMeter();
                volumeChange((int) vol);
                mHandler.removeCallbacks(mUpdateVolume);
                mHandler.postDelayed(mUpdateVolume, POWER_UPDATE_INTERVAL);
            }
        }
    };


    @Override
    public void onWeatherHandle(CityTable table) {
        ((HomeActivity) getActivity()).showWeatherNoRegion(table);
    }

    private void showFragment(BaseFragment fragment) {
        for (BaseFragment f : childFragmentList) {
            if (fragment != f && f.isVisible())
                getChildFragmentManager().beginTransaction().hide(f).commit();
        }
        getChildFragmentManager().beginTransaction().show(fragment).commit();
    }
}
