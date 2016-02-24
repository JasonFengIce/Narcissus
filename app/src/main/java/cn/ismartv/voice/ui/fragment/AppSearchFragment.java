package cn.ismartv.voice.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.ismartv.recyclerview.widget.GridLayoutManager;
import cn.ismartv.recyclerview.widget.RecyclerView;
import cn.ismartv.voice.MainApplication;
import cn.ismartv.voice.R;
import cn.ismartv.voice.core.http.HttpAPI;
import cn.ismartv.voice.core.http.HttpManager;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.data.http.RecommandAppEntity;
import cn.ismartv.voice.ui.SpaceItemDecoration;
import cn.ismartv.voice.ui.widget.LaunchAppTransitionPopWindow;
import cn.ismartv.voice.util.ViewScaleUtil;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by huaijie on 1/28/16.
 */
public class AppSearchFragment extends BaseFragment implements View.OnFocusChangeListener {
    private View contentView;

    private RecyclerView recyclerView;
    private TextView searchTitle;


    private ImageView arrowUp;
    private ImageView arrowDown;
    private View firstItemView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_app_search, null);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.app_search_list);
        searchTitle = (TextView) view.findViewById(R.id.search_title);

        arrowUp = (ImageView) view.findViewById(R.id.arrow_up);
        arrowDown = (ImageView) view.findViewById(R.id.arrow_down);
        arrowUp.bringToFront();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false);

        int verticalSpacingInPixels = (int) (getResources().getDimensionPixelSize(R.dimen.content_fragment_item_vertical_space) / getDensityRate());
        int horizontalSpacingInPixels = (int) (getResources().getDimensionPixelSize(R.dimen.content_fragment_item_horizontal_space) / getDensityRate());
        recyclerView.addItemDecoration(new SpaceItemDecoration(verticalSpacingInPixels, horizontalSpacingInPixels));
        recyclerView.setLayoutManager(gridLayoutManager);
    }


    public void notifyDataChanged(List<AppSearchObjectEntity> objectEntities, String rawText) {
        String rawTextValue = getString(R.string.search_title);
        searchTitle.setText(String.format(rawTextValue, rawText));
        recyclerView.setAdapter(new RecyclerAdapter(objectEntities));
    }

    public void setSearchTitle() {
        searchTitle.setText(getString(R.string.recommend_content_title));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.recyclerview:
                if (firstItemView != null) {
                    firstItemView.requestFocus();
                }
                break;
        }
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener, View.OnFocusChangeListener {

        private List<AppSearchObjectEntity> datas;

        public RecyclerAdapter(List<AppSearchObjectEntity> objectEntities) {
            if (objectEntities.size() <= 8) {
                arrowUp.setVisibility(View.GONE);
                arrowDown.setVisibility(View.GONE);
            } else {
                arrowUp.setVisibility(View.VISIBLE);
                arrowDown.setVisibility(View.VISIBLE);
            }

            this.datas = objectEntities;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_app_search, viewGroup, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int postion) {
            myViewHolder.textView.setText(datas.get(postion).getTitle());
            boolean isLocal = datas.get(postion).isLocal();
            if (isLocal) {
                try {
                    PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(datas.get(postion).getCaption(), 0);
                    Drawable drawable = packageInfo.applicationInfo.loadIcon(getContext().getPackageManager());
                    myViewHolder.imageView.setImageDrawable(drawable);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
//                Picasso.with(getContext()).load().error(R.drawable.horizontal_preview_bg).into(myViewHolder.imageView);
            } else {
                Picasso.with(getContext()).load(datas.get(postion).getAdlet_url()).memoryPolicy(MemoryPolicy.NO_STORE).error(R.drawable.horizontal_preview_bg).into(myViewHolder.imageView);
            }

            myViewHolder.mItemView.setTag(datas.get(postion));
            myViewHolder.mItemView.setOnClickListener(this);
            myViewHolder.mItemView.setOnFocusChangeListener(this);
            if (postion == 0) {
                firstItemView = myViewHolder.mItemView;
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
            AppSearchObjectEntity objectEntity = (AppSearchObjectEntity) v.getTag();
            if (objectEntity.isLocal()) {
                launchAppTransition(objectEntity.getCaption());
            } else {

                Intent intent = new Intent("com.boxmate.tv.detail");
                //app_id从服务端获取
                intent.putExtra("app_id", objectEntity.getPk());
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), R.string.ismartv_store_app_not_install, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            ImageView imageView = (ImageView) v.findViewById(R.id.image);
            TextView textView = (TextView) v.findViewById(R.id.id_number);
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
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;
        private View mItemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.id_number);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            this.mItemView = itemView;
        }
    }

    public void fetchRecommendApp() {
        Retrofit retrofit = HttpManager.getInstance().resetAdapter_WUGUOJUN;
        retrofit.create(HttpAPI.RecommandApp.class).doRequest(8).enqueue(new Callback<List<RecommandAppEntity>>() {
            @Override
            public void onResponse(Response<List<RecommandAppEntity>> response) {
                List<RecommandAppEntity> entities = response.body();
                ArrayList<AppSearchObjectEntity> list = new ArrayList<AppSearchObjectEntity>();
                for (RecommandAppEntity recommandAppEntity : entities) {
                    AppSearchObjectEntity objectEntity = new AppSearchObjectEntity();
                    objectEntity.setIsLocal(false);
                    objectEntity.setTitle(recommandAppEntity.getTitle());
                    objectEntity.setUrl(recommandAppEntity.getDownload_url());
                    objectEntity.setAdlet_url(recommandAppEntity.getIcon_url());
                    list.add(objectEntity);
                }
                recyclerView.setAdapter(new RecyclerAdapter(list));
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    private void launchApp(String appPackage) {
        Context context = MainApplication.getContext();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(appPackage);
        context.startActivity(intent);
    }

    private void launchAppTransition(final String appPackage) {
        LaunchAppTransitionPopWindow popWindow = new LaunchAppTransitionPopWindow(getContext());
        popWindow.showAtLocation(getView(), Gravity.CENTER, new LaunchAppTransitionPopWindow.DisappearCallback() {
            @Override
            public void onDisappear() {
                launchApp(appPackage);
            }
        });
    }
}
