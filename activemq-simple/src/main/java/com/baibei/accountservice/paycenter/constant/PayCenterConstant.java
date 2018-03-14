package com.baibei.accountservice.paycenter.constant;

public class PayCenterConstant {
    /**
     * 账户未签约标识 
     */
    public static final int ACCOUNT_IS_SIGN_NO = 0;
    /**
     * 账户已签约标识 
     */
    public static final int ACCOUNT_IS_SIGN_YES = 1;
    
    /**
     * 账户未删除标识 
     */
    public static final int ACCOUNT_IS_DELETE_NO = 0;
    /**
     * 账户已删除标识 
     */
    public static final int ACCOUNT_IS_DELETE_YES = 1;
    
    /**
     * 账户不可入金标识
     */
    public static final int ACCOUNT_IS_CAN_NO_RECHARGE = 1;
    /**
     * 账户可入金标识
     */
    public static final int ACCOUNT_IS_CAN_RECHARGE = 0;
    
    
    /**
     * 账户不可出 金标识
     */
    public static final int ACCOUNT_IS_CAN_NO_WITHDRAW = 1;
    /**
     * 账户可出 金标识
     */
    public static final int ACCOUNT_IS_CAN_WITHDRAW = 0;
    
    
      /**出入金订单状态：成功**/
    public static final String STATUS_SUCCESS = "SUCCESS";
    
    /**出入金订单状态：失败**/
    public static final String STATUS_FAIL = "FAIL";
    
    /**出入金订单状态：处理中**/
    public static final String STATUS_DOING = "DOING";
    
    /**渠道处理状态:请求**/
    public static final String HANDLE_STATUS_INIT = "INIT";
    /**渠道处理状态:请求**/
    public static final String HANDLE_STATUS_REQUEST = "REQUEST";
    
    /**渠道处理状态:响应**/
    public static final String HANDLE_STATUS_RESPONSE = "RESPONSE";
    
    /**可用余额**/
    public static final String ACCOUNT_BALANCE_TYPE_USEABLE = "AVALIABLE";
    
    /**冻结余额**/
    public static final String ACCOUNT_BALANCE_TYPE_FREEZON = "FREEZON";
    
    /**融资余额**/
    public static final String ACCOUNT_BALANCE_TYPE_LOAN = "LOAN";
    
    /**其它应付款**/
    public static final String ACCOUNT_BALANCE_TYPE_UNPAY = "UNPAY";
    
    /**已结算**/
    public static final String ACCOUNT_BALANCE_TYPE_SETTLED = "settled";
    
    /** 余额对账文件名前缀 **/
    public static final String DAILY_BALANCE_FILENAME_PREFIX = "BAT_BALANCE_";
    /** 结算对账文件名前缀 **/
    public static final String DAILY_SETTLEMENT_FILENAME_PREFIX = "BAT_TRADE_";
    /** 结算券对账文件名前缀 **/
    public static final String DAILY_SETTLEMENTTICKET_FILENAME_PREFIX = "BAT_TRADE_TICKET_";
    /** 出入金对账文件名前缀 **/
    public static final String DAILY_PAYCENTER_FILENAME_PREFIX = "BAT_PAYCENTER_";
    /**入金标识**/
    public static final String DAILY_RECHARGE = "IN";
    /**出金标识**/
    public static final String DAILY_WITHDRAW = "OUT";
    
    /** 对账请求标识 **/
    public static final String DAILY_REQ_FLAG = "REQ_";
    
    /** 组装报文页面大小 **/
    public static final Integer DAILY_PAGESIZE = 10000;
    
    /**Redis Channel 入金结果**/
    public static final String CHANNEL_RECHARGE_RESULT = "CHANNEL:RECHARGE:RESULT";
    
    /**Redis Channel 出金结果**/
    public static final String CHANNEL_WITHDRAW_RESULT = "CHANNEL:WITHDRAW:RESULT";

    /**业务系统标识**/
    public static final String BUSINESS_TYPE_CB = "CB";

    /**中南快捷支付(代收)-编号**/
    public static final String PAY_CODE_ZNPAYH5 = "ZNPAYH5";

    /**中南代付-编号**/
    public static final String PAY_CODE_ZNDEFRAY = "ZNDEFRAY";
    
    public enum DailyTaskType{
		
		SETTLEMENT("SETTLEMENT","日清结算"),
		BALANCE("BALANCE","余额对账"),
		PAYCENTER("PAYCENTER","出入金对账");
		
		private final String value;
		private final String chs;
		
		private DailyTaskType(String value,String chs){
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

	public enum NotifyFileType{
		
		SETTLEMENT("1","日结清算文件"),
		BALANCE("2","余额对账文件"),
		PAYCENTER("3","出入金对账文件");
		
		private final String value;
		private final String chs;
		
		private NotifyFileType(String value,String chs){
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
