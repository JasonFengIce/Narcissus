package cn.ismartv.voice.ui.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import cn.ismartv.voice.R;

/**
 * Created by huaijie on 2/18/16.
 */
public class LaunchAppTransitionPopWindow extends PopupWindow {
    private ImageView progressView;

    public LaunchAppTransitionPopWindow(Context context) {
        super(context);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        int screenWidth = point.x;
        int screenHeight =point.y;

        setWidth(screenWidth);
        setHeight(screenHeight);


        View view = LayoutInflater.from(context).inflate(R.layout.layout_launch_app_transition, null);
        progressView = (ImageView) view.findViewById(R.id.progress_view);
        setContentView(view);
    }

    public void showAtLocation(View parent, int gravity, final DisappearCallback callback) {
        super.showAtLocation(parent, gravity, 0, 0);
        final AnimationDrawable drawable = (AnimationDrawable) progressView.getDrawable();
        drawable.start();
        progressView.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawable.stop();
                dismiss();
                callback.onDisappear();
            }
        }, 500);
    }

    public interface DisappearCallback {
        void onDisappear();
    }
}
