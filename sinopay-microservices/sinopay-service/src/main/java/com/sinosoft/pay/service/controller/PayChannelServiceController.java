package com.sinosoft.pay.service.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sinosoft.pay.common.util.MyBase64;
import com.sinosoft.pay.common.util.MyLog;
import com.sinosoft.pay.dal.dao.model.PayChannel;
import com.sinosoft.pay.service.service.PayChannelService;

/**
 * @Description: 支付渠道接口
 * @author kevin
 * @date 2017-07-05
 * @version V1.0
 */
@RestController
public class PayChannelServiceController {

    private final MyLog _log = MyLog.getLog(PayChannelServiceController.class);

    @Autowired
    private PayChannelService payChannelService;

    @RequestMapping(value = "/pay_channel/select")
    public String selectPayChannel(@RequestParam String jsonParam) {
        // TODO 参数校验
        _log.info("selectPayChannel << {}", jsonParam);
        JSONObject retObj = new JSONObject();
        retObj.put("code", "0000");
        if(StringUtils.isBlank(jsonParam)) {
            retObj.put("code", "0001"); // 参数错误
            retObj.put("msg", "缺少参数");
            return retObj.toJSONString();
        }
        JSONObject paramObj = JSON.parseObject(new String(MyBase64.decode(jsonParam)));
        String channelId = paramObj.getString("channelId");
        String mchId = paramObj.getString("mchId");
        PayChannel payChannel = payChannelService.selectPayChannel(channelId, mchId);
        if(payChannel == null) {
            retObj.put("code", "0002");
            retObj.put("msg", "数据对象不存在");
            return retObj.toJSONString();
        }
        retObj.put("result", JSON.toJSON(payChannel));
        _log.info("selectPayChannel >> {}", retObj);
        return retObj.toJSONString();
    }


}
