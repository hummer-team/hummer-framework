package com.hummer.test.dao;

import com.hummer.dao.annotation.DaoAnnotation;
import com.hummer.test.po.UserCouponPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DaoAnnotation
public interface UserCouponDao {
    List<UserCouponPo> queryBuIds(@Param("ids")List<Integer> ids);

    double maxUsedMoney();

    String top10UserId();
}
