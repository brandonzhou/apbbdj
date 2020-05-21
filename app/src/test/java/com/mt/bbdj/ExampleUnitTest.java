package com.mt.bbdj;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shshcom.station.storage.http.bean.BaseResult;
import com.shshcom.station.storage.http.bean.StationOrcResult;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }



    @Test
    public void json(){
        Gson gson = new Gson();
        String json = "{\"code\":5001,\"msg\":\"请求成功\",\"data\":{\"succeed\":1,\"fail\":7,\"succeed_lists\":[{\"pie_id\":145121,\"number\":\"77111187755872\",\"code\":\"Y700940\",\"mobile\":\"18618203265\",\"msg\":\"\",\"express_id\":100101,\"express_name\":\"中通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/1c10e35e31062840ea909e3288b60e41.jpg\"}],\"fail_lists\":[{\"pie_id\":144528,\"number\":\"7711118775587223\",\"code\":\"Y7009402\",\"mobile\":\"18618203265\",\"msg\":\"取件码重复\",\"express_id\":100101,\"express_name\":\"中通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/74391167756b4f963f23f5c07488e76c.jpg\"},{\"pie_id\":145120,\"number\":\"77111187755872243\",\"code\":\"33333\",\"mobile\":\"18618203265\",\"msg\":\"取件码重复\",\"express_id\":100101,\"express_name\":\"中通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/3fa492d5c06ebcceb648e563c680181b.jpg\"},{\"pie_id\":145233,\"number\":\"YT4295748861510\",\"code\":\"1000\",\"mobile\":\"\",\"msg\":\"手机号识别失败\",\"express_id\":100102,\"express_name\":\"圆通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/b594b926d2a3a9cd7710a7f23da444c8.jpg\"},{\"pie_id\":145234,\"number\":\"YT4295748861510\",\"code\":\"1000\",\"mobile\":\"\",\"msg\":\"手机号识别失败\",\"express_id\":100102,\"express_name\":\"圆通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/f6e7d3d2bda5b8699477196d9942e1b6.jpg\"},{\"pie_id\":145240,\"number\":\"YT4295748861510\",\"code\":\"1000\",\"mobile\":\"\",\"msg\":\"手机号识别失败\",\"express_id\":100102,\"express_name\":\"圆通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/9facaf1ae8dbe0c024e685cda8194439.jpg\"},{\"pie_id\":145241,\"number\":\"YT4295748861510\",\"code\":\"1000\",\"mobile\":\"\",\"msg\":\"手机号识别失败\",\"express_id\":100102,\"express_name\":\"圆通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/c9efee6867eb6225a20f51c92f97c870.jpg\"},{\"pie_id\":145242,\"number\":\"YT4295748861510\",\"code\":\"1000\",\"mobile\":\"\",\"msg\":\"手机号识别失败\",\"express_id\":100102,\"express_name\":\"圆通快递\",\"picture\":\"https://qrcode.taowangzhan.com/express/20200521/1ee4301d5381cac47144909ee96608e6.jpg\"}]}}";
//        ResponseResult<List<UserInfo>> responseResult1 = JSON.parseObject(str, new TypeReference<ResponseResult<List<UserInfo>>>() {});
//        BaseResult<List<String>> responseResult2 = gson.fromJson(json , new TypeToken<BaseResult<List<String>>>(){}.getType());
//        BaseResult<List<String>> responseResult2 = gson.fromJson(json , new TypeToken<BaseResult<StationOrcResult>>(){}.getType());

        BaseResult result = JSON.parseObject(json, new TypeReference<BaseResult<StationOrcResult>>(){});


        BaseResult responseResult2 = gson.fromJson(json , new TypeToken<BaseResult<StationOrcResult>>(){}.getType());


        System.out.println(1);
    }
}