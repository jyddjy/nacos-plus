### 编译打包
```text
mvn clean deploy -Dmaven.test.skip=true
```
### nacos 源码中添加该插件得使用
```text
naming 模块中

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.bytes", "com.alibaba.nacos.naming", "com.alibaba.nacos.core"})
@Import(ServerMemberManagerPlus.class)
public class NamingApp {

    public static void main(String[] args) {
        SpringApplication.run(NamingApp.class, args);
    }
}
```
### 本地编译nacos
```text
mvn -Prelease-nacos -Dmaven.test.skip=true clean install -U
```

### 打镜像
```text
https://github.com/nacos-group/nacos-docker 修改Dockerfile内容将本地得压缩包放进去

本地修改的内容如下 
==============================build.sh======================================
cp /Users/jianghao/home/code/bytes/nacos/distribution/target/nacos-server-1.3.2.tar.gz .

HARBOR=47.108.188.27:9999

PUSH=$HARBOR/bytes/bfs-nacos:lt

sudo docker build  -t $PUSH  --pull=true --file=Dockerfile .

sudo docker login $HARBOR -u admin -p [password]

sudo docker push $PUSH

==============================Dockerfile==================================
#FROM centos:7.5.1804
MAINTAINER pader "jh_vip_1024@163.com"

# set environment
ENV MODE="cluster" \
    PREFER_HOST_MODE="ip"\
    BASE_DIR="/home/nacos" \
    CLASSPATH=".:/home/nacos/conf:$CLASSPATH" \
    CLUSTER_CONF="/home/nacos/conf/cluster.conf" \
    FUNCTION_MODE="all" \
    JAVA_HOME="/usr/lib/jvm/java-1.8.0-openjdk" \
    NACOS_USER="nacos" \
    JAVA="/usr/lib/jvm/java-1.8.0-openjdk/bin/java" \
    JVM_XMS="2g" \
    JVM_XMX="2g" \
    JVM_XMN="1g" \
    JVM_MS="128m" \
    JVM_MMS="320m" \
    NACOS_DEBUG="n" \
    TOMCAT_ACCESSLOG_ENABLED="false" \
    TIME_ZONE="Asia/Shanghai"

ARG NACOS_VERSION=1.3.2

WORKDIR /$BASE_DIR

RUN set -x \
    && yum update -y \
    && yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel wget iputils nc  vim libcurl
#RUN wget  https://github.com/alibaba/nacos/releases/download/${NACOS_VERSION}/nacos-server-${NACOS_VERSION}.tar.gz -P /home

RUN mkdir -p /home
COPY nacos-server-1.3.2.tar.gz /home/nacos-server-1.3.2.tar.gz

RUN tar -xzvf /home/nacos-server-1.3.2.tar.gz -C /home \
    && rm -rf /home/nacos-server-1.3.2.tar.gz /home/nacos/bin/* /home/nacos/conf/*.properties /home/nacos/conf/*.example /home/nacos/conf/nacos-mysql.sql
RUN yum autoremove -y wget \
    && ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo $TIME_ZONE > /etc/timezone \
    && yum clean all


ADD bin/docker-startup.sh bin/docker-startup.sh
ADD conf/application.properties conf/application.properties
ADD init.d/custom.properties init.d/custom.properties


# set startup log dir
RUN mkdir -p logs \
        && cd logs \
        && touch start.out \
        && ln -sf /dev/stdout start.out \
        && ln -sf /dev/stderr start.out
RUN chmod +x bin/docker-startup.sh

EXPOSE 8848
ENTRYPOINT ["bin/docker-startup.sh"]

```
### 启动容器，添加环境变量
```text

SPRING.REDIS.PORT	=
SPRING_REDIS_HOST	=

SPRING_DATASOURCE_PLATFORM	=mysql
MYSQL_SERVICE_USER	=
MYSQL_SERVICE_PASSWORD	=
MYSQL_SERVICE_HOST	=
MYSQL_SERVICE_DB_NAME= nacos
```