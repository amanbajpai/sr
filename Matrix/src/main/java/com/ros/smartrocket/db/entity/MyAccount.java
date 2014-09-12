package com.ros.smartrocket.db.entity;

public class MyAccount extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private String Name;
    private String PhotoUrl;
    private Double TotalEarnings;
    private Double Balance;
    private Double MinimalWithdrawAmount;
    private Integer Experience;
    private Integer MinLevelExperience;
    private Integer MaxLevelExperience;
    private Integer ToNextLevel;
    private Integer LevelNumber;
    private String LevelName;
    private String LevelDescription;
    private String LevelIconUrl;
    private String CurrencySign;
    private Boolean CashoutRequested;

    private Integer TermsAndConditionsVersion;

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


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public Integer getMinLevelExperience() {
        return MinLevelExperience;
    }

    public void setMinLevelExperience(Integer minLevelExperience) {
        MinLevelExperience = minLevelExperience;
    }

    public Integer getMaxLevelExperience() {
        return MaxLevelExperience;
    }

    public void setMaxLevelExperience(Integer maxLevelExperience) {
        MaxLevelExperience = maxLevelExperience;
    }

    public Integer getLevelNumber() {
        return LevelNumber;
    }

    public void setLevelNumber(Integer levelNumber) {
        LevelNumber = levelNumber;
    }

    public String getLevelName() {
        return LevelName;
    }

    public void setLevelName(String levelName) {
        LevelName = levelName;
    }

    public String getLevelIconUrl() {
        return LevelIconUrl;
    }

    public void setLevelIconUrl(String levelIconUrl) {
        LevelIconUrl = levelIconUrl;
    }

    public Integer getTermsAndConditionsVersion() {
        return TermsAndConditionsVersion != null ? TermsAndConditionsVersion : 1;
    }

    public void setTermsAndConditionsVersion(Integer termsAndConditionsVersion) {
        TermsAndConditionsVersion = termsAndConditionsVersion;
    }

    public String getLevelDescription() {
        return LevelDescription;
    }

    public void setLevelDescription(String levelDescription) {
        LevelDescription = levelDescription;
    }

    public String getCurrencySign() {
        return CurrencySign;
    }

    public void setCurrencySign(String currencySign) {
        CurrencySign = currencySign;
    }

    public Double getMinimalWithdrawAmount() {
        return MinimalWithdrawAmount;
    }

    public void setMinimalWithdrawAmount(Double minimalWithdrawAmount) {
        MinimalWithdrawAmount = minimalWithdrawAmount;
    }

    public Boolean getCashoutRequested() {
        return CashoutRequested;
    }

    public void setCashoutRequested(Boolean cashoutRequested) {
        CashoutRequested = cashoutRequested;
    }
}
