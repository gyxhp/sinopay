package com.sinosoft.pay.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.sinosoft.pay.shop.dao.mapper.PayOrderMapper;
import com.sinosoft.pay.shop.dao.model.PayOrder;
import com.sinosoft.pay.shop.dao.model.PayOrderExample;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class PayOrderQueryService {

    @Autowired
    private PayOrderMapper payOrderMapper;


    public PayOrder selectPayOrder(String payOrderId) {
        return payOrderMapper.selectByPrimaryKey(payOrderId);
    }

    public List<PayOrder> selectPayOrderByMchIdAndMchOrderNo(String mchId, String mchOrderNo) {
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria criteria = example.createCriteria();
        criteria.andMchIdEqualTo(mchId);
        criteria.andMchOrderNoEqualTo(mchOrderNo);
        List<PayOrder> payOrderList = payOrderMapper.selectByExample(example);
        return CollectionUtils.isEmpty(payOrderList) ? null : payOrderList;
    }
}
