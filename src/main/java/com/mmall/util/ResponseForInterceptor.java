package com.mmall.util;

import com.alibaba.fastjson.JSON;
import com.mmall.common.ServerResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by guotao on 2017/7/24.
 * com.mmall.util
 * qrprinter
 */
public class ResponseForInterceptor {

    private int status;
    private String msg;

    private ResponseForInterceptor() {
    }

    private ResponseForInterceptor(String msg) {
        ServerResponse serverResponse = ServerResponse.createByErrorMessage(msg);
        this.status = serverResponse.getStatus();
        this.msg = serverResponse.getMsg();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static void writeMessageToHttpResponse(HttpServletResponse httpServletResponse, String msg) {
        try {
            PrintWriter writer = null;
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            writer = httpServletResponse.getWriter();

            writer.print(JSON.toJSONString(new ResponseForInterceptor(msg)));
        } catch (IOException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
