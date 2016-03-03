package cn.ismartv.voice.ui.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import cn.ismartv.voice.R;

/**
 * Created by huaijie on 3/3/16.
 */
public class SearchLoadingPopWindow extends PopupWindow {

    public SearchLoadingPopWindow(Context context) {
        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.pop_bg));

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();
        int screenHeight = wm.getDefaultDisplay().getHeight();

        setWidth(screenWidth);
        setHeight(screenHeight);

        View contentView = LayoutInflater.from(context).inflate(R.layout.fragment_search_loading, null);
        ImageView progressView = (ImageView) contentView.findViewById(R.id.progress_view);
        AnimationDrawable drawable = (AnimationDrawable) progressView.getDrawable();
        drawable.start();
        setContentView(contentView);
        setFocusable(true);
    }
}
