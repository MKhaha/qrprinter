package com.mmall.controller;

import com.mmall.common.ServerResponse;
import com.mmall.service.IAssetPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ServerResponse<String> printQRcode(String assetId, String assetName) {
        return iAssetPrinter.assetPrint(assetId, assetName);
    }

}
