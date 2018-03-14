package com.baibei.account.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BalancetypeRoleMap {
    

    /*******************余额类型********************************/
    /**余额类型：可用**/
    public static final String BALANCE_TYPE_AVALIABLE = "AVALIABLE";
    
    /**余额类型：冻结**/
    public static final String BALANCE_TYPE_FREEZON = "FREEZON";
    
    /**余额类型：交易手续费**/
    public static final String BALANCE_TYPE_POUNDAGE_TRADE = "POUNDAGE_TRADE";
    
    /**余额类型：融资融货手续费**/
    public static final String BALANCE_TYPE_POUNDAGE_FINANCING = "POUNDAGE_FINANCING";
    
    /**余额类型：融资利息**/
    public static final String BALANCE_TYPE_FINANCING_INTERESTS = "FINANCING_INTERESTS";
    
    /**
     * 交易中心  
     */
    public static String TYPE_CENTER = "CENTER";
    /**
     * 经纪商 经济会员
     */
    public static String TYPE_BROKER = "BROKER";
    /**
     * 交易基金   特会
     */
    public static String TYPE_SPECIAL = "SPECIAL";
    /**
     * 采购商  直营会员
     */
    public static String TYPE_DIRECT = "DIRECT";
    /**
     * 产业基金   融资机构
     */
    public static String TYPE_INDUSTRY = "INDUSTRY";
    /**
     * IT公司 
     */
    public static String TYPE_IT_COMPANY = "IT_COMPANY";
    /**余额类型：其它应付款**/
    public static final String BALANCE_TYPE_UNPAY = "UNPAY";

    
    public final static  Map<String,List<String>> balanceTypeDiction=new HashMap<String,List<String>>();
    static{
        List<String> personalBalanceType=new ArrayList<String>();
        personalBalanceType.add("AVALIABLE");
        personalBalanceType.add("FREEZON");
        personalBalanceType.add("UNPAY");
        personalBalanceType.add("LOAN");
        
        balanceTypeDiction.put("PERSONAL", personalBalanceType);
        List<String> centerBalanceType=new ArrayList<String>();
        centerBalanceType.add("AVALIABLE");
        centerBalanceType.add("FREEZON");
        centerBalanceType.add("UNPAY");
        centerBalanceType.add("LOAN");
        
        balanceTypeDiction.put("CENTER", personalBalanceType);
        List<String> brokerBalanceType=new ArrayList<String>();
        brokerBalanceType.add("AVALIABLE");
        brokerBalanceType.add("FREEZON");
        brokerBalanceType.add("UNPAY");
        brokerBalanceType.add("LOAN");
        balanceTypeDiction.put("BROKER", personalBalanceType);
     
        List<String> specialBalanceType=new ArrayList<String>();
        specialBalanceType.add("AVALIABLE");
        specialBalanceType.add("FREEZON");
        specialBalanceType.add("UNPAY");
        specialBalanceType.add("LOAN");
        balanceTypeDiction.put("SPECIAL", specialBalanceType);
        
        List<String> directBalanceType=new ArrayList<String>();
        directBalanceType.add("AVALIABLE");
        directBalanceType.add("FREEZON");
        directBalanceType.add("UNPAY");
        directBalanceType.add("LOAN");
        balanceTypeDiction.put("PROCUREMENT", directBalanceType);
        
        List<String> industryBalanceType=new ArrayList<String>();
        industryBalanceType.add("AVALIABLE");
        industryBalanceType.add("FREEZON");
        industryBalanceType.add("UNPAY");
        industryBalanceType.add("LOAN");
        balanceTypeDiction.put("INDUSTRY", industryBalanceType);
        
        List<String> itCompanyBalanceType=new ArrayList<String>();
        itCompanyBalanceType.add("AVALIABLE");
        itCompanyBalanceType.add("FREEZON");
        itCompanyBalanceType.add("UNPAY");
        itCompanyBalanceType.add("LOAN");
        balanceTypeDiction.put("IT_COMPANY", itCompanyBalanceType);
        
        
        List<String> clearCenterBalanceType=new ArrayList<String>();
        clearCenterBalanceType.add("AVALIABLE");
        clearCenterBalanceType.add("FREEZON");
        clearCenterBalanceType.add("UNPAY");
        clearCenterBalanceType.add("LOAN");
        balanceTypeDiction.put("CLEARING_SETTLEMENT", clearCenterBalanceType);
    }
    
}
