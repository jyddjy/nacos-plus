//package com.bytes.bfs.nacos.plus.test;
//
//import com.alibaba.fastjson.JSONObject;
//import com.hope.saas.bfs.common.auth.AuthCaller;
//import com.hope.saas.bfs.common.auth.AuthInterceptor;
//import com.hope.saas.bfs.common.auth.OriginalCaller;
//import com.hope.saas.bfs.common.auth.UserSession;
//import com.hope.saas.bfs.foundation.enums.AppEnv;
//import com.hope.saas.bfs.support.event.Event;
//import com.hope.saas.bfs.support.event.EventProducer;
//import com.hope.saas.bfs.support.event.KafkaEventProducer;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.task.SyncTaskExecutor;
//import org.springframework.core.task.TaskExecutor;
//import org.springframework.web.servlet.HandlerInterceptor;
//import redis.embedded.RedisServer;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//@TestConfiguration
//@Slf4j
//public class TestConfig {
//    private RedisServer redisServer;
//
//    @Bean
//    @Primary
//    public TaskExecutor mockTaskExecutor() {
//        return new SyncTaskExecutor();
//    }
//
//
//    @PostConstruct
//    public void postConstruct() {
//        redisServer = new RedisServer(16379);
//        // redis server会在后台启动一个redis server的进程，默认IP=127.0.0.1
//        redisServer.start();
//    }
//
//    @PreDestroy
//    public void preDestroy() {
//        // 测试结束后必须调用redisServer.stop(), 否则后台运行的redis server进程会一直存在并占用6379端口
//        redisServer.stop();
//    }
//
//    @Bean
//    @Primary
//    public EventProducer<KafkaEventProducer.KafkaMessageMeta> dummyEventProducer() {
//        return new EventProducer<KafkaEventProducer.KafkaMessageMeta>() {
//            @Override
//            public void send(Event event, Context<KafkaEventProducer.KafkaMessageMeta> context) {
//                log.info("send event to console, event = {}, content = {}", event.toJsonString(), JSONObject.toJSONString(context));
//            }
//
//            @Override
//            public void send(Event event) {
//                log.info("send event to console, event = {}", event.toJsonString());
//            }
//        };
//    }
//
//    /**
//     * 为需要OriginalCaller的单元测试注入caller信息.
//     *
//     * @return
//     * @throws Exception
//     */
//    @Bean
//    public HandlerInterceptor authInterceptor() throws Exception {
//        return new HandlerInterceptor() {
//            @Override
//            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//                AuthCaller payload = AuthCaller.builder().appId("001").appEnv(AppEnv.unittest).build();
//                request.setAttribute(AuthInterceptor.AUTH_PAYLOAD_KEY, payload);
//                request.setAttribute(AuthInterceptor.CALLER_PAYLOAD_KEY, OriginalCaller.fromAuthCaller(payload));
//                return true;
//            }
//        };
//    }
//
//    @Bean
//    @Primary
//    public HandlerInterceptor mockUserSessionInterceptor() {
//        return new HandlerInterceptor() {
//            @Override
//            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//                // TODO: 构建服务关注的user session;
//                UserSession userSession = UserSession.builder()
//                        .accountOid("XXXXXXXXXX")
//                        .accountType(UserSession.AccountType.USER).build();
//                request.setAttribute("_session_payload_", userSession);
//                return true;
//            }
//        };
//    }
//
//}
