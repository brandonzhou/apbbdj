package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;

import java.io.Serializable;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class ShopAddressMapActivity extends BaseActivity {

    private MapView mMapView;
    private AMap aMap;;
    private RelativeLayout rl_back;
    private AMapLocationClient mLocationClient;
    private LatLng mLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_address_map);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(ShopAddressMapActivity.this);
        initParams();
        initView(savedInstanceState);
        setMarker();     //设置标记
        initListener();
    }

    private void initParams() {
        Intent intent = getIntent();
        mLatLng =  intent.getParcelableExtra("latLng");
    }

    private void setMarker() {
       /* MarkerOptions markerOption = new MarkerOptions();
        LatLng latLng = new LatLng(34.341568,108.940174);
        markerOption.position(latLng);
        markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");

        markerOption.draggable(true);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.ic_address_log)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        aMap.addMarker(markerOption);*/
    }

    private void initListener() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView(Bundle savedInstanceState) {
        mMapView = findViewById(R.id.map);
        rl_back = findViewById(R.id.rl_back);
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
            setUpMap();
        }

    }

    private void setUpMap() {
        //new LatLng(34.341568, 108.940174)
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatLng,17);
    //    aMap.animateCamera(cameraUpdate);
        aMap.moveCamera(cameraUpdate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
