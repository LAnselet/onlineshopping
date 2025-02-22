package com.qiuzhitech.onlineshopping.db.dao.impl;

import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingUserDao;
import com.qiuzhitech.onlineshopping.db.mappers.OnlineShoppingUserMapper;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingUser;

import org.springframework.stereotype.Repository;

import java.util.List;

import javax.annotation.Resource;


@Repository
public class OnlineShoppingUserDaoImpl implements OnlineShoppingUserDao {

  @Resource
  OnlineShoppingUserMapper userMapper;

  @Override
  public int deleteUserById(Long userId) {
    return 0;
  }

  @Override
  public int insertUser(OnlineShoppingUser user) {
    return userMapper.insert(user);
  }

  @Override
  public OnlineShoppingUser queryUserById(Long userId) {
    return null;
  }

  @Override
  public int updateUser(OnlineShoppingUser user) {
    return 0;
  }

  @Override
  public List<OnlineShoppingUser> listAllUsers() {
    return userMapper.listAllUsers();
  }
}
