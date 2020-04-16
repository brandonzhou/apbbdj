package com.mt.bbdj.community.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.EnterDetailModel;

import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2020/4/2
 * @Description :
 */
public class EnterFailureAdapter extends RecyclerView.Adapter<EnterFailureAdapter.EnterFailureViewHolder> {

    private List<EnterDetailModel> mData;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public EnterFailureAdapter(List<EnterDetailModel> list) {
        this.mData = list;
    }



    @NonNull
    @Override
    public EnterFailureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enter_failure, parent, false);
        return new EnterFailureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnterFailureViewHolder holder, int position) {
        EnterDetailModel enterDetailModel = mData.get(position);
        holder.item_change_phone.setText(enterDetailModel.getPhone());
        holder.item_change_yundan.setText(enterDetailModel.getExpress_yundan());
        holder.item_express.setText(enterDetailModel.getExpress_name());
        holder.item_code.setText(enterDetailModel.getCode());

        if (onItemClickListener != null){
            holder.itemView.setOnClickListener(view->{
                onItemClickListener.onChangeExpress(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class EnterFailureViewHolder extends RecyclerView.ViewHolder {

        TextView item_express;
        TextView item_change_yundan;
        TextView item_change_phone;
        TextView item_code;

        public EnterFailureViewHolder(@NonNull View itemView) {
            super(itemView);
            item_express= itemView.findViewById(R.id.item_express);
            item_change_yundan= itemView.findViewById(R.id.item_change_yundan);
            item_change_phone= itemView.findViewById(R.id.item_change_phone);
            item_code= itemView.findViewById(R.id.item_code);
        }
    }

    public interface OnItemClickListener {
        void onChangeExpress(int position);   //改变快递公司

    }
}
