package com.qiuzhitech.onlineshopping.db.mappers;

import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingUser;

import java.util.List;

public interface OnlineShoppingUserMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(OnlineShoppingUser record);

    int insertSelective(OnlineShoppingUser record);

    OnlineShoppingUser selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(OnlineShoppingUser record);

    int updateByPrimaryKey(OnlineShoppingUser record);

    List<OnlineShoppingUser> listAllUsers();
}