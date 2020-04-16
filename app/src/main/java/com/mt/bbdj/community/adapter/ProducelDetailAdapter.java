package com.mt.bbdj.community.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.StringUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/22
 * Description :
 */
public class ProducelDetailAdapter extends RecyclerView.Adapter<ProducelDetailAdapter.SimpleViewHolder> {

    private List<HashMap<String, String>> mList;

    private Context context;

    public ProducelDetailAdapter(Context context, List<HashMap<String, String>> list) {

        this.context = context;

        this.mList = list;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_simple_product,parent,false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        if (holder == null) {
             return;
        }
        HashMap<String, String> data = mList.get(position);
        holder.waterType.setText(data.get("product_title"));
        String price = data.get("price");
        String number = data.get("number");
        double singlePrice = StringUtil.changeStringToDouble(price);
        int productNumber = StringUtil.changeStringToInt(number);
        double allPrice = singlePrice* productNumber;
        holder.waterNumber.setText(price+"元*"+number);
        holder.allprice.setText(allPrice+"元");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        private TextView waterType;    //桶装水品牌
        private TextView waterNumber;   //桶装水数量
        private TextView allprice;   //总价格

        public SimpleViewHolder(View itemView) {
            super(itemView);
            waterType = itemView.findViewById(R.id.id_name);
            waterNumber = itemView.findViewById(R.id.id_water_number);
            allprice = itemView.findViewById(R.id.id_price_all);
        }
    }

}
