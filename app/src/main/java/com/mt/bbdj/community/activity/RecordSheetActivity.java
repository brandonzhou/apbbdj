package com.mt.bbdj.community.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mylhyl.circledialog.CircleDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordSheetActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;    //返回
    @BindView(R.id.et_goods_name)
    EditText etGoodsName;     //物品名字
    @BindView(R.id.et_goods_weight)
    EditText etGoodsWeight;   //物品重量
    @BindView(R.id.et_goods_money)
    EditText etGoodsMoney;    //运费
    @BindView(R.id.rb_payfor_now)
    RadioButton rbPayforNow;   //现付
    @BindView(R.id.rb_payfor_by_month)
    RadioButton rbPayforByMonth;    //月付
    @BindView(R.id.rg_payfor_type)
    RadioGroup rgPayforType;
    @BindView(R.id.et_mark)
    EditText etMark;   //备注
    @BindView(R.id.bt_commit_next)
    Button btCommitNext;   //提交

    private String payforType = "现付";    //付款方式
    private String mail_id;   //订单id
    private String user_id;   //用户id

    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;
    private boolean isWaitPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_sheet);
        ButterKnife.bind(this);
        initParams();
        initData();
        initListener();
    }

    private void initData() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(this, "请稍候...");
    }

    private void initParams() {
        Intent intent = getIntent();
        mail_id = intent.getStringExtra("mail_id");
        user_id = intent.getStringExtra("user_id");

        isWaitPrint = intent.getBooleanExtra("waitPrint", false);
        //表示待打印界面传递过来的数据
        String goods_name = intent.getStringExtra("goods_name");
        String goods_weight = intent.getStringExtra("goods_weight");
        String mailing_momey = intent.getStringExtra("mailing_momey");
        String content = intent.getStringExtra("content");

        etGoodsName.setText("null".equals(goods_name)?"":goods_name);
        etGoodsWeight.setText("null".equals(goods_weight)?"":goods_weight);
        etGoodsMoney.setText("null".equals(mailing_momey)?"":mailing_momey);
        etMark.setText("null".equals(content)?"":content);

    }

    private void initListener() {
        rgPayforType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_payfor_now) {      //现付
                    payforType = "现付";
                } else {  //月付
                    payforType = "月结";
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.bt_commit_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_commit_next:
                showPromitDialog();

                break;
        }
    }

    private void commitData() {
        String goods_name = etGoodsName.getText().toString();
        String goods_weight = etGoodsWeight.getText().toString();
        String goods_money = etGoodsMoney.getText().toString();
        String goods_mark = etMark.getText().toString();
        if (!isCompleteData(goods_name, goods_weight, goods_money)) {
            return;
        }

        Request<String> request = null;

        if (isWaitPrint) {   //待打印
            request = NoHttpRequest.waitMimeographRequest(user_id, mail_id, goods_name
                    , goods_weight, goods_money, goods_mark);
        } else {  //待收件
            request = NoHttpRequest.commitRecordMailRequest(user_id, mail_id, goods_name
                    , goods_weight, goods_money, goods_mark);
        }

        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                dialogLoading.show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    LogUtil.i("photoFile--", "RecordSheetActivity::" + jsonObject.toString());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();

                    if ("5001".equals(code)) {
                        ToastUtil.showShort("提交成功！");
                        if (isWaitPrint) {    //立刻打印
                            //savePannelMessage(jsonObject);
                            JSONObject dataObj = jsonObject.getJSONObject("data");
                            String mailing_momey = dataObj.getString("mailing_momey"); //运费
                            String mail_id = dataObj.getString("mail_id"); //运单号
                            String goods_name = dataObj.getString("goods_name"); //商品名称
                            String goods_weight = dataObj.getString("weight"); //计费重量

                            PrintPannelActivity.actionTo(RecordSheetActivity.this,user_id,mail_id,goods_name,goods_weight,mailing_momey);
//                            Intent intent = new Intent(RecordSheetActivity.this,BluetoothSearchActivity.class);
//                            startActivity(intent);
                            EventBus.getDefault().post(new TargetEvent(2));
                        } else {
                            //寄件管理中 点击现存后打
                            EventBus.getDefault().post(new TargetEvent(1));
                        }
                        finish();
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialogLoading.cancel();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                dialogLoading.cancel();
            }

            @Override
            public void onFinish(int what) {
                dialogLoading.cancel();
            }
        });
    }

    private void showPromitDialog() {
        new CircleDialog.Builder()
                .setTitle("提示")
                .setWidth(0.8f)
                .setText("\n尊敬的驿站您好  请确认此次订单已经进行实名认证和开箱验视！\n")
                .setPositive("确认", null)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        commitData();    //提交数据
                    }
                })
                .show(getSupportFragmentManager());
    }

    private void savePannelMessage(JSONObject jsonObject) throws JSONException {
        JSONObject dataObj = jsonObject.getJSONObject("data");
        String mail_id = dataObj.getString("mail_id");       //订单id
        String express_id = dataObj.getString("express_id");     //快递公司id
        String express_name = dataObj.getString("express_name");     //快递公司名称
        String number = dataObj.getString("number");      //驿站代码
        String yundanhao = dataObj.getString("yundanhao");    //运单号
        String code = dataObj.getString("code");   //标识码
        String place = dataObj.getString("place");      //中转地
        String transit = dataObj.getString("transit");     //中转地标识码和时间
        String send_name = dataObj.getString("send_name");    //寄送名称
        String send_phone = dataObj.getString("send_phone");    //寄送电话
        String send_region = dataObj.getString("send_region");    //寄送区域
        String send_address = dataObj.getString("send_address");  //寄送地址
        String collect_name = dataObj.getString("collect_name");   //收件人
        String collect_phone = dataObj.getString("collect_phone"); //收件电话
        String collect_region = dataObj.getString("collect_region");  //收件区域
        String collect_address = dataObj.getString("collect_address");  //收件地址
        String goods_name = dataObj.getString("goods_name");  //商品名称
        String weight = dataObj.getString("weight"); //计费重量
        String mailing_momey = dataObj.getString("mailing_momey"); //计费重量

        SharedPreferences.Editor editor = SharedPreferencesUtil.getEditor();
        editor.putString("mail_id",StringUtil.handleNullResultForString(mail_id));
        editor.putString("express_id",StringUtil.handleNullResultForString(express_id));
        editor.putString("express_name",StringUtil.handleNullResultForString(express_name));
        editor.putString("number",StringUtil.handleNullResultForString(number));
        editor.putString("yundanhao",StringUtil.handleNullResultForString(yundanhao));
        editor.putString("code",StringUtil.handleNullResultForString(code));
        editor.putString("place",StringUtil.handleNullResultForString(place));
        editor.putString("transit",StringUtil.handleNullResultForString(transit));
        editor.putString("send_name",StringUtil.handleNullResultForString(send_name));
        editor.putString("send_phone",StringUtil.handleNullResultForString(send_phone));
        editor.putString("send_region",StringUtil.handleNullResultForString(send_region));
        editor.putString("send_address",StringUtil.handleNullResultForString(send_address));
        editor.putString("collect_name",StringUtil.handleNullResultForString(collect_name));
        editor.putString("collect_phone",StringUtil.handleNullResultForString(collect_phone));
        editor.putString("collect_region",StringUtil.handleNullResultForString(collect_region));
        editor.putString("collect_address",StringUtil.handleNullResultForString(collect_address));
        editor.putString("goods_name",StringUtil.handleNullResultForString(goods_name));
        editor.putString("weight",StringUtil.handleNullResultForString(weight));
        editor.putString("content",etMark.getText().toString());
        editor.putString("mailing_momey",StringUtil.handleNullResultForString(mailing_momey));
        editor.commit();
    }

    private boolean isCompleteData(String goods_name, String goods_weight, String goods_money) {
        if ("".equals(goods_name)) {
            ToastUtil.showShort("请填写物品名称！");
            return false;
        }
        if ("".equals(goods_weight)|| 0.0 ==Float.parseFloat(goods_weight)) {
            ToastUtil.showShort("请填写重量！");
            return false;
        }
        if ("".equals(goods_money)|| 0.0 ==Float.parseFloat(goods_money)) {
            ToastUtil.showShort("请填写运费！");
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
