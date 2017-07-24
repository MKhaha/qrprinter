package com.mmall.controller;

import com.mmall.common.Frequency;
import com.mmall.common.ServerResponse;
import com.mmall.service.IAssetPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/5/19.
 */
@Controller
@RequestMapping("/")
public class AssetController {

    @Autowired
    private IAssetPrinter iAssetPrinter;


    @RequestMapping(value = "printQRcode.do")
    @ResponseBody
    @Frequency(name = "printQRcode", time = 3, limit = 1)
    public ServerResponse<String> printQRcode(String assetId, String assetName) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println("current time : " + simpleDateFormat.format(new Date()));
        return ServerResponse.createBySuccessMessage("adfasdfasdfasdf");
        //return iAssetPrinter.assetPrint(assetId, assetName);
    }

}
