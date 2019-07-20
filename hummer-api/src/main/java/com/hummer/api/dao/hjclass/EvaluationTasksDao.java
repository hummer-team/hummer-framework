package com.hummer.api.dao.hjclass;

import com.hummer.api.po.hjclass.EvaluationTasksPo;
import com.hummer.dao.annotation.DaoAnnotation;
import com.hummer.dao.annotation.TargetDataSource;
import org.apache.ibatis.annotations.Param;

@DaoAnnotation

public interface EvaluationTasksDao {
    @TargetDataSource("HJ_Class")
    EvaluationTasksPo queryById(@Param("taskId")int taskId);
}
