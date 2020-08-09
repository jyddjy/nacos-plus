package com.bytes.bfs.nacos.plus;

import com.alibaba.nacos.core.cluster.Member;
import com.alibaba.nacos.core.cluster.ServerMemberManager;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RSet;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * nacos 注册中心的增强操作<br/>
 * 1.将当前机器ip存放到redis中 <br/>
 * 2.轮询redis不断检查集群节点信息<br />
 * 3.应对rancher部署，节点动态变化的场景
 * 4.集群信息只能近实时
 * 5.针对多网卡的情况需要处理 TODO
 */
@Component
@ConditionalOnClass(ServerMemberManagerPlus.class)
@Slf4j
public class ServerMemberManagerPlus implements InitializingBean, DisposableBean {

    private static final String NACOS_CLUSTER_MEMBER = "nacos:cluster:member";

    private static final String NACOS_CLUSTER_MEMBER_TOPIC ="nacos:cluster:member:topic";

    private final Redisson redisson;

    private final ServerMemberManager serverMemberManager;

    private final RTopic topic;

    public ServerMemberManagerPlus(Redisson redisson,ServerMemberManager serverMemberManager) {
        this.redisson = redisson;
        this.serverMemberManager = serverMemberManager;
        this.topic =  redisson.getTopic(NACOS_CLUSTER_MEMBER_TOPIC);
        this.topic.addListener(TopicMessage.class,new TopicListener());
    }

    private RSet<Member> getMembers() {
        return redisson.getSet(NACOS_CLUSTER_MEMBER);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Member self = serverMemberManager.getSelf();
        RSet<Member> members = getMembers();
        members.add(self);
        this.topic.publish(TopicMessage.builder().member(self).opt("add").build());
    }

    @Override
    public void destroy() throws Exception {
        Member self = serverMemberManager.getSelf();
        RSet<Member> members = getMembers();
        members.remove(self);
        this.topic.publish(TopicMessage.builder().member(self).opt("remove").build());
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopicMessage {

        private String opt;

        private Member member;

    }

    class TopicListener implements MessageListener<TopicMessage>{

        @Override
        public void onMessage(CharSequence charSequence, TopicMessage topicMessage) {

            if(Objects.isNull(topicMessage) ) {
                return;
            }

            final String opt = topicMessage.getOpt();
            final Member member = topicMessage.getMember();

            if(member.equals(serverMemberManager.getSelf())) {
                return;
            }

            if(StringUtils.equalsIgnoreCase(opt,"remove")) {
                serverMemberManager.memberLeave(ImmutableList.of(member));
                log.info("memberLeave: {}",member);
            }

            if(StringUtils.equalsIgnoreCase(opt,"add")) {
                serverMemberManager.memberJoin(ImmutableList.of(member));
                log.info("memberJoin: {}",member);
            }

        }
    }
}
