package com.mt.bbdj.community.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : ZSK
 * @Date : 2019/10/29
 * @Description :
 */
public class BluetoothScanAdapter extends RecyclerView.Adapter<BluetoothScanAdapter.BluetoothViewHolder> {

    private List<HashMap<String,String>> mdata;

    private OnItemConnectClickListener connectClickListener;

    public BluetoothScanAdapter(List<HashMap<String,String>> data) {
        this.mdata = data;
    }

    //点击打印
    public void setConnectClickListener(OnItemConnectClickListener connectClickListener) {
        this.connectClickListener = connectClickListener;
    }
    @NonNull
    @Override
    public BluetoothViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth,parent,false);
        return new BluetoothViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothViewHolder holder, int position) {

        holder.item_bluetooth_name.setText(mdata.get(position).get("name"));

        //连接打印
        holder.item_bluetooth_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectClickListener != null) {
                    connectClickListener.onConnect(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    class BluetoothViewHolder extends RecyclerView.ViewHolder{

        TextView item_bluetooth_name;
        TextView item_bluetooth_print;
        public BluetoothViewHolder(View itemView) {
            super(itemView);
            item_bluetooth_name = itemView.findViewById(R.id.item_bluetooth_name);
            item_bluetooth_print = itemView.findViewById(R.id.item_bluetooth_print);
        }
    }

    //连接
    public interface OnItemConnectClickListener{
        void onConnect(int position);
    }

}
