package com.hummer.api.web;

//import com.hummer.spring.plugin.context.SpringApplicationContext;
import com.hummer.spring.plugin.context.PropertiesContainer;
import com.hummer.spring.plugin.context.SpringApplicationContext;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by MRomeh on 08/08/2017.
 */
@RestController
@RequestMapping("/application")
@Api(value = "Applciation demo")
public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);


    @Value("${test.A}")
    private int value;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getAllAlerts() {

        log.debug("Trying to retrieve all alerts");
        log.info("---------------------{}---------------------", SpringApplicationContext.getBean("demo"));
        log.info("*********************{}*********************",value);
        log.info("+++++++++++++++++++++{}+++++++++++++++++++++", PropertiesContainer.get("test.A",String.class));
       return PropertiesContainer.get("test.A",String.class);

    }
}
