package com.shshcom.station.storage.ocrclip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.mt.bbdj.R;

public final class ERectFindView extends View {

    /**
     * 刷新界面的时间
     */
    private static final long ANIMATION_DELAY = 25L;

    /**
     * 四周边框的宽度
     */
    private static final int FRAME_LINE_WIDTH = 8;
    private static final int FRAME_LINE_WIDTH_2 = 8;
    private static final int FRAME_LINE_LENGTH = 40;
    private Rect frame;
    private int width, height;
    private Paint paint;
    private int screenWidth, screenHeight;

    private Context context;
    private DisplayMetrics dm;
    private String drawText;
    private float leftPointX, leftPointY, rectWidth, rectHeight, namePositionX, namePositionY;
    private String nameTextColor = "#ffffff";
    //    private String drawColor = "#00BE7E";
    private String drawColor = "#ffffff";
    private String drawColor_red = "#ff0000";
    private int TestTextZize = 16;

    // 蒙层 dp
    public int grayMarginTop = 55;
    // dp
    public int grayMarginBottom = 120;


    public ERectFindView(Context context, OcrTypeHelper ocrTypeHelper) {
        super(context);
        paint = new Paint();
        this.context = context;
        // 获取当前屏幕
        dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        if (ocrTypeHelper != null) {
            try {
                this.leftPointX = ocrTypeHelper.leftPointX;
                this.leftPointY = ocrTypeHelper.leftPointY;
                this.rectWidth = ocrTypeHelper.width;
                this.rectHeight = ocrTypeHelper.height;
                this.namePositionX = ocrTypeHelper.namePositionX;
                this.namePositionY = ocrTypeHelper.namePositionY;
                this.drawText = ocrTypeHelper.ocrTypeName;
                this.TestTextZize = ocrTypeHelper.nameTextSize;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        width = canvas.getWidth();
        height = canvas.getHeight();
//        Log.e("rectFindView width", width + "--" + height);
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        float density = dm.density;        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        paint.setColor(Color.parseColor(nameTextColor));
        paint.setTextSize(TestTextZize * density);

        int left = (int) (leftPointX * width);
        int top = (int) (height * leftPointY);
        int right = (int) ((leftPointX + rectWidth) * width);
        int bottom = (int) (height * (leftPointY + rectHeight));

        /*手机号码对应的识别位置记忆参考线的位置*/
//        int referAlignY = (top + bottom)/2;

        /**
         * 这个矩形就是中间显示的那个框框
         */
        frame = new Rect(left, top, right, bottom);


        // 绘制 识别区域外蒙层
        drawExterior(canvas, frame, width, height);
        // 绘制边角
        drawCorner(canvas, frame);
        // 绘制文字提示
        drawTextInfo(canvas, frame);
        // 绘制扫描线
        drawLineScanner(canvas, frame);
        fresh();
    }

    /**
     * 绘制模糊区域
     *
     * @param canvas
     * @param frame
     * @param width
     * @param height
     */
    private void drawExterior(Canvas canvas, Rect frame, int width, int height) {
        // 画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
        // 扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
        paint.setColor(Color.argb(50, 0, 0, 0));
        canvas.drawRect(0, grayMarginTop * dm.density, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1,
                paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height - dm.density * grayMarginBottom, paint);
    }

    // 绘制边角
    private void drawCorner(Canvas canvas, Rect frame) {
        // 绘制两个像素边宽的绿色线框
        paint.setColor(Color.parseColor(drawColor));

        /*左上角*/
        canvas.drawRect(frame.left, frame.top,
                (frame.left) + FRAME_LINE_LENGTH, frame.top
                        + FRAME_LINE_WIDTH, paint);// 上边
        canvas.drawRect(frame.left, frame.top,
                frame.left + FRAME_LINE_WIDTH, frame.top
                        + FRAME_LINE_LENGTH, paint);// 左边

        /*右上角*/
        canvas.drawRect(frame.right - FRAME_LINE_LENGTH, frame.top,
                frame.right, frame.top + FRAME_LINE_WIDTH, paint);// 上边
        canvas.drawRect(frame.right - FRAME_LINE_WIDTH, frame.top,
                frame.right, frame.top + FRAME_LINE_LENGTH, paint);// 右边

        /*左下角*/
        canvas.drawRect(frame.left, frame.bottom - FRAME_LINE_LENGTH,
                frame.left + FRAME_LINE_WIDTH, frame.bottom, paint);// 左边
        canvas.drawRect(frame.left, frame.bottom - FRAME_LINE_WIDTH,
                frame.left + FRAME_LINE_LENGTH, frame.bottom, paint);// 底边

        /*右下角*/
        canvas.drawRect(frame.right - FRAME_LINE_WIDTH, frame.bottom - FRAME_LINE_LENGTH,
                frame.right, frame.bottom, paint);// 右边
        canvas.drawRect(frame.right - FRAME_LINE_LENGTH, frame.bottom - FRAME_LINE_WIDTH,
                frame.right, frame.bottom, paint);// 底边
    }

    /**
     * 绘制文本
     *
     * @param canvas
     * @param frame
     */
    private void drawTextInfo(Canvas canvas, Rect frame) {
        paint.setTextAlign(Paint.Align.CENTER);
        int textX = 0;
        if (dm.densityDpi > 320) {
            textX = (int) (namePositionX * width);
        } else if (dm.densityDpi == 320) {
            textX = (int) (namePositionX * width * 0.9);
        } else {
            textX = (int) (namePositionX * width * 0.75);
        }

        float textAlignY = frame.bottom + dm.density * 20;
        paint.setColor(Color.argb(80, 0, 0, 0));
        float r = dm.density * (TestTextZize + 4);
        RectF targetRect = new RectF(textX / 2, textAlignY, width - textX / 2, textAlignY + r * 2);
        canvas.drawRoundRect(targetRect, r, r, paint);

        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        float baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;

        paint.setColor(Color.parseColor(nameTextColor));
        canvas.drawText(drawText, targetRect.centerX(), baseline, paint);
    }

    public void fresh() {
        postInvalidateDelayed(ANIMATION_DELAY, 0, 0, width, height);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //return super.dispatchTouchEvent(event);
        float density = dm.density;
        if (event.getY() > density * grayMarginTop && event.getY() < (width - density * grayMarginBottom)) {
            return true;
        }

        return super.dispatchTouchEvent(event);
    }


    /**
     * 扫描线开始位置
     */
    public float scannerStart = 0;
    /**
     * 扫描线结束位置
     */
    public float scannerEnd = 0;


    /**
     * 绘制线性式扫描
     *
     * @param canvas
     * @param frame
     */
    private void drawLineScanner(Canvas canvas, Rect frame) {
        float scannerLineMoveDistance = 2 * dm.density;
        float scannerLineHeight = 5 * dm.density;
        if (scannerStart == 0 || scannerEnd == 0) {
            scannerStart = frame.top;
            scannerEnd = frame.bottom - scannerLineHeight;
        }

        int laserColor = getResources().getColor(R.color.mainColor);
        //线性渐变
        LinearGradient linearGradient = new LinearGradient(
                frame.left, scannerStart,
                frame.left, scannerStart + scannerLineHeight,
                shadeColor(laserColor),
                laserColor,
                Shader.TileMode.MIRROR);

        paint.setShader(linearGradient);

        if (scannerStart <= scannerEnd) {
            //椭圆
            RectF rectF = new RectF(frame.left + 2 * scannerLineHeight, scannerStart,
                    frame.right - 2 * scannerLineHeight, scannerStart + scannerLineHeight);
            canvas.drawOval(rectF, paint);
            scannerStart += scannerLineMoveDistance;
        } else {
            scannerStart = frame.top;
        }

        paint.setShader(null);

    }

    /**
     * 处理颜色模糊
     *
     * @param color
     * @return
     */
    private int shadeColor(int color) {
        String hax = Integer.toHexString(color);
        String result = "01" + hax.substring(2);
        return Integer.valueOf(result, 16);
    }



    /**
     * bitmap 和 蒙层的宽高比,安装宽高比，获取对应区域的 bitmap 截图区域
     * 注意：ERectFindView 和preview 的布局一致，都为全屏
     *
     * @param imageWidth 获取的bitmap的 宽度，
     * @param imageHeight bitmap height
     * @return bitmap 截图区域
     */
    public Rect getScanBoxAreaRect(int imageWidth, int imageHeight) {
        Rect rect = new Rect(frame);
        // widthRatio:1.0 heightRatio:0.91168094
        float widthRatio = 1.0f * imageWidth / getWidth();
        float heightRatio = 1.0f * imageHeight / getHeight();

        //Log.e("zzzzz", "width:" +getWidth() + " height:"+getHeight() + " "+ getMeasuredHeight());
        //Log.e("zzzzz", "image width:" +imageWidth + " height:"+imageHeight);
        //Log.e("zzzzz", "widthRatio:" +widthRatio + " heightRatio:"+heightRatio);
        //Log.e("zzzzz", "getScanBoxAreaRect: old "+ rect.toString() );
        rect.left = (int) (rect.left /widthRatio);
        rect.right = (int) (rect.right * widthRatio);
        rect.top = (int) (rect.top * heightRatio);
        rect.bottom = (int) (rect.bottom * heightRatio);

        Log.d("zzzzz", "getScanBoxAreaRect: new  "+ rect.toString() );


        return rect;
    }




}
