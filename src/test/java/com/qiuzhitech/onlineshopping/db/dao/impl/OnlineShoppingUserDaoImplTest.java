package com.qiuzhitech.onlineshopping.db.dao.impl;

import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingUserDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingUser;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@SpringBootTest
class OnlineShoppingUserDaoImplTest {

  @Resource
  OnlineShoppingUserDao userDao;


  @Test
  void deleteUserById() {

  }

  @Test
  void insertUser() {
    OnlineShoppingUser user = OnlineShoppingUser.builder()
            .userId(2L)
            .userType(2)
            .name("张三")
            .email("zhangsan@gmail.com")
            .address("zhangsan")
            .phone("1234567")
            .build();

    userDao.insertUser(user);
  }

  @Test
  void queryUserById() {
  }

  @Test
  void updateUser() {
  }

  @Test
  void listAllUsers() {
    log.info(userDao.listAllUsers().toString());
  }
}