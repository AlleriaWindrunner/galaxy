# Lemon API gateway 使用指南

##  认证

+   登录， session默认有效时间30分钟

```
请求
path: /security/login
method: POST
header：
    x-auth-token：任意值
    x-lemon-sign: 签名
    x-lemon-secure: 签名索引
parameters: 
    loginName
    loginPwd
    random
    
结果
rspCode:
    successful: 200
    failure: 200
sessionId   : session id
refresh_token : refresh token

```

+   刷新，实现了oauth2.0 password mode；可以实现客户端保持长时间登录，refresh_token 默认空闲有效时间 30天

```
请求
path: /security/refresh
method: POST
header：
    x-auth-token：任意值
parameters: 
    refresh_token   : 登录返回的refresh_token
    
    
结果
rspCode:
    successful: 200
    failure: 200
sessionId   : session id
refresh_token : refresh token
返回结果和登录交易返回结果一样

```

+   登出

```
请求
path: /security/logout
method: DELETE
header：
    x-auth-token:SESSION_ID
    
结果
rspCode: 200
Json:{msgCd:AGW00000}
    
```

+   访问需要认证交易

> 
>   用户没有认证，响应码返回401，同时msgCd返回AGW00401
>
>   用户已认证，响应码返回200, 则根据交易结果返回
>

##  配置说明

```yaml

zuul :
  routes:
    #服务名：建议 实例名+"-"+功能名
    tst-addUser :
      #api gateway请求路径
      path : /user/addUser
      #服务ID
      serviceId : tst
      #是否需要认证，默认为true, 如果不需要认证，请主动设置为false
      authenticated : false
      #是否需要签名，默认为true, 如果不需要签名，请主动设置为false
      signatured : true
      
```

##  SESSION

+   sessionId支持header和cookie方式

+   默认使用cookie，如果使用header传送sessionId，需要在请求时增加header：

>    x-auth-token：任意值或sessionId

+   header中sessionId传送的方式

>
>    x-auth-token:SESSION_ID
>

+   cookie 中 sessionId传送方式

>    cookie id: SESSION

## 签名

+   密钥索引用header “x-lemon-secure” 传送

+   签名结果用header “x-lemon-sign” 传送

+   签名方式

>   
>   JSon字符串： “Json字符串 + 密钥” 进行签名
>
>   parameter形式： "param1=value1&param2=value2 + 密钥" 进行签名
>
>   path Variable:  "path variable 的值 + 密钥” 进行签名
>
>   form 表单形式： “需要签名的parameter value（gateway 配置）组成字符串 + 密钥” 进行签名
>

+   签名算法支持，算法由网关配置

>   
>   MD5
>
>   SHA1
>
