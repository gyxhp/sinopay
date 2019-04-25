package com.sinosoft.pay.service.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sinosoft.pay.common.constant.PayConstant;
import com.sinosoft.pay.common.util.MyBase64;
import com.sinosoft.pay.common.util.MyLog;
import com.sinosoft.pay.dal.dao.model.PayOrder;
import com.sinosoft.pay.service.service.PayOrderService;

import java.util.List;

/**
 * @Description: 支付订单接口
 * @author kevin
 * @date 2017-07-05
 * @version V1.0
 */
@RestController
public class PayOrderServiceController extends Notify4BasePay {

    private final MyLog _log = MyLog.getLog(PayOrderServiceController.class);

    @Autowired
    private PayOrderService payOrderService;

    @RequestMapping(value = "/pay/create")
    public String createPayOrder(@RequestParam String jsonParam) {
        _log.info("接收创建支付订单请求,jsonParam={}", jsonParam);
        JSONObject retObj = new JSONObject();
        retObj.put("code", "0000");
        if(StringUtils.isBlank(jsonParam)) {
            retObj.put("code", "0001");
            retObj.put("msg", "缺少参数");
            return retObj.toJSONString();
        }
        try {
            PayOrder payOrder = JSON.parseObject(new String(MyBase64.decode(jsonParam)), PayOrder.class);
            //查询该商户下该订单是否已经支付过，如果已经支付过了，防止重复支付
            List<PayOrder> payOrderList = payOrderService.selectPayOrderByMchIdAndMchOrderNo(payOrder.getMchId(), payOrder.getMchOrderNo());
            if(payOrderList!=null){
                for (PayOrder aorder : payOrderList ) {
                    if (aorder.getStatus()==PayConstant.PAY_STATUS_SUCCESS || aorder.getStatus()==PayConstant.PAY_STATUS_COMPLETE){
                        _log.info("该订单已支付成功，不可重复支付>> {}  订单状态 << {}",aorder.getPayOrderId(), aorder.getStatus());
                        retObj.put("code", "0002");
                        retObj.put("msg", "该订单已支付成功，不可重复支付");
                        return retObj.toJSONString();
                    }
                }
            }

            int result = payOrderService.createPayOrder(payOrder);
            retObj.put("result", result);
        }catch (Exception e) {
            retObj.put("code", "9999"); // 系统错误
            retObj.put("msg", "系统错误");
            _log.info("系统错误",e);
        }
        return retObj.toJSONString();
    }

    @RequestMapping(value = "/pay/query")
    public String queryPayOrder(@RequestParam String jsonParam) {
        _log.info("selectPayOrder << {}", jsonParam);
        JSONObject retObj = new JSONObject();
        retObj.put("code", "0000");
        if(StringUtils.isBlank(jsonParam)) {
            retObj.put("code", "0001"); // 参数错误
            retObj.put("msg", "缺少参数");
            return retObj.toJSONString();
        }
        JSONObject paramObj = JSON.parseObject(new String(MyBase64.decode(jsonParam)));
        String mchId = paramObj.getString("mchId");
        String payOrderId = paramObj.getString("payOrderId");
        String mchOrderNo = paramObj.getString("mchOrderNo");
        PayOrder payOrder = null;
        if(StringUtils.isNotBlank(payOrderId)) {
            payOrder = payOrderService.selectPayOrderByMchIdAndPayOrderId(mchId, payOrderId);
        }else {
            List<PayOrder> payOrderList = payOrderService.selectPayOrderByMchIdAndMchOrderNo(mchId, mchOrderNo);
            //对比找到最新的支付状态
            byte currStatus = 0;
            if(payOrderList!=null) {
                for (PayOrder aorder : payOrderList) {
                    if (aorder.getStatus() >= currStatus) {
                        currStatus = aorder.getStatus();
                        payOrder = aorder;
                        _log.info("订单号>> {}  订单状态 << {}", mchOrderNo, aorder.getStatus());
                    }
                }
            }
            _log.info("订单号>> {}  订单最新状态 << {}",mchOrderNo, currStatus);
        }
        if(payOrder == null) {
            retObj.put("code", "0002");
            retObj.put("msg", "支付订单不存在");
            return retObj.toJSONString();
        }

        //
        boolean executeNotify = paramObj.getBooleanValue("executeNotify");
        // 如果选择回调且支付状态为支付成功,则回调业务系统
        if(executeNotify && payOrder.getStatus() == PayConstant.PAY_STATUS_SUCCESS) {
            this.doNotify(payOrder);
        }
        retObj.put("result", JSON.toJSON(payOrder));
        _log.info("selectPayOrder >> {}", retObj);
        return retObj.toJSONString();
    }

}
