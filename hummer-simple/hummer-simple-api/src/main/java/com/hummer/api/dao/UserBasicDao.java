package com.hummer.api.dao;

import com.hummer.api.po.UserBasicPo;
import com.hummer.dao.annotation.DaoAnnotation;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@DaoAnnotation
public interface UserBasicDao {
    List<UserBasicPo> querySingleUserBasicInfoByUserId(@Param("userId") Integer userId);
}
