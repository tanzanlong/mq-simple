package com.baibei.accountservice.comm;

import java.util.UUID;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baibei.accountservice.config.SchedulerMasterConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SchedulerMasterCheck implements IZkDataListener{

    @Autowired
    SchedulerMasterConfig schedulerMasterConfig;
    
    private final String MASTER_NODE = "/accountservicemaster";
    
    //ZK客户端
    private ZkClient zk = null;
    
    //临时serverID
    private String serverId = UUID.randomUUID().toString().toLowerCase();
    
    public void init(){
        log.info("SchedulerMasterCheck zk = {}", schedulerMasterConfig.getZk());
        zk = new ZkClient(schedulerMasterConfig.getZk(), schedulerMasterConfig.getZkSessionTimeout(), schedulerMasterConfig.getZkSessionTimeout(), new SerializableSerializer());
        log.info("Temp serverId = {}", serverId);
        takeMaster();
        zk.subscribeDataChanges(MASTER_NODE, this);
    }
    
    //抢master
    private void takeMaster(){
        try{
            zk.createEphemeral(MASTER_NODE, serverId);
            log.info("serverId {} take master SUCCESS", serverId);
        }catch(ZkNodeExistsException e){//节点已存在
            log.info("serverId {} take master FAILURE", serverId);
            if(zk.readData(MASTER_NODE) == null){//在读取过程中，发现master节点已经被释放
                takeMaster();
            }
        }
    }
    
    public boolean isMaster(){
        try{
            String data = zk.readData(MASTER_NODE);
            if(serverId.equalsIgnoreCase(data)){
                return true;
            }
        }catch(Exception e){
            log.error(e.getMessage());
        }
        return false;
    }

    @Override
    public void handleDataChange(String arg0, Object arg1) throws Exception {
    }

    @Override
    public void handleDataDeleted(String arg0) throws Exception {
        log.info("Old Master is down, Take master...");
        takeMaster();
    }
}
