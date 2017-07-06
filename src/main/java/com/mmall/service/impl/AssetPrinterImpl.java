package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.service.IAssetPrinter;

import com.mmall.util.PropertiesUtil;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.Native;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by Administrator on 2017/6/1.
 */
@Service("iAssetPrinter")
public class AssetPrinterImpl implements IAssetPrinter{

    public interface TscLibDll extends StdCallLibrary {
        TscLibDll INSTANCE = (TscLibDll) Native.loadLibrary("TSCLIB", TscLibDll.class);

        int about();

        int openport(String pirnterName);

        int closeport();

        int sendcommand(String printerCommand);

        int setup(String width, String height, String speed, String density, String sensor, String vertical, String offset);

        int downloadpcx(String filename, String image_name);

        int barcode(String x, String y, String type, String height, String readable, String rotation, String narrow, String wide, String code);

        int printerfont(String x, String y, String fonttype, String rotation, String xmul, String ymul, String text);

        int clearbuffer();

        int printlabel(String set, String copy);

        int formfeed();

        int nobackfeed();

        int windowsfont(int x, int y, int fontheight, int rotation, int fontstyle, int fontunderline, String szFaceName, String content);
    }

    @Override
    public ServerResponse<String> assetPrint(String assetId, String assetName) {
        try {
            System.setProperty("jna.encoding", "GBK");// 支持中文
            // TscLibDll.INSTANCE.about();
            TscLibDll.INSTANCE.openport("\\\\192.168.1.116\\TSC TTP-244 Plus");
            // TscLibDll.INSTANCE.downloadpcx("C:\\UL.PCX", "UL.PCX");
            // TscLibDll.INSTANCE.sendcommand("REM ***** This is a test by JAVA. *****");
            TscLibDll.INSTANCE.setup("60", "40", "5", "15", "0", "2", "0");

            TscLibDll.INSTANCE.sendcommand("SET TEAR ON");
            TscLibDll.INSTANCE.clearbuffer();

            int startXQRcode = 32;
            int startYQRcode = 36;
            String command = "QRCODE " + startXQRcode + "," + startYQRcode + ",L,11,A,0,M2,S3,\"" + assetId + "\"";// 打印二维码
            TscLibDll.INSTANCE.sendcommand(command);
            // TscLibDll.INSTANCE.sendcommand("PUTPCX 550,10,\"UL.PCX\"");

            // TscLibDll.INSTANCE.printerfont("100", "50", "TSS24.BF2", "0", "1", "1", "Technology");
            // TscLibDll.INSTANCE.barcode("70", "140", "128", "90", "0", "0", "2", "2", "A123456789");// 打印内容，参数是位置和字体

            int fontheight = 24;
            int startXFont = 280;
            int startYFont = startYQRcode;
            int spacingFont = fontheight * 2;
            TscLibDll.INSTANCE.windowsfont(startXFont, startYQRcode, fontheight, 0, 2, 1, "Arial", "资产编号：");
            TscLibDll.INSTANCE.windowsfont(startXFont, startYQRcode + spacingFont, fontheight, 0, 2, 0, "Arial", assetId);
            TscLibDll.INSTANCE.windowsfont(startXFont, startYQRcode + spacingFont * 2, fontheight, 0, 2, 1, "Arial", "资产名称：");
            TscLibDll.INSTANCE.windowsfont(startXFont, startYQRcode + spacingFont * 3, fontheight, 0, 2, 0, "Arial", assetName);
            // TscLibDll.INSTANCE.windowsfont(120, 240, 32, 0, 2, 0, "Arial", assetId);
            TscLibDll.INSTANCE.printlabel("1", "1");
            TscLibDll.INSTANCE.closeport();

            return ServerResponse.createBySuccessMessage("打印过程OK");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("打印出错");
        }


    }
}
