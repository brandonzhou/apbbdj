package com.mt.bbdj.baseconfig.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Author : ZSK
 * Date : 2018/12/26
 * Description :  自定义的表格式布局
 */
public class ShopGridView extends GridView {

    public ShopGridView(Context context) {
        super(context);
    }

    public ShopGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShopGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
