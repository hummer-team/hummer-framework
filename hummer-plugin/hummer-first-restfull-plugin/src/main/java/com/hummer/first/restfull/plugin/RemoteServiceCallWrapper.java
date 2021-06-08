package com.hummer.first.restfull.plugin;

import java.lang.reflect.Type;

/**
 * @author lee
 */
public interface RemoteServiceCallWrapper {
    /**
     * call service by config parameter
     *
     * @param config
     * @param businessName
     * @param parse
     * @param type
     * @return
     */
    Object callByConfig(HummerRestByConfig config, String businessName
            , Class<? extends CustomParseResp> parse, Type type) throws Exception;

    /**
     * call service by declare parameter
     *
     * @param declare
     * @param businessName
     * @param parse
     * @param type
     * @return
     */
    Object callByDeclare(HummerRestByDeclare declare, String businessName
            , Class<? extends CustomParseResp> parse, Type type) throws Exception;
}
