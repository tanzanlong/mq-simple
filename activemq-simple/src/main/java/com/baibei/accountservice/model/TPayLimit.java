package com.baibei.accountservice.model;

import java.io.Serializable;
import java.util.Date;

public class TPayLimit implements Serializable {
    private Long accountId;

    private String userId;

    private Integer canNotRecharge;

    private Integer canNotWithdraw;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public Integer getCanNotRecharge() {
        return canNotRecharge;
    }

    public void setCanNotRecharge(Integer canNotRecharge) {
        this.canNotRecharge = canNotRecharge;
    }

    public Integer getCanNotWithdraw() {
        return canNotWithdraw;
    }

    public void setCanNotWithdraw(Integer canNotWithdraw) {
        this.canNotWithdraw = canNotWithdraw;
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
        sb.append(", accountId=").append(accountId);
        sb.append(", userId=").append(userId);
        sb.append(", canNotRecharge=").append(canNotRecharge);
        sb.append(", canNotWithdraw=").append(canNotWithdraw);
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
        TPayLimit other = (TPayLimit) that;
        return (this.getAccountId() == null ? other.getAccountId() == null : this.getAccountId().equals(other.getAccountId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getCanNotRecharge() == null ? other.getCanNotRecharge() == null : this.getCanNotRecharge().equals(other.getCanNotRecharge()))
            && (this.getCanNotWithdraw() == null ? other.getCanNotWithdraw() == null : this.getCanNotWithdraw().equals(other.getCanNotWithdraw()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAccountId() == null) ? 0 : getAccountId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getCanNotRecharge() == null) ? 0 : getCanNotRecharge().hashCode());
        result = prime * result + ((getCanNotWithdraw() == null) ? 0 : getCanNotWithdraw().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }
}