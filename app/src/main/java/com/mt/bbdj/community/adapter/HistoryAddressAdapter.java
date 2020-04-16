package com.mt.bbdj.community.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/4
 * Description :
 */
public class HistoryAddressAdapter extends RecyclerView.Adapter<HistoryAddressAdapter.FastmailViewHolder> {

    private List<HashMap<String,String>> mData = new ArrayList<>();

    public HistoryAddressAdapter(List<HashMap<String,String>> data) {
        this.mData = data;
    }

    private OnEditClickListener editClickListener;    //编辑

    private OnDeleteClickListener deleteClickListener;   //删除

    private OnItemSelectClickListener itemSelectClickListener;   //选中

    public void setEditClickListener(OnEditClickListener editClickListener) {
        this.editClickListener = editClickListener;
    }

    public void setDeleteClickListener(OnDeleteClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    public void setItemClickListener(OnItemSelectClickListener itemClickListener) {
        this.itemSelectClickListener = itemClickListener;
    }


    @Override
    public FastmailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_address,parent,false);
        return new FastmailViewHolder(view);
    }


    @Override
    public void onBindViewHolder(FastmailViewHolder holder, final int position) {
        if (holder == null) {
            return ;
        }

        HashMap<String,String> item = mData.get(position);
        holder.name.setText(item.get("book_name"));
        holder.phone.setText(item.get("book_telephone"));
        holder.detailAddress.setText(item.get("book_region")+item.get("book_address"));

        //选中
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemSelectClickListener != null) {
                    itemSelectClickListener.onItemSelectClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    class FastmailViewHolder extends RecyclerView.ViewHolder{

        private TextView name;       //姓名
        private TextView phone;      //电话号码
        private TextView detailAddress;    //详细地址

        public FastmailViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_name);
            phone = itemView.findViewById(R.id.tv_item_phone);
            detailAddress = itemView.findViewById(R.id.tv_item_address);
        }
    }

   //################################  接口 ###################################
    //点击编辑的接口
    public interface OnEditClickListener {
        void onClick(int position);
   }

   //点击删除的接口
    public interface OnDeleteClickListener{
        void onClick(int position);
   }

   //点击选择地址
    public interface OnItemSelectClickListener {
        void onItemSelectClick(int position);
    }

}
