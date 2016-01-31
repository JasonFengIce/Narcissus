package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.ismartv.imagereflection.RelectionImageView;
import cn.ismartv.recyclerview.widget.GridLayoutManager;
import cn.ismartv.recyclerview.widget.RecyclerView;
import cn.ismartv.voice.R;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.data.http.SemantichObjectEntity;
import cn.ismartv.voice.data.http.SharpHotWordsEntity;
import cn.ismartv.voice.ui.SpaceItemDecoration;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 1/18/16.
 */
public class ContentFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private TextView searchTitle;
//    private static final int resIds[] = {R.drawable.sample_1, R.drawable.sample_2, R.drawable.sample_3, R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7, R.drawable.sample_8};

    private static final int resIds[] = {R.drawable.test_1, R.drawable.test_2, R.drawable.test_3, R.drawable.test_4, R.drawable.test_5,
            R.drawable.test_6, R.drawable.test_7, R.drawable.test_8,
            R.drawable.sample_1, R.drawable.sample_2, R.drawable.sample_3, R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7, R.drawable.sample_8


    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        searchTitle = (TextView) view.findViewById(R.id.search_title);
        fetchSharpHotWords();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false);

        int verticalSpacingInPixels = (int) (getResources().getDimensionPixelSize(R.dimen.content_fragment_item_vertical_space) / getDensityRate());
        int horizontalSpacingInPixels = (int) (getResources().getDimensionPixelSize(R.dimen.content_fragment_item_horizontal_space) / getDensityRate());
        recyclerView.addItemDecoration(new SpaceItemDecoration(verticalSpacingInPixels, horizontalSpacingInPixels));
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    public void notifyDataChanged(SemanticSearchResponseEntity responseEntity, String data) {
        String rawTextValue = getString(R.string.search_title);
        String rawText = new JsonParser().parse(data).getAsJsonObject().get("raw_text").toString();
        searchTitle.setText(String.format(rawTextValue, rawText));
        recyclerView.setAdapter(new RecyclerAdapter(responseEntity.getFacet().get(0).getObjects()));
        //first item request focus
    }


    private class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private List<SemantichObjectEntity> datas;

        public RecyclerAdapter(List<SemantichObjectEntity> objectEntities) {
            this.datas = objectEntities;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_recycler, viewGroup, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int postion) {

            myViewHolder.textView.setText(datas.get(postion).getTitle());
            String postUrl = datas.get(postion).getPoster_url();
            String verticalUrl = datas.get(postion).getVertical_url();
            if (!TextUtils.isEmpty(postUrl)) {
                myViewHolder.imageView.setIsHorizontal(true);
                Picasso.with(getContext()).load(postUrl).error(R.drawable.horizontal_preview_bg).into(myViewHolder.imageView);


            } else if (!TextUtils.isEmpty(verticalUrl)) {
                myViewHolder.imageView.setIsHorizontal(false);
                Picasso.with(getContext())
                        .load(datas.get(postion).getVertical_url())
                        .error(R.drawable.vertical_preview_bg)
                        .placeholder(R.drawable.vertical_preview_bg)
                        .into(myViewHolder.imageView);
            } else {
                Picasso.with(getContext())
                        .load(R.drawable.vertical_preview_bg)
                        .placeholder(R.drawable.vertical_preview_bg)
                        .into(myViewHolder.imageView);
            }

            if (postion == 0) {
                myViewHolder.imageView.requestFocusFromTouch();
                myViewHolder.imageView.requestFocus();

            }
        }


        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private RelectionImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.id_number);
            imageView = (RelectionImageView) itemView.findViewById(R.id.image);

        }
    }

    private void fetchSharpHotWords() {
        Retrofit retrofit = HttpManager.getInstance().resetAdapter_WUGUOJUN;
        retrofit.create(HttpAPI.SharpHotWords.class).doRequest(8).enqueue(new Callback<SharpHotWordsEntity>() {
            @Override
            public void onResponse(Response<SharpHotWordsEntity> response) {
                if (response.errorBody() == null) {
                    recyclerView.setAdapter(new RecyclerAdapter(response.body().getObjects()));
                } else {

                }

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }
}
