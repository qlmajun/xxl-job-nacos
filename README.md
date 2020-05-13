### xxl-job-nacos文档

---

### 描述

​        xxl-job-nacos项目目前基于xxl-job v2.2.0版本改造，基于nacos服务注册中心实现服务自动发现的分布式定时任务。

### 设计架构

![image](./images/xxl-job-nacos.png)

### xxl-job-nacos服务构建步骤

* GitHub上拉取最新稳定版本的代码，源代码地址：*https://github.com/xuxueli/xxl-job*

* 导入数据库脚本

  1、sql脚本存放目录：xxl-job/doc/db/

  2、修改修改application.properties数据库连接配置

  ```properties
  ### xxl-job, datasource
  spring.datasource.url=jdbc:mysql://127.0.0.1:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
  spring.datasource.username=root
  spring.datasource.password=root
  spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
  ```

* 在父项目的pom.xml文件中加入springCloud和nacos的依赖声明

  ```xml
    <parent>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-parent</artifactId>
          <version>2.2.5.RELEASE</version>
          <relativePath/>
      </parent>
   
  <dependencyManagement>
          <dependencies>
              <dependency>
                  <groupId>com.alibaba.cloud</groupId>
                  <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                  <version>2.2.0.RELEASE</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
          </dependencies>
      </dependencyManagement>
  ```

  

#### xxl-job-core 公共依赖改造

* pom.xml文件中引入nacos的依赖

  ```xml
  <dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <scope>provided</scope>
    </dependency>
    <dependency>
       <groupId>com.alibaba.cloud</groupId>
       <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
  </dependencies>
  ```
  
* 新增内容
![image](./images/xxl-job-core.png)



#### xxl-job-admin 调度中心改造

* pom.xml文件中引入nacos的依赖

  ```xml
  <dependencies>
    <dependency>
       <groupId>com.alibaba.cloud</groupId>
       <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
  </dependencies>
  
  ```
  
* XxlJobAdminApplication启动类中添加nacos服务注解

  ```java
  @SpringBootApplication
  @EnableDiscoveryClient
  public class XxlJobAdminApplication {
  	public static void main(String[] args) {
          SpringApplication.run(XxlJobAdminApplication.class, args);
  	}
  }
  ```

* application.properties文件中添加nacos服务注册信息配置

  ```properties
  spring.main.allow-bean-definition-overriding=true
  
  spring.application.name=xxl-job-admin
  
  spring.cloud.nacos.discovery.enabled=true
  spring.cloud.nacos.discovery.server-addr=
  spring.cloud.nacos.discovery.namespace=
  ```

* 在XxlJobAdminConfig中添加应用名及XxlJobService获取方法，详情看项目代码。

* 修改XxlJobService新增获取注册中心中服务实例，更改JobRegistryMonitorHelper及XxlJobTrigger，详情看项目代码。



### 微服务端定时任务执行器使用

* pom.xml文件中引入xxl-job-core（目前使用2.2.0版本）和nacos的依赖：

  ```xml
   <dependency>
     <groupId>com.xuxueli</groupId>
      <artifactId>xxl-job-core</artifactId>
      <version>2.2.0</version>
   </dependency>
   <dependency>
      <groupId>com.alibaba.cloud</groupId>
      <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
   </dependency>
  ```

* 添加配置信息

  ```properties
  # web port
  server.port=8083
  spring.application.name=xxl-job-executor-sample-springCloud
  
  spring.cloud.nacos.discovery.enabled=true
  spring.cloud.nacos.discovery.server-addr=
  spring.cloud.nacos.discovery.namespace=
  
  # log config
  logging.config=classpath:logback.xml
  
  ### xxl-job executor log-path
  xxl.job.executor.logpath=/data/applogs/xxl-job/jobhandler
  ### xxl-job executor log-retention-days
  xxl.job.executor.logretentiondays=30
  ```

* 定义任务执行器

  ```java
  /**
   * XxlJob开发示例（Bean模式）
   * <p>
   * 开发步骤：
   * 1、在Spring Bean实例中，开发Job方法，方式格式要求为 "public ReturnT<String> execute(String param)"
   * 2、为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
   * 3、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
   */
  @Component
  public class SampleXxlJob {
      /**
       * 1、简单任务示例（Bean模式）
       */
      @XxlJob("demoJobHandler")
      public ReturnT<String> demoJobHandler(String param) throws Exception {
          XxlJobLogger.log("XXL-JOB, Hello World.");
  
          for (int i = 0; i < 5; i++) {
              XxlJobLogger.log("beat at:" + i);
              TimeUnit.SECONDS.sleep(2);
          }
          return ReturnT.SUCCESS;
      }
  }
  ```

* 登入xxl-job控制台配置执行器
![image](./images/xxl-job-executor.png)

* xxl-job控制台上配置任务
![image](./images/xxl-job-job.png)
  

