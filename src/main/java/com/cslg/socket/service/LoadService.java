package com.cslg.socket.service;

import com.cslg.socket.dao.SaveData;
import com.cslg.socket.listener.SocketListener;
import com.cslg.socket.model.Load;
import com.cslg.socket.utils.CodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;

/**
 * @author twilight
 */
public class LoadService extends AbstractService<Load> {

    private Logger logger = LoggerFactory.getLogger(LoadService.class);

    private static final String ORDER = "01030000002845D4";

    public LoadService(String sign) {
        setSign(sign);
    }

    public LoadService() {}

    private void analysis(String data) {
        getObject().setCurrent(((double) Integer.parseInt(data.substring(0, 4), 16)) / 100);
        getObject().setVoltage(((double) Integer.parseInt(data.substring(4, 8), 16)) / 100);
        getObject().setApparentPower(((double) Integer.parseInt(data.substring(8, 12), 16)));
        getObject().setActivePower(((double) Integer.parseInt(data.substring(12, 16), 16)));
    }

    @Override
    public boolean readData(String ... str) {
        byte[] bytes = new byte[1];
        StringBuilder stringBuilder = new StringBuilder();
        int k = 0;
        int signSum = 0;
        try {
            while (k != 27) {
                // 如果连续6次都是"EE"，说明客户端休息了。
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
                signSum = 0;
                stringBuilder.append(data);
                k++;
            }
        }catch (SocketTimeoutException e) {
            e.printStackTrace();
            logger.error("read()超时线程即将退出");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("从流中读取数据异常", e);
            return true;
        }
        if (stringBuilder.length() > 0) {
            System.out.println(new Date() + stringBuilder.toString());
            String data = stringBuilder.toString().substring(38);
            analysis(data);
            logger.info("负荷: {}", data);
        }
        return false;
    }

    @Override
    public boolean writeData() {
        try {
            setObject(new Load());
            getOutputStream().write(CodeUtil.hex2byte(ORDER));
            if (readData()) {
                //如果为true,，说明该线程任务已经完成，该回收回线程池。
                return true;
            }
            logger.info("-------------------------------------工作线程有{}个", SocketListener.sum.get());
            handleMessage();
            // 先把缓冲区清空
            getInputStream().read(new byte[512]);
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
        SaveData.saveLoad(getObject());
    }
}
