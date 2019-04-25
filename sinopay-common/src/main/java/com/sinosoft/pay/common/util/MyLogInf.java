package com.sinosoft.pay.common.util;

/**
 * @Description:
 * @author kevin
 * @date 2017-07-05
 * @version V1.0
 */
public abstract interface MyLogInf {

    public abstract void debug(String paramString, Object[] paramArrayOfObject);

    public abstract void info(String paramString, Object[] paramArrayOfObject);

    public abstract void warn(String paramString, Object[] paramArrayOfObject);

    public abstract void error(Throwable paramThrowable, String paramString, Object[] paramArrayOfObject);
}
