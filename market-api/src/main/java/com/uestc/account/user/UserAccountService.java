package com.uestc.account.user;

import com.uestc.account.user.vo.PayResultVO;

import java.math.BigDecimal;

public interface UserAccountService {

  PayResultVO payment(String userId, String orderId, String accountId, BigDecimal money,String token);

}
