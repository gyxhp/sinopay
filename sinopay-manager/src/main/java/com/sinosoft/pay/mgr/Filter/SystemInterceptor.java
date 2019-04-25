
package com.sinosoft.pay.mgr.Filter;


import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * ClassName: SystemInterceptor
 * Description: 系统拦截器
 * Date: 2016/12/31 16:32
 *
 * @author SAM SHO
 * @version V1.0
 */

public class SystemInterceptor implements HandlerInterceptor {

    private static final Logger logger = Logger.getLogger(SystemInterceptor.class);


    /**
     * 前置拦截,主要在于权限拦截。true-会去访问Controller
     * Object-就是要访问的那个Controller
     * requestURI: /rabby/user/getUser
     * contextPath: /rabby
     * servletPath: /user/getUser
     *
     * @param request
     * @param response
     * @param object
     * @return
     * @throws Exception
     */

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {


        String servletPath = request.getServletPath();
        logger.info("++++ 访问地址 +++" + servletPath);

        HttpSession session = request.getSession();
        if (session != null && session.getAttribute("user") != null) {// 以登录用户
            //TODO 配置权限或校验权限
            return true;

        } else { // 非登录用户
            if (UrlHelper.isLoginRequest(servletPath)) { //去登录或登出，放行
                return true;
            } else {// 非登录，去访问其他
                if (UrlHelper.isAjaxRequest(request, servletPath)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "请先登录");
                    return false;//没登录，无法进行Ajax请求
                } else {
                    //强制去登录
                    String location = request.getContextPath() + UrlHelper.loginPath;

                    /* if ("GET".equalsIgnoreCase(request.getMethod())) {
                        location += "?url=" + URLEncoder.encode(servletPath, "utf-8");
                    }*/

                    //response.sendRedirect(location);
                   request.getRequestDispatcher("login").forward(request, response);
                    return false;
                }
            }
        }
    }


    /**
     * 执行时机：Controller执行完，视图解析器没有把视图解析成页面
     * ModelAndView：统一修改视图,如修改Model对象的信息
     *
     * @param request
     * @param response
     * @param object
     * @param modelAndView
     * @throws Exception
     */

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView modelAndView) throws Exception {

        logger.info("++++ postHandle +++");
    }


    /**
     * 执行时机：视图已经被解析完毕。Exception：监控后置拦截
     *
     * @param request
     * @param response
     * @param object
     * @param e
     * @throws Exception
     */

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception e) throws Exception {
        logger.info("++++ afterCompletion +++");
    }

    private static class UrlHelper {

        private static final String loginPath = "/login";
        private static final String logoutPath = "/logout";
        private static final String checkPath = "/mlogin";
        private static final String ajaxPath = "/ajax/";
       // private static final String indexPath = "/";

        private static boolean isAjaxRequest(HttpServletRequest request, String path) {
            return path.contains(ajaxPath) && "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
        }


        /**
         * @param path 访问的路径
         *             http://localhost:8080/rabby/login ==> /login
         * @return 是否是登录页面或者登出或者注册
         */

        private static boolean isLoginRequest(String path) {
            return loginPath.equals(path) ||logoutPath.equals(path)|| checkPath.equals(path)|| checkPath.equals(path);
        }
    }
}

