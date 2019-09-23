package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mt.bbdj.R;

import java.util.List;
import java.util.Map;

/**
 * @Author : ZSK
 * @Date : 2019/8/2
 * @Description :
 */
public class GoodsImageAdapter extends BaseAdapter {

    private List<Map<String, String>> mData;

    private Context context;

    @Override
    public int getCount() {
        return mData.size();
    }

    public GoodsImageAdapter(Context context, List<Map<String, String>> list) {
        this.mData = list;
        this.context = context;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            if (type == 0) {
                convertView = LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.item_add_goods_picture, parent, false);
            } else {
                convertView = LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.layout_picture_demo, parent, false);
                viewHolder.demoImage = convertView.findViewById(R.id.iv_demo_picture);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (type != 0) {
            Map<String, String> data = mData.get(position);
            Glide.with(context).load(data.get("img")).into(viewHolder.demoImage);
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(mData.get(position).get("type"));
    }

    public class ViewHolder {
        ImageView demoImage;
    }
}
