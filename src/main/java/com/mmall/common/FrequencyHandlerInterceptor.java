package com.mmall.common;

import com.mmall.util.ResponseForInterceptor;
import com.mmall.util.UserIp;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Created by guotao on 2017/7/21.
 * com.mmall.common
 * qrprinter
 */
public class FrequencyHandlerInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(FrequencyHandlerInterceptor.class);
    private static final int MAX_BASE_STATION_SIZE = 100000;
    private static Map<String, FrequencyStruct> BASE_STATION = new HashMap<>(MAX_BASE_STATION_SIZE);
    private static final float SCALE = 0.75F;
    private static final int MAX_CLEANUP_COUNT = 3;
    private static final int CLEANUP_INTERVAL = 1000;
    private final Object syncRoot = new Object();
    private int cleanupCount = 0;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Frequency methodFrequency = ((HandlerMethod) handler).getMethodAnnotation(Frequency.class);
        Frequency classFrequency = ((HandlerMethod) handler).getBean().getClass().getAnnotation(Frequency.class);

//        System.out.println("methodFrequency is null : " + (methodFrequency==null));
//        System.out.println("classFrequency is null : " + (classFrequency==null));

        boolean going = true;
        if(classFrequency != null) {
            going = handleFrequency(request, response, classFrequency);
        }

        if(going && methodFrequency != null) {
            going = handleFrequency(request, response, methodFrequency);
        }

        if (!going) {
            ResponseForInterceptor.writeMessageToHttpResponse(response, "权限不足无法访问");
        }
        return going;
    }

    private boolean handleFrequency(HttpServletRequest request, HttpServletResponse response, Frequency frequency) {

        boolean going = true;
        if(frequency == null) {
            return true;
        }

        String name = frequency.name();
        int limit = frequency.limit();
        int time = frequency.time();

//        System.out.println("name = [" + name + "], limit = [" + limit + "], time = [" + time + "]");

        if(time == 0 || limit == 0) {
            return false;
        }

        long currentTimeMilles = System.currentTimeMillis() / 1000;

        String ip = UserIp.getUsrIPAddr(request);
//        System.out.println("ip = [" + ip + "]");
        if (StringUtils.isBlank(ip)) {
            return false;
        }

        String key = ip + "_" + name;
        FrequencyStruct frequencyStruct = BASE_STATION.get(key);

        if(frequencyStruct == null) {

            frequencyStruct = new FrequencyStruct();
            frequencyStruct.uniqueKey = name;
            frequencyStruct.start = frequencyStruct.end = currentTimeMilles;
            frequencyStruct.limit = limit;
            frequencyStruct.time = time;
            frequencyStruct.accessPoints.add(currentTimeMilles);

            synchronized (syncRoot) {
                BASE_STATION.put(key, frequencyStruct);
            }
            if(BASE_STATION.size() > MAX_BASE_STATION_SIZE * SCALE) {
                cleanup(currentTimeMilles);
            }
        } else {

            frequencyStruct.end = currentTimeMilles;
            frequencyStruct.accessPoints.add(currentTimeMilles);
        }

        //时间是否有效
        if(frequencyStruct.end - frequencyStruct.start >= time) {

            if(logger.isDebugEnabled()) {
                logger.debug("frequency struct be out of date, struct will be reset., struct: {}", frequencyStruct.toString());
            }
            frequencyStruct.reset(currentTimeMilles);
        } else {

            int count = frequencyStruct.accessPoints.size();
            if(count > limit) {
                if(logger.isDebugEnabled()) {
                    logger.debug("key: {} too frequency. count: {}, limit: {}.", key, count, limit);
                }
                going = false;
            }
        }
        return going;
    }

    private void cleanup(long currentTimeMilles) {

        synchronized (syncRoot) {

            Iterator<String> it = BASE_STATION.keySet().iterator();
            while(it.hasNext()) {

                String key = it.next();
                FrequencyStruct struct = BASE_STATION.get(key);
                if((currentTimeMilles - struct.end) > struct.time) {
                    it.remove();
                }
            }

            if((MAX_BASE_STATION_SIZE - BASE_STATION.size()) > CLEANUP_INTERVAL) {
                cleanupCount = 0;
            } else {
                cleanupCount++;
            }

            if(cleanupCount > MAX_CLEANUP_COUNT ) {
                randomCleanup(MAX_CLEANUP_COUNT);
            }
        }
    }

    /**
     * 随机淘汰count个key
     * @param count
     */
    private void randomCleanup(int count) {
        //防止调用错误
        if(BASE_STATION.size() < MAX_BASE_STATION_SIZE * SCALE) {
            return;
        }

        Iterator<String> it = BASE_STATION.keySet().iterator();
        Random random = new Random();
        int tempCount = 0;

        while(it.hasNext()) {
            if(random.nextBoolean()) {
                it.remove();
                tempCount++;
                if(tempCount >= count) {
                    break;
                }
            }
        }
    }
}
