package cn.ismartv.voice.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.ismartv.voice.R;
import cn.ismartv.voice.ui.activity.BaseActivity;


/**
 * Created by huaijie on 10/15/15.
 */
public class MessagePopWindow extends PopupWindow implements View.OnClickListener, View.OnFocusChangeListener {
    private Button confirmBtn;
    private Button cancelBtn;
    private TextView firstMessage;
    private TextView secondMessage;
    private ConfirmListener confirmListener;
    private CancelListener cancleListener;

    private String mFirstLineMessage;
    private String mSecondLineMessage;

    private Context mContext;

    private ImageView cursorImageView;
    private float rate;
//    private int tmpX = 0;

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        int x1 = (int) (mContext.getResources().getDimension(R.dimen.pop_confirm_cursor_x) / rate);
        int x2 = (int) (mContext.getResources().getDimension(R.dimen.pop_cancel_cursor_x) / rate);
        switch (v.getId()) {
            case R.id.confirm_btn:
                if (hasFocus) {
                    ((Button) v).setTextColor(mContext.getResources().getColor(R.color._ff9c3c));
                    slideview(cursorImageView, 0, x1 - v.getX());
                } else {
                    ((Button) v).setTextColor(mContext.getResources().getColor(R.color._ffffff));

                }
                break;
            case R.id.cancel_btn:
                if (hasFocus) {
                    ((Button) v).setTextColor(mContext.getResources().getColor(R.color._ff9c3c));
                    slideview(cursorImageView, 0, x2 - v.getX());
                } else {
                    ((Button) v).setTextColor(mContext.getResources().getColor(R.color._ffffff));
                }
                break;
        }

    }

    public interface CancelListener {
        void cancelClick(View view);
    }

    public interface ConfirmListener {
        void confirmClick(View view);
    }


    public MessagePopWindow(Context context, String line1Message, String line2Message) {
        rate = ((BaseActivity) context).getDensityRate();
        mFirstLineMessage = line1Message;
        mSecondLineMessage = line2Message;
        mContext = context;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();
        int screenHeight = wm.getDefaultDisplay().getHeight();


        int width = (int) (context.getResources().getDimension(R.dimen.message_pop_width) / ((BaseActivity) context).getDensityRate());
        int height = (int) (context.getResources().getDimension(R.dimen.message_pop_height) / ((BaseActivity) context).getDensityRate());

        setWidth(screenWidth);
        setHeight(screenHeight);

        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_message, null);
        confirmBtn = (Button) contentView.findViewById(R.id.confirm_btn);
        cancelBtn = (Button) contentView.findViewById(R.id.cancel_btn);
        cursorImageView = (ImageView) contentView.findViewById(R.id.pop_cursor);
        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnFocusChangeListener(this);
        cancelBtn.setOnFocusChangeListener(this);
        firstMessage = (TextView) contentView.findViewById(R.id.first_text_info);
        secondMessage = (TextView) contentView.findViewById(R.id.pop_second_text);
        firstMessage.setText(mFirstLineMessage);

        RelativeLayout frameLayout = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams layoutParams;
        layoutParams = new RelativeLayout.LayoutParams(width, height);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        if (TextUtils.isEmpty(mSecondLineMessage)) {
            secondMessage.setVisibility(View.GONE);

        } else {
            secondMessage.setVisibility(View.VISIBLE);
            secondMessage.setText(mSecondLineMessage);
        }


        frameLayout.addView(contentView, layoutParams);
        setContentView(frameLayout);
        setFocusable(true);

    }

    public void setButtonText(String btn1, String btn2) {
        confirmBtn.setText(btn1);
        cancelBtn.setText(btn2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_btn:
                if (confirmListener != null) {
                    confirmListener.confirmClick(v);
                }
                break;
            case R.id.cancel_btn:
                if (cancleListener != null) {
                    cancleListener.cancelClick(v);
                }
                break;
        }
    }


    public void showAtLocation(View parent, int gravity, ConfirmListener confirmListener, CancelListener cancleListener) {
        if (confirmListener == null) {
            confirmBtn.setVisibility(View.GONE);
        }

        if (cancleListener == null) {
            cancelBtn.setVisibility(View.GONE);
        }
        this.confirmListener = confirmListener;
        this.cancleListener = cancleListener;
        super.showAtLocation(parent, gravity, 0, 0);
    }

    public void slideview(final View view, final float xFrom, final float xTo) {
        TranslateAnimation animation = new TranslateAnimation(xFrom, xTo, 0, 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(500);
//        animation.setStartOffset(delayMillis);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int left = view.getLeft() + (int) (xTo - xFrom);
                int top = view.getTop();
                int width = view.getWidth();
                int height = view.getHeight();
                view.clearAnimation();
                view.layout(left, top, left + width, top + height);
            }
        });

        view.startAnimation(animation);
    }

}