# itsu-token

#### 介绍
itsu-token作为SpringBoot 的starter 模块依赖SpringBoot环境。使用了hu-tool工具包作为底层开发工具包。依赖Spring Aop进行token校验。支持token的持久化保存，目前仅支持mysql数据库。支持token的可视化构建和管理。token构建，token list页面使用bootstrap和jquery-confirm进行设计。实现了第三方系统调用时的token校验、token注册等功能

#### 软件架构
Spring, SpringBoot, Jquery, Hutool, Spring Aop


#### 安装教程

1.  git clone https://github.com/zjwan461/itsu-token.git
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

#####  一、快速上手

1. 第一步需要导入maven依赖。

   ```xml
   <dependency>
   	<groupId>com.itsu</groupId>
   	<artifactId>itsu-token</artifactId>
   	<version>1.0</version>
   </dependency>
   ```

2.  在application.yml或application.properties中开启itsu-token功能

    ```yml
    itsu-token:
      enable: true
    ```

3.  在Application启动入口添加itsu-token的包扫描，确保itsu-token的IOC组件能够顺利被Spring IOC容器加载

    ```java
    @SpringBootApplication
    @ComponentScan(basePackages = { "com.itsu.itsutoken", "com.itsu.token.test" })
    public class ItsuTokenTestApplication {
    
    	public static void main(String[] args) {
    		SpringApplication.run(ItsuTokenTestApplication.class, args);
    	}
    }
    
    ```

4.  在需要提供token校验的接口上添加@Token注解

    ```java
    @RestController
    public class TestController {
    	@Token
    	@GetMapping("/index")
    	public String idx() {
    		return "index";
    	}
    }
    ```

#####  二、自动创建token数据库表

 1. 开启自动建表功能

 2. 提供自动建表schema.sql

    ```yml
    itsu-token:
      enable: true
      init:
        auto-create-table: true
        schema-location: classpath:example.sql
    ```

    需要留意的是，即使使用者不提供schema，itsu-token也内置了两种建表方案。他们分别是rsaSchema.sql和simpleSchema.sql，分别对应了系统内置的两种校验方式“RSA” & "SIMPLE"

    ![QQBvD.png](https://b1.sbimg.org/file/chevereto-jia/2020/12/09/QQBvD.png)

    通常情况下我建议直接使用内置的schema完成自动建表，如果使用者一定要使用自定义的schema建表，还需要开启custom-schema功能，并给出自定义的schema-location。

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request
