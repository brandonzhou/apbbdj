package com.mt.bbdj.baseconfig.view;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.mt.bbdj.R;

/**
 * Author : ZSK
 * Date : 2019/1/2
 * Description :
 */
public class MarginDecoration extends RecyclerView.ItemDecoration {
    private int margin;
    private int left_right;

    public MarginDecoration(Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.dp_5);
        left_right = 5;
    }

    public MarginDecoration(Context context, int left_right) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.dp_5);
        this.left_right = left_right;
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // outRect.set(margin, margin, margin, margin);
        outRect.set(margin, left_right, margin, left_right);
    }
}
