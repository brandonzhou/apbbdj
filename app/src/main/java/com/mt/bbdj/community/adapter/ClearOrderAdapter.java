package com.mt.bbdj.community.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer.C;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;

import java.util.List;

public class ClearOrderAdapter extends RecyclerView.Adapter<ClearOrderAdapter.OrderViewHolder> {

    private List<ProductModel> mList;
    private Context context;
    private ExpressInterfaceManager expressInterfaceManager;    //快递订单接口
    public ClearOrderAdapter(Context context,List<ProductModel> data) {
        this.mList = data;
        this.context = context;
    }

    public void setExpressInterfaceManager(ExpressInterfaceManager expressInterfaceManager) {
        this.expressInterfaceManager = expressInterfaceManager;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clear_order,parent,false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder clearViewHolder, int position) {
        ProductModel productModel = mList.get(position);
        boolean isshowType = productModel.isShowType();
        boolean isshowBottom = productModel.isShowBottom();
        int clearState = productModel.getClearState();     //干洗状态

        if (isshowBottom) {
            clearViewHolder.v_bottom.setVisibility(View.VISIBLE);
        } else {
            clearViewHolder.v_bottom.setVisibility(View.GONE);
        }

        //设置状态
        setClearView(clearState, clearViewHolder);
        clearViewHolder.tv_order_state.setText(productModel.getClearStateName());

        clearViewHolder.tv_product_name.setText(productModel.getProductName());
        clearViewHolder.tv_address.setText(productModel.getAddress());
        clearViewHolder.tv_content.setText((DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm",productModel.getContext())+"上门"));
        clearViewHolder.tv_time_state.setText(productModel.getJuli_time());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        clearViewHolder.product_list.setLayoutManager(linearLayoutManager);
        clearViewHolder.product_list.setFocusable(false);
        clearViewHolder.product_list.setNestedScrollingEnabled(false);
        clearViewHolder.product_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    clearViewHolder.itemView.performClick();    //模拟点击
                }
                return false;
            }
        });
        WaterlistAdapter waterAdapter = new WaterlistAdapter(context, productModel.getClearMessageList());
        clearViewHolder.product_list.setAdapter(waterAdapter);

        clearViewHolder.tv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnCallClick(position);
                }
            }
        });

        //提交报价
        clearViewHolder.tv_confirm_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnCommitPrice(position);
                }
            }
        });

        //接单
        clearViewHolder.tv_confirm_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expressInterfaceManager != null) {
                    expressInterfaceManager.OnClearReceiveClick(position);
                }
            }
        });

        //查看干洗详情
        clearViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnCheckClearOrderClick(position);
                }
            }
        });

        //干洗确认送达
        clearViewHolder.tv_confirm_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnConfirmClearSendClick(position);
                }
            }
        });

        //拒绝接单
        clearViewHolder.tv_confirm_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnConfirmRefauseClick(position);
                }
            }
        });
    }

    private void setClearView(int clearState, OrderViewHolder clearViewHolder) {
        switch (clearState) {
            case 1:
                clearViewHolder.ll_action.setVisibility(View.VISIBLE);
                clearViewHolder.tv_confirm_refuse.setVisibility(View.VISIBLE);
                clearViewHolder.tv_call.setVisibility(View.VISIBLE);
                clearViewHolder.tv_confirm_receive.setVisibility(View.VISIBLE);
                clearViewHolder.tv_confirm_price.setVisibility(View.GONE);
                clearViewHolder.tv_confirm_send.setVisibility(View.VISIBLE);
                clearViewHolder.ll_product.setVisibility(View.GONE);
                clearViewHolder.v_splite_product.setVisibility(View.GONE);
                clearViewHolder.tv_order_state.setVisibility(View.GONE);
                clearViewHolder.tv_content.setVisibility(View.VISIBLE);
                break;
            case 2:
                clearViewHolder.ll_action.setVisibility(View.VISIBLE);
                clearViewHolder.tv_call.setVisibility(View.VISIBLE);
                clearViewHolder.tv_confirm_refuse.setVisibility(View.GONE);
                clearViewHolder.tv_confirm_price.setVisibility(View.VISIBLE);
                clearViewHolder.tv_confirm_receive.setVisibility(View.GONE);
                clearViewHolder.tv_confirm_send.setVisibility(View.GONE);
                clearViewHolder.ll_product.setVisibility(View.GONE);
                clearViewHolder.v_splite_product.setVisibility(View.GONE);
                clearViewHolder.tv_content.setVisibility(View.VISIBLE);
                clearViewHolder.tv_order_state.setVisibility(View.GONE);
                break;
            case 5:
            case 6:
                clearViewHolder.ll_action.setVisibility(View.GONE);
                clearViewHolder.ll_product.setVisibility(View.VISIBLE);
                clearViewHolder.tv_confirm_refuse.setVisibility(View.GONE);
                clearViewHolder.v_splite_product.setVisibility(View.GONE);
                clearViewHolder.tv_order_state.setVisibility(View.VISIBLE);
                clearViewHolder.tv_content.setVisibility(View.GONE);
                clearViewHolder.v_splite_product.setVisibility(View.VISIBLE);
                clearViewHolder.tv_order_state.setVisibility(View.VISIBLE);
                break;
            case 7:
                clearViewHolder.ll_action.setVisibility(View.VISIBLE);
                clearViewHolder.tv_call.setVisibility(View.VISIBLE);
                clearViewHolder.tv_confirm_receive.setVisibility(View.GONE);
                clearViewHolder.tv_confirm_price.setVisibility(View.GONE);
                clearViewHolder.tv_confirm_refuse.setVisibility(View.GONE);
                clearViewHolder.tv_confirm_send.setVisibility(View.VISIBLE);
                clearViewHolder.ll_product.setVisibility(View.VISIBLE);
                clearViewHolder.tv_content.setVisibility(View.GONE);
                clearViewHolder.v_splite_product.setVisibility(View.VISIBLE);
                clearViewHolder.tv_order_state.setVisibility(View.VISIBLE);
                break;
            case 8:
                clearViewHolder.ll_action.setVisibility(View.GONE);
                clearViewHolder.ll_product.setVisibility(View.VISIBLE);
                clearViewHolder.tv_order_state.setVisibility(View.VISIBLE);
                clearViewHolder.tv_content.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_product_name;   //名称
        private TextView tv_address;   //小区地址
        private TextView tv_confirm_send;   //确认送达
        private TextView tv_call;   //确认送达
        private View v_bottom;
        private RecyclerView product_list;  //干洗类目
        private TextView tv_content;   //备注
        private TextView tv_order_state;   //订单状态
        private TextView tv_confirm_refuse;     //拒绝接单
        private TextView tv_confirm_price;     //确认报价
        private TextView tv_confirm_receive;     //确认报价
        private LinearLayout ll_action;     //确认报价
        private LinearLayout ll_product;     //产品列表布局
        private View v_splite_product;     //产品列表分割线
        private TextView tv_time_state;     //时间状态

        public OrderViewHolder(View itemView) {
            super(itemView);

            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_confirm_send = itemView.findViewById(R.id.tv_confirm_send);
            v_bottom = itemView.findViewById(R.id.v_bottom);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_order_state = itemView.findViewById(R.id.tv_order_state);
            tv_call = itemView.findViewById(R.id.tv_call);
            product_list = itemView.findViewById(R.id.product_list);
            tv_confirm_refuse = itemView.findViewById(R.id.tv_confirm_refuse);
            tv_confirm_price = itemView.findViewById(R.id.tv_confirm_price);
            tv_confirm_receive = itemView.findViewById(R.id.tv_confirm_receive);
            ll_action = itemView.findViewById(R.id.ll_action);
            ll_product = itemView.findViewById(R.id.ll_product);
            v_splite_product = itemView.findViewById(R.id.v_splite_product);
            tv_time_state = itemView.findViewById(R.id.tv_time_state);
        }
    }

    /**
     * ########################################接口##########################################
     */
    public interface ExpressInterfaceManager {

        void OnCheckClearOrderClick(int position);   //查看干洗订单详情

        void OnConfirmClearSendClick(int position);   //干洗送达

        void OnConfirmRefauseClick(int position);    //干洗拒绝接单

        void OnCallClick(int position);   //打电话

        void OnCommitPrice(int position);    //提交报价

        void OnClearReceiveClick(int position);  //干洗接单

    }
}
