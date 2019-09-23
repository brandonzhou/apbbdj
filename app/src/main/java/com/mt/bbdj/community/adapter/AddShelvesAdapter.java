package com.mt.bbdj.community.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.yanzhenjie.nohttp.OkHttpNetwork;

import java.util.HashMap;
import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/9/3
 * @Description :
 */
public class AddShelvesAdapter extends RecyclerView.Adapter<AddShelvesAdapter.AddShelvesViewHolder> {

    private List<HashMap<String,String>> mList;

    private OnActionListener onActionListener;

    public void setOnActionListener(OnActionListener onActionListener){
        this.onActionListener = onActionListener;
    }

    public AddShelvesAdapter(List<HashMap<String,String>> list) {
        this.mList = list;
    }

    public void setData(List<HashMap<String,String>> mlist) {
        this.mList = mlist;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddShelvesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_sheleves,parent,false);
        return new AddShelvesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddShelvesViewHolder holder, int position) {
        HashMap<String,String> map = mList.get(position);
        holder.tv_shelver_name.setText(map.get("name"));
        String haveAdd = map.get("type");
        if ("1".equals(haveAdd)) {
            holder.ll_add_shelves.setVisibility(View.VISIBLE);
            holder.ll_add_shelves_have.setVisibility(View.GONE);
        } else {
            holder.ll_add_shelves.setVisibility(View.GONE);
            holder.ll_add_shelves_have.setVisibility(View.VISIBLE);
        }

        holder.ll_add_shelves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( onActionListener != null) {
                    onActionListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class AddShelvesViewHolder extends RecyclerView.ViewHolder{

        TextView tv_shelver_name;
        LinearLayout ll_add_shelves;
        LinearLayout ll_add_shelves_have;


        public AddShelvesViewHolder(View itemView) {
            super(itemView);
            tv_shelver_name = itemView.findViewById(R.id.tv_shelver_name);
            ll_add_shelves = itemView.findViewById(R.id.ll_add_shelves);
            ll_add_shelves_have = itemView.findViewById(R.id.ll_add_shelves_have);
        }
    }

    public interface  OnActionListener{
        void onItemClick(int position);
    }
}
