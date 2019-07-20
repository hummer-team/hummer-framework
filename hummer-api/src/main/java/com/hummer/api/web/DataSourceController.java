package com.hummer.api.web;

import com.hummer.api.dao.BizLogTableConfigDao;
import com.hummer.api.dao.CoursewareDaoMapper;
import com.hummer.api.dao.hjclass.EvaluationTasksDao;
import com.hummer.dao.annotation.TargetDataSourceTM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "/v1")
public class DataSourceController {

    @Autowired
    private BizLogTableConfigDao configDao;
    @Autowired
    private EvaluationTasksDao tasksDao;
    @Autowired
    private CoursewareDaoMapper coursewareDaoMapper;

    @GetMapping(value = "/course")
    @ResponseBody
    @TargetDataSourceTM(dbName = "hj_classs_courseware"
        ,transactionManager = "hj_classs_courseware_TM",timeout = 5)
    public Object getOne() {
        return coursewareDaoMapper.getOne();
    }

    @GetMapping(value = "/config")
    @ResponseBody
    public Object queryAllConfig() {
        return configDao.getAllByTableName();
    }

    @GetMapping(value = "/multiple")
    @ResponseBody
    public Object queryTwoDataSource(){
        Map<String,Object> map=new HashMap<>(2);
        map.put("ONE",coursewareDaoMapper.getOne());
        map.put("TWO",configDao.getAllByTableName());
        return map;
    }

    @GetMapping(value = "/task")
    @ResponseBody
    public Object queryTask(){
        return tasksDao.queryById(4566);
    }
}
