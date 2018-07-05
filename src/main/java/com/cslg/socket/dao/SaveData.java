package com.cslg.socket.dao;

import com.cslg.socket.common.ConnectionHolder;
import com.cslg.socket.model.Inverter;
import com.cslg.socket.model.Load;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class SaveData {

    private static final Logger logger = LoggerFactory.getLogger(SaveData.class);

    private static AtomicInteger sum = new AtomicInteger(1);

    public static void main(String[] args) {
        System.out.println(new java.util.Date(System.currentTimeMillis()));
        System.out.println(new Timestamp(System.currentTimeMillis()));
    }

    private static void closePreparedStatement(PreparedStatement preparedStatement) {
        try {
            if(preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("存储数据出现异常", e);
        }
    }

    @SuppressWarnings("SqlDialectInspection")
    public static void saveInverter(Inverter inverter) {

        String sql = "INSERT INTO tb_inverter (local, inverter_name, times, daily_output," +
                "total_output, a_phase_current, a_phase_voltage, b_phase_current," +
                "b_phase_voltage, c_phase_current, c_phase_voltage, total_active_power," +
                "tans_temp_1, tans_temp_2) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = null;
        try {
            if(sum.get() == 1) {
                inverter.setInverterName("inverter1");
                inverter.setLocal("长沙理工大学云塘校区");
                sum.set(2);
            } else if(sum.get() == 2) {
                inverter.setInverterName("inverter1");
                inverter.setLocal("株洲市");
                sum.set(3);
            } else if(sum.get() == 3) {
                inverter.setInverterName("inverter1");
                inverter.setLocal("车溪村");
                sum.set(1);
            }
            inverter.setInverterName("inverter1");
            //inverter.setLocal("长沙理工大学云塘校区工一");
            inverter.setTimes(new Timestamp(System.currentTimeMillis()));
            Connection connection = ConnectionHolder.getCurrentConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, inverter.getLocal());
            preparedStatement.setString(2, inverter.getInverterName());
            preparedStatement.setTimestamp(3, inverter.getTimes());
            preparedStatement.setDouble(4, inverter.getDailyOutput());
            preparedStatement.setDouble(5, inverter.getTotalOutput());
            preparedStatement.setDouble(6, inverter.getaPhaseCurrent());
            preparedStatement.setDouble(7, inverter.getaPhaseVoltage());
            preparedStatement.setDouble(8, inverter.getbPhaseCurrent());
            preparedStatement.setDouble(9, inverter.getbPhaseVoltage());
            preparedStatement.setDouble(10, inverter.getcPhaseCurrent());
            preparedStatement.setDouble(11, inverter.getcPhaseVoltage());
            preparedStatement.setDouble(12, inverter.getTotalActivePower());
            preparedStatement.setDouble(13, inverter.getTansTemp1());
            preparedStatement.setDouble(14, inverter.getTansTemp2());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(Thread.currentThread().getName() + "存储出现异常", e);
        } finally {
            closePreparedStatement(preparedStatement);
        }
    }

    @SuppressWarnings("SqlDialectInspection")
    public static void saveLoad(Load load) {
        String sql = "INSERT INTO tb_load (load_name, times, local, current, voltage," +
                " apparent_power, active_power) VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = null;
        Connection connection = ConnectionHolder.getCurrentConnection();
        load.setLoadName("load1");
        load.setTimes(new Timestamp(System.currentTimeMillis()));

        String[] s = {"工科一号楼", "工科二号楼", "综合教学楼"};
        int i = (int) (Math.random() * s.length);

        load.setLocal("长沙理工大学" + s[i]);
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, load.getLoadName());
            preparedStatement.setTimestamp(2, load.getTimes());
            preparedStatement.setString(3, load.getLocal());
            preparedStatement.setDouble(4, load.getCurrent());
            preparedStatement.setDouble(5, load.getVoltage());
            preparedStatement.setDouble(6, load.getApparentPower());
            preparedStatement.setDouble(7, load.getActivePower());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(Thread.currentThread().getName() + "存储出现异常", e);
        } finally {
            closePreparedStatement(preparedStatement);
        }
    }
}
