package com.kelab.usercenter.dal.dao;

import com.kelab.usercenter.dal.model.UserInfoModel;
import org.apache.ibatis.annotations.Param;

public interface UserInfoMapper {

    UserInfoModel queryById(@Param("id") Integer id);

}