package cn.ismartv.voice.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.event.AnswerAvailableEvent;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.util.ViewScaleUtil;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 1/28/16.
 */
public class RecommendAppFragment extends BaseFragment implements View.OnFocusChangeListener, View.OnClickListener {
    private View contentView;
    private TextView searchTitle;

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
        contentView = inflater.inflate(R.layout.fragment_recommend_app, null);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchTitle = (TextView) view.findViewById(R.id.search_title);
        searchTitle.setText(getString(R.string.recommend_content_title));

        firstRowLayout = (LinearLayout) view.findViewById(R.id.first_row);
        secondRowLayout = (LinearLayout) view.findViewById(R.id.second_row);

        fetchRecommendApp();
    }


    public void fetchRecommendApp() {
        Retrofit retrofit = HttpManager.getInstance().resetAdapter_WUGUOJUN;
        retrofit.create(HttpAPI.RecommandApp.class).doRequest(8).enqueue(new Callback<List<AppSearchObjectEntity>>() {
            @Override
            public void onResponse(Response<List<AppSearchObjectEntity>> response) {
                if (response.errorBody() != null) {
                    EventBus.getDefault().post(new AnswerAvailableEvent(AnswerAvailableEvent.EventType.NETWORK_ERROR, AnswerAvailableEvent.NETWORK_ERROR));
                } else {
                    List<AppSearchObjectEntity> entities = response.body();
                    fillGridLayout(entities);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                EventBus.getDefault().post(new AnswerAvailableEvent(AnswerAvailableEvent.EventType.NETWORK_ERROR, AnswerAvailableEvent.NETWORK_ERROR));
            }
        });
    }

    private void fillGridLayout(List<AppSearchObjectEntity> list) {
        for (int i = 0; i < list.size(); i++) {

            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_app, null);
            TextView itemVodTitle = (TextView) itemView.findViewById(R.id.item_app_title);
            ImageView itemVodImage = (ImageView) itemView.findViewById(R.id.item_app_image);
            itemVodTitle.setText(list.get(i).getTitle());
            String iconUrl = list.get(i).getAdlet_url();
            Picasso.with(getContext())
                    .load(iconUrl)
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .error(R.drawable.vertical_preview_bg)
                    .placeholder(R.drawable.vertical_preview_bg)
                    .into(itemVodImage);

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
        View imageView = v.findViewById(R.id.item_app_image);
        TextView textView = (TextView) v.findViewById(R.id.item_app_title);
        if (hasFocus) {
            textView.setSelected(true);
            imageView.setSelected(true);
            ViewScaleUtil.zoomin_1_15(v);
        } else {
            textView.setSelected(false);
            imageView.setSelected(false);
            ViewScaleUtil.zoomout_1_15(v);
        }
    }


    @Override
    public void onClick(View v) {
        onAppItemClick((AppSearchObjectEntity) v.getTag());
    }
}
