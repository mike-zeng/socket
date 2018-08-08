package com.cslg.socket.model;

import java.sql.Timestamp;

public class Load {

    private Integer id;

    private String loadName;

    private Timestamp times;

    private String local;

    private Double current;

    private Double voltage;

    private Double apparentPower;

    private Double activePower;

    public Double getCurrent() {
        return current;
    }

    public void setCurrent(Double current) {
        this.current = current;
    }

    public Double getVoltage() {
        return voltage;
    }

    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }

    public Double getApparentPower() {
        return apparentPower;
    }

    public void setApparentPower(Double apparentPower) {
        this.apparentPower = apparentPower;
    }

    public Double getActivePower() {
        return activePower;
    }

    public void setActivePower(Double activePower) {
        this.activePower = activePower;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLoadName() {
        return loadName;
    }

    public void setLoadName(String loadName) {
        this.loadName = loadName;
    }

    public Timestamp getTimes() {
        return times;
    }

    public void setTimes(Timestamp times) {
        this.times = times;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }
}
