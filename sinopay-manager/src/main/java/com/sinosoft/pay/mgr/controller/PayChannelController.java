package com.sinosoft.pay.mgr.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import com.sinosoft.pay.common.constant.PayConstant;
import com.sinosoft.pay.common.util.DateUtil;
import com.sinosoft.pay.common.util.MyLog;
import com.sinosoft.pay.dal.dao.model.PayChannel;
import com.sinosoft.pay.dal.dao.plugin.PageModel;
import com.sinosoft.pay.mgr.service.PayChannelService;

import java.util.List;

@Controller
@RequestMapping("/pay_channel")
public class PayChannelController {

    private final static MyLog _log = MyLog.getLog(PayChannelController.class);

    @Autowired
    private PayChannelService payChannelService;

    @RequestMapping("/list.html")
    public String listInput(ModelMap model) {
        return "pay_channel/list";
    }

    @RequestMapping("/edit.html")
    public String editInput(String id, ModelMap model) {
        PayChannel item = null;
        if(StringUtils.isNotBlank(id) && NumberUtils.isNumber(id)) {
            item = payChannelService.selectPayChannel(Integer.parseInt(id));
        }
        if(item == null) item = new PayChannel();
        model.put("item", item);
        return "pay_channel/edit";
    }

    @RequestMapping("/list")
    @ResponseBody
    public String list(@ModelAttribute PayChannel payChannel, Integer pageIndex, Integer pageSize) {
        PageModel pageModel = new PageModel();
        int count = payChannelService.count(payChannel);
        if(count <= 0) return JSON.toJSONString(pageModel);
        List<PayChannel> payChannelList = payChannelService.getPayChannelList((pageIndex-1)*pageSize, pageSize, payChannel);
        if(!CollectionUtils.isEmpty(payChannelList)) {
            JSONArray array = new JSONArray();
            for(PayChannel pc : payChannelList) {
                JSONObject object = (JSONObject) JSONObject.toJSON(pc);
                object.put("createTime", DateUtil.date2Str(pc.getCreateTime()));
                array.add(object);
            }
            pageModel.setList(array);
        }
        pageModel.setCount(count);
        pageModel.setMsg("ok");
        pageModel.setRel(true);
        return JSON.toJSONString(pageModel);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public String save(@RequestParam String params) {
//        params = "{\"mchId\":\"20001223\",\"param\":\"{\\\"mchId\\\":\\\"1481721182\\\", \\\"appId\\\":\\\"wx077cb62e341f8a5c\\\", \\\"key\\\":\\\"***\\\", \\\"certLocalPath\\\":\\\"wx/1481721182_cert.p12\\\", \\\"certPassword\\\":\\\"1481721182\\\", \\\"desc\\\":\\\"sinopay_shop-native(sinopay扫码支付)\\\"}\",\"channelName\":\"WX\",\"remark\":\"微信扫码支付\",\"channelId\":\"WX_NATIVE\",\"channelMchId\":\"1481721182\"}\n";
        JSONObject po = JSONObject.parseObject(params);
        String channelId = po.getString("channelId");
        String param = po.getString("param");
        // 对于配置支付宝参数时,前端将+号转为空格bug处理
        if(PayConstant.PAY_CHANNEL_ALIPAY_MOBILE.equals(channelId) ||
                PayConstant.PAY_CHANNEL_ALIPAY_PC.equals(channelId) ||
                PayConstant.PAY_CHANNEL_ALIPAY_WAP.equals(channelId) ||
                PayConstant.PAY_CHANNEL_ALIPAY_QR.equals(channelId)) {
            JSONObject paramObj = null;
            try{
                paramObj = JSON.parseObject(po.getString("param"));
            }catch (Exception e) {
                _log.info("param is not json");
            }
            if(paramObj != null) {
                paramObj.put("private_key", paramObj.getString("private_key").replaceAll(" ", "+"));
                paramObj.put("alipay_public_key", paramObj.getString("alipay_public_key").replaceAll(" ", "+"));
                param = paramObj.toJSONString();
            }
        }
        PayChannel payChannel = new PayChannel();
        Integer id = po.getInteger("id");
        payChannel.setChannelId(channelId);
        payChannel.setMchId(po.getString("mchId"));
        payChannel.setChannelName(po.getString("channelName"));
        payChannel.setChannelMchId(po.getString("channelMchId"));
        payChannel.setState((byte) ("on".equalsIgnoreCase(po.getString("state")) ? 1 : 0));
        payChannel.setParam(param);
        payChannel.setRemark(po.getString("remark"));
        int result;
        if(id == null) {
            // 添加
            result = payChannelService.addPayChannel(payChannel);
        }else {
            // 修改
            payChannel.setId(id);
            result = payChannelService.updatePayChannel(payChannel);
        }
        _log.info("保存渠道记录,返回:{}", result);
        return result+"";
    }

    @RequestMapping("/view.html")
    public String viewInput(String id, ModelMap model) {
        PayChannel item = null;
        if(StringUtils.isNotBlank(id) && NumberUtils.isNumber(id)) {
            item = payChannelService.selectPayChannel(Integer.parseInt(id));
        }
        if(item == null) item = new PayChannel();
        model.put("item", item);
        return "pay_channel/view";
    }

}