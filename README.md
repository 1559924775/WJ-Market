# WJ-Market
微服务架构电商网站下单支付和支付业务<br/>
开发工具：IDEA
依赖工具：spring-boot、mybatis、maven、redis、dubbo、rocketmq、mysql、tcc-trasaction、mycat

核心业务介绍：<br/>
下单业务：order<br/>
用户下单后从redis中取出对应的购物车列表（一个供应商对应一个购物车，所以是购物车列表），针对每一个购物车，
先用CAS操作冻结购物车中商品的库存，库存不足者将该商品的数量设为0。冻结库存后，将购物车中数据插入到订单表和订单详情表中，通过TCC-trasaction框架构建分布式事务。<br/>
insert:将数据状态设置为"inserting"<br/>
confirmInsert:将数据状态设置为"inserted",并将冻结的库存减掉<br/>
cancelInsert:删除该条数据，并将冻结的库存加回去<br/>
支付成功后，将订单状态设置为已支付，并向包裹应用投递顺序消息（创建包裹和发送物流通知）<br/>

支付业务：pay-user,pay-manager<br/>
使用RocketMQ提供的基于可靠性消息投递的分布式事务。本地事务：提取用户订单，找到用户的账户，由于存在多台机器登陆用户账户同时下单的小概率事件，使用CAS的方式更新账户金额。
并将信息100%的投递给pay-manager应用，增加平台的账户金额。消息投递成功后向下单应用发送支付成功信息。
用于多个pay-manager应用同时消费消息操作同一个平台账户，并发量高，使用分布式锁实现原子性。



