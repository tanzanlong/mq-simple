package com.baibei.account.enums;

public enum BalanceTypeEnum {


    BALANCE_TYPE_AVALIABLE("AVALIABLE", "可用"), BALANCE_TYPE_FREEZON("FREEZON", "冻结"), BALANCE_TYPE_LOAN(
            "LOAN", "融资"), BALANCE_TYPE_UNPAY("UNPAY", "其它应付款");

    private final String balanceType;
    private final String description;

    private BalanceTypeEnum(String balanceType, String description) {
        this.balanceType = balanceType;
        this.description = description;
    }

    public static BalanceTypeEnum fromOrderType(String balanceType) {
        for (BalanceTypeEnum type : values()) {
            if (type.getBalanceType().equals(balanceType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("invalid orderType");
    }



    public String getBalanceType() {
        return balanceType;
    }

    public String getDescription() {
        return description;
    }

}
