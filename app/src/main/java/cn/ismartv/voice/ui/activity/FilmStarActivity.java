package cn.ismartv.voice.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import cn.ismartv.imagereflection.RelectionImageView;
import cn.ismartv.recyclerview.widget.LinearLayoutManager;
import cn.ismartv.recyclerview.widget.RecyclerView;
import cn.ismartv.voice.R;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.http.ActorRelateRequestParams;
import cn.ismartv.voice.data.http.AttributesEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.data.http.SemantichObjectEntity;
import cn.ismartv.voice.ui.StarSpaceItemDecoration;
import cn.ismartv.voice.util.ViewScaleUtil;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huaijie on 2/22/16.
 */
public class FilmStarActivity extends BaseActivity implements OnFocusChangeListener, View.OnClickListener {
    private static final String TAG = "FilmStarActivity";

    private long pk;
    private TextView filmStartitle;
    private LinearLayout indicatorListLayout;
    private RecyclerView recyclerView;

    private ImageView contentArrowLeft;
    private ImageView contentArrowRight;

    private TextView actorView;
    private TextView directorView;
    private TextView areaView;
    private TextView descriptionView;
    private ImageView indicatorArrowLeft;
    private ImageView indicatorArrowRight;

    private View lostFocusView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filmstar);
        initViews();
        Intent intent = getIntent();
        pk = intent.getLongExtra("pk", 0);
//        pk = 1054;
        String title = intent.getStringExtra("title");
//        String title = "刘德华";
        filmStartitle.setText(title);
        fetchActorRelate(pk);

    }

    private void initViews() {
        filmStartitle = (TextView) findViewById(R.id.film_star_title);
        indicatorListLayout = (LinearLayout) findViewById(R.id.film_list_indicator);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        actorView = (TextView) findViewById(R.id.actor);
        directorView = (TextView) findViewById(R.id.director);
        areaView = (TextView) findViewById(R.id.area);
        descriptionView = (TextView) findViewById(R.id.description);
        contentArrowLeft = (ImageView) findViewById(R.id.content_arrow_left);
        contentArrowRight = (ImageView) findViewById(R.id.content_arrow_right);
        indicatorArrowLeft = (ImageView) findViewById(R.id.indicator_left);
        indicatorArrowRight = (ImageView) findViewById(R.id.indicator_right);
        contentArrowRight.setOnFocusChangeListener(this);
        contentArrowLeft.setOnFocusChangeListener(this);
        indicatorArrowLeft.setOnFocusChangeListener(this);
        indicatorArrowRight.setOnFocusChangeListener(this);

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false);

        int verticalSpacingInPixels = (int) (getResources().getDimensionPixelSize(R.dimen.content_fragment_item_vertical_space));
        int horizontalSpacingInPixels = (int) (getResources().getDimensionPixelSize(R.dimen.filmStar_item_horizontal_space));
        recyclerView.addItemDecoration(new StarSpaceItemDecoration(verticalSpacingInPixels, horizontalSpacingInPixels));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setOnFocusChangeListener(this);
    }


    private void fetchActorRelate(final long pk) {
        ActorRelateRequestParams params = new ActorRelateRequestParams();
        params.setActor_id(pk);
        params.setPage_no(1);
        params.setPage_count(100);
        HttpManager.getInstance().resetAdapter_SKY.create(HttpAPI.ActorRelate.class).doRequest(params).enqueue(new Callback<SemanticSearchResponseEntity>() {
            @Override
            public void onResponse(Response<SemanticSearchResponseEntity> response) {
                if (response.errorBody() == null) {
                    SemanticSearchResponseEntity entity = response.body();
                    indicatorListLayout.removeAllViews();
                    int i = 0;
                    for (SemanticSearchResponseEntity.Facet facet : entity.getFacet()) {
                        View itemView = LayoutInflater.from(FilmStarActivity.this).inflate(R.layout.item_film_star_indicator, null);
                        TextView indicatorTitle = (TextView) itemView.findViewById(R.id.title);
                        indicatorTitle.setText(facet.getName());
                        itemView.setOnFocusChangeListener(FilmStarActivity.this);
                        itemView.setOnClickListener(FilmStarActivity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        int marginLeft = (int) (getResources().getDimension(R.dimen.filmStar_indicatorLayout_item_marginLeft));
                        layoutParams.setMargins(marginLeft, 0, 0, 0);
                        itemView.setTag(facet.getContent_type());
                        if (i == 0) {
                            indicatorListLayout.addView(itemView);
                        } else {

                            indicatorListLayout.addView(itemView, layoutParams);
                        }
                        i = i + 1;
                    }
                    fetchActorRelateByType(pk, entity.getFacet().get(0).getContent_type());
                } else {

                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    private void fetchActorRelateByType(long pk, String type) {
        ActorRelateRequestParams params = new ActorRelateRequestParams();
        params.setActor_id(pk);
        params.setPage_no(1);
        params.setPage_count(100);
        params.setContent_type(type);
        HttpManager.getInstance().resetAdapter_SKY.create(HttpAPI.ActorRelate.class).doRequest(params).enqueue(new Callback<SemanticSearchResponseEntity>() {
            @Override
            public void onResponse(Response<SemanticSearchResponseEntity> response) {
                if (response.errorBody() == null) {
                    SemanticSearchResponseEntity entity = response.body();
                    recyclerView.setAdapter(new RecyclerAdapter(entity.getFacet().get(0).getObjects()));
                } else {

                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.filmStar_indicator_item:
                ImageView bg = (ImageView) v.findViewById(R.id.indicator_bg);
                TextView textView = (TextView) v.findViewById(R.id.title);

                if (hasFocus) {
                    textView.setTextColor(getResources().getColor(R.color._ff9c3c));
                    bg.setVisibility(View.VISIBLE);
                    ViewScaleUtil.scaleToLarge(v, 1.3f);
                } else {
                    textView.setTextColor(getResources().getColor(R.color._a6a6a6));
                    bg.setVisibility(View.INVISIBLE);
                    ViewScaleUtil.scaleToNormal(v, 1.3f);
                }
                break;
            case R.id.content_arrow_left:
            case R.id.content_arrow_right:
            case R.id.indicator_left:
            case R.id.indicator_right:
                if (hasFocus) {
                    ViewScaleUtil.scaleToLarge(v, 1.3f);
                } else {
                    ViewScaleUtil.scaleToNormal(v, 1.3f);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filmStar_indicator_item:
                String type = (String) v.getTag();
                fetchActorRelateByType(pk, type);
                break;
        }
    }


    private class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener, OnFocusChangeListener {
        private List<SemantichObjectEntity> datas;

        public RecyclerAdapter(List<SemantichObjectEntity> objectEntities) {
            if (objectEntities.size() <= 8) {
                contentArrowLeft.setVisibility(View.GONE);
                contentArrowRight.setVisibility(View.GONE);
            } else {
                contentArrowLeft.setVisibility(View.VISIBLE);
                contentArrowRight.setVisibility(View.VISIBLE);
            }
            this.datas = objectEntities;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(FilmStarActivity.this).inflate(R.layout.item_recycler, viewGroup, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int postion) {

            myViewHolder.textView.setText(datas.get(postion).getTitle());
            String postUrl = datas.get(postion).getPoster_url();
            String verticalUrl = datas.get(postion).getVertical_url();
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("url", datas.get(postion).getUrl());
            hashMap.put("content_model", datas.get(postion).getContent_model());
            hashMap.put("pk", datas.get(postion).getPk());
            hashMap.put("title", datas.get(postion).getTitle());
            hashMap.put("attr", new Gson().toJson(datas.get(postion).getAttributes()));

            if (!TextUtils.isEmpty(verticalUrl)) {
                hashMap.put("orientation", "vertical");
                myViewHolder.imageView.setIsHorizontal(false);
                Picasso.with(FilmStarActivity.this)
                        .load(datas.get(postion).getVertical_url())
                        .memoryPolicy(MemoryPolicy.NO_STORE)
                        .error(R.drawable.vertical_preview_bg)
                        .placeholder(R.drawable.vertical_preview_bg)
                        .into(myViewHolder.imageView);

            } else if (!TextUtils.isEmpty(postUrl)) {
                hashMap.put("orientation", "horizontal");
                myViewHolder.imageView.setIsHorizontal(true);
                Picasso.with(FilmStarActivity.this).load(postUrl)
                        .memoryPolicy(MemoryPolicy.NO_STORE)
                        .error(R.drawable.horizontal_preview_bg)
                        .placeholder(R.drawable.horizontal_preview_bg)
                        .into(myViewHolder.imageView);

            } else {
                hashMap.put("orientation", "vertical");
                myViewHolder.imageView.setIsHorizontal(false);
                Picasso.with(FilmStarActivity.this)
                        .load(R.drawable.vertical_preview_bg)
                        .memoryPolicy(MemoryPolicy.NO_STORE)
                        .placeholder(R.drawable.vertical_preview_bg)
                        .error(R.drawable.vertical_preview_bg)
                        .into(myViewHolder.imageView);
            }
            myViewHolder.mItemView.setTag(hashMap);
            myViewHolder.mItemView.setOnClickListener(this);
            myViewHolder.mItemView.setOnFocusChangeListener(this);
            if (postion == 0) {
                myViewHolder.mItemView.requestFocusFromTouch();
                myViewHolder.mItemView.requestFocus();
            }
        }


        @Override
        public int getItemCount() {
            return datas.size();
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

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            HashMap<String, String> tag = (HashMap) v.getTag();
            AttributesEntity attributesEntity = new Gson().fromJson(tag.get("attr"), AttributesEntity.class);
            ImageView imageView = (ImageView) v.findViewById(R.id.image);
            TextView textView = (TextView) v.findViewById(R.id.id_number);
            if (hasFocus) {
                setFilmAttr(attributesEntity);
                textView.setSelected(true);
                imageView.setBackgroundResource(R.drawable.item_focus);
                ViewScaleUtil.scaleToLarge(v, 1.15f);
            } else {
                textView.setSelected(false);
                imageView.setBackgroundDrawable(null);
                ViewScaleUtil.scaleToNormal(v, 1.15f);
            }
        }
    }

    private void setFilmAttr(AttributesEntity attributesEntity) {
        actorView.setText("演员 : ");
        descriptionView.setText("导演 : ");
        areaView.setText("国家/地区 : ");
        descriptionView.setText("影片介绍 : ");
        if (attributesEntity.getActor() != null && attributesEntity.getActor().length != 0) {
            for (String[] strings : attributesEntity.getActor()) {
                actorView.append(strings[1] + " ");
            }
        } else if (attributesEntity.getAttendee() != null && attributesEntity.getAttendee().length != 0) {
            for (String[] strings : attributesEntity.getAttendee()) {
                actorView.append(strings[1] + " ");
            }
        } else {
            actorView.setVisibility(View.GONE);
        }

        if (attributesEntity.getDirector() != null && attributesEntity.getDirector().length != 0) {
            for (String[] strings : attributesEntity.getDirector()) {
                directorView.append(strings[1] + " ");
            }
        } else {
            directorView.setVisibility(View.GONE);
        }

        if (attributesEntity.getArea() != null && attributesEntity.getArea().length != 0) {
            areaView.append(attributesEntity.getArea()[1] + " ");
        } else {
            areaView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(attributesEntity.getDescription())) {
            descriptionView.append(attributesEntity.getDescription() + " ");
        } else {
            descriptionView.setVisibility(View.GONE);
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private RelectionImageView imageView;
        private View mItemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
            textView = (TextView) itemView.findViewById(R.id.id_number);
            imageView = (RelectionImageView) itemView.findViewById(R.id.image);
//            imageView.setLrPaddingFlag(1);

        }
    }

}
