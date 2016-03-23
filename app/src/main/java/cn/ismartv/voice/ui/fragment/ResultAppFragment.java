package cn.ismartv.voice.ui.fragment;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.ismartv.voice.R;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.ui.widget.ZGridView;
import cn.ismartv.voice.ui.widget.ZGridView.OnItemHoverExitListener;

/**
 * Created by huaijie on 1/28/16.
 */
public class ResultAppFragment extends BaseFragment implements View.OnFocusChangeListener, AdapterView.OnItemClickListener
        , AdapterView.OnItemSelectedListener, View.OnHoverListener, OnItemHoverExitListener {
    private static final String TAG = "ResultAppFragment";
    private View contentView;

    private ZGridView recyclerView;
    private TextView searchTitle;


    private ImageView arrowUp;
    private ImageView arrowDown;

    private List<AppSearchObjectEntity> appSearchObjectEntities;

    private String raw;

    private View itemClickedView;

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public void setAppSearchObjectEntities(List<AppSearchObjectEntity> appSearchObjectEntities) {
        this.appSearchObjectEntities = appSearchObjectEntities;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_result_app, null);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (ZGridView) view.findViewById(R.id.recyclerview);
        searchTitle = (TextView) view.findViewById(R.id.search_title);

        arrowUp = (ImageView) view.findViewById(R.id.arrow_up);
        arrowDown = (ImageView) view.findViewById(R.id.arrow_down);
        arrowUp.bringToFront();

        arrowUp.setOnHoverListener(this);
        arrowDown.setOnHoverListener(this);

        recyclerView.setOnItemSelectedListener(this);
        recyclerView.setOnItemClickListener(this);
        recyclerView.setOnItemHoverExitListener(this);

        notifyDataChanged(appSearchObjectEntities, raw);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (itemClickedView != null) {
            itemClickedView.requestFocusFromTouch();
            itemClickedView.requestFocus();
        }
    }

    public void notifyDataChanged(List<AppSearchObjectEntity> objectEntities, String rawText) {
        String rawTextValue = getString(R.string.search_title);
        searchTitle.setText(String.format(rawTextValue, rawText));
        recyclerView.setAdapter(new GridAdapter(objectEntities, getContext()));
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.recyclerview:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        itemClickedView = view;
        Log.i(TAG, "click position: " + position);
        onAppItemClick((AppSearchObjectEntity) recyclerView.getAdapter().getItem(position));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_HOVER_ENTER:
            case MotionEvent.ACTION_HOVER_MOVE:
                if (!v.isFocused()) {
                    v.requestFocus();
                }
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
                searchTitle.requestFocus();
                break;
        }


        return true;
    }

    @Override
    public boolean onItemHoverExit() {
        searchTitle.requestFocus();
        return true;
    }

    private class GridAdapter extends BaseAdapter {
        private Context context;
        private List<AppSearchObjectEntity> datas;

        public GridAdapter(List<AppSearchObjectEntity> objectEntities, Context context) {
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
                convertView = LayoutInflater.from(context).inflate(R.layout.item_app_result, null);
                myViewHolder = new ViewHolder();
                myViewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_app_image);
                myViewHolder.textView = (TextView) convertView.findViewById(R.id.item_app_title);
                myViewHolder.localLabel = (TextView) convertView.findViewById(R.id.item_app_local_label);
                convertView.setTag(myViewHolder);
            } else {
                myViewHolder = (ViewHolder) convertView.getTag();
            }

            if (datas.get(position).isLocal()) {
                try {
                    PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(datas.get(position).getCaption(), 0);
                    Drawable drawable = packageInfo.applicationInfo.loadIcon(getContext().getPackageManager());
                    myViewHolder.imageView.setImageDrawable(drawable);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                myViewHolder.localLabel.setVisibility(View.VISIBLE);

            } else {
                String iconUrl = datas.get(position).getAdlet_url();
                Picasso.with(getContext())
                        .load(iconUrl)
                        .memoryPolicy(MemoryPolicy.NO_STORE)
                        .error(R.drawable.vertical_preview_bg)
                        .placeholder(R.drawable.vertical_preview_bg)
                        .into(myViewHolder.imageView);
            }
            myViewHolder.textView.setText(datas.get(position).getTitle());
            return convertView;
        }
    }


    private class ViewHolder {
        private TextView textView;
        private ImageView imageView;
        private TextView localLabel;
    }

}
