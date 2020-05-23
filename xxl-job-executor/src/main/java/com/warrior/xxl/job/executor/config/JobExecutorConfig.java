package com.warrior.xxl.job.executor.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author majun
 * @description xxl-job 执行器配置
 * @date 2020/5/14
 */
@ComponentScan("com.bosssoft.nontax3.saas.xxl.job.executor.endpoint")
public class JobExecutorConfig implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(JobExecutorConfig.class);

    /***
     * 执行器名称
     */
    private String appName;

    /***
     * 执行器存储日志路径
     */
    private String logPath;

    /***
     * 执行器日志存储天数
     */
    private int logRetentionDays;


    /**
     * init 初始化
     *
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);

        return xxlJobSpringExecutor;
    }

    /**
     * see com.bosssoft.nontax3.saas.xxl.job.executor.util.RestTemplateClient
     * 负载均衡
     *
     * @return
     */
    @LoadBalanced
    @Bean
    public RestTemplate restTemplateNacosJob() {
        return new RestTemplate();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Environment environment = ApplicationContextHolder.getApplicationContext().getEnvironment();
        String logpath = environment.getProperty("xxl.job.executor.logpath");
        String logretentiondays = environment.getProperty("xxl.job.executor.logretentiondays");
        this.appName = environment.getProperty("spring.application.name");
        this.logPath = StringUtils.isEmpty(logpath) ? "/data/applogs/xxl-job/jobhandler" : logpath;
        this.logRetentionDays = StringUtils.isEmpty(logretentiondays) ? -1 : Integer.parseInt(logretentiondays);
    }

    public String getAppName() {
        return appName;
    }
}
