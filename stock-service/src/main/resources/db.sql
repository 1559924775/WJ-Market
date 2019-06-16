CREATE TABLE `tb_stock` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '库存ID',
  `goods_id` varchar(32) DEFAULT NULL COMMENT '商品ID',
  `seller_id` varchar(32) DEFAULT NULL COMMENT '商家ID',
  `goods_name` varchar(40) DEFAULT NULL COMMENT '商品名称',
  `stock` int(10) DEFAULT NULL COMMENT '库存',
  `version` int(10) DEFAULT NULL COMMENT '版本',
  `creat_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8