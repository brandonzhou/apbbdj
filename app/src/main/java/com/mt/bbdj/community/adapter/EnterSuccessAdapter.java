package com.mt.bbdj.community.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.EnterDetailModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2020/4/2
 * @Description :
 */
public class EnterSuccessAdapter extends RecyclerView.Adapter<EnterSuccessAdapter.EnterSuccessViewHolder> {

    private List<EnterDetailModel> mData = new ArrayList<>();

    public EnterSuccessAdapter(List<EnterDetailModel> mData){
        this.mData = mData;
    }

    @NonNull
    @Override
    public EnterSuccessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enter_success,parent,false);
        return new EnterSuccessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnterSuccessViewHolder holder, int position) {
        EnterDetailModel model = mData.get(position);
        holder.item_code.setText(model.getCode());
        holder.item_express_name.setText(model.getExpress_name());
        holder.item_express_state.setText(model.getCurrent_state());
        holder.item_message.setText(model.getMessage());
        holder.item_phone.setText(model.getPhone());
        holder.item_yundan.setText(model.getExpress_yundan());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class EnterSuccessViewHolder extends RecyclerView.ViewHolder{

        TextView item_code;
        TextView item_express_name;
        TextView item_yundan;
        TextView item_phone;
        TextView item_express_state;
        TextView item_message;

        public EnterSuccessViewHolder(@NonNull View itemView) {
            super(itemView);
            item_code = itemView.findViewById(R.id.item_code);
            item_express_name = itemView.findViewById(R.id.item_express_name);
            item_yundan = itemView.findViewById(R.id.item_yundan);
            item_phone = itemView.findViewById(R.id.item_phone);
            item_express_state = itemView.findViewById(R.id.item_express_state);
            item_message = itemView.findViewById(R.id.item_message);
        }
    }
}
