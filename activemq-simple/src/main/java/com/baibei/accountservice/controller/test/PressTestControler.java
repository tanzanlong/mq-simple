package com.baibei.accountservice.controller.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.comm.MybatisSqlExecuter;
import com.baibei.accountservice.dao.TAccountBalanceMapper;
import com.baibei.accountservice.dao.TAccountBalanceOnthewayMapper;
import com.baibei.accountservice.dao.TAccountMapper;
import com.baibei.accountservice.model.TAccount;
import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TAccountBalanceOntheway;
import com.baibei.accountservice.paycenter.dto.BaseResponse;
import com.baibei.accountservice.util.RspUtils;

@RestController
@EnableAutoConfiguration
@RequestMapping("/account/test")
public class PressTestControler {

    @Autowired
    TAccountMapper tAccountMapper;
    
    @Autowired
    TAccountBalanceMapper tAccountBalanceMapper;
    
    @Autowired
    TAccountBalanceOnthewayMapper tAccountBalanceontheWayMapper;
    
    @Autowired
    MybatisSqlExecuter mybatisSqlExecuter;
    
    @RequestMapping(value = "/testCreateAccountData")
    public BaseResponse<Boolean> testCreateAccountData(){
        long start = 100000000L;
        for(int j=0; j<3000; j++){
            long startTime = System.currentTimeMillis();
            List<TAccount> accountList = new ArrayList<TAccount>();
            List<TAccountBalance> accountBalanceList = new ArrayList<TAccountBalance>();
            for(int i=0; i<1000; i++){
                Date date = new Date();
                TAccount tAccount = new TAccount();
                tAccount.setAccountId(start + j *1000 + i);
                tAccount.setCreateTime(date);
                tAccount.setCustomerId(tAccount.getAccountId());
                tAccount.setIsDel(0);
                tAccount.setIsSign(0);
                tAccount.setOrgType("");
                tAccount.setSignAccountId("");
                tAccount.setSignChannel("");
                tAccount.setTopOrgId("");
                tAccount.setUpdateTime(date);
                tAccount.setUserId("" + tAccount.getAccountId());
                tAccount.setUserType("PERSONAL");
                accountList.add(tAccount);
                
                TAccountBalance balance = new TAccountBalance();
                balance.setAccountId(tAccount.getAccountId());
                balance.setAmount(100000000000000L);
                balance.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                balance.setCreateTime(date);
                balance.setUpdateTime(date);
                balance.setUserId(tAccount.getUserId());
                balance.setVersion(0L);
              
                TAccountBalance balance2 = new TAccountBalance();
                balance2.setAccountId(tAccount.getAccountId());
                balance2.setAmount(0L);
                balance2.setBalanceType(Constants.BALANCE_TYPE_FREEZON);
                balance2.setCreateTime(date);
                balance2.setUpdateTime(date);
                balance2.setUserId(tAccount.getUserId());
                balance2.setVersion(0L);
                accountBalanceList.add(balance);
                accountBalanceList.add(balance2);
            }
            tAccountMapper.insertBatch(accountList);
            tAccountBalanceMapper.insertBatch(accountBalanceList);
            System.out.println(System.currentTimeMillis() - startTime + " MS");
        }
        return RspUtils.success(true);
    }
    
    @RequestMapping(value = "/testCreateBalanceOnTheWay")
    public BaseResponse<Boolean> testCreateBalanceOnTheWay(){
        long start = 700000000000000000L;
        for(int j=0; j<10000; j++){
            long startTime = System.currentTimeMillis();
            List<TAccountBalanceOntheway> list = new ArrayList<TAccountBalanceOntheway>();
            for(int i=0; i<4000; i++){
                Date date = new Date();
                TAccountBalanceOntheway item = new TAccountBalanceOntheway();
                item.setAccountId(start + i);
                item.setAmount(10L);
                item.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                item.setCreateTime(date);
                item.setIsHandle(1);
                item.setMsgId(UUID.randomUUID().toString());
                item.setUpdateTime(date);
                item.setUserId("u_" + item.getAccountId() );
                list.add(item);
            }
            tAccountBalanceontheWayMapper.insertBatch(list);
            System.out.println(System.currentTimeMillis() - startTime + " MS");
        }
        return RspUtils.success(true);
    }
    
    @RequestMapping(value = "/testCreateBalanceOnTheWayJdbc")
    public BaseResponse<Boolean> testCreateBalanceOnTheWayJdbc() throws Exception{
       DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://192.168.212.25:3306/account?characterEncoding=UTF-8");
        dataSource.setUsername("account");
        dataSource.setPassword("account2017");
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        long start = 700000000000000000L;
        String sql = "insert into t_account_balance_ontheway (msg_id, balance_type, amount, account_id, user_id, is_handle, create_time, update_time) values (?,?,?,?,?,?,?,?)";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for(int j=0; j<10000; j++){
            //jdbcTemplate = new JdbcTemplate();
            //jdbcTemplate.setDataSource(dataSource);
            long startTime = System.currentTimeMillis();
            List<Object[]> objArrayList = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            sb.append("insert into t_account_balance_ontheway (msg_id, balance_type, amount, account_id, user_id, is_handle, create_time, update_time) values ");
            for(int i=0; i<4000; i++){
                Date date = new Date();
                TAccountBalanceOntheway item = new TAccountBalanceOntheway();
                item.setAccountId(start + i);
                item.setAmount(10L);
                item.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                item.setCreateTime(date);
                item.setIsHandle(1);
                item.setMsgId(UUID.randomUUID().toString());
                item.setUpdateTime(date);
                item.setUserId("u_" + item.getAccountId() );
                Object[] objArray = new Object[]{item.getMsgId(), item.getBalanceType(), item.getAmount(), item.getAccountId(), item.getUserId(), item.getIsHandle(), item.getCreateTime(), item.getUpdateTime()};
                objArrayList.add(objArray);
                if(i > 0){
                    sb.append(",");
                }
                sb.append("(");
                sb.append("'"); sb.append(item.getMsgId()); sb.append("'"); sb.append(",");
                sb.append("'"); sb.append(item.getBalanceType()); sb.append("'"); sb.append(",");
                sb.append(item.getAmount()); sb.append(",");
                sb.append(item.getAccountId()); sb.append(",");
                sb.append("'"); sb.append(item.getUserId()); sb.append("'"); sb.append(",");
                sb.append(item.getIsHandle()); sb.append(",");
                sb.append("'"); sb.append(df.format(item.getCreateTime())); sb.append("'");  sb.append(",");
                sb.append("'"); sb.append(df.format(item.getUpdateTime())); sb.append("'");
                sb.append(")");
              
            }
            //System.out.println(sb.toString());
            //jdbcTemplate.batchUpdate(sql, objArrayList);
            jdbcTemplate.execute(sb.toString());
            System.out.println(System.currentTimeMillis() - startTime + " MS");
        }
        return RspUtils.success(true);
    }
    
    @RequestMapping(value = "/testCreateBalanceOnTheWayNative")
    public BaseResponse<Boolean> testCreateBalanceOnTheWayNative() throws Exception{
       DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://192.168.212.25:3306/account?characterEncoding=UTF-8");
        dataSource.setUsername("account");
        dataSource.setPassword("account2017");
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        long start = 700000000000000000L;
        
        for(int j=0; j<10000; j++){
            //jdbcTemplate = new JdbcTemplate();
            //jdbcTemplate.setDataSource(dataSource);
            long startTime = System.currentTimeMillis();
            List<Object[]> objArrayList = new ArrayList<>();
            for(int i=0; i<4000; i++){
                Date date = new Date();
                TAccountBalanceOntheway item = new TAccountBalanceOntheway();
                item.setAccountId(start + i);
                item.setAmount(10L);
                item.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                item.setCreateTime(date);
                item.setIsHandle(1);
                item.setMsgId(UUID.randomUUID().toString());
                item.setUpdateTime(date);
                item.setUserId("u_" + item.getAccountId() );
                Object[] objArray = new Object[]{item.getMsgId(), item.getBalanceType(), item.getAmount(), item.getAccountId(), item.getUserId(), item.getIsHandle(), item.getCreateTime(), item.getUpdateTime()};
                objArrayList.add(objArray);
            }
            String sql = "insert into t_account_balance_ontheway (msg_id, balance_type, amount, account_id, user_id, is_handle, create_time, update_time) values";
            mybatisSqlExecuter.batchUpdate(sql, objArrayList);
            System.out.println(System.currentTimeMillis() - startTime + " MS");
        }
        return RspUtils.success(true);
    }
    
}
