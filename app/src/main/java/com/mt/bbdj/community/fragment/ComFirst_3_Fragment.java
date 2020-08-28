package com.mt.bbdj.community.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.ConfirmPopupView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.activity.LoginByCodeActivity;
import com.mt.bbdj.baseconfig.application.MyApplication;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.MingleArea;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.CityDao;
import com.mt.bbdj.baseconfig.db.gen.CountyDao;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.MingleAreaDao;
import com.mt.bbdj.baseconfig.db.gen.ProvinceDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Constant;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.DialogUtil;
import com.mt.bbdj.baseconfig.utls.DownloadUtil;
import com.mt.bbdj.baseconfig.utls.FileUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.NotificationUtils;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.HorizontalProgressBar;
import com.mt.bbdj.baseconfig.view.MyGridView;
import com.mt.bbdj.community.activity.BindUserActivity;
import com.mt.bbdj.community.activity.ChangeManagerdActivity;
import com.mt.bbdj.community.activity.ClearOrderActivity;
import com.mt.bbdj.community.activity.ClientManagerActivity;
import com.mt.bbdj.community.activity.ComplainManagerdActivity;
import com.mt.bbdj.community.activity.CouponActivity;
import com.mt.bbdj.community.activity.EnterDetailActivity;
import com.mt.bbdj.community.activity.FailureEnterActivity;
import com.mt.bbdj.community.activity.GlobalSearchActivity;
import com.mt.bbdj.community.activity.ManualMailingActivity;
import com.mt.bbdj.community.activity.MatterShopActivity;
import com.mt.bbdj.community.activity.MessageAboutActivity;
import com.mt.bbdj.community.activity.MessageManagerdActivity;
import com.mt.bbdj.community.activity.MessageRechargePannelActivity;
import com.mt.bbdj.community.activity.MoneyFormatManagerActivity;
import com.mt.bbdj.community.activity.MyClientActivity;
import com.mt.bbdj.community.activity.OpearteActivity;
import com.mt.bbdj.community.activity.PannelRechargeActivity;
import com.mt.bbdj.community.activity.QCodeScanActivity;
import com.mt.bbdj.community.activity.RechargeActivity;
import com.mt.bbdj.community.activity.RecommendUserActivity;
import com.mt.bbdj.community.activity.RepertoryActivity;
import com.mt.bbdj.community.activity.RepertoryStoreActivity;
import com.mt.bbdj.community.activity.SaveManagerMoneyActivity;
import com.mt.bbdj.community.activity.SearchPackageActivity;
import com.mt.bbdj.community.activity.SelectExpressActivity;
import com.mt.bbdj.community.activity.SendManagerActivity;
import com.mt.bbdj.community.activity.SetWayMoneyActivity;
import com.mt.bbdj.community.activity.SystemMessageAboutActivity;
import com.mt.bbdj.community.activity.WaitHandleOrderActivity;
import com.mt.bbdj.community.activity.WaterOrderActivity;
import com.mt.bbdj.community.activity.WebDetailActivity;
import com.mt.bbdj.community.activity.goodmanage.GoodsManagerActivity;
import com.mt.bbdj.community.adapter.GlideImageLoader;
import com.mt.bbdj.community.adapter.MyGridViewAdapter;
import com.mylhyl.circledialog.CircleDialog;
import com.shshcom.station.base.ICaseBack;
import com.shshcom.station.blockuser.ui.activity.BlockUserListActivity;
import com.shshcom.station.statistics.domain.PackageUseCase;
import com.shshcom.station.statistics.http.bean.TodayExpressStatistics;
import com.shshcom.station.statistics.http.bean.WeChatTodayNotice;
import com.shshcom.station.statistics.ui.NotifyPackListActivity;
import com.shshcom.station.statistics.ui.PackStockListActivity;
import com.shshcom.station.statistics.ui.TotalPackStockActivity;
import com.shshcom.station.storage.activity.ScanPickOutActivity;
import com.shshcom.station.storage.activity.ScanStorageActivity;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Author : ZSK
 * Date : 2018/12/26
 * Description : 社区版首页
 */
public class ComFirst_3_Fragment extends BaseFragment {

    @BindView(R.id.iv_scan_out_package)
    ImageView iv_scan_out_package;
    @BindView(R.id.tv_today_in)
    TextView tv_today_in;
    @BindView(R.id.tv_today_out)
    TextView tv_today_out;
    @BindView(R.id.tv_storage_all_num)
    TextView tv_storage_all_num;


    @BindView(R.id.gv_com_zero)
    MyGridView mComGridViewZero;
    @BindView(R.id.gv_com_first)
    MyGridView mComGridView;
    @BindView(R.id.gv_com_two)
    MyGridView mComGridViewTwo;
    @BindView(R.id.gv_com_three)
    MyGridView mComGridViewThree;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.rl_banner)
    RelativeLayout rl_banner;
    @BindView(R.id.ll_title)
    LinearLayout ll_title;
    @BindView(R.id.textview_serach)
    TextView tvSearch;     //搜索
    @BindView(R.id.tv_handle_failure)
    TextView tv_handle_failure;     //错误件
    @BindView(R.id.ll_title_handing)
    LinearLayout ll_title_handing;
    @BindView(R.id.tv_handle_ing)
    TextView tv_handle_ing;
    Unbinder unbinder;

    @BindView(R.id.ll_wechat_notify)
    LinearLayout ll_wechat_notify;
    @BindView(R.id.ll_recharge_notify)
    LinearLayout ll_recharge_notify;
    @BindView(R.id.tv_wechat_notify_number)
    TextView tv_wechat_notify_number;
    @BindView(R.id.tv_recharge)
    TextView tv_recharge;


    View mView;


    private String APP_PATH_ROOT = FileUtil.getRootPath(MyApplication.getInstance()).getAbsolutePath() + File.separator + "bbdj";


    private List<HashMap<String, Object>> mListZero = new ArrayList<>();
    private List<HashMap<String, Object>> mList = new ArrayList<>();
    private List<HashMap<String, Object>> mListTwo = new ArrayList<>();
    private List<HashMap<String, Object>> mListThree = new ArrayList<>();
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;
    private String express_id = "";   //快递公司id

    private ProvinceDao mProvinceDao;     //省
    private CityDao mCityDao;     //市
    private CountyDao mCountyDao;   //县
    private MingleAreaDao mMingleAreaDao;    //混合地区

    private final int REQUEST_UPLOAD_AREA = 2;    //下载省市区

    private final int REQUEST_UPLOAD_LOGO = 3;    //下载没有图片的logo

    private final int REQUEST_PANNEL_MESSAGE = 101;    //获取面板信息

    private final int REQUEST_BANNER_MESSAGE = 102;    //请求轮播图

    private final int REQUEST_ENTER_MESSAGE = 103;    //请求入库数据

    private final int REQUEST_CHECK_VERSION = 104;    //检查版本更新


    private String user_id;
    private ExpressLogoDao mExpressLogoDao;
    private List<ExpressLogo> mExpressLogoList;
    private String version_url;
    private String versionRemote;
    private ProgressDialog mProgressBar;

    final String fileName = "bbdj.apk";
    private boolean isGetData = false;
    private HorizontalProgressBar progressBar;
    private UserBaseMessage mUserBaseMessage;
    private boolean isHidden = true;
    private SharedPreferences.Editor editor;
    private Banner mBanner;
    private IWXAPI api;
    private String shareTitle = "";
    private MyGridViewAdapter myGridViewAdapter;

    public static ComFirst_3_Fragment getInstance() {
        ComFirst_3_Fragment comFirstFragment = new ComFirst_3_Fragment();
        return comFirstFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layout_com_first_3_fragment, container, false);
        unbinder = ButterKnife.bind(this, mView);
        api = WXAPIFactory.createWXAPI(getActivity(), null);    //注册到微信
        api.registerApp(Constant.appid);
        EventBus.getDefault().register(this);
        initParams();
        initData();
        initView();
        initClick();
        requestAreaData();    //下载省市县
        //updataExpressState();   //更新快递公司状态
        return mView;
    }


    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(getActivity(), "请稍候...");
        editor = SharedPreferencesUtil.getEditor();
        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        mProvinceDao = mDaoSession.getProvinceDao();
        mExpressLogoDao = mDaoSession.getExpressLogoDao();
        mCityDao = mDaoSession.getCityDao();
        mCountyDao = mDaoSession.getCountyDao();
        mMingleAreaDao = mDaoSession.getMingleAreaDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
            shareTitle = list.get(0).getMingcheng();
            mUserBaseMessage = list.get(0);
            user_id = mUserBaseMessage.getUser_id();
            String address = mUserBaseMessage.getAddress();
            if (null != address) {
                if (address.contains("北京市") || address.contains("廊坊市")) {
                    isHidden = false;
                } else {
                    isHidden = true;
                }
            }
        }

    }

    private void updataExpressState() {
        mExpressLogoList = mExpressLogoDao.queryBuilder()
                .where(ExpressLogoDao.Properties.LogoLocalPath.eq(""))
                .where(ExpressLogoDao.Properties.States.eq(1)).list();
        if (mExpressLogoList == null || mExpressLogoList.size() == 0) {
            return;
        }

        for (ExpressLogo expressLogo : mExpressLogoList) {
            String localPath = expressLogo.getLogoLocalPath();
            if ("".equals(localPath) || null == localPath) {
                express_id = expressLogo.getExpress_id();
                String type = expressLogo.getProperty();
                uploadLogoPicture(type);
            }
        }
    }

    private void uploadLogoPicture(String type) {
        Request<String> request = NoHttpRequest.updateExpressState(user_id, express_id, type);
        mRequestQueue.add(REQUEST_UPLOAD_LOGO, request, mResponseListener);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        //收到推送消息
        if (TargetEvent.COMMIT_FIRST_REFRESH == targetEvent.getTarget()) {
            //更新界面的信息
            requestPannelMessage();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestPannelMessage();
        requestBannerMessage();   //请求界面轮播图
        requestEnterData();   //入库数据
        notifyCheck();    //通知权限
        httpTodayExpressStatistics();
        httpWeChatTodayNotice();
    }


    private void notifyCheck() {
        if (!NotificationUtils.isNotificationEnabled(getActivity())) {   //没有开启权限
            new CircleDialog.Builder()
                    .setTitle("提示")
                    .setMaxHeight(0.8f)
                    .setText("\n请开启应用通知，避免订单延误\n")
                    .setPositive("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            NotificationUtils.gotoSet(getActivity());
                        }
                    })
                    .show(getFragmentManager());
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            requestPannelMessage();
            requestBannerMessage();   //请求界面轮播图
            requestEnterData();   //入库数据
        }
    }


    private void requestPannelMessage() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        Request<String> request = NoHttpRequest.getPannelmessageRequest(params);
        mRequestQueue.add(REQUEST_PANNEL_MESSAGE, request, mResponseListener);

        String version = SystemUtil.getVersion(getActivity());
        Request<String> requestVersion = NoHttpRequest.checkVersion(user_id, version);
        mRequestQueue.add(REQUEST_CHECK_VERSION, requestVersion, mResponseListener);
    }

    private void requestBannerMessage() {
        Map<String, String> map = new HashMap<>();
        map.put("distributor_id", user_id);
        String signature = StringUtil.getsignature(map);
        Request<String> request = NoHttpRequest.getBanner(signature, user_id);
        mRequestQueue.add(REQUEST_BANNER_MESSAGE, request, mResponseListener);
    }

    private void requestEnterData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("station_id", user_id);
        Request<String> request = NoHttpRequest.getEnterStateData(map);
        mRequestQueue.add(REQUEST_ENTER_MESSAGE, request, mResponseListener);
    }

    private void httpWeChatTodayNotice() {
        PackageUseCase.INSTANCE.queryWeChatTodayNotice(new ICaseBack<WeChatTodayNotice>() {
            @Override
            public void onSuccess(WeChatTodayNotice result) {
                if (tv_wechat_notify_number != null) {
                    tv_wechat_notify_number.setText(
                            String.format("今日使用微信公众号通知了%d个快递", result.getWechatTotal()));
                }
            }

            @Override
            public void onError(@NotNull String error) {
            }
        });
    }

    private void initData() {

    }

    private void requestAreaData() {
        //  uploadGenealData();    //下载省市区数据
    }


    private void uploadGenealData() {
        Request<String> request = NoHttpRequest.getAreaRequest(user_id, express_id);
        mRequestQueue.add(REQUEST_UPLOAD_AREA, request, mResponseListener);
    }

    private void initClick() {
        mComGridViewZero.setOnItemClickListener(mGrideClickListener);
        mComGridView.setOnItemClickListener(mGrideClickListener);
        mComGridViewTwo.setOnItemClickListener(mGrideClickListener);
        mComGridViewThree.setOnItemClickListener(mGrideClickListener);

        iv_scan_out_package.setOnClickListener(v -> handleOutManagerEvent());

        ll_wechat_notify.setOnClickListener(v -> NotifyPackListActivity.Companion.openActivity(getActivity()));

        tv_recharge.setOnClickListener(v -> RechargeActivity.openActivity(getActivity()));

    }


    private void handlePannelAboutEvent() {
        MessageAboutActivity.startAction(getActivity(), 2);
    }

    private void handleMessageAbouitEvent() {
        MessageAboutActivity.startAction(getActivity(), 1);
    }


    //功能列表的点击事件
    private AdapterView.OnItemClickListener mGrideClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()) {
                case R.id.gv_com_zero:
                    handleItemClickZero(position);
                    break;
                case R.id.gv_com_first:
                    handleItemClickFirst(position);
                    break;
                case R.id.gv_com_two:
                    handleItemClickTwo(position);
                    break;
                case R.id.gv_com_three:
                    handleItemClickThree(position);
                    break;
            }

        }
    };

    private void handleItemClickZero(int position) {
        HashMap<String, Object> item = mListZero.get(position);
        String id = item.get("id").toString();
        switch (id) {
            case "0":       //待处理订单
                handleZeroWaitOrder();
                break;
            case "1":       //已处理订单
                handleFinishOrder();
                break;
            case "2":       //我的商品
                handleGoodsManagerEvent();
                break;
            case "3":       //分享小程序
                handleShareEvent();
                break;
            case "4":      //设置运费
                handleSetWayMoney();
                break;
            case "5":      //设置优惠券
                handleSetCoupon();
                break;
            case "6":      //我的用户
                handleMyClient();
                break;
            case "7":     //查看店铺
                handleMyShop();
                break;
        }
    }


    private void handleMyClient() {
        MyClientActivity.actionTo(getActivity(), user_id);
    }

    private void handleSetCoupon() {
        CouponActivity.actionTo(getActivity(), user_id);
    }

    private void handleFinishOrder() {
        Intent intent = new Intent(getActivity(), WaitHandleOrderActivity.class);
        intent.putExtra("type", "2");
        startActivity(intent);
    }

    private void handleSetWayMoney() {
        SetWayMoneyActivity.actionTo(getActivity());
    }

    private void handleZeroWaitOrder() {
        Intent intent = new Intent(getActivity(), WaitHandleOrderActivity.class);
        intent.putExtra("type", "1");
        startActivity(intent);
    }

    private void handleItemClickThree(int position) {
        HashMap<String, Object> item = mListThree.get(position);
        String id = item.get("id").toString();
        switch (id) {
            case "0":       //物流查询
                handleSearchPackageEvent();
                break;
            case "1":       //交接管理
                handleChangeManagerEvent();
                break;
            case "2":       //桶装水
                handleWaterManagerEvent();
                break;
            case "3":       //干洗服务
                handleClearManagerEvent();
                break;
            case "4":       //财务管理
                handleMoneyManagerEvent();
                break;
            case "5":       //客户管理
                handleClientManagerEvent();
                break;
            case "6":       //短信管理
                handleMessageEvent();
                break;
            case "7":      //投诉
                handleComplainEvent();
                break;
            case "8":      //操作手册
                handleOperateEvent();
                break;
            case "9":
                handleRecommendEvent();
                break;
            case "10":
                handleBindUserEvent();
                break;
        }
    }


    private void handleItemClickTwo(int position) {
        HashMap<String, Object> item = mListTwo.get(position);
        String id = item.get("id").toString();
        switch (id) {
            case "0":     //寄存管理
                handleStoreManageEvent();
                break;
            case "1":       //入库管理
                handleEnterManagerEvent();
                break;
            case "2":       //用户取件
                handleOutManagerEvent();
                break;
            case "3":       //二维码取件
                handleQCodeEvent();
                break;
            case "4":       //我的存放
                handleSaveManagerEvent();
                break;
            case "5":       //寄存费用
                handleSaveManagerMoneyEvent();
                break;
            case "6":       //拍照入库
                handleEnterHourseByCameraEvent();
                break;
            case "7":       //数据统计
                WebDetailActivity.actionTo(getActivity(), "https://tongji.shshcom.com/#/?stationId=" + user_id);
                break;
            case "8":       //敏感用户
                BlockUserListActivity.Companion.openActivity(getActivity());
                break;
        }
    }


    private void handleQCodeEvent() {
        QCodeScanActivity.actionTo(getActivity(), user_id);
    }

    private void handleSaveManagerMoneyEvent() {
        SaveManagerMoneyActivity.actionTo(getActivity(), user_id);
    }

    private void handleSaveManagerEvent() {
        Intent intent = new Intent(getActivity(), RepertoryActivity.class);
        startActivity(intent);
    }

    private void handleItemClickFirst(int position) {
        HashMap<String, Object> item = mList.get(position);
        String id = item.get("id").toString();
        switch (id) {
            case "0":       //寄件管理
                handleSendManagerEvent();
                break;
            case "1":       //手动寄件
                handleSendByhandEvent();
                break;
            case "2":       //购买物料
                handleShopEvent();
                break;
        }
    }

    private void handleClearManagerEvent() {
        Intent intent = new Intent(getActivity(), ClearOrderActivity.class);
        startActivity(intent);
    }

    private void handleWaterManagerEvent() {
        Intent intent = new Intent(getActivity(), WaterOrderActivity.class);
        startActivity(intent);
    }

    private void handleGoodsManagerEvent() {
        GoodsManagerActivity.actionTo(getActivity());
    }


    private void handleShareEvent() {
        WXMiniProgramObject miniProgram = new WXMiniProgramObject();
        miniProgram.webpageUrl = "http://www.81kdwd.com/";//自定义
        miniProgram.userName = "gh_2a52c80e436e";//小程序端提供参数
        miniProgram.path = "/pages/index/index?title=" + shareTitle + "&id=" + user_id;//小程序端提供参数
        WXMediaMessage mediaMessage = new WXMediaMessage(miniProgram);
        miniProgram.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;
        mediaMessage.title = shareTitle;//自定义
        mediaMessage.description = "这是兵兵驿站的商铺";//自定义
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_logo_);
        Bitmap sendBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
        bitmap.recycle();
        mediaMessage.thumbData = bmpToByteArray(sendBitmap, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "";
        req.scene = SendMessageToWX.Req.WXSceneSession;
        req.message = mediaMessage;
        api.sendReq(req);
    }

    private void handleMyShop() {
        String appId = Constant.appid; // 填应用AppId
        IWXAPI api = WXAPIFactory.createWXAPI(getActivity(), appId);
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = "gh_2a52c80e436e"; // 填小程序原始id
        req.path = "/pages/index/index?id=" + user_id;         //拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void handleBindUserEvent() {
        startActivity(new Intent(getActivity(), BindUserActivity.class));
    }

    private void handleRecommendEvent() {
        startActivity(new Intent(getActivity(), RecommendUserActivity.class));
    }

    private void handleOperateEvent() {
        Intent intent = new Intent(getActivity(), OpearteActivity.class);
        startActivity(intent);
    }

    private void handleStoreManageEvent() {
        Intent intent = new Intent(getActivity(), RepertoryStoreActivity.class);
        startActivity(intent);
    }

    private void handleOutManagerEvent() {
//        ScannerOutActivity.actionTo(getActivity());
        ScanPickOutActivity.Companion.openActivity(getActivity());
    }

    private void handleEnterManagerEvent() {
        SelectExpressActivity.actionTo(getActivity());
//        Intent intent = new Intent(getActivity(), EnterManagerActivity.class);
//        //Intent intent = new Intent(getActivity(), EnterManager_new_Activity.class);
//        startActivity(intent);
    }

    private void handleMoneyManagerEvent() {
        Intent intent = new Intent(getActivity(), MoneyFormatManagerActivity.class);
        startActivity(intent);
    }

    private void handleChangeManagerEvent() {
        Intent intent = new Intent(getActivity(), ChangeManagerdActivity.class);
        startActivity(intent);
    }

    private void handleClientManagerEvent() {
        Intent intent = new Intent(getActivity(), ClientManagerActivity.class);
        startActivity(intent);
    }

    private void handleSearchPackageEvent() {
        Intent intent = new Intent(getActivity(), SearchPackageActivity.class);
        startActivity(intent);
    }

    private void handleComplainEvent() {
        Intent intent = new Intent(getActivity(), ComplainManagerdActivity.class);
        startActivity(intent);
    }

    private void handleMessageEvent() {
        Intent intent = new Intent(getActivity(), MessageManagerdActivity.class);
        startActivity(intent);
    }

    private void handleSendManagerEvent() {
        Intent intent = new Intent(getActivity(), SendManagerActivity.class);
        startActivity(intent);
    }

    private void handleSendByhandEvent() {
        ManualMailingActivity.actionTo(getActivity());
//        Intent intent = new Intent();
//        intent.setClass(getActivity(), SendResByHandActivity.class);
//        startActivity(intent);
    }

    //跳转物料商城界面
    private void handleShopEvent() {
        Intent intent = new Intent(getActivity(), MatterShopActivity.class);
        startActivity(intent);
    }

    private void initView() {
        setService();     //外卖服务
        setFirstItemData();    //设置快递寄出
        setTwoItemData();    //设置快递存放
        setThreeItemData();    //设置其他
    }

    @Override
    public void onStart() {
        super.onStart();
        // mBanner.startAutoPlay();
    }


    @Override
    public void onStop() {
        super.onStop();
        // mBanner.stopAutoPlay();
    }

    private void setBanner(List<String> images) {
        if (images.isEmpty()) {
            rl_banner.setVisibility(View.GONE);
        } else {
            rl_banner.setVisibility(View.GONE);
            mBanner = banner.setImages(images).setImageLoader(new GlideImageLoader());
            mBanner.start();
        }
    }

    private void setService() {
        for (int i = 0; i < 8; i++) {
            HashMap<String, Object> item = new HashMap<>();
            if (i == 0) {
                item.put("id", "0");
                item.put("name", "待处理订单");
                item.put("tag", "0");
                item.put("ic", R.drawable.ic_zero_wait_order);
            }
            if (i == 1) {
                item.put("id", "1");
                item.put("name", "已完成订单");
                item.put("tag", "0");
                item.put("ic", R.drawable.ic_zero_finish_order);
            }
            if (i == 2) {
                item.put("id", "2");
                item.put("name", "商品管理");
                item.put("tag", "0");
                item.put("ic", R.drawable.ic_my_goods);
            }
            if (i == 3) {
                item.put("id", "3");
                item.put("name", "分享商铺");
                item.put("tag", "0");
                item.put("ic", R.drawable.ic_share);
            }
            if (i == 4) {
                item.put("id", "4");
                item.put("name", "设置运费");
                item.put("tag", "0");
                item.put("ic", R.drawable.ic_three_5);
            }
            if (i == 5) {
                item.put("id", "5");
                item.put("name", "设置优惠券");
                item.put("tag", "0");
                item.put("ic", R.drawable.ic_coupon);
            }
            if (i == 6) {
                item.put("id", "6");
                item.put("name", "我的用户");
                item.put("tag", "0");
                item.put("ic", R.drawable.ic_three_6);
            }

            if (i == 7) {
                item.put("id", "7");
                item.put("name", "查看店铺");
                item.put("tag", "0");
                item.put("ic", R.drawable.ic_my_shop);
            }
            mListZero.add(item);
        }
        myGridViewAdapter = new MyGridViewAdapter(mListZero);
        mComGridViewZero.setAdapter(myGridViewAdapter);
    }

    private void setThreeItemData() {
        for (int i = 0; i < 9; i++) {
            HashMap<String, Object> item = new HashMap<>();

            if (i == 0) {
                item.put("id", "0");
                item.put("name", "物流查询");
                item.put("tag", "0");
                item.put("ic", R.drawable.ic_three_1);
            }
            if (i == 1) {
                item.put("id", "1");
                item.put("name", "交接管理");
                item.put("tag", "0");
                item.put("ic", R.drawable.ic_three_2);
            }

          /*  if (i == 2) {
                item.put("tag", "0");
                item.put("id", "2");
                item.put("name", "桶装水");
                item.put("ic", R.drawable.ic_three_3);
            }

            if (i == 3) {
                item.put("tag", "0");
                item.put("id", "3");
                item.put("name", "干洗服务");
                item.put("ic", R.drawable.ic_three_4);
            }*/


            if (i == 2) {
                item.put("tag", "0");
                item.put("id", "4");
                item.put("name", "财务管理");
                item.put("ic", R.drawable.ic_three_5);
            }
            if (i == 3) {
                item.put("tag", "0");
                item.put("id", "5");
                item.put("name", "客户管理");
                item.put("ic", R.drawable.ic_three_6);
            }
            if (i == 4) {
                item.put("tag", "0");
                item.put("id", "6");
                item.put("name", "短信管理");
                item.put("ic", R.drawable.ic_three_7);
            }
            if (i == 5) {
                item.put("tag", "0");
                item.put("id", "7");
                item.put("name", "投诉管理");
                item.put("ic", R.drawable.ic_three_9);
            }
            if (i == 6) {
                item.put("tag", "0");
                item.put("id", "8");
                item.put("name", "操作手册");
                item.put("ic", R.drawable.ic_three_10);
                mListThree.add(item);
            }
            if (i == 7) {
                item.put("tag", "0");
                item.put("id", "9");
                item.put("name", "推荐的订单");
                item.put("ic", R.drawable.ic_three_2);
            }
            if (i == 8) {
                item.put("tag", "0");
                item.put("id", "10");
                item.put("name", "绑定的用户");
                item.put("ic", R.drawable.ic_first_two);
            }
        }
        MyGridViewAdapter myGridViewAdapter = new MyGridViewAdapter(mListThree);
        mComGridViewThree.setAdapter(myGridViewAdapter);
    }


    private void setTwoItemData() {
        HashMap<String, Object> item6 = new HashMap<>();
        item6.put("tag", "0");
        item6.put("id", "6");
        item6.put("name", "拍照入库");
        item6.put("ic", R.drawable.ic_main_pack_scan);
        mListTwo.add(item6);

        //隐藏
        HashMap<String, Object> item0 = new HashMap<>();
        item0.put("tag", "0");
        item0.put("id", "0");
        item0.put("name", "接收存放");
        item0.put("ic", R.drawable.ic_two_1);

        HashMap<String, Object> item1 = new HashMap<>();
        item1.put("tag", "0");
        item1.put("id", "1");
        item1.put("name", "扫码入库");
        item1.put("ic", R.drawable.ic_two_2);
        mListTwo.add(item1);

        HashMap<String, Object> item2 = new HashMap<>();
        item2.put("tag", "0");
        item2.put("id", "2");
        item2.put("name", "用户取件");
        item2.put("ic", R.drawable.ic_two_3);
        mListTwo.add(item2);


        HashMap<String, Object> item3 = new HashMap<>();
        item3.put("tag", "0");
        item3.put("id", "3");
        item3.put("name", "二维码取件");
        item3.put("ic", R.drawable.ic_two_3);


        HashMap<String, Object> item4 = new HashMap<>();
        item4.put("tag", "0");
        item4.put("id", "4");
        item4.put("name", "我的存放");
        item4.put("ic", R.drawable.ic_three_2);


        HashMap<String, Object> item5 = new HashMap<>();
        item5.put("tag", "0");
        item5.put("id", "5");
        item5.put("name", "寄存费用");
        item5.put("ic", R.drawable.ic_three_5);
        mListTwo.add(item5);


        HashMap<String, Object> item7 = new HashMap<>();
        item7.put("tag", "0");
        item7.put("id", "7");
        item7.put("name", "数据统计");
        item7.put("ic", R.drawable.ic_three_2);
        mListTwo.add(item7);

        HashMap<String, Object> item8 = new HashMap<>();
        item8.put("tag", "0");
        item8.put("id", "8");
        item8.put("name", "敏感用户");
        item8.put("ic", R.drawable.ic_three_6);
        mListTwo.add(item8);


        MyGridViewAdapter myGridViewAdapter = new MyGridViewAdapter(mListTwo);
        mComGridViewTwo.setAdapter(myGridViewAdapter);

    }

    private void setFirstItemData() {
        for (int i = 0; i < 3; i++) {
            HashMap<String, Object> item = new HashMap<>();
            if (i == 0) {
                item.put("tag", "0");
                item.put("id", "0");
                item.put("name", "寄件管理");
                item.put("ic", R.drawable.ic_first_one);
            }
            if (i == 1) {
                item.put("tag", "0");
                item.put("id", "1");
                item.put("name", "帮客户寄件");
                item.put("ic", R.drawable.ic_first_two);
            }
            if (i == 2) {
                item.put("tag", "0");
                item.put("id", "2");
                item.put("name", "购买物料");
                item.put("ic", R.drawable.ic_three_8);
            }
            mList.add(item);
        }
        MyGridViewAdapter myGridViewAdapter = new MyGridViewAdapter(mList);
        mComGridView.setAdapter(myGridViewAdapter);
    }

    private OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            //  dialogLoading.show();
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("v", "ComFirstFragment::" + what + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if (what == REQUEST_BANNER_MESSAGE) {
                    handleBannerMessage(jsonObject);
                } else {
                    if ("5001".equals(code)) {
                        handleHttpEvent(what, jsonObject);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // dialogLoading.cancel();
                ToastUtil.showShort(e.getMessage());
            }
            //  dialogLoading.cancel();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            //  dialogLoading.cancel();
            //  loginOut();
        }

        @Override
        public void onFinish(int what) {
            //  dialogLoading.cancel();
        }
    };

    private void loginOut() {
        ToastUtil.showShort("登录状态失效，请重新登录");
        editor.putString("userName", "");
        editor.putString("password", "");
        editor.putBoolean("update", false);
        editor.commit();
        Intent intent = new Intent(getActivity(), LoginByCodeActivity.class);
        startActivity(intent);
        EventBus.getDefault().post(new TargetEvent(111));
    }

    private void handleHttpEvent(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_UPLOAD_AREA:    //下载省市县
                handleUploadArea(jsonObject);
                break;
            case REQUEST_PANNEL_MESSAGE:      //更新主界面的信息
                chnagePannelMessage(jsonObject);
                break;
            case REQUEST_UPLOAD_LOGO:    //更新logo图片
                updateImagePath(jsonObject);
                break;
//            case REQUEST_BANNER_MESSAGE:    //首页banner图
//                handleBannerMessage(jsonObject);
//                break;
            case REQUEST_ENTER_MESSAGE:   //入库情况
                // zhhli 不再显示 20200528
                // handleEnterMessage(jsonObject);
                break;
            case REQUEST_CHECK_VERSION:
                handleNewVersion(jsonObject);
                break;
        }
    }

    private void handleEnterMessage(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        String today_fail = StringUtil.handleNullResultForNumber(data.getString("today_fail"));
        String today_handle = StringUtil.handleNullResultForNumber(data.getString("today_handle"));
        if (Integer.parseInt(today_fail) != 0) {
            ll_title.setVisibility(View.VISIBLE);
            tv_handle_failure.setText("今日入库失败" + today_fail + "件，请立刻处理");
        } else {
            ll_title.setVisibility(View.GONE);
        }
        if (Integer.parseInt(today_handle) != 0) {
            ll_title_handing.setVisibility(View.VISIBLE);
            tv_handle_ing.setText("入库处理中：" + today_handle + "件");
        } else {
            ll_title_handing.setVisibility(View.GONE);
        }
    }

    private void updateImagePath(JSONObject jsonObject) {

    }

    private void handleBannerMessage(JSONObject jsonObject) throws JSONException {
        List<String> images = new ArrayList<>();
        if (jsonObject.has("data")) {
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject1 = data.getJSONObject(i);
                String thumb = jsonObject1.getString("thumb");
                String link = jsonObject1.getString("link");
                images.add(thumb);
            }
        }

        setBanner(images);
    }

    private void handleNewVersion(JSONObject result) throws JSONException {
        JSONObject jsonObject = result.getJSONObject("data");
        if (jsonObject == null) {
            return;
        }
        boolean force = jsonObject.getBoolean("force");
        String versionHttp = jsonObject.getString("version_number");
        version_url = jsonObject.getString("version_url");
        String version = SystemUtil.getVersion(getActivity());

        if (version.compareTo(versionHttp) < 0) {
            if (force) {
                versionRemote = versionHttp;
                showForceNewVersionDownLoadDialog();
            } else {
                if (!versionHttp.equals(versionRemote)) {
                    versionRemote = versionHttp;
                    showNewVersion();
                }
            }
        }

    }

    private void chnagePannelMessage(JSONObject jsonObject) throws JSONException {
        JSONObject dataObj = jsonObject.getJSONObject("data");
        String mail_stay = dataObj.getString("mail_stay");    //待收件未处理
        String orders_sum = StringUtil.handleNullResultForNumber(dataObj.getString("orders_sum"));    //零售--待接单
        //String mail_processed = dataObj.getString("mail_processed");  //待收件已处理
        //String abnormal_stay = dataObj.getString("abnormal_stay");   //异常件未处理
        // String abnormal_processed = dataObj.getString("abnormal_processed");  //异常件已处理
        String sms_number = dataObj.getString("sms_number");   //短信余额
        String face_number = dataObj.getString("face_number");   //面单余额
        String username = dataObj.getString("username");   //位置
        String money = dataObj.getString("money");   //账户余额
        String min_money = dataObj.getString("min_money");   //警戒余额
        String birthday = dataObj.getString("birthday");   //入驻天数
        //String version_number = dataObj.getString("version_number");   //版本号
        String prohibit = dataObj.getString("prohibit");   //状态 1：正常营业  其他：禁止登录

        int need_recharge = dataObj.getInt("need_recharge");   //是否提示充值 （新增参数）
        String recharge_notify_msg = dataObj.getString("recharge_notify_msg");   //充值提示文字内容 （新增参数）


        //String unread_url = dataObj.getString("unread_url");   //未读消息

        editor.putString("money", money);
        editor.putString("birthday", birthday);
        editor.putString("address", username);
        editor.commit();

        if (!"1".equals(prohibit)) {
            //显示禁止登录
            showProhibitDialog();
        } else {
            float moneyInt = IntegerUtil.getStringChangeToFloat(money);
            float min_moneyInt = IntegerUtil.getStringChangeToFloat(min_money);
            //账户余额不足
            if (moneyInt < min_moneyInt) {
                showNOmoneyAlertDialog(moneyInt, min_moneyInt);
            } else {
                float messageNumber = IntegerUtil.getStringChangeToFloat(sms_number);
                if (messageNumber <= 0) {
                    showNoMessageAlterDialog();    //短信不足
                } else {
                    float face_numberNumber = IntegerUtil.getStringChangeToFloat(face_number);
                    if (face_numberNumber <= 0) {
                        showNoPannelDialog();       //面单不足
                    } else {
                        // upLoadNewVersion(version_number, version_url);    //更新最新版本
                    }
                }
            }
        }

        // 1 显示充值提醒
        ll_recharge_notify.setVisibility(need_recharge == 1 ? View.VISIBLE : View.GONE);

        mListZero.get(0).put("tag", orders_sum);
        myGridViewAdapter.notifyDataSetChanged();
    }

    private void showNoPannelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage("面单数量不足，请充值")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(getActivity(), PannelRechargeActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showNoMessageAlterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage("短信数量不足，请充值")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(getActivity(), MessageRechargePannelActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

    }

    private void showNOmoneyAlertDialog(float moneyInt, float min_moneyInt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage("当前账户可用余额为" + moneyInt + ",低于保证余额" + min_moneyInt + ",请先充值")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(getActivity(), RechargeActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showProhibitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage("您的驿站已禁止登录，如有疑问请与客服人员联系")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(getActivity(), LoginByCodeActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    ConfirmPopupView versionUpdateDialog;

    private void showNewVersion() {
        if (versionUpdateDialog == null) {
            versionUpdateDialog = new XPopup.Builder(getActivity())
                    .asConfirm("更新提示", "有新版本上线，请先更新！\n" + versionRemote, () -> download());
        }

        if (!versionUpdateDialog.isShow()) {
            versionUpdateDialog.show();
        }

    }


    private void showForceNewVersionDownLoadDialog() {
        DialogUtil.promptDialog1(getActivity(), "更新提示", "有新版本上线，请先更新！\n" + versionRemote, DetermineListener, throwListener);
    }


    DialogInterface.OnClickListener DetermineListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            download();
        }
    };

    private void download() {
        editor.putString("userName", "");
        editor.putString("password", "");
        editor.putBoolean("update", false);
        editor.commit();

        mProgressBar = new ProgressDialog(getActivity());
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressBar.setTitle("正在下载");
        mProgressBar.setMessage("请稍候...");
        mProgressBar.setProgress(0);
        mProgressBar.setMax(100);
        mProgressBar.show();
        mProgressBar.setCancelable(false);

        DownloadUtil.get().download(version_url, APP_PATH_ROOT, fileName, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                if (mProgressBar != null && mProgressBar.isShowing()) {
                    mProgressBar.dismiss();
                }
                //下载完成进行相关逻辑操作
                installApk(file);// 安装
            }

            @Override
            public void onDownloading(int progress) {
                mProgressBar.setProgress(progress);
                //progressBar.setCurrentProgress(progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                //下载异常进行相关提示操作
                //ToastUtil.showShort(e.getMessage());
            }
        });
    }

    private void installApk(File file) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri photoURI = FileProvider.getUriForFile(getActivity(), MyApplication.getInstance().getPackageName() + ".provider", file);
            intent.setDataAndType(photoURI, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    DialogInterface.OnClickListener throwListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            dialogLoading.cancel();
        }
    };

    private void handleUploadArea(JSONObject jsonObject) throws JSONException {    //下载省市县
        mProvinceDao.deleteAll();
        mCountyDao.deleteAll();
        mCityDao.deleteAll();
        mMingleAreaDao.deleteAll();
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        List<MingleArea> mingleAreaList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonProvince = jsonArray.getJSONObject(i);
            String provinceId = jsonProvince.getString("id");
            String region_name = jsonProvince.getString("region_name");
            String parent_id = jsonProvince.getString("parent_id");
            String region_code = jsonProvince.getString("region_code");

            MingleArea mingleArea = new MingleArea(provinceId, region_name, parent_id, region_code);
            mingleAreaList.add(mingleArea);
        }
        mMingleAreaDao.saveInTx(mingleAreaList);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }

    @OnClick({R.id.textview_serach, R.id.ll_title, R.id.tv_handle_failure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.textview_serach:
                actionToSearchPannel();    //搜索
                break;
            case R.id.ll_title:
                actionToHandleFaile();    //处理失败
                break;
            case R.id.tv_handle_failure:
                EnterDetailActivity.actionTo(getActivity());
                break;
        }
    }

    private void actionToHandleFaile() {
        FailureEnterActivity.actionTo(getActivity(), user_id);
    }


    private void actionToSearchPannel() {
        Intent intent = new Intent(getActivity(), GlobalSearchActivity.class);
        startActivity(intent);
    }

    private void actionToRepertoryPannel() {
        Intent intent = new Intent(getActivity(), RepertoryActivity.class);
        startActivity(intent);
    }

    private void actionToMessagePannel() {
        Intent intent = new Intent(getActivity(), SystemMessageAboutActivity.class);
        startActivity(intent);
    }

    private void actionToFinishPannel() {
        Intent intent = new Intent(getActivity(), SendManagerActivity.class);
        intent.putExtra("currentItem", 1);
        startActivity(intent);
    }

    private void actionToWaitPannel() {
        Intent intent = new Intent(getActivity(), SendManagerActivity.class);
        intent.putExtra("currentItem", 0);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void handleEnterHourseByCameraEvent() {
        // Intent intent = new Intent(getActivity(), KotlinActivity.class);

        Intent intent = new Intent(getActivity(), ScanStorageActivity.class);
        startActivity(intent);
    }


    private void httpTodayExpressStatistics() {
        PackageUseCase.INSTANCE.todayExpressStatistics(new ICaseBack<TodayExpressStatistics>() {
            @Override
            public void onSuccess(TodayExpressStatistics result) {
                if (tv_today_in != null) {
                    tv_today_in.setText(result.getEnter() + "");
                    tv_today_out.setText(result.getOut() + "");
                    tv_storage_all_num.setText(result.getTotal() + "");
                }
            }

            @Override
            public void onError(@org.jetbrains.annotations.Nullable String error) {

            }
        });
    }

    @OnClick({R.id.view_today_out, R.id.view_today_in, R.id.view_storage_all_number})
    public void onTodayViewClicked(View view) {
        switch (view.getId()) {
            case R.id.view_today_in:
                PackStockListActivity.Companion.openActivity(getActivity(),
                        DateTime.now().toString("yyyy-MM-dd"), 1, true);
                break;
            case R.id.view_today_out:
                PackStockListActivity.Companion.openActivity(getActivity(),
                        DateTime.now().toString("yyyy-MM-dd"), 4, true);
                break;
            case R.id.view_storage_all_number:
                Intent intent = new Intent();
                intent.setClass(getActivity(), TotalPackStockActivity.class);
                getActivity().startActivity(intent);
                break;
        }

    }

}
