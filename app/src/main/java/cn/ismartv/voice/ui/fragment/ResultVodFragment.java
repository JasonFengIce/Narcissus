package cn.ismartv.voice.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonParser;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.data.http.SemantichObjectEntity;
import cn.ismartv.voice.ui.ReflectionTransformationBuilder;
import cn.ismartv.voice.ui.widget.ZGridView;

/**
 * Created by huaijie on 1/18/16.
 */
public class ResultVodFragment extends BaseFragment implements View.OnFocusChangeListener, OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "ResultVodFragment";
    private ZGridView recyclerView;
    private TextView searchTitle;

    private ImageView arrowUp;
    private ImageView arrowDown;


    private List<SemantichObjectEntity> objectEntities;
    private String raw;

    public void setObjectEntities(List<SemantichObjectEntity> objectEntities) {
        this.objectEntities = objectEntities;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content_3, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (ZGridView) view.findViewById(R.id.recyclerview);
        searchTitle = (TextView) view.findViewById(R.id.search_title);
        arrowUp = (ImageView) view.findViewById(R.id.arrow_up);
        arrowDown = (ImageView) view.findViewById(R.id.arrow_down);
        arrowUp.setOnClickListener(this);
        arrowDown.setOnClickListener(this);
        arrowUp.bringToFront();

        recyclerView.setOnItemSelectedListener(this);
        recyclerView.setOnItemClickListener(this);

        notifyDataChanged(objectEntities, raw);

    }


    public void notifyDataChanged(List<SemantichObjectEntity> entities, String data) {
        String rawTextValue = getString(R.string.search_title);
        String rawText = new JsonParser().parse(data).getAsJsonObject().get("raw_text").toString();
        searchTitle.setText(String.format(rawTextValue, rawText));
        recyclerView.setAdapter(new GridAdapter(entities, getContext()));
        //first item request focus
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_up:
                recyclerView.smoothScrollBy(0, -recyclerView.getHeight());
                break;
            case R.id.arrow_down:
                recyclerView.smoothScrollBy(0, recyclerView.getHeight());
                break;
        }
    }

    private class GridAdapter extends BaseAdapter {
        private Context context;
        private List<SemantichObjectEntity> datas;

        public GridAdapter(List<SemantichObjectEntity> objectEntities, Context context) {
            this.context = context;
            if (objectEntities.size() > 8) {
                arrowDown.setVisibility(View.VISIBLE);
            } else {
                arrowDown.setVisibility(View.GONE);
            }

            this.datas = objectEntities;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder myViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_vod_search, null);
                myViewHolder = new ViewHolder();
                myViewHolder.itemVodTitle = (TextView) convertView.findViewById(R.id.item_vod_title);
                myViewHolder.itemVodImage = (ImageView) convertView.findViewById(R.id.item_vod_image);
                myViewHolder.itemScore = (TextView) convertView.findViewById(R.id.item_vod_score);
                myViewHolder.itemPrice = (TextView) convertView.findViewById(R.id.item_vod_price);
                myViewHolder.itemFocus = (TextView) convertView.findViewById(R.id.item_vod_focus);
                convertView.setTag(myViewHolder);
            } else {
                myViewHolder = (ViewHolder) convertView.getTag();
            }

            myViewHolder.itemVodTitle.setText(datas.get(position).getTitle());
            Transformation mTransformation = new ReflectionTransformationBuilder()
                    .setIsHorizontal(true)
                    .build();
            String verticalUrl = datas.get(position).getVertical_url();
            String horizontalUrl = datas.get(position).getPoster_url();
            String scoreValue = datas.get(position).getBean_score();
            float priceValue = datas.get(position).getExpense() == null ? 0 : datas.get(position).getExpense().getPrice();
            String focusValue = datas.get(position).getFocus();

            if (!TextUtils.isEmpty(verticalUrl)) {
                if (!TextUtils.isEmpty(scoreValue)) {
                    myViewHolder.itemScore.setVisibility(View.VISIBLE);
                    myViewHolder.itemScore.setText(scoreValue);
                }
                if (priceValue != 0) {
                    myViewHolder.itemPrice.setVisibility(View.VISIBLE);
                    myViewHolder.itemPrice.setText("￥" + String.valueOf(priceValue));
                }
                if (!TextUtils.isEmpty(focusValue)) {
                    myViewHolder.itemFocus.setVisibility(View.VISIBLE);
                    myViewHolder.itemFocus.setText(focusValue);
                }
                Picasso.with(getContext())
                        .load(verticalUrl)
                        .memoryPolicy(MemoryPolicy.NO_STORE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .error(R.drawable.vertical_preview_bg)
                        .placeholder(R.drawable.vertical_preview_bg)
                        .transform(mTransformation)
                        .into(myViewHolder.itemVodImage);
            } else {
                Picasso.with(getContext())
                        .load(horizontalUrl)
                        .memoryPolicy(MemoryPolicy.NO_STORE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .error(R.drawable.vertical_preview_bg)
                        .placeholder(R.drawable.vertical_preview_bg)
                        .transform(mTransformation)
                        .into(myViewHolder.itemVodImage);
            }

            return convertView;
        }


    }


    private class ViewHolder {
        TextView itemVodTitle;
        ImageView itemVodImage;
        TextView itemScore;
        TextView itemPrice;
        TextView itemFocus;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SemantichObjectEntity objectEntity = (SemantichObjectEntity) recyclerView.getAdapter().getItem(position);
        onVodItemClick(objectEntity);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View currentFocusView, int position, long id) {
        if (currentFocusView == null) {
            currentFocusView = recyclerView.getChildAt(0);
        }

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
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


}