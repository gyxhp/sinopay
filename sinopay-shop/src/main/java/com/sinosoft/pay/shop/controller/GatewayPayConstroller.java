package com.sinosoft.pay.shop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sinosoft.pay.common.constant.PayConstant;
import com.sinosoft.pay.common.util.*;
import com.sinosoft.pay.shop.dao.model.GoodsOrder;
import com.sinosoft.pay.shop.dao.model.MchInfo;
import com.sinosoft.pay.shop.dao.model.PayOrder;
import com.sinosoft.pay.shop.service.GoodsOrderService;
import com.sinosoft.pay.shop.service.MchInfoService;
import com.sinosoft.pay.shop.service.PayOrderQueryService;
import com.sinosoft.pay.shop.util.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yang@dehong
 * 2018-07-19 23:06
 */

@Controller
@RequestMapping("/wechatpay")
public class GatewayPayConstroller {

    private AtomicLong seq = new AtomicLong(0L);


    @Autowired
    private MchInfoService mchInfoService;
    @Autowired
    private GoodsOrderService goodsOrderService;
    @Autowired
    private PayOrderQueryService payOrderQueryService;
    private final static MyLog _log = MyLog.getLog(GatewayPayConstroller.class);

    //url=https://pay.shanghailife.com.cn/sinopay/wechatpay/rollback
    @ResponseBody
    @RequestMapping(value = "/rollback")
    public String paymoney(HttpServletRequest request) {
        _log.info("开始处理回调了url={}", request.getRequestURL());
        return "success";
    }

    @RequestMapping(value = "/gatewaypay")
    public String checkMchId(ModelMap model,HttpServletRequest request, @RequestParam("params") String params) {
        _log.info("商户传过来的参数：{}",params);
//        params = "{\"mchId\":\"10000000\",\n" +
//                "\"mchOrderNo\":\"G20180807230642000000\",\n" +
//                "\"amount\":1,\n" +
//                "\"subject\":\"sinopay_支付测试\",\n" +
//                "\"sign\":\"jieshuntest123!@#\"}";
        JSONObject paramObj = JSON.parseObject(params);
        String mchId = paramObj.getString("mchId");
        Map<String, String> map = new HashMap<>();
        _log.info("mchId=" + mchId );
        if(StringUtils.isBlank(mchId)) {
            _log.info("参数无效");
            model.put("message", "参数无效");
            return "500";
        }
        MchInfo mchInfo = new MchInfo();
        mchInfo = mchInfoService.selectMchInfo(mchId);
        if (mchInfo == null) {
            _log.info("无效的商户ID");
            model.put("message", "无效的商户ID");
            return "500";
        }
        Byte state = mchInfo.getState();
        if (1 != state) {
            _log.info("商户已停用");
            model.put("message", "商户已停用");
            return "500";
        }
        Map paramsMap = JSON.parseObject(params);
        //验签
//        String sign = paramsMap.get("sign").toString();
//        String checkSign = PayDigestUtil.getSign(paramsMap, mchInfo.getReqKey(), "sign");
//        _log.info("checkSign = {}",checkSign);
//        if(!checkSign.equals(sign)){
//            _log.info("验签失败,商户sign={}，支付平台checkSign={}",sign,checkSign);
//            model.put("message", "验签失败!");
//            return "500";
//        }
        try{
            model.put("params", URLEncoder.encode(params, "UTF-8"));
        }catch (Exception e){
            _log.info("系统异常",e);
            model.put("message", "系统异常");
            return "500";
        }

        model.put("mchOrderNo",  paramObj.getString("mchOrderNo"));
        model.put("subject",  paramObj.getString("subject"));
        model.put("amount",  paramObj.getIntValue("amount")/100.00);
        model.put("mchId", mchId);
        //保存支付订单记录
        GoodsOrder goodsOrder = createGoodsOrder("SHLIFEPAY001",  paramObj.getLongValue("amount"),paramObj.getString("subject"),mchId,paramObj.getString("mchOrderNo"));
        //支付系统订单号
        model.put("goodsOrderNo",  goodsOrder.getGoodsOrderId());
        //跳转到支付页面
        return "paymentPage";
    }

    @RequestMapping(value = "/paymoney")
    public String paymoney(ModelMap model, HttpServletRequest request) {
        try {
            String logPrefix = "【支付系统】";
            String client = "";
            String view = "";
            String amount = "";

            String param = request.getQueryString();
            if (param == null || "".equals(param)) {
                model.put("message", "参数无效");
                return "500";
            }
            Map<String, String> map = getparamMap(param);//解析参数
            if (map == null) {
                model.put("message", "参数无效");
                return "500";
            }

            String goodsOrderNo = map.get("goodsOrderNo");
//            String paramsFromMch = "{\"mchId\":\"10000000\",\n" +
//                    "\"notifyUrl\":\"http://www.baidu.com\",\n" +
//                    "\"amount\":1,\n" +
//                    "\"subject\":\"sinopay_支付测试\",\n" +
//                    "\"body\":\"sinopay_支付测试\",\n" +
//                    "\"succReturnUrl\":\"http://www.taobao.com\",\n" +
//                    "\"failReturnUrl\":\"http://www.jd.com\"}";
            String paramsFromMch = map.get("params");//商户传过来的完整参数
            String channelId = map.get("paytype");
            if ("ALIPAY_WAP".equals(channelId)) {
                //支付宝支付
                client = "alipay";
                view = "qrPay";
            } else if ("WX_JSAPI".equals(channelId)) {
                //公众号支付
                client = "wx";
                view = "weixinpay/qrPay";
            } else if ("WX_MWEB".equals(channelId)) {
                //微信H5支付
                client = "wx";
                view = "weixinpay/wxPay";
            }  else if ("YL_UNION".equals(channelId)) {
                //银联支付
            }
            float payamount = Float.valueOf(map.get("payamount"));//元转分
            //查询订单
            GoodsOrder goodsOrder = goodsOrderService.getGoodsOrder(goodsOrderNo);
            if(goodsOrder==null){
                model.put("message", "支付失败,订单查询失败");
                return "500";
            }
            Map<String, String> orderMap = null;
            Map params = new HashMap<>();
            params.put("channelId", channelId);
            params.put("clientIp", getLocalIp(request));
            params.put("paramsFromMch", paramsFromMch);
            params.put("payamount", payamount);
            if ("alipay".equals(client)) {
                _log.info("{}{}下单", logPrefix, "支付宝");

                // 下单
                orderMap = createPayOrder(goodsOrder, params);
            }else if("wx".equals(client)){
                _log.info("{}{}下单", logPrefix, "微信");
                // 判断是否拿到openid，如果没有则去获取
                String openId = request.getParameter("openId");
                if (StringUtils.isNotBlank(openId) || "WX_MWEB".equals(channelId)) {
                    _log.info("{}openId：{}", logPrefix, openId);
                    params.put("openId", openId);
                    // 下单
                    orderMap = createPayOrder(goodsOrder, params);
                }else {
                    String redirectUrl = Constant.PAY_URL + "?payamount=" + map.get("payamount") +"&paytype="+channelId+"&goodsOrderNo="+goodsOrderNo+"&params"+paramsFromMch;
                    String url = Constant.GetOpenIdURL;// + "?redirectUrl=" + redirectUrl;
                    _log.info("跳转URL={}", url);
                    return "redirect:" + url;
                }
            }
            model.put("goodsOrder", goodsOrder);
            model.put("amount", AmountUtil.convertCent2Dollar(goodsOrder.getAmount()+""));
            if(orderMap != null && "SUCCESS".equals(orderMap.get("retCode")) && "SUCCESS".equalsIgnoreCase(orderMap.get("resCode").toString())) {
                model.put("orderMap", orderMap);
                String payOrderId = orderMap.get("payOrderId");
                GoodsOrder go = new GoodsOrder();
                go.setGoodsOrderId(goodsOrder.getGoodsOrderId());
                go.setPayOrderId(payOrderId);
                go.setChannelId(channelId);
                int ret = goodsOrderService.update(go);
                _log.info("修改商品订单,返回信息:{}", ret);
            }else{
                model.put("message", "支付处理失败，原因："+orderMap == null?"无":orderMap.get("retMsg"));
                return "500";
            }
            model.put("client", client);
            Map paramsFromMchMap = JSON.parseObject(paramsFromMch);
            model.put("succReturnUrl", paramsFromMchMap.get("succReturnUrl"));
            model.put("failReturnUrl", paramsFromMchMap.get("failReturnUrl"));
            if("WX_MWEB".equals(channelId)){
                _log.info("redirect：{}", orderMap.get("payUrl"));
                String payOrderId = orderMap.get("payOrderId");
                String succReturnUrl = paramsFromMchMap.get("succReturnUrl")+"?orderno="+paramsFromMchMap.get("mchOrderNo");
                String failReturnUrl = paramsFromMchMap.get("failReturnUrl")+"?orderno="+paramsFromMchMap.get("mchOrderNo");
                JSONObject paramMap = new JSONObject();
                paramMap.put("payOrderId", orderMap.get("payOrderId"));
                paramMap.put("succReturnUrl", succReturnUrl);
                paramMap.put("failReturnUrl", failReturnUrl);
                String reqData = "params=" + MyBase64.encode(paramMap.toJSONString().getBytes());
                _log.info("请求数据{}",reqData);
                String redirectUrl = orderMap.get("payUrl") + "&redirect_url="+Constant.QUR_URL+"?" + reqData;
                _log.info("redirect：{}", redirectUrl);
                //return "redirect:" + redirectUrl;
                model.put("redirectUrl", redirectUrl);
            }
            return view;
        } catch (Exception e) {
            _log.error(e, "系统繁忙");
            model.put("message", "系统繁忙");
            return "500";
        }
    }

    //保存支付订单记录
    GoodsOrder createGoodsOrder(String goodsId, Long amount,String goodsName,String mchId,String mchOrderNo) {
        // 先插入订单数据
        String goodsOrderId = String.format("%s%s%06d", "G", DateUtil.getSeqString(), (int) seq.getAndIncrement() % 1000000);
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setGoodsOrderId(goodsOrderId);
        goodsOrder.setGoodsId(goodsId);
        goodsOrder.setGoodsName(goodsName);
        goodsOrder.setAmount(amount);
        goodsOrder.setUserId(mchId);
        goodsOrder.setStatus(Constant.GOODS_ORDER_STATUS_INIT);
        goodsOrder.setMchOrderNo(mchOrderNo);
        int result = goodsOrderService.addGoodsOrder(goodsOrder);
        _log.info("插入商品订单,返回:{}", result);
        return goodsOrder;
    }

    private Map<String, String> getparamMap(String param) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
//        int a = param.indexOf("&sign=");//获取验签的数据
//        String sign = param.substring(a + 6);
//        param = param.substring(0, a);
//        String par = RpcSignUtils.sha1(param);
//        if (!Objects.equals(par, sign)) {//校验数据
//            return null;
//        }
        String[] strs = param.split("&");
        for (String str : strs) {
            String[] is = str.split("=");
            map.put(is[0], is[1]);
        }
        return map;
    }

    private Map createPayOrder(GoodsOrder goodsOrder, Map<String, String> map) {
        JSONObject paramMap = new JSONObject();
        String channelId = map.get("channelId");
        String paramsFromMch = map.get("paramsFromMch");
        //编码转换，防止乱码
        try{
            paramsFromMch = new String(paramsFromMch.getBytes("UTF-8"),"UTF-8");
            _log.info("字符串转码UTF-8：{}",paramsFromMch);
        }catch (Exception e){
            _log.info("系统繁忙",e);
        }
        Map paramsFromMchMap = JSON.parseObject(paramsFromMch);
        String mchId = paramsFromMchMap.get("mchId").toString();
        MchInfo mchInfo = new MchInfo();
        mchInfo = mchInfoService.selectMchInfo(mchId);
        if (mchInfo == null) {
            _log.info("无效的商户ID");
            return null;
        }
        String reqKey=mchInfo.getReqKey();
        String resKey=mchInfo.getResKey();

        String notifyUrl = paramsFromMchMap.get("notifyUrl").toString();

        paramMap.put("mchId", mchId);                               // 商户ID
        paramMap.put("mchOrderNo", System.currentTimeMillis());//paramsFromMchMap.get("mchOrderNo"));     // 商户订单号
        // 支付渠道ID, WX_NATIVE(微信扫码),WX_JSAPI(微信公众号或微信小程序),WX_APP(微信APP),WX_MWEB(微信H5),ALIPAY_WAP(支付宝手机支付),ALIPAY_PC(支付宝网站支付),ALIPAY_MOBILE(支付宝移动支付)
        paramMap.put("channelId", channelId);

        paramMap.put("amount", paramsFromMchMap.get("amount"));                                  // 支付金额,单位分
        paramMap.put("currency", "cny");                            // 币种, cny-人民币
        paramMap.put("clientIp", map.get("clientIp"));                 // 用户地址,微信H5支付时要真实的
        paramMap.put("device", "WEB");                              // 设备
        paramMap.put("subject", paramsFromMchMap.get("subject"));
        paramMap.put("body", paramsFromMchMap.get("body"));
        paramMap.put("notifyUrl", notifyUrl);                       // 回调URL
        paramMap.put("aliReturnUrl", paramsFromMchMap.get("succReturnUrl"));   // 支付宝支付成功后跳转的url
        paramMap.put("quit_url", paramsFromMchMap.get("failReturnUrl"));   // 支付宝支付成功后跳转的url
        paramMap.put("param1", "");                                 // 扩展参数1
        paramMap.put("param2", "");                                 // 扩展参数2
        paramMap.put("extra", "{\n" +
                "  \"productId\": \"\",\n" +
                "  \"openId\": \""+map.get("openId")+"\",\n" +
                "  \"sceneInfo\": {\n" +
                "    \"h5_info\": {\n" +
                "      \"type\": \"Wap\",\n" +
                "      \"wap_url\": \"\",\n" +
                "      \"wap_name\": \"\"\n" +
                "    }\n" +
                "  }\n" +
                " ,\"discountable_amount\":\"0.00\"," + //面对面支付扫码参数：可打折金额 可打折金额+不可打折金额=总金额
                "  \"undiscountable_amount\":\"0.00\"," + //面对面支付扫码参数：不可打折金额
                "  \"aliReturnUrl\": \"" +  paramsFromMchMap.get("succReturnUrl") +"\",\n" +
                "  \"quit_url\":\"" + paramsFromMchMap.get("failReturnUrl") + "\"\n" +
                "}");  // 附加参数

        //{"h5_info": {"type":"Wap","wap_url": "https://pay.qq.com","wap_name": "腾讯充值"}}
        String reqSign = PayDigestUtil.getSign(paramMap, reqKey);
        paramMap.put("sign", reqSign);                              // 签名
        String reqData = "params=" + paramMap.toJSONString();
        _log.info("请求支付中心下单接口,请求数据:" + reqData);
        String url = Constant.baseUrl + "/pay/create_order?";
        String result = SinoPayUtil.call4Post(url + reqData);
        _log.info("请求支付中心下单接口,响应数据:" + result);
        Map retMap = JSON.parseObject(result);
        if ("SUCCESS".equals(retMap.get("retCode")) && "SUCCESS".equalsIgnoreCase(retMap.get("resCode").toString())) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retMap, resKey, "sign", "payParams");
            String retSign = (String) retMap.get("sign");
            if (checkSign.equals(retSign)) {
                _log.info("=========支付中心下单验签成功=========");
            } else {
                _log.error("=========支付中心下单验签失败=========");
                return null;
            }
        }
        return retMap;
    }

    /**
     * 从Request对象中获得客户端IP，处理了HTTP代理服务器和Nginx的反向代理截取了ip
     * @param request
     * @return ip
     */
    public static String getLocalIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String forwarded = request.getHeader("X-Forwarded-For");
        String realIp = request.getHeader("X-Real-IP");

        String ip = null;
        if (realIp == null) {
            if (forwarded == null) {
                ip = remoteAddr;
            } else {
                ip = remoteAddr + "/" + forwarded.split(",")[0];
            }
        } else {
            if (realIp.equals(forwarded)) {
                ip = realIp;
            } else {
                if(forwarded != null){
                    forwarded = forwarded.split(",")[0];
                }
                ip = realIp + "/" + forwarded;
            }
        }
        return ip;
    }

    @RequestMapping(value = "/payOrderQuery")
    public String orderQuery(ModelMap model, @RequestParam String params) {
        String logPrefix = "【订单查询】";
        try {
            if(StringUtils.isBlank(params)) {
                model.put("code", "0002"); // 参数错误
                model.put("message", "缺少参数");
            }
            JSONObject paramObj = JSON.parseObject(new String(MyBase64.decode(params)));
            _log.info("{}传入参数页面:{}",logPrefix,params);
            String payOrderId = paramObj.getString("payOrderId");
            String succReturnUrl= paramObj.getString("succReturnUrl");
            String failReturnUrl = paramObj.getString("failReturnUrl");
            if(StringUtils.isNotBlank(payOrderId)) {
                PayOrder payOrder = payOrderQueryService.selectPayOrder(payOrderId);
                if (payOrder != null && payOrder.getStatus()==PayConstant.PAY_STATUS_SUCCESS || payOrder.getStatus()==PayConstant.PAY_STATUS_COMPLETE){
                    _log.info("{}支付成功,返回页面:{}",logPrefix,succReturnUrl);
                    model.put("code", "0000");
                } else {
                    _log.info("{}支付失败,返回信息:{}",logPrefix,failReturnUrl);
                    model.put("code", "0001");
                }
            } else {
                model.put("code", "0002"); // 参数错误
                model.put("message", "缺少参数");
            }
            model.put("succReturnUrl", succReturnUrl);
            model.put("failReturnUrl", failReturnUrl);
            return "weixinpay/wxh5Pay";
        } catch (Exception e) {
            _log.error(e, "系统繁忙");
            model.put("code", "0002");
            model.put("message", "系统繁忙");
            return "500";
        }
    }
}
