package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class MyAccount extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    @SerializedName("Name")
    private String name;
    @SerializedName("PhotoUrl")
    private String photoUrl;
    @SerializedName("TotalEarnings")
    private Double totalEarnings;
    @SerializedName("Balance")
    private Double balance;
    @SerializedName("MinimalWithdrawAmount")
    private Double minimalWithdrawAmount;
    @SerializedName("Experience")
    private Integer experience;
    @SerializedName("MinLevelExperience")
    private Integer minLevelExperience;
    @SerializedName("MaxLevelExperience")
    private Integer maxLevelExperience;
    @SerializedName("ToNextLevel")
    private Integer toNextLevel;
    @SerializedName("LevelNumber")
    private Integer levelNumber;
    @SerializedName("LevelName")
    private String levelName;
    @SerializedName("LevelDescription")
    private String levelDescription;
    @SerializedName("LevelIconUrl")
    private String levelIconUrl;
    @SerializedName("CurrencySign")
    private String currencySign;
    @SerializedName("CashoutRequested")
    private Boolean cashoutRequested;
    @SerializedName("TermsAndConditionsVersion")
    private Integer termsAndConditionsVersion;
    @SerializedName("InPaymentProcess")
    private Double inPaymentProcess;
    @SerializedName("AliPayAccountExists")
    private Boolean aliPayAccountExists;

    public Double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(Double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getToNextLevel() {
        return toNextLevel;
    }

    public void setToNextLevel(Integer toNextLevel) {
        this.toNextLevel = toNextLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Integer getMinLevelExperience() {
        return minLevelExperience;
    }

    public void setMinLevelExperience(Integer minLevelExperience) {
        this.minLevelExperience = minLevelExperience;
    }

    public Integer getMaxLevelExperience() {
        return maxLevelExperience;
    }

    public void setMaxLevelExperience(Integer maxLevelExperience) {
        this.maxLevelExperience = maxLevelExperience;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelIconUrl() {
        return levelIconUrl;
    }

    public void setLevelIconUrl(String levelIconUrl) {
        this.levelIconUrl = levelIconUrl;
    }

    public Integer getTermsAndConditionsVersion() {
        return termsAndConditionsVersion != null ? termsAndConditionsVersion : 1;
    }

    public void setTermsAndConditionsVersion(Integer termsAndConditionsVersion) {
        this.termsAndConditionsVersion = termsAndConditionsVersion;
    }

    public String getLevelDescription() {
        return levelDescription;
    }

    public void setLevelDescription(String levelDescription) {
        this.levelDescription = levelDescription;
    }

    public String getCurrencySign() {
        return currencySign;
    }

    public void setCurrencySign(String currencySign) {
        this.currencySign = currencySign;
    }

    public Double getMinimalWithdrawAmount() {
        return minimalWithdrawAmount;
    }

    public void setMinimalWithdrawAmount(Double minimalWithdrawAmount) {
        this.minimalWithdrawAmount = minimalWithdrawAmount;
    }

    public Boolean getCashoutRequested() {
        return cashoutRequested;
    }

    public void setCashoutRequested(Boolean cashoutRequested) {
        this.cashoutRequested = cashoutRequested;
    }

    public Double getInPaymentProcess() {
        return inPaymentProcess;
    }

    public void setInPaymentProcess(Double inPaymentProcess) {
        this.inPaymentProcess = inPaymentProcess;
    }

    public Boolean getAliPayAccountExists() {
        return aliPayAccountExists;
    }

    public void setAliPayAccountExists(Boolean aliPayAccountExists) {
        this.aliPayAccountExists = aliPayAccountExists;
    }
}
