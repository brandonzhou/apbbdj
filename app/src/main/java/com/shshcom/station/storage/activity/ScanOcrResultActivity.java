package com.shshcom.station.storage.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mt.bbdj.R;
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

    private TextView tv_btn_fail;




    public static void openActivity(Activity activity){
        Intent intent = new Intent(activity, ScanOcrResultActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_scan_ocr_result);

        storageCase = ScanStorageCase.getInstance();

        initView();
        refresh();
    }

    private void initView(){
        tv_scan_total = findViewById(R.id.tv_scan_total);
        tv_scan_success = findViewById(R.id.tv_scan_success);
        tv_scan_fail = findViewById(R.id.tv_scan_fail);

        tv_btn_fail = findViewById(R.id.tv_btn_fail);
        tv_btn_fail.setOnClickListener(this);

    }

    private void refresh(){
        storageCase.httpOcrResult()
                .subscribe(new Observer<BaseResult<StationOrcResult>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

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
                            tv_btn_fail.setVisibility(failSize>0 ? View.VISIBLE: View.GONE);

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showShort(e.getMessage());

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onClick(View v) {

    }
}
