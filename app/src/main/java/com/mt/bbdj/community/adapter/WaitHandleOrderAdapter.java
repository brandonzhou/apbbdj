package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.TakeOutModel;

import java.io.LineNumberReader;
import java.util.List;

public class WaitHandleOrderAdapter extends RecyclerView.Adapter<WaitHandleOrderAdapter.OrderViewHolder> {

    private List<TakeOutModel> mList;
    private Context context;
    private boolean isShow = false;

    private boolean  isShowGoods = false;

    public WaitHandleOrderAdapter(Context context, List<TakeOutModel> data) {
        this.context = context;
        this.mList = data;
    }

    private WaitHandleOrderManager waitHandleOrderManager;    //订单接口

    public void setOnClickManagerListener(WaitHandleOrderManager expressInterfaceManager) {
        this.waitHandleOrderManager = expressInterfaceManager;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wait_handle_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position) {
        TakeOutModel productModel = mList.get(position);

        String states = productModel.getOrderState();


        if (waitHandleOrderManager != null) {
            //订单详情
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    waitHandleOrderManager.OnCheckDetailClick(position);
                }
            });
            //接单
            viewHolder.tv_confirm_receive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    waitHandleOrderManager.OnReceiveOrderClick(position);
                }
            });
            //取消订单
            viewHolder.tv_cannel_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    waitHandleOrderManager.OnCannelOrderClick(position);
                }
            });
            //取消订单
            viewHolder.rl_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    waitHandleOrderManager.OnCallPhoneClick(position);
                }
            });

            //确认送达
            viewHolder.tv_confirm_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    waitHandleOrderManager.OnConfirmSendClick(position);
                }
            });

            //配送
            viewHolder.tv_dispacth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    waitHandleOrderManager.OnDispathingClick(position);
                }
            });

            //配送方式
            viewHolder.tv_send_by_me.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    waitHandleOrderManager.OnChangeSendType(position);
                }
            });
        }

      /*  viewHolder.ll_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isShow = !isShow;
                viewHolder.product_list.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        });*/



        viewHolder.tv_address.setText(productModel.getAddress());
        viewHolder.tv_time_state.setText(productModel.getEstimatedTime() + " " + productModel.getCurrentTimeState());
        viewHolder.tv_name.setText(productModel.getName());

        String state = productModel.getOrderState();
        String tag = "";
        viewHolder.tv_confirm_send.setVisibility(View.GONE);
        viewHolder.tv_confirm_receive.setVisibility(View.GONE);
        viewHolder.tv_dispacth.setVisibility(View.GONE);
        viewHolder.tv_cannel_order.setVisibility(View.VISIBLE);
        viewHolder.ll_function.setVisibility(View.VISIBLE);
        viewHolder.tv_send_by_me.setVisibility(View.GONE);

        resetButtonState(viewHolder);
        if ("1".equals(state)) {
            tag = "待接单";
            viewHolder.tv_cannel_order.setVisibility(View.VISIBLE);
            viewHolder.tv_confirm_receive.setVisibility(View.VISIBLE);
        } else if ("2".equals(state)) {
            tag = "已接单";
            viewHolder.tv_cannel_order.setVisibility(View.GONE);
            viewHolder.tv_dispacth.setVisibility(View.VISIBLE);
        } else if ("3".equals(state)) {
            tag = "配送中";
            viewHolder.tv_confirm_send.setVisibility(View.VISIBLE);
            viewHolder.tv_confirm_receive.setVisibility(View.GONE);
            viewHolder.tv_dispacth.setVisibility(View.GONE);
            viewHolder.tv_cannel_order.setVisibility(View.GONE);
        } else if ("4".equals(state)){
            tag = "等待用户确认";
            viewHolder.ll_function.setVisibility(View.GONE);
           /* viewHolder.tv_confirm_send.setVisibility(View.GONE);
            viewHolder.tv_confirm_receive.setVisibility(View.GONE);
            viewHolder.tv_dispacth.setVisibility(View.GONE);
            viewHolder.tv_cannel_order.setVisibility(View.GONE);*/
        } else if ("10".equals(state)) {
            tag = "等待快递员接单";
            viewHolder.ll_function.setVisibility(View.VISIBLE);
            viewHolder.tv_send_by_me.setVisibility(View.VISIBLE);
        } else if ("11".equals(state)) {
            tag = "快递员配送中";
            viewHolder.ll_function.setVisibility(View.GONE);
        }else if ("12".equals(state)) {
            tag = "等待快递员取件";
            viewHolder.ll_function.setVisibility(View.VISIBLE);
        }else {
            tag = "订单已完成";
            viewHolder.ll_function.setVisibility(View.GONE);
        }
        viewHolder.tv_order_state.setText(tag);

    /*    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        viewHolder.product_list.setLayoutManager(linearLayoutManager);
        viewHolder.product_list.setFocusable(false);
        viewHolder.product_list.setNestedScrollingEnabled(false);
        viewHolder.product_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    viewHolder.itemView.performClick();    //模拟点击
                }
                return false;
            }
        });
        ProducelistAdapter waterAdapter = new ProducelistAdapter(context, productModel.getTakeOutList());
        viewHolder.product_list.setAdapter(waterAdapter);
*/
    }

    private void resetButtonState(OrderViewHolder viewHolder) {
        viewHolder.tv_confirm_send.setVisibility(View.GONE);
        viewHolder.tv_confirm_receive.setVisibility(View.GONE);
        viewHolder.tv_dispacth.setVisibility(View.GONE);
        viewHolder.tv_cannel_order.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_address;   //小区地址
        private TextView tv_confirm_send;   //确认送达
        private TextView tv_confirm_receive;   //确认接单
        private TextView tv_dispacth;   //去配送
        private TextView tv_send_by_me;   //去配送
        private RecyclerView product_list;  //桶装水品牌
        private TextView tv_cannel_order;    //取消

        private TextView tv_order_state;    //订单状态
        private TextView tv_time_state;    //时间状态
        private TextView tv_name;    //姓名
        private RelativeLayout rl_phone;    //电话
        private LinearLayout ll_product;    //商品列表
        private ImageView iv_select;    //商品列下拉框

        private LinearLayout ll_function;

        public OrderViewHolder(View itemView) {
            super(itemView);

            tv_order_state = itemView.findViewById(R.id.tv_order_state);
            tv_time_state = itemView.findViewById(R.id.tv_time_state);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_name = itemView.findViewById(R.id.tv_name);
            rl_phone = itemView.findViewById(R.id.rl_phone);
            tv_confirm_send = itemView.findViewById(R.id.tv_confirm_send);
            product_list = itemView.findViewById(R.id.product_list);
            tv_confirm_receive = itemView.findViewById(R.id.tv_confirm_receive);
            tv_cannel_order = itemView.findViewById(R.id.tv_cannel_order);
            iv_select = itemView.findViewById(R.id.iv_select);
            tv_send_by_me = itemView.findViewById(R.id.tv_send_by_me);
            ll_product = itemView.findViewById(R.id.ll_product);
            tv_dispacth = itemView.findViewById(R.id.tv_dispacth);
            ll_function = itemView.findViewById(R.id.ll_function);
        }
    }

    /**
     * ########################################接口##########################################
     */

    public interface WaitHandleOrderManager {

        void OnCheckDetailClick(int position);    //查看详情

        void OnReceiveOrderClick(int position);    //接单

        void OnConfirmSendClick(int position);    //确认送达

        void OnCallPhoneClick(int position);      //打电话

        void OnCannelOrderClick(int position);     //取消订单

        void OnDispathingClick(int position);    //去配送

        void OnChangeSendType(int position);    //改变配送方式
    }

}
