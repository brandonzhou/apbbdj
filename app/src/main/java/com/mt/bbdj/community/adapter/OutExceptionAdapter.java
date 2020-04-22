package com.mt.bbdj.community.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mt.bbdj.R;

import java.util.List;
import java.util.Map;

/**
 * Author : ZSK
 * Date : 2020/4/22
 * Description :
 */
public class OutExceptionAdapter extends RecyclerView.Adapter<OutExceptionAdapter.OutExceptionViewHolder> {

    private List<Map<String, String>> mData;

    private OnItemClickListener onItemClickListener;

    private int currentPosition = -1;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public OutExceptionAdapter(List<Map<String, String>> data) {
        this.mData = data;
    }

    public void setCurrentPosition(int position) {
        currentPosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OutExceptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_out_exception, parent, false);
        return new OutExceptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutExceptionViewHolder holder, int position) {
        Map<String,String> map = mData.get(position);
        holder.item_out_exception.setText(map.get("exception"));
        if (currentPosition == position){
            holder.item_out_exception.setBackgroundResource(R.color.grey_8);
        } else {
            holder.item_out_exception.setBackgroundResource(R.color.whilte);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class OutExceptionViewHolder extends RecyclerView.ViewHolder {

        TextView item_out_exception;
        public OutExceptionViewHolder(@NonNull View itemView) {
            super(itemView);
            item_out_exception = itemView.findViewById(R.id.item_out_exception);
        }
    }

    public interface OnItemClickListener{

        void onItemClick(int position);
    }
}
