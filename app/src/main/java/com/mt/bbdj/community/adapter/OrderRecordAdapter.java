package com.mt.bbdj.community.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.OrderRecordModel;

import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/11/14
 * @Description :
 */
public class OrderRecordAdapter extends RecyclerView.Adapter<OrderRecordAdapter.OrderRecordViewHolder> {

    private List<OrderRecordModel> mData;

    public OrderRecordAdapter(List<OrderRecordModel> list) {
            mData = list;
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public OrderRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_recorde,parent,false);
        return new OrderRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderRecordViewHolder holder, int position) {
        OrderRecordModel model = mData.get(position);

        holder.tv_item_cut_time.setText("（扣款时间"+model.getCallback_time()+"）");
        holder.tv_item_title.setText("-￥"+model.getSettle_money());
        holder.tv_item_dingdan.setText("订单号"+model.getOrder_number());
        holder.tv_item_yundan.setText("运单号"+model.getWaybill_number());
        holder.tv_item_time.setText(model.getCreate_time());

        if ("1".equals(model.getCallback_states())) {
            holder.tv_item_title.setText("等待快递公司返回重量（未扣款）");
            holder.tv_item_cut_time.setVisibility(View.INVISIBLE);
        } else if (!"2".equals(model.getStates())){
            holder.tv_item_title.setText("无快递单号");
            holder.tv_item_yundan.setText("无");
            holder.tv_item_cut_time.setVisibility(View.INVISIBLE);
        }else {
            holder.tv_item_title.setText("-￥"+model.getSettle_money());
            holder.tv_item_cut_time.setVisibility(View.VISIBLE);
        }

        if ("1".equals(model.getFlag())) {
            holder.tv_item_order_state.setText("正常");
        } else {
            holder.tv_item_order_state.setText("已取消");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class OrderRecordViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_item_title;
        private TextView tv_item_cut_time;
        private TextView tv_item_order_state;
        private TextView tv_item_dingdan;
        private TextView tv_item_yundan;
        private TextView tv_item_time;

        public OrderRecordViewHolder(View itemView) {
            super(itemView);
            tv_item_title = itemView.findViewById(R.id.tv_item_title);
            tv_item_cut_time = itemView.findViewById(R.id.tv_item_cut_time);
            tv_item_order_state = itemView.findViewById(R.id.tv_item_order_state);
            tv_item_dingdan = itemView.findViewById(R.id.tv_item_dingdan);
            tv_item_yundan = itemView.findViewById(R.id.tv_item_yundan);
            tv_item_time = itemView.findViewById(R.id.tv_item_time);
        }
    }

    public interface OnClickListener{

        void onItemClick(int position);
    }

}
