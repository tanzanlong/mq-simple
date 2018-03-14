package com.baibei.accountservice.settlement.controller.notifyrec;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;
import com.baibei.accountservice.paycenter.dto.BaseResponse;
import com.baibei.accountservice.paycenter.vo.DailySettlementNotify;
import com.baibei.accountservice.settlement.business.DailySettlementNotifyBusiness;


/**
 * 清算结果文件已生成异步通知接口
 * 
 * @author lich
 */
@RestController
@RequestMapping("/account/dailySettlementNotify")
public class DailySettlementNotifyController {

	static final Logger logger = LoggerFactory.getLogger(DailySettlementNotifyController.class);

	@Autowired
	DailyTaskConfig dailyTaskConfig;
	
	@Autowired
	DailySettlementNotifyBusiness dailySettlementNotifyBusiness;

	@RequestMapping(method = RequestMethod.POST, value = "/receiveSettlementRes")
	public BaseResponse<Boolean> receiveSettlementRes(@RequestBody DailySettlementNotify dailySettlementNotify,HttpServletRequest request) {
		BaseResponse<Boolean> response = new BaseResponse<Boolean>();
		logger.info("receiveSettlementRes request {}", dailySettlementNotify);
		try {
			// 参数检查
			checkParam(dailySettlementNotify);

			dealSettlementRes(dailySettlementNotify);

			// 组织返回结果
			Boolean settlementRollBackRes = true;
			response.setRc(BaseResponse.RC_SUCCESS);
			response.setData(settlementRollBackRes);

		} catch (Exception e) {
			response.setRc(BaseResponse.RC_FAIL);
			response.setMsg(e.getMessage());
		}
		return response;
	}

	// 参数检查
	private void checkParam(DailySettlementNotify dailySettlementNotify) {
		if (StringUtils.isBlank(dailySettlementNotify.getFileName())) {
			throw new IllegalArgumentException("parameter fileName can not be blank");
		}
		if (StringUtils.isBlank(dailySettlementNotify.getFileType())) {
			throw new IllegalArgumentException("parameter fileType can not be blank");
		}
		if (StringUtils.isBlank(dailySettlementNotify.getSign())) {
			throw new IllegalArgumentException("parameter sign can not be blank");
		}
	}

	// 处理结算结果文件
	public void dealSettlementRes(DailySettlementNotify dailySettlementNotify)
			throws Exception {
		String appSecret = dailyTaskConfig.getAppSecret();
		
		String fileName = dailySettlementNotify.getFileName();
		String fileType = dailySettlementNotify.getFileType();
		String sign = dailySettlementNotify.getSign();
		
		dailySettlementNotify.fillSign(appSecret);
		String newSign = dailySettlementNotify.getSign();
		
		// 校验报文
		if (sign.equals(newSign)) {
			dailySettlementNotifyBusiness.dealSettlementRes(fileName,fileType);
		} else {
			throw new IllegalArgumentException("sign invalid");
		}
	}
}
