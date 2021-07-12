package com.hummer.api.web;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.hummer.api.dto.CacheTestReqDto;
import com.hummer.api.dto.KafkaMessageReq;
import com.hummer.cache.plugin.HummerSimpleObjectCache;
import com.hummer.cache.plugin.HummerSimpleObjectCacheKey;
import com.hummer.common.http.HttpAsyncClient;
import com.hummer.common.http.RequestCustomConfig;
import com.hummer.common.utils.ObjectCopyUtils;
import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import com.hummer.local.persistence.plugin.LocalPersistence;
import com.hummer.message.facade.publish.MessageBus;
import com.hummer.message.facade.publish.PublishCallback;
import com.hummer.rest.annotations.BindRestParameterSimpleModel;
import com.hummer.rest.model.ResourceResponse;
import com.hummer.rest.utils.ParameterAssertUtil;
import com.sun.management.OperatingSystemMXBean;
import comm.hummer.simple.common.module.QueryStringDto;
import io.swagger.annotations.Api;
import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.management.ManagementFactory;
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.Map;

/**
 * Created by MRomeh on 08/08/2017.
 */
@RestController
@RequestMapping("/v1")
@Api(value = "Applciation demo")
@Validated
public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);
    @Autowired
    @Qualifier("RocksDbPersistence")
    private LocalPersistence persistence;

    //    @Value("${test.A}")
    private int value;

    @PostMapping(value = "/local/store")
    public ResourceResponse<Map<String, Object>> storeAndGet(@RequestBody Map<String, Object> map) {
        persistence.put(map.get("_columnFamily").toString(), map.get("_key").toString(), JSON.toJSONBytes(map));
        Map<String, Object> val = JSON.parseObject(persistence.get(map.get("_columnFamily").toString()
            , map.get("_key").toString()), Map.class);
        return ResourceResponse.ok(val);
    }


    @GetMapping(value = "/local/store/list")
    public ResourceResponse<Map<String, Object>> getList(@RequestParam("columnFamily") String columnFamily
        , @RequestParam("key") String key) {

        Map<String, Object> val = JSON.parseObject(persistence.get(columnFamily
            , key), Map.class);
        return ResourceResponse.ok(val);
    }

    @GetMapping(value = "/all", produces = "application/json")
    @ResponseBody
    public String getAllAlerts() {

        log.debug("Trying to retrieve all alerts");
        log.info("---------------------{}---------------------", SpringApplicationContext.getBean("demo"));
        log.info("*********************{}*********************", value);
        log.info("+++++++++++++++++++++{}+++++++++++++++++++++", PropertiesContainer.get("test.A", String.class));
        return PropertiesContainer.get("test.A", String.class);

    }

    @GetMapping(value = "/query")
    public ResourceResponse<QueryStringDto> queryStringParse(@BindRestParameterSimpleModel @Valid
                                                                 QueryStringDto queryStringDto
        , Errors errors) {
        ParameterAssertUtil.assertRequestFirstValidated(errors);
        return ResourceResponse.ok(ObjectCopyUtils.copy(queryStringDto, QueryStringDto.class));
    }

    @GetMapping(value = "/query2")
    public ResourceResponse<QueryStringDto> queryStringParse2(@ModelAttribute @Valid
                                                                  QueryStringDto queryStringDto
        , Errors errors) {
        ParameterAssertUtil.assertRequestFirstValidated(errors);
        return ResourceResponse.ok(ObjectCopyUtils.copy(queryStringDto, QueryStringDto.class));
    }

    @GetMapping(value = "/query3")
    public ResourceResponse<QueryStringDto> queryStringParse3(@RequestParam
                                                              @NotEmpty(message = "uuId can't null")
                                                                  String uuId,
                                                              @RequestParam
                                                              @NotNull(message = "class id can't null")
                                                                  Integer classId,
                                                              @RequestParam
                                                              @NotNull(message = "class id can't null")
                                                                  Collection<String> users
    ) {
        QueryStringDto queryStringDto = new QueryStringDto();
        queryStringDto.setClassId(classId);
        queryStringDto.setUuId(uuId);
        queryStringDto.setUsers(users);
        return ResourceResponse.ok(queryStringDto);
    }


    @PostMapping(value = "/message_bus")
    public ResourceResponse sendMessage(@RequestBody KafkaMessageReq req) {
        MessageBus
            .builder()
            .topicId("test")
            .body(req)
            .callback(new PublishCallback() {
                @Override
                public void success(int partition, long offset) {
                   log.info("send message done");
                }
            })
            .kafka(MessageBus.Kafka.builder().topicId("log-type-group-out2").build())
            .messageKey(req.getId())
            .build()
            .publish();

        return ResourceResponse.ok();
    }

    @GetMapping(value = "/memory")
    public ResourceResponse getMemoryInfo(@RequestParam(value = "buffer", required = false) boolean buffer) {
        long m = 1;
        Map<String, Object> memoryMap = Maps.newHashMapWithExpectedSize(16);

        Runtime rt = Runtime.getRuntime();
        memoryMap.put("totalMemoryForJVM", rt.totalMemory() / m);
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        memoryMap.put("totalMemoryForMachine", osmxb.getTotalPhysicalMemorySize() / m);


        RequestCustomConfig config = RequestCustomConfig.builder()
            .setUrl("http://courseclass.soa.yeshj.com/v1/classes/19400532")
            .setSocketTimeOutMillisecond(3000)
            .build();

        String result = HttpAsyncClient.create().sendGet(config);
        memoryMap.put("totalMemoryForJVM_01", rt.totalMemory() / m);
        memoryMap.put("totalMemoryForMachine_01", osmxb.getTotalPhysicalMemorySize() / m);

        if (buffer) {
            CharBuffer charBuffer = CharBuffer.wrap(result);
            memoryMap.put("charBufferString", charBuffer.toString());
        } else {
            memoryMap.put("charBufferStringNO", result);
        }

        rt = Runtime.getRuntime();
        memoryMap.put("totalMemoryForJVM_01", rt.totalMemory() / m);
        memoryMap.put("totalMemoryForMachine_01", osmxb.getTotalPhysicalMemorySize() / m);

        return ResourceResponse.ok(memoryMap);
    }

    @PostMapping(value = "/cache")
    @HummerSimpleObjectCache(applicationName = "hummer", businessCode = "test", timeoutSeconds = 120)
    public ResourceResponse<String> getCacheTest(@RequestBody CacheTestReqDto req) {
        return ResourceResponse.ok(req.getUserId());
    }

    @PostMapping(value = "/cache2")
    @HummerSimpleObjectCache(applicationName = "hummer", businessCode = "test")
    public ResourceResponse<String> getCacheTest2(@RequestBody CacheTestReqDto req
        , @RequestParam(value = "name") @HummerSimpleObjectCacheKey String name) {
        return ResourceResponse.ok(req.getUserId() + ":" + name);
    }

    @PostMapping(value = "/cache3")
    @HummerSimpleObjectCache(applicationName = "hummer", businessCode = "test")
    public ResourceResponse<String> getCacheTest2(
        @RequestParam(value = "name") @HummerSimpleObjectCacheKey String name
        , @RequestParam(value = "arg") @HummerSimpleObjectCacheKey String arg) {
        return ResourceResponse.ok(name + ":" + arg);
    }

    @GetMapping(value = "/log/{level}")
    public ResourceResponse<String> write(@PathVariable("level") String level) {
        if (Level.valueOf(level) == Level.INFO) {
            log.info("ssssssddddddddd" + level);
        }

        if (Level.valueOf(level) == Level.WARN) {
            log.warn("ssssssddddddddd" + level);
        }

        if (Level.valueOf(level) == Level.ERROR) {
            log.error("ssssssddddddddd" + level);
        }

        return ResourceResponse.ok();
    }
}
