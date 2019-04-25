### 项目介绍
******

- `第三方支付平台` 使用Java开发，包括spring-cloud 已接入微信、支付宝等主流支付渠道。

### 版本更新
***

版本 |日期 |描述
------- | ------- | -------
V0.0.1 |2018-05-23 |搭建springcloud版本


### 项目结构
***
```
sinopay
├── sinopay-microservices -- spring-cloud架构实现
|    ├── sinopay-config -- 配置中心
|    ├── sinopay-gateway -- API网关
|    ├── sinopay-server -- 服务注册中心
|    ├── sinopay-service -- 服务生产者
|    └── sinopay-web -- 服务消费者
├── sinopay-common -- 公共模块
├── sinopay-dal -- 数据持久层
├── sinopay-manager -- 运营管理平台
├── sinopay-shop -- 演示商城
```

#### sinopay
| 项目  | 端口 | 描述
|---|---|---
|sinopay-common |  | 公共模块(常量、工具类等)，jar发布
|sinopay-dal |  | 支付数据持久层，jar发布
|sinopay-mgr | 8092 | 支付运营平台
|sinopay-shop | 8181 | 支付商城演示系统
|sinopay-microservices |  | 支付中心spring-cloud架构实现
#### sinopay-microservices
| 项目  | 端口 | 描述
|---|---|---
|sinopay-config | 2020 | 支付服务配置中心
|sinopay-gateway | 3020 | 支付服务API网关
|sinopay-server | 2000 | 支付服务注册中心
|sinopay-service | 3000 | 支付服务生产者
|sinopay-web | 3010 | 支付服务消费者
项目启动顺序：
```
sinopay-server > sinopay-config > sinopay-service > sinopay-web > sinopay-gateway
```

```
### 项目部署
***
| CPU  | 内存 | 操作系统
|---|---|---
|2核 | 4 GB | CentOS 6.8 64位

安装的各软件对应的版本为（仅供参考）：

| 软件  | 版本 | 说明
|---|---|---
|JDK | 1.8 | spring boot 对低版支持没有测过
|ActiveMQ|  5.11.1 | 高版本也可以，如：5.14.3
|MySQL | 5.7.17 | 要在5.6以上，否则初始化SQL会报错，除非手动修改建表语句

相关配置文件修改：
http://note.youdao.com/noteshare?id=97ef4659b234cdb5dfaf2febeca2c32d&sub=DD01AA93B3074A209B389FBF89AAB59A
项目编译打包以及部署：
http://note.youdao.com/noteshare?id=4ea775724c1c3bf1a059efdcd53b87a8&sub=FE361588CCDE4406AC8F7CA645C65616

### 关于我们
***
项目经理：周欣
技术支持：唐纪权