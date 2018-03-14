package com.baibei.accountservice.paycenter.common;

public class Constants {

    /**角色：普通用户**/
    public static Integer ROLE_NORMAL_USER = 1;
    
    /**角色：酒项目普通用户**/
    public static Integer ROLE_NORMAL_USER_WINE = 101;
 
    /**审核状态：未审核**/
    public static final String AUDIT_STATUS_NEW = "NEW";
    
    /**审核状态：待审核**/
    public static final String AUDIT_STATUS_AUDITING = "AUDITING";
    
    /**审核状态：成功**/
    public static final String AUDIT_STATUS_SUCCESS = "SUCCESS";
    
    /**审核状态：失败**/
    public static final String AUDIT_STATUS_FAIL = "FAIL";
    

	
	/** 余额对账文件名前缀 **/
    public static final String DAILY_BALANCE_FILENAME_PREFIX = "BAT_BALANCE_";
    /** 结算对账文件名前缀 **/
    public static final String DAILY_SETTLEMENT_FILENAME_PREFIX = "BAT_TRADE_";
    
    /** 对账业务类型 **/
    public static final String DAILY_BUSINESSTYPE = "WINE";
    
    /** 对账请求标识 **/
    public static final String DAILY_REQ_FLAG = "REQ_";
    
    /** 对账响应标识 **/
    public static final String DAILY_RESULT_FLAG = "RESULT_";
    
    /** 对账页面大小 **/
    public static final Integer DAILY_PAGESIZE = 10000;
    
    /** 平安-余额对账文件名前缀 **/
	public static final String DAILY_BALANCE_FILENAME_HEADER = "BatCustDz";
	
	/** 平安-开销户对账文件名前缀 **/
	public static final String DAILY_CREATE_DROP_ACCOUNT_FILENAME_HEADER = "KXH";
	
	/** 平安-开销户对账文件名ThirdLogNo **/
	public static final String DAILY_CREATE_DROP_ACCOUNT_THIRDLOGNO = "020120928";

	/** 平安-交易网代码（4位） **/
	public static final String DAILY_BUSINESS_CODE = "WINE";
	
	/** 结算完成通知_topic **/
	public static final String SETTLEMENT_DONE_TOPIC = "USER_SETTLERESULT_EVENT";
    
	public enum DailyTaskType{
		
		SETTLEMENT("SETTLEMENT","日清结算"),
		SETTLEMENTTICKET("SETTLEMENTTICKET","日券清结算"),
		BALANCE("BALANCE","余额对账"),
		PAYCENTER("PAYCENTER","出入金对账"),
		PINGAN_BALANCE_RECONCILIATION("PINGAN_BALANCE_RECONCILIATION","平安银行余额对账"),
		PINGAN_ACCOUNT_RECONCILIATION("PINGAN_ACCOUNT_RECONCILIATION","平安银行开销户对账");
		
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
	
	public enum DailyTaskStatus{
		INIT("INIT","初始化"),
		INVOKE_SUCCESS("INVOKE_SUCCESS","调用银行接口成功"),
		ALREADY_GENERATED_FILE("ALREADY_GENERATED_FILE","对账文件已经生成"),
		STORED_IN("STORED_IN","已入库"),
		DEAL_SUCCESS("DEAL_SUCCESS","处理成功"),
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
		DEAL_FAIL("DEAL_FAIL","处理失败");
		
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
	public enum DataFailStatus{
		AMOUNT_NOT_EQUAL("AMOUNT_NOT_EQUAL","金额不一致"),
		NO_INFO("NO_INFO","业务系统指定的账户记录不存在");
		
		private final String value;
		private final String chs;
		
		private DataFailStatus(String value,String chs){
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
