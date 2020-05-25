package com.shshcom.station.storage.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.DialogUtil;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.shshcom.station.storage.domain.ScanStorageCase;
import com.shshcom.station.storage.http.bean.BaseResult;
import com.shshcom.station.storage.http.bean.StationOrcResult;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Ocr识别结果
 */
public class ScanOcrResultActivity extends AppCompatActivity implements View.OnClickListener {
    private ScanStorageCase storageCase;
    private TextView tv_scan_total;
    private TextView tv_scan_success;
    private TextView tv_scan_fail;
    private RelativeLayout rl_fail;

    private TextView tv_btn_fail;
    private Button btn_submit;


    private Activity activity;

    private Disposable disposable;

    private boolean isComplete;


    public static void openActivity(Activity activity){
        Intent intent = new Intent(activity, ScanOcrResultActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_scan_ocr_result);

        activity = this;

        storageCase = ScanStorageCase.getInstance();

        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        refresh();
    }

    private void initView(){
        findViewById(R.id.rl_back).setOnClickListener(this);
        tv_scan_total = findViewById(R.id.tv_scan_total);
        tv_scan_success = findViewById(R.id.tv_scan_success);
        tv_scan_fail = findViewById(R.id.tv_scan_fail);

        tv_btn_fail = findViewById(R.id.tv_btn_fail);
        tv_btn_fail.setOnClickListener(this);

        rl_fail = findViewById(R.id.rl_fail);
        rl_fail.setOnClickListener(this);

        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);

        findViewById(R.id.rl_back).setOnClickListener(this);

    }

    private void refresh(){
        if(disposable!= null){
            disposable.dispose();
            LoadDialogUtils.cannelLoadingDialog();
        }
        storageCase.httpOcrResult()
                .subscribe(new Observer<BaseResult<StationOrcResult>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LoadDialogUtils.showLoadingDialog(activity);
                        disposable = d;
                    }

                    @Override
                    public void onNext(BaseResult<StationOrcResult> result) {
                        StationOrcResult orcResult = result.getData();
                        if(orcResult!= null && tv_scan_total!=null){
                            storageCase.setOrcResult(orcResult);
                            int failSize = orcResult.getFail();
                            tv_scan_total.setText(orcResult.getSucceed()+ orcResult.getFail()+"");
                            tv_scan_success.setText(orcResult.getSucceed()+"");

                            tv_scan_fail.setText(failSize+"");

                            isComplete = failSize==0;

                            tv_btn_fail.setVisibility(isComplete ? View.GONE: View.VISIBLE);

                            rl_fail.setClickable(!isComplete);


                        }
                        LoadDialogUtils.cannelLoadingDialog();

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showShort(e.getMessage());
                        LoadDialogUtils.cannelLoadingDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_btn_fail:
            case R.id.rl_fail:
                if(storageCase.getOrcResult()!= null){
                    DealWithFailThingsActivity.openActivity(this);
                }
                break;
            case R.id.btn_submit:
                doSubmit();
                break;
            case R.id.rl_back:
                finish();
                break;
                default:
        }
    }

    private void doSubmit(){
        if(isComplete){
            finish();
        }else {
            DialogUtil.promptDialog(activity,"有错误件未处理");
        }
    }
}
