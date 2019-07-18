package com.hummer.api.web;

import com.hummer.api.dao.BizLogTableConfigDao;
import com.hummer.api.dao.hjclass.EvaluationTasksDao;
import com.hummer.dao.annotation.TargetDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/v1")
public class DataSourceController {

    @Autowired
    private BizLogTableConfigDao configDao;
    @Autowired
    private EvaluationTasksDao tasksDao;


    @TargetDataSource("hj_class_learning_biz_log")
    @GetMapping(value = "/config")
    @ResponseBody
    public Object queryAllConfig() {
        return configDao.getAllByTableName();
    }

    @TargetDataSource("HJ_Class")
    @GetMapping(value = "/task")
    @ResponseBody
    public Object queryTask(){
        return tasksDao.queryById(4566);
    }
}
