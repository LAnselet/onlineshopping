package com.qiuzhitech.onlineshopping;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.qiuzhitech.onlineshopping.db.mappers")
@ComponentScan(basePackages = "com.qiuzhitech")
public class OnlineshoppingApplication {

  public static void main(String[] args) {
    SpringApplication.run(OnlineshoppingApplication.class, args);
  }

}
