package cn.ismartv.voice.ui.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.ismartv.voice.R;

/**
 * Created by huaijie on 1/29/16.
 */
public class SearchLoadingWithBGFragment extends BaseFragment {

    private ImageView progressView;
    private View contentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_search_loading_with_bg, null);
        return contentView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressView = (ImageView) view.findViewById(R.id.progress_view);
        AnimationDrawable drawable = (AnimationDrawable) progressView.getDrawable();
        drawable.start();
    }


}
