package com.projn.sample.alps.module.console.dao;

import com.projn.sample.alps.module.console.domain.UserInfo;

public interface IUserInfoDao {
    int deleteByPrimaryKey(String account);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(String account);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
}