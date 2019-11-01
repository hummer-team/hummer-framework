package com.hummer.api.dao;

import com.hummer.api.po.OrderPo;
import com.hummer.dao.annotation.DaoAnnotation;
import com.hummer.dao.annotation.TargetDataSource;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/11/1 17:54
 **/
@DaoAnnotation
public interface OrderDaoMapper {
    @TargetDataSource("hj_classs_courseware")
    int save (@Param("orderPo")OrderPo orderPo);
}
