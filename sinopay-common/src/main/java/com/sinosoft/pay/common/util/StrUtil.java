package com.sinosoft.pay.common.util;

/**
 * @Description:
 * @author kevin
 * @date 2017-07-05
 * @version V1.0
 */
public class StrUtil {

    public static String toString(Object obj) {
        return obj == null?"":obj.toString();
    }

    public static String toString(Object obj, String nullStr) {
        return obj == null?nullStr:obj.toString();
    }

}
