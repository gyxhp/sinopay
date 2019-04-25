
package com.sinosoft.pay.mgr.controller;


import com.sinosoft.pay.common.util.MyLog;
import com.sinosoft.pay.common.util.RpcSignUtils;
import com.sinosoft.pay.dal.dao.model.SysUser;
import com.sinosoft.pay.mgr.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


/**
 * @author yang@dehong
 * 2018-07-08 0:32
 */


@Controller
public class LoginController {

    private final static MyLog _log = MyLog.getLog(MchInfoController.class);


    @Autowired
    private SysUserService sysUserService;


    @RequestMapping(value = {"/"})
    public String index() {
        try {
            _log.info("主页");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/index";
    }

    @RequestMapping(value = "/logout")
    public String Loginout(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            _log.info("注销");
            HttpSession session = request.getSession(false);
            session.removeAttribute("user");
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "redirect:/";
    }

    @RequestMapping(value = "/login")
    public String CheckUser(Model model, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> map = new HashMap<String, String>();
        String msg = "";
        try {
            _log.info("验证客户");
            HttpSession session = request.getSession();
            String userName = request.getParameter("userName");
            String password = request.getParameter("password");
            _log.info("userName=" + userName + ":password=" + password);
            if (userName != null || password != null) {
                SysUser SysUser =sysUserService.checkUser(userName, password);
                if (SysUser!=null) {
                    session.setAttribute("user", userName);
                    session.setAttribute("name", SysUser.getRealName());
                    return "redirect:/";
                } else {
                    msg = "用户名或密码错误!";

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            msg = "系统异常!";
        }
        model.addAttribute("message", msg);
        return "/login";
    }


    public static void main(String[] args) {
        String a = RpcSignUtils.sha1("payamount=100&paytype=ALIPAY_WAP&mchId=10000000");
        System.out.println("a = " + a);
        System.out.println("02fa655579bfe3eb564937f635b5f7857202daa2".equals(a));



    }


}
