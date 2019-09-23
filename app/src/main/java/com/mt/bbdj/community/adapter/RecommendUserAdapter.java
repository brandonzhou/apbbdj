package com.mt.bbdj.community.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.HashMap;
import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/8/9
 * @Description :
 */
public class RecommendUserAdapter extends RecyclerView.Adapter<RecommendUserAdapter.RecomendUserViewHolder> {
    private List<HashMap<String, String>> mList;

    public RecommendUserAdapter(List<HashMap<String, String>> paramList) {
        this.mList = paramList;
    }

    public int getItemCount() {
        return this.mList.size();
    }

    public void onBindViewHolder(@NonNull RecomendUserViewHolder viewHolder, int paramInt) {
        Object localObject1 = (HashMap) this.mList.get(paramInt);
        Object localObject3 = (String) ((HashMap) localObject1).get("commodity_name");
        Object localObject2 = (String) ((HashMap) localObject1).get("is_cancel");
        String str1 = (String) ((HashMap) localObject1).get("region");
        String str2 = (String) ((HashMap) localObject1).get("address");
        localObject1 = (String) ((HashMap) localObject1).get("distributor_money");
        viewHolder.tv_type_name.setText((CharSequence) localObject3);
        localObject3 = viewHolder.tv_address;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(str1);
        localStringBuilder.append(" | ");
        localStringBuilder.append(str2);
        ((TextView) localObject3).setText(localStringBuilder.toString());

        if ("2".equals(localObject2)) {
            viewHolder.tv_state.setText("订单取消");
        } else {
            viewHolder.tv_state.setText("分成 : "+localObject1);
        }
    }

    @NonNull
    public RecomendUserViewHolder onCreateViewHolder(@NonNull ViewGroup paramViewGroup, int paramInt) {
        return new RecomendUserViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.item_recommend, paramViewGroup, false));
    }

    class RecomendUserViewHolder extends RecyclerView.ViewHolder {
        TextView tv_address;
        TextView tv_state;
        TextView tv_type_name;

        public RecomendUserViewHolder(View localView) {
            super(localView);
            this.tv_type_name = ((TextView) localView.findViewById(R.id.tv_type_name));
            this.tv_state = ((TextView) localView.findViewById(R.id.tv_state));
            this.tv_address = ((TextView) localView.findViewById(R.id.tv_address));
        }
    }
}
