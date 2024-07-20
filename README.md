# fate_oj
一个基于Spring Cloud微服务+RabbitMQ+Docker的简易的在线判题系统

<h3>1.模块划分</h3>
fate_oj Spring Boot版<br/>
fate_oj-code-sandbox 代码沙箱服务<br/>
fate_oj-backend-microservice Spring Cloud 微服务版<br/>
fate_oj-frontend 前端<br/>

<h3>2.基本流程图</h3>
![image](https://github.com/user-attachments/assets/3f069cb7-dd27-4b11-9f4d-8b18123c06d5)



<h3>3.使用的一些技术栈</h3>
<h4>后端</h4>
Java Spring Cloud + Spring Cloud Alibaba 微服务<br/>
Nacos 注册中心<br/>
OpenFeign 客户端调用<br/>
GateWay 网关<br/>
Swagger 聚合接口文档<br/>
Java Spring Boot<br/>
Docker 代码沙箱实现<br/>
MySQL 数据库<br/>
MyBatis-Plus 及 MyBatis X 自动生成<br/>
Redis 分布式 Session<br/>
RabbitMQ 消息队列<br/>

<h4>前端</h4>
Vue3
Vue-CLI 脚手架
Vuex 状态管理
Arco Design 组件库
前端工程化：ESLint + Prettier + TypeScript
Markdown 富文本编辑器
Monaco Editor 代码编辑器
OpenAPI 前端代码生成
