package com.kelab.usercenter.support;

import com.alibaba.fastjson.JSON;
import com.kelab.info.context.Context;
import com.kelab.usercenter.constant.enums.CacheBizName;
import com.kelab.usercenter.dal.redis.RedisCache;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ContextLogger {

    private final Logger LOGGER = LoggerFactory.getLogger(ContextLogger.class);

    private static final String CONTEXT_MSG = "context info:%s\n";

    private RedisCache redisCache;

    public ContextLogger(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    private String genMsgAndSaveRedis(Context context, String format, Object... objects) {
        String contextInfo = JSON.toJSONString(context);
        if (context == null) {
            contextInfo = "暂无上下文信息";
        }
        String pre = String.format(CONTEXT_MSG, contextInfo);
        String msg = String.format(pre + format, objects);
        if (context != null && StringUtils.isNotBlank(context.getLogId())) {
            redisCache.saveLog(CacheBizName.LOG, context.getLogId(), msg);
        }
        return msg;
    }

    public void info(Context context, String format, Object... objects) {
        this.LOGGER.info(genMsgAndSaveRedis(context, format, objects));
    }

    public void error(Context context, String format, Object... objects) {
        this.LOGGER.error(genMsgAndSaveRedis(context, format, objects));
    }

    public void debug(Context context, String format, Object... objects) {
        this.LOGGER.debug(genMsgAndSaveRedis(context, format, objects));
    }
}
