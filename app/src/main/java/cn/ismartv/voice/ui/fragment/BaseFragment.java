package cn.ismartv.voice.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import java.util.List;

import cn.ismartv.injectdb.library.query.Select;
import cn.ismartv.voice.R;
import cn.ismartv.voice.data.http.AppSearchObjectEntity;
import cn.ismartv.voice.data.http.SemantichObjectEntity;
import cn.ismartv.voice.data.table.AppTable;
import cn.ismartv.voice.ui.widget.LaunchAppTransitionPopWindow;
import cn.ismartv.voice.util.DensityRate;

/**
 * Created by huaijie on 1/18/16.
 */
public class BaseFragment extends Fragment {

    private float densityRate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        densityRate = DensityRate.getDensityRate(getActivity());
    }

    public float getDensityRate() {
        return densityRate;
    }

    protected void onVodItemClick(SemantichObjectEntity objectEntity) {
        String url = objectEntity.getUrl();
        String contentModel = objectEntity.getContent_model();
        Long pk = Long.parseLong(objectEntity.getPk());
        String title = objectEntity.getTitle();

        String verticalUrl = objectEntity.getVertical_url();
        String horizontalUrl = objectEntity.getPoster_url();

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        switch (contentModel) {
            case "person":
                intent.putExtra("pk", pk);
                intent.putExtra("title", title);
                intent.setAction("cn.ismartv.voice.film_star");
                break;
            default:
                intent.putExtra("url", url);
                if (!TextUtils.isEmpty(verticalUrl)) {
                    intent.setAction("tv.ismar.daisy.PFileItem");
                } else {
                    intent.setAction("tv.ismar.daisy.Item");
                }
        }
        startActivity(intent);
    }

    protected void onAppItemClick(AppSearchObjectEntity objectEntity) {
        if (objectEntity.isLocal()) {
            launchAppTransition(objectEntity.getCaption());
        } else {
            String appPackage = objectEntity.getCaption();
            final List<AppTable> appTables = new Select().from(AppTable.class).where("app_package = ?", appPackage).execute();
            if (appTables.size() != 0) {
                launchAppTransition(objectEntity.getCaption());
            } else {
                Intent intent = new Intent("com.boxmate.tv.detail");
                //app_id从服务端获取
                intent.putExtra("app_id", objectEntity.getPk());
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), R.string.ismartv_store_app_not_install, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void launchAppTransition(final String appPackage) {
        LaunchAppTransitionPopWindow popWindow = new LaunchAppTransitionPopWindow(getContext());
        popWindow.showAtLocation(getView(), Gravity.CENTER, new LaunchAppTransitionPopWindow.DisappearCallback() {
            @Override
            public void onDisappear() {
                launchApp(appPackage);
            }
        });
    }


    private void launchApp(String appPackage) {
        PackageManager packageManager = getContext().getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(appPackage);
        startActivity(intent);
    }
}
