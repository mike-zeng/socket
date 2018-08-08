package com.cslg.socket.model;

import java.sql.Timestamp;

public class Inverter {

    private Integer id;

    private String local;

    private String inverterName;

    private Timestamp times;

    private Double dailyOutput;

    private Double totalOutput;

    private Double aPhaseCurrent;

    private Double aPhaseVoltage;

    private Double bPhaseCurrent;

    private Double bPhaseVoltage;

    private Double cPhaseCurrent;

    private Double cPhaseVoltage;

    private Double totalActivePower;

    private Double tansTemp1;

    private Double tansTemp2;

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Timestamp getTimes() {
        return times;
    }

    public void setTimes(Timestamp times) {
        this.times = times;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInverterName() {
        return inverterName;
    }

    public void setInverterName(String inverterName) {
        this.inverterName = inverterName;
    }

    public Double getDailyOutput() {
        return dailyOutput;
    }

    public void setDailyOutput(Double dailyOutput) {
        this.dailyOutput = dailyOutput;
    }

    public Double getTotalOutput() {
        return totalOutput;
    }

    public void setTotalOutput(Double totalOutput) {
        this.totalOutput = totalOutput;
    }

    public Double getaPhaseCurrent() {
        return aPhaseCurrent;
    }

    public void setAPhaseCurrent(Double aPhaseCurrent) {
        this.aPhaseCurrent = aPhaseCurrent;
    }

    public Double getaPhaseVoltage() {
        return aPhaseVoltage;
    }

    public void setAPhaseVoltage(Double aPhaseVoltage) {
        this.aPhaseVoltage = aPhaseVoltage;
    }

    public Double getbPhaseCurrent() {
        return bPhaseCurrent;
    }

    public void setBPhaseCurrent(Double bPhaseCurrent) {
        this.bPhaseCurrent = bPhaseCurrent;
    }

    public Double getbPhaseVoltage() {
        return bPhaseVoltage;
    }

    public void setBPhaseVoltage(Double bPhaseVoltage) {
        this.bPhaseVoltage = bPhaseVoltage;
    }

    public Double getcPhaseCurrent() {
        return cPhaseCurrent;
    }

    public void setCPhaseCurrent(Double cPhaseCurrent) {
        this.cPhaseCurrent = cPhaseCurrent;
    }

    public Double getcPhaseVoltage() {
        return cPhaseVoltage;
    }

    public void setCPhaseVoltage(Double cPhaseVoltage) {
        this.cPhaseVoltage = cPhaseVoltage;
    }

    public Double getTotalActivePower() {
        return totalActivePower;
    }

    public void setTotalActivePower(Double totalActivePower) {
        this.totalActivePower = totalActivePower;
    }

    public Double getTansTemp1() {
        return tansTemp1;
    }

    public void setTansTemp1(Double tansTemp1) {
        this.tansTemp1 = tansTemp1;
    }

    public Double getTansTemp2() {
        return tansTemp2;
    }

    public void setTansTemp2(Double tansTemp2) {
        this.tansTemp2 = tansTemp2;
    }
}
