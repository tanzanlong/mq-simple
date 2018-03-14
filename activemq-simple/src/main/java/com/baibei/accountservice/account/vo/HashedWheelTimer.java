package com.baibei.accountservice.account.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HashedWheelTimer extends TimerTask {

    private static int period = 1000;
    
    private static int slotNum = 3600;
    
    private int currentIndex;
    
    public List<Slot> slotList;
    
    @Override
    public void run() {
        currentIndex++;
        if(currentIndex >= slotNum){
            currentIndex = 0;
        }
    }
    
    public void start(){
        slotList = new ArrayList<Slot>(slotNum);
        Timer timer = new Timer();  
        timer.schedule(this, 0L, period);
    }
    
    private int calcSlotIndex(long time){
        return (currentIndex + 1 + (int)((time/period)%slotNum)) % slotNum;
    }
    
    public Slot getTargetSlot(long time){
        return slotList.get(calcSlotIndex(time));
    }
    
    public Slot getCurrentSlot(){
        return slotList.get(currentIndex);
    }

    public static class Slot<T>{
        
       
    }
    
    public static void main(String[] args){
        HashedWheelTimer task = new HashedWheelTimer();
        task.start();
    }
    
}
