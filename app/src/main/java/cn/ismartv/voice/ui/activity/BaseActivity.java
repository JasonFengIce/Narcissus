package cn.ismartv.voice.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;

import java.lang.ref.WeakReference;

import cn.ismartv.voice.ui.widget.MessagePopWindow;
import cn.ismartv.voice.util.DensityRate;

/**
 * Created by huaijie on 1/18/16.
 */
public class BaseActivity extends FragmentActivity {
    public final static int NETWORK_ERROR = 0x0001;
    private float densityRate;
    public static MessageHandler messageHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageHandler = new MessageHandler(this);
        densityRate = DensityRate.getDensityRate(this);
    }

    public float getDensityRate() {
        return densityRate;
    }


    public void showTextPop(String text) {
        final MessagePopWindow popupWindow = new MessagePopWindow(this, text, null);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, new MessagePopWindow.ConfirmListener() {
                    @Override
                    public void confirmClick(View view) {
                        popupWindow.dismiss();
                    }
                },
                null
        );
    }

   private class MessageHandler extends Handler {
        private WeakReference<BaseActivity> weakReference;

        public MessageHandler(BaseActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weakReference.get() != null) {
                switch (msg.what) {
                    case NETWORK_ERROR:
                        showTextPop("网络异常，请检查网络");
                        break;
                }
            }
        }
    }
}
