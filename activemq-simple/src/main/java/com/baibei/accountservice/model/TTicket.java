package com.baibei.accountservice.model;

import java.io.Serializable;
import java.util.Date;

public class TTicket implements Serializable {
    private String id;

    private String batchNo;

    private String ticketName;

    private String ticketType;

    private Long ticketValue;

    private Integer ticketFaceValue;

    private Date effectiveTime;

    private Date expireTime;

    private Long accountId;

    private String userId;

    private Long sellerAccountId;

    private String sellerUserId;

    private String businessType;

    private String ticketStatus;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo == null ? null : batchNo.trim();
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName == null ? null : ticketName.trim();
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType == null ? null : ticketType.trim();
    }

    public Long getTicketValue() {
        return ticketValue;
    }

    public void setTicketValue(Long ticketValue) {
        this.ticketValue = ticketValue;
    }

    public Integer getTicketFaceValue() {
        return ticketFaceValue;
    }

    public void setTicketFaceValue(Integer ticketFaceValue) {
        this.ticketFaceValue = ticketFaceValue;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

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

    public Long getSellerAccountId() {
        return sellerAccountId;
    }

    public void setSellerAccountId(Long sellerAccountId) {
        this.sellerAccountId = sellerAccountId;
    }

    public String getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(String sellerUserId) {
        this.sellerUserId = sellerUserId == null ? null : sellerUserId.trim();
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType == null ? null : businessType.trim();
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus == null ? null : ticketStatus.trim();
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
        sb.append(", batchNo=").append(batchNo);
        sb.append(", ticketName=").append(ticketName);
        sb.append(", ticketType=").append(ticketType);
        sb.append(", ticketValue=").append(ticketValue);
        sb.append(", ticketFaceValue=").append(ticketFaceValue);
        sb.append(", effectiveTime=").append(effectiveTime);
        sb.append(", expireTime=").append(expireTime);
        sb.append(", accountId=").append(accountId);
        sb.append(", userId=").append(userId);
        sb.append(", sellerAccountId=").append(sellerAccountId);
        sb.append(", sellerUserId=").append(sellerUserId);
        sb.append(", businessType=").append(businessType);
        sb.append(", ticketStatus=").append(ticketStatus);
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
        TTicket other = (TTicket) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getBatchNo() == null ? other.getBatchNo() == null : this.getBatchNo().equals(other.getBatchNo()))
            && (this.getTicketName() == null ? other.getTicketName() == null : this.getTicketName().equals(other.getTicketName()))
            && (this.getTicketType() == null ? other.getTicketType() == null : this.getTicketType().equals(other.getTicketType()))
            && (this.getTicketValue() == null ? other.getTicketValue() == null : this.getTicketValue().equals(other.getTicketValue()))
            && (this.getTicketFaceValue() == null ? other.getTicketFaceValue() == null : this.getTicketFaceValue().equals(other.getTicketFaceValue()))
            && (this.getEffectiveTime() == null ? other.getEffectiveTime() == null : this.getEffectiveTime().equals(other.getEffectiveTime()))
            && (this.getExpireTime() == null ? other.getExpireTime() == null : this.getExpireTime().equals(other.getExpireTime()))
            && (this.getAccountId() == null ? other.getAccountId() == null : this.getAccountId().equals(other.getAccountId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getSellerAccountId() == null ? other.getSellerAccountId() == null : this.getSellerAccountId().equals(other.getSellerAccountId()))
            && (this.getSellerUserId() == null ? other.getSellerUserId() == null : this.getSellerUserId().equals(other.getSellerUserId()))
            && (this.getBusinessType() == null ? other.getBusinessType() == null : this.getBusinessType().equals(other.getBusinessType()))
            && (this.getTicketStatus() == null ? other.getTicketStatus() == null : this.getTicketStatus().equals(other.getTicketStatus()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getBatchNo() == null) ? 0 : getBatchNo().hashCode());
        result = prime * result + ((getTicketName() == null) ? 0 : getTicketName().hashCode());
        result = prime * result + ((getTicketType() == null) ? 0 : getTicketType().hashCode());
        result = prime * result + ((getTicketValue() == null) ? 0 : getTicketValue().hashCode());
        result = prime * result + ((getTicketFaceValue() == null) ? 0 : getTicketFaceValue().hashCode());
        result = prime * result + ((getEffectiveTime() == null) ? 0 : getEffectiveTime().hashCode());
        result = prime * result + ((getExpireTime() == null) ? 0 : getExpireTime().hashCode());
        result = prime * result + ((getAccountId() == null) ? 0 : getAccountId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getSellerAccountId() == null) ? 0 : getSellerAccountId().hashCode());
        result = prime * result + ((getSellerUserId() == null) ? 0 : getSellerUserId().hashCode());
        result = prime * result + ((getBusinessType() == null) ? 0 : getBusinessType().hashCode());
        result = prime * result + ((getTicketStatus() == null) ? 0 : getTicketStatus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }
}