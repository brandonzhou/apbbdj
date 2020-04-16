package com.mt.bbdj.community.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
public class MyClientAdapter extends RecyclerView.Adapter<MyClientAdapter.MyClientViewHolder> {

    private Context context;

    private  List<UserForCouponModel> mList;

    private OnClickManager onClickManager;

    public void setOnClickManager(OnClickManager onClickManager) {
        this.onClickManager = onClickManager;
    }

    public MyClientAdapter(Context context, List<UserForCouponModel> list) {
        this.context = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public MyClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_couponfor_user,parent,false);
        return new MyClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClientViewHolder holder, int position) {
        UserForCouponModel model = mList.get(position);
        holder.dispathCoupath.setVisibility(View.GONE);
        holder.haveDispath.setVisibility(View.GONE);
        RequestOptions mRequestOptions = RequestOptions.circleCropTransform();
        Glide.with(context).load(model.getHeadImge()).apply(mRequestOptions).into(holder.headImage);
        holder.lastBuyTime.setText(model.getLast_buy_time());
        holder.name.setText(model.getUser_name());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyClientViewHolder extends RecyclerView.ViewHolder{

        ImageView headImage;    //头像
        TextView name;     //姓名
        TextView dispathCoupath;    //发券
        TextView lastBuyTime;    //最后一次购买时间
        ImageView haveDispath;   //已经发放

        public MyClientViewHolder(View itemView) {
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
