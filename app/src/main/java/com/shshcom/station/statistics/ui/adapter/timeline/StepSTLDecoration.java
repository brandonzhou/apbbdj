package com.shshcom.station.statistics.ui.adapter.timeline;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import com.orient.me.data.ITimeItem;
import com.orient.me.utils.UIUtils;
import com.orient.me.widget.rv.itemdocration.timeline.SingleTimeLineDecoration;

/**
 * desc: 快递详情 时间线
 * author: zhhli
 * 2020/6/8
 */
public class StepSTLDecoration extends SHSingleTimeLineDecoration {

    private Paint mRectPaint;

    public StepSTLDecoration(SingleTimeLineDecoration.Config config) {
        super(config);

        mRectPaint = new Paint();
        mRectPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));
        mDotPaint.setMaskFilter(new BlurMaskFilter(6, BlurMaskFilter.Blur.SOLID));
    }

   // @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDrawTitleItem(Canvas canvas, int left, int top, int right, int bottom, int pos) {
        ITimeItem item = timeItems.get(pos);

        int rectWidth = UIUtils.dip2px(120);
        int height = bottom - top;
        int paddingLeft = UIUtils.dip2px(10);
        mRectPaint.setColor(item.getColor());
        //canvas.drawRoundRect(left+paddingLeft,top,left+rectWidth,bottom,UIUtils.dip2px(6),UIUtils.dip2px(6),mRectPaint);


        String title = item.getTitle();
        if(TextUtils.isEmpty(title))
            return;
        Rect mRect = new Rect();

        mTextPaint.getTextBounds(title,0,title.length(),mRect);
//        int x = left + (rectWidth - mRect.width())/2;
        int x = left + UIUtils.dip2px(20);
        int y = bottom - (height - mRect.height())/2;
        canvas.drawText(title,x,y,mTextPaint);
    }

    @Override
    protected void onDrawDotItem(Canvas canvas, int cx, int cy, int radius, int pos) {
        ITimeItem item = timeItems.get(pos);
        if(pos == timeItems.size()-1){
            mDotPaint.setColor(item.getColor());
        }else {
            mDotPaint.setColor(Color.parseColor("#FFD8D8D8"));
        }
        canvas.drawCircle(cx,cy,UIUtils.dip2px(6),mDotPaint);
    }

    @Override
    int getYEnd() {
        // margintop 12  text 12/2= 6 drawCircle= 6
        return UIUtils.dip2px(12+6+6);
    }
}
