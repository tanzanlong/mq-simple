package com.baibei.accountservice.model;

import java.io.Serializable;
import java.util.Date;

public class TDailyPaycenterResult implements Serializable {
    private Long id;

    private String orderId;

    private Long amountExpect;

    private Long amountActual;

    private String orderType;

    private String orderStatusExpect;

    private String orderStatusActual;

    private Date dealDate;

    private String type;

    private String memo;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public Long getAmountExpect() {
        return amountExpect;
    }

    public void setAmountExpect(Long amountExpect) {
        this.amountExpect = amountExpect;
    }

    public Long getAmountActual() {
        return amountActual;
    }

    public void setAmountActual(Long amountActual) {
        this.amountActual = amountActual;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType == null ? null : orderType.trim();
    }

    public String getOrderStatusExpect() {
        return orderStatusExpect;
    }

    public void setOrderStatusExpect(String orderStatusExpect) {
        this.orderStatusExpect = orderStatusExpect == null ? null : orderStatusExpect.trim();
    }

    public String getOrderStatusActual() {
        return orderStatusActual;
    }

    public void setOrderStatusActual(String orderStatusActual) {
        this.orderStatusActual = orderStatusActual == null ? null : orderStatusActual.trim();
    }

    public Date getDealDate() {
        return dealDate;
    }

    public void setDealDate(Date dealDate) {
        this.dealDate = dealDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
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
        sb.append(", orderId=").append(orderId);
        sb.append(", amountExpect=").append(amountExpect);
        sb.append(", amountActual=").append(amountActual);
        sb.append(", orderType=").append(orderType);
        sb.append(", orderStatusExpect=").append(orderStatusExpect);
        sb.append(", orderStatusActual=").append(orderStatusActual);
        sb.append(", dealDate=").append(dealDate);
        sb.append(", type=").append(type);
        sb.append(", memo=").append(memo);
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
        TDailyPaycenterResult other = (TDailyPaycenterResult) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
            && (this.getAmountExpect() == null ? other.getAmountExpect() == null : this.getAmountExpect().equals(other.getAmountExpect()))
            && (this.getAmountActual() == null ? other.getAmountActual() == null : this.getAmountActual().equals(other.getAmountActual()))
            && (this.getOrderType() == null ? other.getOrderType() == null : this.getOrderType().equals(other.getOrderType()))
            && (this.getOrderStatusExpect() == null ? other.getOrderStatusExpect() == null : this.getOrderStatusExpect().equals(other.getOrderStatusExpect()))
            && (this.getOrderStatusActual() == null ? other.getOrderStatusActual() == null : this.getOrderStatusActual().equals(other.getOrderStatusActual()))
            && (this.getDealDate() == null ? other.getDealDate() == null : this.getDealDate().equals(other.getDealDate()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getMemo() == null ? other.getMemo() == null : this.getMemo().equals(other.getMemo()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getAmountExpect() == null) ? 0 : getAmountExpect().hashCode());
        result = prime * result + ((getAmountActual() == null) ? 0 : getAmountActual().hashCode());
        result = prime * result + ((getOrderType() == null) ? 0 : getOrderType().hashCode());
        result = prime * result + ((getOrderStatusExpect() == null) ? 0 : getOrderStatusExpect().hashCode());
        result = prime * result + ((getOrderStatusActual() == null) ? 0 : getOrderStatusActual().hashCode());
        result = prime * result + ((getDealDate() == null) ? 0 : getDealDate().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getMemo() == null) ? 0 : getMemo().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }
}