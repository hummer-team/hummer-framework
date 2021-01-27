package com.hummer.request.idempotent.plugin.constants;

/**
 * Constants
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/28 10:26
 */
public class Constants {

    public static final int DEFAULT_EXPIRED_TIME_SECONDS = 60 * 60 * 24;

    public static final int REDIS_ADD_LOCK_TIME_SECONDS = 20;

    public static final int REDIS_REQUEST_IDEMPOTENT_TIME_SECONDS = 60 * 5;
}
