package cn.ismartv.voice.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.event.AnswerAvailableEvent;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.http.SemantichObjectEntity;
import cn.ismartv.voice.data.http.SharpHotWordsEntity;
import cn.ismartv.voice.ui.ReflectionTransformationBuilder;
import cn.ismartv.voice.util.ViewScaleUtil;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 1/18/16.
 */
public class RecommendVodFragment extends BaseFragment implements OnFocusChangeListener, OnClickListener {
    private static final String TAG = "RecommendVodFragment";
    private TextView searchTitle;

    private boolean isRecommend = false;

    public boolean isRecommend() {
        return isRecommend;
    }

    private Context mContext;

    private LinearLayout firstRowLayout;
    private LinearLayout secondRowLayout;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommend_vod, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchTitle = (TextView) view.findViewById(R.id.search_title);

        firstRowLayout = (LinearLayout) view.findViewById(R.id.first_row);
        secondRowLayout = (LinearLayout) view.findViewById(R.id.second_row);

        fetchSharpHotWords();

    }

    public void reset() {
        isRecommend = false;
        searchTitle.setText(getString(R.string.today_hot_vod));
    }

    public void changeTitle() {
        isRecommend = true;
        searchTitle.setText(getString(R.string.recommend_content_title));
    }


    public void fetchSharpHotWords() {
        Retrofit retrofit = HttpManager.getInstance().resetAdapter_WUGUOJUN;
        retrofit.create(HttpAPI.SharpHotWords.class).doRequest(8).enqueue(new Callback<SharpHotWordsEntity>() {
            @Override
            public void onResponse(Response<SharpHotWordsEntity> response) {
                if (response.errorBody() == null) {
                    fillGridLayout(response.body().getObjects());
                } else {
                    EventBus.getDefault().post(new AnswerAvailableEvent(AnswerAvailableEvent.EventType.NETWORK_ERROR, AnswerAvailableEvent.NETWORK_ERROR));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                EventBus.getDefault().post(new AnswerAvailableEvent(AnswerAvailableEvent.EventType.NETWORK_ERROR, AnswerAvailableEvent.NETWORK_ERROR));
            }
        });
    }

    private void fillGridLayout(List<SemantichObjectEntity> list) {
        for (int i = 0; i < list.size(); i++) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_vod, null);
            TextView itemVodTitle = (TextView) itemView.findViewById(R.id.item_vod_title);
            ImageView itemVodImage = (ImageView) itemView.findViewById(R.id.item_vod_image);
            TextView itemScore = (TextView) itemView.findViewById(R.id.item_vod_score);
            TextView itemPrice = (TextView) itemView.findViewById(R.id.item_vod_price);
            TextView itemFocus = (TextView) itemView.findViewById(R.id.item_vod_focus);
            itemView.setOnHoverListener(mOnHoverListener);
            itemVodTitle.setText(list.get(i).getTitle());
            Transformation mTransformation = new ReflectionTransformationBuilder()
                    .setIsHorizontal(true)
                    .build();
            String verticalUrl = list.get(i).getVertical_url();
            String horizontalUrl = list.get(i).getPoster_url();
            String scoreValue = list.get(i).getBean_score();
            float priceValue = list.get(i).getExpense() == null ? 0 : list.get(i).getExpense().getPrice();
            String focusValue = list.get(i).getFocus();

            if (!TextUtils.isEmpty(verticalUrl)) {
                if (!TextUtils.isEmpty(scoreValue)) {
                    itemScore.setVisibility(View.VISIBLE);
                    itemScore.setText(scoreValue);
                }
                if (priceValue != 0) {
                    itemPrice.setVisibility(View.VISIBLE);
                    itemPrice.setText("￥" + String.valueOf(priceValue));
                }
                if (!TextUtils.isEmpty(focusValue)) {
                    itemFocus.setVisibility(View.VISIBLE);
                    itemFocus.setText(focusValue);
                }

                Picasso.with(getContext())
                        .load(verticalUrl)
                        .error(R.drawable.vertical_preview_bg)
                        .placeholder(R.drawable.vertical_preview_bg)
                        .transform(mTransformation)
                        .into(itemVodImage);
            } else {
                Picasso.with(getContext())
                        .load(horizontalUrl)
                        .error(R.drawable.vertical_preview_bg)
                        .placeholder(R.drawable.vertical_preview_bg)
                        .transform(mTransformation)
                        .into(itemVodImage);
            }
            itemView.setTag(list.get(i));
            itemView.setOnFocusChangeListener(this);
            itemView.setOnClickListener(this);

            int row = i / 4 + 1;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            switch (row) {
                case 1:
                    itemView.setNextFocusUpId(itemView.getId());
                    firstRowLayout.addView(itemView, layoutParams);
                    break;
                case 2:
                    secondRowLayout.addView(itemView, layoutParams);
                    break;
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        View imageViewLayout = v.findViewById(R.id.item_vod_image_layout);
        TextView textView = (TextView) v.findViewById(R.id.item_vod_title);
        if (hasFocus) {
            textView.setSelected(true);
            imageViewLayout.setSelected(true);
            ViewScaleUtil.zoomin_1_15(v);
        } else {
            textView.setSelected(false);
            imageViewLayout.setSelected(false);
            ViewScaleUtil.zoomout_1_15(v);
        }
    }

    @Override
    public void onClick(View v) {
        onVodItemClick((SemantichObjectEntity) v.getTag());
    }

    private View.OnHoverListener mOnHoverListener = new View.OnHoverListener() {

        @Override
        public boolean onHover(View v, MotionEvent keycode) {
            switch (keycode.getAction()) {
                case MotionEvent.ACTION_HOVER_ENTER:
                case MotionEvent.ACTION_HOVER_MOVE:
                    v.requestFocusFromTouch();
                    v.requestFocus();
                    break;
                case MotionEvent.ACTION_HOVER_EXIT:
                    searchTitle.requestFocus();
                    break;
                default:
                    break;
            }
            return true;
        }
    };
}
