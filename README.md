# itsu-token

#### 介绍
itsu-token作为SpringBoot 的starter 模块依赖SpringBoot环境。使用了hu-tool工具包作为底层开发工具包。依赖Spring Aop进行token校验。支持token的持久化保存，目前仅支持mysql数据库。支持token的可视化构建和管理。token构建，token list页面使用bootstrap和jquery-confirm进行设计。实现了第三方系统调用时的token校验、token注册等功能

#### 软件架构
​Spring, SpringBoot, Jquery, Hutool, Spring Aop


#### 安装教程

1.  git clone 
2.  mvn clean install
3.  在需要使用的项目中添加新构建的maven依赖
```xml
<dependency>
    <groupId>com.itsu</groupId>
        <artifactId>itsu-token</artifactId>
    <version>1.0</version>
</dependency>
```

#### 使用说明

1.  第一步需要导入maven依赖，在需要进行校验的接口上添加@Token(required=true)注解.
2.  修改application.yml或application.properties配置文件，可参考下述配置详解。如下提供一个配置参考

```yml
itsu-token:
  enable: true
  table-class: com.itsu.itsutoken.table.RSATableSample
  type: RSA
  web-register:
    enable: true
    # register-url: registerToken
    token-list-url: /listToken 
  system:
    encrypt-base64: true
  init:
    auto-create-table: true
    # schema-location: classpath:schema/simpleSchema.sql
spring:
  datasource:
    url: jdbc:mysql://192.168.22.122:3306/local_db
    username: root
    password: password
debug: true
#    driver-class-name: com.mysql.jdbc.Driver
```
3.  默认提供了Simple & RSA 两种token校验模式，也支持自定义校验,需要继承com.itsu.itsutoken.checker.TokenChecker抽象类并实现check()方法。这里留的自由度比较大，使用者可以随意发挥

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request