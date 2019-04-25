package com.sinosoft.pay.mgr.service;

import com.sinosoft.pay.common.util.RpcSignUtils;
import com.sinosoft.pay.dal.dao.mapper.SysUserMapper;
import com.sinosoft.pay.dal.dao.model.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yang@dehong
 * 2018-07-09 0:02
 */

@Component
public class SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    public SysUser checkUser(String name, String password) {
        SysUser SysUser = new SysUser();
//        password = RpcSignUtils.sha1(password);
        SysUser.setUsername(name);
        SysUser.setPassword(password);
        SysUser = sysUserMapper.CheckUser(SysUser);
        return sysUserMapper.CheckUser(SysUser);
    }
}
