package com.baibei.accountservice.util;

public class IdGenTools {

    private final long twepoch = 1420041600000L;
    
    public static void main(String[] args){
        IdGenTools t = new IdGenTools();
        System.out.println(t.next());
//        long start = System.currentTimeMillis();
//        for(int i=0; i<10000000; i++){
//            IDGenerator.next();
//        }
//        System.out.println(System.currentTimeMillis() - start + "MS");
    }
    
    public long next(){
        return (timeGen() - twepoch) << 22;
    }
    
    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
