package com.cslg.socket.common;

import com.cslg.socket.dao.SaveData;
import com.cslg.socket.model.Inverter;
import com.cslg.socket.utils.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HandleData {

    private Inverter inverter;

    private OutputStream outputStream;

    private InputStream inputStream;

    private static Map<String, String> orderMap;

    private static Map<String, String> nameMap;

    private static Set<String> specialOrder  = new HashSet<>();

    private static final Logger logger = LoggerFactory.getLogger(HandleData.class);

    static {
        orderMap = PropertyUtil.getPropertyOrderMap();
        nameMap = PropertyUtil.getPropertyNameMap();
        specialOrder.add("当天发电量高2字节");
        specialOrder.add("当天发电量低2字节");
        specialOrder.add("总发电量高2字节");
        specialOrder.add("总发电量低2字节");
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * 字符串转成十六进制byte[] 类型数组
     */
    private byte[] hex2byte(String hex) {
        String digital = "0123456789ABCDEF";
        String hex1 = hex.replace(" ", "");
        char[] hex2char = hex1.toCharArray();
        byte[] bytes = new byte[hex1.length() / 2];
        byte temp;
        for(int p = 0; p < bytes.length; p++) {
            temp = (byte) (digital.indexOf(hex2char[2 * p]) * 16);
            temp += digital.indexOf(hex2char[2 * p + 1]);
            bytes[p] = (byte) (temp & 0xff);
        }
        return bytes;
    }

    /**
     *  将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    public String encode(byte[] bytes) {
        String hexString = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for(byte k : bytes) {
            sb.append(hexString.charAt((k & 0xf0) >> 4));
            sb.append(hexString.charAt(k & 0x0f));
        }
        return sb.toString();
    }

    private double analysis(String data) {
        int number = Integer.parseInt(data.substring(4, 6));
        String hex = data.substring(6, 6 + number * 2);
        return (double) Integer.parseInt(hex, 16);
    }

    private void setDataByMethodName(String methodName, String data) {
        double value = analysis(data) / 10.0;
        methodName = "set" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        try {
            Class<?> c = Class.forName("com.cslg.socket.model.Inverter");
            Method method = c.getMethod(methodName, Double.class);
            method.invoke(inverter, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealHeightAndLow(String data, String name) {
        double value = analysis(data);
        if(name.startsWith("当天发电量")) {
            if(inverter.getDailyOutput() == null) {
                inverter.setDailyOutput(value / 10.0);
            } else {
                double sum = inverter.getDailyOutput() + value;
                inverter.setDailyOutput(sum);
            }
        } else if(name.startsWith("总发电量")) {
            if(inverter.getTotalOutput() == null) {
                inverter.setTotalOutput(value / 10.0);
            } else {
                double sum = inverter.getTotalOutput() + value;
                inverter.setTotalOutput(sum);
            }
        }
    }

    private boolean readData(String name) {
        try {
            byte[] bytes = new byte[1];
            StringBuffer stringBuffer = new StringBuffer();
            int k = 0;
            int feSum = 0;
            while (k != 7) {
                // 如果连续3次都是"FE"，说明客户端休息了。
                if(feSum >= 3) {
                    return true;
                }
                inputStream.read(bytes);
                String data = encode(bytes);
                if ("FE".equals(data) && k == 0) {
                    logger.info("心跳返回: {}", data);
                    feSum++;
                    continue;
                }
                stringBuffer.append(data);
                k++;
            }
            if (stringBuffer.length() > 0) {
                String data = stringBuffer.toString();
                logger.info("{}: {}", name, data);
                String methodName = nameMap.get(name);
                if(!specialOrder.contains(name)) {
                    setDataByMethodName(methodName, data);
                } else {
                    dealHeightAndLow(data, name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("从流中读取数据异常", e);
        }
        return false;
    }

    public boolean writeData() {
        try {
            inverter = new Inverter();
            for(Map.Entry<String, String> entry : orderMap.entrySet()) {
                outputStream.write(hex2byte(entry.getValue()));
                if(readData(entry.getKey())) {
                    //如果为true,，说明该线程任务已经完成，该回收回线程池。
                    return true;
                }
            }
            logger.info("-------------------------------------");
            handleMessage();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("指令写入流中出错", e);
        }
        return false;
    }

    private void handleMessage() {
        //存入数据库
        SaveData.save(inverter);
    }
}
