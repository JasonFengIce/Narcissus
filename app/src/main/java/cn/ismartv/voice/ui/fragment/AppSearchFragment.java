package cn.ismartv.voice.ui.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.ismartv.recyclerview.widget.GridLayoutManager;
import cn.ismartv.recyclerview.widget.RecyclerView;
import cn.ismartv.voice.R;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.ui.SpaceItemDecoration;

/**
 * Created by huaijie on 1/28/16.
 */
public class AppSearchFragment extends BaseFragment {
    private View contentView;

    private RecyclerView recyclerView;

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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false);

        int verticalSpacingInPixels = (int) (getResources().getDimensionPixelSize(R.dimen.content_fragment_item_vertical_space) / getDensityRate());
        int horizontalSpacingInPixels = (int) (getResources().getDimensionPixelSize(R.dimen.content_fragment_item_horizontal_space) / getDensityRate());
        recyclerView.addItemDecoration(new SpaceItemDecoration(verticalSpacingInPixels, horizontalSpacingInPixels));
        recyclerView.setLayoutManager(gridLayoutManager);
    }


    public void notifyDataChanged(List<AppSearchObjectEntity> objectEntities) {
        recyclerView.setAdapter(new RecyclerAdapter(objectEntities));
    }


    private class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private List<AppSearchObjectEntity> datas;

        public RecyclerAdapter(List<AppSearchObjectEntity> objectEntities) {
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
                Picasso.with(getContext()).load(datas.get(postion).getAdlet_url()).error(R.drawable.horizontal_preview_bg).into(myViewHolder.imageView);
            }
        }


        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.id_number);
            imageView = (ImageView) itemView.findViewById(R.id.image);

        }
    }
}
