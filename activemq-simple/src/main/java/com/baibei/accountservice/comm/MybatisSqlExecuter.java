package com.baibei.accountservice.comm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baibei.accountservice.dao.TAccountBalanceOnthewayMapper;

/**
 * MyBatis在执行单线程批量任务时，使用foreach效率不高，使用原生sql效率能提升10倍，故有此类
 * @author peng
 */
@Component
public class MybatisSqlExecuter {

    @Autowired
    TAccountBalanceOnthewayMapper tAccountBalanceontheWayMapper;
    
    public void batchUpdate(String sql, List<Object[]> osList){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(sql);
        sb.append(" ");
        for(int j=0; j<osList.size();  j++){
            if(j > 0){
                sb.append(",");
            }
            Object[] os = osList.get(j);
            sb.append("(");
            for(int i=0; i<os.length; i++){
                if(i > 0){
                    sb.append(",");
                }
                if(os[i] instanceof Number){
                    sb.append(os[i]);
                }else if(os[i] instanceof Date){
                    sb.append("'");
                    sb.append(df.format(os[i]));
                    sb.append("'");
                }else{
                    sb.append("'");
                    sb.append(os[i]);
                    sb.append("'");
                }
            }
            sb.append(")");
        }
        SqlVo sqlVo = new SqlVo();
        sqlVo.setSql(sb.toString());
        tAccountBalanceontheWayMapper.insertBySql(sqlVo);
    }
    
    public static void main(String[] args){
        MybatisSqlExecuter mybatisSqlExecuter = new MybatisSqlExecuter();
        String sql = "insert into t_account_balance_ontheway (msg_id, balance_type, amount, account_id, user_id, is_handle, create_time, update_time) values";
        Object[] os = new Object[]{"1","2",3,4,"5",1,new Date(), new Date()};
        List<Object[]> osList = new ArrayList<>();
        osList.add(os);
        osList.add(os);
        mybatisSqlExecuter.batchUpdate(sql, osList);
    }
}
