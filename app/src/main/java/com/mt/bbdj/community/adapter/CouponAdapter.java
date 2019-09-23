package com.mt.bbdj.community.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.CouponModel;
import com.mt.bbdj.baseconfig.utls.StringUtil;

import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/8/20
 * @Description :   优惠券
 */
public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

    private List<CouponModel> mList;

    private OnClickManager onClickManager;

    public void setOnClickManager(OnClickManager onClickManager) {
        this.onClickManager = onClickManager;
    }

    public CouponAdapter(List<CouponModel> data) {
        this.mList = data;
    }


    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon,parent,false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        CouponModel couponModel = mList.get(position);
        holder.tv_time.setText(couponModel.getStarttime()+" 至 "+couponModel.getEndtime());
        holder.tv_youhui.setText("￥"+couponModel.getTerm_money());
        holder.tv_faquan.setVisibility(View.GONE);
        holder.tv_guoqi.setVisibility(View.GONE);
        holder.ll_guoqi.setVisibility(View.GONE);
        String term_money = StringUtil.handleNullResultForString(couponModel.getTerm_money());    //满足金额
        String reduction_money = StringUtil.handleNullResultForString(couponModel.getReduction_money());     //折扣 金额
        String type = couponModel.getTypes();
        if ("1".equals(type)) {
            //折扣
            holder.tv_youhui_message.setText("满"+term_money+"打"+reduction_money+"折");
            holder.tv_youhui_message_1.setText("满"+term_money+"打"+reduction_money+"折");
        } else {
            holder.tv_youhui_message.setText("满"+term_money+"减"+reduction_money+"元");
            holder.tv_youhui_message_1.setText("满"+term_money+"减"+reduction_money+"元");
        }

        if ("1".equals(couponModel.getEffection())) {
            holder.tv_faquan.setVisibility(View.VISIBLE);
        } else {
            holder.tv_guoqi.setVisibility(View.VISIBLE);
            holder.ll_guoqi.setVisibility(View.VISIBLE);
        }


        //发券
        holder.tv_faquan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickManager != null) {
                    onClickManager.onSendCouponClick(position);
                }
            }
        });

        //优惠券详情
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickManager != null) {
                    onClickManager.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class CouponViewHolder extends RecyclerView.ViewHolder {

        TextView tv_youhui;
        TextView tv_youhui_message;
        TextView tv_youhui_message_1;
        TextView tv_time;
        TextView tv_faquan;
        TextView tv_guoqi;
        View ll_guoqi;
        public CouponViewHolder(View itemView) {
            super(itemView);
            tv_youhui = itemView.findViewById(R.id.tv_youhui);
            tv_youhui_message = itemView.findViewById(R.id.tv_youhui_message);
            tv_youhui_message_1 = itemView.findViewById(R.id.tv_youhui_message_1);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_faquan = itemView.findViewById(R.id.tv_faquan);
            tv_guoqi = itemView.findViewById(R.id.tv_guoqi);
            ll_guoqi = itemView.findViewById(R.id.ll_guoqi);
        }
    }

    //###################################################################
    public interface OnClickManager{

        void onSendCouponClick(int position);     //发券

        void onItemClick(int position);     //点击
    }
}
