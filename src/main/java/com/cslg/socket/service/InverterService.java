package com.cslg.socket.service;

import com.cslg.socket.dao.SaveData;
import com.cslg.socket.listener.SocketListener;
import com.cslg.socket.model.Inverter;
import com.cslg.socket.utils.CodeUtil;
import com.cslg.socket.utils.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author twilight
 */
public class InverterService extends AbstractService<Inverter> {

    private static Map<String, String> orderMap;

    private static Map<String, String> nameMap;

    private String dailyOutput = null;

    private String totalOutput = null;

    private static Set<String> specialOrder  = new HashSet<>();

    private static final Logger logger = LoggerFactory.getLogger(InverterService.class);

    static {
        orderMap = PropertyUtil.getPropertyOrderMap();
        nameMap = PropertyUtil.getPropertyNameMap();
        specialOrder.add("当天发电量高2字节");
        specialOrder.add("当天发电量低2字节");
        specialOrder.add("总发电量高2字节");
        specialOrder.add("总发电量低2字节");
    }

    public InverterService(String sign) {
        setSign(sign);
    }

    public InverterService() {}

    private double analysis(String data) {
        int number = Integer.parseInt(data.substring(4, 6));
        String hex = data.substring(6, 6 + number * 2);
        return (double) Integer.parseInt(hex, 16);
    }

    private String analysisSpecialOrder(String data) {
        int number = Integer.parseInt(data.substring(4, 6));
        return data.substring(6, 6 + number * 2);
    }

    private void setDataByMethodName(String methodName, String data) {
        double value = analysis(data) / 10.0;
        methodName = "set" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        try {
            Class<?> c = Class.forName("com.cslg.socket.model.Inverter");
            Method method = c.getMethod(methodName, Double.class);
            method.invoke(getObject(), value);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(Thread.currentThread().getName(), "setDataByMethodName方法异常", e);
        }
    }

    private void dealHeightAndLow(String data, String name) {
        if(name.startsWith("当天发电量")) {
            if(dailyOutput == null) {
                dailyOutput = analysisSpecialOrder(data);
            } else {
                dailyOutput = analysisSpecialOrder(data) + dailyOutput;
                getObject().setDailyOutput((double) Integer.parseInt(dailyOutput, 16));
                dailyOutput = null;
            }
        } else if(name.startsWith("总发电量")) {
            if(totalOutput == null) {
                totalOutput = analysisSpecialOrder(data);
            } else {
                totalOutput = analysisSpecialOrder(data) + totalOutput;
                getObject().setTotalOutput((double) Integer.parseInt(totalOutput, 16));
                totalOutput = null;
            }
        }
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean readData(String ... str) {
        String name = str[0];
        byte[] bytes = new byte[1];
        StringBuilder stringBuilder = new StringBuilder();
        int k = 0;
        int signSum = 0;
        try {
            while (k != 7) {
                // 如果连续6次都是"FE"，说明客户端休息了。
                if (signSum >= 6) {
                    return true;
                }
                getInputStream().read(bytes);
                String data = CodeUtil.encode(bytes);
                if (SocketListener.clientSignMap.containsKey(data)) {
                    logger.info("心跳标志返回: {}", data);
                    signSum++;
                    continue;
                }
                stringBuilder.append(data);
                k++;
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            logger.error("read()超时线程即将退出");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("从流中读取数据异常", e);
            return true;
        }
        if (stringBuilder.length() > 0) {
            String data = stringBuilder.toString();
            if(data.contains("80008000")) {
                return true;
            }
            System.out.println(name + "------" + data);
            logger.info("{}: {}", name, data);
            String methodName = nameMap.get(name);
            if(!specialOrder.contains(name)) {
                setDataByMethodName(methodName, data);
            } else {
                dealHeightAndLow(data, name);
            }
        }
        return false;
    }

    @Override
    public boolean writeData() {
        try {
            setObject(new Inverter());
            for (Map.Entry<String, String> entry : orderMap.entrySet()) {
                getOutputStream().write(CodeUtil.hex2byte(entry.getValue()));
                if (readData(entry.getKey())) {
                    //如果为true,，说明该线程任务已经完成，该回收回线程池。
                    return true;
                }
            }
            logger.info("-------------------------------------工作线程有{}个", SocketListener.sum.get());
            handleMessage();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("指令写入流中出错", e);
            //让该工作线程回收
            return true;
        }
        return false;
    }

    @Override
    public void handleMessage() {
        //存入数据库
        SaveData.saveInverter(getObject());
    }
}
