package com.mt.bbdj.community.fragment;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.AbstractSimpleFragment;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.EnterDetailModel;
import com.mt.bbdj.baseconfig.model.EventModel;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.EnterSuccessAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;

/**
 * @Author : ZSK
 * @Date : 2020/4/2
 * @Description :
 */
public class EnterSuccessFragment extends AbstractSimpleFragment {

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private String courier_id;
    private final int REQUEST_SUCCESS = 101;

    private List<EnterDetailModel> mData = new ArrayList<>();
    private EnterSuccessAdapter mAdapter;
    private RequestQueue mRequestQueue;

    public static EnterSuccessFragment getInstance(String courier_id) {
        EnterSuccessFragment sf = new EnterSuccessFragment();
        //sf.courier_id = courier_id;
        return sf;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEvent(EventModel eventModel){
        if (eventModel.getTagType() == EventModel.TYPE_FRESH){
            requestData();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (mRequestQueue != null){
            mRequestQueue.stop();
            mRequestQueue.cancelAll();
            mRequestQueue = null;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.enter_success;
    }

    @Override
    protected void initParams() {
        mRequestQueue = NoHttp.newRequestQueue();
        UserBaseMessageDao userMessageDao = GreenDaoManager.getInstance().getSession().getUserBaseMessageDao();
        List<UserBaseMessage> list = userMessageDao.queryBuilder().list();
        if (list.size() != 0) {
            courier_id = list.get(0).getUser_id();
        }
    }

    @Override
    protected void initData() {
        initRecyclerView();
        requestData();
    }

    private void requestData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("station_id", courier_id);
        params.put("type", "1");
        params.put("uuid", UUID.randomUUID().toString());
        Request<String> request = NoHttpRequest.getEnterDateDetail(params);
        mRequestQueue.add(REQUEST_SUCCESS, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(getActivity());
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                try {
                    Log.d("====++", response.get());
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        setData(jsonObject);
                    } else {
                        mData.clear();
                        mAdapter.notifyDataSetChanged();
                        ToastUtil.showShort(msg);
                    }
                    LoadDialogUtils.cannelLoadingDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    LoadDialogUtils.cannelLoadingDialog();
                    ToastUtil.showShort("网络异常请重试！");
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                ToastUtil.showShort("网络异常请重试！");
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    private void setData(JSONObject jsonObject) throws JSONException {
        mData.clear();
        mAdapter.notifyDataSetChanged();
        JSONArray dataArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            EnterDetailModel model = new EnterDetailModel();
            model.setCode(StringUtil.handleNullResultForString(obj.getString("code")));
            model.setExpress_yundan(StringUtil.handleNullResultForString(obj.getString("number")));
            model.setPhone(StringUtil.handleNullResultForString(obj.getString("mobile")));
            model.setCode(StringUtil.handleNullResultForString(obj.getString("code")));
            model.setExpress_id(StringUtil.handleNullResultForString(obj.getString("express_id")));
            model.setExpress_name(StringUtil.handleNullResultForString(obj.getString("express_name")));
            model.setCurrent_state(StringUtil.handleNullResultForString(obj.getString("logistics_status")));
            model.setMessage(StringUtil.handleNullResultForString(obj.getString("sms")));
            model.setPie_id(StringUtil.handleNullResultForString(obj.getString("pie_id")));
            mData.add(model);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        //设置线性布局 Creates a vertical LinearLayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        //    recyclerView.addItemDecoration(new MyDecoration(getActivity(), LinearLayoutManager.VERTICAL, Color.parseColor("#e9e9e9"), 1));
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new EnterSuccessAdapter(mData);
        recyclerView.setAdapter(mAdapter);

    }

}
