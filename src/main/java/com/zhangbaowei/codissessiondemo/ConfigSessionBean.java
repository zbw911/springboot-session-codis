package com.zhangbaowei.codissessiondemo;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.RedisSessionProperties;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.RedisOperationsSessionRepositoryImpl;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.util.StringUtils;

import java.time.Duration;

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

//    @Bean
//    public ConfigureRedisAction configureRedisAction() {
//        return ConfigureRedisAction.NO_OP;
//    }

    @Configuration
    public class SpringBootRedisHttpSessionConfiguration
            extends RedisHttpSessionConfiguration {

        @Autowired
        RedisOperations<Object, Object> sessionRedisOperations;
        @Autowired
        SessionProperties sessionProperties;
        @Autowired
        RedisSessionProperties redisSessionProperties;
        @Autowired
        RedisConnectionFactory redisConnectionFactory;

        @Bean
        @Override
        public RedisOperationsSessionRepository sessionRepository() {

            RedisOperationsSessionRepositoryImpl sessionRepository = new RedisOperationsSessionRepositoryImpl(sessionRedisOperations);

            Duration timeout = sessionProperties.getTimeout();
          /*
           # 下面的代码是在父类里初始化时候用的，暂时看注掉也没关系
          if (timeout != null) {
                setMaxInactiveIntervalInSeconds((int) timeout.getSeconds());
            }
            setRedisNamespace(redisSessionProperties.getNamespace());
            setRedisFlushMode(redisSessionProperties.getFlushMode());
            setCleanupCron(redisSessionProperties.getCleanupCron());*/


            if (timeout != null) {
                sessionRepository
                        .setDefaultMaxInactiveInterval((int) timeout.getSeconds());
            }

            if (StringUtils.hasText(redisSessionProperties.getNamespace())) {
                sessionRepository.setRedisKeyNamespace(redisSessionProperties.getNamespace());
            }
            sessionRepository.setRedisFlushMode(redisSessionProperties.getFlushMode());

            return sessionRepository;
        }


        /**
         * 跳过Redis “Config”  命令
         *
         * @return
         */
//        @Override
        @Bean
        public InitializingBean enableRedisKeyspaceNotificationsInitializer() {
            return null;
        }

        /**
         * 跳过发布-订阅命令
         *
         * @return
         */
        @Bean
        @Override
        public RedisMessageListenerContainer redisMessageListenerContainer() {
            RedisMessageListenerContainer container = new RedisMessageListenerContainer();
            container.setConnectionFactory(this.redisConnectionFactory);
//            if (this.redisTaskExecutor != null) {
//                container.setTaskExecutor(this.redisTaskExecutor);
//            }
//            if (this.redisSubscriptionExecutor != null) {
//                container.setSubscriptionExecutor(this.redisSubscriptionExecutor);
//            }
//            container.addMessageListener(sessionRepository(),
//                    Arrays.asList(new PatternTopic("__keyevent@*:del"),
//                            new PatternTopic("__keyevent@*:expired")));
//            container.addMessageListener(sessionRepository(),
//                    Collections.singletonList(new PatternTopic(
//                            sessionRepository().getSessionCreatedChannelPrefix() + "*")));
            return container;
        }

    }
}
