package com.sinosoft.pay.shop.dao.mapper;


import com.sinosoft.pay.shop.dao.model.MchInfo;
import com.sinosoft.pay.shop.dao.model.MchInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MchInfoMapper {
    int countByExample(MchInfoExample example);

    int deleteByExample(MchInfoExample example);

    int deleteByPrimaryKey(String mchId);

    int insert(MchInfo record);

    int insertSelective(MchInfo record);

    List<MchInfo> selectByExample(MchInfoExample example);

    MchInfo selectByPrimaryKey(String mchId);

    int updateByExampleSelective(@Param("record") MchInfo record, @Param("example") MchInfoExample example);

    int updateByExample(@Param("record") MchInfo record, @Param("example") MchInfoExample example);

    int updateByPrimaryKeySelective(MchInfo record);

    int updateByPrimaryKey(MchInfo record);

    MchInfo getMchInfoMapper(MchInfo mchInfo);
}