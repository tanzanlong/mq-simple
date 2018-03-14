package com.baibei.account.enums;

public enum FeeItemEnum {

    FEE_TYPE_FREEZE("1001", "冻结金额", "冻结金额"), FEE_TYPE_UNFREEZE("1002", "解冻金额", "解冻金额"), FEE_TYPE_BUYTRADE("10031",
            "交易（买货）", "购入"), FEE_TYPE_SELLTRADE("10032", "交易（卖货）", "售出"), FEE_TYPE_BUYTRA售DE_POUNDAGE("10033",
            "交易（买货手续费）","买入服务费"), FEE_TYPE_SELLTRADE_POUNDAGE("10034", "交易（卖货手续费）", "售出服务费"), FEE_TYPE_LOSSBUYTRADE(
            "10035", "强制交易（买货）", "买入"), FEE_TYPE_LOSSBUYTRADE_ADVANCED("10036", "强制交易垫付（买货垫付）", "强制交易垫付（买货垫付）"), FEE_TYPE_LOAN(
            "10041", "融资", "融资"), FEE_TYPE_LOAN_POUNDAGE("10042", "融资手续费", "融资服务费"), FEE_TYPE_MARGIN("10051", "融货", "融货"), FEE_TYPE_MARGIN_POUNDAGE(
            "10052", "融货手续费", "融货服务费"), FEE_TYPE_REPAYMENT("10061", "还款", "还款"), FEE_TYPE_LOSS_REPAYMENT("10062",
            "强制还款 (还款垫付)","强制还款 (还款垫付)"), FEE_TYPE_LOANINTEREST("10071", "融资利息", "融资利息"), FEE_TYPE_MARGININTEREST(
            "10072", "融货利息", "融货利息"), FEE_TYPE_DELIVERY_PREMIUM("1008", "交收升贴水", "交收升贴水"), FEE_TYPE_TRADEORDER(
            "1009", "贸易", "采购"), FEE_TYPE_RECHARGE("1010", "资金转入", "资金转入"), FEE_TYPE_WITHDRAW("1011", "资金转出", "资金转出");

    private final String feeItem;
    private final String description;
    
    /**
     * 用户流水显示用
     */
    private final String remark;

    private FeeItemEnum(String feeItem, String description,String remark) {
        this.feeItem = feeItem;
        this.description = description;
        this.remark=remark;
    }

    public static FeeItemEnum fromOrderType(String feeItem) {
        for (FeeItemEnum type : values()) {
            if (type.getFeeItem().equals(feeItem)) {
                return type;
            }
        }
        throw new IllegalArgumentException("invalid feeItem");
    }

    public String getFeeItem() {
        return feeItem;
    }

    public String getDescription() {
        return description;
    }

    public String getRemark() {
        return remark;
    }

    

}
