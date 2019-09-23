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

import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/8/21
 * @Description :
 */
public class CouponForUserAdapter extends RecyclerView.Adapter<CouponForUserAdapter.CouponForUserViewHolder> {

    private Context context;

    private  List<UserForCouponModel> mList;

    private OnClickManager onClickManager;

    public void setOnClickManager(OnClickManager onClickManager) {
        this.onClickManager = onClickManager;
    }

    public CouponForUserAdapter(Context context, List<UserForCouponModel> list) {
        this.context = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public CouponForUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_couponfor_user,parent,false);
        return new CouponForUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponForUserViewHolder holder, int position) {
        UserForCouponModel model = mList.get(position);
        if ("2".equals(model.getType())) {
            holder.dispathCoupath.setVisibility(View.VISIBLE);
            holder.haveDispath.setVisibility(View.GONE);
        } else {
            holder.dispathCoupath.setVisibility(View.GONE);
            holder.haveDispath.setVisibility(View.VISIBLE);
        }
        RequestOptions mRequestOptions = RequestOptions.circleCropTransform();
        Glide.with(context).load(model.getHeadImge()).apply(mRequestOptions).into(holder.headImage);
        holder.lastBuyTime.setText(model.getLast_buy_time());
        holder.name.setText(model.getUser_name());

        //发券
        holder.dispathCoupath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickManager != null) {
                    onClickManager.onDispathCouponClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class CouponForUserViewHolder extends RecyclerView.ViewHolder{

        ImageView headImage;    //头像
        TextView name;     //姓名
        TextView dispathCoupath;    //发券
        TextView lastBuyTime;    //最后一次购买时间
        ImageView haveDispath;   //已经发放

        public CouponForUserViewHolder(View itemView) {
            super(itemView);

            headImage = itemView.findViewById(R.id.iv_head);
            name = itemView.findViewById(R.id.tv_name);
            dispathCoupath = itemView.findViewById(R.id.tv_faquan);
            lastBuyTime = itemView.findViewById(R.id.tv_last_purch);
            haveDispath = itemView.findViewById(R.id.tv_have_dispath);
        }
    }


    public interface OnClickManager{

        void onDispathCouponClick(int position);      //发券
    }
}
