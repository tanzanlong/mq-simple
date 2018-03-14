package com.baibei.accountservice.model;

import java.io.Serializable;
import java.util.Date;

public class TAbnormalOrderLog implements Serializable {
    private Long id;

    private String orderType;

    private String orderId;

    private String orgStatus;

    private String channelStatus;

    private Long orgAmount;

    private Long channelAmount;

    private String channel;

    private String auditStatus;

    private Date auditTime;

    private String auditMsg;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType == null ? null : orderType.trim();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getOrgStatus() {
        return orgStatus;
    }

    public void setOrgStatus(String orgStatus) {
        this.orgStatus = orgStatus == null ? null : orgStatus.trim();
    }

    public String getChannelStatus() {
        return channelStatus;
    }

    public void setChannelStatus(String channelStatus) {
        this.channelStatus = channelStatus == null ? null : channelStatus.trim();
    }

    public Long getOrgAmount() {
        return orgAmount;
    }

    public void setOrgAmount(Long orgAmount) {
        this.orgAmount = orgAmount;
    }

    public Long getChannelAmount() {
        return channelAmount;
    }

    public void setChannelAmount(Long channelAmount) {
        this.channelAmount = channelAmount;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel == null ? null : channel.trim();
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus == null ? null : auditStatus.trim();
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditMsg() {
        return auditMsg;
    }

    public void setAuditMsg(String auditMsg) {
        this.auditMsg = auditMsg == null ? null : auditMsg.trim();
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
        sb.append(", orderType=").append(orderType);
        sb.append(", orderId=").append(orderId);
        sb.append(", orgStatus=").append(orgStatus);
        sb.append(", channelStatus=").append(channelStatus);
        sb.append(", orgAmount=").append(orgAmount);
        sb.append(", channelAmount=").append(channelAmount);
        sb.append(", channel=").append(channel);
        sb.append(", auditStatus=").append(auditStatus);
        sb.append(", auditTime=").append(auditTime);
        sb.append(", auditMsg=").append(auditMsg);
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
        TAbnormalOrderLog other = (TAbnormalOrderLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderType() == null ? other.getOrderType() == null : this.getOrderType().equals(other.getOrderType()))
            && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
            && (this.getOrgStatus() == null ? other.getOrgStatus() == null : this.getOrgStatus().equals(other.getOrgStatus()))
            && (this.getChannelStatus() == null ? other.getChannelStatus() == null : this.getChannelStatus().equals(other.getChannelStatus()))
            && (this.getOrgAmount() == null ? other.getOrgAmount() == null : this.getOrgAmount().equals(other.getOrgAmount()))
            && (this.getChannelAmount() == null ? other.getChannelAmount() == null : this.getChannelAmount().equals(other.getChannelAmount()))
            && (this.getChannel() == null ? other.getChannel() == null : this.getChannel().equals(other.getChannel()))
            && (this.getAuditStatus() == null ? other.getAuditStatus() == null : this.getAuditStatus().equals(other.getAuditStatus()))
            && (this.getAuditTime() == null ? other.getAuditTime() == null : this.getAuditTime().equals(other.getAuditTime()))
            && (this.getAuditMsg() == null ? other.getAuditMsg() == null : this.getAuditMsg().equals(other.getAuditMsg()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderType() == null) ? 0 : getOrderType().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getOrgStatus() == null) ? 0 : getOrgStatus().hashCode());
        result = prime * result + ((getChannelStatus() == null) ? 0 : getChannelStatus().hashCode());
        result = prime * result + ((getOrgAmount() == null) ? 0 : getOrgAmount().hashCode());
        result = prime * result + ((getChannelAmount() == null) ? 0 : getChannelAmount().hashCode());
        result = prime * result + ((getChannel() == null) ? 0 : getChannel().hashCode());
        result = prime * result + ((getAuditStatus() == null) ? 0 : getAuditStatus().hashCode());
        result = prime * result + ((getAuditTime() == null) ? 0 : getAuditTime().hashCode());
        result = prime * result + ((getAuditMsg() == null) ? 0 : getAuditMsg().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }
}