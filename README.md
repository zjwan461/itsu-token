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

**使用者可以在<a href="https://github.com/zjwan461/itsu-token-sample/">itsu-token-sample</a>中查看到具体的使用案例**

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

3.  在需要提供token校验的接口上添加@Token注解

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

    ![QA2JR.png](https://b1.sbimg.org/file/chevereto-jia/2020/12/10/QA2JR.png)

    通常情况下我建议直接使用内置的schema完成自动建表，如果使用者一定要使用自定义的schema建表，还需要开启custom-schema功能，并给出自定义的schema-location。并且需要在IOC容器中注入TableSample接口的实现类。并且使用特定的“表结构修饰注解”给这个类打上标记，以适配使用者在自定义schema脚本建表的表接口。请看如下一个完整的使用案例。

    我准备了一个example.sql作为自定义建表的schema文件

    ```sql
    CREATE TABLE IF NOT EXISTS sys_token (
        id char(32) not null primary key,
        name varchar(255) not null,
        simple_token char(32) not null
    );
    ```

    同时我也开启了auto-create-table和custom-schema功能，并给出了schema文件的所在位置。

    ```yaml
    itsu-token:
      enable: true
      init:
        auto-create-table: true
        schema-location: classpath:example.sql
        custom-schema: true  #声明开启自定义schema功能
      web-register:
        enable: true
    ```

    此时我还需要自定义一个tableSample类，如下。使用TableDesc,TableId,SimpleToken,SysName这几个注解完成和自定义example.sql的表结构统一。另一种验证方式RSA和这种方式是一致的，只不过不再使用SimpleToken这个注解，取而代之的是PrivateKey, PublicKey这两个注解。用于表示RSA非对称加密中的“公钥”和“私钥”。

    ```java
    @TableDesc("sys_token")
    public class MySimpleTableSample implements TableSample {
    	@TableId
    	private String id;
        
    	@SimpleToken("simple_token")
    	private String token;
    	
    	@SysName("name")
    	private String name;
    	
    }
    ```

#####  三、自定义Token验证规则

1. itsu-token提供了自定义的Token校验机制。让使用者可以根据自己的需求，个性化的来定制自己所需的token校验。你需要继承一个com.itsu.itsutoken.checker.CusTokenChecker抽象类，并重写check()、getTableSample() 方法。

   ```java
   public class MyCustomTokenChecker extends CusTokenChecker<MyCustomTableSample> {
   
   	@Override
   	public void check(JoinPoint joinPoint) throws TokenCheckException {
   		// do some check here
   	}
   
   	@Override
   	public MyCustomTableSample getTableSample() {
   		return new MyCustomTableSample();
   	}
   
   }
   ```

2. 为何要重写getTableSample()方法？因为对于自定义token校验来说，使用者在数据库中存储的token信息是不明确的，也无法知道使用者需要进行那种方式的token校验。所以需要给出数据库中存储token信息的表的结构。对于这种要求，itsu-token是通过TableSample类+TableSample注解来实现的。这一点在上文中的custom-schema中也有提及。那也就意味着如果使用者使用了自动建表功能，那就需要同时给出schema-location，然后还需要定义TableSample的具体实现。当然手动建表也需要提供TableSample的具体实现。

   ```java
   @TableDesc("tb_token")
   public class MyCustomTableSample implements TableSample {
   
   	@TableId
   	private String id;
   	
   	@TableField("custom_field")
   	private String token;
   }
   
   ```

3. 工具方法

   CustomTokenChecker提供了三个基本工具方法，分别是getTableFieldNames()，getTableName()，getTableId()用来获取用户自定义TableSample的字段&表的定义。可以方便后期使用者获取Table的详细信息。我知道这样看起来多此一举，因为我大部分会用到自定义token校验的使用者都会清楚的知道表结构是什么样子的，直接写原生的sql查询数据就可以达到目标。这里只是提供一种方式。

   ```java
   protected List<String> getTableFieldNames() throws TokenCheckException {
   		try {
   			return ClassUtil.getTableFieldValues(tableSample.getClass());
   		} catch (Exception e) {
   			throw new TokenCheckException(e);
   		}
   	}
   
   protected String getTableName() throws TokenCheckException {
   		try {
   			return AnnotationUtil.getAnnotationValue(tableSample.getClass(), TableDesc.class);
   		} catch (UtilException e) {
   			throw new TokenCheckException(e);
   		}
   }
   
   protected String getTableId() throws TokenCheckException {
   		try {
   			return ClassUtil.getId(tableSample.getClass());
   		} catch (Exception e) {
   			throw new TokenCheckException(e);
   		}
   }
   ```

#####  四、web register 功能

​	目前itsu-token仅对默认支持的Token校验类型“simple”，”rsa“ 提供了web register功能。至于web register，顾名思义就是提供了集成化的非侵入式的通过web方式来向系统注册token的功能。在itsu-token中，这一功能，默认是关闭的。需要在配置文件中开启。如下是一个web-register功能的完整配置。可以看到的是除了web register的开关位，还提供了几个选项。详细的解释可以参考下文的定义。<font color='red'>需要注意的是，web register目前暂时不支持自定义校验，也就是custom token check。</font>

```yaml
itsu-token:
  web-register:
    enable: true
    user: admin
    password: password
    register-url: /register.html
    token-list-url: /tokenlist.html
```

​	当你开启web register后，想要访问到tokenlist.html, 此时itsu-token会要求你先进行登录。账号和密码也就是在上述代码中所配置的。

![QB2rA.png](https://b1.sbimg.org/file/chevereto-jia/2020/12/11/QB2rA.png)

当你完成登录后，就能访问register.html和tokenlist.html了。你可以在register页面做token的注册工作，在tokenlist.html查看已注册的token列表。

![QB0wY.png](https://b1.sbimg.org/file/chevereto-jia/2020/12/11/QB0wY.png)

![QBkG4.png](https://b1.sbimg.org/file/chevereto-jia/2020/12/11/QBkG4.png)

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request
