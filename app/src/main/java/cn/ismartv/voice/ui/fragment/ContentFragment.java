package cn.ismartv.voice.ui.fragment;

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
import cn.ismartv.voice.data.http.SemanticSearchResponseEntity;
import cn.ismartv.voice.data.http.SemantichObjectEntity;

/**
 * Created by huaijie on 1/18/16.
 */
public class ContentFragment extends BaseFragment {
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

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
            myViewHolder.textView.setText(datas.get(postion).getTitle() + "   " + postion);
            Picasso.with(getContext()).load(datas.get(postion).getPoster_url()).into(myViewHolder.imageView);


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
