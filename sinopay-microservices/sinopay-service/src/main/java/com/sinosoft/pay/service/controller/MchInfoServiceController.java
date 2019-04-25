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
import com.sinosoft.pay.dal.dao.model.MchInfo;
import com.sinosoft.pay.service.service.MchInfoService;

/**
 * @Description: 商户信息接口
 * @author kevin
 * @date 2017-07-05
 * @version V1.0
 */
@RestController
public class MchInfoServiceController {

    private final MyLog _log = MyLog.getLog(MchInfoServiceController.class);

    @Autowired
    private MchInfoService mchInfoService;

    @RequestMapping(value = "/mch_info/select")
    public String selectMchInfo(@RequestParam String jsonParam) {
        // TODO 参数校验
        String param = new String(MyBase64.decode(jsonParam));
        JSONObject paramObj = JSON.parseObject(param);
        String mchId = paramObj.getString("mchId");
        MchInfo mchInfo = mchInfoService.selectMchInfo(mchId);
        JSONObject retObj = new JSONObject();
        retObj.put("code", "0000");
        if(StringUtils.isBlank(jsonParam)) {
            retObj.put("code", "0001"); // 参数错误
            retObj.put("msg", "缺少参数");
            return retObj.toJSONString();
        }
        if(mchInfo == null) {
            retObj.put("code", "0002");
            retObj.put("msg", "数据对象不存在");
            return retObj.toJSONString();
        }
        retObj.put("result", JSON.toJSON(mchInfo));
        _log.info("result:{}", retObj.toJSONString());
        return retObj.toJSONString();
    }



}
