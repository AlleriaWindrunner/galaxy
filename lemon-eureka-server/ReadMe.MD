# Eureka服务端

##  环境变量
+   EUREKA_SERVER_HOST : eureka HA, 该eureka服务注册到另一个eureka服务端，EUREKA_SERVER_HOST为另一个eureka的IP地址；目前eureka的高可用是2个eureka相互注册

```shell
export LEMON_ENV=dev
export EUREKA_SERVER_HOST=192.168.3.39

```