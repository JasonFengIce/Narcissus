package cn.ismartv.voice.ui;

import android.graphics.Rect;
import android.view.View;

import cn.ismartv.recyclerview.widget.RecyclerView;

/**
 * Created by huaijie on 1/25/16.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int verticalSpace;
    private int horizontalSpace;

    public SpaceItemDecoration(int verticalSpace, int horizontalSpace) {
        this.verticalSpace = verticalSpace;
        this.horizontalSpace = horizontalSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

//        if (parent.getChildLayoutPosition(view) != 0 && parent.getChildLayoutPosition(view) != 1) {
//            outRect.left = horizontalSpace;
//        }
        int position = parent.getChildLayoutPosition(view);
        if (position != 0 || position != 1 || position != 2 || position != 3) {
            outRect.top = verticalSpace;
        }

        outRect.left = 0;
//
//        if (position / 4 != 0 || position == 1 || position == 2 || position == 3) {
//            outRect.left = horizontalSpace;
//        }

    }


}