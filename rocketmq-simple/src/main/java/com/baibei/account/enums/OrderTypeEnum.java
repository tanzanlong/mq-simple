package com.baibei.account.enums;

public enum OrderTypeEnum {
    
    ORDER_TYPE_FREEZE("1001", "冻结"), ORDER_TYPE_UNFREEZE("1002", "解冻"), ORDER_TYPE_TRADE(
            "1003", "交易"), ORDER_TYPE_LOAN("1004", "融资"), ORDER_TYPE_MARGIN(
            "1005", "融货"), ORDER_TYPE_REPAYMENT(
            "1006", "还款"), ORDER_TYPE_LOANINTEREST(
            "1007", "扣息"), ORDER_TYPE_DELIVERY(
            "1008", "交收"), ORDER_TYPE_TRADEORDER(
            "1009", "贸易"), ORDER_TYPE_RECHARGE(
            "1010", "入金"), ORDER_TYPE_WITHDRAW(
            "1011", "出金");

    private final String orderType;
    private final String description;

    private OrderTypeEnum(String orderType, String description) {
        this.orderType = orderType;
        this.description = description;
    }

    public static OrderTypeEnum fromOrderType(String orderType) {
        for (OrderTypeEnum type : values()) {
            if (type.getOrderType().equals(orderType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("invalid orderType");
    }

    public String getOrderType() {
        return orderType;
    }

    public String getDescription() {
        return description;
    }

}
