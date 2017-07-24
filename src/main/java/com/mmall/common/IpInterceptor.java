package com.mmall.common;

import com.alibaba.fastjson.JSON;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.ResponseForInterceptor;
import com.mmall.util.UserIp;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by guotao on 2017/7/21.
 * com.mmall.common
 * qrprinter
 */
public class IpInterceptor implements HandlerInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(UserIp.class);

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        List<String> ipWhiteList = UserIp.getIpWhiteListFromPropertiesFile();
        if (ipWhiteList == null) {
            ResponseForInterceptor.writeMessageToHttpResponse(httpServletResponse, "服务器没有白名单");

            return false;
        }

//        System.out.println(ipWhiteList);

        String ipAddress = UserIp.getUsrIPAddr(httpServletRequest);
//        System.out.println("ipAddress = [" + ipAddress + "]");
        logger.error("ipAddress : " + ipAddress);
        if (StringUtils.isNotBlank(ipAddress) && ipWhiteList.contains(ipAddress)) {
            return true;
        } else {
            logger.info("ip address is : " + ipAddress);
            ResponseForInterceptor.writeMessageToHttpResponse(httpServletResponse, "权限不足无法访问");


            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
