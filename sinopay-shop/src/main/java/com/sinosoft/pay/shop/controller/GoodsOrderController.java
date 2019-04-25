package com.sinosoft.pay.shop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sinosoft.pay.common.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.sinosoft.pay.common.constant.PayConstant;
import com.sinosoft.pay.shop.dao.model.GoodsOrder;
import com.sinosoft.pay.shop.service.GoodsOrderService;
import com.sinosoft.pay.shop.util.Constant;
import com.sinosoft.pay.shop.util.OAuth2RequestParamHelper;
import com.sinosoft.pay.shop.util.vx.WxApi;
import com.sinosoft.pay.shop.util.vx.WxApiClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Controller
@RequestMapping("/pay")
public class GoodsOrderController {

    private final static MyLog _log = MyLog.getLog(GoodsOrderController.class);

    @Autowired
    private GoodsOrderService goodsOrderService;



    private AtomicLong seq = new AtomicLong(0L);


    @RequestMapping(value = "/sinopay")
    @ResponseBody
    public String sinopay(@RequestParam("jsonstr") String jsonstr) {
        JSONObject paramObj = JSON.parseObject(jsonstr);
        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", Constant.mchId);                               // 商户ID
        paramMap.put("mchOrderNo", System.currentTimeMillis());     // 商户订单号
        // 支付渠道ID, WX_NATIVE(微信扫码),WX_JSAPI(微信公众号或微信小程序),WX_APP(微信APP),WX_MWEB(微信H5),ALIPAY_WAP(支付宝手机支付),ALIPAY_PC(支付宝网站支付),ALIPAY_MOBILE(支付宝移动支付)
        paramMap.put("channelId", paramObj.get("paytype"));
        float payamount = 100*paramObj.getFloat("payamount");//元转分
        long payAmountL = (long)payamount;
        paramMap.put("amount", payAmountL);                                  // 支付金额,单位分
        paramMap.put("currency", "cny");                            // 币种, cny-人民币
        paramMap.put("clientIp", "101.81.99.25");                 // 用户地址,微信H5支付时要真实的
        paramMap.put("device", "WEB");                              // 设备
        paramMap.put("subject", "sinopay_支付测试");
        paramMap.put("body", "sinopay_支付测试");
        paramMap.put("notifyUrl", Constant.notifyUrl);                       // 回调URL
        paramMap.put("param1", "");                                 // 扩展参数1
        paramMap.put("param2", "");                                 // 扩展参数2
        paramMap.put("extra", "{\n" +
                "  \"productId\": \"120989823\",\n" +
                "  \"openId\": \"oIkQuwhPgPUgl-TvQ48_UUpZUwMs\",\n" +
                "  \"sceneInfo\": {\n" +
                "    \"h5_info\": {\n" +
                "      \"type\": \"Wap\",\n" +
                "      \"wap_url\": \"http://oa.sinosoftzx.cn\",\n" +
                "      \"wap_name\": \"oa.sinosoftzx.cn_test\"\n" +
                "    }\n" +
                "  }\n" +
                " ,\"discountable_amount\":\"0.00\"," + //面对面支付扫码参数：可打折金额 可打折金额+不可打折金额=总金额
                "  \"undiscountable_amount\":\"0.00\"," + //面对面支付扫码参数：不可打折金额
                "}");  // 附加参数

        //{"h5_info": {"type":"Wap","wap_url": "https://pay.qq.com","wap_name": "腾讯充值"}}

        String reqSign = PayDigestUtil.getSign(paramMap, Constant.reqKey);
        paramMap.put("sign", reqSign);                              // 签名
        String reqData = "params=" + paramMap.toJSONString();
        System.out.println("请求支付中心下单接口,请求数据:" + reqData);
        String url = Constant.baseUrl + "/pay/create_order?";
        String result = SinoPayUtil.call4Post(url + reqData);
        System.out.println("请求支付中心下单接口,响应数据:" + result);
        Map retMap = JSON.parseObject(result);
        if("SUCCESS".equals(retMap.get("retCode")) && "SUCCESS".equalsIgnoreCase(retMap.get("resCode").toString())) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retMap, Constant.resKey, "sign", "payParams");
            String retSign = (String) retMap.get("sign");
            if(checkSign.equals(retSign)) {
                System.out.println("=========支付中心下单验签成功=========");
            }else {
                System.err.println("=========支付中心下单验签失败=========");
                return null;
            }
        }
        //return retMap.get("payOrderId")+"";
        return result;
    }

    @RequestMapping(value = "/buy/{goodsId}", method = RequestMethod.GET)
    @ResponseBody
    public String buy(@PathVariable("goodsId") String goodsId) {
        if(!"G_0001".equals(goodsId)) {
            return "fail";
        }
        String goodsOrderId = String.format("%s%s%06d", "G", DateUtil.getSeqString(), (int) seq.getAndIncrement() % 1000000);
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setGoodsOrderId(goodsOrderId);
        goodsOrder.setGoodsId(goodsId);
        goodsOrder.setGoodsName("捐助商品G_0001");
        goodsOrder.setAmount(1l);
        goodsOrder.setUserId("000001");
        goodsOrder.setStatus(Constant.GOODS_ORDER_STATUS_INIT);
        int result = goodsOrderService.addGoodsOrder(goodsOrder);
        _log.info("插入商品订单,返回:{}", result);
        return result+"";
    }

    @RequestMapping(value = "/pay/{goodsOrderId}", method = RequestMethod.GET)
    @ResponseBody
    public String pay(@PathVariable("goodsOrderId") String goodsOrderId) {
        GoodsOrder goodsOrder = goodsOrderService.getGoodsOrder(goodsOrderId);
        if(goodsOrder == null){ return "fail";}
        int status = goodsOrder.getStatus();
        if(status != Constant.GOODS_ORDER_STATUS_INIT) {
            return "fail_001";
        }
        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", Constant.mchId);                       // 商户ID
        paramMap.put("mchOrderNo", goodsOrderId);           // 商户订单号
        paramMap.put("channelId", "ALIPAY_WAP");             // 支付渠道ID, WX_NATIVE,ALIPAY_WAP
        paramMap.put("amount", goodsOrder.getAmount());                          // 支付金额,单位分
        paramMap.put("currency", "cny");                    // 币种, cny-人民币
        paramMap.put("clientIp", "114.112.124.236");        // 用户地址,IP或手机号
        paramMap.put("device", "WEB");                      // 设备
        paramMap.put("subject", goodsOrder.getGoodsName());
        paramMap.put("body", goodsOrder.getGoodsName());
        paramMap.put("notifyUrl", Constant.notifyUrl);         // 回调URL
        paramMap.put("param1", "");                         // 扩展参数1
        paramMap.put("param2", "");                         // 扩展参数2
        paramMap.put("extra", "{\"productId\":\"120989823\",\"openId\":\"o2RvowBf7sOVJf8kJksUEMceaDqo\"}");  // 附加参数

        String reqSign = PayDigestUtil.getSign(paramMap, Constant.reqKey);
        paramMap.put("sign", reqSign);   // 签名
        String reqData = "params=" + paramMap.toJSONString();
        System.out.println("请求支付中心下单接口,请求数据:" + reqData);
        String url = Constant.baseUrl + "/pay/create_order?";
        String result = SinoPayUtil.call4Post(url + reqData);
        System.out.println("请求支付中心下单接口,响应数据:" + result);
        Map retMap = JSON.parseObject(result);
        if("SUCCESS".equals(retMap.get("retCode"))) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retMap, Constant.resKey, "sign", "payParams");
            String retSign = (String) retMap.get("sign");
            if(checkSign.equals(retSign)) {
                System.out.println("=========支付中心下单验签成功=========");
            }else {
                System.err.println("=========支付中心下单验签失败=========");
                return null;
            }
        }
        String payOrderId = retMap.get("payOrderId").toString();

        goodsOrder = new GoodsOrder();
        goodsOrder.setGoodsOrderId(goodsOrderId);
        goodsOrder.setPayOrderId(payOrderId);
        goodsOrder.setChannelId("ALIPAY_WAP");
        int ret = goodsOrderService.update(goodsOrder);
        _log.info("修改商品订单,返回:{}", ret);
        return result+"";
    }

    private Map createPayOrder(GoodsOrder goodsOrder, Map<String, Object> params) {
        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", Constant.mchId);                       // 商户ID
        paramMap.put("mchOrderNo", goodsOrder.getGoodsOrderId());           // 商户订单号
        paramMap.put("channelId", params.get("channelId"));             // 支付渠道ID, WX_NATIVE,ALIPAY_WAP
        paramMap.put("amount", goodsOrder.getAmount());                          // 支付金额,单位分
        paramMap.put("currency", "cny");                    // 币种, cny-人民币
        paramMap.put("clientIp", "114.112.124.236");        // 用户地址,IP或手机号
        paramMap.put("device", "WEB");                      // 设备
        paramMap.put("subject", goodsOrder.getGoodsName());
        paramMap.put("body", goodsOrder.getGoodsName());
        paramMap.put("notifyUrl", Constant.notifyUrl);         // 回调URL
        paramMap.put("param1", "");                         // 扩展参数1
        paramMap.put("param2", "");                         // 扩展参数2

        JSONObject extra = new JSONObject();
        extra.put("openId", params.get("openId"));
        paramMap.put("extra", extra.toJSONString());  // 附加参数

        String reqSign = PayDigestUtil.getSign(paramMap, Constant.reqKey);
        paramMap.put("sign", reqSign);   // 签名
        String reqData = "params=" + paramMap.toJSONString();
        System.out.println("请求支付中心下单接口,请求数据:" + reqData);
        String url = Constant.baseUrl + "/pay/create_order?";
        String result = SinoPayUtil.call4Post(url + reqData);
        System.out.println("请求支付中心下单接口,响应数据:" + result);
        Map retMap = JSON.parseObject(result);
        if("SUCCESS".equals(retMap.get("retCode"))) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retMap, Constant.resKey, "sign", "payParams");
            String retSign = (String) retMap.get("sign");
            if(checkSign.equals(retSign)) {
                System.out.println("=========支付中心下单验签成功=========");
            }else {
                System.err.println("=========支付中心下单验签失败=========");
                return null;
            }
        }
        return retMap;
    }

    @RequestMapping("/openQrPay.html")
    public String openQrPay(ModelMap model) {
        return "openQrPay";
    }

    @RequestMapping("/qrPay.html")
    public String qrPay(ModelMap model, HttpServletRequest request, Long amount) {
        String logPrefix = "【二维码扫码支付】";
        String view = "qrPay";
        _log.info("====== 开始接收二维码扫码支付请求 ======");
        String ua = request.getHeader("User-Agent");
        String goodsId = "G_0001";
        _log.info("{}接收参数:goodsId={},amount={},ua={}", logPrefix, goodsId, amount, ua);
        String client = "alipay";
        String channelId = "ALIPAY_WAP";
        if(StringUtils.isBlank(ua)) {
            String errorMessage = "User-Agent为空！";
            _log.info("{}信息：{}", logPrefix, errorMessage);
            model.put("result", "failed");
            model.put("resMsg", errorMessage);
            return view;
        }else {
            if(ua.contains("Alipay")) {
                client = "alipay";
                channelId = "ALIPAY_WAP";
            }else if(ua.contains("MicroMessenger")) {
                client = "wx";
                channelId = "WX_JSAPI";
            }
        }
        if(client == null) {
            String errorMessage = "请用微信或支付宝扫码";
            _log.info("{}信息：{}", logPrefix, errorMessage);
            model.put("result", "failed");
            model.put("resMsg", errorMessage);
            return view;
        }
        // 先插入订单数据
        GoodsOrder goodsOrder = null;
        Map<String, String> orderMap = null;
        if ("alipay".equals(client)) {
            _log.info("{}{}扫码下单", logPrefix, "支付宝");
            Map params = new HashMap<>();
            params.put("channelId", channelId);
            // 下单
            goodsOrder = createGoodsOrder(goodsId, amount);
            orderMap = createPayOrder(goodsOrder, params);
        }else if("wx".equals(client)){
            _log.info("{}{}扫码", logPrefix, "微信");
            // 判断是否拿到openid，如果没有则去获取
//            String openId = request.getParameter("openId");  目前写死openid
            String openId="oak-Ov7gX2fhnRbrXu0FRZWFC7Rc";
            if (StringUtils.isNotBlank(openId)) {
                _log.info("{}openId：{}", logPrefix, openId);
                Map params = new HashMap<>();
                params.put("channelId", channelId);
                params.put("openId", openId);
                goodsOrder = createGoodsOrder(goodsId, amount);
                // 下单
                orderMap = createPayOrder(goodsOrder, params);
                view = "weixinpay/qrPay";
            }else {
                String redirectUrl = Constant.QR_PAY_URL + "?amount=" + amount;
                String url = Constant.GetOpenIdURL2 + "?redirectUrl=" + redirectUrl;
                _log.info("跳转URL={}", url);
                return "redirect:" + url;
            }
        }
        model.put("goodsOrder", goodsOrder);
        model.put("amount", AmountUtil.convertCent2Dollar(goodsOrder.getAmount()+""));
        if(orderMap != null) {
            model.put("orderMap", orderMap);
            String payOrderId = orderMap.get("payOrderId");
            GoodsOrder go = new GoodsOrder();
            go.setGoodsOrderId(goodsOrder.getGoodsOrderId());
            go.setPayOrderId(payOrderId);
            go.setChannelId(channelId);
            int ret = goodsOrderService.update(go);
            _log.info("修改商品订单,返回:{}", ret);
        }
        model.put("client", client);
        return view;
    }

    GoodsOrder createGoodsOrder(String goodsId, Long amount) {
        // 先插入订单数据
        String goodsOrderId =  String.format("%s%s%06d", "G", DateUtil.getSeqString(), (int) seq.getAndIncrement() % 1000000);
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setGoodsOrderId(goodsOrderId);
        goodsOrder.setGoodsId(goodsId);
        goodsOrder.setGoodsName("SINOPAY测试商品");
        goodsOrder.setAmount(amount);
        goodsOrder.setUserId("000001");
        goodsOrder.setStatus(Constant.GOODS_ORDER_STATUS_INIT);
        goodsOrder.setMchOrderNo(goodsOrderId);
        int result = goodsOrderService.addGoodsOrder(goodsOrder);
        _log.info("插入商品订单,返回:{}", result);
        return goodsOrder;
    }

    /**
     * 获取code
     * @return
     */
    @RequestMapping("/getOpenId")
    public void getOpenId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        _log.info("进入获取用户openID页面");
        String redirectUrl = "https://localhost:8181/sinopay/wechatpay/paymoney?payamount=1&paytype=WX_JSAPI&goodsOrderNo=G20180807230642000000&params{\"mchId\":\"10000000\",\n" +
                "\"notifyUrl\":\"http://www.baidu.com\",\n" +
                "\"amount\":1,\n" +
                "\"subject\":\"sinopay_支付测试\",\n" +
                "\"body\":\"sinopay_支付测试\",\n" +
                "\"succReturnUrl\":\"http://www.taobao.com\",\n" +
                "\"failReturnUrl\":\"http://www.jd.com\"}";//request.getParameter("redirectUrl");
//        String openId = session.getAttribute("openId").toString();
//        if(!StringUtils.isBlank(openId)){
//            if(redirectUrl.indexOf("?") > 0) {
//                redirectUrl += "&openId=" + openId;
//            }else {
//                redirectUrl += "?openId=" + openId;
//            }
//            _log.info("最终重定向的URL1={}",redirectUrl);
//            response.sendRedirect(redirectUrl);
//        }else{
//            String redirectUrls = session.getAttribute("redirectUrl")==null?"":session.getAttribute("redirectUrl").toString();
//            if(!StringUtils.isBlank(redirectUrls)){//redirectUrl如果在session中，就用session中的值，不在就存入session
//                redirectUrl = redirectUrls;
//            }else{
//                session.setAttribute("redirectUrl",redirectUrl);
//            }
            String code = request.getParameter("code");
            String stateStr = request.getParameter("state");
            _log.info("redirectUrl = {} ; code = {} ;state = {}", redirectUrl,code,stateStr);
//            stateStr = "redirectUrl=http://127.0.0.1:8181/sinopay/wechatpay/paymoney?payamount=0.01&params={\"mchId\":\"10000000\",\"mchOrderNo\":\"0000006\",\"currency\":\"cny\",\"amount\":\"1\",\"notifyUrl\":\"http://www.xxx.com/pay_callback\",\"subject\":\"捷顺测试1\",\"body\":\"捷顺测试2\",\"succReturnUrl\":\"http://www.baidu.com?appid=123\",\"failReturnUrl\":\"http://www.qq.com\",\"sign\":\"123\"}";
            if(!StringUtils.isBlank(stateStr)){
                String str = stateStr.substring(stateStr.indexOf("=")+1,stateStr.length());
                //风险提示：如果参数里面有!  那有可能会导致替换错误。
                redirectUrl = str.replaceAll("!","&");
                //参数需要进行URLEncode
                String strParam = redirectUrl.substring(str.indexOf("?")+1);
                strParam = URLEncoder.encode(strParam, "UTF-8");
                redirectUrl = redirectUrl.substring(0,str.indexOf("?")+1)+strParam;
            }
            String openId = "";
            if(!StringUtils.isBlank(code)){//如果request中包括code，则是微信回调
                try {
                    openId = WxApiClient.getOAuthOpenId(Constant.AppID, Constant.AppSecret, code);
                    _log.info("调用微信返回openId={}", openId);
                } catch (Exception e) {
                    _log.error(e, "调用微信查询openId异常");
                }
                if(redirectUrl.indexOf("?") > 0) {
                    redirectUrl += "&openId=" + openId;
                }else {
                    redirectUrl += "?openId=" + openId;
                }
//                session.setAttribute("openId",openId);
                _log.info("最终重定向的URL2={}",redirectUrl);
                response.sendRedirect(redirectUrl);
            }else{//oauth获取code
                String redirectUrl4Vx = Constant.GetOpenIdURL + "?redirectUrl=" + redirectUrl;
                String state = OAuth2RequestParamHelper.prepareState(request);
                String url = WxApi.getOAuthCodeUrl(Constant.AppID, redirectUrl4Vx, "snsapi_base", state);
                _log.info("跳转URL={}", url);
                response.sendRedirect(url);
            }
//        }
    }

    /**
     * 获取code
     * @return
     */
    @RequestMapping("/getOpenId2")
    public void getOpenId2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        _log.info("进入获取用户openID页面");
        String redirectUrl = request.getParameter("redirectUrl");
        String code = request.getParameter("code");
        String openId = "";
        if(!StringUtils.isBlank(code)){//如果request中包括code，则是微信回调
            try {
                openId = WxApiClient.getOAuthOpenId(Constant.AppID, Constant.AppSecret, code);
                _log.info("调用微信返回openId={}", openId);
            } catch (Exception e) {
                _log.error(e, "调用微信查询openId异常");
            }
            if(redirectUrl.indexOf("?") > 0) {
                redirectUrl += "&openId=" + openId;
            }else {
                redirectUrl += "?openId=" + openId;
            }
            response.sendRedirect(redirectUrl);
        }else{//oauth获取code
            //http://www.abc.com/xxx/get-weixin-code.html?appid=XXXX&scope=snsapi_base&state=hello-world&redirect_uri=http%3A%2F%2Fwww.xyz.com%2Fhello-world.html
            String redirectUrl4Vx = Constant.GetOpenIdURL2 + "?redirectUrl=" + redirectUrl;
            String url = String.format("http://www.xiaoshuding.com/get-weixin-code.html?appid=%s&scope=snsapi_base&state=hello-world&redirect_uri=%s", Constant.AppID, WxApi.urlEnodeUTF8(redirectUrl4Vx));
            _log.info("跳转URL={}", url);
            //在这里我们给Post请求的头部加上User-Agent来伪装成微信内置浏览器
            response.setHeader("User-Agent","Mozilla/5.0 (Linux; U; Android 2.3.6; zh-cn; GT-S5660 Build/GINGERBREAD) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1 MicroMessenger/4.5.255");
            //这个是在网上看到的，要加上这个，避免其他错误
            response.setHeader("Referer", "https://mp.weixin.qq.com");
            response.sendRedirect(url);
        }
    }

    /**
     * 接收支付中心通知
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/payNotify")
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        _log.info("====== 开始处理支付中心通知 ======");
        Map<String,Object> paramMap = request2payResponseMap(request, new String[]{
                "payOrderId","mchId","mchOrderNo","channelId","amount","currency","status", "clientIp",
                "device",  "subject", "channelOrderNo", "param1",
                "param2","paySuccTime","backType","sign"
        });
        _log.info("支付中心通知请求参数,paramMap={}", paramMap);
        if (!verifyPayResponse(paramMap)) {
            String errorMessage = "verify request param failed.";
            _log.warn(errorMessage);
            outResult(response, "fail");
            return;
        }
        String payOrderId = (String) paramMap.get("payOrderId");
        String mchOrderNo = (String) paramMap.get("mchOrderNo");
        String resStr;
        try {
            GoodsOrder goodsOrder = goodsOrderService.getGoodsOrder(mchOrderNo);
            if(goodsOrder != null && goodsOrder.getStatus() == Constant.GOODS_ORDER_STATUS_COMPLETE) {
                outResult(response, "success");
                return;
            }
            // 执行业务逻辑
            int ret = goodsOrderService.updateStatus4Success(mchOrderNo);
            // ret返回结果
            // 等于1表示处理成功,返回支付中心success
            // 其他值,返回支付中心fail,让稍后再通知
            if(ret == 1) {
                ret = goodsOrderService.updateStatus4Complete(mchOrderNo);
                if(ret == 1) {
                    resStr = "success";
                }else {
                    resStr = "fail";
                }
            }else {
                resStr = "fail";
            }
        }catch (Exception e) {
            resStr = "fail";
            _log.error(e, "执行业务异常,payOrderId=%s.mchOrderNo=%s", payOrderId, mchOrderNo);
        }
        _log.info("响应支付中心通知结果:{},payOrderId={},mchOrderNo={}", resStr, payOrderId, mchOrderNo);
        outResult(response, resStr);
        _log.info("====== 支付中心通知处理完成 ======");
    }

    @RequestMapping("/notify_test")
    public void notifyTest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        outResult(response, "success");
    }

    @RequestMapping("/toAliPay.html")
    @ResponseBody
    public String toAliPay(HttpServletRequest request, Long amount, String channelId) {
        String logPrefix = "【支付宝支付】";
        _log.info("====== 开始接收支付宝支付请求 ======");
        String goodsId = "G_0001";
        _log.info("{}接收参数:goodsId={},amount={},channelId={}", logPrefix, goodsId, amount, channelId);
        // 先插入订单数据
        Map params = new HashMap<>();
        params.put("channelId", channelId);
        // 下单
        GoodsOrder goodsOrder = createGoodsOrder(goodsId, amount);
        Map<String, String> orderMap = createPayOrder(goodsOrder, params);
        if(orderMap != null && "success".equalsIgnoreCase(orderMap.get("resCode"))) {
            String payOrderId = orderMap.get("payOrderId");
            GoodsOrder go = new GoodsOrder();
            go.setGoodsOrderId(goodsOrder.getGoodsOrderId());
            go.setPayOrderId(payOrderId);
            go.setChannelId(channelId);
            int ret = goodsOrderService.update(go);
            _log.info("修改商品订单,返回:{}", ret);
        }
        if(PayConstant.PAY_CHANNEL_ALIPAY_MOBILE.equalsIgnoreCase(channelId)) return orderMap.get("payParams");
        return orderMap.get("payUrl");
    }

    void outResult(HttpServletResponse response, String content) {
        response.setContentType("text/html");
        PrintWriter pw;
        try {
            pw = response.getWriter();
            pw.print(content);
            _log.error("response  complete.");
        } catch (IOException e) {
            _log.error(e, "response  write exception.");
        }
    }

    public Map<String, Object> request2payResponseMap(HttpServletRequest request, String[] paramArray) {
        Map<String, Object> responseMap = new HashMap<>();
        for (int i = 0;i < paramArray.length; i++) {
            String key = paramArray[i];
            String v = request.getParameter(key);
            if (v != null) {
                responseMap.put(key, v);
            }
        }
        return responseMap;
    }

    public boolean verifyPayResponse(Map<String,Object> map) {
        String mchId = (String) map.get("mchId");
        String payOrderId = (String) map.get("payOrderId");
        String mchOrderNo = (String) map.get("mchOrderNo");
        String amount = (String) map.get("amount");
        String sign = (String) map.get("sign");

        if (StringUtils.isEmpty(mchId)) {
            _log.warn("Params error. mchId={}", mchId);
            return false;
        }
        if (StringUtils.isEmpty(payOrderId)) {
            _log.warn("Params error. payOrderId={}", payOrderId);
            return false;
        }
        if (StringUtils.isEmpty(amount) || !NumberUtils.isNumber(amount)) {
            _log.warn("Params error. amount={}", amount);
            return false;
        }
        if (StringUtils.isEmpty(sign)) {
            _log.warn("Params error. sign={}", sign);
            return false;
        }

        // 验证签名
        if (!verifySign(map)) {
            _log.warn("verify params sign failed. payOrderId={}", payOrderId);
            return false;
        }

        // 根据payOrderId查询业务订单,验证订单是否存在
        GoodsOrder goodsOrder = goodsOrderService.getGoodsOrder(mchOrderNo);
        if(goodsOrder == null) {
            _log.warn("业务订单不存在,payOrderId={},mchOrderNo={}", payOrderId, mchOrderNo);
            return false;
        }
        // 核对金额
        if(goodsOrder.getAmount() != Long.parseLong(amount)) {
            _log.warn("支付金额不一致,dbPayPrice={},payPrice={}", goodsOrder.getAmount(), amount);
            return false;
        }
        return true;
    }

    public boolean verifySign(Map<String, Object> map) {
        String mchId = (String) map.get("mchId");
        if(!Constant.mchId.equals(mchId)) return false;
        String localSign = PayDigestUtil.getSign(map, Constant.resKey, "sign");
        String sign = (String) map.get("sign");
        return localSign.equalsIgnoreCase(sign);
    }

}