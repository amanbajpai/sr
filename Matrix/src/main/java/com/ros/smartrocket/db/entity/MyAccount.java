package com.ros.smartrocket.db.entity;

public class MyAccount extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;


    private Double TotalEarnings;
    private Double Balance;
    private Integer Lavel;
    private Integer Expierence;
    private Integer ToNextLavel;

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

    public Integer getLavel() {
        return Lavel;
    }

    public void setLavel(Integer lavel) {
        Lavel = lavel;
    }

    public Integer getExpierence() {
        return Expierence;
    }

    public void setExpierence(Integer expierence) {
        Expierence = expierence;
    }

    public Integer getToNextLavel() {
        return ToNextLavel;
    }

    public void setToNextLavel(Integer toNextLavel) {
        ToNextLavel = toNextLavel;
    }


}
