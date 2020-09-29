package com.hummer.api.web;


import com.hummer.api.dao.CoursewareDaoMapper;
import com.hummer.api.dao.UserBasicDao;
import com.hummer.api.dao.hjclass.EvaluationTasksDao;
import com.hummer.api.po.CoursewarePo;
import com.hummer.api.po.UserBasicPo;
import com.hummer.api.po.hjclass.EvaluationTasksPo;
import com.hummer.dao.annotation.TargetDataSourceTM;
import com.hummer.rest.model.ResourceResponse;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/v1")
public class DataSourceController {

    @Autowired(required = false)
    private EvaluationTasksDao tasksDao;
    @Autowired(required = false)
    private CoursewareDaoMapper coursewareDaoMapper;
    @Autowired
    private UserBasicDao userBasicDao;

    @GetMapping("/user-info/{userId}")
    public ResourceResponse<List<UserBasicPo>> getUser(@PathVariable("userId") Integer userId) {
        return ResourceResponse.ok(userBasicDao.querySingleUserBasicInfoByUserId(userId));
    }


    @GetMapping(value = "/course")
    @ResponseBody
    @TargetDataSourceTM(dbName = "hj_classs_courseware"
        , transactionManager = "hj_classs_courseware_TM", timeout = 5)
    public ResourceResponse<Collection<CoursewarePo>> getOne() {
        return ResourceResponse.ok(coursewareDaoMapper.getOne());
    }

    @GetMapping(value = "/config")
    @ResponseBody
    public Object queryAllConfig() {
        return null;
        //return configDao.getAllByTableName();
    }

    @GetMapping(value = "/multiple")
    @ResponseBody
    public Object queryTwoDataSource() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("ONE", coursewareDaoMapper.getOne());
        //map.put("TWO", configDao.getAllByTableName());
        return map;
    }

    @GetMapping(value = "/task")
    @ResponseBody
    public ResourceResponse<EvaluationTasksPo> queryTask() {
        return ResourceResponse.ok(tasksDao.queryById(4566));
    }


    @GetMapping(value = "/course_set")
    public ResourceResponse setCourseware(@RequestParam("coursewareId") int coursewareId) {
        coursewareDaoMapper.setCourseware(String.format("%s-%s"
            , DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(DateTime.now()), "test")
            , Double.valueOf(Math.random() * 100d).intValue()
            , coursewareId);

        return ResourceResponse.ok(coursewareDaoMapper.getOneById(coursewareId));
    }
}
