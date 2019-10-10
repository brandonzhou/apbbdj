package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.ExpressMoney;

import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/10/9
 * @Description :
 */
public class ExpressMoneyAdapter extends RecyclerView.Adapter<ExpressMoneyAdapter.ExpressMoneyViewHolder> {

    private List<ExpressMoney> mList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ExpressMoneyAdapter(Context context, List<ExpressMoney> list) {
        mList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ExpressMoneyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.express_money, parent, false);
        return new ExpressMoneyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpressMoneyViewHolder holder, int position) {
        ExpressMoney expressMoney = mList.get(position);
        Glide.with(context).load(expressMoney.getLogo()).into(holder.iv_head);
        holder.tv_name.setText(expressMoney.getName());
        holder.tv_price.setText(expressMoney.getPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ExpressMoneyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_head;
        TextView tv_name;
        TextView tv_price;

        public ExpressMoneyViewHolder(View itemView) {
            super(itemView);
            iv_head = itemView.findViewById(R.id.iv_head);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_price = itemView.findViewById(R.id.tv_price);
        }
    }


    public interface OnItemClickListener{
        void onClick(int position);
    }
}
