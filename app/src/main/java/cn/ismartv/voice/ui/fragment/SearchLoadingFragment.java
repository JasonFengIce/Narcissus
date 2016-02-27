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
public class SearchLoadingFragment extends BaseFragment {

    private ImageView progressView;
    private View contentView;
    private boolean hasBackground;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_search_loading, null);
        return contentView;
    }

    public boolean isHasBackground() {
        return hasBackground;
    }

    public void setHasBackground(boolean hasBackground) {
        this.hasBackground = hasBackground;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (contentView != null && !hidden) {
            if (hasBackground) {
                contentView.setBackgroundDrawable(getResources().getDrawable(R.drawable.pop_bg));
            } else {
                contentView.setBackgroundDrawable(getResources().getDrawable(R.drawable.pop_bg));
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressView = (ImageView) view.findViewById(R.id.progress_view);
        AnimationDrawable drawable = (AnimationDrawable) progressView.getDrawable();
        drawable.start();
    }


}
