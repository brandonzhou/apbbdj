package com.shshcom.station.storage.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mt.bbdj.R;
import com.shshcom.station.storage.base.BaseActivity;
import com.shshcom.station.storage.domain.ScanStorageCase;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 处理入库上传失败件
 *
 * @author cst
 */
public class DealWithFailThingsActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.iv_photo)
    ImageView mIvPhoto;

    ScanStorageCase mCase;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, DealWithFailThingsActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected int initView(Bundle savedInstanceState) {
        return R.layout.activity_deal_with_fail_things;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mCase = ScanStorageCase.getInstance();
        mTvTitle.setText("提交入库");

        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_head)
                .placeholder(R.drawable.ic_head);
        Glide.with(this).load(mCase.getOrcResult().getFail_lists().get(0).getPicture()).apply(options).into(mIvPhoto);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
