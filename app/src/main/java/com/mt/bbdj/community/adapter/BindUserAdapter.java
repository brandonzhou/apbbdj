package com.mt.bbdj.community.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.DateUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : ZSK
 * @Date : 2019/8/9
 * @Description :
 */
public class BindUserAdapter extends RecyclerView.Adapter<BindUserAdapter.BindUserViewHolder> {
    private List<HashMap<String, String>> mList;

    public BindUserAdapter(List<HashMap<String, String>> paramList) {
        this.mList = paramList;
    }

    public int getItemCount() {
        return this.mList.size();
    }

    public void onBindViewHolder(@NonNull BindUserViewHolder paramBindUserViewHolder, int paramInt) {
        Map<String,String> localObject = this.mList.get(paramInt);
        paramBindUserViewHolder.tv_name.setText(localObject.get("username"));
        String time = DateUtil.changeStampToStandrdTime("yyyy-MM-dd", localObject.get("create_time"));
        paramBindUserViewHolder.tv_time.setText(time);
    }

    @NonNull
    public BindUserViewHolder onCreateViewHolder(@NonNull ViewGroup paramViewGroup, int paramInt) {
        return new BindUserViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.item_bing_user, paramViewGroup, false));
    }

    class BindUserViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_time;

        public BindUserViewHolder(View localView) {
            super(localView);
            this.tv_name = ((TextView) localView.findViewById(R.id.tv_name));
            this.tv_time = ((TextView) localView.findViewById(R.id.tv_time));
        }
    }
}