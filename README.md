# kzp(SpringCloud)

### 版本对应

[版本说明 · alibaba/spring-cloud-alibaba Wiki (github.com)](https://github.com/alibaba/spring-cloud-alibaba/wiki/版本说明)

| Spring Boot Version | Spring Cloud Alibaba Version | Sentinel Version | Nacos Version | RocketMQ Version | Dubbo Version | Seata Version |
| ------------------- | ---------------------------- | ---------------- | ------------- | ---------------- | ------------- | ------------- |
| 2.6.11              | 2021.0.4.0                   | 1.8.5            | 2.0.4         | 4.9.4            | ~             | 1.5.2         |



## 架构

### 项目结构

<img src="Images/image-20221027142456611.png" alt="image-20221027142456611" style="zoom:33%;" />



### Maven

#### 父工程pom

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <packaging>pom</packaging>

    <groupId>com.kiligz</groupId>
    <artifactId>kzp</artifactId>
    <version>0.0.1</version>
    <name>kzp</name>
    <description>kz-push</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.7.RELEASE</version>
    </parent>

    <modules>
        <module>kzp-api</module>
        <module>kzp-dispatcher</module>
        <module>kzp-processor</module>
        <module>kzp-data-house</module>
        <module>kzp-pusher</module>
        <module>kzp-scheduler</module>
        <module>kzp-admin</module>
        <module>kzp-commons</module>
    </modules>

    <properties>
        <java.version>1.8</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <spring-boot.version>2.3.7.RELEASE</spring-boot.version>
        <spring-cloud-alibaba.version>2.2.2.RELEASE</spring-cloud-alibaba.version>
        <guava.version>31.1-jre</guava.version>
        <hutool.version>5.5.0</hutool.version>
        <mybatis-plus.version>3.4.2</mybatis-plus.version>
    </properties>

    <!-- 项目公共依赖 commons使用 -->
    <dependencies>
        <!-- spring-boot-tomcat启动 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- nacos-config -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>

        <!-- nacos-discovery -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>

        <!-- dubbo -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-dubbo</artifactId>
        </dependency>

        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <!-- guava -->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>${guava.version}</version>
        </dependency>

        <!-- hutool -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>${hutool.version}</version>
        </dependency>
    </dependencies>

    <!-- 项目特定依赖的管理 -->
    <dependencyManagement>
        <dependencies>
            <!-- spring-cloud-alibaba版本管理 -->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring-cloud-alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- mybatis-plus -->
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-boot-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <!-- 插件 -->
        <plugins>
            <!--定制化打包-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>${maven-assembly-plugin.version}</version>
                <configuration>
                    <skipAssembly>true</skipAssembly>
                </configuration>
                <inherited>false</inherited>
                <executions>
                    <execution>
                        <id>kzp</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                        <configuration>
                            <appendAssemblyId>false</appendAssemblyId>
                            <finalName>${project.artifactId}-${project.version}</finalName>
                            <descriptors>
                                <!--suppress UnresolvedMavenProperty 子模块使用-->
                                <descriptor>${project.parent.basedir}/assembly/assembly.xml</descriptor>
                            </descriptors>
                        </configuration>
                        <inherited>true</inherited>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>

```



#### assembly.xml

定制化打包

```xml
<assembly xmlns="http://maven.apache.org/ASSEMBLY/2.1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/ASSEMBLY/2.1.1 https://maven.apache.org/xsd/assembly-2.1.1.xsd">
    <id>kzp</id>
    <formats>
        <format>tar.gz</format>
    </formats>
    <includeBaseDirectory>true</includeBaseDirectory>
    <baseDirectory>${project.artifactId}</baseDirectory>
    <dependencySets>
        <dependencySet>
            <unpack>false</unpack>
            <useProjectArtifact>true</useProjectArtifact>
            <outputDirectory>lib</outputDirectory>
            <scope>provided</scope>
        </dependencySet>
        <dependencySet>
            <unpack>false</unpack>
            <useProjectArtifact>true</useProjectArtifact>
            <outputDirectory>lib</outputDirectory>
            <scope>system</scope>
        </dependencySet>
        <dependencySet>
            <unpack>false</unpack>
            <useProjectArtifact>true</useProjectArtifact>
            <outputDirectory>lib</outputDirectory>
            <scope>runtime</scope>
        </dependencySet>
    </dependencySets>
    <fileSets>
        <fileSet>
            <directory>src/main/resources</directory>
            <outputDirectory>/</outputDirectory>
            <excludes>
                <exclude>shell/**</exclude>
            </excludes>
        </fileSet>
        <fileSet>
            <directory>src/main/resources/shell</directory>
            <outputDirectory>/</outputDirectory>
            <fileMode>777</fileMode>
        </fileSet>
        <fileSet>
            <directory>config</directory>
            <outputDirectory>config</outputDirectory>
        </fileSet>
        <fileSet>
            <directory>${project.parent.basedir}/global</directory>
            <outputDirectory>/</outputDirectory>
        </fileSet>
    </fileSets>
</assembly>
```



#### commons模块-pom

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <artifactId>kzp-commons</artifactId>

    <parent>
        <artifactId>kzp</artifactId>
        <groupId>com.kiligz</groupId>
        <version>0.0.1</version>
    </parent>

    <!-- 该pom不添加依赖只从parent继承 -->

    <build>
        <plugins>
            <!-- 跳过定制化打包 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>${maven-assembly-plugin.version}</version>
                <configuration>
                    <skipAssembly>true</skipAssembly>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

```



#### 其它模块-pom

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <artifactId>kzp-api</artifactId>

    <parent>
        <artifactId>kzp</artifactId>
        <groupId>com.kiligz</groupId>
        <version>0.0.1</version>
    </parent>

    <dependencies>
        <!-- kzp-commons -->
        <dependency>
            <groupId>com.kiligz</groupId>
            <artifactId>kzp-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>

```



### 配置文件

#### 全局配置

bootstrap.yml，可放在commons模块中

```yml
# nacos
spring:
  cloud:
    nacos:
      server-addr: 39.107.87.235:8848
      config:
        file-extension: yml
# dubbo
dubbo:
  protocol:
    name: dubbo
    # 自增
    port: -1
  registry:
    address: spring-cloud://39.107.87.235:8848
  scan:
    base-packages: com.kiligz.kzp
  consumer:
    check: false
```

- 读取顺序：
    - bootstrap.yml
    - bootstrap.yaml
    - bootstrap.properties
    - nacos的配置
    - application.yml
    - application.yaml
    - application.properties



### [Nacos](../Nacos/Nacos.md)

> v2.0.4

- 引入依赖

  ```xml
  <!-- nacos-config -->
  <dependency>
      <groupId>com.alibaba.cloud</groupId>
      <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
  </dependency>
  
  <!-- nacos-discovery -->
  <dependency>
      <groupId>com.alibaba.cloud</groupId>
      <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
  </dependency>
  ```

- 配置

  ```yml
  spring:
    cloud:
      nacos:
        server-addr: 39.107.87.235:8848
        discovery:
          group: KZP_GROUP
        config:
          group: KZP_GROUP
          file-extension: yml
  ```

- 使用

    - @Value(${xxx:default})：自动赋值

    - @RefreshScope ：自动刷新



### Dubbo

> 3.1.0

- 引入依赖

  ```xml
  <!-- dubbo -->
  <dependency>
      <groupId>org.apache.dubbo</groupId>
      <artifactId>dubbo-spring-boot-starter</artifactId>
      <version>3.1.0</version>
  </dependency>
  ```

- 配置

  ```yml
  dubbo:
    application:
      metadataType: remote
      register-mode: instance
    scan:
      base-packages: com.kiligz.kzp
    consumer:
      check: false
    protocol:
      name: dubbo
      port: -1
    registry:
      address: nacos://39.107.87.235:8848
      group: DUBBO_GROUP
      parameters:
        namespace: b24c7b62-9366-49e8-8ed1-42fa03add38a
  ```

  ```yml
  dubbo:
    application:
      logger: slf4j
      # 元数据中心 local 本地 remote 远程 这里使用远程便于其他服务获取
      # 注意 这里不能使用 本地 local 会读取不到元数据
      metadataType: remote
      # 可选值 interface、instance、all，默认是 all，即接口级地址、应用级地址都注册
      register-mode: instance
      service-discovery:
        # FORCE_INTERFACE，只消费接口级地址，如无地址则报错，单订阅 2.x 地址
        # APPLICATION_FIRST，智能决策接口级/应用级地址，双订阅
        # FORCE_APPLICATION，只消费应用级地址，如无地址则报错，单订阅 3.x 地址
        migration: FORCE_APPLICATION
    protocol:
      # 设置为 tri 即可使用 Triple 3.0 新协议
      # 性能对比 dubbo 协议并没有提升 但基于 http2 用于多语言异构等 http 交互场景
      # 使用 dubbo 协议通信
      name: dubbo
      # dubbo 协议端口(-1表示自增端口,从20880开始)
      port: -1
    # 注册中心配置
    registry:
      address: nacos://localhost:8848
      # 这里注意 由于 3.X 的 bug 导致注册组不生效
      group: DUBBO_GROUP
      # 由于 group 不生效 这里使用 namespace 将 dubbo 服务与 cloud 服务隔离
      # 否则会导致正常请求路由到 dubbo 服务报错
      parameters:
        # 注意 这里要在 nacos 创建名为 dubbo 的 namespace 环境
        namespace: dubbo
    # 消费者相关配置
    consumer:
      # 支持校验注解
      validation: true
      # 超时时间
      timeout: 3000
      # 初始化检查
      check: false
    scan:
      # 接口实现类扫描
      base-packages: com.ruoyi.**.dubbo
  ```



- 使用

    - 可将接口都维护在commons模块，其它模块引用、实现
    - @DubboService：服务提供
    - @DubboReference：服务调用




### [Gateway](../Gateway/Gateway.md)

- 引入依赖

  ```xml
  <!-- gateway -->
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-gateway</artifactId>
  </dependency>
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-loadbalancer</artifactId>
  </dependency>
  <!-- 去除web-starter -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
      <scope>test</scope>
  </dependency>
  ```

- 配置

  ```yml
  spring:
    cloud:
      gateway:
        discovery:
          locator:
            enabled: true
        routes:
          - id: kzp-api
            uri: lb://kzp-api
            predicates:
              - Path=/kzp/**
            filters:
              - StripPrefix=1
  ```

- 使用

  http://localhost:17000/kzp/api	->	http://localhost:17001/api



### [Sentinel](../Sentinel/Sentinel.md)

> v1.8.5

- 引入依赖

  ```xml
  <!-- sentinel -->
  <dependency>
      <groupId>com.alibaba.cloud</groupId>
      <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
  </dependency>
  ```

- 配置

  ```yml
  spring:
    cloud:
  		sentinel:
        transport:
          dashboard: 39.107.87.235:8858
  ```

- 使用

    - 注解方式

      ```java
      @SentinelResource(blockHandlerClass = TestController.class, blockHandler = "testBlock")
      ```

    - 统一处理

      ```java
      package com.kiligz.kzp.commons.handler;
      
      import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
      import com.alibaba.csp.sentinel.slots.block.BlockException;
      import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
      import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
      import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
      import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
      import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
      import com.fasterxml.jackson.databind.ObjectMapper;
      import org.springframework.http.MediaType;
      import org.springframework.stereotype.Component;
      
      import javax.servlet.http.HttpServletRequest;
      import javax.servlet.http.HttpServletResponse;
      import java.util.HashMap;
      import java.util.Map;
      
      /**
       * Sentinel流控异常处理器
       *
       * @author Ivan
       * @date 2022/10/27 17:22
       */
      @Component
      public class SentinelBlockExceptionHandler implements BlockExceptionHandler {
          private final ObjectMapper mapper = new ObjectMapper();
      
          @Override
          public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
              Map<Integer,String> resultMap = new HashMap<>();
              if(e instanceof FlowException){
                  resultMap.put(100, "接口限流了");
              }else if(e instanceof DegradeException){
                  resultMap.put(101, "服务降级了");
              }else if(e instanceof ParamFlowException){
                  resultMap.put(102, "热点参数限流了");
              }else if(e instanceof SystemBlockException){
                  resultMap.put(103, "触发系统保护规则了");
              }else if(e instanceof AuthorityException){
                  resultMap.put(104, "授权规则不通过");
              }
      
              //返回json数据
              httpServletResponse.setStatus(500);
              httpServletResponse.setCharacterEncoding("utf-8");
              httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
              mapper.writeValue(httpServletResponse.getWriter(), resultMap);
          }
      }
      ```



### [Seata](../Seata/Seata.md)

> v1.4.2

seata1.5.2与dubbo3有冲突

[(20条消息) Spring Cloud集成seata_qq_32415191的博客-CSDN博客_springcloud集成seata](https://blog.csdn.net/qq_32415191/article/details/125932442)

- 引用依赖

  ```xml
  <!-- seata -->
  <dependency>
      <groupId>com.alibaba.cloud</groupId>
      <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
      <exclusions>
          <exclusion>
              <groupId>io.seata</groupId>
              <artifactId>seata-spring-boot-starter</artifactId>
          </exclusion>
      </exclusions>
  </dependency>
  <dependency>
      <groupId>io.seata</groupId>
      <artifactId>seata-spring-boot-starter</artifactId>
      <version>1.4.2</version>
  </dependency>
  ```

- 配置

  ```yml
  seata:
    config:
      type: nacos
      nacos:
        server-addr: 39.107.87.235:8848
        namespace: bcb04fb7-a1bf-49b6-8a8d-1c8ed118f794
        data-id: seataServer.properties
    registry:
      type: nacos
      nacos:
        application: seata-server
        server-addr: 39.107.87.235:8848
        namespace: bcb04fb7-a1bf-49b6-8a8d-1c8ed118f794
    tx-service-group: default_tx_group
  ```

- 使用

  @GlobalTransactional加载类或方法上



### [RocketMQ](../RocketMQ/RocketMQ.md)

> v4.9.4

[RocketMQ与kafka的区别](https://blog.csdn.net/shijinghan1126/article/details/104724407)

```shell
docker pull xuchengen/rocketmq:4.9.4
```

```
#  cloud:
#    stream:
#      bindings:
#        input1:
#          destination: kzp-topic
#          content-type: text/plain
#          group: test
#      rocketmq:
#        binder:
#          name-server: 192.168.32.32:9876
#        bindings:
#          input1:
#            consumer:
#              tags: test
```



### Canal



### Xxl-Job

```properties
### web
server.port=8080
server.servlet.context-path=/xxl-job-admin

### actuator
management.server.servlet.context-path=/actuator
management.health.mail.enabled=false

### resources
spring.mvc.servlet.load-on-startup=0
spring.mvc.static-path-pattern=/static/**
spring.resources.static-locations=classpath:/static/

### freemarker
spring.freemarker.templateLoaderPath=classpath:/templates/
spring.freemarker.suffix=.ftl
spring.freemarker.charset=UTF-8
spring.freemarker.request-context-attribute=request
spring.freemarker.settings.number_format=0.##########

### mybatis
mybatis.mapper-locations=classpath:/mybatis-mapper/*Mapper.xml
#mybatis.type-aliases-package=com.xxl.job.admin.core.model

### xxl-job, datasource
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=root_pwd
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

### datasource-pool
spring.datasource.type=com.zaxxer.hikari.HikariDataSource
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.maximum-pool-size=30
spring.datasource.hikari.auto-commit=true
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.pool-name=HikariCP
spring.datasource.hikari.max-lifetime=900000
spring.datasource.hikari.connection-timeout=10000
spring.datasource.hikari.connection-test-query=SELECT 1
spring.datasource.hikari.validation-timeout=1000

### xxl-job, email
spring.mail.host=smtp.qq.com
spring.mail.port=25
spring.mail.username=xxx@qq.com
spring.mail.from=xxx@qq.com
spring.mail.password=xxx
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory

### xxl-job, access token
xxl.job.accessToken=default_token

### xxl-job, i18n (default is zh_CN, and you can choose "zh_CN", "zh_TC" and "en")
xxl.job.i18n=zh_CN

## xxl-job, triggerpool max size
xxl.job.triggerpool.fast.max=200
xxl.job.triggerpool.slow.max=100

### xxl-job, log retention days
xxl.job.logretentiondays=30

```



## 环境安装

[kiligz-spring-cloud.tar.gz](kiligz-spring-cloud.tar.gz)

> 配置对应主机IP及mysql、redis使用的密码，使用`./kiligz.sh install`一键安装



### xxl-job-admin

docker pull xuxueli/xxl-job-admin:2.3.1

```java
version: '3.8'
services:
  xxl-job-admin:
    image: xuxueli/xxl-job-admin:2.3.1
    container_name: xxl-job-admin
    ports:
      - 8082:8080
    environment:
      - PARAMS="--spring.datasource.url=jdbc:mysql://${host}:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai --spring.datasource.password=${password}"
```

```properties
### web
server.port=8080
server.servlet.context-path=/xxl-job-admin

### actuator
management.server.servlet.context-path=/actuator
management.health.mail.enabled=false

### resources
spring.mvc.servlet.load-on-startup=0
spring.mvc.static-path-pattern=/static/**
spring.resources.static-locations=classpath:/static/

### freemarker
spring.freemarker.templateLoaderPath=classpath:/templates/
spring.freemarker.suffix=.ftl
spring.freemarker.charset=UTF-8
spring.freemarker.request-context-attribute=request
spring.freemarker.settings.number_format=0.##########

### mybatis
mybatis.mapper-locations=classpath:/mybatis-mapper/*Mapper.xml
#mybatis.type-aliases-package=com.xxl.job.admin.core.model

### xxl-job, datasource
spring.datasource.url=jdbc:mysql://192.168.32.32:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=qepwq
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

### datasource-pool
spring.datasource.type=com.zaxxer.hikari.HikariDataSource
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.maximum-pool-size=30
spring.datasource.hikari.auto-commit=true
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.pool-name=HikariCP
spring.datasource.hikari.max-lifetime=900000
spring.datasource.hikari.connection-timeout=10000
spring.datasource.hikari.connection-test-query=SELECT 1
spring.datasource.hikari.validation-timeout=1000

### xxl-job, email
spring.mail.host=smtp.qq.com
spring.mail.port=25
spring.mail.username=xxx@qq.com
spring.mail.from=xxx@qq.com
spring.mail.password=xxx
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory

### xxl-job, access token
xxl.job.accessToken=default_token

### xxl-job, i18n (default is zh_CN, and you can choose "zh_CN", "zh_TC" and "en")
xxl.job.i18n=zh_CN

## xxl-job, triggerpool max size
xxl.job.triggerpool.fast.max=200
xxl.job.triggerpool.slow.max=100

### xxl-job, log retention days
xxl.job.logretentiondays=30

```



### kiligz.sh

```shell
#!/bin/bash

# 主机ip
HOST=192.168.32.32
# 密码
PASSWORD=qepwq

use() {
  echo "use:$0 {install|uninstall|restore}"
}

install() {
  echo "[ start installing ]"
  
  sed -i 's/${host}/'$HOST'/g' *.yml */*.yml */*.properties
  sed -i 's/${password}/'$PASSWORD'/g' *.yml */*.properties */*.conf
  
  docker-compose -f kiligz.yml up -d
  
  sleep 60
  
  curl -X POST "http://$HOST:8848/nacos/v1/console/namespaces" -d "customNamespaceId=seata&namespaceName=seata&namespaceDesc=seata"
  curl -X POST "http://$HOST:8848/nacos/v1/console/namespaces" -d "customNamespaceId=dubbo&namespaceName=dubbo&namespaceDesc=dubbo"
  
  CONTENT="tenant=seata&dataId=seataServer.properties&group=SEATA_GROUP&content=`cat seata/seataServer.properties`&type=properties"
  curl -X POST "http://$HOST:8848/nacos/v1/cs/configs" -d "$CONTENT"
  
  docker-compose -f kiligz-seata.yml up -d
  
  echo "[ install finish ]"
  echo ""
  echo "[                modules              ]"
  echo "[ mysql: $HOST:3306 root/$PASSWORD ]"
  echo "[ nacos: $HOST:8848/nacos nacos/nacos ]"
  echo "[ xxl-job-admin: $HOST:8082/xxl-job-admin admin/123456 ]"
  echo "[ canal-server: $HOST:11111 ]"
  echo "[ sentinel-dashboard: $HOST:8858 sentinel/sentinel ]"
  echo "[ rocketmq: $HOST:8081 admin/admin 9876 ]"
  echo "[ seata: $HOST:7091 seata/seata 8091]"
  echo "[ redis: $HOST:6379 $PASSWORD ]"
}

uninstall() {
  echo "[ start uninstalling ]"
  docker-compose -f kiligz-seata.yml down
  docker-compose -f kiligz.yml down
  echo "[ uninstall finish ]"
}

restore() {
  echo "[ start restoring ]"
  sed -i 's/'$HOST'/${host}/g' *.yml */*.yml */*.properties
  sed -i 's/'$PASSWORD'/${password}/g' *.yml */*.properties */*.conf
}

case $1 in
install)
  install
  ;;
uninstall)
  uninstall
  ;;
restore)
  restore
  ;;
*)
  use
  ;;
esac
exit 0
```



### kiligz.yml

```yml
version: '3.8'
services:
  # mysql
  mysql:
    image: library/mysql:5.7
    container_name: mysql
    privileged: true
    restart: always
    ports:
      - 3306:3306
    volumes: 
      # 初始化sql位置，nacos.sql，seata.sql，xxl-job.sql
      - ./mysql/init:/docker-entrypoint-initdb.d
      - ./mysql/conf/my.cnf:/etc/mysql/my.cnf
    environment:
      - MYSQL_ROOT_PASSWORD=${password}
    healthcheck:
      test: [ "CMD", "mysqladmin" ,"ping", "-h", "localhost" ]
      interval: 3s
      timeout: 3s
      retries: 7
      start_period: 3s
  # nacos
  nacos-server:
    image: nacos/nacos-server:v2.0.4
    container_name: nacos-server
    privileged: true
    restart: always
    ports:
      - 8848:8848
      - 9848:9848
      - 9849:9849
    environment:
      - JVM_XMS=512m
      - JVM_XMX=512m
      - JVM_XMN=256m
      - MODE=standalone
      - SPRING_DATASOURCE_PLATFORM=mysql
      - MYSQL_SERVICE_HOST=${host}
      - MYSQL_SERVICE_DB_NAME=nacos
      - MYSQL_SERVICE_USER=root
      - MYSQL_SERVICE_PASSWORD=qepwq
    # 依赖mysql
    depends_on:
      mysql:
        condition: service_healthy
  # xxl-job-admin
  xxl-job-admin:
    image: xuxueli/xxl-job-admin:2.3.1
    container_name: xxl-job-admin
    ports:
      - 8082:8080
    environment:
      - PARAMS=--spring.datasource.url=jdbc:mysql://${host}:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai --spring.datasource.password=${password}
    # 依赖mysql
    depends_on:
      mysql:
        condition: service_healthy
  # canal
  canal-server:
    image: canal/canal-server:v1.1.6
    container_name: canal-server
    ports:
      - 9100:9100
      - 11110:11110
      - 11111:11111
      - 11112:11112
    environment:
      - canal.destinations=kzp
      - canal.instance.master.address=${host}:3306
      - canal.instance.dbUsername=root
      - canal.instance.dbPassword=${password}
      - canal.instance.connectionCharset=UTF-8
      - canal.instance.tsdb.enable=true
      - canal.instance.gtidon=false
      - canal.instance.filter.regex=.*\\..*
      - canal.instance.filter.query.dml=true
    # 依赖mysql
    depends_on:
      mysql:
        condition: service_healthy
  # sentinel
  sentinel-dashboard: 
    image: bladex/sentinel-dashboard:latest
    container_name: sentinel-dashboard
    privileged: true
    restart: always
    ports:
      - 8858:8858
  # rocketmq
  rocketmq:
    image: xuchengen/rocketmq:4.9.4
    container_name: rocketmq
    privileged: true
    restart: always
    ports:
      - 8081:8080
      - 9876:9876
      - 10909:10909
      - 10911:10911
      - 10912:10912
    environment:
      - NAMESRV_XMS=256m
      - NAMESRV_XMX=256m
      - NAMESRV_XMN=128m
      - BROKER_XMS=512m
      - BROKER_XMX=512m
      - BROKER_XMN=256m
      - BROKER_MDM=256m
      - NAMESRV_ADDR=${host}:9876
    volumes:
      - /etc/localtime:/etc/localtime
      - /var/run/docker.sock:/var/run/docker.sock
  # redis
  redis:
      image: library/redis:7.0
      container_name: redis
      privileged: true
      restart: always
      ports:
        - 6379:6379
      environment:
        - TZ=Asia/Shanghai
      volumes:
        - ./redis/redis.conf:/etc/redis/redis.conf
      command: ["redis-server","/etc/redis/redis.conf"]
```



#### nacos.sql

```mysql
/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/******************************************/
/*   数据库全名 = nacos   */
/*   表名称 = config_info   */
/******************************************/
CREATE DATABASE nacos;

USE nacos;

CREATE TABLE `config_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) DEFAULT NULL,
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT '2010-05-05 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT '2010-05-05 00:00:00' COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(20) DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) DEFAULT NULL,
  `c_use` varchar(64) DEFAULT NULL,
  `effect` varchar(64) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `c_schema` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_aggr   */
/******************************************/
CREATE TABLE `config_info_aggr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) NOT NULL COMMENT 'datum_id',
  `content` longtext NOT NULL COMMENT '内容',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='增加租户字段';


/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_beta   */
/******************************************/
CREATE TABLE `config_info_beta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT '2010-05-05 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT '2010-05-05 00:00:00' COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(20) DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_beta';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_tag   */
/******************************************/
CREATE TABLE `config_info_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT '2010-05-05 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT '2010-05-05 00:00:00' COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(20) DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_tag';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_tags_relation   */
/******************************************/
CREATE TABLE `config_tags_relation` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`),
  UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_tag_relation';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = group_capacity   */
/******************************************/
CREATE TABLE `group_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT '2010-05-05 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT '2010-05-05 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='集群、各Group容量信息表';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = his_config_info   */
/******************************************/
CREATE TABLE `his_config_info` (
  `id` bigint(64) unsigned NOT NULL,
  `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL,
  `md5` varchar(32) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT '2010-05-05 00:00:00',
  `gmt_modified` datetime NOT NULL DEFAULT '2010-05-05 00:00:00',
  `src_user` text,
  `src_ip` varchar(20) DEFAULT NULL,
  `op_type` char(10) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`nid`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_gmt_modified` (`gmt_modified`),
  KEY `idx_did` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='多租户改造';


/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = tenant_capacity   */
/******************************************/
CREATE TABLE `tenant_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT '2010-05-05 00:00:00' COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT '2010-05-05 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='租户容量信息表';


CREATE TABLE `tenant_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) default '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) default '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tenant_info';

CREATE TABLE users (
	username varchar(50) NOT NULL PRIMARY KEY,
	password varchar(500) NOT NULL,
	enabled boolean NOT NULL
);

CREATE TABLE roles (
	username varchar(50) NOT NULL,
	role varchar(50) NOT NULL,
	constraint uk_username_role UNIQUE (username,role)
);

CREATE TABLE permissions (
    role varchar(50) NOT NULL,
    resource varchar(512) NOT NULL,
    action varchar(8) NOT NULL,
    constraint uk_role_permission UNIQUE (role,resource,action)
);

INSERT INTO users (username, password, enabled) VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE);

INSERT INTO roles (username, role) VALUES ('nacos', 'ROLE_ADMIN');
```



#### seata.sql

```mysql
-- -------------------------------- The script used when storeMode is 'db' --------------------------------
CREATE DATABASE seata;

USE seata;

-- the table to store GlobalSession data
CREATE TABLE IF NOT EXISTS `global_table`
(
    `xid`                       VARCHAR(128) NOT NULL,
    `transaction_id`            BIGINT,
    `status`                    TINYINT      NOT NULL,
    `application_id`            VARCHAR(32),
    `transaction_service_group` VARCHAR(32),
    `transaction_name`          VARCHAR(128),
    `timeout`                   INT,
    `begin_time`                BIGINT,
    `application_data`          VARCHAR(2000),
    `gmt_create`                DATETIME,
    `gmt_modified`              DATETIME,
    PRIMARY KEY (`xid`),
    KEY `idx_status_gmt_modified` (`status` , `gmt_modified`),
    KEY `idx_transaction_id` (`transaction_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- the table to store BranchSession data
CREATE TABLE IF NOT EXISTS `branch_table`
(
    `branch_id`         BIGINT       NOT NULL,
    `xid`               VARCHAR(128) NOT NULL,
    `transaction_id`    BIGINT,
    `resource_group_id` VARCHAR(32),
    `resource_id`       VARCHAR(256),
    `branch_type`       VARCHAR(8),
    `status`            TINYINT,
    `client_id`         VARCHAR(64),
    `application_data`  VARCHAR(2000),
    `gmt_create`        DATETIME(6),
    `gmt_modified`      DATETIME(6),
    PRIMARY KEY (`branch_id`),
    KEY `idx_xid` (`xid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- the table to store lock data
CREATE TABLE IF NOT EXISTS `lock_table`
(
    `row_key`        VARCHAR(128) NOT NULL,
    `xid`            VARCHAR(128),
    `transaction_id` BIGINT,
    `branch_id`      BIGINT       NOT NULL,
    `resource_id`    VARCHAR(256),
    `table_name`     VARCHAR(32),
    `pk`             VARCHAR(36),
    `status`         TINYINT      NOT NULL DEFAULT '0' COMMENT '0:locked ,1:rollbacking',
    `gmt_create`     DATETIME,
    `gmt_modified`   DATETIME,
    PRIMARY KEY (`row_key`),
    KEY `idx_status` (`status`),
    KEY `idx_branch_id` (`branch_id`),
    KEY `idx_xid_and_branch_id` (`xid` , `branch_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `distributed_lock`
(
    `lock_key`       CHAR(20) NOT NULL,
    `lock_value`     VARCHAR(20) NOT NULL,
    `expire`         BIGINT,
    primary key (`lock_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('AsyncCommitting', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('RetryCommitting', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('RetryRollbacking', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('TxTimeoutCheck', ' ', 0);
```



#### xxl-job.sql

```mysql
#
# XXL-JOB v2.3.1
# Copyright (c) 2015-present, xuxueli.

CREATE database if NOT EXISTS `xxl_job` default character set utf8mb4 collate utf8mb4_unicode_ci;
use `xxl_job`;

SET NAMES utf8mb4;

CREATE TABLE `xxl_job_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
  `job_desc` varchar(255) NOT NULL,
  `add_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `author` varchar(64) DEFAULT NULL COMMENT '作者',
  `alarm_email` varchar(255) DEFAULT NULL COMMENT '报警邮件',
  `schedule_type` varchar(50) NOT NULL DEFAULT 'NONE' COMMENT '调度类型',
  `schedule_conf` varchar(128) DEFAULT NULL COMMENT '调度配置，值含义取决于调度类型',
  `misfire_strategy` varchar(50) NOT NULL DEFAULT 'DO_NOTHING' COMMENT '调度过期策略',
  `executor_route_strategy` varchar(50) DEFAULT NULL COMMENT '执行器路由策略',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `executor_block_strategy` varchar(50) DEFAULT NULL COMMENT '阻塞处理策略',
  `executor_timeout` int(11) NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
  `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `glue_type` varchar(50) NOT NULL COMMENT 'GLUE类型',
  `glue_source` mediumtext COMMENT 'GLUE源代码',
  `glue_remark` varchar(128) DEFAULT NULL COMMENT 'GLUE备注',
  `glue_updatetime` datetime DEFAULT NULL COMMENT 'GLUE更新时间',
  `child_jobid` varchar(255) DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
  `trigger_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
  `trigger_last_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '上次调度时间',
  `trigger_next_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '下次调度时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
  `job_id` int(11) NOT NULL COMMENT '任务，主键ID',
  `executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `executor_sharding_param` varchar(20) DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
  `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `trigger_time` datetime DEFAULT NULL COMMENT '调度-时间',
  `trigger_code` int(11) NOT NULL COMMENT '调度-结果',
  `trigger_msg` text COMMENT '调度-日志',
  `handle_time` datetime DEFAULT NULL COMMENT '执行-时间',
  `handle_code` int(11) NOT NULL COMMENT '执行-状态',
  `handle_msg` text COMMENT '执行-日志',
  `alarm_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
  PRIMARY KEY (`id`),
  KEY `I_trigger_time` (`trigger_time`),
  KEY `I_handle_code` (`handle_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_log_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trigger_day` datetime DEFAULT NULL COMMENT '调度-时间',
  `running_count` int(11) NOT NULL DEFAULT '0' COMMENT '运行中-日志数量',
  `suc_count` int(11) NOT NULL DEFAULT '0' COMMENT '执行成功-日志数量',
  `fail_count` int(11) NOT NULL DEFAULT '0' COMMENT '执行失败-日志数量',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_trigger_day` (`trigger_day`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_logglue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` int(11) NOT NULL COMMENT '任务，主键ID',
  `glue_type` varchar(50) DEFAULT NULL COMMENT 'GLUE类型',
  `glue_source` mediumtext COMMENT 'GLUE源代码',
  `glue_remark` varchar(128) NOT NULL COMMENT 'GLUE备注',
  `add_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_registry` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `registry_group` varchar(50) NOT NULL,
  `registry_key` varchar(255) NOT NULL,
  `registry_value` varchar(255) NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `i_g_k_v` (`registry_group`,`registry_key`,`registry_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(64) NOT NULL COMMENT '执行器AppName',
  `title` varchar(12) NOT NULL COMMENT '执行器名称',
  `address_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '执行器地址类型：0=自动注册、1=手动录入',
  `address_list` text COMMENT '执行器地址列表，多地址逗号分隔',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '账号',
  `password` varchar(50) NOT NULL COMMENT '密码',
  `role` tinyint(4) NOT NULL COMMENT '角色：0-普通用户、1-管理员',
  `permission` varchar(255) DEFAULT NULL COMMENT '权限：执行器ID列表，多个逗号分割',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_lock` (
  `lock_name` varchar(50) NOT NULL COMMENT '锁名称',
  PRIMARY KEY (`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `xxl_job_group`(`id`, `app_name`, `title`, `address_type`, `address_list`, `update_time`) VALUES (1, 'xxl-job-executor-sample', '示例执行器', 0, NULL, '2018-11-03 22:21:31' );
INSERT INTO `xxl_job_info`(`id`, `job_group`, `job_desc`, `add_time`, `update_time`, `author`, `alarm_email`, `schedule_type`, `schedule_conf`, `misfire_strategy`, `executor_route_strategy`, `executor_handler`, `executor_param`, `executor_block_strategy`, `executor_timeout`, `executor_fail_retry_count`, `glue_type`, `glue_source`, `glue_remark`, `glue_updatetime`, `child_jobid`) VALUES (1, 1, '测试任务1', '2018-11-03 22:21:31', '2018-11-03 22:21:31', 'XXL', '', 'CRON', '0 0 0 * * ? *', 'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2018-11-03 22:21:31', '');
INSERT INTO `xxl_job_user`(`id`, `username`, `password`, `role`, `permission`) VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);
INSERT INTO `xxl_job_lock` ( `lock_name`) VALUES ( 'schedule_lock');

commit;
```



#### my.cnf

```properties
[mysqld]
log-bin=mysql-bin
server-id=1
```



#### redis.conf

选择对应版本：[redis/redis.conf at 7.0 · redis/redis (github.com)](https://github.com/redis/redis/blob/7.0/redis.conf)

```properties
protected-mode no
port 6379
tcp-backlog 511
timeout 0
tcp-keepalive 300
daemonize no
pidfile /var/run/redis_6379.pid
loglevel notice
logfile ""
databases 16
always-show-logo no
set-proc-title yes
proc-title-template "{title} {listen-addr} {server-mode}"
stop-writes-on-bgsave-error yes
rdbcompression yes
rdbchecksum yes
dbfilename dump.rdb
rdb-del-sync-files no
dir ./
replica-serve-stale-data yes
replica-read-only yes
repl-diskless-sync yes
repl-diskless-sync-delay 5
repl-diskless-sync-max-replicas 0
repl-diskless-load disabled
repl-disable-tcp-nodelay no
replica-priority 100
acllog-max-len 128
requirepass ${password}
lazyfree-lazy-eviction no
lazyfree-lazy-expire no
lazyfree-lazy-server-del no
replica-lazy-flush no
lazyfree-lazy-user-del no
lazyfree-lazy-user-flush no
oom-score-adj no
oom-score-adj-values 0 200 800
disable-thp yes
appendonly no
appendfilename "appendonly.aof"
appenddirname "appendonlydir"
appendfsync everysec
no-appendfsync-on-rewrite no
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb
aof-load-truncated yes
aof-use-rdb-preamble yes
aof-timestamp-enabled no
slowlog-log-slower-than 10000
slowlog-max-len 128
latency-monitor-threshold 0
notify-keyspace-events ""
hash-max-listpack-entries 512
hash-max-listpack-value 64
list-max-listpack-size -2
list-compress-depth 0
set-max-intset-entries 512
zset-max-listpack-entries 128
zset-max-listpack-value 64
hll-sparse-max-bytes 3000
stream-node-max-bytes 4096
stream-node-max-entries 100
activerehashing yes
client-output-buffer-limit normal 0 0 0
client-output-buffer-limit replica 256mb 64mb 60
client-output-buffer-limit pubsub 32mb 8mb 60
hz 10
dynamic-hz yes
aof-rewrite-incremental-fsync yes
rdb-save-incremental-fsync yes
jemalloc-bg-thread yes
```



### kiligz-seata.yml

```yml
version: '3.8'
services:
  # seata
  seata-server:
    image: seataio/seata-server:1.5.2
    container_name: seata-server
    ports:
      - 7091:7091
      - 8091:8091
    environment:
      - TZ=Asia/Shanghai
      - SEATA_IP=${host}
      - SEATA_PORT=8091
    volumes:
      # seata配置
      - ./seata/application.yml:/seata-server/resources/application.yml
```



#### application.yml

```yml
server:
  port: 7091
spring:
  application:
    name: seata-server
logging:
  config: classpath:logback-spring.xml
  file:
    path: ./logs/seata
console:
  user:
    username: seata
    password: seata
seata:
  config:
    type: nacos
    nacos:
      server-addr: ${host}:8848
      group: SEATA_GROUP
      namespace: seata
      username: nacos
      password: nacos
      data-id: seataServer.properties
  registry:
    type: nacos
    nacos:
      application: seata-server
      server-addr: ${host}:8848
      group: SEATA_GROUP
      namespace: seata
      cluster: default
      username: nacos
      password: nacos
  security:
    secretKey: SeataSecretKey0c382ef121d778043159209298fd40bf3850a017
    tokenValidityInMilliseconds: 1800000
    ignore:
      urls: /,/**/*.css,/**/*.js,/**/*.html,/**/*.map,/**/*.svg,/**/*.png,/**/*.ico,/console-fe/public/**,/api/v1/auth/login
```



#### seataServer.properties

```properties
#For details about configuration items, see https://seata.io/zh-cn/docs/user/configurations.html
#Transport configuration, for client and server
transport.type=TCP
transport.server=NIO
transport.heartbeat=true
transport.enableTmClientBatchSendRequest=false
transport.enableRmClientBatchSendRequest=true
transport.enableTcServerBatchSendResponse=false
transport.rpcRmRequestTimeout=30000
transport.rpcTmRequestTimeout=30000
transport.rpcTcRequestTimeout=30000
transport.threadFactory.bossThreadPrefix=NettyBoss
transport.threadFactory.workerThreadPrefix=NettyServerNIOWorker
transport.threadFactory.serverExecutorThreadPrefix=NettyServerBizHandler
transport.threadFactory.shareBossWorker=false
transport.threadFactory.clientSelectorThreadPrefix=NettyClientSelector
transport.threadFactory.clientSelectorThreadSize=1
transport.threadFactory.clientWorkerThreadPrefix=NettyClientWorkerThread
transport.threadFactory.bossThreadSize=1
transport.threadFactory.workerThreadSize=default
transport.shutdown.wait=3
transport.serialization=seata
transport.compressor=none

#Transaction routing rules configuration, only for the client
service.vgroupMapping.KZP_GROUP=default
#If you use a registry, you can ignore it
service.default.grouplist=127.0.0.1:8091
service.enableDegrade=false
service.disableGlobalTransaction=false

#Transaction rule configuration, only for the client
client.rm.asyncCommitBufferLimit=10000
client.rm.lock.retryInterval=10
client.rm.lock.retryTimes=30
client.rm.lock.retryPolicyBranchRollbackOnConflict=true
client.rm.reportRetryCount=5
client.rm.tableMetaCheckEnable=true
client.rm.tableMetaCheckerInterval=60000
client.rm.sqlParserType=druid
client.rm.reportSuccessEnable=false
client.rm.sagaBranchRegisterEnable=false
client.rm.sagaJsonParser=fastjson
client.rm.tccActionInterceptorOrder=-2147482648
client.tm.commitRetryCount=5
client.tm.rollbackRetryCount=5
client.tm.defaultGlobalTransactionTimeout=60000
client.tm.degradeCheck=false
client.tm.degradeCheckAllowTimes=10
client.tm.degradeCheckPeriod=2000
client.tm.interceptorOrder=-2147482648
client.undo.dataValidation=true
client.undo.logSerialization=jackson
client.undo.onlyCareUpdateColumns=true
server.undo.logSaveDays=7
server.undo.logDeletePeriod=86400000
client.undo.logTable=undo_log
client.undo.compress.enable=true
client.undo.compress.type=zip
client.undo.compress.threshold=64k
#For TCC transaction mode
tcc.fence.logTableName=tcc_fence_log
tcc.fence.cleanPeriod=1h

#Log rule configuration, for client and server
log.exceptionRate=100

#Transaction storage configuration, only for the server. The file, DB, and redis configuration values are optional.
store.mode=db
store.lock.mode=db
store.session.mode=db
#Used for password encryption
store.publicKey=

#If `store.mode,store.lock.mode,store.session.mode` are not equal to `file`, you can remove the configuration block.
store.file.dir=file_store/data
store.file.maxBranchSessionSize=16384
store.file.maxGlobalSessionSize=512
store.file.fileWriteBufferCacheSize=16384
store.file.flushDiskMode=async
store.file.sessionReloadReadSize=100

#These configurations are required if the `store mode` is `db`. If `store.mode,store.lock.mode,store.session.mode` are not equal to `db`, you can remove the configuration block.
store.db.datasource=druid
store.db.dbType=mysql
store.db.driverClassName=com.mysql.jdbc.Driver
store.db.url=jdbc:mysql://${host}:3306/seata?rewriteBatchedStatements=true
store.db.user=root
store.db.password=${password}
store.db.minConn=5
store.db.maxConn=30
store.db.globalTable=global_table
store.db.branchTable=branch_table
store.db.distributedLockTable=distributed_lock
store.db.queryLimit=100
store.db.lockTable=lock_table
store.db.maxWait=5000

#These configurations are required if the `store mode` is `redis`. If `store.mode,store.lock.mode,store.session.mode` are not equal to `redis`, you can remove the configuration block.
store.redis.mode=single
store.redis.single.host=127.0.0.1
store.redis.single.port=6379
store.redis.sentinel.masterName=
store.redis.sentinel.sentinelHosts=
store.redis.maxConn=10
store.redis.minConn=1
store.redis.maxTotal=100
store.redis.database=0
store.redis.password=
store.redis.queryLimit=100

#Transaction rule configuration, only for the server
server.recovery.committingRetryPeriod=1000
server.recovery.asynCommittingRetryPeriod=1000
server.recovery.rollbackingRetryPeriod=1000
server.recovery.timeoutRetryPeriod=1000
server.maxCommitRetryTimeout=-1
server.maxRollbackRetryTimeout=-1
server.rollbackRetryTimeoutUnlockEnable=false
server.distributedLockExpireTime=10000
server.xaerNotaRetryTimeout=60000
server.session.branchAsyncQueueSize=5000
server.session.enableBranchAsyncRemove=false
server.enableParallelRequestHandle=false

#Metrics configuration, only for the server
metrics.enabled=false
metrics.registryType=compact
metrics.exporterList=prometheus
metrics.exporterPrometheusPort=9898
```





## 统一异常处理

### GlobalExceptionHandler

> 全局统一异常处理

```java
package com.kiligz.kzp.commons.exception;

import com.kiligz.kzp.commons.domain.Status;
import com.kiligz.kzp.commons.enums.StatusEnum;
import com.kiligz.kzp.commons.vo.RespVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.List;

/**
 * 统一异常处理
 *
 * @author Ivan
 * @date 2022/11/10 14:59
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Kzp异常处理
     */
    @ExceptionHandler({KzpException.class})
    public RespVO<?> handlerException(KzpException e) {
        Status status = e.getStatus();
        List<String> errorMsgList = e.getErrorMsgList();
        if (errorMsgList.isEmpty()) {
            log.error("KzpException: {}", status, e);
        } else {
            log.error("KzpException: {} {}", status, errorMsgList, e);
        }
        return RespVO.fail(status);
    }

    /**
     * Rpc异常处理
     */
    @ExceptionHandler({RpcException.class})
    public RespVO<?> handlerException(RpcException e) {
        Status status = Status.global(StatusEnum.NO_PROVIDER);
        log.error("RpcException: {}", status, e);
        return RespVO.fail(HttpStatus.SERVICE_UNAVAILABLE, status);
    }

    /**
     * Runtime异常处理
     */
    @ExceptionHandler({RuntimeException.class})
    public RespVO<?> handlerException(Exception e) {
        log.error("RuntimeException: ", e);
        return RespVO.fail();
    }

    /**
     * 404异常处理
     */
    @RestController
    public static class NotFoundController implements ErrorController {
        @RequestMapping("/error")
        public RespVO<?> error(HttpServletRequest request) {
            // 获取error转发前uri
            ServletRequest fromRequest = ((HttpServletRequestWrapper) request).getRequest();
            String requestURI = ((RequestFacade) fromRequest).getRequestURI();

            String errorMsg = "Not found uri: " + requestURI;
            log.error(errorMsg);
            return RespVO.fail(HttpStatus.NOT_FOUND, Status.fail(), errorMsg);
        }
    }
}

```



### RouteExceptionHandler

> 路由转发异常处理

```java
package com.kiligz.kzp.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kiligz.kzp.commons.domain.Status;
import com.kiligz.kzp.commons.utils.JsonUtil;
import com.kiligz.kzp.commons.vo.RespVO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * 路由转发异常处理
 *
 * @author Ivan
 * @date 2022/11/10 23:10
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class RouteExceptionHandler implements WebExceptionHandler {

    @NotNull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, @NotNull Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        }

        return response.writeWith(
                Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    try {
                        RespVO<Void> respVO = RespVO.fail(response.getStatusCode(), Status.fail());
                        return bufferFactory.wrap(
                                JsonUtil.getMapper().writeValueAsBytes(respVO));
                    } catch (JsonProcessingException e) {
                        log.error("Error writing response", e);
                        return bufferFactory.wrap(new byte[0]);
                    }
                }));
    }
}
```



### KzpException

> 自定义异常

```java
package com.kiligz.kzp.commons.exception;

import com.kiligz.kzp.commons.domain.Status;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Kzp异常
 *
 * @author Ivan
 * @date 2022/11/10 14:26
 */
@Getter
public class KzpException extends RuntimeException {
    /**
     * 状态
     */
    private final Status status;

    /**
     * 错误信息列表
     */
    private final List<String> errorMsgList = new ArrayList<>();

    public KzpException(Status status, String... errorMsgArr) {
        this.status = status;
        Collections.addAll(errorMsgList, errorMsgArr);
    }
}
```



```java
    /**
     * 参数校验(Valid)异常
     */
    PARAM_VALID_ERROR("U00P01", 400, "参数校验异常"),
    /**
     * Token已过期
     */
    TOKEN_EXPIRED("U00A02", 401, "Token已过期"),
    /**
     * 未授权，不能访问
     */
    AUTH_INVALID("U00A05", 403, "未授权，不能访问"),
    /**
     * 服务器繁忙，请稍后重试
     */
    SERVER_BUSY("U00S00", 500,"服务器繁忙"),
    /**
     * 未知异常，无法识别的异常
     */
    SERVER_ERROR("U00O99", 500, "未知异常"),
```



### StatusEnum

> 状态枚举

```java
package com.kiligz.kzp.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态枚举<br/>
 * 第一级：
 *      A：admin  B：business  Z：global<br/>
 * 第二级：
 *      01：kzp-gateway     02：kzp-api     03：kzp-dispatcher
 *      04：kzp-processor   05：kzp-pusher  06：kzp-scheduler
 *      07：kzp-data-house  08：kzp-admin   99：global<br/>
 * 第三级：
 *      A：认证授权    B：业务错误    C：缓存错误
 *      D：数据库错误  F：文件IO错误  N：网络错误
 *      R：RPC错误    S：系统错误    Z：global<br/>
 * 第四级：
 *      Z99：global
 *
 * @author Ivan
 * @date 2022/11/9 17:11
 */
@AllArgsConstructor
@Getter
public enum StatusEnum {

    SUCCESS("111111", "请求成功"),
    FAIL("000000", "请求失败"),


    UNKNOWN("Z99", "未知异常"),
    NULL_POINTER("B01", "空指针异常"),
    NO_PROVIDER("R01", "没有服务提供方")

    ;

    private final String code;
    private final String msg;

    public static final String GATEWAY = "G01";
    public static final String API = "B01";
    public static final String DISPATCHER = "B02";
    public static final String PROCESSOR = "B02";
    public static final String DATA_HOUSE = "B03";
    public static final String PUSHER = "B04";
    public static final String SCHEDULER = "B05";
    public static final String ADMIN = "A01";
    public static final String GLOBAL = "Z99";
}

```





### Status

> 状态实体

```java
package com.kiligz.kzp.commons.domain;

import com.kiligz.kzp.commons.constant.SymbolConstant;
import com.kiligz.kzp.commons.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 状态
 *
 * @author Ivan
 * @date 2022/11/10 18:06
 */
@Data
@AllArgsConstructor
public class Status {
    /**
     * 状态码
     */
    private final String code;

    /**
     * 状态信息
     */
    private final String msg;

    private Status(StatusEnum statusEnum) {
        this(statusEnum, "");
    }

    private Status(StatusEnum statusEnum, String prefix) {
        this.code = prefix + statusEnum.getCode();
        this.msg = statusEnum.getMsg();
    }

    public static Status success() {
        return new Status(StatusEnum.SUCCESS);
    }

    public static Status fail() {
        return new Status(StatusEnum.FAIL);
    }

    public static Status gateway(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.GATEWAY);
    }

    public static Status api(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.API);
    }

    public static Status dispatcher(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.DISPATCHER);
    }

    public static Status processor(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.PROCESSOR);
    }

    public static Status dataHouse(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.DATA_HOUSE);
    }

    public static Status pusher(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.PUSHER);
    }

    public static Status scheduler(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.SCHEDULER);
    }

    public static Status admin(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.ADMIN);
    }

    public static Status global(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.GLOBAL);
    }

    @Override
    public String toString() {
        return code + SymbolConstant.Hyphen + msg;
    }
}

```



### VO

```java
package com.kiligz.kzp.commons.vo;

import com.kiligz.kzp.commons.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 响应结果vo
 *
 * @author Ivan
 * @date 2022/11/9 16:36
 */
@Data
@AllArgsConstructor
public class RespVO<T> {
    /**
     * http响应码
     */
    private int code;

    /**
     * 服务状态
     */
    private Status status;

    /**
     * 数据
     */
    private T data;

    public RespVO(HttpStatus httpStatus, Status status, T data) {
        this(httpStatus.value(), status, data);
    }

    public static RespVO<Void> success() {
        return success(null);
    }

    public static <T> RespVO<T> success(T data) {
        return new RespVO<>(HttpStatus.OK, Status.success(), data);
    }

    public static RespVO<Void> fail() {
        return fail(Status.fail());
    }

    public static RespVO<Void> fail(Status status) {
        return fail(HttpStatus.INTERNAL_SERVER_ERROR, status);
    }

    public static RespVO<Void> fail(HttpStatus httpStatus) {
        return fail(httpStatus, Status.fail());
    }

    public static RespVO<Void> fail(HttpStatus httpStatus, Status status) {
        return fail(httpStatus, status, null);
    }

    public static <T> RespVO<T> fail(Status status, T data) {
        return fail(HttpStatus.INTERNAL_SERVER_ERROR, status, data);
    }

    public static <T> RespVO<T> fail(HttpStatus httpStatus, Status status, T data) {
        return new RespVO<>(httpStatus, status, data);
    }
}

```





## 日志

> logback-spring.xml，可存放在common模块

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 日志文件名，获取应用名作为日志文件名 -->
    <springProperty name="log.name" source="spring.application.name"/>
    <!-- 日志级别 -->
    <property name="log.level" value="info"/>
    <!-- SpringBoot日志级别 -->
    <property name="spring.log.level" value="info"/>
    <!-- 日志存放路径 -->
    <property name="log.path" value="./logs"/>
    <!-- 日志保存天数 -->
    <property name="log.save" value="30"/>
    <!-- 日志文件大小 -->
    <property name="log.size" value="20MB"/>
    <!-- 日志输出格式 -->
    <property name="log.pattern.console"
              value="%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) - %boldMagenta([%thread]) %yellow(%logger) %blue([%method,%line]) - %msg%n"/>
    <property name="log.pattern.file"
              value="%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level - [%thread] %logger [%method,%line] - %msg%n"/>

    <!-- 控制台输出 -->
    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${log.pattern.console}</pattern>
        </encoder>
    </appender>

    <!-- 防止加载两次，需引入janino依赖 -->
    <if condition='isDefined("log.name")'>
        <then>
            <!-- 系统日志输出 -->
            <appender name="info_file" class="ch.qos.logback.core.rolling.RollingFileAppender">
                <file>${log.path}/${log.name}-info.log</file>
                <encoder>
                    <pattern>${log.pattern.file}</pattern>
                </encoder>
                <!-- 滚动策略：基于时间创建日志文件 -->
                <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
                    <!-- 日志文件名格式 -->
                    <fileNamePattern>${log.path}/${log.name}_info_%d{yyyy-MM-dd}_%i.log</fileNamePattern>
                    <maxFileSize>${log.size}</maxFileSize>
                    <!-- 日志文件保留天数：30天 -->
                    <maxHistory>${log.save}</maxHistory>
                </rollingPolicy>
                <filter class="ch.qos.logback.classic.filter.LevelFilter">
                    <level>ERROR</level>
                    <onMatch>ACCEPT</onMatch>
                    <onMismatch>NEUTRAL</onMismatch>
                </filter>
                <filter class="ch.qos.logback.classic.filter.LevelFilter">
                    <level>WARN</level>
                    <onMatch>ACCEPT</onMatch>
                    <onMismatch>NEUTRAL</onMismatch>
                </filter>
                <filter class="ch.qos.logback.classic.filter.LevelFilter">
                    <level>INFO</level>
                    <onMatch>ACCEPT</onMatch>
                    <onMismatch>DENY</onMismatch>
                </filter>
            </appender>

            <appender name="warn_file" class="ch.qos.logback.core.rolling.RollingFileAppender">
                <file>${log.path}/${log.name}-warn.log</file>
                <encoder>
                    <pattern>${log.pattern.file}</pattern>
                </encoder>
                <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
                    <fileNamePattern>${log.path}/${log.name}_warn_%d{yyyy-MM-dd}_%i.log</fileNamePattern>
                    <maxFileSize>${log.size}</maxFileSize>
                    <maxHistory>${log.save}</maxHistory>
                </rollingPolicy>
                <filter class="ch.qos.logback.classic.filter.LevelFilter">
                    <level>ERROR</level>
                    <onMatch>ACCEPT</onMatch>
                    <onMismatch>NEUTRAL</onMismatch>
                </filter>
                <filter class="ch.qos.logback.classic.filter.LevelFilter">
                    <level>WARN</level>
                    <onMatch>ACCEPT</onMatch>
                    <onMismatch>DENY</onMismatch>
                </filter>
            </appender>

            <appender name="error_file" class="ch.qos.logback.core.rolling.RollingFileAppender">
                <file>${log.path}/${log.name}-error.log</file>
                <encoder>
                    <pattern>${log.pattern.file}</pattern>
                </encoder>
                <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
                    <fileNamePattern>${log.path}/${log.name}_error_%d{yyyy-MM-dd}_%i.log</fileNamePattern>
                    <maxFileSize>${log.size}</maxFileSize>
                    <maxHistory>${log.save}</maxHistory>
                </rollingPolicy>
                <filter class="ch.qos.logback.classic.filter.LevelFilter">
                    <level>ERROR</level>
                    <onMatch>ACCEPT</onMatch>
                    <onMismatch>DENY</onMismatch>
                </filter>
            </appender>
        </then>
    </if>

    <root level="${log.level}">
        <appender-ref ref="console"/>
        <if condition='isDefined("log.name")'>
            <then>
                <appender-ref ref="info_file"/>
                <appender-ref ref="warn_file"/>
                <appender-ref ref="error_file"/>
            </then>
        </if>
    </root>

    <!--Spring日志级别控制  -->
    <logger name="org.springframework" level="${spring.log.level}" />
</configuration>
```



## BeanCopy

```java
package com.kiligz.kzp.user.utils;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 使用高速缓存ASM实现的beanCopy
 *
 * @author Ivan
 * @date 2020/5/14 5:16 下午
 */
public class BeanUtil {
    /**
     * 方法访问器缓存
     */
    private static final ConcurrentMap<Class<?>, MethodAccess> cache = Maps.newConcurrentMap();

    /**
     * 获取方法访问器
     */
    public static MethodAccess getMethodAccess(Class<?> clazz) {
        if(cache.containsKey(clazz)) {
            return cache.get(clazz);
        }
        MethodAccess methodAccess = MethodAccess.get(clazz);
        cache.putIfAbsent(clazz, methodAccess);
        return methodAccess;
    }

    /**
     * 复制属性并返回to对象
     */
    public static <F, T> T copy(F from, T to) {
        Class<?> fromClass = from.getClass();
        Class<?> toClass = to.getClass();
        MethodAccess fromMethodAccess = getMethodAccess(fromClass);
        MethodAccess toMethodAccess = getMethodAccess(toClass);

        Field[] fromFields = fromClass.getDeclaredFields();
        List<String> toNames = getFieldNames(toClass);

        for(Field field : fromFields) {
            String name = field.getName();
            if (toNames.contains(name)) {
                String capitalize = StringUtils.capitalize(name);
                String fromPrefix = field.getType() == Boolean.class ? "is" : "get";

                Object value = fromMethodAccess.invoke(from, fromPrefix + capitalize);
                toMethodAccess.invoke(to, "set" + capitalize, value);
            }
        }
        return to;
    }

    /**
     * 获取所有属性名
     */
    private static List<String> getFieldNames(Class<?> toClass) {
        return Arrays.stream(toClass.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());
    }
}
```





## 缓存

> 通过@Cacheable开启，支持配置过期时间

### RedisConfig

```java
package com.kiligz.kzp.common.config;

import com.kiligz.kzp.common.utils.JsonUtil;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Objects;

/**
 * Redis配置，开启缓存
 *
 * @author Ivan
 * @since 2022/11/11
 */
@Configuration
@EnableCaching
public class RedisConfig {
    /**
     * redis模板配置
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer =
                new GenericJackson2JsonRedisSerializer(JsonUtil.getRedisMapper());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * redisCache管理Bean
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisTemplate<String, Object> redisTemplate) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(
                Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()));
        return new KzpRedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }
}

```



### KzpRedisCacheManager

```java
package com.kiligz.kzp.common.config;

import com.kiligz.kzp.common.constant.Consts;
import lombok.NonNull;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.time.Duration;

/**
 * 自定义RedisCacheManager
 *
 * @author Ivan
 * @since 2022/12/27
 */
public class KzpRedisCacheManager extends RedisCacheManager {

    public KzpRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    /**
     * 构造缓存的方法，cacheNames值：xxx-一天左右失效，xxx#10-10s失效，xxx#-永不失效
     */
    @NonNull
    @Override
    protected RedisCache createRedisCache(@NonNull String name, RedisCacheConfiguration cacheConfig) {
        if (name.contains(Consts.POUND)) {
            String[] arr = name.split(Consts.POUND);
            name = arr[0];
            if (arr.length == 2) {
                cacheConfig = cacheConfig.entryTtl(Duration.ofSeconds(Long.parseLong(arr[1])));
            }
        } else {
            cacheConfig = cacheConfig.entryTtl(Duration.ofSeconds(Consts.RANDOM_DAY));
        }
        return super.createRedisCache(name, cacheConfig);
    }
}

```



## 数据库表设计

- 经常查询的信息、不经常查询的信息

