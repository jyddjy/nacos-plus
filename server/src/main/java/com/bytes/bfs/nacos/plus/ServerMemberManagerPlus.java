package com.bytes.bfs.nacos.plus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.core.cluster.Member;
import com.alibaba.nacos.core.cluster.ServerMemberManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import org.redisson.api.map.event.EntryEvent;
import org.redisson.api.map.event.EntryExpiredListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * nacos 注册中心的增强操作<br/>
 * 1.将当前机器ip存放到redis中 <br/>
 * 2.轮询redis不断检查集群节点信息<br />
 * 3.应对rancher部署，节点动态变化的场景
 * 4.集群信息只能近实时
 * 5.针对多网卡的情况需要处理 TODO
 */
@Slf4j
@EnableConfigurationProperties(PlusConfig.class)
public class ServerMemberManagerPlus implements InitializingBean, Runnable {

    private static final String NACOS_CLUSTER_MEMBER = "nc.cluster.members";

    private ScheduledThreadPoolExecutor poolExecutor = ThreadPoolUtils.defaultScheduledThreadPool(5, ServerMemberManagerPlus.class.getName());

    private RMapCache<String, String> memberCache;

    private final ServerMemberManager serverMemberManager;

    private final PlusConfig plusConfig;

    public ServerMemberManagerPlus(Redisson redisson, ServerMemberManager serverMemberManager, PlusConfig plusConfig) {
        this.serverMemberManager = serverMemberManager;
        memberCache = redisson.getMapCache(NACOS_CLUSTER_MEMBER);
        memberCache.addListener(new MemberCacheExpiredListener(serverMemberManager));
        this.plusConfig = plusConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Member self = serverMemberManager.getSelf();
        registerSelf(self);
        poolExecutor.scheduleAtFixedRate(this, plusConfig.getHeartbeat(), plusConfig.getHeartbeat(), TimeUnit.MILLISECONDS);
    }

    private void registerSelf(Member self) {
        String member = JSONObject.toJSONString(self);
        memberCache.put(self.getAddress(), member, plusConfig.getExpire(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        this.registerSelf(serverMemberManager.getSelf());
        Collection<String> members = memberCache.values();
        if (CollectionUtils.isEmpty(members)) {
            return;
        }

        //检查拉到一定是存活得如果不是存活踢出掉
        List<Member> resolvedMembers = members.stream()
                .map(member -> JSON.toJavaObject(JSONObject.parseObject(member), Member.class))
                .collect(Collectors.toList());

        //fixme: 如果原始得member不存在那么移出掉，但是这种方式破坏了以前所有得方式,只使用k8s动态扩容方式可以使用该模块方式
        Collection<Member> oldMembers = serverMemberManager.allMembers();
        Sets.SetView<Member> difference = Sets.difference(Sets.newHashSet(oldMembers), Sets.newHashSet(resolvedMembers));
        if (CollectionUtils.isNotEmpty(difference)) {
            serverMemberManager.memberLeave(difference);
        }

        //如果新节点全部都已经存在了 那么不需要加入新节点
        boolean exist = oldMembers.containsAll(resolvedMembers);
        if (exist) {
            return;
        }

        this.serverMemberManager.memberJoin(resolvedMembers);
    }

    /**
     * expired
     */
    static class MemberCacheExpiredListener implements EntryExpiredListener<String, String> {

        private final ServerMemberManager serverMemberManager;

        public MemberCacheExpiredListener(ServerMemberManager serverMemberManager) {
            this.serverMemberManager = serverMemberManager;
        }

        @Override
        public void onExpired(EntryEvent<String, String> entryEvent) {
            Member member = JSON.toJavaObject(JSONObject.parseObject(entryEvent.getOldValue()), Member.class);
            serverMemberManager.memberLeave(ImmutableList.of(member));

        }
    }

}
