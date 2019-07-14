package com.hummer.api.dao.hjclass;

import com.hummer.api.po.hjclass.EvaluationTasksPo;
import com.hummer.dao.annotation.DaoAnnotation;
import org.apache.ibatis.annotations.Param;

@DaoAnnotation
public interface EvaluationTasksDao {
    EvaluationTasksPo queryById(@Param("taskId")int taskId);
}
