//package cn.ismartv.voice.ui.fragment;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.google.gson.JsonParser;
//import com.squareup.picasso.MemoryPolicy;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Transformation;
//
//import java.util.List;
//
//import cn.ismartv.imagereflection.ReflectionTransformationBuilder;
//import cn.ismartv.imagereflection.RelectionImageView;
//import cn.ismartv.voice.R;
//import cn.ismartv.voice.data.http.SemantichObjectEntity;
//import cn.ismartv.voice.util.ViewScaleUtil;
//
///**
// * Created by huaijie on 1/18/16.
// */
//public class ContentFragment2 extends BaseFragment implements View.OnFocusChangeListener, OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
//    private static final String TAG = "ContentFragment";
//    private GridView recyclerView;
//    private TextView searchTitle;
//
//    private View lostFocusItemView;
//    private ImageView arrowUp;
//    private ImageView arrowDown;
//
//    private View lastItemFocusView;
//
//
//    private List<SemantichObjectEntity> objectEntities;
//    private String raw;
//
//    public void setObjectEntities(List<SemantichObjectEntity> objectEntities) {
//        this.objectEntities = objectEntities;
//    }
//
//    public void setRaw(String raw) {
//        this.raw = raw;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_content_2, null);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        recyclerView = (GridView) view.findViewById(R.id.recyclerview);
//        searchTitle = (TextView) view.findViewById(R.id.search_title);
//        arrowUp = (ImageView) view.findViewById(R.id.arrow_up);
//        arrowDown = (ImageView) view.findViewById(R.id.arrow_down);
//        arrowUp.setOnClickListener(this);
//        arrowDown.setOnClickListener(this);
//        arrowUp.bringToFront();
//
//        recyclerView.setOnFocusChangeListener(this);
//
//        notifyDataChanged(objectEntities, raw);
//
//    }
//
//
//    public void notifyDataChanged(List<SemantichObjectEntity> entities, String data) {
//        String rawTextValue = getString(R.string.search_title);
//        String rawText = new JsonParser().parse(data).getAsJsonObject().get("raw_text").toString();
//        searchTitle.setText(String.format(rawTextValue, rawText));
//        recyclerView.setAdapter(new GridAdapter(entities, getContext()));
//        //first item request focus
//    }
//
//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        switch (v.getId()) {
//            case R.id.recyclerview:
//                Log.e(TAG, "grid view focus: " + hasFocus);
//                if (hasFocus) {
//                    recyclerView.setSelection(-1);
//                    View currentFocusView = recyclerView.getChildAt(0);
//                    if (currentFocusView != null) {
//                        ImageView imageView = (ImageView) currentFocusView.findViewById(R.id.image);
//                        TextView textView = (TextView) currentFocusView.findViewById(R.id.id_number);
//                        textView.setSelected(true);
//                        imageView.setSelected(true);
//                        ViewScaleUtil.scaleOut1(currentFocusView);
//                        lastItemFocusView = currentFocusView;
//                    }
//
//                } else {
//                    if (lastItemFocusView != null) {
//                        ImageView imageView = (ImageView) lastItemFocusView.findViewById(R.id.image);
//                        TextView textView = (TextView) lastItemFocusView.findViewById(R.id.id_number);
//                        textView.setSelected(false);
//                        imageView.setSelected(false);
//                        ViewScaleUtil.scaleIn1(lastItemFocusView);
//                    }
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
//
//
//    }
//
//    private class GridAdapter extends BaseAdapter {
//        private Context context;
//        private List<SemantichObjectEntity> datas;
//
//        public GridAdapter(List<SemantichObjectEntity> objectEntities, Context context) {
//            this.context = context;
//            if (objectEntities.size() > 8) {
//                arrowDown.setVisibility(View.VISIBLE);
//            } else {
//                arrowDown.setVisibility(View.GONE);
//            }
//            this.datas = objectEntities;
//        }
//
//        @Override
//        public int getCount() {
//            return datas.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return datas.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder myViewHolder;
//            if (convertView == null) {
//                convertView = LayoutInflater.from(context).inflate(R.layout.item_recycler_2, null);
//                myViewHolder = new ViewHolder();
//                myViewHolder.imageView = (RelectionImageView) convertView.findViewById(R.id.image);
//                myViewHolder.textView = (TextView) convertView.findViewById(R.id.id_number);
//                convertView.setTag(myViewHolder);
//            } else {
//                myViewHolder = (ViewHolder) convertView.getTag();
//            }
//
//            myViewHolder.textView.setText(datas.get(position).getTitle());
//            String postUrl = datas.get(position).getPoster_url();
//            String verticalUrl = datas.get(position).getVertical_url();
//            String focusString = datas.get(position).getFocus();
//
//            float price = 0;
//            if (!TextUtils.isEmpty(focusString) && focusString.length() > 10) {
//                focusString = focusString.subSequence(0, 10).toString();
//            }
//
//            String score = datas.get(position).getBean_score();
//            SemantichObjectEntity.Expense expense = datas.get(position).getExpense();
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
//                myViewHolder.imageView.setIsHorizontal(false);
//                Picasso.with(getContext())
//                        .load(datas.get(position).getVertical_url())
//                        .memoryPolicy(MemoryPolicy.NO_STORE)
//                        .error(R.drawable.vertical_preview_bg)
//                        .placeholder(R.drawable.vertical_preview_bg)
//                        .into(myViewHolder.imageView);
//
//            } else if (!TextUtils.isEmpty(postUrl)) {
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
//                myViewHolder.imageView.setIsHorizontal(false);
//                Picasso.with(getContext())
//                        .load(R.drawable.vertical_preview_bg)
//                        .memoryPolicy(MemoryPolicy.NO_STORE)
//                        .placeholder(R.drawable.vertical_preview_bg)
//                        .error(R.drawable.vertical_preview_bg)
//                        .into(myViewHolder.imageView);
//            }
//            return convertView;
//        }
//
//
//    }
//
//
//    private class ViewHolder {
//        private TextView textView;
//        private RelectionImageView imageView;
//    }
//
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        SemantichObjectEntity objectEntity = (SemantichObjectEntity) recyclerView.getAdapter().getItem(position);
//        String postUrl = objectEntity.getPoster_url();
//        String verticalUrl = objectEntity.getVertical_url();
//        String url = objectEntity.getUrl();
//        String contentModel = objectEntity.getContent_model();
//        long pk = Long.parseLong(objectEntity.getPk());
//        String title = objectEntity.getTitle();
//
//        Intent intent = new Intent();
//        switch (contentModel) {
//            case "person":
//
//                intent.putExtra("pk", pk);
//                intent.putExtra("title", title);
//                intent.setAction("cn.ismartv.voice.film_star");
//                startActivity(intent);
//                break;
//            default:
//                if (!TextUtils.isEmpty(url)) {
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    intent.putExtra("url", url);
//                    if (!TextUtils.isEmpty(verticalUrl)) {
//                        intent.setAction("tv.ismar.daisy.PFileItem");
//                    } else {
//                        intent.setAction("tv.ismar.daisy.Item");
//                    }
//                    startActivity(intent);
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View currentFocusView, int position, long id) {
//        Log.e(TAG, "selected position: " + position);
//        if (currentFocusView == null) {
//            currentFocusView = recyclerView.getChildAt(0);
//        }
//
//        if (lastItemFocusView != null) {
//            ImageView imageView = (ImageView) lastItemFocusView.findViewById(R.id.image);
//            TextView textView = (TextView) lastItemFocusView.findViewById(R.id.id_number);
//            textView.setSelected(false);
//            imageView.setSelected(false);
//            ViewScaleUtil.scaleIn1(lastItemFocusView);
//        }
//
//        if (currentFocusView != null) {
//            ImageView imageView = (ImageView) currentFocusView.findViewById(R.id.image);
//            TextView textView = (TextView) currentFocusView.findViewById(R.id.id_number);
//            textView.setSelected(true);
//            imageView.setSelected(true);
//            ViewScaleUtil.scaleOut1(currentFocusView);
//        }
//        lastItemFocusView = currentFocusView;
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//    }
//
//
//}