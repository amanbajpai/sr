package com.ros.smartrocket.db.entity.account;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;
import com.ros.smartrocket.utils.UIUtils;

import java.math.BigDecimal;
import java.util.Locale;

public class MyAccount extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;
    private static final Integer PAY_PAL = 1;
    private static final Integer ALI_PAY = 2;
    private static final Integer NATIONAL_ID = 4;
    private static final String SUPPORT_EMAIL = "support@smart-rocket.com";

    @SerializedName("SingleName")
    private String singleName;
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
    @SerializedName("IsPaymentAccountExists")
    private Boolean isPaymentAccountExists;
    @SerializedName("AllowPushNotification")
    private Boolean allowPushNotification;
    @SerializedName("IsUpdateNameRequired")
    private Boolean isUpdateNameRequired;
    @SerializedName("CountryName")
    private String countryName;
    @SerializedName("CityName")
    private String cityName;
    @SerializedName("Joined")
    private String joined;
    @SerializedName("PaymentSystem")
    private Integer paymentSystem;
    @SerializedName("SupportEmail")
    private String supportEmail;
    @SerializedName("Reputation")
    private Double reputation;
    @SerializedName("CountryCode")
    private String countryCode;
    @SerializedName("CompanyName")
    private String companyName = "";
    @SerializedName("CountryId")
    private Integer countryId;

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

    public String getSingleName() {
        return singleName;
    }

    public void setSingleName(String singleName) {
        this.singleName = singleName;
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
        return TextUtils.isEmpty(levelIconUrl) ? "" : levelIconUrl.replaceAll(" ", "%20");
    }

    public void setLevelIconUrl(String levelIconUrl) {
        this.levelIconUrl = levelIconUrl;
    }

    public Integer getTermsAndConditionsVersion() {
        return termsAndConditionsVersion;
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

    public Boolean getIsPaymentAccountExists() {
        return isPaymentAccountExists;
    }

    public void setIsPaymentAccountExists(Boolean isPaymentAccountExists) {
        this.isPaymentAccountExists = isPaymentAccountExists;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Boolean getAllowPushNotification() {
        return allowPushNotification == null ? false : allowPushNotification;
    }

    public void setAllowPushNotification(Boolean allowPushNotification) {
        this.allowPushNotification = allowPushNotification;
    }

    public Boolean getIsUpdateNameRequired() {
        return isUpdateNameRequired == null ? false : isUpdateNameRequired;
    }

    public void setIsUpdateNameRequired(Boolean isUpdateNameRequired) {
        this.isUpdateNameRequired = isUpdateNameRequired;
    }

    public String getJoined() {
        return joined == null ? "" : joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public String getCityName() {
        return cityName == null ? "" : cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName == null ? "" : countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Integer getPaymentSystem() {
        return paymentSystem;
    }

    public void setPaymentSystem(Integer paymentSystem) {
        this.paymentSystem = paymentSystem;
    }

    public boolean isPayPal() {
        return PAY_PAL.equals(paymentSystem);
    }

    public boolean isAliPay() {
        return ALI_PAY.equals(paymentSystem);
    }

    private boolean isNationalId() {
        return NATIONAL_ID.equals(paymentSystem);
    }

    public boolean canWithdraw() {
        return PAY_PAL.equals(paymentSystem) || (ALI_PAY.equals(paymentSystem) || NATIONAL_ID.equals(paymentSystem)) && getIsPaymentAccountExists();
    }

    public boolean isPaymentSettingsEnabled() {
        return isAliPay() || isNationalId();
    }

    public boolean isWithdrawEnabled() {
        return balance >= minimalWithdrawAmount && !cashoutRequested;
    }

    public String getSupportEmail() {
        return TextUtils.isEmpty(supportEmail) ? SUPPORT_EMAIL : supportEmail;
    }

    public String getStringReputation() {
        return reputation != null
                ? String.format(Locale.getDefault(), "%.0f", UIUtils.round(reputation, 0, BigDecimal.ROUND_HALF_UP))
                : "0";
    }

    public String getCountryCode() {
        return TextUtils.isEmpty(countryCode) ? "en" : countryCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getCountryId() {
        return countryId == null ? 0 : countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }
}
