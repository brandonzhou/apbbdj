package com.shshcom.station.storage.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.PickupCode;
import com.shshcom.station.storage.base.BaseActivity;

import androidx.constraintlayout.widget.Group;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.curzbin.library.BottomDialog;
import me.curzbin.library.Item;
import me.curzbin.library.OnItemClickListener;

/**
 * 设置取件码类型
 */
public class SetPickupCodeTypeActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_rule_value)
    TextView mTvRuleValue;
    @BindView(R.id.et_start_number_value)
    EditText mEtStartNumberValue;
    @BindView(R.id.group_date)
    Group mGroupDate;
    @BindView(R.id.group_shelf)
    Group mGroupShelf;
    @BindView(R.id.et_shelf_number_value)
    EditText mEtShelfNumberValue;
    @BindView(R.id.group_start_number_value)
    Group mGroupStartNumberValue;
    @BindView(R.id.group_tail_number_value)
    Group mGroupTailNumberValue;
    @BindView(R.id.tv_shelf_value)
    TextView mTvShelfValue;
    @BindView(R.id.tv_code_value)
    TextView mTvCodeValue;
    @BindView(R.id.group_set_shelf)
    Group mGroupSetShelf;


    /*默认开始编号 - 用户可自定义*/
    String startCode = "1";
    /*日期 - day*/
    @SuppressLint("NewApi")
    String date = "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    /*货号 - 用户自定义*/
    String shelfNum = "A";
    @BindView(R.id.tv_date_value)
    TextView mTvDateValue;

    /*取件码类型*/
    String codeType;

    PickupCode mPickupCode;

    public static void openActivity(Activity context,int requestCode,PickupCode pickupCode) {
        Intent intent = new Intent(context, SetPickupCodeTypeActivity.class);
        intent.putExtra("pickupCode", pickupCode);
        context.startActivityForResult(intent,requestCode);
    }

    @Override
    protected int initView(Bundle savedInstanceState) {
        return R.layout.activity_set_pickup_code_type;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mTvTitle.setText("设置取件码");
        mPickupCode = (PickupCode) getIntent().getSerializableExtra("pickupCode");
        if (null != mPickupCode) {
            codeType = mPickupCode.getType();
            startCode = ""+mPickupCode.getStartNumber();
            mTvRuleValue.setText(codeType);
            if (PickupCode.Type.type_code.getDesc().equals(codeType)) {
                setEditFocus(mEtStartNumberValue,startCode);
            }else{
                setEditFocus(mEtShelfNumberValue,shelfNum);
            }
            mTvCodeValue.setText(startCode);
        }

        setEditTextChangeListener(mEtShelfNumberValue, mTvShelfValue);
        setEditTextChangeListener(mEtStartNumberValue, mTvCodeValue);

        updateUI(codeType);
    }


    /**
     * 设置EditText 文本改变引起的逻辑事件
     *
     * @param primaryEditText   目标输入框
     * @param toUpdateTv        需要更新显示的TextView
     */
    private void setEditTextChangeListener(EditText primaryEditText, TextView toUpdateTv) {
        primaryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                toUpdateTv.setText(s.toString());
            }
        });
    }

    /**
     * 设置EditText焦点
     * @param primaryEditText
     * @param etDefaultText
     */
    private void setEditFocus(EditText primaryEditText, String etDefaultText) {
        primaryEditText.setText(etDefaultText);
        primaryEditText.setFocusable(true);
        primaryEditText.setFocusableInTouchMode(true);
        primaryEditText.requestFocus();
        if (!TextUtils.isEmpty(etDefaultText)) {
            primaryEditText.setSelection(etDefaultText.length());
        }
    }

    private void updateUI(String type) {
        if (PickupCode.Type.type_code.getDesc().equals(type)){
            setTypeCode();
        }else if (PickupCode.Type.type_shelf_code.getDesc().equals(type)){
            setTypeShelfCode();
        }else if (PickupCode.Type.type_shelf_date_code.getDesc().equals(type)){
            setTypeShelfDateCode();
        }else if (PickupCode.Type.type_shelf_date_tail.getDesc().equals(type)){
            setTypeShelfDateTail();
        }else if (PickupCode.Type.type_shelf_tail.getDesc().equals(type)){
            setTypeShelfTail();
        }
    }

    private void setTypeShelfTail() {
        mGroupSetShelf.setVisibility(View.VISIBLE);
        mGroupShelf.setVisibility(View.VISIBLE);
        mGroupStartNumberValue.setVisibility(View.GONE);
        mGroupTailNumberValue.setVisibility(View.VISIBLE);
        mGroupDate.setVisibility(View.GONE);

        mTvShelfValue.setText(mEtShelfNumberValue.getText());
        mTvCodeValue.setText(getString(R.string.tail_number));
    }

    private void setTypeShelfDateTail() {
        mGroupSetShelf.setVisibility(View.VISIBLE);
        mGroupShelf.setVisibility(View.VISIBLE);
        mGroupStartNumberValue.setVisibility(View.GONE);
        mGroupTailNumberValue.setVisibility(View.VISIBLE);
        mGroupDate.setVisibility(View.VISIBLE);

        mTvShelfValue.setText(mEtShelfNumberValue.getText());
        mTvDateValue.setText(date);
        mTvCodeValue.setText(getString(R.string.tail_number));
    }

    private void setTypeShelfDateCode() {
        mGroupSetShelf.setVisibility(View.VISIBLE);
        mGroupShelf.setVisibility(View.VISIBLE);
        mGroupStartNumberValue.setVisibility(View.VISIBLE);
        mGroupTailNumberValue.setVisibility(View.GONE);
        mGroupDate.setVisibility(View.VISIBLE);

        mTvShelfValue.setText(mEtShelfNumberValue.getText());
        mTvDateValue.setText(date);
        mEtStartNumberValue.setText(startCode);
    }

    private void setTypeShelfCode() {
        mGroupSetShelf.setVisibility(View.VISIBLE);
        mGroupShelf.setVisibility(View.VISIBLE);
        mGroupStartNumberValue.setVisibility(View.VISIBLE);
        mGroupTailNumberValue.setVisibility(View.GONE);
        mGroupDate.setVisibility(View.GONE);

        mTvShelfValue.setText(mEtShelfNumberValue.getText());
        mTvCodeValue.setText(startCode);
        mEtStartNumberValue.setText(startCode);
    }

    private void setTypeCode() {
        mGroupSetShelf.setVisibility(View.GONE);
        mGroupShelf.setVisibility(View.GONE);
        mGroupStartNumberValue.setVisibility(View.VISIBLE);
        mGroupTailNumberValue.setVisibility(View.GONE);
        mGroupDate.setVisibility(View.GONE);

        mEtStartNumberValue.setText(startCode);
    }


    @OnClick({R.id.tv_rule_value, R.id.btn_save, R.id.iv_set_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_rule_value:
                break;
            case R.id.btn_save:
                PickupCode pickupCode = new PickupCode();
                pickupCode.setType(codeType);
                pickupCode.setShelfNumber(mTvShelfValue.getText().toString());
                if (PickupCode.Type.type_shelf_tail.getDesc().equals(codeType) || PickupCode.Type.type_shelf_date_tail.getDesc().equals(codeType)) {
                    pickupCode.setStartNumber(-1);
                }else{
                    pickupCode.setStartNumber(Integer.parseInt(mTvCodeValue.getText().toString()));
                }
                Intent intent = new Intent();
                intent.putExtra("pickupCodeRule",pickupCode);
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.iv_set_type:
                showBottomDialog();
                break;
                default:
        }
    }

    /**
     * 底部弹出菜单
     */
    private void showBottomDialog() {
        BottomDialog dialog = new BottomDialog(this);
        dialog.orientation(BottomDialog.VERTICAL)
                .inflateMenu(R.menu.menu_set_code_type, new OnItemClickListener() {
                    @Override
                    public void click(Item item) {
                        mTvRuleValue.setText(item.getTitle());
                        switch (item.getId()) {
                            case R.id.type_code:
                                codeType = PickupCode.Type.type_code.getDesc();
                                setEditFocus(mEtStartNumberValue,mTvCodeValue.getText().toString());
                                break;
                            case R.id.type_shelf_code:
                                codeType = PickupCode.Type.type_shelf_code.getDesc();
                                setEditFocus(mEtShelfNumberValue,mTvShelfValue.getText().toString());
                                break;
                            case R.id.type_shelf_tail:
                                codeType = PickupCode.Type.type_shelf_tail.getDesc();
                                setEditFocus(mEtShelfNumberValue,mTvShelfValue.getText().toString());
                                break;
                            case R.id.type_shelf_date_code:
                                codeType = PickupCode.Type.type_shelf_date_code.getDesc();
                                setEditFocus(mEtShelfNumberValue,mTvShelfValue.getText().toString());
                                break;
                            case R.id.type_shelf_date_tail:
                                codeType = PickupCode.Type.type_shelf_date_tail.getDesc();
                                setEditFocus(mEtShelfNumberValue,mTvShelfValue.getText().toString());
                                break;
                                default:
                        }

                        updateUI(codeType);
                        dialog.dismiss();
                        Toast.makeText(SetPickupCodeTypeActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

}
