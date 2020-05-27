package com.shshcom.station.storage.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lxj.xpopup.XPopup;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.core.GreenDaoUtil;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.shshcom.station.storage.widget.CustomExpressCompanyPopup;
import com.shshcom.station.storage.base.BaseActivity;
import com.shshcom.station.storage.domain.ScanStorageCase;
import com.shshcom.station.storage.http.bean.BaseResult;
import com.shshcom.station.storage.http.bean.ExpressCompany;
import com.shshcom.station.storage.http.bean.OcrResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 处理入库上传失败件
 *
 * @author cst
 */
public class DealWithFailThingsActivity extends BaseActivity {

    ScanStorageCase mCase;
    /*错误件列表*/
    List<OcrResult> mList;
    /*当前件信息*/
    OcrResult curOcrResult;
    /*快递公司列表*/
    ArrayList<ExpressCompany> mExpressCompanies;
    /*总件数*/
    int count;
    /*当前处理索引*/
    int curIndex = 0;
    /*快递公司是否需要修改*/
    boolean isExpressCompanyNeedModify = true;
    /*是否显示提示框module*/
    boolean isShowModule = true;


    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.iv_photo)
    ImageView mIvPhoto;
    @BindView(R.id.tv_fail_title)
    TextView tv_fail_title;
    @BindView(R.id.tv_tracking_company_value)
    TextView mTvTrackingCompanyValue;
    @BindView(R.id.et_phone_value)
    EditText mEtPhoneValue;

    @BindView(R.id.tv_pick_code)
    TextView tv_pick_code;
    @BindView(R.id.tv_bar_code)
    TextView tv_bar_code;


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

        mList = mCase.getOrcResult().getFail_lists();
        count = mList.size();

        mEtPhoneValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        save();
                        break;
                    case EditorInfo.IME_ACTION_NEXT:
                        break;
                }
                return false;
            }
        });

        refreshUI(curIndex);
    }

    private void refreshUI(int index) {
        if (index < count) {
            curOcrResult = null;
            curOcrResult = mList.get(index);
            /*设置图片*/
            RequestOptions options = new RequestOptions()
                    .error(R.drawable.ic_no_picture)
                    .placeholder(R.drawable.ic_finish);
            Glide.with(this).load(curOcrResult.getPicture()).apply(options)
                    .into(mIvPhoto);
            tv_fail_title.setText(curOcrResult.getMsg());
            /*取件码*/
            tv_pick_code.setText("取件码："+curOcrResult.getCode());
            /*快递公司*/
            mTvTrackingCompanyValue.setText(curOcrResult.getExpress_name());
            /*快递单号*/
            tv_bar_code.setText("快递单号："+curOcrResult.getNumber());
            /*手机号码*/
            mEtPhoneValue.setText(curOcrResult.getMobile());

//            if (curOcrResult.getExpress_id() < 1) {
//                isExpressCompanyNeedModify = true;
//            }
        } else {
            ToastUtil.showShort("已完成全部错误件处理");
            finish();
        }

    }


    private void setViewShow(boolean show, int... viewIds){
        for(int id : viewIds){
            findViewById(id).setVisibility(show? View.VISIBLE: View.GONE);
        }
    }




    @OnClick({R.id.tv_tracking_company_value, R.id.btn_delete, R.id.btn_save,R.id.iv_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_photo:
                isShowModule = !isShowModule;
                setViewShow(isShowModule,R.id.group_module);
                break;
            case R.id.tv_tracking_company_value:
                configExpressCompany();
                break;
            case R.id.btn_delete:
                delete();
                break;
            case R.id.btn_save:
                save();
                break;
                default:
        }
    }

    /**
     * 快递公司设置
     */
    private void configExpressCompany() {
        /*快递公司id=0时,说明未识别到快递公司; 需要补录快递公司信息*/
        if (isExpressCompanyNeedModify) {
            if (mExpressCompanies == null) {
                getExpressCompany();
            }else{
                showExpressCompanies();
            }
        }else{
            ToastUtil.showShort("快递公司不可修改");
        }
    }


    /**
     * 删除快递信息
     */
    private void delete(){
        mCase.httpStationSyncDelete("" + curOcrResult.getPie_id()).subscribe(new Observer<BaseResult<Object>>() {
            @Override
            public void onSubscribe(Disposable d) {
                LoadDialogUtils.showLoadingDialog(DealWithFailThingsActivity.this);
            }

            @Override
            public void onNext(BaseResult<Object> baseResult) {
                GreenDaoUtil.deleteScanImage(curOcrResult.getNumber());
                curIndex++ ;
                refreshUI(curIndex);
                LoadDialogUtils.cannelLoadingDialog();
                ToastUtil.showShort(baseResult.getMsg());
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showShort(e.getMessage());
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onComplete() {
                LoadDialogUtils.cannelLoadingDialog();
            }
        });
    }

    /**
     * 获取快递公司列表
     */
    private void save(){
        if (curOcrResult.getExpress_id() < 1 || TextUtils.isEmpty(curOcrResult.getExpress_name())) {
            ToastUtil.showShort("快递公司不能为空");
            return;
        }
        String number = curOcrResult.getNumber();
//        if (TextUtils.isEmpty(number)) {
//            ToastUtil.showShort("快递单号不能为空");
//            return;
//        }
        curOcrResult.setNumber(number);

        String mobile = mEtPhoneValue.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtil.showShort("手机号不能为空");
            return;
        }

        if (!StringUtil.isMobile(mobile)) {
            ToastUtil.showShort("手机号格式不正确");
            return;
        }
        curOcrResult.setMobile(mobile);


        mCase.httpStationUpdatePie(curOcrResult).subscribe(new Observer<BaseResult<Object>>() {
            @Override
            public void onSubscribe(Disposable d) {
                LoadDialogUtils.showLoadingDialog(DealWithFailThingsActivity.this);
            }

            @Override
            public void onNext(BaseResult<Object> baseResult) {
                curIndex++ ;
                refreshUI(curIndex);
                LoadDialogUtils.cannelLoadingDialog();
                ToastUtil.showShort(baseResult.getMsg());
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showShort(e.getMessage());
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onComplete() {
                LoadDialogUtils.cannelLoadingDialog();
            }
        });
    }
    /**
     * 获取快递公司列表
     */
    private void getExpressCompany(){
        mCase.httpGetExpressCompany().subscribe(new Observer<BaseResult<ArrayList<ExpressCompany>>>() {
            @Override
            public void onSubscribe(Disposable d) {
                LoadDialogUtils.showLoadingDialog(DealWithFailThingsActivity.this);
            }

            @Override
            public void onNext(BaseResult<ArrayList<ExpressCompany>> baseResult) {
                LogUtil.d("stringBaseResult", baseResult.getData().toString());
                mExpressCompanies = baseResult.getData();
                showExpressCompanies();
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showShort(e.getMessage());
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onComplete() {
                LoadDialogUtils.cannelLoadingDialog();
            }
        });
    }

    /**
     * 显示快递公司列表
     */
    private void showExpressCompanies() {
        new XPopup.Builder(DealWithFailThingsActivity.this)
                .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                .asCustom(getCustomExpressCompanyPopup(mExpressCompanies)/*.enableDrag(false)*/)
                .show();
    }

    private CustomExpressCompanyPopup getCustomExpressCompanyPopup(ArrayList<ExpressCompany> list) {
        CustomExpressCompanyPopup popup = new CustomExpressCompanyPopup(DealWithFailThingsActivity.this,list);
        popup.setOnItemClickListener(new CustomExpressCompanyPopup.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ExpressCompany expressCompany = list.get(position);
                String name = expressCompany.getExpress_name();
                mTvTrackingCompanyValue.setText(name);

                curOcrResult.setExpress_id(expressCompany.getExpress_id());
                curOcrResult.setExpress_name(name);
            }
        });
        return popup;
    }
}
