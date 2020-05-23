package com.warrior.xxl.job.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author majun
 * @description 任务调度配置
 * @date 2020/5/23
 */
public class JobScheduleConfig {

    /**
     * see com.bosssoft.nontax3.saas.xxl.job.util.RestTemplateClient
     * 负载均衡
     *
     * @return
     */
    @LoadBalanced
    @Bean
    public RestTemplate restTemplateNacosJob() {
        return new RestTemplate();
    }
}
