package com.cslg.socket.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author aekc
 */
public class PropertyUtil {

    private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);

    private static Properties propertiesName;

    private static Properties propertiesOrder;

    private static Map<String, String> propertyOrderMap;

    private static Map<String, String> propertyNameMap;

    static {
        loadProps();
        propertyOrderMap = new TreeMap<>();
        propertyNameMap = new HashMap<>();
    }

    private static synchronized void loadProps() {
        propertiesOrder = new Properties();
        propertiesName = new Properties();
        InputStream in = null;
        try {
            //通过类加载器进行获取properties文件流
            in = PropertyUtil.class.getClassLoader().getResourceAsStream("order.properties");
            //properties.load(in);
            propertiesOrder.load(new InputStreamReader(in, "gbk"));

            //通过类加载器进行获取properties文件流
            in = PropertyUtil.class.getClassLoader().getResourceAsStream("name.properties");
            //properties.load(in);
            propertiesName.load(new InputStreamReader(in, "gbk"));
        } catch (FileNotFoundException e) {
            logger.error("properties文件没有找到");
        } catch (IOException e) {
            logger.error("加载文件出现错误");
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
            } catch (IOException e) {
                logger.error("properties文件流关闭出现异常");
            }
        }
    }

    public static Map<String, String> getPropertyNameMap() {
        if(propertiesName == null || propertiesOrder == null) {
            loadProps();
        }
        Enumeration en = propertiesName.keys();
        while(en.hasMoreElements()) {
            String key = (String) en.nextElement();
            propertyNameMap.put(key, propertiesName.getProperty(key));
        }
        return propertyNameMap;
    }

    public static Map<String, String> getPropertyOrderMap() {
        if(propertiesName == null || propertiesOrder == null) {
            loadProps();
        }
        Enumeration en = propertiesOrder.keys();
        while(en.hasMoreElements()) {
            String key = (String) en.nextElement();
            propertyOrderMap.put(key, propertiesOrder.getProperty(key));
        }
        return propertyOrderMap;
    }

    public static String getOrderProperty(String key) {
        if(propertiesName == null || propertiesOrder == null) {
            loadProps();
        }
        return propertiesOrder.getProperty(key);
    }

    public static String getOrderProperty(String key, String defaultValue) {
        if(propertiesName == null || propertiesOrder == null) {
            loadProps();
        }
        return propertiesOrder.getProperty(key, defaultValue);
    }
}
