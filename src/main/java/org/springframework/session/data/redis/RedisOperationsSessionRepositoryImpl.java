package org.springframework.session.data.redis;

import org.springframework.data.redis.core.RedisOperations;

/**
 * Create by  zhangbaowei on 2018/8/15 10:37.
 */
public class RedisOperationsSessionRepositoryImpl extends RedisOperationsSessionRepository {
    /**
     * Creates a new instance. For an example, refer to the class level javadoc.
     *
     * @param sessionRedisOperations the {@link RedisOperations} to use for managing the
     *                               sessions. Cannot be null.
     */
    public RedisOperationsSessionRepositoryImpl(RedisOperations<Object, Object> sessionRedisOperations) {
        super(sessionRedisOperations);

    }

    @Override
    public void save(RedisSession session) {
        session.setNew(false);
        super.save(session);
    }
}
