package com.ros.smartrocket.db.entity;

public class MyAccount extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;


    private Double TotalEarnings;
    private Double Balance;
    private Integer Level;
    private Integer Experience;
    private Integer ToNextLevel;

    public Double getTotalEarnings() {
        return TotalEarnings;
    }

    public void setTotalEarnings(Double totalEarnings) {
        TotalEarnings = totalEarnings;
    }

    public Double getBalance() {
        return Balance;
    }

    public void setBalance(Double balance) {
        Balance = balance;
    }

    public Integer getLevel() {
        return Level;
    }

    public void setLevel(Integer level) {
        Level = level;
    }

    public Integer getExperience() {
        return Experience;
    }

    public void setExperience(Integer experience) {
        Experience = experience;
    }

    public Integer getToNextLevel() {
        return ToNextLevel;
    }

    public void setToNextLevel(Integer toNextLevel) {
        ToNextLevel = toNextLevel;
    }


}
