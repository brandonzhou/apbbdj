package com.mt.bbdj.community.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.activity.CouponForUserActivity;
import com.mt.bbdj.community.adapter.CouponForUserAdapter;
import com.mt.bbdj.community.adapter.CouponUseAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : ZSK
 * @Date : 2019/8/31
 * @Description :
 */
public class CouponUserFragment extends Fragment implements XRecyclerView.LoadingListener {

    private String user_id;

    private String type;
    private String coupon_id;

    private XRecyclerView recyclerView;
    private TextView tv_no_address;

    private List<HashMap<String, String>> mList = new ArrayList<>();

    private CouponUseAdapter mAdapter;
    private RequestQueue mRequestQueue;

    public CouponUserFragment() {
    }

    @SuppressLint("ValidFragment")
    public CouponUserFragment(String user_id, String type, String coupon_id) {
        this.type = type;
        this.user_id = user_id;
        this.coupon_id = coupon_id;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupon_user, container, false);
        initView(view);
        initParams();
        requestData();
        return view;
    }

    private void initParams() {
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler);
        tv_no_address = view.findViewById(R.id.tv_no_address);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setLoadingListener(this);
        recyclerView.addItemDecoration(new MyDecoration(getActivity(), LinearLayoutManager.VERTICAL, Color.parseColor("#f8f8f8"), 1));
        mAdapter = new CouponUseAdapter(getActivity(), mList);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(getActivity()).resumeRequests();//恢复Glide加载图片
                } else {
                    Glide.with(getActivity()).pauseRequests();//禁止Glide加载图片
                }
            }
        });
    }

    //获取使用记录
    private void requestData() {
        Map<String, String> params = new HashMap<>();
        params.put("distributor_id", user_id);
        params.put("type", type);
        params.put("coupon_id", coupon_id);
        Request<String> request = NoHttpRequest.getCouponUseRequest(params);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(getActivity());
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "CouponUserFragment::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    if ("5001".equals(code)) {
                        setData(jsonObject);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                    LoadDialogUtils.cannelLoadingDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    LoadDialogUtils.cannelLoadingDialog();
                }

            }

            @Override
            public void onFailed(int what, Response<String> response) {
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onFinish(int what) {

            }
        });

    }

    private void setData(JSONObject jsonObject) throws JSONException {
        JSONArray dataArray = jsonObject.getJSONArray("data");
        mList.clear();
        mAdapter.notifyDataSetChanged();
        if (dataArray.length() == 0) {
            tv_no_address.setVisibility(View.VISIBLE);
        } else {
            tv_no_address.setVisibility(View.GONE);
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject obj = dataArray.getJSONObject(i);
                String  user_name = obj.getString("user_name");
                String  user_headimg = obj.getString("user_headimg");
                HashMap<String,String> map = new HashMap<>();
                map.put("image",user_headimg);
                map.put("name",user_name);
                mList.add(map);
                map = null;
            }
            mAdapter.setData(mList);
        }
    }

    @Override
    public void onRefresh() {
        recyclerView.refreshComplete();
    }

    @Override
    public void onLoadMore() {
        recyclerView.loadMoreComplete();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mList = null;
    }
}
