package com.hummer.api.web;

import com.hummer.api.dao.BizLogTableConfigDao;
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

    @GetMapping(value = "/config")
    @ResponseBody
    public Object queryAllConfig() {
        return configDao.getAllByTableName();
    }
}
