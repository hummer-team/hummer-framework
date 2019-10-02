package com.hummer.api.dao;


import com.hummer.api.po.CoursewarePo;
import com.hummer.dao.annotation.DaoAnnotation;
import com.hummer.dao.annotation.TargetDataSource;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/18 15:58
 **/
@DaoAnnotation

public interface CoursewareDaoMapper {
    /**
     * query all table configuration
     *
     * @return BizLogTableConfigPo
     * @author liguo
     * @date 2018/12/18 15:59
     * @version 1.0.0
     **/
    @TargetDataSource("hj_classs_courseware")
    Collection<CoursewarePo> getOne();

    @TargetDataSource("hj_classs_courseware")
    int setCourseware(@Param("coursewareName") String coursewareName
        , @Param("playTime") int playTime
        , @Param("coursewareId") int coursewareId);

    @TargetDataSource("hj_classs_courseware")
    CoursewarePo getOneById(@Param("coursewareId") int coursewareId);
}
