package com.mmall.service;

import com.mmall.common.ServerResponse;

/**
 * Created by Administrator on 2017/6/1.
 */
public interface IAssetPrinter {
    ServerResponse<String> assetPrint(String assetId, String assetName);
}
