package com.mt.bbdj.community.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;

import java.util.HashMap;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<ProductModel> mList;
    private Context context;

    public OrderAdapter(Context context,List<ProductModel> data) {
        this.context = context;
        this.mList = data;
    }

    private ExpressInterfaceManager expressInterfaceManager;    //订单接口

    public void setOnCheckDetailListener(ExpressInterfaceManager expressInterfaceManager) {
        this.expressInterfaceManager = expressInterfaceManager;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_water_product, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder waterViewHolder, int position) {
        ProductModel productModel = mList.get(position);
        boolean isshowType = productModel.isShowType();
        boolean isshowBottom = productModel.isShowBottom();
        String states = productModel.getStates();

        if ("1".equals(states)) {
            waterViewHolder.tv_confirm_receive.setVisibility(View.VISIBLE);
            waterViewHolder.tv_confirm_send.setVisibility(View.GONE);
        } else if ("2".equals(states)) {
            waterViewHolder.tv_confirm_receive.setVisibility(View.GONE);
            waterViewHolder.tv_confirm_send.setVisibility(View.VISIBLE);
        }

        if (isshowBottom) {
            waterViewHolder.v_bottom.setVisibility(View.VISIBLE);
        } else {
            waterViewHolder.v_bottom.setVisibility(View.GONE);
        }

        //打电话
        waterViewHolder.tv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnCallClick(position);
                }
            }
        });

        //桶装水的详情
        waterViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnCheckWaterOrderClick(position);
                }
            }
        });

        //桶装水取消订单
        waterViewHolder.tv_cannel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnConfirWaterCannelClick(position);
                }
            }
        });

        //确认桶装水接单
        waterViewHolder.tv_confirm_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnConfirmWaterReceiveClick(position);
                }
            }
        });

        //确认送达
        waterViewHolder.tv_confirm_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnConfirmWaterSendClick(position);
                }
            }
        });

        waterViewHolder.tv_product_name.setText(productModel.getProductName());
        waterViewHolder.tv_address.setText(productModel.getAddress());
        waterViewHolder.tv_content.setText(DateUtil.changeStampToStandrdTime("HH:mm", productModel.getContext()) + "上门");
        waterViewHolder.tv_time_state.setText(productModel.getJuli_time());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        waterViewHolder.product_list.setLayoutManager(linearLayoutManager);
        waterViewHolder.product_list.setFocusable(false);
        waterViewHolder.product_list.setNestedScrollingEnabled(false);
        waterViewHolder.product_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    waterViewHolder.itemView.performClick();    //模拟点击
                }
                return false;
            }
        });
        WaterlistAdapter waterAdapter = new WaterlistAdapter(context, productModel.getWaterMessageList());
        waterViewHolder.product_list.setAdapter(waterAdapter);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_product_name;   //名称
        private TextView tv_address;   //小区地址
        private TextView tv_phone;   //电话
        private TextView tv_confirm_send;   //确认送达
        private TextView tv_confirm_receive;   //确认接单
        private View v_bottom;
        private TextView tv_content;   //备注
        private RecyclerView product_list;  //桶装水品牌
        private TextView tv_call;   //打电话
        private TextView tv_cannel_order;    //取消
        private TextView tv_order_state;    //订单状态
        private TextView tv_time_state;   //时间状态 超时，，或者还剩多少时间

        public OrderViewHolder(View itemView) {
            super(itemView);

            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            tv_confirm_send = itemView.findViewById(R.id.tv_confirm_send);
            tv_confirm_receive = itemView.findViewById(R.id.tv_confirm_receive);
            v_bottom = itemView.findViewById(R.id.v_bottom);
            tv_content = itemView.findViewById(R.id.tv_content);
            product_list = itemView.findViewById(R.id.product_list);
            tv_call = itemView.findViewById(R.id.tv_call);
            tv_cannel_order = itemView.findViewById(R.id.tv_cannel_order);
            tv_time_state = itemView.findViewById(R.id.tv_time_state);
        }
    }

    /**
     * ########################################接口##########################################
     */
    public interface ExpressInterfaceManager {

        void OnCheckWaterOrderClick(int position);   //查看桶装水详情

        void OnConfirmWaterReceiveClick(int position);  //确认桶装水接单

        void OnConfirmWaterSendClick(int position);  //确认桶装水送达

        void OnConfirWaterCannelClick(int position);   //桶装水取消订单

        void OnCallClick(int position);   //打电话

    }

}
