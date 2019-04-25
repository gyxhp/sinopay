package com.sinosoft.pay.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.sinosoft.pay.common.util.MyLog;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: Zuul过滤器
 * @author kevin
 * @date 2017-07-05
 * @version V1.0
 */
public class AccessFilter extends ZuulFilter  {

    private static final MyLog _log = MyLog.getLog(ZuulFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        _log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
        Object accessToken = request.getParameter("accessToken");
        /*if(accessToken == null) {
            log.warn("access token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            return null;
        }*/
        return null;
    }

}
