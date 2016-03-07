package cn.ismartv.voice.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.core.ScreenManager;
import cn.ismartv.voice.core.event.AnswerAvailableEvent;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.http.ActorRelateRequestParams;
import cn.ismartv.voice.data.http.AttributesEntity;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.data.http.SemantichObjectEntity;
import cn.ismartv.voice.ui.widget.MessagePopWindow;
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
    private LinearLayout vodListView;
    private HorizontalScrollView vodHorizontalScrollView;

    private ImageView contentArrowLeft;
    private ImageView contentArrowRight;

    private TextView actorView;
    private TextView directorView;
    private TextView areaView;
    private TextView descriptionView;
    private ImageView indicatorArrowLeft;
    private ImageView indicatorArrowRight;

    private View lostFocusItemView;

    private MessagePopWindow networkEorrorPopupWindow;
    private View contentView;
    private View indicatorSelectedView;
    private HorizontalScrollView horizontalScrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenManager.getScreenManager().pushActivity(this);
        networkEorrorPopupWindow = new MessagePopWindow(this, "网络异常，请检查网络", null);
        contentView = LayoutInflater.from(this).inflate(R.layout.activity_filmstar, null);
        setContentView(contentView);

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        window.setAttributes(params);

        initViews();
        Intent intent = getIntent();
        pk = intent.getLongExtra("pk", 0);
//        pk = 2857;
        String title = intent.getStringExtra("title");
//        String title = "刘德华";
        filmStartitle.setText(title);
        fetchActorRelate(pk);

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        networkEorrorPopupWindow = null;
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ScreenManager.getScreenManager().popActivity(this);

        super.onDestroy();
    }

    private void initViews() {
        filmStartitle = (TextView) findViewById(R.id.film_star_title);
        indicatorListLayout = (LinearLayout) findViewById(R.id.film_list_indicator);
        actorView = (TextView) findViewById(R.id.actor);
        directorView = (TextView) findViewById(R.id.director);
        areaView = (TextView) findViewById(R.id.area);
        descriptionView = (TextView) findViewById(R.id.description);
        contentArrowLeft = (ImageView) findViewById(R.id.content_arrow_left);
        contentArrowRight = (ImageView) findViewById(R.id.content_arrow_right);
        indicatorArrowLeft = (ImageView) findViewById(R.id.indicator_left);
        indicatorArrowRight = (ImageView) findViewById(R.id.indicator_right);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.scrollview);
        vodListView = (LinearLayout) findViewById(R.id.vod_list_view);
        vodHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.vod_scrollview);

        contentArrowRight.setOnFocusChangeListener(this);
        contentArrowLeft.setOnFocusChangeListener(this);
        indicatorArrowLeft.setOnFocusChangeListener(this);
        indicatorArrowRight.setOnFocusChangeListener(this);


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
//                        int marginLeft = (int) (getResources().getDimension(R.dimen.filmStar_indicatorLayout_item_marginLeft));
//                        layoutParams.setMargins(marginLeft, 0, 0, 0);
                        itemView.setTag(facet.getContent_type());
                        itemView.setTag(R.id.filmStar_indicator_item, i);
                        if (i == 0) {
                            indicatorSelectedView = itemView;
                            indicatorTitle.setTextColor(getResources().getColor(R.color._ffffff));
                            itemView.setNextFocusLeftId(itemView.getId());
                            indicatorListLayout.addView(itemView);
                        } else {
                            if (i == entity.getFacet().size() - 1) {
                                itemView.setNextFocusRightId(itemView.getId());
                            }
                            indicatorListLayout.addView(itemView, layoutParams);
                        }
                        i = i + 1;
                    }

                    if (entity.getFacet().size() > 4) {
                        indicatorArrowRight.setVisibility(View.VISIBLE);
                    }
                    fetchActorRelateByType(pk, entity.getFacet().get(0).getContent_type());
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


    private void fetchActorRelateByType(long pk, String type) {
        ActorRelateRequestParams params = new ActorRelateRequestParams();
        params.setActor_id(pk);
        params.setPage_no(1);
        params.setPage_count(30);
        params.setContent_type(type);
        HttpManager.getInstance().resetAdapter_SKY.create(HttpAPI.ActorRelate.class).doRequest(params).enqueue(new Callback<SemanticSearchResponseEntity>() {
            @Override
            public void onResponse(Response<SemanticSearchResponseEntity> response) {
                if (response.errorBody() == null) {
                    SemanticSearchResponseEntity entity = response.body();
                    fillVodList(entity.getFacet().get(0).getObjects());
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

    private void fillVodList(List<SemantichObjectEntity> list) {
        if (list.size() > 5) {
            contentArrowRight.setVisibility(View.VISIBLE);
        } else {
            contentArrowRight.setVisibility(View.INVISIBLE);
        }
        vodListView.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_vod_star, null);
            TextView itemVodTitle = (TextView) itemView.findViewById(R.id.item_vod_title);
            ImageView itemVodImage = (ImageView) itemView.findViewById(R.id.item_vod_image);
            TextView itemScore = (TextView) itemView.findViewById(R.id.item_vod_score);
            TextView itemPrice = (TextView) itemView.findViewById(R.id.item_vod_price);
            TextView itemFocus = (TextView) itemView.findViewById(R.id.item_vod_focus);

            itemVodTitle.setText(list.get(i).getTitle());
            Transformation mTransformation = new cn.ismartv.voice.ui.ReflectionTransformationBuilder()
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
                Picasso.with(this)
                        .load(verticalUrl)
                        .memoryPolicy(MemoryPolicy.NO_STORE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .error(R.drawable.vertical_preview_bg)
                        .placeholder(R.drawable.vertical_preview_bg)
                        .transform(mTransformation)
                        .into(itemVodImage);
            } else {
                Picasso.with(this)
                        .load(horizontalUrl)
                        .memoryPolicy(MemoryPolicy.NO_STORE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .error(R.drawable.vertical_preview_bg)
                        .placeholder(R.drawable.vertical_preview_bg)
                        .transform(mTransformation)
                        .into(itemVodImage);
            }

            itemView.setTag(list.get(i));
            if (i == list.size() - 1) {
                itemView.setNextFocusRightId(itemView.getId());
            }

            itemView.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    View imageViewLayout = v.findViewById(R.id.item_vod_image_layout);
                    TextView textView = (TextView) v.findViewById(R.id.item_vod_title);
                    if (hasFocus) {
                        SemantichObjectEntity entity = (SemantichObjectEntity) v.getTag();
                        AttributesEntity attributesEntity = entity.getAttributes();
                        String description = entity.getDescription();
                        setFilmAttr(attributesEntity, description);
                        textView.setSelected(true);
                        imageViewLayout.setSelected(true);
                        ViewScaleUtil.zoomin_1_15(v);
                    } else {
                        textView.setSelected(false);
                        imageViewLayout.setSelected(false);
                        ViewScaleUtil.zoomout_1_15(v);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FilmStarActivity.this.onVodItemClick((SemantichObjectEntity) v.getTag());
                }
            });
            itemView.setOnFocusChangeListener(this);

            int padding = (int) getResources().getDimension(R.dimen.filmStar_item_horizontal_space);
            int verticalPadding = (int) getResources().getDimension(R.dimen.filmStar_item_vertical_space);
            itemView.setPadding(padding, verticalPadding, padding, verticalPadding);


            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
//            layoutParams.setMargins(padding, 0, padding, 0);
            vodListView.addView(itemView, layoutParams);
        }
        vodListView.getChildAt(0).requestFocus();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.filmStar_indicator_item:
                int position = (int) v.getTag(R.id.filmStar_indicator_item);
                Log.i(TAG, "indicator position: " + position);

                ImageView bg = (ImageView) v.findViewById(R.id.indicator_bg);
                TextView textView = (TextView) v.findViewById(R.id.title);

                if (hasFocus) {
                    if (indicatorSelectedView == v) {
                        textView.setTextColor(getResources().getColor(R.color._ffffff));
                    } else {
                        textView.setTextColor(getResources().getColor(R.color._ff9c3c));
                    }

                    bg.setVisibility(View.VISIBLE);
                    ViewScaleUtil.zoomin_1_3(v);
                } else {
                    if (indicatorSelectedView == v) {
                        textView.setTextColor(getResources().getColor(R.color._ffffff));
                    } else {
                        textView.setTextColor(getResources().getColor(R.color._a6a6a6));
                    }

                    bg.setVisibility(View.INVISIBLE);
                    ViewScaleUtil.zoomout_1_3(v);
                }

                int scrollX = horizontalScrollView.getScrollX();
                int itemWidth = (int) getResources().getDimension(R.dimen.film_star_indicator_bg_width);
                if (scrollX >= itemWidth) {
                    indicatorArrowLeft.setVisibility(View.VISIBLE);
                } else {
                    indicatorArrowLeft.setVisibility(View.INVISIBLE);
                }

                int count = indicatorListLayout.getChildCount();
                if (scrollX >= (count - 4) * itemWidth) {
                    indicatorArrowRight.setVisibility(View.INVISIBLE);
                } else {
                    indicatorArrowRight.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.content_arrow_left:
            case R.id.content_arrow_right:
            case R.id.indicator_left:
            case R.id.indicator_right:
                if (hasFocus) {
                    ViewScaleUtil.zoomin_1_3(v);
                } else {
                    ViewScaleUtil.zoomout_1_3(v);
                }
                break;
            case R.id.recyclerview:
                if (lostFocusItemView != null) {
                    lostFocusItemView.requestFocus();
                }
                break;
            case R.id.star_vod_list_item:
                View imageLayout = v.findViewById(R.id.item_vod_image_layout);
                if (hasFocus) {
                    imageLayout.setSelected(true);
                } else {
                    imageLayout.setSelected(false);
                }

                int vodScrollX = vodHorizontalScrollView.getScrollX();
                if (vodScrollX >= 312) {
                    contentArrowLeft.setVisibility(View.VISIBLE);
                } else {
                    contentArrowLeft.setVisibility(View.INVISIBLE);
                }

                int vodCount = vodListView.getChildCount();
                if (vodScrollX >= (vodCount - 5 - 1) * 332 + 312) {
                    contentArrowRight.setVisibility(View.INVISIBLE);
                } else {
                    contentArrowRight.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filmStar_indicator_item:
                if (indicatorSelectedView != null) {
                    TextView textView = (TextView) indicatorSelectedView.findViewById(R.id.title);
                    textView.setTextColor(getResources().getColor(R.color._a6a6a6));
                } else {
                    TextView textView = (TextView) v.findViewById(R.id.title);
                    textView.setTextColor(getResources().getColor(R.color._ffffff));
                }
                indicatorSelectedView = v;
                String type = (String) v.getTag();
                fetchActorRelateByType(pk, type);
                break;
        }
    }


    private void setFilmAttr(AttributesEntity attributesEntity, String description) {
        actorView.setText("演员 : ");
        directorView.setText("导演 : ");
        areaView.setText("国家/地区 : ");
        descriptionView.setText("影片介绍 : ");
        if (attributesEntity.getActor() != null && attributesEntity.getActor().length != 0) {
            for (Object[] strings : attributesEntity.getActor()) {
                actorView.setVisibility(View.VISIBLE);
                actorView.append(strings[1] + " ");
            }
        } else if (attributesEntity.getAttendee() != null && attributesEntity.getAttendee().length != 0) {
            for (Object[] strings : attributesEntity.getAttendee()) {
                actorView.setVisibility(View.VISIBLE);
                actorView.append(strings[1] + " ");
            }
        } else {
            actorView.setVisibility(View.GONE);
        }

        if (attributesEntity.getDirector() != null && attributesEntity.getDirector().length != 0) {
            for (Object[] strings : attributesEntity.getDirector()) {
                directorView.setVisibility(View.VISIBLE);
                directorView.append(strings[1] + " ");
            }
        } else {
            directorView.setVisibility(View.GONE);
        }

        if (attributesEntity.getArea() != null && attributesEntity.getArea().length != 0) {
            areaView.setVisibility(View.VISIBLE);
            areaView.append(attributesEntity.getArea()[1] + " ");
        } else {
            areaView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(description)) {
            descriptionView.setVisibility(View.VISIBLE);
            descriptionView.append(description + " ");
        } else {
            descriptionView.setVisibility(View.GONE);
        }
    }


    public void showNetworkErrorPop() {
        networkEorrorPopupWindow = new MessagePopWindow(this, "网络异常，请检查网络", null);
        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!networkEorrorPopupWindow.isShowing()) {
                    networkEorrorPopupWindow.showAtLocation(contentView, Gravity.CENTER, new MessagePopWindow.ConfirmListener() {
                                @Override
                                public void confirmClick(View view) {
                                    networkEorrorPopupWindow.dismiss();
                                    ScreenManager.getScreenManager().popAllActivityExceptOne(null);
                                }
                            },
                            null
                    );
                }
            }
        }, 1000);
    }


    @Subscribe
    public void answerAvailable(AnswerAvailableEvent event) {
        showNetworkErrorPop();
    }


}
