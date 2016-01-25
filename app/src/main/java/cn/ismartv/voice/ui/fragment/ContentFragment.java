package cn.ismartv.voice.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ismartv.recyclerview.widget.GridLayoutManager;
import cn.ismartv.recyclerview.widget.RecyclerView;
import cn.ismartv.voice.R;
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.data.http.SemantichObjectEntity;
import cn.ismartv.voice.ui.SpaceItemDecoration;
import cn.ismartv.voice.util.ImageUtil;

/**
 * Created by huaijie on 1/18/16.
 */
public class ContentFragment extends BaseFragment {
    private RecyclerView recyclerView;
//    private static final int resIds[] = {R.drawable.sample_1, R.drawable.sample_2, R.drawable.sample_3, R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7, R.drawable.sample_8};

    private static final int resIds[] = {R.drawable.test_1, R.drawable.test_2, R.drawable.test_3, R.drawable.test_4, R.drawable.test_5,
            R.drawable.test_6, R.drawable.test_7, R.drawable.test_8};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false);

        int verticalSpacingInPixels = (int) (getResources().getDimensionPixelSize(R.dimen.content_fragment_item_vertical_space) / getDensityRate());
        int horizontalSpacingInPixels = (int) (getResources().getDimensionPixelSize(R.dimen.content_fragment_item_horizontal_space) / getDensityRate());
        recyclerView.addItemDecoration(new SpaceItemDecoration(verticalSpacingInPixels, horizontalSpacingInPixels));

        recyclerView.setLayoutManager(gridLayoutManager);
        ArrayList<SemantichObjectEntity> arrayList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            SemantichObjectEntity objectEntity = new SemantichObjectEntity();
            objectEntity.setPoster_url(" ");
            objectEntity.setTitle(" ");
            arrayList.add(objectEntity);
        }
        recyclerView.setAdapter(new RecyclerAdapter(arrayList));


    }

    public void notifyDataChanged(SemanticSearchResponseEntity responseEntity) {
        recyclerView.setAdapter(new RecyclerAdapter(responseEntity.getFacet().get(0).getObjects()));
    }


    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

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
//            myViewHolder.textView.setText(datas.get(postion).getTitle() + "   " + postion);
//            Picasso.with(getContext()).load(datas.get(postion).getPoster_url()).into(myViewHolder.imageView);

            myViewHolder.textView.setText("电影 " + postion);
//            Picasso.with(getContext()).load(resIds[postion]).into(myViewHolder.imageView);
            ImageUtil.createReflectedImages(myViewHolder.imageView, resIds[postion]);

        }


        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.id_number);
            imageView = (ImageView) itemView.findViewById(R.id.image);

        }
    }
}
