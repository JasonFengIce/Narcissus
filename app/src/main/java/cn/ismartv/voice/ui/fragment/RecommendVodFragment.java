package cn.ismartv.voice.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
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
    private static final String TAG = "ContentFragment";
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
            itemVodTitle.setText(list.get(i).getTitle());
            Transformation mTransformation = new ReflectionTransformationBuilder()
                    .setIsHorizontal(true)
                    .build();
            String verticalUrl = list.get(i).getVertical_url();
            String horizontalUrl = list.get(i).getPoster_url();
            if (!TextUtils.isEmpty(verticalUrl)) {
                Picasso.with(getContext())
                        .load(verticalUrl)
                        .memoryPolicy(MemoryPolicy.NO_STORE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .error(R.drawable.vertical_preview_bg)
                        .placeholder(R.drawable.vertical_preview_bg)
                        .transform(mTransformation)
                        .into(itemVodImage);
            } else {
                Picasso.with(getContext())
                        .load(horizontalUrl)
                        .memoryPolicy(MemoryPolicy.NO_STORE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
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
        ImageView imageView = (ImageView) v.findViewById(R.id.item_vod_image);
        TextView textView = (TextView) v.findViewById(R.id.item_vod_title);
        if (hasFocus) {
            textView.setSelected(true);
            imageView.setBackgroundResource(R.drawable.item_focus);
            ViewScaleUtil.scaleToLarge(v, 1.15f);
        } else {
            textView.setSelected(false);
            imageView.setBackgroundDrawable(null);
            ViewScaleUtil.scaleToNormal(v, 1.15f);
        }
    }

    @Override
    public void onClick(View v) {
        onVodItemClick((SemantichObjectEntity) v.getTag());
    }
}
