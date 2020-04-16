package com.mt.bbdj.community.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.ScannerMessageModel;

import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/11/2
 * @Description :s
 */
public class ScannerMessageAdapter extends RecyclerView.Adapter<ScannerMessageAdapter.ScannerMessageViewHolder> {

    private List<ScannerMessageModel> mData;

    private Context mContext;

    private OnClickManager onClickManager;

    public void setOnClickManager(OnClickManager onClickManager) {
        this.onClickManager = onClickManager;
    }

    public ScannerMessageAdapter(Context context, List<ScannerMessageModel> data) {
        mData = data;
        mContext = context;
    }

    @NonNull
    @Override
    public ScannerMessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_scanner, viewGroup, false);
        return new ScannerMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScannerMessageViewHolder viewHolder, int position) {
        ScannerMessageModel model = mData.get(position);
        resetView(viewHolder);    //重置面板

        if (0 == model.getIsHavaPhone() && 0 == model.getIsHaveWayNumber()) {   //表示无运单号、手机号
            viewHolder.tv_promit_title.setText("请扫描手机号");
            viewHolder.tv_phone.setVisibility(View.GONE);
            viewHolder.tv_wait_scan_phone.setVisibility(View.VISIBLE);
        } else if (1 == model.getIsHavaPhone() && 0 == model.getIsHaveWayNumber()) { //表示只有手机号
            viewHolder.tv_promit_title.setText("请扫描条形码");
            viewHolder.ll_way_number_layout.setVisibility(View.GONE);
            viewHolder.tv_wait_scanner_way_number.setVisibility(View.VISIBLE);
            viewHolder.tv_phone.setVisibility(View.VISIBLE);
            viewHolder.tv_wait_scan_phone.setVisibility(View.GONE);
            viewHolder.tv_phone.setText(model.getPhone()); //手机号码
        } else if (0 == model.getIsHavaPhone() && 1 == model.getIsHaveWayNumber()) {  //表示只有运单号
            viewHolder.tv_promit_title.setText("请扫描手机号");
            viewHolder.ll_way_number_layout.setVisibility(View.VISIBLE);
            viewHolder.tv_wait_scanner_way_number.setVisibility(View.GONE);
            viewHolder.tv_phone.setVisibility(View.GONE);
            viewHolder.tv_wait_scan_phone.setVisibility(View.VISIBLE);
        } else if (1 == model.getIsHavaPhone() && 1 == model.getIsHaveWayNumber()) {                                                     //表示信息齐全
            viewHolder.itemView.setBackgroundResource(R.drawable.shape_round_white);
            viewHolder.ll_promit_layout.setVisibility(View.GONE);
            viewHolder.tv_wait_scanner_way_number.setVisibility(View.GONE);
            viewHolder.ll_way_number_layout.setVisibility(View.VISIBLE);
            viewHolder.tv_phone.setVisibility(View.VISIBLE);
            viewHolder.tv_wait_scan_phone.setVisibility(View.GONE);
            viewHolder.tv_phone.setText(model.getPhone()); //手机号码

        }

        viewHolder.tv_code.setText(model.getCode());   //取货码
        viewHolder.tv_way_number.setText(model.getWaybill());  //运单号
        viewHolder.tv_express_name.setText(model.getExpressName());   //快递公司名称

        //删除信息
        viewHolder.iv_delete_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickManager != null) {
                    onClickManager.onRemoveMessage(position, model);
                }
            }
        });

        //编辑信息
        viewHolder.ll_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickManager != null) {
                    onClickManager.onEditMessage(position,model);
                }
            }
        });

        //修改取件码
        viewHolder.tv_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickManager != null) {
                    onClickManager.onChangeCode(position,model);
                }
            }
        });

    }

    private void resetView(ScannerMessageViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundResource(R.drawable.shape_round_yellow);    //背景颜色
        viewHolder.ll_promit_layout.setVisibility(View.VISIBLE);    //信息补全消息
        viewHolder.tv_wait_scanner_way_number.setVisibility(View.VISIBLE);    //待扫描运单信息
        viewHolder.ll_way_number_layout.setVisibility(View.GONE);    //运单信息
        viewHolder.tv_phone.setVisibility(View.GONE);  //手机号码
        viewHolder.tv_wait_scan_phone.setVisibility(View.VISIBLE);   //待扫描手机号
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ScannerMessageViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_promit_layout;    //补充信息提示
        LinearLayout ll_way_number_layout;    //运单信息
        LinearLayout ll_edit;    //编辑信息
        TextView tv_promit_title;   //补充消息标题
        TextView tv_wait_scanner_way_number;   //等待扫描运单号
        TextView tv_wait_scan_phone;   //等待扫描手机号
        TextView tv_code;   //取货码
        TextView tv_way_number;   //运单号
        TextView tv_express_name;   //快递公司
        RelativeLayout iv_delete_message;   //删除
        TextView tv_phone;   //手机号码

        public ScannerMessageViewHolder(View itemView) {
            super(itemView);
            ll_promit_layout = itemView.findViewById(R.id.ll_promit_layout);
            tv_promit_title = itemView.findViewById(R.id.tv_promit_title);
            ll_edit = itemView.findViewById(R.id.ll_edit);
            tv_wait_scanner_way_number = itemView.findViewById(R.id.tv_wait_scanner_way_number);
            ll_way_number_layout = itemView.findViewById(R.id.ll_way_number_layout);
            tv_code = itemView.findViewById(R.id.tv_code);
            tv_way_number = itemView.findViewById(R.id.tv_way_number);
            tv_express_name = itemView.findViewById(R.id.tv_express_name);
            tv_wait_scan_phone = itemView.findViewById(R.id.tv_wait_scan_phone);
            iv_delete_message = itemView.findViewById(R.id.iv_delete_message);
            tv_phone = itemView.findViewById(R.id.tv_phone);
        }
    }


    //添加
    public void addData(int position, ScannerMessageModel scannerMessageModel) {
        notifyItemChanged(position, scannerMessageModel);
        String code = scannerMessageModel.getCode();
        if (1 == scannerMessageModel.getIsHavaPhone() && 1 == scannerMessageModel.getIsHaveWayNumber() && null != code && !"".equals(code)) {
            //insertEmptyData();   //插入空的一条数据
            if (onClickManager != null) {
                onClickManager.onCompliteMessage(position, scannerMessageModel);
            }
        }
    }

    //更新
    public void changeData(int position, ScannerMessageModel scannerMessageModel) {
        notifyItemChanged(position, scannerMessageModel);
        if (1 == scannerMessageModel.getIsHavaPhone() && 1 == scannerMessageModel.getIsHaveWayNumber()) {
            //insertEmptyData();   //插入空的一条数据
            if (onClickManager != null) {
                onClickManager.onUpdateMessage(position, scannerMessageModel);
            }
        }
    }

    public void insertEmptyData() {
        ScannerMessageModel sc = new ScannerMessageModel();
        mData.add(0, sc);
        notifyItemInserted(0);
        notifyDataSetChanged();
    }


    //##############################    点击事件 ########################################
    public interface OnClickManager {

        void onEditMessage(int position,ScannerMessageModel data);    //编辑信息

        void onCompliteMessage(int position, ScannerMessageModel data);  //完成

        void onRemoveMessage(int position, ScannerMessageModel data);   //删除

        void onUpdateMessage(int position, ScannerMessageModel data);  //更新

        void onChangeCode(int position,ScannerMessageModel model);   //修改取件码

    }


}
