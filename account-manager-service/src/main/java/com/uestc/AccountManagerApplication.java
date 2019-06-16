package com.uestc;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.uestc.dao")
@EnableDubboConfiguration
@SpringBootApplication
public class AccountManagerApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(AccountManagerApplication.class, args);
		
	}
	
}
