
CREATE TABLE `tb_user_account` (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `bank_account` varchar(30) DEFAULT NULL,
  `account_balance` decimal(20,2) DEFAULT NULL,
  `version` int(30) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8