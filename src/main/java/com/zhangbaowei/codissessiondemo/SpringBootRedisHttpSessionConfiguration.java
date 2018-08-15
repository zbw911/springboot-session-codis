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

//public class ConfigSessionBean {
@Configuration
@EnableConfigurationProperties(RedisSessionProperties.class)
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


        /**
         * 使用自定义 StringSerializer ,否则Redis中Key会变成 右边的B样 "\xac\xed\x00\x05t\x00<spring:session:sessions:65203e63-29c1-4a0c-869b-abe7b120070d"
         *
         * @return
         */
        private RedisOperations<Object, Object> createsessionRedisOperations() {
            RedisTemplate<Object, Object> redisTemplate = new RedisTemplate();
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setHashKeySerializer(new StringRedisSerializer());
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            redisTemplate.afterPropertiesSet();
            return redisTemplate;
        }


        /**
         * 用自定义 sessionRepository 替换 默认 sessionRepository
         *
         * @return
         */
        @Bean
        @Override
        public RedisOperationsSessionRepository sessionRepository() {

            RedisOperationsSessionRepository sessionRepository = new RedisOperationsSessionRepositoryImpl(createsessionRedisOperations());

            Duration timeout = sessionProperties.getTimeout();
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
        @Override
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
//}
