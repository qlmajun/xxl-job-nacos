package com.warrior.xxl.job.executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author majun
 * @description
 * @date 2020/5/12
 */
@SpringBootApplication
@EnableDiscoveryClient
public class XxlJobSpringCloudExecutorApp {
    public static void main(String[] args) {
        SpringApplication.run(XxlJobSpringCloudExecutorApp.class, args);
    }
}
