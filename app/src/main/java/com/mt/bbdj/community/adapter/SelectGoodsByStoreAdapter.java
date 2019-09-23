package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.StoreGoodsModel;

import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/8/22
 * @Description :
 */
public class SelectGoodsByStoreAdapter extends RecyclerView.Adapter<SelectGoodsByStoreAdapter.SelectGoodsByStoreViewHolder> {

    private List<StoreGoodsModel> mList;

    private Context context;

    private OnClickManager onClickManager;

    public void setOnClickManager(OnClickManager onClickManager) {
        this.onClickManager = onClickManager;
    }


    public SelectGoodsByStoreAdapter(Context context,List<StoreGoodsModel> mList) {
        this.context = context;
        this.mList = mList;
    }

    public void setData(List<StoreGoodsModel> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public SelectGoodsByStoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_select_goods, parent, false);
        return new SelectGoodsByStoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectGoodsByStoreViewHolder holder, int position, @NonNull List<Object> payloads) {

        if (payloads.isEmpty()) {
            onBindViewHolder(holder,position);
        } else {
            holder.tv_goods_add.setVisibility(View.GONE);
            holder.tv_goods_add_finish.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SelectGoodsByStoreViewHolder holder, int position) {
        StoreGoodsModel storeGoodsModel = mList.get(position);
        Glide.with(context).load(storeGoodsModel.getGoods_img()).into(holder.tv_goods_image);
        holder.tv_goods_name.setText(storeGoodsModel.getGoods_name());
        holder.tv_price.setText("建议价: " + storeGoodsModel.getGoods_price() + "元");
        if ("1".equals(storeGoodsModel.getFlag())) {
            holder.tv_goods_add.setVisibility(View.VISIBLE);
            holder.tv_goods_add_finish.setVisibility(View.GONE);
        } else {
            holder.tv_goods_add.setVisibility(View.GONE);
            holder.tv_goods_add_finish.setVisibility(View.VISIBLE);
        }

        //添加
        holder.tv_goods_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickManager != null) {
                    onClickManager.onAddClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class SelectGoodsByStoreViewHolder extends RecyclerView.ViewHolder {

        ImageView tv_goods_image;
        AppCompatTextView tv_goods_name;
        TextView tv_price;
        TextView tv_goods_add;
        TextView tv_goods_add_finish;

        public SelectGoodsByStoreViewHolder(View itemView) {
            super(itemView);
            tv_goods_image = itemView.findViewById(R.id.tv_goods_image);
            tv_goods_name = itemView.findViewById(R.id.tv_goods_name);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_goods_add = itemView.findViewById(R.id.tv_goods_add);
            tv_goods_add_finish = itemView.findViewById(R.id.tv_goods_add_finish);
        }
    }


    public interface OnClickManager {

        void onAddClick(int position);  //添加
    }
}
