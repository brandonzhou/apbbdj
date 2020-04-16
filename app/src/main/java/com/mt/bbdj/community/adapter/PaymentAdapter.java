package com.mt.bbdj.community.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.OrderRecordModel;
import com.mt.bbdj.baseconfig.model.PaymentRecordModel;

import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/11/14
 * @Description :
 */
public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    private List<PaymentRecordModel> mData;

    public PaymentAdapter(List<PaymentRecordModel> list) {
            mData = list;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pay_ment,parent,false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        PaymentRecordModel model = mData.get(position);
        holder.tv_item_title.setText(model.getTitle());
        holder.tv_item_balance.setText("余额：￥"+model.getCon_balance());
        holder.tv_item_yundan.setText("快递单号："+model.getWaybill_number());
        if ("1".equals(model.getBudget())){
            holder.tv_item_money.setText("-￥"+model.getCon_amount());
        } else {
            holder.tv_item_money.setText("+￥"+model.getCon_amount());
        }

        if ("1".equals(model.getTypes())||"2".equals(model.getTypes())) {
            holder.tv_item_yundan.setVisibility(View.VISIBLE);
            holder.v_splite.setVisibility(View.VISIBLE);
        } else {
            holder.tv_item_yundan.setVisibility(View.INVISIBLE);
            holder.v_splite.setVisibility(View.INVISIBLE);
        }
        holder.tv_item_time.setText(model.getTime());

        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null){
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class PaymentViewHolder extends RecyclerView.ViewHolder{
        TextView tv_item_title;
        TextView tv_item_yundan;
        TextView tv_item_money;
        TextView tv_item_time;
        TextView tv_item_balance;
        View v_splite;

        public PaymentViewHolder(View itemView) {
            super(itemView);
            tv_item_title = itemView.findViewById(R.id.tv_item_title);
            tv_item_yundan = itemView.findViewById(R.id.tv_item_yundan);
            tv_item_money = itemView.findViewById(R.id.tv_item_money);
            tv_item_time = itemView.findViewById(R.id.tv_item_time);
            tv_item_balance = itemView.findViewById(R.id.tv_item_balance);
            v_splite = itemView.findViewById(R.id.v_splite);
        }
    }

    public interface OnItemClickListener{
        void  onItemClick(int position);
    }

}
