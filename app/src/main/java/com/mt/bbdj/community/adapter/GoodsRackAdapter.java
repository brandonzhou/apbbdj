package com.mt.bbdj.community.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.Goods;

import java.util.List;
import java.util.Map;

/**
 * @Author : ZSK
 * @Date : 2019/7/30
 * @Description :
 */
public class GoodsRackAdapter extends RecyclerView.Adapter<GoodsRackAdapter.GoodsRackViewHolder> {

    private List<Goods> data;

    private int clickPosition = 0;    //点击的位置

    private OnClickListener onClickListener;

    public void setData(List<Goods> data) {
        this.data = data;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setClickPosition(int clickPosition) {
        this.clickPosition = clickPosition;
    }

    public GoodsRackAdapter(List<Goods> data) {
        this.data = data;
    }


    @NonNull
    @Override
    public GoodsRackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_rack, parent, false);
        return new GoodsRackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsRackViewHolder holder, int position) {
        Goods goods = data.get(position);
        holder.item_title.setText(goods.getShelces_name());
        holder.item_number.setText(goods.getSpecs());
        if (clickPosition == position) {
            holder.v_select.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundResource(R.color.grey_6);
        } else {
            holder.v_select.setVisibility(View.INVISIBLE);
            holder.itemView.setBackgroundResource(R.color.whilte);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class GoodsRackViewHolder extends RecyclerView.ViewHolder {

        TextView item_title;
        TextView item_number;
        View v_select;

        public GoodsRackViewHolder(View itemView) {
            super(itemView);
            item_title = itemView.findViewById(R.id.item_title);
            item_number = itemView.findViewById(R.id.item_number);
            v_select = itemView.findViewById(R.id.v_select);
        }
    }


    //################################################################
    public interface OnClickListener {

        void onClick(int position);
    }
}
