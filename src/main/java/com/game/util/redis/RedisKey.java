package com.game.util.redis;

/**
 * Created by jeniss on 18/5/10.
 */
public class RedisKey {
    public static final String PROCESSED_SERVER_CATEGORIES = "processed_server_categories";// set value:subServerId-itemId
    public static final String CRON_EXCEPTION_FLAG = "cron_exception_flag";// string value:has exception:Y
}
