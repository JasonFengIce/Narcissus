package cn.ismartv.voice.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.event.AnswerAvailableEvent;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.http.SemantichObjectEntity;
import cn.ismartv.voice.data.http.SharpHotWordsEntity;
import cn.ismartv.voice.util.ViewScaleUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 2/18/16.
 */
public class RecognizeErrorFragment extends BaseFragment implements View.OnFocusChangeListener, OnClickListener {

    private LinearLayout errorTipListLayout;
    private Call wordsCall;

    private TextView searchTitle;

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
        searchTitle = (TextView) view.findViewById(R.id.search_title);
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
                                      layoutParams.topMargin = marginTop / 2;
                                      layoutParams.bottomMargin = marginTop / 2;
//                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color._ff9c3c));
//                    ScaleXSpan scaleXSpan = new ScaleXSpan(1.2f);

                                      for (int i = 0; i < tipList.getObjects().size() && i < 7; i++) {
                                          TextView textView = new TextView(getContext());
                                          textView.setClickable(true);
                                          textView.setFocusable(true);
                                          textView.setId(R.id.recognize_error_item);
                                          textView.setTextSize(getResources().getDimension(R.dimen.textSize_36sp) / getDensityRate());
                                          SemantichObjectEntity entity = tipList.getObjects().get(i);
                                          String text = entity.getTitle();

                                          textView.setText(text);

                                          List<SemantichObjectEntity> datas = tipList.getObjects();
                                          int postion = i;

                                          String postUrl = datas.get(postion).getPoster_url();
                                          String verticalUrl = datas.get(postion).getVertical_url();
                                          HashMap<String, String> hashMap = new HashMap<>();
                                          hashMap.put("url", datas.get(postion).getUrl());
                                          hashMap.put("content_model", datas.get(postion).getContent_model());
                                          hashMap.put("pk", datas.get(postion).getPk());
                                          hashMap.put("title", datas.get(postion).getTitle());

                                          if (!TextUtils.isEmpty(verticalUrl)) {
                                              hashMap.put("orientation", "vertical");

                                          } else if (!TextUtils.isEmpty(postUrl)) {
                                              hashMap.put("orientation", "horizontal");

                                          } else {
                                              hashMap.put("orientation", "vertical");
                                          }

                                          textView.setTag(hashMap);
                                          textView.setOnFocusChangeListener(RecognizeErrorFragment.this);
                                          textView.setOnClickListener(RecognizeErrorFragment.this);
                                          textView.setOnHoverListener(new View.OnHoverListener() {
                                              @Override
                                              public boolean onHover(View view, MotionEvent motionEvent) {
                                                  switch (motionEvent.getAction()) {
                                                      case MotionEvent.ACTION_HOVER_ENTER:
                                                      case MotionEvent.ACTION_HOVER_MOVE:
                                                          view.requestFocusFromTouch();
                                                          view.requestFocus();
                                                          break;
                                                      case MotionEvent.ACTION_HOVER_EXIT:
                                                          searchTitle.requestFocus();
                                                          break;
                                                  }
                                                  return true;
                                              }
                                          });


                                          errorTipListLayout.addView(textView, layoutParams);
                                          if (postion == 0) {
                                              textView.setNextFocusUpId(textView.getId());
                                              textView.requestFocus();
                                              textView.requestFocusFromTouch();
                                          }
                                      }
                                  } else

                                  {
                                      EventBus.getDefault().post(new AnswerAvailableEvent(AnswerAvailableEvent.EventType.NETWORK_ERROR, AnswerAvailableEvent.NETWORK_ERROR));
                                  }
                              }

                              @Override
                              public void onFailure(Throwable t) {
                                  EventBus.getDefault().post(new AnswerAvailableEvent(AnswerAvailableEvent.EventType.NETWORK_ERROR, AnswerAvailableEvent.NETWORK_ERROR));
                              }
                          }

        );
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        TextView textView = (TextView) v;
        if (hasFocus) {
            textView.setTextColor(getResources().getColor(R.color._ff9c3c));
            ViewScaleUtil.zoomin_1_3(v);
        } else {
            textView.setTextColor(getResources().getColor(R.color._ffffff));
            ViewScaleUtil.zoomout_1_3(v);
        }
    }

    @Override
    public void onClick(View v) {
        HashMap<String, String> tag = (HashMap) v.getTag();
        String url = tag.get("url");
        String contentModel = tag.get("content_model");
        Long pk = Long.parseLong(tag.get("pk"));
        String title = tag.get("title");
        Intent intent = new Intent();
        switch (contentModel) {
            case "person":
                intent.putExtra("pk", pk);
                intent.putExtra("title", title);
                intent.setAction("cn.ismartv.voice.film_star");
                startActivity(intent);
                break;
            default:
                if (!TextUtils.isEmpty(url)) {

                    intent.putExtra("url", url);

                    switch (tag.get("orientation")) {
                        case "horizontal":
                            intent.setAction("tv.ismar.daisy.Item");
                            break;
                        case "vertical":
                            intent.setAction("tv.ismar.daisy.PFileItem");
                            break;
                    }
                    startActivity(intent);
                }
                break;
        }

    }
}
