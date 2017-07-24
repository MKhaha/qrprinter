package com.mmall.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guotao on 2017/7/21.
 * com.mmall.common
 * qrprinter
 */
public class FrequencyStruct {
    String uniqueKey;
    long start;
    long end;
    // 限制两次访问的时间间隔
    int time;
    // 限制同一个ip下，时间间隔中可以访问的次数
    int limit;
    List<Long> accessPoints = new ArrayList<Long>();

    public void reset(long timeMillis) {

        start = end = timeMillis;
        accessPoints.clear();
        accessPoints.add(timeMillis);
    }

    @Override
    public String toString() {
        return "FrequencyStruct [uniqueKey=" + uniqueKey + ", start=" + start
                + ", end=" + end + ", time=" + time + ", limit=" + limit
                + ", accessPoints=" + accessPoints + "]";
    }
}
