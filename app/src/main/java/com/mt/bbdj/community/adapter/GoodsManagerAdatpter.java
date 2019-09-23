package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.GoodsManagerModel;

import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/8/10
 * @Description :
 */
public class GoodsManagerAdatpter extends RecyclerView.Adapter<GoodsManagerAdatpter.GoodsManagerViewHolder> {

    private List<GoodsManagerModel> mData;

    private Context context;

    private OnItemClickListener onItemClickListener;

    public void setData(List<GoodsManagerModel> mData) {
        this.mData = mData;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public GoodsManagerAdatpter(Context context, List<GoodsManagerModel> list) {
        this.mData = list;
        this.context = context;
    }

    @NonNull
    @Override
    public GoodsManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_goods_manager, parent, false);
        return new GoodsManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsManagerViewHolder holder, int position) {
        GoodsManagerModel goodsManagerModel = mData.get(position);
        Glide.with(context).load(goodsManagerModel.getImageUrl()).into(holder.iv_image);
        holder.tv_goods_name.setText(goodsManagerModel.getGoodsName());
        holder.tv_goods_price.setText(goodsManagerModel.getGoodsPrice());
        holder.tv_goods_xiajia.setVisibility(View.GONE);
        holder.ll_goods_backage.setVisibility(View.GONE);
        holder.tv_goods_name.setTextColor(Color.parseColor("#777777"));
        holder.tv_goods_price.setTextColor(Color.parseColor("#0da95f"));
        holder.tv_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onManagerListener(position);
                }
            }
        });

        if (!"1".equals(goodsManagerModel.getGoodsState())) {
            holder.tv_goods_xiajia.setVisibility(View.VISIBLE);
            holder.ll_goods_backage.setVisibility(View.VISIBLE);
            holder.tv_goods_name.setTextColor(Color.parseColor("#bbbbbb"));
            holder.tv_goods_price.setTextColor(Color.parseColor("#bbbbbb"));
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class GoodsManagerViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_image;
        private TextView tv_goods_name;
        private TextView tv_goods_price;
        private TextView tv_manager;
        private TextView tv_goods_xiajia;
        private View ll_goods_backage;

        public GoodsManagerViewHolder(View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
            tv_goods_name = itemView.findViewById(R.id.tv_goods_name);
            tv_goods_price = itemView.findViewById(R.id.tv_goods_price);
            tv_manager = itemView.findViewById(R.id.tv_manager);
            tv_goods_xiajia = itemView.findViewById(R.id.tv_goods_xiajia);
            ll_goods_backage = itemView.findViewById(R.id.ll_goods_backage);
        }
    }

    public interface OnItemClickListener {
        void onManagerListener(int position);
    }
}
