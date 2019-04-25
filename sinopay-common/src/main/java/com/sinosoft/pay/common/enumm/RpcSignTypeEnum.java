package com.sinosoft.pay.common.enumm;

/**
 * @Description: RPC通讯层签名计算方法枚举类
 * @author kevin
 * @date 2017-07-05
 * @version V1.0
 */
public enum RpcSignTypeEnum {

    NOT_SIGN(0),// 明文
    SHA1_SIGN(1);// SHA-1签名

    private Integer code;

    private RpcSignTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode()
    {
        return this.code;
    }

    public static RpcSignTypeEnum getRpcSignTypeEnum(Integer code) {
        if (code == null) {
            return null;
        }

        RpcSignTypeEnum[] values =RpcSignTypeEnum.values();
        for (RpcSignTypeEnum e : values) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

}
