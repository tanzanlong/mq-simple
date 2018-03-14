package com.baibei.accountservice.account.comm;

public class Constants {

    /**默认业务类型：新模式**/
    public static final String BUSINESS_TYPE_DEFAULT = "CH";
    
    
    /*******************余额类型********************************/
    /**余额类型：可用**/
    public static final String BALANCE_TYPE_AVALIABLE = "AVALIABLE";
    
    /**余额类型：冻结**/
    public static final String BALANCE_TYPE_FREEZON = "FREEZON";
    
    /**余额类型：融资**/
    public static final String BALANCE_TYPE_LOAN = "LOAN";
    
    /**余额类型：其它应付款**/
    public static final String BALANCE_TYPE_UNPAY = "UNPAY";
    
    /**余额类型：体验券**/
    public static final String BALANCE_TYPE_TICKET = "TICKET";
    
    /**余额类型: 券保证金**/
    public static final String BALANCE_TYPE_TICKET_BONDS = "TICKET_BONDS";
    
    /*******************订单类型********************************/
    /**订单类型：冻结**/
    public static final String ORDER_TYPE_FREEZE = "1001";
    
    /**订单类型：解冻**/
    public static final String ORDER_TYPE_UNFREEZE = "1002";
    
    /**订单类型：交易**/
    public static final String ORDER_TYPE_TRADE = "1003";
    
    /**订单类型：融资**/
    public static final String ORDER_TYPE_LOAN = "1004";
    
    /**订单类型：融货**/
    public static final String ORDER_TYPE_MARGIN = "1005";
    
    /**订单类型：还款**/
    public static final String ORDER_TYPE_REPAYMENT = "1006";
    
    /**订单类型：扣息**/
    public static final String ORDER_TYPE_LOANINTEREST = "1007";
    
    /**订单类型：交收**/
    public static final String ORDER_TYPE_DELIVERY = "1008";
    
    /**订单类型：交收冲正**/
    public static final String ORDER_TYPE_DELIVERY_ROLLBACK = "11008";
    
    /**订单类型：贸易**/
    public static final String ORDER_TYPE_TRADEORDER = "1009";
    
    /**订单类型：入金**/
    public static final String ORDER_TYPE_RECHARGE = "1010";
    
    /**订单类型：入金冲正**/
    public static final String ORDER_TYPE_RECHARGE_ROLLBACK = "11010";
    
    /**订单类型：出金**/
    public static final String ORDER_TYPE_WITHDRAW = "1011";
    
    /**订单类型：出金冲正**/
    public static final String ORDER_TYPE_WITHDRAW_ROLLBACK = "11011";
    
    /**订单类型：OTC建仓**/
    public static final String ORDER_TYPE_OPENPOSITION = "2001";
    
    /**订单类型：OTC建仓冲正**/
    public static final String ORDER_TYPE_OPENPOSITION_ROLLBACK = "12001";
    
    /**订单类型：OTC平仓**/
    public static final String ORDER_TYPE_CLOSEPOSITION = "2002";
    
    /**订单类型：OTC平仓冲正**/
    public static final String ORDER_TYPE_CLOSEPOSITION_ROLLBACK = "12002";
    
    /*******************费用类型********************************/
    /***费用类型：冻结金额**/
    public static final String FEE_TYPE_FREEZE = "1001";
    
    /***费用类型：解冻金额**/
    public static final String FEE_TYPE_UNFREEZE = "1002";
    
    /**费用类型：交易（买货）**/
    public static final String FEE_TYPE_BUYTRADE = "10031";
    
    /**费用类型：交易（卖货）**/
    public static final String FEE_TYPE_SELLTRADE = "10032";
    
    /**费用类型：交易（买货手续费）**/
    public static final String FEE_TYPE_BUYTRADE_POUNDAGE = "10033";
    
    /**费用类型：交易（卖货手续费）**/
    public static final String FEE_TYPE_SELLTRADE_POUNDAGE = "10034";
    
    /**费用类型：强制交易（买货）**/
    public static final String FEE_TYPE_LOSSBUYTRADE = "10035";
    
    /**费用类型：强制交易垫付（买货垫付）**/
    public static final String FEE_TYPE_LOSSBUYTRADE_ADVANCED = "10036";
    
    /**费用类型：融资**/
    public static final String FEE_TYPE_LOAN = "10041";
    
    /**费用类型：融资手续费**/
    public static final String FEE_TYPE_LOAN_POUNDAGE = "10042";
  
    /**费用类型：融货**/
    public static final String FEE_TYPE_MARGIN = "10051";
    
    /**费用类型：融货手续费**/
    public static final String FEE_TYPE_MARGIN_POUNDAGE = "10052";
    
    /**费用类型：还款**/
    public static final String FEE_TYPE_REPAYMENT = "10061";
    
    /**费用类型：强制还款 (还款垫付)**/
    public static final String FEE_TYPE_LOSS_REPAYMENT = "10062";
    
     /**费用类型：融资利息**/
    public static final String FEE_TYPE_LOANINTEREST = "10071";
    
    /**费用类型：融资利息**/
    public static final String FEE_TYPE_MARGININTEREST = "10072";
    
    /**费用类型：交收升贴水**/
    public static final String FEE_TYPE_DELIVERY_PREMIUM = "1008";
    
    /**费用类型：交收支出**/
    public static final String FEE_TYPE_DELIVERY = "10082";
    
    /**费用类型：交收支出回退**/
    public static final String FEE_TYPE_DELIVERY_ROLLBACK = "110082";
    
    /**费用类型：贸易**/
    public static final String FEE_TYPE_TRADEORDER = "1009";
    
    /**费用类型：入金**/
    public static final String FEE_TYPE_RECHARGE = "1010";
    
    /**费用类型：入金手续费**/
    public static final String FEE_TYPE_RECHARGE_POUNDAGE = "10102";
    
    /**费用类型：入金手续费回退**/
    public static final String FEE_TYPE_RECHARGE_POUNDAGE_ROLLBACK = "110102";
    
    /**费用类型：出金**/
    public static final String FEE_TYPE_WITHDRAW = "1011";
    
    /**费用类型：出金手续费**/
    public static final String FEE_TYPE_WITHDRAW_POUNDAGE = "10112";
    
    /**费用类型：出金手续费回退**/
    public static final String FEE_TYPE_WITHDRAW_POUNDAGE_ROLLBACK = "110112";
    
    /**费用类型：出金回退**/
    public static final String FEE_TYPE_WITHDRAW_ROLLBACK = "11011";
    
    /**费用类型：建仓费用**/
    public static final String FEE_TYPE_OPENPOSITION = "20011";
    
    /**费用类型：建仓回退**/
    public static final String FEE_TYPE_OPENPOSITION_ROLLBACK = "120011";
    
    /**费用类型：建仓手续费**/
    public static final String FEE_TYPE_OPENPOSITION_POUNDAGE = "20012";
    
    /**费用类型：建仓精度调整**/
    public static final String FEE_TYPE_OPENPOSITION_CORRECTION = "20013";
    
    /**费用类型：建仓手续费回退**/
    public static final String FEE_TYPE_OPENPOSITION_POUNDAGE_ROLLBACK = "120012";
    
    /**费用类型：平仓**/
    public static final String FEE_TYPE_CLOSEPOSITION = "20021";
    
    /**费用类型：平仓盈利**/
    public static final String FEE_TYPE_CLOSEPOSITION_GAIN = "20022";
    
    /**费用类型：平仓亏损**/
    public static final String FEE_TYPE_CLOSEPOSITION_LOSS = "20023";
    
    /**费用类型：平仓数据修复**/
    public static final String FEE_TYPE_CLOSEPOSITION_CORRECTION = "20024";
    
    /**订单类型：补偿**/
    public static final String ORDER_TYPE_COMPENSATE = "3001";
    
    /**费用类型：建仓补偿**/
    public static final String FEE_TYPE_COMPENSATE_CORRECTION_OPENPOSITION = "300101";
    
    
    /**费用类型：平仓补偿**/
    public static final String FEE_TYPE_COMPENSATE_CORRECTION_CLOSEPOSITION = "300102";
    
    /**余额更新TOPIC**/
    public static final String TOPIC_UPDATE_ACCOUNT = "TOPIC_UPDATE_ACCOUNT";

    /**余额更新TAG**/
    public static final String TAG_UPDATE_ACCOUNT = "TAG_UPDATE_ACCOUNT";
    
    
    /**帐户状态正常标识**/
    public static final int ACCOUNT_ISDELETE_NO = 0;
    
    /**出入金订单状态：成功**/
    public static final String STATUS_SUCCESS = "SUCCESS";
    
    /**出入金订单状态：失败**/
    public static final String STATUS_FAIL = "FAIL";
    
    /**出入金订单状态：处理中**/
    public static final String STATUS_DOING = "DOING";
    
    
    /**         rocketmq constants     **/

    /**签约主题**/
    public static final String MQ_TOPIC_CUSTOMER_SIGN = "USER_BANKSIGN_EVENT";
    
    public static final String MQ_TOPIC_TAR_CUSTOMER_SIGN = "userSignTar";
     
    
    
    public enum DailyTaskStatus{
		INIT("INIT","初始化"),
		DEAL_DONE("DEAL_DONE","完成");
		
		private final String value;
		private final String chs;
		
		private DailyTaskStatus(String value,String chs){
			this.value = value;
			this.chs = chs;
		}
		
		public String getValue() {
			return value;
		}
		public String getChs() {
			return chs;
		}
	}
    public enum DataDealStatus{
		INIT("INIT","初始化"),
		DEAL_SUCCESS("DEAL_SUCCESS","处理成功"),
		DEAL_FAIL("DEAL_FAIL","处理失败"),
		ROLL_BACK("ROLL_BACK","回退");
		
		private final String value;
		private final String chs;
		
		private DataDealStatus(String value,String chs){
			this.value = value;
			this.chs = chs;
		}
		
		public String getValue() {
			return value;
		}
		public String getChs() {
			return chs;
		}
	}
}
