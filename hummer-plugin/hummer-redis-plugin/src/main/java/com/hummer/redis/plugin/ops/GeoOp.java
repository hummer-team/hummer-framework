package com.hummer.redis.plugin.ops;

import org.springframework.util.CollectionUtils;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.params.GeoRadiusParam;

import java.util.List;
import java.util.Map;

/**
 * GeoOp
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/8/19 18:11
 */
public class GeoOp extends BaseOp<GeoOp> {

    private String redisDbGroupName;

    public GeoOp() {
        super();
        this.redisDbGroupName = REDIS_DB_GROUP_NAME;
        redis(this.redisDbGroupName);
    }

    public GeoOp(final String redisDbGroupName) {
        super(redisDbGroupName);
        this.redisDbGroupName = redisDbGroupName;
        redis(this.redisDbGroupName);
    }

    public Long add(String geoKey, String name, double longitude, double latitude) {

        return redis(redisDbGroupName).doExecute(jedis -> jedis.geoadd(geoKey, longitude, latitude, name));
    }

    public Long add(String geoKey, Map<String, GeoCoordinate> map) {

        return redis(redisDbGroupName).doExecute(jedis -> jedis.geoadd(geoKey, map));
    }

    public GeoCoordinate ops(String geoKey, String name) {

        List<GeoCoordinate> list = redis(redisDbGroupName).doExecute(jedis -> jedis.geopos(geoKey, name));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 距离默认单位m
     *
     * @author chen wei
     * @date 2020/8/19
     */
    public Double dist(String geoKey, String name1, String name2) {

        return redis(redisDbGroupName).doExecute(jedis -> jedis.geodist(geoKey, name1, name2));
    }

    public Double dist(String geoKey, String name1, String name2, GeoUnit unit) {

        return redis(redisDbGroupName).doExecute(jedis -> jedis.geodist(geoKey, name1, name2, unit));
    }

    public List<GeoRadiusResponse> radiusByMember(String geoKey, String name, double radius, GeoUnit unit) {

        return redis(redisDbGroupName).doExecute(jedis -> jedis.georadiusByMemberReadonly(geoKey, name, radius, unit));
    }

    public List<GeoRadiusResponse> radiusByMember(String geoKey, String name, double radius, GeoUnit unit
            , GeoRadiusParam param) {

        return redis(redisDbGroupName).doExecute(jedis ->
                jedis.georadiusByMemberReadonly(geoKey, name, radius, unit, param));
    }

    public List<GeoRadiusResponse> radius(String geoKey, double longitude, double latitude, double radius, GeoUnit unit) {

        return redis(redisDbGroupName).doExecute(jedis ->
                jedis.georadius(geoKey, longitude, latitude, radius, unit));
    }

    public List<GeoRadiusResponse> radius(String geoKey, double longitude, double latitude, double radius, GeoUnit unit
            , GeoRadiusParam param) {

        return redis(redisDbGroupName).doExecute(jedis ->
                jedis.georadius(geoKey, longitude, latitude, radius, unit, param));
    }

}
