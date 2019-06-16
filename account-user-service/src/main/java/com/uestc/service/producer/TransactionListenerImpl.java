package com.uestc.service.producer;

import com.uestc.dao.TbUserAccountMapper;
import com.uestc.domain.TbUserAccount;
import com.uestc.domain.TbUserAccountExample;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
//实现本地事务和check会查
public class TransactionListenerImpl implements TransactionListener {

	@Autowired
	private TbUserAccountMapper tbUserAccountMapper;
	
	@Override  //Message msg, Object arg从transactionProducer.sendMessage(message, params);传过来的
	public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
		System.err.println("执行本地事务单元------------");
		CountDownLatch currentCountDown = null;
		try {
			Map<String, Object> params = (Map<String, Object>) arg;
			String userId = (String)params.get("userId");
			String accountId = (String)params.get("accountId");
			String orderId = (String)params.get("orderId");
			BigDecimal payMoney = (BigDecimal)params.get("payMoney");	//	当前的支付款
			BigDecimal newBalance = (BigDecimal)params.get("newBalance");	//	前置扣款成功的余额
			int currentVersion = (int)params.get("currentVersion");
			currentCountDown = (CountDownLatch)params.get("currentCountDown");
		
			//updateBalance 传递当前的支付款 数据库操作: 
			Date currentTime = new Date();
			TbUserAccount tbUserAccount=new TbUserAccount();
			tbUserAccount.setAccountBalance(newBalance);
			tbUserAccount.setVersion(currentVersion+1);
			tbUserAccount.setUpdateTime(currentTime);
			TbUserAccountExample example=new TbUserAccountExample();
			TbUserAccountExample.Criteria criteria=example.createCriteria();
			criteria.andIdEqualTo(accountId);
			criteria.andVersionEqualTo(currentVersion);//CAS操作
			int count = this.tbUserAccountMapper.updateByExampleSelective(tbUserAccount,example);
			if(count == 1) {
				currentCountDown.countDown();
				return LocalTransactionState.COMMIT_MESSAGE;
			} else if(count==0){
				//cas失败，需要重试。小概率事件
				int cnt=0;
				while(count==0&&cnt<5){
					List<TbUserAccount> tbUserAccounts = tbUserAccountMapper.selectByExample(example);
					TbUserAccount old=tbUserAccounts.get(0);
					currentVersion = old.getVersion();
					BigDecimal currentBalance = old.getAccountBalance();
					newBalance=currentBalance.subtract(payMoney);
					tbUserAccount.setAccountBalance(newBalance);
					if(newBalance.doubleValue()>0){
						//还是之前的tbUserAccount,但要改变版本号。
						tbUserAccount.setVersion(currentVersion+1);
						criteria.andIdEqualTo(accountId);
						criteria.andVersionEqualTo(currentVersion);
						count = this.tbUserAccountMapper.updateByExampleSelective(tbUserAccount,example);
					}else{
						//余额不足
						return LocalTransactionState.ROLLBACK_MESSAGE;
					}
					Thread.sleep(500);
				}
				currentCountDown.countDown();
				if(count==1)
					return LocalTransactionState.COMMIT_MESSAGE;
				return LocalTransactionState.ROLLBACK_MESSAGE;
			}
		} catch (Exception e) {
			e.printStackTrace();
			currentCountDown.countDown();
			return LocalTransactionState.ROLLBACK_MESSAGE;
		}
		return  LocalTransactionState.UNKNOW;
	}

	@Override
	//如果消息处于LocalTransactionState.UNKNOW中间状态，需要调用这个方法，
	//如果上面返回rollback或commit，就不会会查了。
	//可以返回LocalTransactionState.COMMIT_MESSAGE重新投递消息。
	public LocalTransactionState checkLocalTransaction(MessageExt msg) {
		// TODO Auto-generated method stub
		return LocalTransactionState.COMMIT_MESSAGE;
	}

}
