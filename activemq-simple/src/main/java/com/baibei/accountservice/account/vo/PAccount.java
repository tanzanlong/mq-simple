package com.baibei.accountservice.account.vo;

import java.io.UnsupportedEncodingException;

import com.baibei.accountservice.paycenter.utill.MD5;

public class PAccount {

    public static void main(String[] args){
        for(int i=0; i<99999999; i++){
            try {
                MD5.getHash(i + "", "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
