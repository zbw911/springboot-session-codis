package com.zhangbaowei.codissessiondemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.RedisSessionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.RedisOperationsSessionRepositoryImpl;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

/**
 * Create by  zhangbaowei on 2018/8/15 10:40.
 */
@Configuration
@EnableConfigurationProperties(RedisSessionProperties.class)
public class ConfigSessionBean {
//    @Bean(name = "sessionRepository")
//    RedisOperationsSessionRepository redisRepository(RedisOperations<Object, Object> sessionRedisOperations) {
//        return new RedisOperationsSessionRepositoryImpl(sessionRedisOperations);
//    }


    @Bean
    public RedisOperations<Object, Object> sessionRedisOperations(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
    }

    @Bean
    public ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Configuration
    public class SpringBootRedisHttpSessionConfiguration
            extends RedisHttpSessionConfiguration {

        @Autowired
        RedisOperations<Object, Object> sessionRedisOperations;

//        @Autowired
//        public void customize(SessionProperties sessionProperties,
//                              RedisSessionProperties redisSessionProperties) {
//            Duration timeout = sessionProperties.getTimeout();
//            if (timeout != null) {
//                setMaxInactiveIntervalInSeconds(100);
//            }
//            setRedisNamespace(redisSessionProperties.getNamespace());
//            setRedisFlushMode(redisSessionProperties.getFlushMode());
//            setCleanupCron(redisSessionProperties.getCleanupCron());
//        }

        @Bean
        @Override
        public RedisOperationsSessionRepository sessionRepository() {
            //RedisTemplate<Object, Object> redisTemplate = sessionRedisOperations;
//            RedisOperationsSessionRepository sessionRepository = new RedisOperationsSessionRepository(
//                    sessionRedisOperations);

            RedisOperationsSessionRepositoryImpl sessionRepository = new RedisOperationsSessionRepositoryImpl(sessionRedisOperations);
            //  sessionRepository.setApplicationEventPublisher(this.applicationEventPublisher);
//            if (this.defaultRedisSerializer != null) {
//                sessionRepository.setDefaultSerializer(this.defaultRedisSerializer);
//            }
            sessionRepository
                    .setDefaultMaxInactiveInterval(100);
//            if (StringUtils.hasText(this.redisNamespace)) {
//                sessionRepository.setRedisKeyNamespace(this.redisNamespace);
//            }
//            sessionRepository.setRedisFlushMode(this.redisFlushMode);
            return sessionRepository;
        }

    }
}
