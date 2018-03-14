package com.baibei.account.dto.notify;

import lombok.Data;

@Data
public class PaySuccessNotify {
 private String userID;
 private Long totalMoney;
 private Long changeMoney;
 private String type;
}
