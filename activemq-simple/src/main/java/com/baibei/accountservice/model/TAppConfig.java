package com.baibei.accountservice.model;

import java.io.Serializable;
import java.util.Date;

public class TAppConfig implements Serializable {
    private Long id;

    private String appId;

    private String appSrcret;

    private String rechargeNotifyUrl;

    private String withdrawNotifyUrl;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    public String getAppSrcret() {
        return appSrcret;
    }

    public void setAppSrcret(String appSrcret) {
        this.appSrcret = appSrcret == null ? null : appSrcret.trim();
    }

    public String getRechargeNotifyUrl() {
        return rechargeNotifyUrl;
    }

    public void setRechargeNotifyUrl(String rechargeNotifyUrl) {
        this.rechargeNotifyUrl = rechargeNotifyUrl == null ? null : rechargeNotifyUrl.trim();
    }

    public String getWithdrawNotifyUrl() {
        return withdrawNotifyUrl;
    }

    public void setWithdrawNotifyUrl(String withdrawNotifyUrl) {
        this.withdrawNotifyUrl = withdrawNotifyUrl == null ? null : withdrawNotifyUrl.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", appId=").append(appId);
        sb.append(", appSrcret=").append(appSrcret);
        sb.append(", rechargeNotifyUrl=").append(rechargeNotifyUrl);
        sb.append(", withdrawNotifyUrl=").append(withdrawNotifyUrl);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TAppConfig other = (TAppConfig) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()))
            && (this.getAppSrcret() == null ? other.getAppSrcret() == null : this.getAppSrcret().equals(other.getAppSrcret()))
            && (this.getRechargeNotifyUrl() == null ? other.getRechargeNotifyUrl() == null : this.getRechargeNotifyUrl().equals(other.getRechargeNotifyUrl()))
            && (this.getWithdrawNotifyUrl() == null ? other.getWithdrawNotifyUrl() == null : this.getWithdrawNotifyUrl().equals(other.getWithdrawNotifyUrl()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
        result = prime * result + ((getAppSrcret() == null) ? 0 : getAppSrcret().hashCode());
        result = prime * result + ((getRechargeNotifyUrl() == null) ? 0 : getRechargeNotifyUrl().hashCode());
        result = prime * result + ((getWithdrawNotifyUrl() == null) ? 0 : getWithdrawNotifyUrl().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }
}