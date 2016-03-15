package cn.ismartv.voice.ui.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class LinerLayoutContainer extends LinearLayout {


    public LinerLayoutContainer(Context context) {
        super(context);
    }

    public LinerLayoutContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LinerLayoutContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean dispatchHoverEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        Log.v("Linerlayaaa","event.getAction()="+event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_HOVER_ENTER:
            case MotionEvent.ACTION_HOVER_MOVE:
                setHovered(true);
                requestFocus();
//                invalidate();
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
                setHovered(false);
                invalidate();
                break;
        }
        return false;
    }

}