package com.mt.bbdj.baseconfig.internet;

/**
 * Author : ZSK
 * Date : 2018/12/27
 * Description :  网络接口
 */
public class InterApi {
    /**
     * 服务器地址/
     */
    // public static final String SERVER_ADDRESS = "http://www.81dja.com/BbdjApi/";
    public static final String BASE_URL = "http://www.81dja.com/";
    public static final String BASE_URL_EXPRESS = "http://ning.shijianping.com/";
    // public static final String BASE_URL = "http://cs.81bb.cn";
    public static final String SERVER_ADDRESS = BASE_URL + "/BbdjApi/";
    //public static final String SERVER_ADDRESS_ENTER = "http://www.81dja.com/Pie/";
    public static final String SERVER_ADDRESS_ENTER = BASE_URL + "/Pie/";
    public static final String SERVER_HANDLE = BASE_URL + "/StationApi/";
    public static final String SERVER_ORDER = BASE_URL + "/ServiceOrders/";
    public static final String SERVER_URL_3 = BASE_URL + "/TakeApi/";
    //门店
    public static final String BASE_URL_SHOP = "https://shop.81dja.com";
    public static final String SERVICE_SHOP = BASE_URL_SHOP + "/Take/AppV1/";
    public static final String SERVICE_SHOP_1 = BASE_URL_SHOP + "/Take/AppV2/";
    public static final String SERVICE_NEW= BASE_URL + "/Merchant/PublicApi/";
    public static final String SERVICE_NEW_1= BASE_URL + "/Merchant/IndexApi/";
    public static final String SERVICE_NEW_3= BASE_URL + "/Merchant/UserApi/";
    public static final String SERVICE_NEW_4= BASE_URL_EXPRESS + "Express/Express/";


    // public static final String SERVER_ADDRESS = "http://yanshi.81dja.com/BbdjApi/";
    // public static final String SERVER_ADDRESS = "http://www.81dja.com/BbdjApi/";

    /**
     * 获取验证码
     */
    public static final String ACTION_GET_IDENTIFY_CODE = "getcode100";

    /**
     * 登录
     */
    public static final String ACTION_LOGIN = "login100";

    /**
     * 登录
     */
    public static final String ACTION_LOGIN_BY_CODE = "loginSMS100";

    /**
     * 上传图片
     */
    public static final String ACTION_COMMIT_PICTURE = "upload100";

    /**
     * 上传扫描图片
     */
    public static final String ACTION_COMMIT_SCAN_PICTURE = "uploadDistinguishImg";


    /**
     * 提交注册信息
     */
    public static final String ACTION_COMMIT_REGISTER_MESSAGE = "register100";

    /**
     * 找回密码
     */
    public static final String ACTION_CHANGE_PASSWORD = "forget100";

    /**
     * 修改密码
     */
    public static final String ACTION_CHANGE_NEW_PASSWORD = "changePassword900";

    /**
     * 选择配送方式
     */
    public static final String ACTION_REQUEST_SELECT_DIS = "takeDistributionMode";

    /**
     * 更改配送方式
     */
    public static final String ACTION_REQUEST_CHANGEDIS = "saveDistributionMode";

    /**
     * 取消订单
     */
    public static final String ACTION_REQUEST_CANNEL_ORDERS = "cancelTakeOrder";

    /**
     * 确认接单
     */
    public static final String ACTION_REQUEST_RECEIVE_ORDERS = "receiptTakeOrder";

    /**
     * 获取外卖订单
     */
    public static final String ACTION_REQUEST_TAKE_ORDER = "getTakeOrders";


    /**
     * 充值记录
     */
    public static final String ACTION_GET_RECHARGE_RECODE = "rechargeRecord200";

    /**
     * 短信充值面板
     */
    public static final String ACTION_GET_RECHARGE_PANNEL = "smsMerchandise200";

    /**
     * 短信充值
     */
    public static final String ACTION_RECHARGE_MONEY = "smsRecharge200";

    /**
     * 面单单价
     */
    public static final String ACTION_PANNEL_UNITE_PRICE = "singleMerchandise200";

    /**
     * 面单充值
     */
    public static final String ACTION_PANNEL_RECHARGEL = "faceRecharge200";

    /**
     * 驿站地址
     */
    public static final String ACTION_STAGE_ADDRESS = "getAddressBook300";

    /**
     * 获取首页轮播图
     */
    public static final String ACTION_REQUEST_BANNER = "getAdvertImage";


    /**
     * 收货地址
     */
    public static final String ACTION_GET_MY_ADDRESS = "getAddress960";

    /**
     * 获取省市县
     */
    public static final String ACTION_GET_AREA = "getRegion400";

    /**
     * 更新快递公司状态
     */
    public static final String ACTION_UPDATE_EXPRESS = "getExpressInfo600";

    /**
     * 修改地址
     */
    public static final String ACTION_CHNAGE_ADDRESS = "saveAddressBook300";

    /**
     * 修改收货地址
     */
    public static final String ACTION_CHNAGE_MY_ADDRESS = "saveAddress960";

    /**
     * 添加地址
     */
    public static final String ACTION_ADD_ADDRESS = "addAddressBook300";

    /**
     * 添加收获地址
     */
    public static final String ACTION_ADD_MY_ADDRESS = "addAddress960";

    /**
     * 删除地址簿
     */
    public static final String ACTION_DELETE_ADDRESS = "deleteAddressBook300";

    /**
     * 删除收货地址
     */
    public static final String ACTION_DELETE_MY_ADDRESS = "deleteAddress960";

    /**
     * 物流公司
     */
    public static final String ACTION_EXPRESSAGE_LIST = "getExpress300";

    /**
     * 物品类型
     */
    public static final String ACTION_GOODS_TYPE = "getItemType300";

    /**
     * 身份认证
     */
    public static final String ACTION_COMMIT_AUTHENTICATION = "realnameAuthentication300";

    /**
     * 实名验证
     */
    public static final String ACTION_IS_IDENTIFY_REQUEST = "testingAuthentication300";

    /**
     * 手动下单
     */
    public static final String ACTION_COMMIT_ORDER = "placeAnOrder300";

    /**
     * 预估价格
     */
    public static final String ACTION_ESTIMMATE_REQUEST = "freightDateEstimate300";

    /**
     * 待收件
     */
    public static final String ACTION_WAIT_COLLECT = "waitingForCollection300";

    /**
     * 已处理
     */
    public static final String ACTION_HAVE_FINISH = "processed400";

    /**
     * 催单
     */
    public static final String ACTION_HANDLE_FINISH = "getReminder101";

    /**
     * 待打印
     */
    public static final String ACTION_WAIT_PRINT = "pendingPrinting400";

    /**
     * 订单详情
     */
    public static final String ACTION_ORDER_DETAIL = "getMailingdetails500";

    /**
     * 取消订单原因
     */
    public static final String ACTION_CANNEL_ORDER_CAUSE = "getReason400";

    /**
     * 取消订单
     */
    public static final String ACTION_COMMIT_CANNEL_ORDER = "cancellationOrder500";

    /**
     * 取消服务类型订单
     */
    public static final String ACTION_COMMIT_SERVICE_ORDER = "cancellationMyOrder";

    /**
     * 打印时验证身份是否实名
     */
    public static final String ACTION_IDETIFY_AT_SEAL = "supplementInformation600";

    /**
     * 先存后打 保存信息
     */
    public static final String ACTION_COMMIT_SAVE_MAIL = "saveInformation500";

    /**
     * 再打一单
     */
    public static final String ACTION_COMMIT_SAVE_MAIL_DETAIL = "fightWaybillNumber600";

    
    /**
     * 寄件管理中身份认证
     */
    public static final String ACTION_COMMIT_IDENTIFICATION_FOR_MANAGER = "testingInformation600";

    /**
     * 立刻打印 之后的信息补充
     */
    public static final String ACTION_PRINT_ONCE_REQUEST = "getWaybillNumber700";

    /**
     * 获取首页面板中的信息
     */
    public static final String ACTION_GET_PANNEL_MESSAGE_rEQUEST = "indexBlending200";

    /**
     * 获取预估价
     */
    public static final String ACTION_GET_PREDICT_REQUEST = "freightDateEstimate300";

    /**
     * 获取快递公司
     */
    public static final String ACTION_GET_EXPRESS_LOGO_REQUEST = "getExpress600";

    /**
     * 短信管理
     */
    public static final String ACTION_GET_MESSAGE_MANAGER_REQUEST = "getSMSManagement700";

    /**
     * 发送短信
     */
    public static final String ACTION_SEND_MESSAGE_AGAIN = "againSendSMS700";

    /**
     * 获取投诉管理
     */
    public static final String ACTION_COMPLAIN_MANAGER = "getComplaintlist700";

    /**
     * 搜索物流信息
     */
    public static final String ACTION_SEARCH_PACKAGE_REQUEST = "getLogisticsSelect700";

    /**
     * 获取用户基本信息
     */
    public static final String ACTION_GET_USER_BASEMESSAGE = "getmessage100";

    /**
     * 获取通知公告
     */
    public static final String ACTION_GET_NOTIFICATION_REQUEST = "getNoticelist700";

    /**
     * 获取系统消息
     */
    public static final String ACTION_GET_MESSAGE_CENTER_REQUEST = "getSystemlist700";

    /**
     * 检测是否绑定账户
     */
    public static final String ACTION_CHECK_BIND_ACCOUNT = "testBindAccount700";

    /**
     * 绑定支付宝账号
     */
    public static final String ACTION_BIND_ALI_ACCOUNT = "getBindAccount700";

    /**
     * 申请提现
     */
    public static final String ACTION_APPLY_MONEY = "getCashApply700";

    /**
     * 获取提现记录
     */
    public static final String ACTION_GET_MONRY_REQUEST = "getWithdrawalslist700";

    /**
     * 消费记录
     */
    public static final String ACTION_CONSUME_RECORD_REQUEST = "getConsumelist700";

    /**
     * 消费记录
     */
    public static final String ACTION_CONSUME_DETAIL_REQUEST = "getConsumptionInfo";

    /**
     * 充值记录
     */
    public static final String ACTION_RECHARGE_RECORD_REQUEST = "getRechargelist1060";

    /**
     * 获取客户含订单列表
     */
    public static final String ACTION_CLIENT_LIST_REQUEST = "getCustomerdata800";

    /**
     * 获取客户管理列表
     */
    public static final String ACTION_CLIENT_MANAGER_REQUEST = "getCustomerlist800";

    /**
     * 添加客户信息
     */
    public static final String ACTION_ADD_CLIENT_REQUEST = "addCustomer800";

    /**
     * 删除客户信息
     */
    public static final String ACTION_DELETE_CLIENT_REQUEST = "deleteCustomer800";

    /**
     * 编辑客户信息
     */
    public static final String ACTION_EDIT_CLIENT_REQUEST = "saveCustomer800";

    /**
     * 获取客户订单
     */
    public static final String ACTION_GET_CLIENT_ORDER_REQUEST = "getCustomerMailing800";

    /**
     * 我的订单
     */
    public static final String ACTION_MY_ORDER_REQUEST = "getmyOrders960";

    /**
     * 获取订单详情
     */
    public static final String ACTION_MY_ORDER_DETAIL_REQUEST = "getmyOrdersdetails960";

    /**
     * 获取物料商城列表
     */
    public static final String ACTION_GOODS_LIST_REQUEST = "getProductdata950";

    /**
     * 获取商品详情
     */
    public static final String ACTION_GOODS_DETAIL_LIST = "getProductdetails950";

    /**
     * 加入购物车
     */
    public static final String ACTION_JOIN_GOODS = "addProductcart950";

    /**
     * 立刻清算
     */
    public static final String ACTION_PAYFOR_ATONCE = "buyProductonce950";

    /**
     * 批量购买
     */
    public static final String ACTION_PAYFOR_MORE = "buyProductcart950";

    /**
     * 获取购物车列表
     */
    public static final String ACTION_GET_SHOP_CAR_REQUEST = "getProductcart950";

    /**
     * 删除购物车商品
     */
    public static final String ACTION_DELETE_GOODS_REQUEST = "deleteProductcart950";

    /**
     * 修改商品的数量
     */
    public static final String ACTION_CHANGE_GOODS_NUMBER = "saveCartnumber950";

    /**
     * 获取交接管理
     */
    public static final String ACTION_CHANGE_MANAGER_REQUEST = "getHandoverlist980";

    /**
     * 确认交接
     */
    public static final String ACTION_CHANGE_SNED_REQUEST = "getConfirmHandover980";

    /**
     * 数据中心
     */
    public static final String ACTION_DATA_CENTER_rEQUEST = "getFinancialData980";

    /**
     * 财务管理
     */
    public static final String ACTION_MONEY_MANAGER_REQEST = "getFinanceData990";

    /**
     * 取消订单
     */
    public static final String ACTION_CANNEL_ORDER_REQUEST = "CancellationMail1010";

    /**
     * 首页搜索寄件
     */
    public static final String ACTION_GLOBALE_SEND_REQUEST = "MailSearch1070";

    /**
     * 确认完成订单
     */
    public static final String ACTION_REQUEST_COMPLETE_ORDERS = "completeTakeOrders";

    /**
     * 首页搜索派件
     */
    public static final String ACTION_GLOABLE_RECEIVE_REQUEST = "PieSearch1070";

    /**
     * 财务首页
     */
    public static final String ACTION_MONEY_MANAGER_REQUESTR = "getFinanceIndex1050";

    /**
     * 获取昨天支出
     */
    public static final String ACTION_GET_YESTERDAY_SEND_REQUEST = "getYesterdayReconciliation1050";

    /**
     * 获取昨天寄件数量
     */
    public static final String ACTION_GET_YESTERDAY_SEND__REQUEST = "getYesterdayMail1050";

    /**
     * 获取昨天派件数量
     */
    public static final String ACTION_GET_YESTERDAY_PAI__REQUEST = "getYesterdayPie1050";

    /**
     * 获取数据排行榜
     */
    public static final String ACTION_SORT_REQUEST = "getRankingList1050";

    /**
     * 获取数据日报
     */
    public static final String ACTION_REPORT_DATE_REQUEST = "getDilyData1050";

    /**
     * 获取月报数据
     */
    public static final String ACITON_REPORT_MONTH_REQUEST = "getMonthlyData1050";

    /**
     * 添加备注
     */
    public static final String ACTION_ADD_MARK_REQUEST = "saveHandoverContent980";

    /**
     * 检测快递公司id
     */
    public static final String ACTION_CHECK_WAY_BILL = "TestingExpress2010";

    /**
     * 全部入库
     */
    public static final String ACTION_ENTER_RECORDE_REQUEST = "WarehousingData300";

    /**
     * 入库列表
     */
    public static final String ACTION_ENTER_REPERTORY_REQUEST = "getWarehouselist2010";

    /**
     * 出库列表
     */
    public static final String ACTION_OUT_REPERTORY_REQUEST = "getOutWarehouselist2010";

    /**
     * 获取最新提货码
     */
    public static final String ACTION_GET_NEW_PACKAGE_REQUEST = "TestingExpress300";

    /**
     * 检测出库单号状态
     */
    public static final String ACTION_CHECK_OUT_BILL_REQUEST = "TestingWaybillNumber2010";

    /**
     * 全部出库
     */
    public static final String ACTION_OUT_OF_REPERTORY_REQUEST = "getExpressDelivery2010";

    /**
     * 获取派件详情
     */
    public static final String ACTION_EXPRESS_DETAIL_REQUEST = "getOutWarehouseinfo2010";

    /**
     * 确认干洗送达
     */
    public static final String ACTION_CONFIRM_CLEAR_ORDER_REQUEST = "confirmationClearService";

    /**
     * 待入库
     */
    public static final String ACTION_EXPRESS_WAIT_STORE = "getPackageList";

    /**
     * 确认入库
     */
    public static final String ACTION_CONFIRM_ENTER_STORE = "confirmCourierPackage";

    /**
     * 刪除
     */
    public static final String ACTION_DELETE_ENTER_STORE = "delectPackage";

    /**
     * 订单
     */
    public static final String ACTION_GET_ORDER = "getOrdersList";

    /**
     * 桶装水送达
     */
    public static final String ACTION_WATER_SEND = "confirmationService";

    /**
     * 桶装水接单
     */
    public static final String ACTION_WATER_RECEIVE = "confirmWaterOrder";

    /**
     * 获取取消服务类型的取消原因
     */
    public static final String ACTION_GET_SERTVICE_ORDER = "getReasons";

    /**
     * 取消干洗订单
     */
    public static final String ACTION_CANNEL_CLEAR_ORDER = "confirmCleaningOrder";


    /**
     * 获取干洗类目
     */
    public static final String ACTION_REQUEST_CLEAR_TYPE = "getServiceGoods";

    /**
     * 获取干洗类目
     */
    public static final String ACTION_REQUEST_CLEAR_PRICE = "confirmGoodsCategory";

    /**
     * 获取门店的货架
     */
    public static final String ACTION_REQUEST_STORE_SHELVES = "getShelves";

    /**
     * 商户运费
     */
    public static final String ACTION_REQUEST_WAY_MONEY = "getFreightList";

    /**
     * 设置运费
     */
    public static final String ACTION_REQUEST_COMMIT_SETTING = "saveStationFreight";

    /**
     * 获取实例图片
     */
    public static final String ACTION_REQUEST_IMAGE = "getBindGoods";

    /**
     * 获取实例图片
     */
    public static final String ACTION_REQUEST_COMMIT_GOODS_CODE = "getGoodsCode";

    /**
     * 获取对应货架下的商品
     */
    public static final String ACTION_REQUEST_GOODS = "getGoods";

    /**
     * 删除货架
     */
    public static final String ACTION_REQUEST_DELETE_GOODS = "deltShelves";

    /**
     * 修改商品名称和价格
     */
    public static final String ACTION_CHAGNE_GOODS_PRICE_NAME = "editGoods";

    /**
     * 修改货架名称
     */
    public static final String ACTION_CHAGNE_SHELVES_NAME= "editShelves";

    /**
     * 删除商品
     */
    public static final String ACTION_DELETE_GOODS = "deleGoods";

    /**
     * 下架
     */
    public static final String ACTION_TOGGLE_GOODS = "toggleGoods";


    /**
     * 获取实例名称
     */
    public static final String ACTION_REQUEST_NAME = "getRecommend";

    /**
     * 添加商品
     */
    public static final String ACTION_REQUEST_ADD_GOODS = "addGoods";

    /**
     * 添加优惠券
     */
    public static final String ACTION_REQUEST_CREATE_COUPON = "addPreferential";

    /**
     * 查询优惠券
     */
    public static final String ACTION_REQUEST_SEARCH_COUPON = "preferentialOffer";

    /**
     * 搜索货架
     */
    public static final String ACTION_REQUEST_SEARCH_SHELVES = "seaShelves";

    /**
     * 搜索货架和商品
     */
    public static final String ACTION_REQUEST_SEARCH_SHELVES_AND_GOODS = "getPreset";

    /**
     * 添加货架
     */
    public static final String ACTION_REQUEST_ADD_SHELVES = "addShelves";

    /**
     * 添加货架和商品
     */
    public static final String ACTION_REQUEST_ADD_SHELVES_GOODS = "addPresetClass";

    /**
     * 获取关注人数
     */
    public static final String ACTION_REQUEST_FLOW_STATION = "followStation";

    /**
     * 我的客户
     */
    public static final String ACTION_REQUEST_MY_CLIENT = "getStationUsers";

    /**
     * 获取商品库
     */
    public static final String ACTION_REQUEST_GOODS_STORE = "getClassProductHouse";

    /**
     * 添加商品
     */
    public static final String ACTION_REQUEST_ADD_GOODS_BY_STORE= "addClassProduct";


    /**
     * 搜索商品
     */
    public static final String ACTION_REQUEST_SEARCH_GOODS= "searchGoods";


    /**
     * 获取商品价格
     */
    public static final String ACTION_REQUEST_GET_GOODS_PRICE= "getGoodsInfo";


    /**
     * 发放优惠券
     */
    public static final String ACTION_REQUEST_DISPATH_COUPON = "followCoupon";

    /**
     * 订单详情
     */
    public static final String ACTION_REQUEST_ORDER_DETAIL = "getTakeOrdersInfo";

    /**
     * 快递员配送金额
     */
    public static final String ACTION_REQUEST_GETSEND_BY_WXPRESS = "getDeliveryMoney";

    /**
     * 获取优惠券使用记录
     */
    public static final String ACTION_REQUEST_COUPON_RECORD = "couponRecord";

    /**
     * 添加特价商品
     */
    public static final String ACTION_REQUEST_ADD_SPECIAL_GOODS = "addSpecialProduct";

    /**
     * 扫描添加
     */
    public static final String ACTION_REQUEST_SCANL_GOODS = "getGoodsCode";

    /**
     * 我的界面
     */
    public static final String ACTION_REQUEST_MY_MESSAGE = "getUserInfo";

    /**
     * 派件 对快递公司 收费管理
     */
    public static final String ACTION_REQUEST_EXPRESS_MONEY = "getExpressMoney";

    /**
     * 派件 对快递公司 收费管理
     */
    public static final String ACTION_REQUEST_SET_EXPRESS_MONEY = "saveExpressMoney";

    /**
     * 获取最新的取件码
     */
    public static final String ACTION_REQUEST_GET_EXPRESS_CODE = "expressCode";

    /**
     * 入库
     */
    public static final String ACTION_REQUEST_ENTER = "warehousing";

    /**
     * 检测运单号
     */
    public static final String ACTION_CHECK_PHONE = "TestingWaybillNumber2010";
}
