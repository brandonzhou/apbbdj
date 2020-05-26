package com.shshcom.station.storage.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.lxj.easyadapter.EasyAdapter;
import com.lxj.easyadapter.MultiItemTypeAdapter;
import com.lxj.easyadapter.ViewHolder;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.lxj.xpopup.widget.VerticalRecyclerView;
import com.mt.bbdj.R;
import com.shshcom.station.storage.http.bean.ExpressCompany;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 功能描述 : 快递公司列表-底部弹窗
 * 创建人 : Administrator 创建时间: 20/5/23
 */
public class CustomExpressCompanyPopup extends BottomPopupView {

    VerticalRecyclerView mRecyclerView;
    private ArrayList<ExpressCompany> items;
    private EasyAdapter<ExpressCompany> commonAdapter;
    private OnItemClickListener mOnItemClickListener;

    Context mContext;
    public CustomExpressCompanyPopup(@NonNull Context context) {
        super(context);
    }

    public CustomExpressCompanyPopup(@NonNull Context context, ArrayList<ExpressCompany> data) {
        super(context);
        mContext = context;
        items = data;
        if (items == null){
            items = new ArrayList<>();
        }
    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.custom_bottom_popup_express_company;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mRecyclerView = findViewById(R.id.recyclerView);
        commonAdapter = new EasyAdapter<ExpressCompany>(items,R.layout.adapter_express_company) {
            @Override
            protected void bind(ViewHolder viewHolder, ExpressCompany expressCompany, int i) {
                viewHolder.setText(R.id.tv_express_company_name, expressCompany.getExpress_name());
            }
        };

        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.SimpleOnItemClickListener(){
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                //不要直接这样做，会导致消失动画未执行完就跳转界面，不流畅。
//                dismiss();
//                getContext().startActivity(new Intent(getContext(), DemoActivity.class))
                //可以等消失动画执行完毕再开启新界面
//                dismissWith(new Runnable() {
//                    @Override
//                    public void run() {
//                        getContext().startActivity(new Intent(getContext(), DemoActivity.class));
//                    }
//                });
                dismiss();
                mOnItemClickListener.onItemClick(position);

            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.shape_recyclerview_diveder);
        dividerItemDecoration.setDrawable(drawable);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(commonAdapter);
    }

    //完全可见执行
    @Override
    protected void onShow() {
        super.onShow();
    }

    //完全消失执行
    @Override
    protected void onDismiss() {

    }

    @Override
    protected int getMaxHeight() {
        return (int) (XPopupUtils.getWindowHeight(getContext()) * .66f);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
