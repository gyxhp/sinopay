package com.sinosoft.pay.dal.dao.mapper;

import com.sinosoft.pay.dal.dao.model.PayChannel;
import com.sinosoft.pay.dal.dao.model.PayChannelExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PayChannelMapper {
    int countByExample(PayChannelExample example);

    int deleteByExample(PayChannelExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PayChannel record);

    int insertSelective(PayChannel record);

    List<PayChannel> selectByExample(PayChannelExample example);

    PayChannel selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") PayChannel record, @Param("example") PayChannelExample example);

    int updateByExample(@Param("record") PayChannel record, @Param("example") PayChannelExample example);

    int updateByPrimaryKeySelective(PayChannel record);

    int updateByPrimaryKey(PayChannel record);
}