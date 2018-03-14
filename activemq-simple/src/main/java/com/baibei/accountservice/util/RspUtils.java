package com.baibei.accountservice.util;

import com.alibaba.fastjson.JSONObject;
import com.baibei.accountservice.comm.ReturnCode;
import com.baibei.accountservice.comm.WebConstant;
import com.baibei.accountservice.paycenter.dto.BaseResponse;

public class RspUtils {
	public static <T> BaseResponse<T> success(T data) {
		BaseResponse<T> rsp = new BaseResponse<T>();
		rsp.setData(data);
		rsp.setMsg("success");
		rsp.setRc(ReturnCode.NORMAL_SUCCESS);
		return rsp;
	}

	public static <T> BaseResponse<T> success(T data,Long total) {
		BaseResponse<T> rsp = new BaseResponse<T>();
		rsp.setData(data);
		rsp.setMsg("success");
		rsp.setTotal(total);
		rsp.setRc(ReturnCode.NORMAL_SUCCESS);
		return rsp;
	}

	public static <T> BaseResponse<T> success() {
		BaseResponse<T> rsp = new BaseResponse<T>();
		rsp.setData(null);
		rsp.setMsg("success");
		rsp.setRc(ReturnCode.NORMAL_SUCCESS);
		return rsp;
	}

	public static <T> BaseResponse<T> error(String msg) {
		BaseResponse<T> rsp = new BaseResponse<T>();
		rsp.setData(null);
		rsp.setMsg(msg);
		rsp.setRc(ReturnCode.NORMAL_ERROR);
		return rsp;
	}

	public static String errorStr(String msg) {
		JSONObject json = new JSONObject();
		json.put(WebConstant.RETURN_CODE, ReturnCode.NORMAL_ERROR);
		json.put(WebConstant.MESSAGE, msg);
		return json.toJSONString();
	}

	public static String successStr(JSONObject data) {
		JSONObject json = new JSONObject();
		json.put("data", data);
		json.put(WebConstant.RETURN_CODE, ReturnCode.NORMAL_SUCCESS);
		json.put(WebConstant.MESSAGE, "success");
		return json.toJSONString();
	}

	public static String needLoginStr() {
		JSONObject json = new JSONObject();
		json.put(WebConstant.RETURN_CODE, ReturnCode.UN_LOGIN);
		json.put(WebConstant.MESSAGE, "need login");
		return json.toJSONString();
	}
}
