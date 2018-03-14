package com.baibei.accountservice.paycenter.vo;

import java.io.UnsupportedEncodingException;

import com.baibei.accountservice.paycenter.utill.MD5;

import lombok.Getter;
import lombok.Setter;

/**
 * 清算结果文件已生成异步通知
 * 
 * @author peng
 *
 */
public class DailySettlementNotify {

	@Setter
	@Getter
	private String fileType;

	@Setter
	@Getter
	private String fileName;

	@Setter
	@Getter
	private String sign;

	public void fillSign(String appSecret) {
		StringBuilder sb = new StringBuilder();
		sb.append("fileName=");
		sb.append(fileName);
		sb.append("fileType=");
		sb.append(fileType);
		sb.append(appSecret);
		try {
			this.sign = MD5.getHashString(sb.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}