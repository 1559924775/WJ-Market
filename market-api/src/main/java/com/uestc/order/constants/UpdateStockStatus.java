package com.uestc.order.constants;

public enum UpdateStockStatus {
    SUCCESS("0"),
    CAS_FAIL("1"),//获取锁失败
    FAIL("3");
    private String code;
    private UpdateStockStatus(String code){
        this.code=code;
    }
}
