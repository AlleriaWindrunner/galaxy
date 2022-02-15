# Galaxy
Micro-Services Framework Base Spring Cloud

## Spring Cloud 核心组件概览图

![Spring Cloud 核心组件概览图](https://github.com/AlleriaWindrunner/galaxy/blob/master/doc/image/component%20overview%20diagram%20of%20spring%20cloud.png)

## Galaxy 核心组件概览图

![Galaxy 核心组件概览图](https://github.com/AlleriaWindrunner/galaxy/blob/master/doc/image/component%20overview%20diagram%20of%20galaxy.png)

## 基本规范

* 除接口外，其他各业务模块不允许直接使用第三方jar包，需要封装后再使用.
* 日期，时间使用java8提供的LocalDate、LocalDateTime、LocalTime，框架已提供支持.
* 在application.yml 增加业务参数，必须以模块名开头. 如用户模块：

```yaml
fap :
  test : testparam
    
```

*   控制器层Controller, 服务层Service，数据访问层Dao，传输对象命名为DTO，持久化对象DO， 业务对象BO
*   Controller 类必须继承基类 [BaseController](/lemon-framework/src/main/java/com/galaxy/lemon/framework/controller/BaseController.java)
*   Service 类必须继承基类 [BaseService](/lemon-framework/src/main/java/com/galaxy/lemon/framework/service/BaseService.java)
*   Dao 类必须继承基类 [BaseDao](/lemon-framework/src/main/java/com/galaxy/lemon/framework/dao/BaseDao.java)
*   DO 类必须继承基类 [BaseDO](), 且必须被 [@DataObject]() 注解
*   BO 类，Controller 层调用Service层传输对象
*   DTO 类 [BaseDTO](/lemon-framework/src/main/java/com/galaxy/lemon/framework/data/BaseDTO.java) 及其子类[GenericDTO]() 和 [GenericRspDTO]()类型，接口数据对象的传输使用
*   任何暴露的接口返回类型必须是[GenericRspDTO](/lemon-framework/src/main/java/com/galaxy/lemon/framework/data/GenericRspDTO.java)类型或其子类型，输入参数中必须有一个参数是[GenericDTO]()类型或其子类型，建议就一个GenericDTO类型参数
*   暴露接口仅仅只需要返回消息码时，返回对象应定义为 [GenericRspDTO<NoBody>]()
*   Redis 缓存的使用，任何KEY必须带前缀，如ID生成前缀是IDGEN，缓存的前缀是CACHE，累计的前缀是CUMULATIVE；原则上不允许使用框架提供之外的方法访问redis，如果必须使用，注入 RedisTemplate 对象
*   java开发规范遵循[阿里巴巴Java开发手册](/%E6%8A%80%E6%9C%AF%E6%96%87%E6%A1%A3/%E8%A7%84%E8%8C%83%E6%96%87%E6%A1%A3/%E9%98%BF%E9%87%8C%E5%B7%B4%E5%B7%B4Java%E5%BC%80%E5%8F%91%E6%89%8B%E5%86%8Cv1.2.0.pdf)
*   Restful 规范遵循[RESTful\_API设计指南1](/%E6%8A%80%E6%9C%AF%E6%96%87%E6%A1%A3/%E8%A7%84%E8%8C%83%E6%96%87%E6%A1%A3/RESTful\_API%E8%AE%BE%E8%AE%A1%E6%8C%87%E5%8D%971.md)
*   所有的Feign interface 开发只引入下面的包,防止第三方引入接口时包冲突

```gradle
dependencies {
    compile("com.galaxy:lemon-interface")
}
```

##  工具类
+   金额计算
    [com.galaxy.lemon.common.Amount](/lemon-common/src/main/java/com/galaxy/lemon/common/Amount.java)

+   日期、时间工具类
    [com.galaxy.lemon.common.utils.DateTimeUtils](/lemon-common/src/main/java/com/galaxy/lemon/common/utils/DateTimeUtils.java)

+   Bean 相关、如对象属性拷贝
    [com.galaxy.lemon.common.utils.BeanUtils](/lemon-common/src/main/java/com/galaxy/lemon/common/utils/BeanUtils.java)

+   判断类，如交易调用是否成功的消息码判断，null、空判断等。
    [com.galaxy.lemon.common.utils.JudgeUtils](/lemon-common/src/main/java/com/galaxy/lemon/common/utils/JudgeUtils.java)

+   validate 
    [com.galaxy.lemon.common.utils.Validate](/lemon-common/src/main/java/com/galaxy/lemon/common/utils/Validate.java)

+   字符串操作
    [com.galaxy.lemon.common.utils.StringUtils](/lemon-common/src/main/java/com/galaxy/lemon/common/utils/StringUtils.java)

+   Number操作
    [com.galaxy.lemon.common.utils.NumberUtils](/lemon-common/src/main/java/com/galaxy/lemon/common/utils/NumberUtils.java)

+   资源类
    [com.galaxy.lemon.common.utils.ResourceUtils](/lemon-common/src/main/java/com/galaxy/lemon/common/utils/ResourceUtils.java)

+   随机数、随机字符串、固定长度随机数、固定长度随机字符串
    [com.galaxy.lemon.common.utils.RandomUtils](/lemon-common/src/main/java/com/galaxy/lemon/common/utils/RandomUtils.java)

+   类处理相关
    [com.galaxy.lemon.common.utils.ClassUtils](/lemon-common/src/main/java/com/galaxy/lemon/common/utils/ClassUtils.java)
    
+   IO相关
    [com.galaxy.lemon.common.utils.IOUtils](/lemon-common/src/main/java/com/galaxy/lemon/common/utils/IOUtils.java)
    
+   反射相关
    [com.galaxy.lemon.common.utils.ReflectionUtils](/lemon-common/src/main/java/com/galaxy/lemon/common/utils/ReflectionUtils.java)

+   注解相关
>
>[com.galaxy.lemon.common.utils.AnnotationUtils](/lemon-common/src/main/java/com/galaxy/lemon/common/utils/AnnotationUtils.java)
>
>[com.galaxy.lemon.common.utils.AnnotatedElementUtils](/lemon-common/src/main/java/com/galaxy/lemon/common/utils/AnnotatedElementUtils.java)
>

+   SHA-1/MD5消息摘要的工具类
    [com.galaxy.lemon.common.security.Digests](/lemon-common/src/main/java/com/galaxy/lemon/common/security/Digests.java)

+   HMAC-SHA1消息签名 及 DES/AES对称加密的工具类
    [com.galaxy.lemon.common.security.Cryptos](/lemon-common/src/main/java/com/galaxy/lemon/common/security/Cryptos.java)

+   编码解码工具类
    [com.galaxy.lemon.common.utils.Encodes](/lemon-common/src/main/java/com/galaxy/lemon/common/utils/Encodes.java)

+   异常类（Runtime exception）
    [com.galaxy.lemon.common.exception.LemonException](/lemon-common/src/main/java/com/galaxy/lemon/common/exception/LemonException.java)
    
+   业务异常类
    [com.galaxy.lemon.common.exception.BusinessException](/lemon-common/src/main/java/com/galaxy/lemon/common/exception/BusinessException.java)
    
+   上下文
    [com.galaxy.lemon.common.context.LemonContext](/lemon-common/src/main/java/com/galaxy/lemon/common/context/LemonContext.java)
    
+   ID、序列号生成
    [com.galaxy.lemon.framework.utils.IdGenUtils](/lemon-framework/src/main/java/com/galaxy/lemon/framework/utils/IdGenUtils.java)

+   流水号、消息号、登录用户号、配置参数、记账日期 等平台相关数据
    [com.galaxy.lemon.framework.utils.LemonUtils](/lemon-framework/src/main/java/com/galaxy/lemon/framework/utils/LemonUtils.java)

+   分页
    [com.galaxy.lemon.framework.utils.PageUtils](/lemon-framework/src/main/java/com/galaxy/lemon/framework/utils/PageUtils.java)
    
+   DTO常用类(galaxy platform)
>[com.galaxy.lemon.framework.data.GenericDTO]()
>[com.galaxy.lemon.framework.data.GenericRspDTO]()
>[com.galaxy.lemon.framework.data.PageableDTO]()
>[com.galaxy.lemon.framework.data.PageableRspDTO]()
>[com.galaxy.lemon.framework.data.GenericCmdDTO]()

##  日志

### 基本使用

+   slf4j + logback
+   配置文件: src/main/resource/config/logback-spring.xml
+   Log Level: ERROR, WARN, INFO, DEBUG, or TRACE.
+   生产设置日志级别INFO，建议测试环境设置为DEBUG 或以下级别
+   日志使用方式

```java

    public class UserController {
        private static final Logger logger = LoggerFactory.getLogger(UserController.class);
        
        public GenericDTO<NoBody> testException(GenericDTO<NoBody> object) {
            if(logger.isErrorEnabled()) {
                logger.error("throw a exception, message code is {}.", "PRD00001");
            }
            throw BusinessException.create("PRD00001");
        }
    }

```

---------------------------------------

### 日志脱敏

- [@Desensitization](/lemon-common/src/main/java/com/galaxy/lemon/framework/desensitization/Desensitization.java)注解

  - [type](/lemon-common/src/main/java/com/galaxy/lemon/framework/desensitization/Type.java) 属性

  | 枚举值       | 描述                           |
  | ------------ | ------------------------------ |
  | CHINESE\_NAME | 中文名                         |
  | ID\_CARD      | 身份证号码                     |
  | PHONE\_NO     | 电话号码                       |
  | MOBILE\_NO    | 手机号码                       |
  | ADDRESS      | 地址信息                       |
  | EMAIL        | email                          |
  | BANK\_CARD    | 银行卡号                       |
  | CNAPS\_CODE   | 联行行号                       |
  | LEFT         | 左边脱敏                       |
  | MIDDLE       | 中间脱敏                       |
  | RIGHT        | 右边脱敏                       |
  | ALL          | 全部脱敏                       |
  | SPEL         | spel表达式, 配合 expr 属性使用 |
  |              |                                |

  - expr 属性

    >SPEL表达式，支持如下扩展表达式

  | 扩展表达式 | 功能             | 描述                                                         |
  | ---------- | ---------------- | ------------------------------------------------------------ |
  | SUBSTR     | 字符串截取       | 参数一：被处理的字符串<br/>参数二：开始位置<br/>参数三：结束位置 |
  | SUBSTRE    | 字符串截取到结尾 | 参数一：被处理的字符串<br/>参数二：开始位置                  |
  | STRCAT     | 字符串拼接       | 参数一：字符串1<br/>参数二：字符串2                          |
  | STRCAT3    | 字符串拼接       | 参数一：字符串1<br/>参数二：字符串2<br/>参数三：字符串3      |
  | STRCAT4    | 字符串拼接       | 参数一：字符串1<br/>参数二：字符串2<br/>参数三：字符串3<br/>参数四：字符串4 |
  | STRLEN     | 字符串长度       | 参数一：字符串                                               |
  | LEFTPAD    | 左填充           | 参数一：字符串<br/>参数二：填充字符数量<br/>参数三：填充字符 |
  | RIGHTPAD   | 右填充           | 参数一：字符串<br/>参数二：填充字符数量<br/>参数三：填充字符 |
  |            |                  |                                                              |

- 示例

```java

@ApiModel(value="DesensitizationDTO", description="脱敏测试")
public class DesensitizationDTO extends GenericDTO<NoBody>  {

    private String userNo;

    @Desensitization(type = Type.CHINESE\_NAME)
    private String userName;

    @Desensitization(type = Type.ID\_CARD)
    private String idCard;

    @Desensitization(type = Type.BANK\_CARD)
    private String bankNo;

    @Desensitization(type = Type.EMAIL)
    private String email;

    @Desensitization(type = Type.ADDRESS)
    private String address;

    @Desensitization(Type.CNAPS\_CODE)
    private String cnapsCode;

    @Desensitization(Type.MOBILE\_NO)
    private String mblNo;

    @Desensitization(Type.PHONE\_NO)
    private String phoneNo;

    @Desensitization(Type.LEFT)
    private String left;

    @Desensitization(Type.RIGHT)
    private String right;

    @Desensitization(Type.MIDDLE)
    private String middle;

    @Desensitization(expr = "#SUBSTR(#simpleSpel,0,4)")
    private String simpleSpel;

    @Desensitization(expr = "#STRCAT(#RIGHTPAD(#SUBSTR(#nestSpel,0,2),5,\"*\"),#SUBSTRE(#nestSpel,5))")
    private String nestSpel;

    private NestObject nestObject;

    public static class NestObject {
        @Desensitization(Type.CHINESE\_NAME)
        private String nestUserName;

        @Desensitization(expr = "#STRCAT(#SUBSTR(#nestObjectSpel,0,2),#LEFTPAD(\"\",8,\"*\"))")
        private String nestObjectSpel;
    }
}

```

- 脱敏前数据：


```json

{
    "userNo" : "123456",
    "userName": "eleven",
    "idCard" : "430111199910004011",
    "bankNo" :"6225555555554111",
    "email" : "eleven.hm@vip.163.com",
    "address" : "湖南省长沙市",
    "cnapsCode" : "308584001024",
    "mblNo" : "18684830733",
    "phoneNo" :  "073182000001",
    "left" : "leftInfo",
    "right" : "rightInfo",
    "middle" : "middleInfo",
    "simpleSpel" : "Simple Spel Test",
    "nestSpel" : "Nest Spel Test",
    "nestObject" : {
    	"nestUserName" : "eleven",
    	"nestObjectSpel" : "Nest Object Spel Test"
    }
}


```

- 脱敏后数据：


```json

{
	"userNo": "123456",
	"userName": "e**",
	"idCard": "430***********4011",
	"bankNo": "622555******4111",
	"email": "e******@vip.163.com.com",
	"address": "湖南省长沙市******",
	"cnapsCode": "30**********",
	"mblNo": "186****5009",
	"phoneNo": "********0001",
	"left": "****Info",
	"right": "righ*****",
	"middle": "m********o",
	"simpleSpel": "Simp",
	"nestSpel": "Ne***Spel Test",
	"nestObject": {
		"nestUserName": "e*",
		"nestObjectSpel": "Ne********"
	}
}

```

---------------------------------------

### 访问日志属性忽略

- [@LogIgnore](/lemon-framework/lemon-framework-core/src/main/java/com/galaxy/lemon/common/log/LogIgnore.java)注解

>在DTO的属性增加@LogIgnore，访问日志不会打印该属性

- 示例

  ```
  public abstract class BaseLemonData {
      @ResponseIgnore
      @ApiModelProperty(hidden = true)
      @LogIgnore
      private Locale locale;
  }
  ```
  
### 响应日志属性忽略

- [@ResponseIgnore](/lemon-framework-core/src/main/java/com/cmpay/lemon/framework/annotation/ResponseIgnore.java)注解

>用于注解在渠道应用及接出网关Response DTO属性上，被注解的属性不会生成响应报文。例如框架内部使用的“会计日期”、“GWA”等。

示例

```java
public abstract class BaseLemonData {
    /**
     * 交易流水号，只代表一次交易，请求和返回DTO对象msgID一致
     */
    @ResponseIgnore
    @ApiModelProperty(hidden = true)
    private String msgId;
    /**
     * 交易发起时间
     */
    @ResponseIgnore
    @ApiModelProperty(hidden = true)
    private LocalDateTime startDateTime;
}
```

### 访问日志关键字

> 关键字是为了方便日志查询。一条日志最多可用设置3个关键字，有全局设置和通过注解或上下文个性设置两种方式。

#### 全局设置

```yaml
lemon :
  log :
    keywords : httpSession.mblNo,httpRequest.paramKeywords,httpHeader.headerKeywords,#response?.msgId
```

#### [@LogKeywords](/lemon-framework/lemon-framework-core/src/main/java/com/galaxy/lemon/common/log/LogKeywords.java) 

#### 注解属性

| 属性名 | 描述                                                         | 默认                                           |
| ------ | ------------------------------------------------------------ | ---------------------------------------------- |
| value  | 获取关键字表达式,支持SPEL表达式及以下扩展表达式。<br/>**request.XXX **从请求对象获取；request必须指定参数位置，位置从0开始，例如“"#request[0].userNo"<br/>**response.XXX **从响应对象获取<br/>**httpRequest.XXX** 从HttpServletRequest获取<br/>**httpSession.XXX** 从HttpSession获取<br/>**httpHeader.xxx** 从http header 获取<br/> | **渠道**：httpSession.mblNo<br/>**其他**：null |

#### 上下文[LemonContextUtils.addKeywords](/lemon-framework/lemon-framework-core/src/main/java/com/galaxy/lemon/framework/context/LemonContextUtils.java)

#### 示例

  ```java
  @RestController
  @RequestMapping("/user")
  @Api(tags="用户交易")
  public class UserController extends BaseController {
      @Resource
      private IUserService userService;
  
      @ApiOperation(value="新增用户", notes="新增用户", produces="application/json")
      @ApiResponse(code = 200, message = "新增用户结果")
      @PostMapping("/openUser")
      @LogKeywords({"#request[0].userNo","#response.msgCd"})
      public GenericRspDTO<NoBody> openUser(@Validated({UsrValidationGroup.OpenUser.class}) @RequestBody UserReqDTO userDTO) {
          this.userService.openUser(BeanUtils.copyPropertiesReturnDest(new UserBO(), userDTO));
          return GenericRspDTO.newSuccessInstance();
      }
  }
  ```

## 异常

>平台提供2种类型的异常，平台异常[com.galaxy.lemon.common.exception.LemonException](/lemon-common/src/main/java/com/galaxy/lemon/common/exception/LemonException.java)和业务异常[com.galaxy.lemon.common.exception.BusinessException](/lemon-common/src/main/java/com/galaxy/lemon/common/exception/BusinessException.java)。业务异常是由业务代码抛出，不会打印异常堆栈信息；平台异常是平台运行错误而抛出的异常，会在error日志打印堆栈信息供异常定位。平台对异常会做统一处理。

### 异常与事务

1. 抛出平台异常[com.galaxy.lemon.common.exception.LemonException](/lemon-common/src/main/java/com/galaxy/lemon/common/exception/LemonException.java)和业务异常[com.galaxy.lemon.common.exception.BusinessException](/lemon-common/src/main/java/com/galaxy/lemon/common/exception/BusinessException.java)会**回滚**事务
2. 抛出业务异常[com.galaxy.lemon.common.exception.BusinessNoRollbackException](/lemon-common/src/main/java/com/galaxy/lemon/common/exception/BusinessNoRollbackException.java)会**提交**事务

### 携带业务数据

>业务异常[com.galaxy.lemon.common.exception.BusinessException](/lemon-common/src/main/java/com/galaxy/lemon/common/exception/BusinessException.java)、[com.galaxy.lemon.common.exception.BusinessNoRollbackException](/lemon-common/src/main/java/com/galaxy/lemon/common/exception/BusinessNoRollbackException.java)实现了[BusinessObjectCapable](/lemon-common/src/main/java/com/galaxy/lemon/common/BusinessObjectCapable.java)接口，可以携带业务数据，业务数据会自动的copy到响应DTO。业务对象的属性必须和DTO的属性名称和类型一致才能copy成功。

### 携带消息参数

>业务异常实现了[AlertParameterizable](/lemon-common/src/main/java/com/galaxy/lemon/common/AlertParameterizable.java)接口，错误码msgCd对应的消息可以用占位符，消息码解析时可以自动解析。例如错误码“USR00001”对应的消息为“当前余额必须大于{1}元”，异常抛出异常方式如下：

* BusinessException.throwBusinessException(UsrErrorCode.USR00001,"10")

>解析后的错误信息为“当前余额必须大于10元”

### 业务异常抛出方式

```java
    public GenericDTO<NoBody> testException(GenericDTO<NoBody> object) {
        //BusinessException.throwBusinessException("PRD00001");
        BusinessException.throwBusinessException(ErrorMsgCode.SYS\_ERROR);
        return null;
    }

```
### 自定义错误码

>使用enum, 并实现AlertCapable接口，如平台错误码：

```java

public enum ErrorMsgCode implements AlertCapable {
    SYS\_ERROR("SYS00001"),                    //系统异常消息码
    ACCESS\_DATABASE\_ERROR("SYS00002"),        //访问数据库异常
    //.......
    WARNING("SYS11111");                      //警告类型

    private String msgCd;
    private String msgInfo;
    
    /**
     * @param msgCd
     * @param msgInfo
     */
    ErrorMsgCode(String msgCd, String msgInfo) {
        this.msgCd = msgCd;
        this.msgInfo = msgInfo;
    }

    @Override
    public String getMsgCd() {
        return this.msgCd;
    }
    @Override
    public String getMsgInfo() {
        return this.msgInfo;
    }
}

```

### 平台错误码

| 错误码   | 描述                          | 枚举                            |
| -------- | ----------------------------- | ------------------------------- |
| SYS00001 | 系统异常                      | SYS\_ERROR                       |
| SYS00002 | 访问数据库异常                | ACCESS\_DATABASE\_ERROR           |
| SYS00003 | 签名异常                      | SIGNATURE\_EXCEPTION             |
| SYS00404 | http请求找不到对应handler     | NO\_HANDLER\_FOUND\_ERROR          |
| SYS00401 | 没有认证                      | NO\_AUTH\_ERROR                   |
| SYS01401 | 用户认证被强制过期（剔除）    | SESSION\_EXPIRED                 |
| SYS02401 | 无效的refresh token           | REFRESH\_TOKEN\_INVALID           |
| SYS03401 | 认证失败                      | AUTHENTICATION\_FAILURE          |
| SYS00403 | 禁止操作                      | FORBIDDEN\_OPERATION             |
| SYS00005 | task schedule exception       | SCHEDULE\_TASK\_EXCEPTION         |
| SYS00006 | Feign请求找不到资源           | SERVER\_RESOURCE\_NOT\_FOUND       |
| SYS00007 | 服务不可用（熔断）            | SERVER\_NOT\_AVAILABLE            |
| SYS00100 | 获取分布式锁失败              | UNABLE\_ACQUIRE\_DISTRIBUTED\_LOCK |
| SYS10001 | Bean validation failure       | BEAN\_VALIDATION\_ERROR           |
| SYS20000 | Feign请求异常                 | CLIENT\_EXCEPTION                |
| SYS20001 | Feign请求UnknownHostException | CLIENT\_EXCEPTION\_UNKNOWN\_HOST   |
| SYS20002 | 客户端请求超时                | CLIENT\_TIMEOUT                  |
| SYS30001 | 非法请求参数                  | ILLEGAL\_PARAMETER               |
| SYS40001 | Rabbitmq生产者异常            | PRODUCER\_RABBIT\_EXCEPTION       |
| SYS40021 | Rabbitmq消费者异常            | CONSUMER\_RABBIT\_EXCEPTION       |
| SYS99999 | 业务交易没有错误码            | MSG\_CD\_NOT\_EXISTS               |
| SYS11111 | 警告                          | WARNING                        

##  数据源

### 单数据源

```yaml

spring :
  datasource :
    type : com.alibaba.druid.pool.DruidDataSource
    url : jdbc:mysql://localhost/test
    username : dbuser
    password : dbpass
    driverClassName : com.mysql.jdbc.Driver

```

###  动态数据源(多数据源)

*   见配置  application.yml

```yaml
lemon :
  #Multiple dataSources
  dataSources :
    primary :
      type : com.alibaba.druid.pool.DruidDataSource
      driverClassName : com.mysql.cj.jdbc.Driver
      url : jdbc:mysql://localhost:3306/seatelpay?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8
      username : root
      password : 123456
    lemon :
      type : com.alibaba.druid.pool.DruidDataSource
      driverClassName : com.mysql.cj.jdbc.Driver
      url : jdbc:mysql://localhost:3306/lemon?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8
      username : lemon
      password : lemon@123
  #dynamic datasource
  dynamicDataSource :
    enabled : true
    defaultDataSource : primary

```

*   切换到非默认数据源, 注解 [@com.galaxy.lemon.framework.datasource.TargetDataSource]()

```java
    //切换到lemon数据源
    @Transactional(readOnly=true)
    @TargetDataSource("lemon")
    public MsgInfoDO getMsgInfo(String msgCd, String language) {
        if(JudgeUtils.isBlank(msgCd)) {
            return null;
        }
        if(JudgeUtils.isBlank(language)) {
            if(JudgeUtils.isBlank(this.defaultLanguage)){
                this.defaultLanguage = this.defaultLocale.split("\_")[0];
            }
            language = this.defaultLanguage;
        }
        return this.msgInfoDao.getMsgInfo(msgCd, language);
    }
```

## ID生成器

### ID生成器策略

```yaml
lemon :
  idgen :
    generator : redisString
```

| 生成器      | 描述                                       | 是否默认 |
| ----------- | ------------------------------------------ | -------- |
| redisString | 采用redis string数据结构存储id             | 是       |
| redisHash   | 采用redis hash 数据结构存储id              | 否       |
| simple      | 本地存储id，适用于单实例或可以重复sequence | 否       |

### ID配置

| 属性      | 描述           | 是否必须配置 | 默认值 |
| --------- | -------------- | ------------ | ------ |
| delta     | id本地缓存数量 | 否           | 500    |
| max-value | 最大值         | 是           |        |
| min-value | 起始值         | 否           | 1      |

```yaml
lemon :
  idgen :
    delta :
      USER\_ID : 1000
    max-value :
      USER\_ID : 99999999
    min-value :
      USER\_ID : 10000000
```

### ID生成器API

* ID、序列号生成
  [com.galaxy.lemon.framework.utils.IdGenUtils](/lemon-framework/src/main/java/com/galaxy/lemon/framework/utils/IdGenUtils.java)

| 常用API                                     | 描述                                      |
| ------------------------------------------- | ----------------------------------------- |
| generateId(String idName)                   | 生成实例范围内唯一sequence                |
| generateId(String idName, String prefix)    | 生成实例范围内唯一并且带前缀的id          |
| generateId(String idName, int length)       | 生成实例范围内唯一id，左填充0直至指定长度 |
| generateReversedId(String idName)           | 生成实例范围内唯一sequence，并且反转      |
| generateGlobalId(String idName)             | 生成全局范围内唯一sequence                |
| generateGlobalId(String idName, int length) | 生成全局范围内唯一id，左填充0直至指定长度 |
| generateReversedGlobalId(String idName)     | 生成全局范围内唯一sequence，并且反转      |
| …...                                        | …...                                      |

###  ID自动生成（DO）

* 示例

```java
@DataObject
public class UserDO extends BaseDO {
    /**
     * @Fields userId 
     */
    @GeneratedValue(key="USER\_ID", prefix="US")
    private String userId;
    
}

```

### 自定义ID生成策略

* 定义ID生成策略

```java
public class DefaultGeneratorStrategy implements GeneratorStrategy {
    @Override
    public String generatedValue(String key, String prefix) {
        Validate.notBlank(key, "Property \"key\" or \"value\" cloud not be blank in @AutoIdGen");
        if(JudgeUtils.isBlank(prefix)){
            return IdGenUtils.generateCommonId(key);
        }
        return IdGenUtils.generateIdWithShortDate(key, prefix, IdGenUtils.getCommonIdSeqLength());
    }

}
```

* 指定ID生成策略

```java
@DataObject
public class UserDO extends BaseDO {
    /**
     * @Fields userId 
     */
    @GeneratedValue(key="USER\_ID", prefix="US", generatorStrategy=DefaultGeneratorStrategy.class)
    private String userId;
    
}
```

## 分页

```java

    List<UserDO> userDOs = PageUtils.pageQuery(userQueryDTO.getPageNum(), userQueryDTO.getPageSize(), () -> {
            return this.userService.findUser(queryUserDO);
            });
            
    List<UserDO> userDOs = PageUtils.pageQuery(userQueryDTO.getPageNum(), userQueryDTO.getPageSize(), false, () -> {
            return this.userService.findUser(queryUserDO);
            });

    //GenericDTO 实现了Pageable接口，可以直接作用PageUtils.pageQuery的参数
    public GenericRspDTO<List<User>> findUsers(@LemonBody UserDTO userDTO) {
        UserBO queryUserBO = new UserBO();
        queryUserBO.setName(userDTO.getName());
        //以下是分页查询
        List<UserBO> userBOs = PageUtils.pageQuery(userDTO, false, () -> this.userService.findUsers(queryUserBO));
        List<User> users = Optional.ofNullable(userBOs).map(s -> s.stream().map(u -> BeanUtils.copyPropertiesReturnDest(new User(), u)).collect(Collectors.toList())).orElse(null);
        return PageableRspDTO.newSuccessNestChildInstance(PageableRspDTO.class, users);
    }

```

## 事务
*   Spring 注解事务
*   事务必须在Service层控制
*   事务隔离级别，TransactionDefinition.ISOLATION\_DEFAULT
*   事务传播行为
>
>TransactionDefinition.PROPAGATION\_REQUIRED：如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务。这是默认值。
>
>TransactionDefinition.PROPAGATION\_REQUIRES\_NEW：创建一个新的事务，如果当前存在事务，则把当前事务挂起。
>
>TransactionDefinition.PROPAGATION\_SUPPORTS：如果当前存在事务，则加入该事务；如果当前没有事务，则以非事务的方式继续运行。
>
>TransactionDefinition.PROPAGATION\_NOT\_SUPPORTED：以非事务方式运行，如果当前存在事务，则把当前事务挂起。
>
>TransactionDefinition.PROPAGATION\_NEVER：以非事务方式运行，如果当前存在事务，则抛出异常。
>
>TransactionDefinition.PROPAGATION\_MANDATORY：如果当前存在事务，则加入该事务；如果当前没有事务，则抛出异常。
>
>TransactionDefinition.PROPAGATION\_NESTED：如果当前存在事务，则创建一个事务作为当前事务的嵌套事务来运行；如果当前没有事务，则该取值等价于TransactionDefinition.PROPAGATION\_REQUIRED。
>

*   事务超时，默认设置为底层事务系统的超时值
*   spring事务回滚规则
>
>默认配置下，spring只有在抛出的异常为运行时unchecked异常时才回滚该事务，也就是抛出的异常为RuntimeException的子类(Errors也会导致事务回滚)，而抛出checked异常则不会导致事务回滚。
>建议业务异常使用LemonException.throwBusinessException抛出
>

*   [@org.springframework.transaction.annotation.Transactional]() 的其他配置


```java

@Transactional
@Service
public class UserServiceImpl implements IUserService {
    @Resource
    private UserDao userDao;
    
    @Override
    public void addUser(UserDO userDO) {
        this.userDao.insert(userDO);
    }
    
    @Cacheable(cacheNames="user")
    @Transactional(propagation= Propagation.NOT\_SUPPORTED)
    @Override
    public UserDO getUser(String userId) {
        return this.userDao.get(userId);
    }

    @Override
    @Transactional(propagation= Propagation.SUPPORTS, readOnly=true)
    public List<UserDO> findUser(UserDO userDo) {
        return this.userDao.find(userDo);
    }

}
```

## 缓存
*   堆内缓存(Ehcache 3.x)、堆外缓存(redis)
*   ehcache3.0 是JCache (JSR107) 的provider

### Redis 缓存
*   redis 主要用作分布式缓存
*   用注解[@RedisCacheable]() 方法即可以实现redis缓存操作，原理是：先查找缓存，缓存没有数据执行方法得到数据，返回得到的数据并将数据存入缓存
*   注解@RedisCacheable 的cacheName 前缀设置 ${lemon.cache.cacheName.prefix}，一般使用默认参数，无需配置
*   支持Spring原生注解 @Cacheable、@CachePut、@CacheEvict；需要将cacheResolver 指定为 "redisCacheResolver"
*   默认的keyGenerator 是 "CACHE." + simple class name + method name + args；可以按@Cacheable 的key 属性指定key，但key 必须带前缀 “CACHE.”， 支持SPEL
*   当args为非原子类型及其包装类型、非String类型时，参数类应该实现CacheKeyParamExtractor接口
*   原则上不允许用RedisTemplate 直接操作redis,如需要操作，使用方式如下：


```java
  //字符串操作
  @Autowire
  private StringRedisTemplate stringRedisTemplate;

  //其他数据类型操作
  @Autowire
  private RedisTemplate redisTemplate;

```

*   可以根据缓存名设置过期时间，不设置即使用默认的过期时间：

```yaml

lemon :
  cache :
    jcache :
      config : classpath:config/ehcache3.xml
      provider : org.ehcache.jsr107.EhcacheCachingProvider
    redis :
      database : 0
      host : ${redis.host:192.168.3.39}
      port : 6379
      password : ${redis.password:Hisunpay2017}
      pool :
        #连接池最大连接数（使用负值表示没有限制）
        max-active : 8
        #连接池最大阻塞等待时间（使用负值表示没有限制）
        max-wait : 10000
        # 连接池中的最大空闲连接
        max-idle : 8
        # 连接池中的最小空闲连接
        min-idle : 1
      #连接超时时间（毫秒）
      timeout : 10000
      #默认缓存过期时间(秒)
      defaultExpiry : 600
      expires :
        USERS : 800
        
```

```java

    @RedisCacheable("USERS")
    @Transactional(readOnly=true)
    @Override
    public UserDO findUser(String userId) {
        return this.userDao.findByUserId(userId);
    }
    
```

### Ehcache3 缓存

*   Ehcache3 主要用作堆内缓存，如消息码信息等
*   使用@JCacheCacheable 注解方法，即可以使用 ehcahce缓存，原理同redis
*   ehcache 的配置见config/ehcache3.xml
*   支持Spring原生注解 @Cacheable、@CachePut、@CacheEvict；需要将cacheResolver 指定为 "jCacheCacheResolver"
*   key生成策略同redis

```java

    @JCacheCacheable("lemonMsgInfo")
    @Transactional(readOnly=true)
    @TargetDataSource("lemon")
    public MsgInfoDO getMsgInfo(String msgCd, String language) {
        if(JudgeUtils.isBlank(msgCd)) {
            return null;
        }
        if(JudgeUtils.isBlank(language)) {
            if(JudgeUtils.isBlank(this.defaultLanguage)){
                this.defaultLanguage = this.defaultLocale.split("\_")[0];
            }
            language = this.defaultLanguage;
        }
        return this.msgInfoDao.getMsgInfo(msgCd, language);
    }
    

```

```xml

    <cache alias="lemonMsgInfo">
	    <expiry>
	      <ttl unit="minutes">5</ttl>
	    </expiry>
	    <heap unit="entries">10000</heap>
	    <heap-store-settings>
	       <max-object-size unit="kB">10</max-object-size>
	    </heap-store-settings>
    </cache>

```

## 并发

### 开启并发功能

* 注解[@EnableConcurrent](/lemon-framework/lemon-framework-concurrent/src/main/java/com/galaxy/lemon/concurrent/EnableConcurrent.java)

```java
@LemonBootApplication
@EnableConcurrent
public class UserApplication {
    public static void main(String[] args) {
        LemonFramework.run(UserApplication.class, args);
    }
}
```

### 线程池

* 创建线程池

注解[@EnableThreadPool](/lemon-framework/lemon-framework-concurrent/src/main/java/com/galaxy/lemon/concurrent/EnableThreadPool.java) 创建线程池

| 属性            | 属性名                                                       | 描述               | 默认值 |
| --------------- | ------------------------------------------------------------ | ------------------ | ------ |
| name            | 线程池名称                                                   | 线程池名称         |        |
| corePoolSize    | the number of threads to keep in the pool                    | 支持spel           | 1      |
| maximumPoolSize | the maximum number of threads to allow in the pool           | 支持spel           | 1      |
| keepAliveTime   | when the number of threads is greater than  the core, this is the maximum time that excess idle threads  will wait for new tasks before terminating | 支持spel，单位：秒 | 60     |
| queueSize       | the queue to use for holding tasks before they are       executed | 支持spel           | 0      |

### 异步

* 创建线程池，且将方法提交到线程池运行

注解[@AsyncConcurrent](/lemon-framework/lemon-framework-concurrent/src/main/java/com/galaxy/lemon/concurrent/AsyncConcurrent.java)创建线程池，且被注解的方法将在该线程池运行

| 属性            | 属性名                                                       | 描述               | 默认值 |
| --------------- | ------------------------------------------------------------ | ------------------ | ------ |
| name            | 线程池名称                                                   | 线程池名称         |        |
| corePoolSize    | the number of threads to keep in the pool                    | 支持spel           | 1      |
| maximumPoolSize | the maximum number of threads to allow in the pool           | 支持spel           | 1      |
| keepAliveTime   | when the number of threads is greater than  the core, this is the maximum time that excess idle threads  will wait for new tasks before terminating | 支持spel，单位：秒 | 60     |
| queueSize       | the queue to use for holding tasks before they are       executed | 支持spel           | 0      |

```java
@Component
public class ConcurrentTest {
    private static final Logger logger = LoggerFactory.getLogger(ConcurrentTest.class);


    @InitialLemonData
    @AsyncConcurrent(name = "ctest", corePoolSize = "2", maximumPoolSize = "${user.threadPool.maxPoolSize:5}")
    public void testConcurrent() {
        logger.info("testConcurrent run in {}, requestId {}", Thread.currentThread().getName(), LemonUtils.getRequestId());
    }

    @InitialLemonData
    @EnableThreadPool(name = "testAsync", corePoolSize = "2", maximumPoolSize = "6")
    @Async(value = "testAsync")
    public void testAsync() {
        logger.info("testAsync run in {}, requestId {}", Thread.currentThread().getName(), LemonUtils.getRequestId());
    }
}
```

##  分布式锁

*   使用注解@DistributedLocked实现分布式锁
  
```java

    @DistributedLocked(lockName = "testLock", leaseTime=40, waitTime=10)
    @Scheduled(fixedRate=10000)
    public void testLock() {
        String str = "testLock1234567890..";
        for(char c : str.toCharArray()) {
            System.out.print(c);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("");
    }
    
```
*   分布式锁不要用在Service层，因为这样会先获取数据库连接，再来等待应用锁；可以用一个非Service层(或者叫XX服务层)方法调用Service层方法，XX服务层注解@DistributedLocked,XX服务层不含事务
*   @DistributedLocked 注解还有一些属性，见代码
*   如果不用注解，可以写java代码使用分布式锁,可以自己控制异常；如果非必须，还是使用注解

```java

    @Autowired
    private DistributedLocker distributedLocker;

    @Around("@annotation(locked)")
    public void lock(ProceedingJoinPoint pjp, Locked locked) {
        Validate.notEmpty(locked.lockName());
        try {
            distributedLocker.lock(locked.lockName(), locked.leaseTime(), locked.waitTime(), 
            () -> {return proceed(pjp);});
        } catch (UnableToAquireLockException e) {
            if(!(locked.ignoreUnableToAquireLockException() || locked.ignoreException())) {
                LemonException.throwLemonException(e);
            }
            
        } catch (LemonException e) {
            if(! locked.ignoreException()) {
                throw e;
            }
           
        } catch (Throwable e) {
            if(! locked.ignoreException()) {
                LemonException.throwLemonException(e);
            }
           
        }
    }

```

## Bean validation JSR303 JSR349

*   feign client bean validate, 注解 @com.galaxy.lemon.framework.validation.ClientValidated

```java
galaxy

@ClientValidated
publicom.galaxyserQueryDTO{
    @NotEmpty(message="DM310001")
    private String name;
    
    private Integer pageNum;
    
```
```yaml
同时配置文件需要开启客户端验证：
feign :
  validation :
    enabled : true

```

*   服务端 mvc 方法级别 bean validate, 注解@Validated

```java
    //在controller层的方法输入参数注解 @Validated
    @PostMapping("/addUser")
    public GenericDTO<NoBody> addUser(@Validated @RequestBody UserDTO userDTO) {
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userDO, userDTO);
        this.userService.addUser(userDO);
        return GenericDTO.newSuccessInstance();
    }

```

##  轮询、定时任务

*   org.springframework.scheduling.annotation.Scheduled
*   如果是Schedule等后台调起的服务，建议忽略调所有异常，业务方法里异常不要往外抛，抛出到线程池的线程也没法处理。

*   例如：

```java

    @Scheduled(fixedRateString = "${feign.httpclient.clearConnectionsRate}", initialDelay = 30000)
    public void clearExpiredAndIdleConnections() {
        if(JudgeUtils.isNotNull(poolingHttpClientConnectionManager)) {
            poolingHttpClientConnectionManager.closeExpiredConnections();
            poolingHttpClientConnectionManager.closeIdleConnections(feignHttpclientProperties.getIdleTimeoutMillis(), TimeUnit.MILLISECONDS);
        }
    }
    
      
```

*   线程池配置

```yaml

lemon :
  schedule :
    threadPool :
      poolSize : 10
      waitForTasksToCompleteOnShutdown : true
      awaitTerminationSeconds : 30
      
```

*   方法调用前`不会`初始化LemonData；配置初始化方式如下:

```java

    @Scheduled(fixedRate=5000)
    @InitialLemonData("backgroundLemonDataInitializer")
    public void testInitialLemonData() {
        //test....
    }
    
```

##  批处理

*   com.galaxy.lemon.framework.schedule.batch.BatchScheduled， 注解参数同@Scheduled

* 示例

```java

    @BatchScheduled(fixedRate=10000)
    public void schedule() {
        System.out.println("@BatchScheduled test..................");
    }
    
```

*   线程池配置同 “轮询、定时任务”

*   批处理模式下，@Schedule 不会处理

*   批处理模式下，MQ消费者默认不会启动； 通过下面配置可以开启MQ消费者
```yaml
spring :
  cloud :
    stream :
      bindings :
        input :
          consumer :
            enabled : true
            batchEnabled : true
```

*   开启批处理模式,默认未开启

>
>   方式一：
>
>   java参数形式"-Dlemon.batch.enabled=true"
>
>   方式二：
>
>   yml配置 "lemon.batch.enabled=true"
>

*   方法调用前会自动初始化LemonData，可以从LemonUtils获取


##  国际化
*   消息码已提供支持，消息码表：lemon.lemon\_msg\_info
*   com.galaxy.lemon.framework.i18n.LocaleMessageSource，该类在BaseController 已注入，读取配com.galaxy/message.properties**
*   系统取不到区域信息、或区域信息不在平台识别的范围类是，默认的区域配置

```yaml
lemon:
  locale :
    default : zh\_cn
```

##  异步及订阅发布

*   spring cloud stream rabbit 实现
*   配置文件

```yaml

spring :
  cloud :
    stream :
      defaultBinder : rabbit
      binders :
        rabbit :
          type : rabbit
          environment :
            spring :
              rabbitmq :
                addresses : stream
                virtualHost : /lemon
                username : stream
                password : stream
                requestedHeartbeat : 10
                publisherConfirms : true
                publisherReturns : true
                connectionTimeout : 10000
                cache:
                  channel :
                    size : 5
      bindings :
        input :
          destination : ${spring.application.name}
          group : ${spring.application.name}
          consumer :
            enabled : true
            concurrency : 1
            maxConcurrency : 5
            maxAttempts : 1
            durableSubscription : true
            prefetch : 10
            txSize : 10
        #channel
        output :
          enabled : true
          #binder topic
          destination : SCM
        output1 :
          enabled : false
          destination : PRD

```

*   生产者（订阅发布的发布者）

```java
//框架会自动将方法的返回对象（即主题、Hello对象）通过通道（channelName）发送出去
@Component
public class TestProducer {
    @Producers({
        @Producer(beanName="helloMessageHandler", channelName=MultiOutput.OUTPUT\_DEFAULT),          //beanName 为消费主题(Hello)的spring bean name
        @Producer(beanName="helloMessageHandler2", channelName=MultiOutput.OUTPUT\_DEFAULT)          //channelName 为将主题发送出去的通道名，如配置文件中的output
    })
    public Hello sendHello() {
        return new Hello("hello-->",40);
    }
}

//系统默认可以启动8个通道
    public static final String OUTPUT\_DEFAULT = "output";
    public static final String OUTPUT\_ONE = "output1";
    public static final String OUTPUT\_TWO = "output2";
    public static final String OUTPUT\_THREE = "output3";
    public static final String OUTPUT\_FOUR = "output4";
    public static final String OUTPUT\_FIVE = "output5";
    public static final String OUTPUT\_SIX = "output6";
    public static final String OUTPUT\_SEVEN = "output7";

```

*   消费者(订阅发布中的订阅者)，框架默认配置一个消费者消费input通道
*   消费者中的spring bean 必须实现 com.galaxy.lemon.framework.stream.MessageHandler 接口

```java

@Component("helloMessageHandler")
public class HelloMessageHandler implements MessageHandler<Hello> {
    private static final Logger logger = LoggerFactory.getLogger(HelloMessageHandler.class);
    
    @Override
    public void onMessageReceive(GenericCmdDTO<Hello> genericCmdDTO) {
        logger.info("Receive msg hand {}", genericCmdDTO.getBody());
    }
}

```
### 异步

#### 异步接口调用，由消费端提供接口

接口定义示例如下

```java

@StreamClient("usr")
public interface OperDetailClient {
    @Source(handlerBeanName = "operDetailHandler", output = "usr", group = "usr", prefix = "mirror.")
    void saveUserOperDetail(UserOperDetailBO userOperDetailBO);
}

```
#### 注解说明

* StreamClient 指定该接口为异步接口

> value : 指定该接口默认的 binding name 

* Source 指定该方法为异步调用

> handlerBeanName : 消费者spring bean name

> output : binging name ，建议配置成消费者实例名

> group : 消费者group，建议配置成消费者实例名，配置了该参数可以自动创建exchange, queue, binding, 否则依赖消费者创建

> prefix : exchange name 和 queue name 的前缀, 如果rabbit mq broken 建议配置成"mirror.", 

* 其他特殊配置只能通过配置文件配置(不推荐)，例如

```yaml

spring :
  cloud :
    stream :
      bindings :
        usr :
          destination : usr #@Source 注解中跟output 属性值一致
          producer :
            requiredGroups : usr
      rabbit :
        bindings :
          usr :
            producer :
              prefix : mirror.
```

##  会话(Session)

### starter

```gradle

compile("com.galaxy:lemon-framework-starter-session")

```

### 支持Cookie 和 header方式传递sessionId

* cookie name 默认为"sid"

* header name 默认为"x-auth-token"

* SessionId 传递方式

  | SessionId Strategy | 说明                                                 | 是否默认值 |
  | ------------------ | ---------------------------------------------------- | ---------- |
  | Cookie             | 使用cookie 传递sessionId                             | N          |
  | Header             | 使用Http Header 传递sessionId                        | N          |
  | CookieOrHeader     | 优先使用Cookie，Cookie 不存在则使用Http Header       | N          |
  | HeaderOrCookie     | 优先使用Http Header， Http Header 不存在则使用Cookie | Y          |

  

* 更改默认配置

```yaml
lemon :
  session :
    sessionId :
      cookieName : sid
      headerName : x-auth-token
      strategy : HeaderOrCookie
```

##  认证
### security starter

```gradle

compile("com.galaxy:lemon-framework-starter-security")

```

### refresh starter (不需要此功能的不要引入该starter)
```gradle

compile("com.galaxy:lemon-framework-starter-security-refresh")

```

#### 认证开发示例
[MockUserNamePasswordMatchableAuthenticationProcessor](/lemon-framework/lemon-framework-security/src/main/java/com/galaxy/lemon/framework/security/auth/MockUserNamePasswordMatchableAuthenticationProcessor.java)

+ 继承抽象类 [AbstractGenericMatchableAuthenticationProcessor](/lemon-framework/lemon-framework-security/src/main/java/com/galaxy/lemon/framework/security/auth/AbstractGenericMatchableAuthenticationProcessor.java), 构造方法参数"filterProcessesUrl"前缀必须与"lemon.security.authentication.loginPathPrefix"一致


#### refresh开发示例
```java
    public static final String BEAN\_NAME\_REFRESH\_TOKEN\_AUTHENTICATION\_PROCESSOR = "refreshTokenAuthenticationProcessor";

    @Bean
    @ConditionalOnMissingBean(name = BEAN\_NAME\_REFRESH\_TOKEN\_AUTHENTICATION\_PROCESSOR)
    public AuthenticationProcessor refreshTokenAuthenticationProcessor() {
        return authentication -> new SimpleUserInfo("mock123456", "mock", "12345678900");
    }
```

#### 登出

+ 设置header  x-auth-token
+ 请求URL /security/logout

#### 配置

```yaml
lemon :
  security :
    authentication :
      loginPathPrefix : /consumer/security/login  #默认 /security/login
      refreshPath : /consumer/security/refresh    #默认 /security/refresh
      logoutPath : /consumer/security/logout      #默认 /security/logout
    authorizeRequests :
      #配置不进行认证检查的交易请求url
      permitAll :
#        - /consumer/openUser
        - /consumer/findUsers
        
```

## XSS

```groovy
dependencies {
    compile ("com.galaxy:lemon-framework-starter-xss")
}
```

>或者引入渠道依赖

```
dependencies {
    compile("com.galaxy:lemon-entry-point-starter")
}
```

## 随机数

*   示例

```java

    @Resource("bindingTokenRandomTemplate")
    private RandomTemplate randomTemplate;
    
    @GetMapping("/testRandomTemplate")
    public GenericRspDTO<NoBody> testRandomTemplate() {
        String random = randomTemplate.apply("Test", 30*60*1000, RandomType.NUMERIC\_LETTER, 15);
        logger.info("random is {}", random);
        randomTemplate.validateOnce("Test", random);
        return GenericRspDTO.newSuccessInstance();
    }

```

*   具体见接口 [com.galaxy.lemon.framework.random.RandomTemplate]()
*   实现类com.galaxy.lemon.redis.random.BindingTokenRandomTemplate; 与用户sessionId绑定

##  日累计、月累计

*   利用redis实现
*   接口Cumulative 实现了日累计、月累计、 日月累计、日累计查询、月累计查询
*   支持多维度的累计; new Dimension("K1","1") 对象，K1 表示维度、“1” 表示累计值，可以同时设置多个维度
*   日累计、月累计、 日月累计 使用LUA 脚本实现，原子性操作
*   使用方法见下面例子

```java
   
    @Autowired
    private Cumulative cumulative;

    @GetMapping("/testCumulative/{mode}")
    public GenericDTO<NoBody> testCumulative(@PathVariable String mode) {
        if(JudgeUtils.equals(mode, "0")) {
            this.cumulative.countByDay("TEST", new Dimension("K1","1"),new Dimension("K2","2"),new Dimension("K3","3"));
        } else if (JudgeUtils.equals(mode, "1")) {
            this.cumulative.countByMonth("TEST", new Dimension("K1","1"),new Dimension("K2","2"),new Dimension("K3","3"));
        } else if(JudgeUtils.equals(mode, "2")) {
            this.cumulative.countByDayAndMonth("TEST", new Dimension("K1","1"),new Dimension("K2","2"),new Dimension("K3","3"));
        }
        return GenericDTO.newSuccessInstance();
    }
    
    @GetMapping("/queryCumulative/{mode}/{dimension}")
    public GenericDTO<String> testCumulative(@PathVariable String mode, @PathVariable String dimension) {
        String rst = "";
        if(JudgeUtils.equals(mode, "0")) {
           rst = this.cumulative.queryByDay("TEST", dimension);
        } else if (JudgeUtils.equals(mode, "1")) {
           rst = this.cumulative.queryByMonth("TEST", dimension);
        }
        return GenericDTO.newSuccessInstance(rst);
    }

```

## lemon framework starter 

| starter                                                      | 功能描述                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| compile("com.galaxy:lemon-framework-starter-cloud")           | 支持spring cloud的lemon framework                            |
| compile("com.galaxy:lemon-framework-starter-stream")          | rabbitmq/kafka支持                                           |
| compile("com.galaxy:lemon-framework-starter-session")         | 分布式session                                                |
| compile("com.galaxy:lemon-framework-starter-security")        | 认证、授权、spring security封装                              |
| compile("com.galaxy:lemon-framework-starter-security-refresh") | refresh token支持、客户端登录保持                            |
| compile("com.galaxy:lemon-swagger-starter")                   | swagger支持                                                  |
| compile("org.springframework.cloud:spring-cloud-starter-config") | config server                                                |
| compile("com.galaxy:lemon-entry-point-starter")               | 渠道应用；包括渠道应用特性及以下starter <br/>compile project(":lemon-framework:lemon-framework-starter-xss")<br/>compile project(":lemon-framework:lemon-framework-starter-security")   <br/>compile project(":lemon-framework:lemon-framework-starter-session") |
| compile("com.galaxy:lemon-framework-starter-xss")             | xss                                                          |

##  mybatis 代码自动生成

*   mybatis 代码自动生成是根据mybatis-generator修改而成，见项目 [lemon-generator](/lemon-generator)

1. 修改配置文件    [generatorConfig.xml](/lemon-generator/generatorConfig.xml)

   修改数据库参数  **connectionURL** 

   ```xml
   <jdbcConnection driverClass="com.mysql.jdbc.Driver" connectionURL="jdbc:mysql://localhost:3306/seatelpay" userId="seatelpay" password="seatelpay">  
   </jdbcConnection>
   ```

    修改生成文件存放目录    **targetProject**   及工程目录结构  **targetPackage**  ，其他配置都不要修改

   ```xml
   <context id="user"  targetRuntime="MyBatis3" defaultModelType="lemonflat">
       <javaModelGenerator targetPackage="com.galaxy.user.entity" targetProject="src/main/java">
           <property name="enableSubPackages" value="false"/>  
           <property name="trimStrings" value="false"/>
           <property name="rootClass" value="com.galaxy.framework.data.BaseDO"/>
       </javaModelGenerator>  
               
       <sqlMapGenerator targetPackage="com.galaxy.user.mapper" targetProject="src/main/resources">
           <property name="enableSubPackages" value="false"/>
       </sqlMapGenerator>
       
       <javaClientGenerator type="XMLMAPPER" targetPackage="com.galaxy.user.dao" targetProject="src/main/java">
           <property name="enableSubPackages" value="false"/>
       </javaClientGenerator>
   </context> 
   ```

   增加需要自动生产mybatis的TABLE配置
   ```xml
   <context id="user"  targetRuntime="MyBatis3" defaultModelType="lemonflat">
       <table tableName="LEMON\_USER" domainObjectName="UserDO" enableCountByExample="false" enableUpdateByExample="false"
                  enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false">
           <columnOverride column="sex" javaType="com.galaxy.user.common.Sex" jdbcType="VARCHAR" />
           <columnOverride column="birthday" javaType="java.time.LocalDate" jdbcType="TIMESTAMP" />
       </table>
   </context> 
   ```

2. 运行 gradle lemon 插件 mybatisGen

3. 生成代码效果如下

  dao, 扩展的方法在此接口中定义

    ```java
    ​    package com.galaxy.user.dao;
         
         import com.galaxy.lemon.framework.dao.BaseDao;
         import com.galaxy.user.entity.UserDO;
         import org.apache.ibatis.annotations.Mapper;
         
         @Mapper
         public interface IUserDao extends BaseDao<UserDO, String> {
          
         }
    ```
  
  java entity
  
    ```java
        package com.galaxy.user.entity;
        
        import com.galaxy.framework.data.BaseDO;
        import com.galaxy.lemon.framework.annotation.DataObject;
        import com.galaxy.lemon.framework.id.GeneratedValue;
        import com.galaxy.user.common.Sex;
        
        import java.time.LocalDate;
        
        @DataObject
        public class UserDO extends BaseDO {
            /**
             * @Fields userId 
             */
            @GeneratedValue(prefix = "U", key = "USER\_ID")
            private String userId;
            //.....省略
        }
    ```
  
  xml mapper
  ​      
    ```xml
    ```
