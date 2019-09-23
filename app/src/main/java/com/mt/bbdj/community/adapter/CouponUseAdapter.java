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
import com.bumptech.glide.request.RequestOptions;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.UserForCouponModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/8/21
 * @Description :
 */
public class CouponUseAdapter extends RecyclerView.Adapter<CouponUseAdapter.CouponUseViewHolder> {

    private Context context;

    private List<HashMap<String, String>> mList = new ArrayList<>();


    public CouponUseAdapter(Context context, List<HashMap<String, String>> list) {
        this.context = context;
        this.mList = list;
    }

    public void setData(List<HashMap<String, String>> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CouponUseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_couponfor_user_detail, parent, false);
        return new CouponUseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponUseViewHolder holder, int position) {
        HashMap<String, String> model = mList.get(position);
        RequestOptions mRequestOptions = RequestOptions.circleCropTransform();
        Glide.with(context).load(model.get("image")).apply(mRequestOptions).into(holder.headImage);
        holder.name.setText(model.get("name"));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class CouponUseViewHolder extends RecyclerView.ViewHolder {

        ImageView headImage;    //头像
        TextView name;     //姓名


        public CouponUseViewHolder(View itemView) {
            super(itemView);

            headImage = itemView.findViewById(R.id.iv_head);
            name = itemView.findViewById(R.id.tv_name);

        }
    }
}
