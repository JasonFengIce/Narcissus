//package cn.ismartv.voice.ui.fragment;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.google.gson.JsonParser;
//import com.squareup.picasso.MemoryPolicy;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Transformation;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.util.HashMap;
//import java.util.List;
//
//import cn.ismartv.imagereflection.ReflectionTransformationBuilder;
//import cn.ismartv.imagereflection.RelectionImageView;
//import cn.ismartv.recyclerview.widget.GridLayoutManager;
//import cn.ismartv.recyclerview.widget.RecyclerView;
//import cn.ismartv.voice.R;
//import cn.ismartv.voice.core.event.AnswerAvailableEvent;
//import cn.ismartv.voice.core.http.HttpAPI;
//import cn.ismartv.voice.core.http.HttpManager;
//import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
//import cn.ismartv.voice.data.http.SemantichObjectEntity;
//import cn.ismartv.voice.data.http.SharpHotWordsEntity;
//import cn.ismartv.voice.ui.SpaceItemDecoration;
//import cn.ismartv.voice.util.ViewScaleUtil;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//
///**
// * Created by huaijie on 1/18/16.
// */
//public class ContentFragment extends BaseFragment implements View.OnFocusChangeListener, OnClickListener {
//    private static final String TAG = "ContentFragment";
//    private RecyclerView recyclerView;
//    private TextView searchTitle;
//
//    private View lostFocusItemView;
//    private ImageView arrowUp;
//    private ImageView arrowDown;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_content, null);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
//        searchTitle = (TextView) view.findViewById(R.id.search_title);
//        arrowUp = (ImageView) view.findViewById(R.id.arrow_up);
//        arrowDown = (ImageView) view.findViewById(R.id.arrow_down);
//        arrowUp.setOnClickListener(this);
//        arrowDown.setOnClickListener(this);
//        arrowUp.bringToFront();
//        fetchSharpHotWords();
//
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false);
//
//        int verticalSpacingInPixels = (int) (getResources().getDimension(R.dimen.content_fragment_item_vertical_space) - getResources().getDimension(R.dimen.item_margin_bottom) * 1.55);
//        int horizontalSpacingInPixels = (int) (getResources().getDimension(R.dimen.content_fragment_item_horizontal_space));
//        recyclerView.addItemDecoration(new SpaceItemDecoration(verticalSpacingInPixels, horizontalSpacingInPixels));
//        recyclerView.setLayoutManager(gridLayoutManager);
//        recyclerView.setOnFocusChangeListener(this);
//    }
//
//
//    public void notifyDataChanged(SemanticSearchResponseEntity responseEntity, String data) {
//        String rawTextValue = getString(R.string.search_title);
//        String rawText = new JsonParser().parse(data).getAsJsonObject().get("raw_text").toString();
//        searchTitle.setText(String.format(rawTextValue, rawText));
//        recyclerView.setAdapter(new RecyclerAdapter(responseEntity.getFacet().get(0).getObjects()));
//        //first item request focus
//    }
//
//
//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        switch (v.getId()) {
//            case R.id.recyclerview:
//                if (lostFocusItemView != null) {
//                    lostFocusItemView.requestFocus();
//                }
//                break;
//        }
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.arrow_up:
//                recyclerView.smoothScrollBy(0, -recyclerView.getHeight());
//                break;
//            case R.id.arrow_down:
//                recyclerView.smoothScrollBy(0, recyclerView.getHeight());
//                break;
//        }
//    }
//
//
//    private class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> implements OnClickListener, View.OnFocusChangeListener {
//        private List<SemantichObjectEntity> datas;
//
//        public RecyclerAdapter(List<SemantichObjectEntity> objectEntities) {
//            if (objectEntities.size() > 8) {
//                arrowDown.setVisibility(View.VISIBLE);
//            } else {
//                arrowDown.setVisibility(View.GONE);
//            }
//            this.datas = objectEntities;
//        }
//
//        @Override
//        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_recycler, viewGroup, false));
//            return holder;
//        }
//
//        @Override
//        public void onBindViewHolder(MyViewHolder myViewHolder, int postion) {
//            myViewHolder.textView.setText(datas.get(postion).getTitle());
//            String postUrl = datas.get(postion).getPoster_url();
//            String verticalUrl = datas.get(postion).getVertical_url();
//
//
//            HashMap<String, String> hashMap = new HashMap<>();
//            hashMap.put("url", datas.get(postion).getUrl());
//            hashMap.put("content_model", datas.get(postion).getContent_model());
//            hashMap.put("pk", datas.get(postion).getPk());
//            hashMap.put("title", datas.get(postion).getTitle());
//            String focusString = datas.get(postion).getFocus();
//
//            float price = 0;
//            if (!TextUtils.isEmpty(focusString) && focusString.length() > 10) {
//                focusString = focusString.subSequence(0, 10).toString();
//            }
//
//            String score = datas.get(postion).getBean_score();
//            SemantichObjectEntity.Expense expense = datas.get(postion).getExpense();
//            if (expense != null) {
//                price = expense.getPrice();
//            }
//
//            myViewHolder.imageView.setFoucsText(focusString);
//            if (!TextUtils.isEmpty(verticalUrl)) {
//                if (!TextUtils.isEmpty(score)) {
//                    myViewHolder.imageView.setScore(score);
//                }
//
//                if (price != 0) {
//                    myViewHolder.imageView.setPrice("￥" + price);
//                }
//
//                hashMap.put("orientation", "vertical");
//                myViewHolder.imageView.setIsHorizontal(false);
//                Picasso.with(getContext())
//                        .load(datas.get(postion).getVertical_url())
//                        .memoryPolicy(MemoryPolicy.NO_STORE)
//                        .error(R.drawable.vertical_preview_bg)
//                        .placeholder(R.drawable.vertical_preview_bg)
//                        .into(myViewHolder.imageView);
//
//            } else if (!TextUtils.isEmpty(postUrl)) {
//                hashMap.put("orientation", "horizontal");
//                myViewHolder.imageView.setIsHorizontal(true);
//                Transformation mTransformation = new ReflectionTransformationBuilder()
//                        .setIsHorizontal(true)
//                        .build();
//
//                Picasso.with(getContext()).load(postUrl)
//                        .memoryPolicy(MemoryPolicy.NO_STORE)
//                        .error(R.drawable.vertical_preview_bg)
//                        .placeholder(R.drawable.vertical_preview_bg)
//                        .transform(mTransformation)
//                        .into(myViewHolder.imageView);
//
//            } else {
//                hashMap.put("orientation", "vertical");
//                myViewHolder.imageView.setIsHorizontal(false);
//                Picasso.with(getContext())
//                        .load(R.drawable.vertical_preview_bg)
//                        .memoryPolicy(MemoryPolicy.NO_STORE)
//                        .placeholder(R.drawable.vertical_preview_bg)
//                        .error(R.drawable.vertical_preview_bg)
//                        .into(myViewHolder.imageView);
//            }
//            myViewHolder.mItemView.setTag(hashMap);
//            myViewHolder.mItemView.setOnClickListener(this);
//            myViewHolder.mItemView.setOnFocusChangeListener(this);
//            if (postion == 0) {
//                lostFocusItemView = myViewHolder.mItemView;
//                myViewHolder.mItemView.requestFocusFromTouch();
//                myViewHolder.mItemView.requestFocus();
//            }
//
//            if (postion >= 8) {
//                arrowUp.setVisibility(View.VISIBLE);
//            } else {
//                arrowUp.setVisibility(View.GONE);
//            }
//
//            if (postion == datas.size() - 1) {
//                arrowDown.setVisibility(View.GONE);
//            } else {
//                arrowDown.setVisibility(View.VISIBLE);
//            }
//
//        }
//
//
//        @Override
//        public int getItemCount() {
//            return datas.size();
//        }
//
//
//        @Override
//        public void onClick(View v) {
//            HashMap<String, String> tag = (HashMap) v.getTag();
//            String url = tag.get("url");
//            String contentModel = tag.get("content_model");
//            Long pk = Long.parseLong(tag.get("pk"));
//            String title = tag.get("title");
//            Intent intent = new Intent();
//            switch (contentModel) {
//                case "person":
//                    intent.putExtra("pk", pk);
//                    intent.putExtra("title", title);
//                    intent.setAction("cn.ismartv.voice.film_star");
//                    startActivity(intent);
//
//                    break;
//                default:
//                    if (!TextUtils.isEmpty(url)) {
//
//                        intent.putExtra("url", url);
//
//                        switch (tag.get("orientation")) {
//                            case "horizontal":
//                                intent.setAction("tv.ismar.daisy.Item");
//                                break;
//                            case "vertical":
//                                intent.setAction("tv.ismar.daisy.PFileItem");
//                                break;
//                        }
//                        startActivity(intent);
//                    }
//                    break;
//            }
//
//        }
//
//        @Override
//        public void onFocusChange(View v, boolean hasFocus) {
//            ImageView imageView = (ImageView) v.findViewById(R.id.image);
//            TextView textView = (TextView) v.findViewById(R.id.id_number);
//            if (hasFocus) {
//                textView.setSelected(true);
//                imageView.setBackgroundResource(R.drawable.item_focus);
//                ViewScaleUtil.scaleToLarge(v, 1.15f);
//            } else {
//                lostFocusItemView = v;
//                textView.setSelected(false);
//                imageView.setBackgroundDrawable(null);
//                ViewScaleUtil.scaleToNormal(v, 1.15f);
//            }
//        }
//    }
//
//    private class MyViewHolder extends RecyclerView.ViewHolder {
//        private TextView textView;
//        private RelectionImageView imageView;
//        private View mItemView;
//
//        public MyViewHolder(View itemView) {
//            super(itemView);
//            this.mItemView = itemView;
//            textView = (TextView) itemView.findViewById(R.id.id_number);
//            imageView = (RelectionImageView) itemView.findViewById(R.id.image);
//
//        }
//    }
//
//    public void setSearchTitle() {
//        searchTitle.setText(getString(R.string.recommend_content_title));
//    }
//
//    public void fetchSharpHotWords() {
//        Retrofit retrofit = HttpManager.getInstance().resetAdapter_WUGUOJUN;
//        retrofit.create(HttpAPI.SharpHotWords.class).doRequest(8).enqueue(new Callback<SharpHotWordsEntity>() {
//            @Override
//            public void onResponse(Response<SharpHotWordsEntity> response) {
//                if (response.errorBody() == null) {
//                    recyclerView.setAdapter(new RecyclerAdapter(response.body().getObjects()));
//                } else {
//                    EventBus.getDefault().post(new AnswerAvailableEvent());
//                }
//
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                EventBus.getDefault().post(new AnswerAvailableEvent());
//            }
//        });
//
//    }
//}
